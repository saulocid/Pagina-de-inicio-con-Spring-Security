package com.saulociddev.springsecproject.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.saulociddev.springsecproject.entities.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario,String>{
    
    @Query("SELECT u FROM Usuario u WHERE u.email = :email")
    public Usuario buscarPorEmail(@Param("email") String email);

    @Query("SELECT u FROM Usuario u WHERE u.username = :username")
    public Usuario buscarPorUsuario(@Param("username") String username);

    @Query("SELECT u FROM Usuario u WHERE u.rol = :rol")
    public List<Usuario> buscarPorRol(@Param("rol") String rol);

}
