package com.rishi.classroom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rishi.classroom.dto.LoginRequest;
import com.rishi.classroom.dto.RegisterRequest;
import com.rishi.classroom.dto.UserResponse;
import com.rishi.classroom.service.UserServiceImpl;

@RestController
@RequestMapping("/api/users")
public class UserController {
	
	@Autowired
	private UserServiceImpl userService;
	
	@GetMapping("/")
	public String working() {
		return "Priya its working priyaaaa";
	}
	@PostMapping("/register")
	public UserResponse register(@RequestBody RegisterRequest request) throws Exception {
		return userService.register(request);
	}
	
	@PostMapping("/login")
	public UserResponse login(@RequestBody LoginRequest request) throws Exception{
		return userService.login(request);
	}
	
	@GetMapping("/{id}")
	public UserResponse getUser(@PathVariable Long id) throws Exception {
		return userService.getUser(id);
	}
	
}
