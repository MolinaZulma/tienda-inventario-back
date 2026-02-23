package com.tienda.inventario.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tienda.inventario.Model.Usuario;
import com.tienda.inventario.Repository.UsuarioRepository;

@Service

public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public Usuario crearUsuario(Usuario usuario){
       if(usuarioRepository.findByEmail(usuario.getEmail()).isPresent()){
            throw new RuntimeException("El correo ya existe");
       }
       usuario.setPassword(passwordEncoder.encode(usuario.getPassword())); 
       Usuario usuarioGuardado = usuarioRepository.save(usuario);
       System.out.println("ID generado: "+ usuarioGuardado.getId());      
        return usuarioGuardado;
    }
    public List<Usuario> listarTodos(){
        return usuarioRepository.findByActivo(true);
    }

    public void desactivarUsuario(Long id){
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(()-> new RuntimeException("Usuario no encontrado"));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    public void activarUsuario(Long id){
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(()-> new RuntimeException("Usuario no encontrado"));
            usuario.setActivo(true);
            usuarioRepository.save(usuario);
    }

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }
}
