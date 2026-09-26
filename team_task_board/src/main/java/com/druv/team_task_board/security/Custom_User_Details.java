package com.druv.team_task_board.security;
import com.druv.team_task_board.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class Custom_User_Details implements UserDetails {
    private final User user;

    public Custom_User_Details(User user) { 
        this.user = user;
    }

    public User getUser(){ 
        return user;
    }

    @Override 
    public Collection<? extends GrantedAuthority> getAuthorities() { 
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override 
    public String getPassword() { 
        return user.getPasswordHash();
    }

    public String getUsername() { 
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() { 
        return true; 
    }

    @Override
    public boolean isAccountNonLocked() { 
        return true; 
    }

    @Override
    public boolean isCredentialsNonExpired() { 
        return true; 
    }

    @Override
    public boolean isEnabled() { 
        return true; 
    }
}
