package com.ujjawal.projects.chatApplication.Services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ujjawal.projects.chatApplication.Entity.UserInfo;
import com.ujjawal.projects.chatApplication.Repo.UserRepository;



@Service
public class MyUserDetailService implements UserDetailsService {
	
	@Autowired
	UserRepository userRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		UserInfo user=userRepo.findByEmail(username);
		
		return new User(user.getEmail(),user.getPassword(),new ArrayList<>());
	}

}
