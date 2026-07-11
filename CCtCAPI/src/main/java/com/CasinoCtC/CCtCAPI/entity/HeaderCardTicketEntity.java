package com.CasinoCtC.CCtCAPI.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "header_card_tickets", schema = "gsi")
@IdClass(HeaderCardTicketId.class)
public class HeaderCardTicketEntity {

    @Id
    @Column(name = "hc_assign_number")
    private Long hcAssignNumber;

    @Id
    @Column(name = "ticket_id", length = 64)
    private String ticketId;

    @Column(name = "file_name", length = 128)
    private String fileName;

    @Column(name = "header_card_ID", nullable = false, length = 20)
    private String headerCardId;

    @Column(name = "machine_id", length = 48)
    private String machineId;

    public Long getHcAssignNumber() { return hcAssignNumber; }
    public void setHcAssignNumber(Long hcAssignNumber) { this.hcAssignNumber = hcAssignNumber; }
    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getHeaderCardId() { return headerCardId; }
    public void setHeaderCardId(String headerCardId) { this.headerCardId = headerCardId; }
    public String getMachineId() { return machineId; }
    public void setMachineId(String machineId) { this.machineId = machineId; }
}
