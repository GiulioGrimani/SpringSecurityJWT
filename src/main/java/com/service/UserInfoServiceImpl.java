package com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dto.AuthRequest;
import com.dto.AuthResponse;
import com.dto.GenericResponse;
import com.dto.SignupRequest;
import com.dto.SignupResponse;
import com.entity.UserInfo;
import com.repository.UserInfoRepository;

@Service
public class UserInfoServiceImpl implements UserInfoService {

	@Autowired
	private UserInfoRepository ur;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private AuthenticationManager authenticationManager;

	// Inseriamo un utente nel DB: username, email, password e ruolo
	public ResponseEntity<GenericResponse> addUser(SignupRequest userInfo) {
		// Con passwordEncoder crittografiamo la password sul DB
		userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
		SignupResponse signupResponse = new SignupResponse();
		GenericResponse genericResponse = new GenericResponse();

		try {
			UserInfo addedUser = ur.save(toEntity(userInfo));
			genericResponse.setMessage("Inserimento utente avvenuto con successo");
			signupResponse.setId(addedUser.getUtenteId());
			signupResponse.setUsername(userInfo.getUsername());
			signupResponse.setEmail(userInfo.getEmail());
			genericResponse.setData(signupResponse);
			return new ResponseEntity<GenericResponse>(genericResponse, HttpStatus.CREATED);
		} catch (IllegalArgumentException iae) {
			iae.printStackTrace();
			System.err.println("*** BAD REQUEST ***");
			genericResponse.setMessage("Inserimento utente fallito");
			return new ResponseEntity<GenericResponse>(genericResponse, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("*** SERVER ERROR ***");
			genericResponse.setMessage("FALLIMENTO GENERALE - SI SALVI CHI PUO'");
			return new ResponseEntity<GenericResponse>(genericResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@Override
	public ResponseEntity<GenericResponse> getToken(AuthRequest authRequest) {
		AuthResponse authResponse = new AuthResponse();
		GenericResponse genericResponse = new GenericResponse();

		// L'autenticazione avviene passando all'authentication manager due parametri:
		// un principal ed una credential. Dal momento che stiamo testando due modi
		// diversi di fornire l'autenticazione, il principal sara' l'email nel primo
		// caso, lo username nel secondo. Tuttavia la generazione del token (JWT) usera'
		// sempre lo username e la password. (per capire il perche' dei tre catch, vedi
		// la documentazione sul metodo authenticationManager.authenticate(...)
		Authentication authentication = null;
		try {
			authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authRequest.getPrincipal(), authRequest.getPassword()));
			if (authentication.isAuthenticated()) {
				String principal = authRequest.getPrincipal();
				// Recupero le credenziali dell'utente per poter generare il token sempre con lo
				// username
				UserInfo userInfo = ur.findByEmailOrUsername(principal).get();
				authResponse.setId(userInfo.getUtenteId());
				authResponse.setToken(jwtService.generateToken(userInfo.getUsername()));
				genericResponse.setData(authResponse);
				genericResponse.setMessage("Beccate sto token (featuring Mr. Cerqua)");
				return new ResponseEntity<GenericResponse>(genericResponse, HttpStatus.OK);
			}
		} catch (DisabledException de) {
			de.printStackTrace();
			genericResponse.setMessage("Utenza disabilitata: sooka");
			return new ResponseEntity<GenericResponse>(genericResponse, HttpStatus.UNAUTHORIZED);
		} catch (LockedException le) {
			le.printStackTrace();
			genericResponse.setMessage("Utenza bloccata: sooka");
			return new ResponseEntity<GenericResponse>(genericResponse, HttpStatus.UNAUTHORIZED);
		} catch (BadCredentialsException bce) {
			bce.printStackTrace();
			genericResponse.setMessage("Credenziali errate (cojone)");
			return new ResponseEntity<GenericResponse>(genericResponse, HttpStatus.UNAUTHORIZED);
		}
		genericResponse.setMessage("NON SO CHE DIAVOLO E' SUCCESSO (nel dubbio, sooka)");
		return new ResponseEntity<GenericResponse>(genericResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private UserInfo toEntity(SignupRequest userInfo) {
		UserInfo u = new UserInfo();
		u.setUsername(userInfo.getUsername());
		u.setEmail(userInfo.getEmail());
		u.setPassword(userInfo.getPassword());
		u.setRoles(userInfo.getRoles());
		return u;

	}

}
