package com.example.demo.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data @RequiredArgsConstructor
public class RoleToUserDTO {
    private String userName;
    private String roleName;
}
