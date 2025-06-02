package com.fish_diseases.auth_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.fish_diseases.auth_service.dtos.CreateUserDTO;
import com.fish_diseases.auth_service.dtos.LoginUserDTO;
import com.fish_diseases.auth_service.dtos.UpdateUserDTO;
import com.fish_diseases.auth_service.entities.User;

/**
 * Mapper para convertir entre objetos DTO (Data Transfer Object) y entidades User.
 * Utiliza MapStruct para simplificar la conversión y mapeo entre las clases.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Convierte un objeto {@link CreateUserDTO} en una entidad {@link User}.
     * Se ignoran ciertos campos como "admin", "userId", "lastLogin" y "updatedAt" ya que
     * no se proporcionan al crear un usuario.
     * 
     * @param createUserDTO El DTO con la información para crear un usuario.
     * @return La entidad {@link User} creada a partir del DTO.
     */
    @Mapping(target = "admin", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
	User convertCreateUserDtoToUserEntity(CreateUserDTO createUserDTO);
	
    /**
     * Convierte un objeto {@link UpdateUserDTO} en una entidad {@link User}.
     * Se ignoran campos como "firstname", "lastname", "username", "userId", "lastLogin", 
     * y "updatedAt" ya que no se deben actualizar en este proceso.
     * 
     * @param updateUserDTO El DTO con la información para actualizar un usuario.
     * @return La entidad {@link User} con los datos actualizados a partir del DTO.
     */
    @Mapping(target = "firstname", ignore = true)
    @Mapping(target = "lastname", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
	User convertUpdateUserDtoToUserEntity(UpdateUserDTO updateUserDTO);

    /**
     * Convierte un objeto {@link LoginUserDTO} en una entidad {@link User}.
     * Se ignoran muchos campos como "nationalId", "roles", "phone", "admin", "email", 
     * "enabled", "city", "country", "jobPosition", "company", "firstname", "lastname", 
     * "username", "userId", "lastLogin" y "updatedAt" ya que no son necesarios para el inicio de sesión.
     * 
     * @param loginUserDTO El DTO con la información del usuario para el inicio de sesión.
     * @return La entidad {@link User} correspondiente al usuario que inicia sesión.
     */
    @Mapping(target = "nationalId", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "admin", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "city", ignore = true)
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "jobPosition", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "firstname", ignore = true)
    @Mapping(target = "lastname", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
	User convertLoginUserDtoToUserEntity(LoginUserDTO loginUserDTO);
}
