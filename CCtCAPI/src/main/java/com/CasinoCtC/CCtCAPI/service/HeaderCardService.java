package com.CasinoCtC.CCtCAPI.service;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.SSLSocket;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.net.ftp.FTP;
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

import com.CasinoCtC.CCtCAPI.dto.HeaderCardAssignRequest;
import com.CasinoCtC.CCtCAPI.dto.HeaderCardAssignResponse;
import com.CasinoCtC.CCtCAPI.dto.HeaderCardBatchLoadResponse;
import com.CasinoCtC.CCtCAPI.dto.HeaderCardProcessResponse;
import com.CasinoCtC.CCtCAPI.entity.HeaderCardAssignEntity;
import com.CasinoCtC.CCtCAPI.entity.HeaderCardCurrencyEntity;
import com.CasinoCtC.CCtCAPI.entity.HeaderCardTicketEntity;
import com.CasinoCtC.CCtCAPI.repository.CollectionPointRepository;
import com.CasinoCtC.CCtCAPI.repository.HeaderCardAssignRepository;
import com.CasinoCtC.CCtCAPI.repository.HeaderCardCurrencyRepository;
import com.CasinoCtC.CCtCAPI.repository.HeaderCardTicketRepository;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
public class HeaderCardService {

    private static final int SOCKET_TIMEOUT_MS = 30000;
    private static final Pattern HEADER_CARD_FILE_PATTERN =
            Pattern.compile(".*_([0-9]{10})_[0-9]{8}-[0-9]{6}\\.xml$", Pattern.CASE_INSENSITIVE);
    private static final Pattern HEADER_CARD_FILE_MACHINE_PATTERN =
            Pattern.compile(".*_([0-9]+)_[0-9]{10}_[0-9]{8}-[0-9]{6}\\.xml$", Pattern.CASE_INSENSITIVE);

    private final HeaderCardAssignRepository assignRepository;
    private final HeaderCardCurrencyRepository currencyRepository;
    private final HeaderCardTicketRepository ticketRepository;
    private final CollectionPointRepository collectionPointRepository;
    private final EntityManager entityManager;

    public HeaderCardService(
            HeaderCardAssignRepository assignRepository,
            HeaderCardCurrencyRepository currencyRepository,
            HeaderCardTicketRepository ticketRepository,
            CollectionPointRepository collectionPointRepository,
            EntityManager entityManager) {
        this.assignRepository = assignRepository;
        this.currencyRepository = currencyRepository;
        this.ticketRepository = ticketRepository;
        this.collectionPointRepository = collectionPointRepository;
        this.entityManager = entityManager;
    }

    public HeaderCardAssignResponse saveAssignment(HeaderCardAssignRequest request) {


        return saveAssignment(request, null);
    }

    @Transactional
    public HeaderCardAssignResponse saveAssignment(HeaderCardAssignRequest request, String userName) {
        validateAssignment(request, userName);

        String headerCardId = normalizeHeaderCard(request.getHeaderCardId());
        String boxNumber = request.getBoxNumber().trim();

        /*
         * Status lifecycle:
         *   0 = XML loaded / not assigned
         *   1 = assigned / waiting to be processed
         *   2 = fully processed / reusable
         *
         * The "already assigned" check applies only to the SAME Header Card ID.
         */
        Optional<HeaderCardAssignEntity> existingAssignedSameHeaderCard = assignRepository
                .findFirstByHeaderCardIdAndStatusOrderByHcAssignNumberDesc(headerCardId, 1);

        if (existingAssignedSameHeaderCard.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This Header Card is already assigned and waiting to be processed.");
        }

        /*
         * If XML loaded this Header Card first, an open row exists with status = 0.
         * Update that row and preserve file_name / machine_id.
         *
         * If no same-card open row exists, create a new assignment row. This allows
         * reuse of a Header Card whose prior row is status = 2.
         */
        HeaderCardAssignEntity entity = assignRepository
                .findFirstByHeaderCardIdAndStatusLessThanOrderByHcAssignNumberDesc(headerCardId, 2)
                .orElseGet(HeaderCardAssignEntity::new);

        boolean isNew = entity.getHcAssignNumber() == null;
        String existingFileName = entity.getFileName();
        Integer existingMachineId = entity.getMachineId();

        entity.setBusinessDate(request.getBusinessDate());
        entity.setCollectionDate(request.getCollectionDate());
        entity.setBoxNumber(boxNumber);
        entity.setHeaderCardId(headerCardId);
        entity.setStatus(1);

        /*
         * Header Card Assign owns business date, collection date, box number,
         * header card id, and status. XML loader owns file name and machine id.
         */
        entity.setFileName(existingFileName);
        entity.setMachineId(existingMachineId);

        HeaderCardAssignEntity saved = assignRepository.save(entity);
        HeaderCardAssignResponse response = toResponse(saved);
        response.setMessage(isNew ? "Header Card assignment added successfully." : "Header Card assignment updated successfully.");
        return response;
    }

    @Transactional(readOnly = true)
    public HeaderCardAssignResponse getAssignment(String headerCardId) {
        String normalizedHeaderCardId = normalizeHeaderCard(headerCardId);

        return assignRepository
                .findFirstByHeaderCardIdAndStatusLessThanOrderByHcAssignNumberDesc(normalizedHeaderCardId, 2)
                .or(() -> assignRepository.findFirstByHeaderCardIdOrderByHcAssignNumberDesc(normalizedHeaderCardId))
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Header Card assignment was not found."));
    }

    @Transactional(readOnly = true)
    public HeaderCardProcessResponse getProcessStatus(String headerCardId) {
        String normalizedHeaderCardId = normalizeHeaderCard(headerCardId);
        HeaderCardProcessResponse response = new HeaderCardProcessResponse();
        response.setHeaderCardId(normalizedHeaderCardId);

        Optional<HeaderCardAssignEntity> assignmentOptional = assignRepository
                .findFirstByHeaderCardIdAndStatusOrderByHcAssignNumberDesc(normalizedHeaderCardId, 1);

        if (assignmentOptional.isEmpty()) {
            response.setStatus("NOT_ASSIGNED");
            response.setMessage("Header Card is not found to process.");
            return response;
        }

        HeaderCardAssignEntity assignment = assignmentOptional.get();
        response.setHcAssignNumber(assignment.getHcAssignNumber());
        response.setBusinessDate(assignment.getBusinessDate());
        response.setCollectionDate(assignment.getCollectionDate());
        response.setBoxNumber(assignment.getBoxNumber());
        response.setHeaderCardId(assignment.getHeaderCardId());
        response.setFileName(assignment.getFileName());
        response.setMachineId(assignment.getMachineId());

        if (assignment.getBusinessDate() == null
                || assignment.getCollectionDate() == null
                || assignment.getBoxNumber() == null
                || assignment.getBoxNumber().trim().isEmpty()) {
            response.setStatus("NOT_ASSIGNED");
            response.setMessage("Header Card is not found to process.");
            return response;
        }

        if (assignment.getFileName() == null || assignment.getFileName().trim().isEmpty()) {
            response.setStatus("ASSIGNED_NOT_PROCESSED");
            response.setMessage("This Header Card is assigned, but XML counts have not been loaded yet.");
            return response;
        }

        /*
         * Important: do NOT set header_card_assign.status = 2 here.
         * The Process button only starts Transaction Entry and passes the
         * Header Card context. Status changes to 2 only after successful
         * transaction submit in TransactionServiceImpl.saveTransaction(...).
         */
        response.setStatus("ASSIGNED_NOT_PROCESSED");
        response.setMessage("Header Card is ready for transaction entry.");
        return response;
    }


    @Transactional(readOnly = true)
    public List<HeaderCardAssignResponse> getAssignments() {
        List<HeaderCardAssignResponse> result = new ArrayList<>();
        for (HeaderCardAssignEntity entity : assignRepository.findAll()) {
            result.add(toResponse(entity));
        }
        return result;
    }

    @Transactional
    public HeaderCardBatchLoadResponse loadBatchHeaderCardFiles(String userName) {
        FtpSettings settings = loadFtpSettings(resolveUserLocation(userName));
        HeaderCardBatchLoadResponse response = new HeaderCardBatchLoadResponse();
        FileZillaFtpsClient ftps = new FileZillaFtpsClient();

        try {
            connectAndLogin(ftps, settings);

            List<String> fileNames = ftps.rawListFileNames();
            List<String> xmlFileNames = new ArrayList<>();
            for (String fileName : fileNames) {
                if (fileName != null && fileName.toLowerCase(Locale.ROOT).endsWith(".xml")) {
                    xmlFileNames.add(extractBaseFileName(fileName));
                }
            }
            response.setFilesSeen(xmlFileNames.size());

            for (String fileName : xmlFileNames) {
                try {
                    byte[] xmlBytes = retrieveFile(ftps, fileName);
                    List<ParsedHeaderCardXml> parsedHeaderCards = parseHeaderCardXml(xmlBytes, fileName);
                    if (parsedHeaderCards == null || parsedHeaderCards.isEmpty()) {
                        response.getSkippedFiles().add(fileName + " - no HeaderCardTransaction records found in XML.");
                        response.setFilesSkipped(response.getFilesSkipped() + 1);
                        continue;
                    }

                    int validHeaderCards = 0;
                    for (ParsedHeaderCardXml parsed : parsedHeaderCards) {
                        if (parsed.headerCardId == null || parsed.headerCardId.isBlank()) {
                            response.getErrors().add(fileName + " - one HeaderCardTransaction is missing DepositID/HeaderCardID.");
                            continue;
                        }
                        upsertParsedHeaderCard(parsed);
                        validHeaderCards++;
                    }

                    if (validHeaderCards == 0) {
                        response.getSkippedFiles().add(fileName + " - header card ID not found in any HeaderCardTransaction.");
                        response.setFilesSkipped(response.getFilesSkipped() + 1);
                        continue;
                    }

                    moveCurrentDirectoryFileToSaveFolder(ftps, fileName);
                    response.getLoadedFiles().add(fileName + " (" + validHeaderCards + " header cards)");
                    response.setFilesLoaded(response.getFilesLoaded() + 1);
                } catch (Exception fileEx) {
                    response.getErrors().add(fileName + " - " + fileEx.getMessage());
                    response.setFilesSkipped(response.getFilesSkipped() + 1);
                }
            }

            return response;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Failed to load Header Card XML files from FTP server: " + ex.getMessage(), ex);
        } finally {
            disconnectQuietly(ftps);
        }
    }

    private int headerCardStatus(HeaderCardAssignEntity assignment) {
        if (assignment == null || assignment.getStatus() == null) {
            return 0;
        }
        return assignment.getStatus();
    }

    private void markHeaderCardProcessed(HeaderCardAssignEntity assignment) {
        assignment.setStatus(2);
        assignRepository.save(assignment);
    }

    private void validateAssignment(HeaderCardAssignRequest request) {
        validateAssignment(request, null);
    }

    private void validateAssignment(HeaderCardAssignRequest request, String userName) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Header Card assignment request is required.");
        }
        if (request.getBusinessDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Business Date is required.");
        }
        if (request.getBusinessDate().isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Business Date cannot be a future date.");
        }
        if (request.getCollectionDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Collection Date is required.");
        }
        if (request.getCollectionDate().isAfter(request.getBusinessDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Collection Date must be the Business Date or a prior date.");
        }
        if (request.getBoxNumber() == null || request.getBoxNumber().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Box Number is required.");
        }
        validateBoxNumber(request.getBoxNumber(), userName);
        if (request.getHeaderCardId() == null || request.getHeaderCardId().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Header Card ID is required.");
        }
        String headerCardId = request.getHeaderCardId().trim();
        if (!headerCardId.matches("\\d+")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Header Card ID must contain digits only.");
        }
        if (headerCardId.length() > 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Header Card ID cannot exceed 10 digits.");
        }
    }

    private void validateBoxNumber(String boxNumber, String userName) {
        String trimmedBoxNumber = cleanText(boxNumber);
        if (trimmedBoxNumber == null) {
            return;
        }

        Integer locationNumber = resolveUserLocation(userName);
        boolean valid = false;

        if (locationNumber != null) {
            valid = collectionPointRepository.existsByLocationNumberAndStatusAndCollectionPointNameIgnoreCase(
                    locationNumber,
                    1,
                    trimmedBoxNumber);
        }

        if (!valid) {
            valid = collectionPointRepository.existsByStatusAndCollectionPointNameIgnoreCase(1, trimmedBoxNumber);
        }

        if (!valid) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid Collection Container ID for your location.");
        }
    }

    private HeaderCardAssignResponse toResponse(HeaderCardAssignEntity entity) {
        HeaderCardAssignResponse response = new HeaderCardAssignResponse();
        response.setHcAssignNumber(entity.getHcAssignNumber());
        response.setBusinessDate(entity.getBusinessDate());
        response.setCollectionDate(entity.getCollectionDate());
        response.setBoxNumber(entity.getBoxNumber());
        response.setHeaderCardId(entity.getHeaderCardId());
        response.setFileName(entity.getFileName());
        response.setMachineId(entity.getMachineId());
        return response;
    }

    private void upsertParsedHeaderCard(ParsedHeaderCardXml parsed) {
        /*
         * XML loader rule:
         * If this same Header Card has an open row with status < 2, update
         * file_name and machine_id on that row and preserve assignment fields.
         * If no open row exists, create an XML-only row with status = 0.
         */
        HeaderCardAssignEntity assign = assignRepository
                .findFirstByHeaderCardIdAndStatusLessThanOrderByHcAssignNumberDesc(parsed.headerCardId, 2)
                .orElseGet(HeaderCardAssignEntity::new);

        boolean isNewAssignment = assign.getHcAssignNumber() == null;

        assign.setHeaderCardId(parsed.headerCardId);

        if (isNewAssignment) {
            assign.setBusinessDate(null);
            assign.setCollectionDate(null);
            assign.setBoxNumber(null);
            assign.setStatus(0);
        }

        String fileName = extractBaseFileName(parsed.fileName);
        assign.setFileName(fileName);

        String machineIdValue = firstNonBlank(
                parsed.machineId,
                machineIdFromFileName(fileName),
                machineIdFromFileName(parsed.fileName)
        );
        assign.setMachineId(parseIntegerOrNull(machineIdValue));

        HeaderCardAssignEntity saved = assignRepository.save(assign);
        Long hcAssignNumber = saved.getHcAssignNumber();

        currencyRepository.deleteByHcAssignNumber(hcAssignNumber);
        ticketRepository.deleteByHcAssignNumber(hcAssignNumber);

        for (ParsedCounter counter : parsed.counters.values()) {
            Short denomNumber = resolveDenomNumber(counter.denomValue);
            if (denomNumber == null) {
                continue;
            }

            HeaderCardCurrencyEntity currency = new HeaderCardCurrencyEntity();
            currency.setHcAssignNumber(hcAssignNumber);
            currency.setDenomNumber(denomNumber);
            currency.setQuality(1);
            currency.setMachineId(counter.machineId != null ? counter.machineId : machineIdValue);
            currency.setCount(0);
            currency.setCountMachine(counter.count);
            currency.setAmount(BigDecimal.valueOf(counter.denomValue).multiply(BigDecimal.valueOf(counter.count)));
            currencyRepository.save(currency);
        }

        for (String ticketId : parsed.ticketIds) {
            HeaderCardTicketEntity ticket = new HeaderCardTicketEntity();
            ticket.setHcAssignNumber(hcAssignNumber);
            ticket.setTicketId(ticketId);
            ticket.setFileName(fileName);
            ticket.setHeaderCardId(parsed.headerCardId);
            ticket.setMachineId(machineIdValue);
            ticketRepository.save(ticket);
        }
    }

    private List<ParsedHeaderCardXml> parseHeaderCardXml(byte[] xmlBytes, String fileName) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setExpandEntityReferences(false);
        Document document = factory.newDocumentBuilder().parse(new java.io.ByteArrayInputStream(xmlBytes));
        document.getDocumentElement().normalize();

        List<ParsedHeaderCardXml> parsedHeaderCards = new ArrayList<>();
        Element rootElement = document.getDocumentElement();
        Element machineElement = firstElement(document, "Machine");
        NodeList headerCardTransactions = document.getElementsByTagName("HeaderCardTransaction");

        if (headerCardTransactions != null && headerCardTransactions.getLength() > 0) {
            for (int i = 0; i < headerCardTransactions.getLength(); i++) {
                Element headerCardTransactionElement = (Element) headerCardTransactions.item(i);
                ParsedHeaderCardXml parsed = parseHeaderCardTransaction(
                        document,
                        rootElement,
                        machineElement,
                        headerCardTransactionElement,
                        fileName);
                parsedHeaderCards.add(parsed);
            }
            return parsedHeaderCards;
        }

        // Fallback for any older XML shape that does not contain HeaderCardTransaction.
        ParsedHeaderCardXml parsed = parseHeaderCardTransaction(
                document,
                rootElement,
                machineElement,
                rootElement,
                fileName);
        parsedHeaderCards.add(parsed);
        return parsedHeaderCards;
    }

    private ParsedHeaderCardXml parseHeaderCardTransaction(
            Document document,
            Element rootElement,
            Element machineElement,
            Element headerCardTransactionElement,
            String fileName) {

        ParsedHeaderCardXml parsed = new ParsedHeaderCardXml();
        parsed.fileName = extractBaseFileName(fileName);

        /*
         * A single XML file can contain multiple HeaderCardTransaction records.
         * Each HeaderCardTransaction must become/update its own header_card_assign row.
         * Example:
         *   <HeaderCardTransaction HeaderCardID="20824" DepositID="0000020824" ...>
         *   <HeaderCardTransaction HeaderCardID="10481" DepositID="0000010481" ...>
         *   <HeaderCardTransaction HeaderCardID="20803" DepositID="0000020803" ...>
         */
        parsed.headerCardId = firstNonBlank(
                attr(headerCardTransactionElement, "DepositID"),
                attr(headerCardTransactionElement, "HeaderCardID"),
                attr(headerCardTransactionElement, "HeaderCard"),
                attr(rootElement, "DepositID"),
                attr(rootElement, "HeaderCard"),
                attr(rootElement, "HeaderCardID"),
                headerCardFromFileName(fileName));
        parsed.headerCardId = normalizeHeaderCard(parsed.headerCardId);

        parsed.machineId = firstNonBlank(
                attr(headerCardTransactionElement, "MachineID"),
                attr(headerCardTransactionElement, "MachineId"),
                attr(headerCardTransactionElement, "Machine"),
                attr(machineElement, "SerialNumber"),
                attr(machineElement, "MachineID"),
                attr(machineElement, "MachineId"),
                attr(rootElement, "MachineID"),
                attr(rootElement, "MachineId"),
                attr(rootElement, "Machine"));

        parsed.businessDate = parseDateOrNull(firstNonBlank(
                attr(headerCardTransactionElement, "BusinessDate"),
                attr(headerCardTransactionElement, "Business_Date"),
                attr(rootElement, "BusinessDate"),
                attr(rootElement, "Business_Date")));
        parsed.collectionDate = parseDateOrNull(firstNonBlank(
                attr(headerCardTransactionElement, "CollectionDate"),
                attr(headerCardTransactionElement, "Collection_Date"),
                attr(rootElement, "CollectionDate"),
                attr(rootElement, "Collection_Date")));
        parsed.boxNumber = firstNonBlank(
                attr(headerCardTransactionElement, "BoxNumber"),
                attr(headerCardTransactionElement, "Box"),
                attr(rootElement, "BoxNumber"),
                attr(rootElement, "Box"));

        NodeList elementsToScan;
        if ("HeaderCardTransaction".equalsIgnoreCase(headerCardTransactionElement.getTagName())) {
            elementsToScan = headerCardTransactionElement.getElementsByTagName("*");
        } else {
            elementsToScan = document.getElementsByTagName("*");
        }

        for (int i = 0; i < elementsToScan.getLength(); i++) {
            Element e = (Element) elementsToScan.item(i);
            String tagName = e.getTagName();

            if (hasAttr(e, "Currency") && hasAttr(e, "Value") && hasAttr(e, "Number")) {
                String currency = attr(e, "Currency");
                if (currency == null || "USD".equalsIgnoreCase(currency.trim())) {
                    long value = parseLong(attr(e, "Value"), 0L);
                    int count = (int) parseLong(attr(e, "Number"), 0L);
                    if (value > 0 && count > 0) {
                        String machineId = firstNonBlank(attr(e, "MachineID"), attr(e, "MachineId"), parsed.machineId);
                        String key = value + "|" + (machineId == null ? "" : machineId);
                        ParsedCounter existing = parsed.counters.get(key);
                        if (existing == null) {
                            existing = new ParsedCounter(value, 0, machineId);
                            parsed.counters.put(key, existing);
                        }
                        existing.count += count;
                    }
                }
            }

            if ("TITO".equalsIgnoreCase(tagName) && hasAttr(e, "ID")) {
                String ticketId = attr(e, "ID");
                if (ticketId != null && !ticketId.trim().isEmpty()) {
                    parsed.ticketIds.add(ticketId.trim());
                }
            }
        }

        return parsed;
    }

    private Short resolveDenomNumber(long denomValue) {
        // Assumes XML Value="1" means $1, and denomination.denom_value is stored in cents.
        long centsValue = denomValue * 100L;
        try {
            Query query = entityManager.createNativeQuery(
                    "SELECT TOP 1 denom_number FROM gsi.denomination WHERE denom_value = :denomValue ORDER BY denom_number");
            query.setParameter("denomValue", centsValue);
            List<?> result = query.getResultList();
            if (!result.isEmpty() && result.get(0) != null) {
                return ((Number) result.get(0)).shortValue();
            }
        } catch (Exception ignored) {
            // Fallback for databases where denom_value is stored as dollars instead of cents.
        }
        try {
            Query query = entityManager.createNativeQuery(
                    "SELECT TOP 1 denom_number FROM gsi.denomination WHERE denom_value = :denomValue ORDER BY denom_number");
            query.setParameter("denomValue", denomValue);
            List<?> result = query.getResultList();
            if (!result.isEmpty() && result.get(0) != null) {
                return ((Number) result.get(0)).shortValue();
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private Integer resolveUserLocation(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            return null;
        }
        List<String> sqlList = List.of(
                "SELECT TOP 1 location_number FROM gsi.users WHERE user_name = :userName",
                "SELECT TOP 1 user_location FROM gsi.users WHERE user_name = :userName",
                "SELECT TOP 1 location_number FROM gsi.[user] WHERE user_name = :userName");
        for (String sql : sqlList) {
            try {
                Query query = entityManager.createNativeQuery(sql);
                query.setParameter("userName", userName);
                List<?> result = query.getResultList();
                if (!result.isEmpty() && result.get(0) != null) {
                    return ((Number) result.get(0)).intValue();
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private FtpSettings loadFtpSettings(Integer locationNumber) {
        String server = getConfigValue(locationNumber, "FTP Server");
        String user = getConfigValue(locationNumber, "FTP user");
        String password = getConfigValue(locationNumber, "FTP Password");
        if (server == null || server.isBlank() || user == null || user.isBlank() || password == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "FTP Server, FTP user, and FTP Password must be configured in Local or Global Config.");
        }
        return FtpSettings.parse(server, user, password);
    }

    private String getConfigValue(Integer locationNumber, String label) {
        if (locationNumber != null) {
            String value = queryConfigValue("SELECT TOP 1 lc_value FROM gsi.local_config "
                    + "WHERE lc_location = :locationNumber AND LOWER(lc_label) = LOWER(:label) "
                    + "AND COALESCE(lc_status, 1) = 1 ORDER BY lc_number", locationNumber, label);
            if (value != null && !value.trim().isEmpty()) {
                return value.trim();
            }
        }
        String value = queryConfigValue("SELECT TOP 1 gc_value FROM gsi.global_config "
                + "WHERE LOWER(gc_label) = LOWER(:label) AND COALESCE(gc_status, 1) = 1 ORDER BY gc_number",
                null, label);
        return value == null ? null : value.trim();
    }

    private String queryConfigValue(String sql, Integer locationNumber, String label) {
        try {
            Query query = entityManager.createNativeQuery(sql);
            if (locationNumber != null) query.setParameter("locationNumber", locationNumber);
            query.setParameter("label", label);
            List<?> rows = query.getResultList();
            return rows.isEmpty() || rows.get(0) == null ? null : String.valueOf(rows.get(0));
        } catch (Exception ex) {
            return null;
        }
    }

    private void connectAndLogin(FileZillaFtpsClient ftps, FtpSettings settings) throws IOException {
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
        ftps.setFileType(FTP.ASCII_FILE_TYPE);
        if (settings.path != null && !settings.path.isBlank()) {
            if (!changeDirectoryRobust(ftps, settings.path)) {
                throw new IOException("Unable to change FTP directory to " + settings.path + ": " + ftps.getReplyString());
            }
        }
    }

    private boolean changeDirectoryRobust(FTPSClient ftps, String path) throws IOException {
        if (path == null || path.isBlank() || "/".equals(path.trim())) return true;
        String normalized = path.replace('\\', '/').trim();
        if (ftps.changeWorkingDirectory(normalized)) return true;
        String noLeadingSlash = normalized.startsWith("/") ? normalized.substring(1) : normalized;
        if (ftps.changeWorkingDirectory(noLeadingSlash)) return true;
        String[] parts = noLeadingSlash.split("/");
        for (String part : parts) {
            if (!part.isBlank() && !ftps.changeWorkingDirectory(part)) return false;
        }
        return true;
    }

    private byte[] retrieveFile(FTPSClient ftps, String fileName) throws IOException {
        ftps.enterLocalPassiveMode();
        ftps.setFileType(FTP.BINARY_FILE_TYPE);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        if (!ftps.retrieveFile(fileName, output)) {
            throw new IOException("Unable to retrieve FTP file " + fileName + ": " + ftps.getReplyString());
        }
        return output.toByteArray();
    }

    private void moveCurrentDirectoryFileToSaveFolder(FTPSClient ftps, String fileName) throws IOException {
        String baseName = extractBaseFileName(fileName);
        String saveFolder = "Save";
        String targetFileName = saveFolder + "/" + baseName;

        if (ftps.changeWorkingDirectory(saveFolder)) {
            ftps.changeToParentDirectory();
        } else {
            ftps.makeDirectory(saveFolder);
        }
        if (!ftps.rename(baseName, targetFileName)) {
            throw new IOException("Unable to move FTP file from " + baseName + " to " + targetFileName + ": " + ftps.getReplyString());
        }
    }

    private void disconnectQuietly(FTPSClient ftps) {
        if (ftps != null && ftps.isConnected()) {
            try { ftps.logout(); } catch (Exception ignored) {}
            try { ftps.disconnect(); } catch (Exception ignored) {}
        }
    }

    private String headerCardFromFileName(String fileName) {
        Matcher matcher = HEADER_CARD_FILE_PATTERN.matcher(extractBaseFileName(fileName));
        return matcher.matches() ? matcher.group(1) : null;
    }

    private String extractBaseFileName(String fileName) {
        String normalized = String.valueOf(fileName == null ? "" : fileName).replace('\\', '/');
        int slashIndex = normalized.lastIndexOf('/');
        return slashIndex >= 0 ? normalized.substring(slashIndex + 1) : normalized;
    }

    private Element firstElement(Document document, String tagName) {
        if (document == null || tagName == null) {
            return null;
        }
        NodeList nodeList = document.getElementsByTagName(tagName);
        if (nodeList == null || nodeList.getLength() == 0) {
            return null;
        }
        return (Element) nodeList.item(0);
    }

    private String attr(Element e, String name) {
        return e != null && e.hasAttribute(name) ? e.getAttribute(name) : null;
    }
    private boolean hasAttr(Element e, String name) { return e != null && e.hasAttribute(name); }
    private String firstNonBlank(String... values) {
        if (values == null) return null;
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) return value.trim();
        }
        return null;
    }
    private LocalDate parseDateOrNull(String value) {
        try { return value == null || value.isBlank() ? null : LocalDate.parse(value.trim().substring(0, 10)); }
        catch (Exception ex) { return null; }
    }
    private long parseLong(String value, long defaultValue) {
        try { return value == null ? defaultValue : Long.parseLong(value.trim()); }
        catch (Exception ex) { return defaultValue; }
    }
    private String resolveBatchBoxNumber(ParsedHeaderCardXml parsed) {
        String explicitBoxNumber = cleanText(parsed != null ? parsed.boxNumber : null);
        if (explicitBoxNumber != null) {
            return explicitBoxNumber;
        }

        /*
         * Batch XML files often do not contain a BoxNumber attribute.
         * In that situation the previous implementation stored literal "BATCH"
         * for every row, which made gsi.header_card_assign.box_number unusable.
         * Use the machine id instead because the file/name/XML identifies the
         * source box/machine for these imported header-card records.
         */
        String machineId = cleanText(parsed != null ? parsed.machineId : null);
        if (machineId != null) {
            return machineId;
        }

        String machineIdFromFileName = machineIdFromFileName(parsed != null ? parsed.fileName : null);
        if (machineIdFromFileName != null) {
            return machineIdFromFileName;
        }

        return "BATCH";
    }

    private String machineIdFromFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return null;
        }
        Matcher matcher = HEADER_CARD_FILE_MACHINE_PATTERN.matcher(extractBaseFileName(fileName));
        return matcher.matches() ? matcher.group(1) : null;
    }

    private String cleanText(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private Integer parseIntegerOrNull(String value) {
        try { return value == null || value.isBlank() ? null : Integer.valueOf(value.trim()); }
        catch (Exception ex) { return null; }
    }
    private String trimToNull(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }
    private String normalizeHeaderCard(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.length() >= 10 ? trimmed : String.format("%10s", trimmed).replace(' ', '0');
    }

    private static class ParsedHeaderCardXml {
        String headerCardId;
        String fileName;
        String machineId;
        LocalDate businessDate;
        LocalDate collectionDate;
        String boxNumber;
        Map<String, ParsedCounter> counters = new LinkedHashMap<>();
        Set<String> ticketIds = new LinkedHashSet<>();
    }

    private static class ParsedCounter {
        long denomValue;
        int count;
        String machineId;
        ParsedCounter(long denomValue, int count, String machineId) {
            this.denomValue = denomValue;
            this.count = count;
            this.machineId = machineId;
        }
    }

    private static class FtpSettings {
        String host;
        int port = 21;
        String path;
        String user;
        String password;

        static FtpSettings parse(String server, String user, String password) {
            FtpSettings settings = new FtpSettings();
            settings.user = user;
            settings.password = password;
            String value = server.trim();
            value = value.replace("ftpes://", "").replace("ftps://", "").replace("ftp://", "");
            int slash = value.indexOf('/');
            String hostPort = slash >= 0 ? value.substring(0, slash) : value;
            settings.path = slash >= 0 ? value.substring(slash + 1) : null;
            int colon = hostPort.lastIndexOf(':');
            if (colon > 0) {
                settings.host = hostPort.substring(0, colon);
                settings.port = Integer.parseInt(hostPort.substring(colon + 1));
            } else {
                settings.host = hostPort;
            }
            return settings;
        }
    }

    private static class FileZillaFtpsClient extends FTPSClient {
        FileZillaFtpsClient() { super("TLSv1.2", false); }

        List<String> rawListFileNames() throws IOException {
            setFileType(FTP.ASCII_FILE_TYPE);
            enterLocalPassiveMode();
            List<String> names = new ArrayList<>();

            try (Socket dataSocket = _openDataConnection_(FTPCmd.LIST, null)) {
                if (dataSocket == null) {
                    throw new IOException("Unable to open FTP LIST data connection: " + getReplyString());
                }

                try (InputStream input = dataSocket.getInputStream();
                     BufferedReader reader = new BufferedReader(
                             new InputStreamReader(input, StandardCharsets.UTF_8))) {

                    String line;
                    while ((line = reader.readLine()) != null) {
                        String parsed = parseListFileName(line);
                        if (parsed != null && !parsed.isBlank()) {
                            names.add(parsed.trim());
                        }
                    }
                }
            }

            if (!completePendingCommand()) {
                throw new IOException("FTP LIST command did not complete successfully: " + getReplyString());
            }

            return names;
        }

        private String parseListFileName(String line) {
            if (line == null) return null;
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("d")) return null;
            String[] unixParts = trimmed.split("\\s+", 9);
            if (unixParts.length >= 9) return unixParts[8];
            String[] windowsParts = trimmed.split("\\s+", 4);
            if (windowsParts.length >= 4 && windowsParts[0].matches("\\d{2}-\\d{2}-\\d{2,4}")) return windowsParts[3];
            return trimmed;
        }

        @Override
        protected void _prepareDataSocket_(Socket socket) throws IOException {
            if (!(socket instanceof SSLSocket) || !(_socket_ instanceof SSLSocket)) {
                super._prepareDataSocket_(socket);
                return;
            }
            SSLSession controlSession = ((SSLSocket) _socket_).getSession();
            if (controlSession == null || !controlSession.isValid()) {
                super._prepareDataSocket_(socket);
                return;
            }
            SSLSessionContext sessionContext = controlSession.getSessionContext();
            try {
                Field field = sessionContext.getClass().getDeclaredField("sessionHostPortCache");
                field.setAccessible(true);
                Object cache = field.get(sessionContext);
                Method put = cache.getClass().getDeclaredMethod("put", Object.class, Object.class);
                put.setAccessible(true);
                int port = socket.getPort();
                Set<String> keys = new LinkedHashSet<>();
                if (socket.getInetAddress() != null) {
                    keys.add(socket.getInetAddress().getHostAddress().toLowerCase(Locale.ROOT) + ":" + port);
                    keys.add(socket.getInetAddress().getHostName().toLowerCase(Locale.ROOT) + ":" + port);
                    keys.add(socket.getInetAddress().getCanonicalHostName().toLowerCase(Locale.ROOT) + ":" + port);
                }
                if (_hostname_ != null) keys.add(_hostname_.toLowerCase(Locale.ROOT) + ":" + port);
                for (String key : keys) put.invoke(cache, key, controlSession);
            } catch (ReflectiveOperationException ex) {
                throw new IOException("Unable to reuse FTPS TLS session. Confirm JVM args: "
                    + "--add-opens java.base/sun.security.ssl=ALL-UNNAMED "
                    + "--add-opens java.base/sun.security.util=ALL-UNNAMED", ex);
            }
            super._prepareDataSocket_(socket);
        }
    }
}
