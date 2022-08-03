package com.example.demo.controller;

import com.example.demo.domain.Role;
import com.example.demo.dto.JwtResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RoleToUserDTO;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> saveUser(@RequestBody LoginRequest loginRequest) throws Exception {
        return ResponseEntity.ok().body(userService.loginUser(loginRequest));
    }


    @PostMapping("/role")
    public ResponseEntity<Role> saveRole(@RequestBody Role role) {
        return ResponseEntity.ok().body(userService.saveRole(role));
    }

    @PostMapping("/role/addtouser")
    public ResponseEntity<Role> addToUser(@RequestBody RoleToUserDTO roleToUserForm) {
        userService.addRoleToUser(roleToUserForm.getUserName(),roleToUserForm.getRoleName());
        return ResponseEntity.ok().build();
    }

}
