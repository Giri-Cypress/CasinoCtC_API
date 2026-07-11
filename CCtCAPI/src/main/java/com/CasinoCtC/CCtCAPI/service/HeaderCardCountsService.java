package com.CasinoCtC.CCtCAPI.service;

import java.io.ByteArrayInputStream;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.io.ByteArrayOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.SSLSocket;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPCmd;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.CasinoCtC.CCtCAPI.dto.HeaderCardCountsResponse;
import com.CasinoCtC.CCtCAPI.dto.HeaderCardCurrencyCountDto;
import com.CasinoCtC.CCtCAPI.entity.UserEntity;
import com.CasinoCtC.CCtCAPI.repository.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
public class HeaderCardCountsService {

    private static final int DEFAULT_FTP_PORT = 21;
    private static final int SOCKET_TIMEOUT_MS = (int) Duration.ofSeconds(20).toMillis();

    @PersistenceContext
    private EntityManager entityManager;

    private final UserRepository userRepository;

    public HeaderCardCountsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public HeaderCardCountsResponse getCounts(String userName, String headerCard) {
        if (headerCard == null || headerCard.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Header Card is required.");
        }

        UserEntity user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Authenticated user not found"));

        String rawHeaderCard = headerCard.trim();
        String normalizedHeaderCard = normalizeHeaderCardForFileName(rawHeaderCard);

        if (isUseBatchHeaderCardsEnabledForLocation(user.getLocationNumber())) {
            return getCountsFromHeaderCardTables(rawHeaderCard, normalizedHeaderCard);
        }

        FtpSettings settings = loadFtpSettings(user.getLocationNumber());
        FileZillaFtpsClient ftps = new FileZillaFtpsClient();

        try {
            ftps.setConnectTimeout(SOCKET_TIMEOUT_MS);
            ftps.setDefaultTimeout(SOCKET_TIMEOUT_MS);
            ftps.setDataTimeout(Duration.ofMillis(SOCKET_TIMEOUT_MS));
            ftps.setAutodetectUTF8(true);
            ftps.setListHiddenFiles(true);

            ftps.connect(settings.host, settings.port);
            if (!FTPReply.isPositiveCompletion(ftps.getReplyCode())) {
                throw new IOException("FTPS connection failed: " + ftps.getReplyString());
            }

            if (!ftps.login(settings.user, settings.password)) {
                throw new IOException("FTPS login failed: " + ftps.getReplyString());
            }

            // Explicit FTPS protected data channel.
            ftps.execPBSZ(0);
            ftps.execPROT("P");
            ftps.enterLocalPassiveMode();

            if (settings.path != null && !settings.path.isBlank()) {
                if (!changeDirectoryRobust(ftps, settings.path)) {
                    throw new IOException("Unable to change FTP directory to "
                            + settings.path
                            + ": "
                            + ftps.getReplyString());
                }
            }

            String currentDirectory = safePrintWorkingDirectory(ftps);
            List<String> fileNames = listFtpFileNamesUsingListCommand(ftps);

            String matchedFileName = findMatchingFile(
                    fileNames,
                    rawHeaderCard,
                    normalizedHeaderCard);

            // Safety fallback: if DB is set to just localhost, try /HC explicitly.
            if (matchedFileName == null && !isHcDirectory(currentDirectory)) {
                if (changeDirectoryRobust(ftps, "/HC")) {
                    currentDirectory = safePrintWorkingDirectory(ftps);
                    fileNames = listFtpFileNamesUsingListCommand(ftps);
                    matchedFileName = findMatchingFile(
                            fileNames,
                            rawHeaderCard,
                            normalizedHeaderCard);
                }
            }

            if (matchedFileName == null) {
                HeaderCardCountsResponse response = new HeaderCardCountsResponse();
                response.setFound(false);
                response.setMessage(
                        "No FTP XML file found for Header Card "
                                + rawHeaderCard
                                + ". Current FTP directory: "
                                + currentDirectory
                                + ". Files seen: "
                                + fileNames
                                + ". Last FTP reply: "
                                + nullToEmpty(ftps.getReplyString()).trim());
                return response;
            }

            // Switch back to binary for retrieving XML content.
            ftps.enterLocalPassiveMode();
            ftps.setFileType(FTP.BINARY_FILE_TYPE);

            ByteArrayOutputStream xmlOutput = new ByteArrayOutputStream();
            if (!ftps.retrieveFile(matchedFileName, xmlOutput)) {
                throw new IOException("Unable to retrieve FTP file "
                        + matchedFileName
                        + ": "
                        + ftps.getReplyString());
            }

            HeaderCardCountsResponse response = parseXml(xmlOutput.toByteArray());
            response.setFound(true);
            response.setFileName(matchedFileName);
            response.setMessage("Counts retrieved from " + matchedFileName + ".");
            return response;

        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Failed to retrieve counts from FTP server: " + ex.getMessage(),
                    ex);
        } finally {
            if (ftps.isConnected()) {
                try {
                    ftps.logout();
                } catch (Exception ignored) {
                    // Ignore logout errors.
                }
                try {
                    ftps.disconnect();
                } catch (Exception ignored) {
                    // Ignore disconnect errors.
                }
            }
        }
    }


    @Transactional(readOnly = true)
    public HeaderCardCountsResponse getCountsByFilePrefix(String userName, String filePrefix) {
        if (isBlank(filePrefix)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ticket FTP ID is required.");
        }

        UserEntity user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Authenticated user not found"));

        String prefix = filePrefix.trim();
        FtpSettings settings = loadFtpSettings(user.getLocationNumber());
        FileZillaFtpsClient ftps = new FileZillaFtpsClient();

        try {
            ftps.setConnectTimeout(SOCKET_TIMEOUT_MS);
            ftps.setDefaultTimeout(SOCKET_TIMEOUT_MS);
            ftps.setDataTimeout(Duration.ofMillis(SOCKET_TIMEOUT_MS));
            ftps.setAutodetectUTF8(true);
            ftps.setListHiddenFiles(true);

            ftps.connect(settings.host, settings.port);
            if (!FTPReply.isPositiveCompletion(ftps.getReplyCode())) {
                throw new IOException("FTPS connection failed: " + ftps.getReplyString());
            }

            if (!ftps.login(settings.user, settings.password)) {
                throw new IOException("FTPS login failed: " + ftps.getReplyString());
            }

            ftps.execPBSZ(0);
            ftps.execPROT("P");
            ftps.enterLocalPassiveMode();

            if (settings.path != null && !settings.path.isBlank()) {
                if (!changeDirectoryRobust(ftps, settings.path)) {
                    throw new IOException("Unable to change FTP directory to "
                            + settings.path
                            + ": "
                            + ftps.getReplyString());
                }
            }

            String currentDirectory = safePrintWorkingDirectory(ftps);
            List<String> fileNames = listFtpFileNamesUsingListCommand(ftps);
            String matchedFileName = findMatchingFileByPrefix(fileNames, prefix);

            if (matchedFileName == null && !isHcDirectory(currentDirectory)) {
                if (changeDirectoryRobust(ftps, "/HC")) {
                    currentDirectory = safePrintWorkingDirectory(ftps);
                    fileNames = listFtpFileNamesUsingListCommand(ftps);
                    matchedFileName = findMatchingFileByPrefix(fileNames, prefix);
                }
            }

            if (matchedFileName == null) {
                HeaderCardCountsResponse response = new HeaderCardCountsResponse();
                response.setFound(false);
                response.setMessage(
                        "No FTP XML file found starting with Ticket FTP ID "
                                + prefix
                                + ". Current FTP directory: "
                                + currentDirectory
                                + ". Files seen: "
                                + fileNames
                                + ". Last FTP reply: "
                                + nullToEmpty(ftps.getReplyString()).trim());
                return response;
            }

            ftps.enterLocalPassiveMode();
            ftps.setFileType(FTP.BINARY_FILE_TYPE);

            ByteArrayOutputStream xmlOutput = new ByteArrayOutputStream();
            if (!ftps.retrieveFile(matchedFileName, xmlOutput)) {
                throw new IOException("Unable to retrieve FTP file "
                        + matchedFileName
                        + ": "
                        + ftps.getReplyString());
            }

            HeaderCardCountsResponse response = parseXml(xmlOutput.toByteArray());
            response.setFound(true);
            response.setFileName(matchedFileName);
            response.setMessage("Counts retrieved from " + matchedFileName + ".");

            moveCurrentDirectoryFileToSaveFolder(ftps, matchedFileName);
            return response;

        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Failed to retrieve counts from FTP server: " + ex.getMessage(),
                    ex);
        } finally {
            if (ftps.isConnected()) {
                try { ftps.logout(); } catch (Exception ignored) {}
                try { ftps.disconnect(); } catch (Exception ignored) {}
            }
        }
    }

    private FtpSettings loadFtpSettings(Integer locationNumber) {
        String server = getConfigValue(locationNumber, "FTP Server");
        String user = getConfigValue(locationNumber, "FTP user");
        String password = getConfigValue(locationNumber, "FTP Password");

        if (isBlank(server) || isBlank(user) || isBlank(password)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "FTP Server, FTP user, and FTP Password must be configured in Local or Global Config.");
        }

        FtpSettings settings = parseFtpServer(server.trim());
        settings.user = user.trim();
        settings.password = password;
        return settings;
    }

    private String getConfigValue(Integer locationNumber, String label) {
        if (locationNumber != null) {
            List<?> localResult = entityManager.createNativeQuery("""
                SELECT TOP 1 lc_value
                  FROM gsi.local_config
                 WHERE lc_location = :locationNumber
                   AND LOWER(lc_label) = LOWER(:label)
                   AND COALESCE(lc_status, 1) = 1
                 ORDER BY lc_number
                """)
                    .setParameter("locationNumber", locationNumber)
                    .setParameter("label", label)
                    .getResultList();

            if (!localResult.isEmpty() && localResult.get(0) != null) {
                return String.valueOf(localResult.get(0));
            }
        }

        List<?> globalResult = entityManager.createNativeQuery("""
            SELECT TOP 1 gc_value
              FROM gsi.global_config
             WHERE LOWER(gc_label) = LOWER(:label)
               AND COALESCE(gc_status, 1) = 1
             ORDER BY gc_number
            """)
                .setParameter("label", label)
                .getResultList();

        return globalResult.isEmpty() || globalResult.get(0) == null
                ? null
                : String.valueOf(globalResult.get(0));
    }

    private FtpSettings parseFtpServer(String value) {
        String server = value;

        if (server.toLowerCase(Locale.ROOT).startsWith("ftp://")) {
            server = server.substring(6);
        }
        if (server.toLowerCase(Locale.ROOT).startsWith("ftps://")) {
            server = server.substring(7);
        }

        String path = null;
        int slashIndex = server.indexOf('/');
        if (slashIndex >= 0) {
            path = server.substring(slashIndex + 1);
            server = server.substring(0, slashIndex);
        }

        int port = DEFAULT_FTP_PORT;
        String host = server;
        int colonIndex = server.lastIndexOf(':');
        if (colonIndex > 0 && colonIndex < server.length() - 1) {
            host = server.substring(0, colonIndex);
            port = Integer.parseInt(server.substring(colonIndex + 1));
        }

        FtpSettings settings = new FtpSettings();
        settings.host = host;
        settings.port = port;
        settings.path = normalizeFtpPath(path);
        return settings;
    }

    private String normalizeFtpPath(String path) {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }

        String normalized = path.trim().replace('\\', '/');
        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized.isEmpty() ? null : normalized;
    }

    private boolean changeDirectoryRobust(FileZillaFtpsClient ftps, String path) throws IOException {
        if (path == null || path.trim().isEmpty()) {
            return true;
        }

        String normalized = normalizeFtpPath(path);
        if (normalized == null) {
            return true;
        }

        if (ftps.changeWorkingDirectory("/" + normalized)) {
            return true;
        }
        if (ftps.changeWorkingDirectory(normalized)) {
            return true;
        }

        int slashIndex = normalized.lastIndexOf('/');
        if (slashIndex >= 0 && slashIndex < normalized.length() - 1) {
            String lastSegment = normalized.substring(slashIndex + 1);
            return ftps.changeWorkingDirectory("/" + lastSegment)
                    || ftps.changeWorkingDirectory(lastSegment);
        }

        return false;
    }

    private String safePrintWorkingDirectory(FileZillaFtpsClient ftps) {
        try {
            return ftps.printWorkingDirectory();
        } catch (Exception ex) {
            return "Unable to read current FTP directory";
        }
    }

    private boolean isHcDirectory(String directory) {
        if (directory == null) {
            return false;
        }
        String normalized = directory.trim().replace('\\', '/');
        return "/HC".equalsIgnoreCase(normalized)
                || "HC".equalsIgnoreCase(normalized)
                || normalized.toUpperCase(Locale.ROOT).endsWith("/HC");
    }

    /**
     * Use raw LIST first, because the command-line curl test confirms FileZilla returns
     * the XML files over EPSV + TYPE A + LIST. If raw LIST returns no names,
     * fall back to Commons Net listFiles().
     */
    private List<String> listFtpFileNamesUsingListCommand(FileZillaFtpsClient ftps) throws IOException {
        Set<String> fileNames = new LinkedHashSet<>();

        List<String> rawListLines = ftps.rawListLines();
        for (String line : rawListLines) {
            addFileName(fileNames, parseFileNameFromListLine(line));
        }

        if (!fileNames.isEmpty()) {
            return new ArrayList<>(fileNames);
        }

        ftps.enterLocalPassiveMode();
        ftps.setFileType(FTP.ASCII_FILE_TYPE);
        FTPFile[] ftpFiles = ftps.listFiles();
        addNamesFromFtpFiles(fileNames, ftpFiles);

        return new ArrayList<>(fileNames);
    }

    private String parseFileNameFromListLine(String line) {
        if (line == null) {
            return null;
        }

        String trimmed = line.trim();
        if (trimmed.isEmpty() || trimmed.startsWith("total ")) {
            return null;
        }

        // Unix-style LIST from FileZilla/curl example:
        // -rw-rw-rw- 1 ftp ftp 918 Jul 01 23:35 GLO_1234_0000010819_20251117-151154.xml
        String[] unixParts = trimmed.split("\s+", 9);
        if (unixParts.length >= 9
                && (unixParts[0].startsWith("-")
                    || unixParts[0].startsWith("d")
                    || unixParts[0].startsWith("l"))) {
            return unixParts[8];
        }

        // Windows-style LIST fallback:
        // 07-01-26  11:35PM  918 GLO_1234_0000010819_20251117-151154.xml
        String[] windowsParts = trimmed.split("\s+", 4);
        if (windowsParts.length >= 4 && windowsParts[0].matches("\\d{2}-\\d{2}-\\d{2,4}")) {
            return windowsParts[3];
        }

        if (trimmed.toLowerCase(Locale.ROOT).endsWith(".xml")) {
            return trimmed;
        }

        return null;
    }

    private void moveCurrentDirectoryFileToSaveFolder(
            FileZillaFtpsClient ftps,
            String fileName) throws IOException {

        String baseName =
                extractBaseFileName(fileName);


		if (baseName == null || baseName.trim().isEmpty()) {
		    return;
		}
		
		if (baseName.replace('\\', '/').toLowerCase().contains("/save/")) {
		    return;
		}

        String saveFolder =
                "Save";

        String targetFileName =
                saveFolder + "/" + baseName;

        if (ftps.changeWorkingDirectory(saveFolder)) {
            ftps.changeToParentDirectory();
        } else {
            ftps.makeDirectory(saveFolder);
        }

        if (!ftps.rename(baseName, targetFileName)) {
            String reply =
                    ftps.getReplyString() == null
                            ? ""
                            : ftps.getReplyString();

            int replyCode =
                    ftps.getReplyCode();

            /*
             * Header Card files loaded by the background batch process are already
             * moved from /HC to /HC/Save.
             *
             * If Transaction Submit later tries to move the same file again,
             * FileZilla returns 550 because the file is no longer in /HC.
             *
             * This is not a transaction failure. The file was already processed.
             */
            if (replyCode == 550
                    || reply.toLowerCase().contains("no such file")
                    || reply.toLowerCase().contains("not found")) {

                System.out.println(
                        "Header Card XML file "
                                + baseName
                                + " is not in the HC folder. "
                                + "It was likely already moved to Save by batch processing. "
                                + "Skipping FTP move."
                );

                return;
            }

            throw new IOException(
                    "Unable to move FTP file from "
                            + baseName
                            + " to "
                            + targetFileName
                            + ": "
                            + reply
            );
        }
    }

    private String extractBaseFileName(String fileName) {
        String normalized =
                fileName.replace('\\', '/');

        int slashIndex =
                normalized.lastIndexOf('/');

        return slashIndex >= 0
                ? normalized.substring(slashIndex + 1)
                : normalized;
    }
    @Transactional(readOnly = true)
    public void moveProcessedFileToSaveFolder(
            String userName,
            String fileName) {

        if (fileName == null || fileName.trim().isEmpty()) {
            return;
        }

        UserEntity user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Authenticated user not found"
                ));

        FtpSettings settings = loadFtpSettings(user.getLocationNumber());

        FileZillaFtpsClient ftps = new FileZillaFtpsClient();

        try {
            ftps.setConnectTimeout(SOCKET_TIMEOUT_MS);
            ftps.setDefaultTimeout(SOCKET_TIMEOUT_MS);
            ftps.setDataTimeout(Duration.ofMillis(SOCKET_TIMEOUT_MS));
            ftps.setAutodetectUTF8(true);

            ftps.connect(settings.host, settings.port);

            if (!FTPReply.isPositiveCompletion(ftps.getReplyCode())) {
                throw new IOException("FTPS connection failed: " + ftps.getReplyString());
            }

            if (!ftps.login(settings.user, settings.password)) {
                throw new IOException("FTPS login failed: " + ftps.getReplyString());
            }

            ftps.execPBSZ(0);
            ftps.execPROT("P");
            ftps.enterLocalPassiveMode();

            if (settings.path != null && !settings.path.isBlank()) {
                if (!changeDirectoryRobust(ftps, settings.path)) {
                    throw new IOException(
                            "Unable to change FTP directory to "
                                    + settings.path
                                    + ": "
                                    + ftps.getReplyString()
                    );
                }
            }

            moveCurrentDirectoryFileToSaveFolder(ftps, fileName.trim());

        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Failed to move header card XML file to Save folder: "
                            + ex.getMessage(),
                    ex
            );
        } finally {
            if (ftps.isConnected()) {
                try {
                    ftps.logout();
                } catch (Exception ignored) {
                }

                try {
                    ftps.disconnect();
                } catch (Exception ignored) {
                }
            }
        }
    }
    private void addNamesFromFtpFiles(Set<String> fileNames, FTPFile[] ftpFiles) {
        if (ftpFiles == null) {
            return;
        }

        for (FTPFile ftpFile : ftpFiles) {
            if (ftpFile != null) {
                addFileName(fileNames, ftpFile.getName());
            }
        }
    }

    private void addFileName(Set<String> fileNames, String name) {
        if (name == null) {
            return;
        }

        String trimmed = name.trim();
        if (trimmed.isEmpty() || ".".equals(trimmed) || "..".equals(trimmed)) {
            return;
        }

        fileNames.add(trimmed);
    }

    private String findMatchingFile(
            List<String> fileNames,
            String rawHeaderCard,
            String normalizedHeaderCard) {

        String rawNoZeros = stripLeadingZeros(rawHeaderCard);
        String normalizedNoZeros = stripLeadingZeros(normalizedHeaderCard);

        for (String fileName : fileNames) {
            if (fileName == null) {
                continue;
            }

            String trimmedFileName = fileName.trim();
            if (!trimmedFileName.toLowerCase(Locale.ROOT).endsWith(".xml")) {
                continue;
            }

            String onlyFileName = trimmedFileName;
            int slashIndex = Math.max(
                    onlyFileName.lastIndexOf('/'),
                    onlyFileName.lastIndexOf('\\'));

            if (slashIndex >= 0) {
                onlyFileName = onlyFileName.substring(slashIndex + 1);
            }

            String onlyFileNameUpper = onlyFileName.toUpperCase(Locale.ROOT);
            String rawUpper = rawHeaderCard.toUpperCase(Locale.ROOT);
            String normalizedUpper = normalizedHeaderCard.toUpperCase(Locale.ROOT);

            if (onlyFileNameUpper.contains(normalizedUpper)
                    || onlyFileNameUpper.contains(rawUpper)) {
                return trimmedFileName;
            }

            String[] parts = onlyFileName.split("_");
            for (String part : parts) {
                if (part == null) {
                    continue;
                }

                String cleanPart = part.trim();
                if (cleanPart.equalsIgnoreCase(normalizedHeaderCard)
                        || cleanPart.equalsIgnoreCase(rawHeaderCard)
                        || stripLeadingZeros(cleanPart).equals(rawNoZeros)
                        || stripLeadingZeros(cleanPart).equals(normalizedNoZeros)) {
                    return trimmedFileName;
                }
            }
        }

        return null;
    }


    private String findMatchingFileByPrefix(List<String> fileNames, String filePrefix) {
        if (filePrefix == null) {
            return null;
        }

        String prefixUpper = filePrefix.trim().toUpperCase(Locale.ROOT);
        if (prefixUpper.isEmpty()) {
            return null;
        }

        for (String fileName : fileNames) {
            if (fileName == null) {
                continue;
            }

            String trimmedFileName = fileName.trim();
            if (!trimmedFileName.toLowerCase(Locale.ROOT).endsWith(".xml")) {
                continue;
            }

            String onlyFileName = trimmedFileName;
            int slashIndex = Math.max(
                    onlyFileName.lastIndexOf('/'),
                    onlyFileName.lastIndexOf('\\'));
            if (slashIndex >= 0) {
                onlyFileName = onlyFileName.substring(slashIndex + 1);
            }

            if (onlyFileName.toUpperCase(Locale.ROOT).startsWith(prefixUpper)) {
                return trimmedFileName;
            }
        }

        return null;
    }

    private HeaderCardCountsResponse parseXml(byte[] xmlBytes) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);

        Document document = factory.newDocumentBuilder().parse(new ByteArrayInputStream(xmlBytes));
        document.getDocumentElement().normalize();

        Map<String, HeaderCardCurrencyCountDto> countsByCurrencyAndValue = new LinkedHashMap<>();
        NodeList counters = document.getElementsByTagName("Counter");

        for (int i = 0; i < counters.getLength(); i++) {
            Element counter = (Element) counters.item(i);
            String currency = counter.getAttribute("Currency");
            String value = counter.getAttribute("Value");
            String number = counter.getAttribute("Number");

            if (isBlank(value) || isBlank(number)) {
                continue;
            }

            Long denomValueCents = new BigDecimal(value.trim())
                    .multiply(new BigDecimal("100"))
                    .longValue();
            Integer count = Integer.parseInt(number.trim());
            String key = (currency == null ? "" : currency.trim().toUpperCase(Locale.ROOT))
                    + "|"
                    + denomValueCents;

            HeaderCardCurrencyCountDto dto = countsByCurrencyAndValue.get(key);
            if (dto == null) {
                dto = new HeaderCardCurrencyCountDto(currency, denomValueCents, 0);
                countsByCurrencyAndValue.put(key, dto);
            }

            dto.setCount((dto.getCount() == null ? 0 : dto.getCount()) + count);
        }

        List<String> ticketIds = new ArrayList<>();
        NodeList titos = document.getElementsByTagName("TITO");
        for (int i = 0; i < titos.getLength(); i++) {
            Element tito = (Element) titos.item(i);
            String id = tito.getAttribute("ID");
            if (!isBlank(id)) {
                ticketIds.add(id.trim());
            }
        }

        HeaderCardCountsResponse response = new HeaderCardCountsResponse();
        response.setCurrencyCounts(new ArrayList<>(countsByCurrencyAndValue.values()));
        response.setTicketIds(ticketIds);
        return response;
    }

    private HeaderCardCountsResponse getCountsFromHeaderCardTables(String rawHeaderCard, String normalizedHeaderCard) {
        HeaderCardCountsResponse response = new HeaderCardCountsResponse();

        String assignSql = """
            SELECT TOP 1 hc_assign_number, file_name
              FROM gsi.header_card_assign
             WHERE header_card_ID = :headerCard
                OR header_card_ID = :rawHeaderCard
             ORDER BY hc_assign_number DESC
            """;
        Query assignQuery = entityManager.createNativeQuery(assignSql);
        assignQuery.setParameter("headerCard", normalizedHeaderCard);
        assignQuery.setParameter("rawHeaderCard", rawHeaderCard);
        List<?> assignRows = assignQuery.getResultList();
        if (assignRows.isEmpty()) {
            response.setFound(false);
            response.setMessage("No batch Header Card record found for Header Card " + rawHeaderCard + ".");
            return response;
        }

        Object[] assignRow = (Object[]) assignRows.get(0);
        Long hcAssignNumber = ((Number) assignRow[0]).longValue();
        String fileName = assignRow[1] == null ? null : String.valueOf(assignRow[1]);

        response.setFound(true);
        response.setFileName(fileName);
        response.setMessage("Header Card counts loaded from batch tables.");

        String currencySql = """
            SELECT c.denom_number,
                   COALESCE(d.denom_value, 0) AS denom_value,
                   SUM(COALESCE(c.count_machine, 0)) AS count_machine
              FROM gsi.header_card_currency c
              LEFT JOIN gsi.denomination d
                ON d.denom_number = c.denom_number
             WHERE c.hc_assign_number = :hcAssignNumber
             GROUP BY c.denom_number, d.denom_value
             ORDER BY c.denom_number
            """;
        Query currencyQuery = entityManager.createNativeQuery(currencySql);
        currencyQuery.setParameter("hcAssignNumber", hcAssignNumber);
        List<?> currencyRows = currencyQuery.getResultList();
        List<HeaderCardCurrencyCountDto> currencyCounts = new ArrayList<>();
        for (Object rowObj : currencyRows) {
            Object[] row = (Object[]) rowObj;
            Long denomValue = row[1] == null ? 0L : ((Number) row[1]).longValue();
            Integer count = row[2] == null ? 0 : ((Number) row[2]).intValue();
            currencyCounts.add(new HeaderCardCurrencyCountDto("USD", denomValue, count));
        }
        response.setCurrencyCounts(currencyCounts);

        String ticketSql = """
            SELECT ticket_id
              FROM gsi.header_card_tickets
             WHERE hc_assign_number = :hcAssignNumber
             ORDER BY ticket_id
            """;
        Query ticketQuery = entityManager.createNativeQuery(ticketSql);
        ticketQuery.setParameter("hcAssignNumber", hcAssignNumber);
        List<?> ticketRows = ticketQuery.getResultList();
        List<String> ticketIds = new ArrayList<>();
        for (Object ticket : ticketRows) {
            if (ticket != null) ticketIds.add(String.valueOf(ticket));
        }
        response.setTicketIds(ticketIds);

        return response;
    }

    private boolean isUseBatchHeaderCardsEnabledForLocation(Integer locationNumber) {
        if (locationNumber != null) {
            String localSql = """
                SELECT TOP 1 lc_value
                  FROM gsi.local_config
                 WHERE lc_location = :locationNumber
                   AND LOWER(lc_label) IN ('use batch header cards', 'use_batch_header_cards', 'usebatchheadercards')
                   AND COALESCE(lc_status, 1) = 1
                 ORDER BY lc_number
                """;
            Query localQuery = entityManager.createNativeQuery(localSql);
            localQuery.setParameter("locationNumber", locationNumber);
            List<?> localRows = localQuery.getResultList();
            if (!localRows.isEmpty()) return isEnabledConfigValue(localRows.get(0));
        }

        String globalSql = """
            SELECT TOP 1 gc_value
              FROM gsi.global_config
             WHERE LOWER(gc_label) IN ('use batch header cards', 'use_batch_header_cards', 'usebatchheadercards')
               AND COALESCE(gc_status, 1) = 1
             ORDER BY gc_number
            """;
        List<?> globalRows = entityManager.createNativeQuery(globalSql).getResultList();
        return !globalRows.isEmpty() && isEnabledConfigValue(globalRows.get(0));
    }

    private boolean isEnabledConfigValue(Object value) {
        if (value == null) return false;
        String normalized = String.valueOf(value).trim();
        return "yes".equalsIgnoreCase(normalized)
                || "y".equalsIgnoreCase(normalized)
                || "true".equalsIgnoreCase(normalized)
                || "1".equals(normalized);
    }

    private String normalizeHeaderCardForFileName(String headerCard) {
        String trimmed = headerCard == null ? "" : headerCard.trim();
        if (trimmed.matches("\\d+") && trimmed.length() < 10) {
            return String.format("%010d", Long.parseLong(trimmed));
        }
        return trimmed;
    }

    private String stripLeadingZeros(String value) {
        return value == null ? "" : value.replaceFirst("^0+(?!$)", "").trim();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static class FtpSettings {
        private String host;
        private int port;
        private String path;
        private String user;
        private String password;
    }

    /**
     * Explicit FTPS client for FileZilla Server.
     *
     * Required JVM arguments on Java 9+ / Java 22:
     * --add-opens java.base/sun.security.ssl=ALL-UNNAMED
     * --add-opens java.base/sun.security.util=ALL-UNNAMED
     */
    private static class FileZillaFtpsClient extends FTPSClient {

        FileZillaFtpsClient() {
            super(false); // explicit FTPS: connect plain, then AUTH TLS
        }


        List<String> rawListLines() throws IOException {
            enterLocalPassiveMode();
            setFileType(FTP.ASCII_FILE_TYPE);

            Socket dataSocket = _openDataConnection_(FTPCmd.LIST, null);
            if (dataSocket == null) {
                throw new IOException("Unable to open FTP LIST data connection: " + getReplyString());
            }

            List<String> lines = new ArrayList<>();
            try (BufferedReader dataReader = new BufferedReader(
                    new InputStreamReader(dataSocket.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = dataReader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        lines.add(line.trim());
                    }
                }
            } finally {
                dataSocket.close();
            }

            if (!completePendingCommand()) {
                throw new IOException("FTP LIST did not complete successfully: " + getReplyString());
            }

            return lines;
        }
        @Override
        protected void _prepareDataSocket_(Socket socket) throws IOException {
            super._prepareDataSocket_(socket);

            if (!(socket instanceof SSLSocket)) {
                return;
            }
            if (!(_socket_ instanceof SSLSocket)) {
                return;
            }

            SSLSession controlSession = ((SSLSocket) _socket_).getSession();
            if (controlSession == null || !controlSession.isValid()) {
                return;
            }

            SSLSessionContext sessionContext = controlSession.getSessionContext();

            try {
                Field sessionHostPortCacheField = sessionContext
                        .getClass()
                        .getDeclaredField("sessionHostPortCache");
                sessionHostPortCacheField.setAccessible(true);

                Object sessionHostPortCache = sessionHostPortCacheField.get(sessionContext);
                Method putMethod = sessionHostPortCache
                        .getClass()
                        .getDeclaredMethod("put", Object.class, Object.class);
                putMethod.setAccessible(true);

                String hostAddress = socket.getInetAddress()
                        .getHostAddress()
                        .toLowerCase(Locale.ROOT);
                String hostName = socket.getInetAddress()
                        .getHostName()
                        .toLowerCase(Locale.ROOT);
                int port = socket.getPort();

                putMethod.invoke(sessionHostPortCache, hostAddress + ":" + port, controlSession);
                putMethod.invoke(sessionHostPortCache, hostName + ":" + port, controlSession);

            } catch (ReflectiveOperationException ex) {
                throw new IOException(
                        "Unable to reuse FTPS TLS session for FileZilla data connection. "
                                + "Confirm these JVM arguments are set: "
                                + "--add-opens java.base/sun.security.ssl=ALL-UNNAMED "
                                + "--add-opens java.base/sun.security.util=ALL-UNNAMED",
                        ex);
            }
        }
    }
}
