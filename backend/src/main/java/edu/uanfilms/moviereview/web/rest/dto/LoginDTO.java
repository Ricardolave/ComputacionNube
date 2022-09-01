package edu.uanfilms.moviereview.web.rest.dto;

import edu.uanfilms.moviereview.domain.Authority;
import edu.uanfilms.moviereview.domain.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Set;

public class LoginDTO {
    private String login;
    private String firstName;
    private String lastName;
    private String email;
    private Set<SimpleGrantedAuthority> authorities = new HashSet<>();

    private String jwtToken;

    public LoginDTO(User user,Set<SimpleGrantedAuthority>authorities){
        this.login=user.getLogin();
        this.firstName= user.getFirstName();
        this.lastName= user.getLastName();
        this.email=user.getEmail();
        this.authorities=authorities;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<SimpleGrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<SimpleGrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}
