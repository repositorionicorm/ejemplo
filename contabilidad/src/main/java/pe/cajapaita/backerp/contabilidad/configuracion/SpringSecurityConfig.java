/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.configuracion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


/**
 *
 * @author hnole
 */
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private Autenticar authenticationManager;
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {        
        auth.authenticationProvider(authenticationManager);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {        
        http.csrf().disable()
            .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/recursos/**").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login").failureUrl("/logeoFallo").defaultSuccessUrl("/index")
                .permitAll()
                .and()
            .logout().logoutUrl("/logout").logoutSuccessUrl("/login")
           
                .and().exceptionHandling().accessDeniedPage("/denegado");
    }

}
