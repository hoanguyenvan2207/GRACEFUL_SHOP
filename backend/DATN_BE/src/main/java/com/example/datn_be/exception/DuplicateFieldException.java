package com.example.datn_be.exception;

import java.util.Map;

public class DuplicateFieldException extends RuntimeException {
    private Map<String, String> fieldErrors;

    public DuplicateFieldException(Map<String, String> fieldErrors) {
        super("Có lỗi trùng dữ liệu.");
        this.fieldErrors = fieldErrors;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}

