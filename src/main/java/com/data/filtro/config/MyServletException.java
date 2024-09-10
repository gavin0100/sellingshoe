package com.data.filtro.config;

import jakarta.servlet.ServletException;

public class MyServletException extends ServletException {
    public MyServletException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause);
        if (!writableStackTrace) {
            this.setStackTrace(new StackTraceElement[0]);
        }
    }
}
