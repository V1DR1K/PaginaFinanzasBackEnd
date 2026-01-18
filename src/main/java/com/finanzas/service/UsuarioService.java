package com.finanzas.service;

import com.finanzas.models.Usuario;
import com.finanzas.repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        // Crear usuario por defecto si no existe ninguno
        if (usuarioRepository.count() == 0) {
            Usuario usuarioPorDefecto = new Usuario();
            usuarioPorDefecto.setUsuario("tomas");
            usuarioPorDefecto.setContrasena(passwordEncoder.encode("tomas"));
            usuarioRepository.save(usuarioPorDefecto);
            System.out.println("Usuario por defecto creado: tomas/tomas");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        return new com.finanzas.config.CustomUserDetails(
                usuario.getId(),
                usuario.getUsuario(),
                usuario.getContrasena(),
                new ArrayList<>()
        );
    }

    public Optional<Usuario> findByUsuario(String usuario) {
        return usuarioRepository.findByUsuario(usuario);
    }

    public boolean validarCredenciales(String usuario, String contrasena) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsuario(usuario);
        if (usuarioOpt.isPresent()) {
            return passwordEncoder.matches(contrasena, usuarioOpt.get().getContrasena());
        }
        return false;
    }

    public boolean cambiarContrasena(String usuario, String contrasenaActual, String contrasenaNueva) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsuario(usuario);

        if (usuarioOpt.isPresent()) {
            Usuario usuarioEntity = usuarioOpt.get();

            // Verificar que la contraseña actual sea correcta
            if (passwordEncoder.matches(contrasenaActual, usuarioEntity.getContrasena())) {
                usuarioEntity.setContrasena(passwordEncoder.encode(contrasenaNueva));
                usuarioRepository.save(usuarioEntity);
                return true;
            }
        }
        return false;
    }
}
