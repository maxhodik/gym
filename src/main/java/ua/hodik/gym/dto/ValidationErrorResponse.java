package ua.hodik.gym.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
public class ValidationErrorResponse {
    private Map<String, Set<String>> errors;

    public ValidationErrorResponse() {
        this.errors = new HashMap<>();
    }
}
