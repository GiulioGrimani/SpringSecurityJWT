package com.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * DTO per fare la POST che serve all'autenticazione
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenericResponse {

	private String message;
	private Object data;
}
