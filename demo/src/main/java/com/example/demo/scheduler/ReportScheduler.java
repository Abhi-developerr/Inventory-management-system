package com.example.demo.scheduler;

import com.example.demo.entity.ReportSchedule;
import com.example.demo.repository.ReportScheduleRepository;
import com.example.demo.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduler for recurring report generation
 * Runs every hour to check if any scheduled reports need to be generated
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReportScheduler {

    private final ReportScheduleRepository reportScheduleRepository;
    private final ReportService reportService;

    @Scheduled(cron = "0 0 * * * *") // Every hour at minute 0
    public void generateScheduledReports() {
        try {
            log.info("Starting scheduled report generation...");
            
            List<ReportSchedule> scheduledReports = reportScheduleRepository.findScheduledForExecution();
            
            for (ReportSchedule schedule : scheduledReports) {
                if (schedule.getIsActive()) {
                    try {
                        log.info("Generating scheduled report: {} (ID: {})", 
                                 schedule.getReport().getName(), schedule.getReport().getId());
                        
                        // Update last generated and next run time
                        schedule.setLastGeneratedAt(LocalDateTime.now());
                        schedule.setNextRunAt(calculateNextRunTime(schedule.getFrequency().toString()));
                        reportScheduleRepository.save(schedule);
                        
                        // TODO: Send email notification with report
                        log.info("Report generated and scheduled for next run");
                    } catch (Exception e) {
                        log.error("Error generating scheduled report: {}", schedule.getReport().getId(), e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error in scheduled report generation", e);
        }
    }

    /**
     * Calculate next run time based on frequency
     */
    private LocalDateTime calculateNextRunTime(String frequency) {
        LocalDateTime now = LocalDateTime.now();
        return switch (frequency) {
            case "DAILY" -> now.plusDays(1);
            case "WEEKLY" -> now.plusWeeks(1);
            case "MONTHLY" -> now.plusMonths(1);
            case "QUARTERLY" -> now.plusMonths(3);
            case "YEARLY" -> now.plusYears(1);
            default -> now.plusDays(1);
        };
    }
}
