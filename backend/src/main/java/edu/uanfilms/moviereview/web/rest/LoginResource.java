package edu.uanfilms.moviereview.web.rest;

import edu.uanfilms.moviereview.domain.Authority_;
import edu.uanfilms.moviereview.domain.User;
import edu.uanfilms.moviereview.repository.UserRepository;
import edu.uanfilms.moviereview.security.jwt.TokenProvider;
import edu.uanfilms.moviereview.web.rest.dto.LoginDTO;
import edu.uanfilms.moviereview.web.rest.errors.BadRequestAlertException;
import edu.uanfilms.moviereview.web.rest.vm.LoginVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@Transactional
public class LoginResource {
    private final Logger log = LoggerFactory.getLogger(GenreResource.class);

    private static final String ENTITY_NAME = "login";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TokenProvider tokenProvider;

    private final UserRepository userRepository;

    public LoginResource(TokenProvider tokenProvider, UserRepository userRepository) {
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }


    @PostMapping("/authenticate")
    public ResponseEntity<LoginDTO> authorize(@Valid @RequestBody LoginVM loginVM) {
        if(loginVM.getUsername()== null)
            throw new BadRequestAlertException("A user must have an username", ENTITY_NAME, "nameNotFound");
        User user=userRepository.findByLogin(loginVM.getUsername());
        if(user==null) throw new BadCredentialsException("Invalid Username");
        if(!Objects.equals(user.getPassword(), loginVM.getPassword())) throw  new BadCredentialsException("Invalid Password");
        if(!user.isActivated())throw  new BadCredentialsException("Invalid User, Contact your admin to be activated");
        if(user.getAuthorities().isEmpty())throw  new BadCredentialsException("Invalid User, Contact your admin to get a role");
        Set<SimpleGrantedAuthority> authorities= user.getAuthorities().stream().map((auth)-> new SimpleGrantedAuthority(auth.getName())).collect(Collectors.toSet());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user.getLogin(),user.getPassword(),authorities);
        LoginDTO result= new LoginDTO(user,authorities);
        String jwtToken= tokenProvider.createToken(authentication,false);
        result.setJwtToken(jwtToken);
        return ResponseEntity
            .ok()
            .body(result);
    }

}
