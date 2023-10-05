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
 * Quando scegliamo di autenticare l'utente recuperando le sue credenziali salvate sul DB,
 * usiamo questa classe per wrappare quanto recuperato in un oggetto di tipo UserDetails
 * che contiene metodi per la gestione dell'utenza.
 * 
 * Questo wrapping avviene avvalendosi di un'ulteriore classe (che implementa UserDetails)
 * che abbiamo chiamato UserInfoDetails
 * 
 */

@Component
public class UserInfoDetailsService implements UserDetailsService {

	@Autowired
	private UserInfoRepository repository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		System.out.println("ENTRO NEL PRIMO DOVE USO LO USERNAME?");
		Optional<UserInfo> userInfo = repository.findByUsername(username);

		return userInfo.map(UserInfoDetails::new)
				.orElseThrow(() -> new UsernameNotFoundException("user not found " + username));
	}

}
