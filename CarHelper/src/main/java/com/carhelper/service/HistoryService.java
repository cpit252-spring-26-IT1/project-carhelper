package com.carhelper.service;

import com.carhelper.model.SearchHistory;
import com.carhelper.model.User;
import com.carhelper.repository.SearchHistoryRepository;
import com.carhelper.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HistoryService {
    private final SearchHistoryRepository searchHistoryRepository;
    private final UserRepository userRepository;

    public HistoryService(SearchHistoryRepository searchHistoryRepository, UserRepository userRepository) {
        this.searchHistoryRepository = searchHistoryRepository;
        this.userRepository = userRepository;
    }

    public void saveHistory(Long userId, String featureName, String inputText, String resultText) {
        if (userId == null) {
            return;
        }

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return;
        }

        SearchHistory history = new SearchHistory();
        history.setUser(user);
        history.setFeatureName(featureName);
        history.setInputText(inputText);
        history.setResultText(resultText);
        history.setCreatedAt(LocalDateTime.now());

        searchHistoryRepository.save(history);
    }

    public List<SearchHistory> getHistoryByUserId(Long userId) {
        return searchHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}