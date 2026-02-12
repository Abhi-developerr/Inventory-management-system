package com.example.demo.entity;

import com.example.demo.enums.ReportFrequency;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "report_schedules",
    indexes = {
        @Index(name = "idx_schedule_report", columnList = "report_id"),
        @Index(name = "idx_schedule_org", columnList = "organization_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportSchedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "report_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_schedule_report"))
    private Report report;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "organization_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_schedule_organization"))
    private Organization organization;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReportFrequency frequency;

    // Email recipients (comma-separated)
    @Column(columnDefinition = "TEXT")
    private String emailRecipients;

    // Cron expression for scheduling
    @Column(length = 100)
    private String cronExpression;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "last_generated_at")
    private java.time.LocalDateTime lastGeneratedAt;

    @Column(name = "next_run_at")
    private java.time.LocalDateTime nextRunAt;
}
