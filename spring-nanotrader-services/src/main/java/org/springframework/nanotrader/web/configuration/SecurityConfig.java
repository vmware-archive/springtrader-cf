/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.nanotrader.web.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.nanotrader.web.security.CustomAuthProvider;
import org.springframework.nanotrader.web.security.CustomLogoutHandler;
import org.springframework.nanotrader.web.security.SecurityUtil;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutHandler;

/**
 * Security configuration used to protect REST API
 *
 * @author Brian Dussault
 */

@EnableWebSecurity
@Configuration
@ComponentScan(basePackages = {"org.springframework.nanotrader.web.security"})
@Profile("cloud")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public AuthenticationEntryPoint entryPoint() {
        return new Http403ForbiddenEntryPoint();
    }

    @Bean
    SecurityUtil securityUtil() {
        return new SecurityUtil();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http
                .authorizeRequests().antMatchers("/api/login", "/api/marketSummary", "/api/accountProfile").permitAll()
                .antMatchers("/api/**").hasRole("API_USER")
                .anyRequest().authenticated().and().

                logout()
                .logoutUrl("/api/logout")
                .invalidateHttpSession(true)
                .addLogoutHandler(customLogoutHandler);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new CustomAuthProvider();
    }

    @Autowired
    public CustomLogoutHandler customLogoutHandler;

}
