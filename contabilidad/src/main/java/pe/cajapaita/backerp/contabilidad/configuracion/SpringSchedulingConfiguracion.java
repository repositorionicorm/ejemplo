/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import pe.cajapaita.backerp.contabilidad.servicio.impl.TareasProgramadasImpl;

/**
 *
 * @author dev-out-02
 */
@Configuration
@EnableScheduling
public class SpringSchedulingConfiguracion {
    
    @Bean
    public TareasProgramadasImpl actualizarVariables(){
        return  new TareasProgramadasImpl();
    }
    
}
