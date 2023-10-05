package com.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * DTO della response della relativa request
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

	private Integer id;
	private String token;

}
