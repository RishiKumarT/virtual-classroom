	package com.rishi.classroom.service;
	
	import com.rishi.classroom.dto.LoginRequest;
	import com.rishi.classroom.dto.RegisterRequest;
	import com.rishi.classroom.dto.UserResponse;
	
	public interface UserService {
		
		UserResponse register(RegisterRequest request) throws Exception;
		UserResponse login(LoginRequest request) throws Exception;
		UserResponse getUser(Long id) throws Exception;
	}
