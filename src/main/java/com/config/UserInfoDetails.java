package com.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.entity.UserInfo;

/*
 * Questa classe realizza il mapping delle credenziali dell'utente a oggetto di tipo UserDetails.
 * Spring Security si interfaccia con l'utente usando l'oggetto che ne rappresenta le sue credenziali,
 * ovvero UserDetails. Tale oggetto e' arricchito di metodi per la gestione dell'utenza (con tanto
 * di lista di autorizzazioni(da approfondire)) 
 */
public class UserInfoDetails implements UserDetails {

	private static final long serialVersionUID = -8773921465190832995L;
	private String username;
	private String email;
	private String password;
	private List<GrantedAuthority> authorities;

	public UserInfoDetails(UserInfo userInfo) {
		username = userInfo.getUsername();
		email = userInfo.getEmail();
		password = userInfo.getPassword();
		authorities = Arrays.stream(userInfo.getRoles().split(",")).map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
		System.out.println("STO DENTRO UserInfoDetails. Lista di authorities= " + authorities);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	// Questa booleana "banna" l'utente :)
	@Override
	public boolean isEnabled() {
		return true;
	}
}
