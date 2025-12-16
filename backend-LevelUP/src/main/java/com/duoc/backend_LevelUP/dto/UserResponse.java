package com.duoc.backend_LevelUP.dto;

import com.duoc.backend_LevelUP.models.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String nombre;
    private String email;
    private Role role;
    private Integer edad;

}