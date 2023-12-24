package com.saulociddev.springsecproject.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Controller;
import com.saulociddev.springsecproject.services.UsuarioService;

@Controller
public class WebSecurity {

    @Autowired
    public UsuarioService usuarioServices;

    // Encriptado de password
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(usuarioServices).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/admin/*").hasAnyRole("ADMIN","MODERATOR")
                        .anyRequest()
                        .permitAll())
                .formLogin((login) -> login
                        .loginPage("/")
                        .loginProcessingUrl("/login") // PostMapping de logeo
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/inicio")
                        .permitAll())
                .logout((logout) -> logout
                        .logoutUrl("/cerrar") // PostMapping de cerrie de sesión
                        .logoutSuccessUrl("/")
                        .permitAll())
                .csrf((csrf) -> csrf.disable());
        return http.build();
    }

}
