package edu.uanfilms.moviereview.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uanfilms.moviereview.domain.User;
import edu.uanfilms.moviereview.web.rest.vm.LoginVM;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;


    public AuthenticationFilter(AuthenticationManager authenticationManager, TokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
       // setFilterProcessesUrl("/api/authenticate");
    }



    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            LoginVM creds = new ObjectMapper()
                .readValue(req.getInputStream(), LoginVM.class);
            var algo= authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    creds.getUsername(),
                    creds.getPassword(),
                    new ArrayList<>())
            );
            return algo;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException {
        String token = tokenProvider.createToken(auth,false);
        String body = ((User) auth.getPrincipal()).getLogin() + " " + token;
        res.getWriter().write(body);
        res.getWriter().flush();
    }
}
