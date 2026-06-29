/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.auth.mapper;

import com.lynx.auth.dto.response.UsuarioResponse;
import com.lynx.auth.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** Mapeo entre la entidad Usuario y sus DTOs de respuesta. */
@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(target = "rol", expression = "java(usuario.getRol() != null ? usuario.getRol().name() : null)")
    @Mapping(target = "estadoRegistro", expression = "java(usuario.getEstadoRegistro() != null ? usuario.getEstadoRegistro().name() : null)")
    UsuarioResponse toResponse(Usuario usuario);
}
