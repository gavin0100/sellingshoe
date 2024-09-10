package com.data.filtro.config;

import jakarta.servlet.ServletException;

public class JwtNullOrEmptyException extends ServletException {
    public JwtNullOrEmptyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause);
        if (!writableStackTrace) {
            this.setStackTrace(new StackTraceElement[0]);
        }
    }
}
