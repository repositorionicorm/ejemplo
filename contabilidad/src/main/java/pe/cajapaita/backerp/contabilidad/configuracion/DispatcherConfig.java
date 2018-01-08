 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.configuracion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import pe.cajapaita.backerp.contabilidad.servicio.IUtilitarioServicio;

/**
 *
 * @author NIKO
 */
@Configuration
@Import({ThymeleafConfig.class, SpringHibernateConfiguracion.class})
public class DispatcherConfig extends WebMvcConfigurerAdapter {
        
    @Autowired
    private IUtilitarioServicio utilitarioServicio;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/recursos/js/**").addResourceLocations("/recursos/js/");
        registry.addResourceHandler("/recursos/css/**").addResourceLocations("/recursos/css/");
        registry.addResourceHandler("/recursos/html/**").addResourceLocations("/recursos/html/");
        registry.addResourceHandler("/recursos/images/**").addResourceLocations("/recursos/images/"); 
               
        utilitarioServicio.cargarDatos();
    }

}
