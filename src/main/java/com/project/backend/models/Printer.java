package com.project.backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "printers")
@RequiredArgsConstructor
public class Printer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Printer name is required")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Brand is required")
    @Column(name = "brand", nullable = false)
    private String brand;

    @NotBlank(message = "Type is required")
    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "description")
    private String description;

    @Column(name = "support_contact")
    private String supportContact;

    @Column(name = "last_maintenance_date")
    private LocalDateTime lastMaintenanceDate;

    // New field for a4 remaining pages
    @Min(value = 0, message = "Remaining pages must be greater than or equal to 0")
    @Column(name = "a4_remaining_pages", nullable = false)
    private int a4RemainingPages;

    // New field for a3 remaining pages
    @Min(value = 0, message = "Remaining pages must be greater than or equal to 0")
    @Column(name = "a3_remaining_pages", nullable = false)
    private int a3RemainingPages;

    // Enum for status
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PrinterStatus status = PrinterStatus.active;

    // Many-to-One relationship with Location
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "printer_location_id_fk"))
    private Location location;

    // Many-to-One relationship with Spso
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "spso_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "printer_spso_id_fk"))
    private SPSO spso;

    public enum PrinterStatus {
        active,
        inactive,
        occupied,
        deleted
    }

    @PrePersist
    public void prePersist() {
        if (this.lastMaintenanceDate == null) {
            this.lastMaintenanceDate = LocalDateTime.now();
        }
    }
}
