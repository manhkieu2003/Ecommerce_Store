package com.example.shopping_cart.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

@Service
public class AuthSucessHaleImpl implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        Set<String> roles = AuthorityUtils.authorityListToSet(authorities);

        if(roles.contains("ROLE_ADMIN"))  // kieem tra quyền role_admin
        {
            response.sendRedirect("/admin"); // chuyển đến trang admin
        }else {
            response.sendRedirect("/");// chuyển đến trang chủ
        }
    }
}