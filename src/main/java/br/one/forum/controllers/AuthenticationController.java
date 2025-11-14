package br.one.forum.controllers;

import br.one.forum.dtos.AuthenticationDTO;
import br.one.forum.dtos.LoginResponseDTO;
import br.one.forum.dtos.RegisterDTO;
import br.one.forum.entities.User;
import br.one.forum.infra.security.TokenService;
import br.one.forum.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository repository;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO data) {
        var userNamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        var auth = this.authenticationManager.authenticate(userNamePassword);

        var token = tokenService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterDTO data) {
        if (this.repository.findByEmail(data.login()) != null) return ResponseEntity.badRequest().build();

        //String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        //User newUser = new User(data.login(), encryptedPassword, data.role());
        User newUser = new User(data.login(), data.password());

        this.repository.save(newUser);
        return ResponseEntity.ok().build();

    }
}
