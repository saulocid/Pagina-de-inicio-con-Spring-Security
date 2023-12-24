package com.saulociddev.springsecproject.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.saulociddev.springsecproject.entities.Usuario;
import com.saulociddev.springsecproject.exceptions.MyException;
import com.saulociddev.springsecproject.services.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@RequestMapping("/")
public class AppController {

    @Autowired
    private UsuarioService userServ;
    
    @GetMapping("/")
    public String index(){
        return "index";
    }

    @PostMapping("/registrar")
    public String registrar(@RequestParam String username, @RequestParam String email, @RequestParam String password, @RequestParam String password2, ModelMap modelo) throws MyException {
        try {
            userServ.nuevoUsuario(username, email, password, password2);
            modelo.addAttribute("exito", "Te has registrado con éxito! Ahora inicia sesión.");
            return "index";
        } catch (MyException e) {
            modelo.addAttribute("error", e.getMessage());
            return "index";
        }
    }

    @PostMapping("/login")
    public String login(@RequestBody String email, @RequestParam String password, ModelMap modelo) throws MyException {
        try {
            userServ.validarLogin(email, password);
            return "inicio";
        } catch (Exception e) {
            // Toma la excepción de UserDetail del logeo "UsernameNotFoundException("Error al encontrar usuario")"
            return "index";
        }
    }
    
    @PreAuthorize("hasAnyRole('USER','ADMIN','MODERATOR')")
    @GetMapping("/inicio")
    public String inicio(HttpSession sesion){
        Usuario logeado = (Usuario) sesion.getAttribute("logeado");
        if (logeado.getRol().toString().equals("ADMIN") || logeado.getRol().toString().equals("MODERATOR")) {
            return "redirect:/admin";
        }
        return "inicio";
    }

    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    @GetMapping("/admin")
    public String admin(){
        return "admin";
    }

}
