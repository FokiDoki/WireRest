package com.wirerest.api.security.authentication;

import com.wirerest.api.security.authority.AdminAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;

public class AdminAuthentication implements Authentication {
    private Boolean isAuthenticated = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of(new AdminAuthority());
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.isAuthenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return "Admin";
    }
}
