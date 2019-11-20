package com.infoway.banking.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.infoway.banking.entities.AuthUserDetail;
import com.infoway.banking.entities.User;
import com.infoway.banking.repositories.UserDetailRepository;

@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserDetailRepository userDetailRepository;
	
	public User persist(User user) {
		return this.userDetailRepository.save(user);
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> user = userDetailRepository.findByUsername(username);
        user.orElseThrow(() -> new UsernameNotFoundException("Username or password wrong"));
        UserDetails userDetails = new AuthUserDetail(user.get());
        new AccountStatusUserDetailsChecker().check(userDetails);
        return userDetails;
	}

}
