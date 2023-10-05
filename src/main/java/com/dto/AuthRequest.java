package com.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * DTO per fare la POST che serve ad ottenere il token
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {

	private String principal;
	private String password;
}
