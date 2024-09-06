package sesac.server.common.config;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import sesac.server.auth.filter.AccessTokenFilter;
import sesac.server.auth.filter.RefreshTokenFilter;
import sesac.server.auth.handler.CustomAccessDeniedHandler;
import sesac.server.auth.handler.CustomAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Log4j2
public class SecurityConfig {

    private final AccessTokenFilter accessTokenFilter;
    private final RefreshTokenFilter refreshTokenFilter;
    @Value("${origins}")
    private String origins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(requests -> requests
                // accounts
                .requestMatchers(HttpMethod.DELETE, "/accounts/**").authenticated()
                .requestMatchers("/accounts/**").permitAll()

                // campuses
                .requestMatchers(HttpMethod.GET, "/campuses", "/campuses/{campusId}/courses")
                .permitAll()
                .requestMatchers("/campuses/**").hasRole("MANAGER")

                // restaurants
                .requestMatchers(HttpMethod.GET, "/restaurants/**").authenticated()
                .requestMatchers("/restaurants/**").hasRole("MANAGER")

                // user
                .requestMatchers("/user/students/**").hasRole("MANAGER")

                // runningmates
                .requestMatchers(HttpMethod.GET, "/runningmates/{runningmateId}/activities")
                .authenticated()
                .requestMatchers(HttpMethod.POST, "/runningmates/{runningmateId}/activities")
                .authenticated()
                .requestMatchers(HttpMethod.GET, "/runningmates/{runningmateId}/activity-form")
                .authenticated()
                .requestMatchers(HttpMethod.GET,
                        "/runningmates/{runningmateId}/activities/{activityId}").authenticated()
                .requestMatchers(HttpMethod.PUT, "/runningmates/{runningmateId}/trans-leader")
                .authenticated()
                .requestMatchers(HttpMethod.DELETE,
                        "/runningmates/{runningmateId}/members/{memberId}").authenticated()
                .requestMatchers("/runningmates/**").hasRole("MANAGER")
                
                .anyRequest().authenticated());

        http.exceptionHandling(handler -> handler
                .accessDeniedHandler(accessDeniedHandler())
                .authenticationEntryPoint(authenticationEntryPoint()));

        http.addFilterBefore(accessTokenFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(refreshTokenFilter, AccessTokenFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(origins.split(",")));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(
                Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);
//        configuration.setMaxAge(3600L); // 1시간 동안 preflight 요청 결과를 캐시
//        configuration.setExposedHeaders(List.of("Set-Cookie", "Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}