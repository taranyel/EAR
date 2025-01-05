package joinMe.rest;

import joinMe.config.SecurityConfig;
import joinMe.environment.Environment;
import joinMe.environment.Generator;
import joinMe.environment.TestConfiguration;
import joinMe.db.entity.Role;
import joinMe.db.entity.User;
import joinMe.rest.dto.Mapper;
import joinMe.security.DefaultAuthenticationProvider;
import joinMe.service.AddressService;
import joinMe.service.UserService;
import joinMe.service.security.UserDetailsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(
        classes = {UserControllerSecurityTest.TestConfig.class,
                SecurityConfig.class, DefaultAuthenticationProvider.class})
public class UserControllerSecurityTest extends BaseControllerTestRunner {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void setUp() {
        this.objectMapper = Environment.getObjectMapper();
    }

    @AfterEach
    public void tearDown() {
        Environment.clearSecurityContext();
        Mockito.reset(userService);
    }

    @Configuration
    @TestConfiguration
    public static class TestConfig {

        @MockBean
        private UserService userService;

        @MockBean
        private AddressService addressService;

        @MockBean
        private UserDetailsService userDetailsService;

        @MockBean
        private PasswordEncoder passwordEncoder;

        @Bean
        public UserController userController() {
            return new UserController(userService, new Mapper(), addressService);
        }
    }

    @WithAnonymousUser
    @Test
    public void registerSupportsAnonymousAccess() throws Exception {
        final User toRegister = Generator.generateUser();
        String jsonToRegister = "{\"user\":" + toJson(toRegister) + ",\"address\":" + toJson(toRegister.getAddress()) + "}";
        mockMvc.perform(
                        post("/users").content(jsonToRegister).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated());
        verify(userService).persist(any(User.class));
    }

    @WithAnonymousUser
    @Test
    public void registerAdminThrowsUnauthorizedForAnonymousUser() throws Exception {
        final User toRegister = Generator.generateUser();
        toRegister.setRole(Role.ADMIN);
        String jsonToRegister = "{\"user\":" + toJson(toRegister) + ",\"address\":" + toJson(toRegister.getAddress()) + "}";
        mockMvc.perform(
                        post("/users").content(jsonToRegister).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isUnauthorized());
        verify(userService, never()).persist(any());
    }

    @WithMockUser(roles = "USER")
    @Test
    public void registerAdminThrowsForbiddenForNonAdminUser() throws Exception {
        final User toRegister = Generator.generateUser();
        toRegister.setRole(Role.ADMIN);
        String jsonToRegister = "{\"user\":" + toJson(toRegister) + ",\"address\":" + toJson(toRegister.getAddress()) + "}";
        mockMvc.perform(
                        post("/users").content(jsonToRegister).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isForbidden());
        verify(userService, never()).persist(any());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void registerAdminIsAllowedForAdminUser() throws Exception {
        final User toRegister = Generator.generateUser();
        toRegister.setRole(Role.ADMIN);
        String jsonToRegister = "{\"user\":" + toJson(toRegister) + ",\"address\":" + toJson(toRegister.getAddress()) + "}";
        mockMvc.perform(
                        post("/users").content(jsonToRegister)
                                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated());
        verify(userService).persist(any(User.class));
    }


}
