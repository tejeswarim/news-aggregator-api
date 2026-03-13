package com.newsaggregator.controller;

import com.newsaggregator.dto.PreferencesRequest;
import com.newsaggregator.service.PreferencesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Set;

@RestController
@RequestMapping("/api/preferences")
@RequiredArgsConstructor
public class PreferencesController {
    private final PreferencesService preferencesService;
    
    @GetMapping
    public ResponseEntity<Set<String>> getPreferences(Authentication authentication) {
        return ResponseEntity.ok(preferencesService.getPreferences(authentication.getName()));
    }
    
    @PutMapping
    public ResponseEntity<Set<String>> updatePreferences(
            @Valid @RequestBody PreferencesRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(preferencesService.updatePreferences(
                authentication.getName(), request.getPreferences()));
    }
}
