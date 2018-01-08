/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 *
 * @author NIKO
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "pe.cajapaita.backerp.contabilidad")
@PropertySource(value = {
    "file:/opt/aplicaciones/contabilidad/sistema.properties",
    "file:/opt/aplicaciones/contabilidad/quartz.properties"   
})
@Import({DispatcherConfig.class})
public class AppContabilidadConfiguration {   
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    @Bean
    public CommonsMultipartResolver multipartResolver(){
        CommonsMultipartResolver  multipartResolver= new CommonsMultipartResolver();
        multipartResolver.setMaxInMemorySize(10240000);
        return multipartResolver;
    }
}
