package com.nezhub.app.infrastructure.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Utilidad para extraer información del usuario autenticado.
 *
 * PROPÓSITO:
 * - Obtener email del usuario autenticado desde SecurityContext
 * - Usado en mutations protegidas para saber quién hace la operación
 */
public class AuthenticationUtils {


    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No hay usuario autenticado");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }

        throw new IllegalStateException("Principal no es UserDetails");
    }
}
