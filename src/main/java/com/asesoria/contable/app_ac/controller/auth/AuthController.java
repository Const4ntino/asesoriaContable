package com.asesoria.contable.app_ac.controller.auth;

import com.asesoria.contable.app_ac.model.dto.auth.AuthResponse;
import com.asesoria.contable.app_ac.model.dto.auth.LoginUsuarioRequest;
import com.asesoria.contable.app_ac.model.dto.auth.RegisterClienteRequest;
import com.asesoria.contable.app_ac.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/")
public class AuthController {

    private final AuthService authService;

    @PostMapping("login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginUsuarioRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("register")
    public ResponseEntity<AuthResponse> registerCliente(@RequestBody @Valid RegisterClienteRequest request) {
        return ResponseEntity.ok(authService.registerCliente(request));
    }

//    @PreAuthorize("hasRole('ADMINISTRADOR')")
//    @PostMapping("register-usuario")
//    public ResponseEntity<MensajeResponse> registerUsuario(@RequestBody @Valid UsuarioRequest request) {
//        authService.registerUsuario(request);
//        return ResponseEntity.ok(new MensajeResponse("Usuario registrado exitosamente"));
//    }


//    @GetMapping("quien-soy")
//    public ResponseEntity<String> quienSoy(@AuthenticationPrincipal Usuario usuario) {
//        return ResponseEntity.ok("Bienvenido " + usuario.getUsername());
//    }
//
//    @GetMapping("quien-soy-cliente")
//    public ResponseEntity<ClienteResponse> quienSoyCliente(@AuthenticationPrincipal Usuario usuario) {
//        return ResponseEntity.ok(authService.devolverCliente(usuario.getId()));
//    }

//    @PreAuthorize("hasRole('ADMINISTRADOR')")
//    @PostMapping("register-trabajador")
//    public ResponseEntity<MensajeResponse> registerTrabajador(@RequestBody RegisterUsuarioRequest request) {
//        authService.registerTrabajador(request);
//        return ResponseEntity.ok(new MensajeResponse("Trabajador registrado exitosamente"));
//    }
}
