package com.service;

import org.springframework.http.ResponseEntity;

import com.dto.AuthRequest;
import com.dto.GenericResponse;
import com.dto.SignupRequest;

public interface UserInfoService {

	public ResponseEntity<GenericResponse> getToken(AuthRequest authRequest);

	public ResponseEntity<GenericResponse> addUser(SignupRequest userInfo);

}
