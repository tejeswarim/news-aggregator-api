package com.newsaggregator.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.Set;

@Data
public class PreferencesRequest {
    @NotEmpty(message = "At least one preference is required")
    private Set<String> preferences;
}
