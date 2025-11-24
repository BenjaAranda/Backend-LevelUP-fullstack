package com.duoc.backend_LevelUP.dto;

import com.duoc.backend_LevelUP.models.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String nombre;
    private String email;
    private String password;
    private Integer edad;
    private Role role;
}