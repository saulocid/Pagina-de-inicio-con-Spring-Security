package com.saulociddev.springsecproject.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.saulociddev.springsecproject.entities.Usuario;
import com.saulociddev.springsecproject.enums.Rol;
import com.saulociddev.springsecproject.exceptions.MyException;
import com.saulociddev.springsecproject.repositories.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = userRepo.buscarPorEmail(email);
        if (usuario != null) {
            List<GrantedAuthority> permisos = new ArrayList<>();
            GrantedAuthority p = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toString());
            permisos.add(p);
            ServletRequestAttributes atrib = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession sesion = atrib.getRequest().getSession();
            sesion.setAttribute("logeado", usuario);
            return new User(usuario.getEmail(), usuario.getPassword(), permisos);
        } else {
            throw new UsernameNotFoundException("Error al encontrar usuario");
        }
    }

    @Transactional
    public void nuevoUsuario(String username, String email, String password, String password2) throws MyException {
        validarUsuario(username, email, password, password2);
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setEmail(email);
        usuario.setPassword(new BCryptPasswordEncoder().encode(password));
        usuario.setRol(Rol.USER);
        userRepo.save(usuario);
    }

    public List<Usuario> buscarUsuarios() {
        return userRepo.findAll();
    }

    @Transactional
    public void borrarUsuario(String id) throws MyException {
        validarId(id);
        Usuario usuario = userRepo.buscarPorId(id);
        userRepo.delete(usuario);
    }

    public void validarUsuario(String usuario, String email, String password, String password2) throws MyException {
        if (usuario.isEmpty()) {
            throw new MyException("El usuario no puede ser nulo");
        }
        if (email.isEmpty()) {
            throw new MyException("El email no puede ser nulo");
        }
        if (password.length() < 8) {
            throw new MyException("Las contraseña debe tener mínimo 8 caracteres");
        }
        if (!password.equals(password2)) {
            throw new MyException("Las contraseñas no son iguales");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new MyException("La constraseña debe contener al menos una letra mayúscula");
        }
        List<Usuario> usuarios = userRepo.findAll();
        if (usuarios != null) {
            for (Usuario user : usuarios) {
                if (user.getUsername().equals(usuario)) {
                    throw new MyException("El usuario ya existe");
                }
                if (user.getEmail().equals(email)) {
                    throw new MyException("El email ya existe");
                }
            }
        }
    }

    public void validarLogin(String email, String password) throws MyException {
        try {
            Usuario usuario = userRepo.buscarPorEmail(email);
            if (usuario == null) {
                throw new MyException("El usuario no existe");
            }
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if (!passwordEncoder.matches(password, usuario.getPassword())) {
                throw new MyException("La contraseña es incorrecta");
            }
        } catch (MyException e) {
            throw new MyException("Error inesperado");
        }
    }

    public void validarId(String dato) throws MyException {
        if (dato == null) {
            throw new MyException("El dato no puede ser nulo");
        }
    }

}
