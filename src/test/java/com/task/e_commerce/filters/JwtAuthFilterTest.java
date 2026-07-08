package com.task.e_commerce.filters;

import com.task.e_commerce.entities.UserEntity;
import com.task.e_commerce.services.JwtService;
import com.task.e_commerce.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthFilterTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private HandlerExceptionResolver handlerExceptionResolver;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testNoAuthorizationHeader() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testInvalidHeaderFormat() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("InvalidPrefix token");

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testEmptyBearerToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer ");

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testWhitespaceOnlyBearerToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer    ");

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testValidTokenAndSuccessfulAuthentication() throws ServletException, IOException {
        String token = "valid.jwt.token";
        Long userId = 123L;
        UserEntity mockUser = mock(UserEntity.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.getUserIdByToken(token)).thenReturn(userId);
        when(userService.getUserById(userId)).thenReturn(mockUser);
        when(mockUser.getAuthorities()).thenReturn(null);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(mockUser, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @Test
    public void testJwtExceptionHandling() throws ServletException, IOException {
        String token = "invalid.jwt.token";
        io.jsonwebtoken.JwtException jwtException = new io.jsonwebtoken.MalformedJwtException("Invalid token signature");

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.getUserIdByToken(token)).thenThrow(jwtException);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(handlerExceptionResolver, times(1)).resolveException(request, response, null, jwtException);
        verify(filterChain, never()).doFilter(request, response);
    }
}
