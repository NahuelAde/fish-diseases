package com.fish_diseases.auth_service.security;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fish_diseases.auth_service.entities.User;
import com.fish_diseases.auth_service.repositories.UserRepository;

/**
 * Implementación personalizada de {@link UserDetailsService} para cargar
 * los detalles de un usuario desde la base de datos. Esta clase es utilizada
 * por Spring Security para autenticar a los usuarios.
 * 
 * Proporciona el método {@link #loadUserByUsername(String)} para cargar los
 * detalles de un usuario basado en su nombre de usuario, incluyendo roles y
 * permisos.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
    private final UserRepository userRepository;
	
	@Autowired
	private MessageSource messageSource;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(messageSource.getMessage("username.notfound", null, Locale.getDefault())));

        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), authorities);
    }
}
