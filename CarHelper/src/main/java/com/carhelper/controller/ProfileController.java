package com.carhelper.controller;

import com.carhelper.model.SearchHistory;
import com.carhelper.service.HistoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
public class ProfileController {
    private final HistoryService historyService;

    public ProfileController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping("/history/{userId}")
    public List<SearchHistory> getHistory(@PathVariable Long userId) {
        return historyService.getHistoryByUserId(userId);
    }
}