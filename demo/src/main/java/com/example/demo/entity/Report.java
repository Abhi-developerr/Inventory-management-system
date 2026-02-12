package com.example.demo.entity;

import com.example.demo.enums.ReportType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "reports",
    indexes = {
        @Index(name = "idx_report_org", columnList = "organization_id"),
        @Index(name = "idx_report_type", columnList = "report_type"),
        @Index(name = "idx_report_created", columnList = "created_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReportType reportType;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "organization_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_report_organization"))
    private Organization organization;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    // JSON stored filters for custom reports
    @Column(columnDefinition = "TEXT")
    private String filterConfig;

    // Generated report file path/blob reference
    @Column(length = 255)
    private String fileLocation;

    @Column(name = "file_format", length = 10)
    private String fileFormat = "PDF"; // PDF, EXCEL, CSV

    @Column(name = "generated_at")
    private java.time.LocalDateTime generatedAt;

    @Column(name = "generated_by", length = 100)
    private String generatedBy;

    // File size in bytes
    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "is_template", nullable = false)
    private Boolean isTemplate = false;

    public String getFileName() {
        return name.replaceAll("[^a-zA-Z0-9.-]", "_") + "_" + 
               (generatedAt != null ? generatedAt.toLocalDate() : "draft") + 
               "." + (fileFormat != null ? fileFormat.toLowerCase() : "pdf");
    }
}
