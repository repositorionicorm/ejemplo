/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.configuracion;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

@Configuration
public class WebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer{

    @Override
    protected Class<?>[] getRootConfigClasses(){        
        return new Class[]{AppContabilidadConfiguration.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses(){
        return null;        
    }

    @Override
    protected String[] getServletMappings() { 
        return new String[]{"/"};      
    }
}
