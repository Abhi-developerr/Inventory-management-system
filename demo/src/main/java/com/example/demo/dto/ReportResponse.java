package com.example.demo.dto;

import com.example.demo.enums.ReportType;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReportResponse(
    Long id,
    String name,
    String description,
    ReportType reportType,
    LocalDate startDate,
    LocalDate endDate,
    String fileFormat,
    LocalDateTime generatedAt,
    String generatedBy,
    Long fileSize,
    Boolean isTemplate,
    String fileName
) {}
