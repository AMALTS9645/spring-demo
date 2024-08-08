 //code-start
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@SpringBootApplication
public class LoginApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoginApiApplication.class, args);
    }
}
//code-end

//code-start
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    // PasswordEncoder should be injected from the application configuration
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Authenticate user and return authentication token if successful.
     *
     * @param request The login request containing username and password.
     * @return The authentication token if successful, otherwise an error response.
     */
    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticate(@Valid @RequestBody LoginRequest request) {
        try {
            UserDetails userDetails = loadUserByUsername(request.getUsername());
            if (passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
                return ResponseEntity.ok("Authentication successful");
            } else {
                return ResponseEntity.status(401).body("Authentication failed");
            }
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(401).body("Username not found");
        }
    }

    private UserDetails loadUserByUsername(String username) {
        UserDetails userDetails = null;
        try {
            userDetails = userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            throw e;
        }
        return userDetails;
    }
}
//code-end

//code-start
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), user.getAuthorities());
    }

    public void save(User user) {
        user.setPassword(encryptPassword(user.getPassword()));
        userRepository.save(user);
    }

    private String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }
}
//code-end

//code-start
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User {

    @Id
    private Long id;

    private String username;

    private String password;

    // Add additional fields and methods as needed

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
//code-end

//code-start
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.Entity;
import javax.persistence.Id;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
//code-end

//code-start
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/api")
public class UserController {

    @Autowired
    private LoginController loginController;

    /**
     * Handle user registration requests.
     *
     * @param request The HTTP request containing user details.
     * @return The registration response.
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        // Validate and register user
        return ResponseEntity.ok("User registered successfully");
    }
}
//code-end
//code-end