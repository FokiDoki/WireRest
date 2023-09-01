package com.wirerest.api.security.authentication;

import com.wirerest.api.security.authority.AdminAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;

public class NoAuthentication implements Authentication {
    Boolean isAuthenticated = false;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of(new AdminAuthority());
    }

    @Override
    public Object getCredentials() {
        return false;
    }

    @Override
    public Object getDetails() {
        return false;
    }

    @Override
    public Object getPrincipal() {
        return false;
    }

    @Override
    public boolean isAuthenticated() {
        return false;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.isAuthenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return "None";
    }
}
