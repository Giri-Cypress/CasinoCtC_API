package com.CasinoCtC.CCtCAPI.entity;

import java.io.Serializable;
import java.util.Objects;

public class HeaderCardTicketId implements Serializable {
    private Long hcAssignNumber;
    private String ticketId;

    public HeaderCardTicketId() {}
    public HeaderCardTicketId(Long hcAssignNumber, String ticketId) {
        this.hcAssignNumber = hcAssignNumber;
        this.ticketId = ticketId;
    }

    public Long getHcAssignNumber() { return hcAssignNumber; }
    public void setHcAssignNumber(Long hcAssignNumber) { this.hcAssignNumber = hcAssignNumber; }
    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HeaderCardTicketId)) return false;
        HeaderCardTicketId that = (HeaderCardTicketId) o;
        return Objects.equals(hcAssignNumber, that.hcAssignNumber)
            && Objects.equals(ticketId, that.ticketId);
    }
    @Override public int hashCode() { return Objects.hash(hcAssignNumber, ticketId); }
}
