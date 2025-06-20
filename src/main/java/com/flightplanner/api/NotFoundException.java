package com.flightplanner.api;

import java.util.Map;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String objectType, Map<String, Object> params) {
        super(buildMessage(objectType, params));
    }

    public NotFoundException(String objectType) {
        super(buildMessage(objectType, null));
    }

    private static String buildMessage(String objectType, Map<String, Object> params) {
        StringBuilder message = new StringBuilder(objectType + " not found");
        if (params != null && !params.isEmpty()) {
            message.append(" with parameters: ");
            params.forEach((key, value) -> message.append(key).append("=").append(value.toString()).append(", "));
            // Remove the last comma and space
            if (!message.isEmpty()) {
                message.setLength(message.length() - 2); // Remove last comma and space
            }
        }
        return message.toString();
    }
}
