package joinMe.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import joinMe.security.AuthenticationFailure;
import joinMe.security.AuthenticationSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile("!test")
public class SecurityConfig {

    private final ObjectMapper objectMapper;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    public SecurityConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        final AuthenticationSuccess authSuccess = authenticationSuccess();

        http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .authenticationProvider(authenticationProvider)
                .exceptionHandling(ehc -> ehc.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(conf -> conf.configurationSource(corsConfigurationSource()))
                .headers(customizer -> customizer.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .formLogin(fl -> fl
                        .loginProcessingUrl("/login")
                        .successHandler(authSuccess)
                        .failureHandler(authenticationFailureHandler()))
                .logout(lgt -> lgt
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(authSuccess));
        return http.build();
    }

    private AuthenticationFailure authenticationFailureHandler() {
        return new AuthenticationFailure(objectMapper);
    }

    private AuthenticationSuccess authenticationSuccess() {
        return new AuthenticationSuccess(objectMapper);
    }

    private CorsConfigurationSource corsConfigurationSource() {
        // We're allowing all methods from all origins so that the application API is usable also by other clients
        // than just the UI.
        // This behavior can be restricted later.
        CorsConfiguration configuration = new CorsConfiguration();
        // AllowCredentials requires a particular origin configured, * is rejected by the browser
//        configuration.setAllowCredentials(true);
//        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.addExposedHeader(HttpHeaders.LOCATION);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
