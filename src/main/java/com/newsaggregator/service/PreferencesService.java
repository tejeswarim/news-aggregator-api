package com.newsaggregator.service;

import com.newsaggregator.model.User;
import com.newsaggregator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PreferencesService {
    private final UserRepository userRepository;
    
    public Set<String> getPreferences(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getPreferences();
    }
    
    public Set<String> updatePreferences(String username, Set<String> preferences) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPreferences(preferences);
        userRepository.save(user);
        return user.getPreferences();
    }
}
