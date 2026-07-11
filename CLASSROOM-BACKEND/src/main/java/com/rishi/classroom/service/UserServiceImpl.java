package com.rishi.classroom.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.rishi.classroom.dto.LoginRequest;
import com.rishi.classroom.dto.RegisterRequest;
import com.rishi.classroom.dto.UserResponse;
import com.rishi.classroom.model.User;
import com.rishi.classroom.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {


	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;

	public UserResponse register(RegisterRequest request) throws Exception {
		if(userRepository.existsByEmail(request.getEmail()))throw new Exception("Email already exists");
		
		User user=User.builder()
					  .name(request.getName())
					  .email(request.getEmail())
					  .password(passwordEncoder.encode(request.getPassword()))
					  .createdAt(LocalDateTime.now())
					  .updatedAt(LocalDateTime.now())
					  .build();
		
		userRepository.save(user);
		
		return mapToResponse(user);
	}
	
	public UserResponse login(LoginRequest request) throws Exception {
		
		User user = userRepository.findByEmail(request.getEmail())
								  .orElseThrow(()->new Exception("Invalid email or password"));
		if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) throw new Exception("Invalid email or password");
		
		return mapToResponse(user);
	}

	public UserResponse getUser(Long id) throws Exception {
		User user = userRepository.findById(id)
							      .orElseThrow(()->new Exception("User not found"));
		
		return mapToResponse(user);
	}
	
	
	private UserResponse mapToResponse(User user) {
		return UserResponse.builder()
						   .id(user.getId())
						   .name(user.getName())
						   .email(user.getEmail())
						   .profilePic(user.getProfilepicture())
						   .build();
				
	}
}
