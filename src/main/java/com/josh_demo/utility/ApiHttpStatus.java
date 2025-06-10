package com.josh_demo.utility;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiHttpStatus {
    OK(200, "Success"),
    CREATED(201, "Successfully created"),
    BAD_REQUEST(400, "Invalid request"),
    UNAUTHORIZED(401, "Unauthorized access"),
    NOT_FOUND(404, "Resource not found"),
    INTERNAL_ERROR(500, "Internal server error");

    private final int code;
    private final String message;
}

