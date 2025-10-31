package com.example.demo.model;

import jakarta.persistence.*;

@Entity
public class GhostNet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String location;
    private String size;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = true)
    private Boolean anonymousReport = Boolean.TRUE;

    private String reporterName;
    private String reporterPhone;

    @ManyToOne
    private Person salvagedBy;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Boolean getAnonymousReport() { return anonymousReport; }
    public boolean isAnonymousReport() { return Boolean.TRUE.equals(anonymousReport); }
    public void setAnonymousReport(Boolean anonymousReport) { this.anonymousReport = anonymousReport; }

    public String getReporterName() { return reporterName; }
    public void setReporterName(String reporterName) { this.reporterName = reporterName; }

    public String getReporterPhone() { return reporterPhone; }
    public void setReporterPhone(String reporterPhone) { this.reporterPhone = reporterPhone; }

    public Person getSalvagedBy() { return salvagedBy; }
    public void setSalvagedBy(Person salvagedBy) { this.salvagedBy = salvagedBy; }

    public enum Status {
        GEMELDET,
        BERGUNG_BEVORSTEHEND,
        GEBORGEN,
        VERSCHOLLEN
    }
}
