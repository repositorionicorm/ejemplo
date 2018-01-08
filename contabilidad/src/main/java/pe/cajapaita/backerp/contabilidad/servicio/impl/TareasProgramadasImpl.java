/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.servicio.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import pe.cajapaita.backerp.contabilidad.servicio.IIntegracionSysoneServicio;
import pe.cajapaita.backerp.contabilidad.servicio.IUtilitarioServicio;

/**
 *
 * @author dev-out-02
 */
public class TareasProgramadasImpl {
   @Autowired
   private IUtilitarioServicio utilitarioServicio;
   
   @Autowired
   private IIntegracionSysoneServicio integracionSysoneServicio;
   
   @Transactional
   @Scheduled(cron = "${cron.job.actualizaVariables}")
   public void actualizarVariables(){
       utilitarioServicio.cargarDatos();
   }
   @Transactional
   @Scheduled(cron = "${cron.job.ejecutaIntegracion}")
   public void integracionSysone(){
       integracionSysoneServicio.ejecutarIntegracionAutomatica();
   }
   
}
