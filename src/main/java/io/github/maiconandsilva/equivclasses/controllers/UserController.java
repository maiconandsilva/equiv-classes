package io.github.maiconandsilva.equivclasses.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.maiconandsilva.equivclasses.data.entities.AcademicUser;
import io.github.maiconandsilva.equivclasses.data.services.UserManagementService;
import io.github.maiconandsilva.equivclasses.security.JwtUtils;
import io.github.maiconandsilva.equivclasses.utils.dtos.UserLogin;
import io.github.maiconandsilva.equivclasses.utils.dtos.UserRegistration;
import io.github.maiconandsilva.equivclasses.utils.dtos.Token;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/user")
@RestController
@CrossOrigin
public class UserController {

    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;
    private final UserManagementService userManagementService;

    public UserController(UserManagementService userManagementService,
                          AuthenticationManager authManager, JwtUtils jwtUtils) {
        this.authManager = authManager;
        this.jwtUtils = jwtUtils;
        this.userManagementService = userManagementService;
    }

    @PostMapping(path = "/register")
    public Token register(@RequestBody UserRegistration userRegistration)
                throws JsonProcessingException {
        AcademicUser academicUser = new AcademicUser();
        academicUser.setUsername(userRegistration.getUsername());
        academicUser.setPassword(userRegistration.getPassword());
        userManagementService.registerUser(academicUser, userRegistration.getCourseId());
        return login(new UserLogin(userRegistration.getUsername(), userRegistration.getPassword()));
    }

    @PostMapping(path = "/login")
    public Token login(@RequestBody UserLogin userLogin) throws JsonProcessingException {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userLogin.getUsername(), userLogin.getPassword());
        auth = authManager.authenticate(auth);
        return new Token(jwtUtils.generateToken(auth));
    }
}