package com.carhelper.controller;

import com.carhelper.command.ImageDiagnosisCommand;
import com.carhelper.model.DiagnosticReport;
import com.carhelper.service.HistoryService;
import com.carhelper.service.ImageDiagnosisService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/diagnosis")
@CrossOrigin(origins = "*")
public class ImageDiagnosisController {
    private final ImageDiagnosisService imageDiagnosisService;
    private final HistoryService historyService;

    public ImageDiagnosisController(ImageDiagnosisService imageDiagnosisService, HistoryService historyService) {
        this.imageDiagnosisService = imageDiagnosisService;
        this.historyService = historyService;
    }

    @PostMapping("/image")
    public DiagnosticReport analyzeImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "language", required = false) String language
    ) {
        ImageDiagnosisCommand command = new ImageDiagnosisCommand(imageDiagnosisService, file, language);
        DiagnosticReport report = command.execute();

        String input = file.getOriginalFilename();

        String result = "Issue Name: " + report.getIssueName()
                + "\nDetected Problems: " + report.getDetectedProblems()
                + "\nRepair Suggestion: " + report.getRepairSuggestion()
                + "\nEstimated Cost: " + report.getEstimatedCost()
                + "\nDisclaimer: " + report.getAiDisclaimer();

        historyService.saveHistory(userId, "Image Diagnosis", input, result);

        return report;
    }
}