package com.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * DTO per fare la POST che serve alla registrazione nuovo utente (addUser)
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {

	private String username;
	private String email;
	private String password;
	private String roles;
}