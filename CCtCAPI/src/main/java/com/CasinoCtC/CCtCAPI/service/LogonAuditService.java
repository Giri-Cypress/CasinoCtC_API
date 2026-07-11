package com.CasinoCtC.CCtCAPI.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.CasinoCtC.CCtCAPI.dto.*;
import com.CasinoCtC.CCtCAPI.entity.*;
import com.CasinoCtC.CCtCAPI.repository.*;

@Service
public class LogonAuditService {
    public static final int LOGON_LOGIN = 1;
    public static final int LOGON_LOGOFF = 0;
    private final LogonAuditRepository logonAuditRepository;
    private final DeviceCfgRepository deviceCfgRepository;
    public LogonAuditService(LogonAuditRepository logonAuditRepository, DeviceCfgRepository deviceCfgRepository) { this.logonAuditRepository = logonAuditRepository; this.deviceCfgRepository = deviceCfgRepository; }
    @Transactional
    public DeviceSelectionContextResponse startLogon(DeviceSelectionStartRequest request) {
        validateUserLocation(request.getLocationNumber(), request.getUserNumber());
        String lastDeviceName = logonAuditRepository.findLatestWithDevice(request.getLocationNumber(), request.getUserNumber()).map(LogonAuditEntity::getDeviceName).orElse(null);
        LogonAuditEntity audit = new LogonAuditEntity();
        audit.setLocationNumber(request.getLocationNumber()); audit.setUserNumber(request.getUserNumber()); audit.setLogon(LOGON_LOGIN); audit.setDeviceName(null); audit.setLogonDatetime(LocalDateTime.now());
        audit = logonAuditRepository.save(audit);
        DeviceSelectionContextResponse response = buildContext(request.getLocationNumber(), request.getUserNumber(), lastDeviceName);
        response.setLogonAuditNumber(audit.getLhNumber()); return response;
    }
    @Transactional(readOnly = true)
    public DeviceSelectionContextResponse getContext(Integer locationNumber, Integer userNumber) { validateUserLocation(locationNumber, userNumber); String lastDeviceName = logonAuditRepository.findLatestWithDevice(locationNumber, userNumber).map(LogonAuditEntity::getDeviceName).orElse(null); return buildContext(locationNumber, userNumber, lastDeviceName); }
    @Transactional
    public void updateSelectedDevice(DeviceSelectionUpdateRequest request) {
        validateUserLocation(request.getLocationNumber(), request.getUserNumber()); String deviceName = trimToNull(request.getDeviceName()); if (deviceName != null && deviceName.length() > 512) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Device Name cannot exceed 512 characters");
        LogonAuditEntity audit = request.getLogonAuditNumber() == null ? null : logonAuditRepository.findById(request.getLogonAuditNumber()).orElse(null);
        if (audit == null) audit = logonAuditRepository.findTopByLocationNumberAndUserNumberAndLogonOrderByLogonDatetimeDescLhNumberDesc(request.getLocationNumber(), request.getUserNumber(), LOGON_LOGIN).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Current logon audit row not found"));
        audit.setDeviceName(deviceName); logonAuditRepository.save(audit);
    }
    @Transactional
    public void logoff(DeviceSelectionUpdateRequest request) { validateUserLocation(request.getLocationNumber(), request.getUserNumber()); String deviceName = trimToNull(request.getDeviceName()); LogonAuditEntity audit = new LogonAuditEntity(); audit.setLocationNumber(request.getLocationNumber()); audit.setUserNumber(request.getUserNumber()); audit.setLogon(LOGON_LOGOFF); audit.setDeviceName(deviceName); audit.setLogonDatetime(LocalDateTime.now()); logonAuditRepository.save(audit); }
    private DeviceSelectionContextResponse buildContext(Integer locationNumber, Integer userNumber, String lastDeviceName) {
        List<DeviceSelectionDeviceResponse> devices = deviceCfgRepository.findByLocationNumberAndUseflgOrderByDeviceNameAscStationNameAscKindsAsc(locationNumber, 1).stream().map(e -> new DeviceSelectionDeviceResponse(e.getLocationNumber(), e.getStationName(), e.getKinds(), e.getDeviceName(), e.getModule(), e.getTicketFtpId())).collect(Collectors.toList());
        boolean lastDeviceStillAvailable = lastDeviceName != null && devices.stream().anyMatch(d -> lastDeviceName.equals(d.getDeviceName()));
        DeviceSelectionContextResponse response = new DeviceSelectionContextResponse(); response.setLocationNumber(locationNumber); response.setUserNumber(userNumber); response.setLastDeviceName(lastDeviceStillAvailable ? lastDeviceName : null); response.setDevices(devices); return response;
    }
    private void validateUserLocation(Integer locationNumber, Integer userNumber) { if (locationNumber == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Location Number is required"); if (userNumber == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Number is required"); }
    private String trimToNull(String value) { if (value == null) return null; String trimmed = value.trim(); return trimmed.isEmpty() ? null : trimmed; }
}
