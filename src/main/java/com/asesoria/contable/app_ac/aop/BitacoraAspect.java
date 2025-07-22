package com.asesoria.contable.app_ac.aop;

import com.asesoria.contable.app_ac.model.entity.Usuario;
import com.asesoria.contable.app_ac.service.BitacoraService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@RequiredArgsConstructor
public class BitacoraAspect {

    private final BitacoraService bitacoraService;

    @Pointcut("@annotation(com.asesoria.contable.app_ac.aop.RegistrarBitacora)")
    public void bitacoraPointcut() {}

    @AfterReturning(pointcut = "bitacoraPointcut()", returning = "result")
    public void registrarMovimiento(JoinPoint joinPoint, Object result) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RegistrarBitacora annotation = method.getAnnotation(RegistrarBitacora.class);

        String descripcion;
        if (annotation.descripcion().isEmpty()) {
            descripcion = "El usuario " + usuario.getUsername() + " realizó la acción de " + annotation.accion() + " en el módulo " + annotation.modulo();
        } else {
            descripcion = procesarDescripcion(annotation.descripcion(), joinPoint, signature);
        }

        bitacoraService.registrarMovimiento(usuario, annotation.modulo(), annotation.accion(), descripcion);
    }

    private String procesarDescripcion(String descripcion, JoinPoint joinPoint, MethodSignature signature) {
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        Map<String, Object> params = new HashMap<>();
        for (int i = 0; i < parameterNames.length; i++) {
            params.put(parameterNames[i], args[i]);
        }

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            descripcion = descripcion.replace("#[" + entry.getKey() + "]", String.valueOf(entry.getValue()));
        }

        return descripcion;
    }
}
