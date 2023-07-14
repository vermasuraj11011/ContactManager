package com.contactManager.service;

import com.contactManager.payload.CustomUserDetails;
import com.contactManager.entities.User;
import com.contactManager.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepo.getUserByUserName(username);
        if(user == null) throw new UsernameNotFoundException("User not found");
        return new CustomUserDetails(user);
    }
}
