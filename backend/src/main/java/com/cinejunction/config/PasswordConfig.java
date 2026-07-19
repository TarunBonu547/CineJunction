package com.cinejunction.config;

import com.cinejunction.movie.mapper.MovieMapper;
import com.cinejunction.movie.mapper.MovieMapperImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public MovieMapper movieMapper() {
        return new MovieMapperImpl();
    }
}