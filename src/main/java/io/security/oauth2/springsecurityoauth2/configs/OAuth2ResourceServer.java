package io.security.oauth2.springsecurityoauth2.configs;

import com.nimbusds.jose.jwk.OctetSequenceKey;
import io.security.oauth2.springsecurityoauth2.filter.authentication.JwtAuthenticationFilter;
import io.security.oauth2.springsecurityoauth2.signature.MacSecuritySigner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class OAuth2ResourceServer {

    @Autowired
    private MacSecuritySigner macSecuritySigner;

    @Autowired
    private OctetSequenceKey octetSequenceKey;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests((requests) -> requests.antMatchers("/").permitAll().anyRequest().authenticated());
        http.userDetailsService(getUserDetailsService());
        http.addFilterBefore(new JwtAuthenticationFilter(http,macSecuritySigner, octetSequenceKey), UsernamePasswordAuthenticationFilter.class);
        http.oauth2ResourceServer().jwt();

        return http.build();
    }

    @Bean
    public UserDetailsService getUserDetailsService() {

        UserDetails user = User.withUsername("user").password("1234").authorities("ROLE_USER").build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }
}