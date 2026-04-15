package com.webtruyen.backend.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String name;
    private String email;
    private String role;
    private String password;
    private String oldPassword;
}
