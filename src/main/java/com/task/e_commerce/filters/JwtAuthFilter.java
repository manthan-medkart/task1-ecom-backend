package com.task.e_commerce.filters;

import com.task.e_commerce.entities.UserEntity;
import com.task.e_commerce.repositories.UserRepository;
import com.task.e_commerce.services.JwtService;
import com.task.e_commerce.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserService userService;
    private JwtService jwtService;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;

    public JwtAuthFilter(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try{
            final String requestTokenHeader = request.getHeader("Authorization");

            if(requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")){
                System.out.println("invalid header");
                filterChain.doFilter(request, response);
                return;
            }

            String token = requestTokenHeader.split("Bearer ")[1];

            Long userId = jwtService.getUserIdByToken(token);

            if(userId != null || SecurityContextHolder.getContext().getAuthentication() == null){
                UserEntity user = userService.getUserById(userId);

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            }

            filterChain.doFilter(request, response);
        }
        catch (Exception e){
            System.out.println(e.getLocalizedMessage());
            handlerExceptionResolver.resolveException(request, response, null , e);
        }
    }
}
