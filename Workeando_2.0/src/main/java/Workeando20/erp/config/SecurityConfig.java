package Workeando20.erp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import Workeando20.erp.seguridad.repository.UsuarioRepository;
import Workeando20.erp.seguridad.service.CustomUserDetailsService;

@Configuration
public class SecurityConfig {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    private CustomAuthenticationSuccessHandler successHandler;

    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;

    @Autowired
    public SecurityConfig(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(usuarioRepository);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/","/login", "/registro", "/403", "/css/**", "/js/**", "/images/**", "/public/**")
                        .permitAll()
                        .requestMatchers("/api/test/**").authenticated()
                        .requestMatchers("/freelancer/**").hasAuthority("VER_PANEL_FREELANCER")
                        .requestMatchers("/empleador/**").hasAuthority("VER_PANEL_EMPLEADOR")
                        .requestMatchers("/admin/**").hasAuthority("GESTIONAR_USUARIOS")
                        .requestMatchers("/api/admin/roles/**").hasAuthority("GESTIONAR_USUARIOS")
                        .requestMatchers("/api/admin/categorias/**").hasAuthority("GESTIONAR_CATEGORIAS")
                        .requestMatchers("/api/admin/usuarios/**").hasAuthority("GESTIONAR_USUARIOS")
                        .anyRequest().authenticated())
                .authenticationProvider(customAuthenticationProvider)
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(successHandler)
                        .failureUrl("/login?error")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())
                .exceptionHandling(ex -> ex.accessDeniedPage("/403"));

        return http.build();
    }

}
