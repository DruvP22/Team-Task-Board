package com.druv.team_task_board.security;
import com.druv.team_task_board.model.User;
import com.druv.team_task_board.repository.User_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class Custom_User_Detail_Service implements UserDetailsService {
    @Autowired
    private User_Repository userRepository;

    @Override 
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException { 
            User user = userRepository.findByEmail(email)
                        .orElseThrow(() ->new UsernameNotFoundException("No user with email: " + email));
                        return new Custom_User_Details(user);

    }
}
