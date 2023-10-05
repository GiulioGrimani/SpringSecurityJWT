package com.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.config.UserInfoDetails;
import com.entity.UserInfo;
import com.repository.UserInfoRepository;

/*
 * Questa classe fa la stessa cosa della sua omonima, ma con email e password
 * come credenziali d'accesso. 
 * 
 * Idealmente parlando, questo secondo modo di autenticarsi dovrebbe essere
 * proprio diverso dal primo, ovvero NON recuperare le credenziali dal DB, ma
 * usare il protocollo OAuth2 come fanno i social login
 */

@Component
public class UserInfoDetailsService2 implements UserDetailsService {

	@Autowired
	private UserInfoRepository repository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		System.out.println("ENTRO NEL SECONDO DOVE USO L'EMAIL?");
		Optional<UserInfo> userInfo = repository.findByEmail(email);
//		System.out.println("STAMPO USER BY EMAIL: " + userInfo.get());

		return userInfo.map(UserInfoDetails::new)
				.orElseThrow(() -> new UsernameNotFoundException("user not found " + email));
	}
}
