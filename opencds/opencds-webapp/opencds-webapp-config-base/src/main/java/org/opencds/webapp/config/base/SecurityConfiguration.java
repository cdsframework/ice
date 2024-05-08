package org.opencds.webapp.config.base;

import org.opencds.config.api.xml.JAXBContextService;
import org.opencds.config.service.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.security.web.util.matcher.RegexRequestMatcher.regexMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Value("${config.security}")
    private String configSecurity;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(15);
    }

    @Bean
    public UserDetailsService userDetailsService(JAXBContextService jaxbContextService) {
        return new UserDetailsServiceImpl(jaxbContextService, configSecurity, passwordEncoder());
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider authenticationProvider) {
        ProviderManager manager = new ProviderManager(authenticationProvider);
        manager.setEraseCredentialsAfterAuthentication(false);
        return manager;
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        BasicAuthenticationEntryPoint entryPoint = new BasicAuthenticationEntryPoint();
        entryPoint.setRealmName("OpenCDS Configuration Service");
        return entryPoint;
    }

    @Bean
    public BasicAuthenticationFilter basicAuthenticationFilter(AuthenticationManager authenticationManager,
                                                               AuthenticationEntryPoint authenticationEntryPoint) {
        return new BasicAuthenticationFilter(authenticationManager, authenticationEntryPoint);
    }

    @Bean
    public Customizer<HttpBasicConfigurer<HttpSecurity>> basicConfigurerCustomizer(
            AuthenticationEntryPoint authenticationEntryPoint) {
        return httpSecurity ->
                new HttpBasicConfigurer<>()
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .realmName("OpenCDS Configuration Service");
    }

    CorsConfigurationSource corsConfigurationSource() {
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration());
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            Customizer<HttpBasicConfigurer<HttpSecurity>> basicConfigurerCustomizer,
            BasicAuthenticationFilter basicAuthenticationFilter) throws Exception {
        return http
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(regexMatcher("/config/.*"))
                        .hasRole("CONFIG_ADMIN"))
                .addFilter(basicAuthenticationFilter)
                .httpBasic(basicConfigurerCustomizer)
                .build();
    }

}
