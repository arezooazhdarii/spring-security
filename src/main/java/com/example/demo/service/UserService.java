package com.example.demo.service;

import com.example.demo.config.JwtTokenUtil;
import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.dto.JwtResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service  @Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtUserDetailsService userDetailsService, JwtTokenUtil jwtTokenUtil, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.authenticationManager = authenticationManager;
    }

    public JwtResponse loginUser(LoginRequest loginRequest) throws Exception {
        authenticate(loginRequest.getUsername(),loginRequest.getPassword());
        loginRequest.setPassword(passwordEncoder.encode(loginRequest.getPassword()));
        log.info("saving new user {} to the database",loginRequest.getUsername());
        saveUser(loginRequest);
        UserDetails userDetails = userDetailsService
                .loadUserByUsername(loginRequest.getUsername());
        String token = jwtTokenUtil.generateToken(userDetails);
        return new JwtResponse(token);
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    private void saveUser(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername());
        ObjectMapper mapper = new ObjectMapper();
        if (user!=null) {
            user = mapper.convertValue(loginRequest, User.class);
            userRepository.save(user);
        }
    }

    public Role saveRole(Role role) {
        log.info("saving new role {} to the database",role.getName());
        return roleRepository.save(role);
    }


    public void addRoleToUser(String userName, String roleName) {
        log.info("adding role {} to user {} ",roleName,userName);
        User user = userRepository.findByUsername(userName);
        Role role = roleRepository.findByName(roleName);
        user.getRoles().add(role);

    }

}
