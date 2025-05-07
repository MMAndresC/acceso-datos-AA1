package com.svalero.tournament.service;

import com.svalero.tournament.constants.Constants;
import com.svalero.tournament.domain.User;
import com.svalero.tournament.exception.PasswordIncorrectException;
import com.svalero.tournament.exception.UserAlreadyExistException;
import com.svalero.tournament.exception.UserNotFoundException;
import com.svalero.tournament.repository.UserRepository;
import com.svalero.tournament.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Profile("!test")
@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String login(String username, String password) throws UserNotFoundException, PasswordIncorrectException {
        User user = this.userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        if (!this.passwordEncoder.matches(password, user.getPassword())) {
            throw new PasswordIncorrectException("Invalid credentials");
        }

        return this.jwtUtil.generateToken(user.getUsername());
    }

    public void register(String username, String password) throws UserAlreadyExistException {
        if (this.userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistException("User already exist");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(this.passwordEncoder.encode(password));
        user.setRole(Constants.Role.USER);

        this.userRepository.save(user);
    }
}
