package ua.hodik.gym.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserDetailsService userDetails;

    @Autowired
    public SecurityConfig(UserDetailsService userDetails) {
        this.userDetails = userDetails;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for APIs
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/gym/**").permitAll())
//                        .requestMatchers("/gym/auth/login/**", "/gym/trainers/registration/**", "/gym/actuator/**").permitAll() // Allow unauthenticated access to these paths
                //         .requestMatchers("/gym/**").authenticated()) // Secure other endpoints
                .build();

    }
//        return http.csrf(AbstractHttpConfigurer::disable)
//                .cors(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(auth->auth.requestMatchers("/gym/auth/login", "/gym/trainers/registration", "/gym/swagger-ui", "/gym/v3/api-docs", "/gym/actuator/**", "/gym/h2-console").permitAll())
////                .authorizeHttpRequests(auth -> auth.requestMatchers("/gym/auth/login", "/gym/trainers/registration", "/gym/swagger-ui",
////                                "/gym/v3/api-docs", "/gym/actuator/**", "/gym/h2-console").permitAll()
////                        .requestMatchers("gym/**").authenticated())
////                .formLogin(AbstractAuthenticationFilterConfigurer::permitAll)
//                .build();

//        http
//                .authorizeHttpRequests()
////          .anyRequest().permitAll()
//                .requestMatchers("/admin/**").hasRole("ADMIN")
//                .requestMatchers("/auth/register", "/auth/login", "/error", "/index", "/schedule").permitAll()
//                .anyRequest().hasAnyRole("USER")
//                .and()
//                .formLogin().loginPage("/auth/login")
//                .loginProcessingUrl("/process_login")
//                .defaultSuccessUrl("/index", true)
//                .failureUrl("/auth/login?error")
//                .and()
//                .logout()
//                .logoutUrl("/logout")
//                .deleteCookies("JSESSIONID")
//                .logoutSuccessUrl("/auth/login");
//        return http.build();


    @Bean
    protected DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetails);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
