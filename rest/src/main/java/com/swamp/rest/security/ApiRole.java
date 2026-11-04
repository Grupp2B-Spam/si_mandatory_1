package com.swamp.rest.security;

public enum ApiRole {
    ADMIN,
    USER;

    public static ApiRole fromHeader(String roleHeader) {
        if ("admin".equalsIgnoreCase(roleHeader)) {
            return ADMIN;
        }

        return USER;
    }

    public boolean canWrite() {
        return this == ADMIN;
    }
}
