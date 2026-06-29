/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.auth.controller;

import com.lynx.auth.dto.response.UsuarioResponse;
import com.lynx.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoints de administración (rol ADMIN). El control de rol lo aplica
 * el api-gateway sobre la ruta /admin/**.
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AuthService authService;

    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        return ResponseEntity.ok(authService.listarUsuarios());
    }

    @PutMapping("/usuarios/{id}/bloquear")
    public ResponseEntity<UsuarioResponse> bloquear(@PathVariable("id") Long id) {
        return ResponseEntity.ok(authService.bloquearUsuario(id));
    }

    @PutMapping("/usuarios/{id}/desbloquear")
    public ResponseEntity<UsuarioResponse> desbloquear(@PathVariable("id") Long id) {
        return ResponseEntity.ok(authService.desbloquearUsuario(id));
    }
}
