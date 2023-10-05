package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dto.AuthRequest;
import com.dto.GenericResponse;
import com.dto.SignupRequest;
import com.service.UserInfoService;

@CrossOrigin("https://www.google.com")
@RestController
@RequestMapping("/api/v1")
public class UserController {

	@Autowired
	private UserInfoService userService;

	@PostMapping("/addUser")
	public ResponseEntity<GenericResponse> addUser(@RequestBody SignupRequest userInfo) {
		return userService.addUser(userInfo);
	}

	@PostMapping("/getToken")
	public ResponseEntity<GenericResponse> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
		return userService.getToken(authRequest);
	}

	@GetMapping("/isRunning")
	public String isRunning() {
		return "Service is running";
	}

	@GetMapping("/whereAmI")
	public String whereAmI() {
		return "Here";
	}

}
