package joinMe.environment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import joinMe.db.entity.User;
import joinMe.security.model.UserDetails;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;

public class Environment {

    private static ObjectMapper objectMapper;

    /**
     * Gets a Jackson object mapper for mapping JSON to Java and vice versa.
     *
     * @return Object mapper
     */
    public static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            JavaTimeModule module = new JavaTimeModule();
            objectMapper.registerModule(module);
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }
        return objectMapper;
    }

    public static HttpMessageConverter<?> createDefaultMessageConverter() {
        return new MappingJackson2HttpMessageConverter(getObjectMapper());
    }

    public static HttpMessageConverter<?> createStringEncodingMessageConverter() {
        return new StringHttpMessageConverter(StandardCharsets.UTF_8);
    }

    /**
     * Initializes security context with the specified user.
     *
     * @param user User to set as currently authenticated
     */
    public static void setCurrentUser(User user) {
        final UserDetails userDetails = new UserDetails(user, new HashSet<>());
        SecurityContext context = new SecurityContextImpl();
        context.setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
        SecurityContextHolder.setContext(context);
        System.out.println("Current SecurityContext: " + SecurityContextHolder.getContext());
    }

    public static void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }
}
