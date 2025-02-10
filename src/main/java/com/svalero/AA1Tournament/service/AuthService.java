package com.svalero.AA1Tournament.service;

import com.svalero.AA1Tournament.constants.Constants;
import com.svalero.AA1Tournament.domain.User;
import com.svalero.AA1Tournament.exception.PasswordIncorrectException;
import com.svalero.AA1Tournament.exception.UserAlreadyExistException;
import com.svalero.AA1Tournament.exception.UserNotFoundException;
import com.svalero.AA1Tournament.repository.UserRepository;
import com.svalero.AA1Tournament.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
