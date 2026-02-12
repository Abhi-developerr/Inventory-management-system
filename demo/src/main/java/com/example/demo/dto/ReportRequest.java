package com.example.demo.dto;

import com.example.demo.enums.ReportType;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReportRequest(
    String name,
    String description,
    ReportType reportType,
    LocalDate startDate,
    LocalDate endDate,
    String filterConfig,
    String fileFormat
) {}
