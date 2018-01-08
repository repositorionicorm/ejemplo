/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.controlador;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import pe.cajapaita.backerp.contabilidad.dto.DetalleSysoneDTO;
import pe.cajapaita.backerp.contabilidad.dto.IntegracionDTO;
import pe.cajapaita.backerp.contabilidad.dto.ResultadoDTO;
import pe.cajapaita.backerp.contabilidad.dto.UsuarioDTO;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;
import pe.cajapaita.backerp.contabilidad.infraestructura.Helper;
import pe.cajapaita.backerp.contabilidad.infraestructura.Mensaje;
import pe.cajapaita.backerp.contabilidad.servicio.IIntegracionProvisionServicio;

/**
 *
 * @author hnole
 */
@Controller
@RequestMapping("integracionProvision")
public class IntegracionProvisionControlador {

    @Autowired
    private IIntegracionProvisionServicio integracionProvisionServicio;
    private final Logger logger = Logger.getLogger(IntegracionProvisionControlador.class);

    @RequestMapping(value = "inicio", method = RequestMethod.GET)
    public ModelAndView pantallaIntegracionProvision(Authentication authentication) {
        ModelAndView modelAndView = null;
        modelAndView = new ModelAndView("integracion/provision");
         UsuarioDTO usuario= (UsuarioDTO) authentication.getPrincipal();  
        modelAndView.addObject("editar", usuario.getEditar());
        return modelAndView;
    }

    @RequestMapping(value = "traerIntegracion", method = RequestMethod.GET)
    public ResponseEntity<ResultadoDTO> traerIntegracion() {
        ResultadoDTO resultadoDTO = new ResultadoDTO();
        try {
            resultadoDTO.setListaObjetos(
                    this.transformaObjetos(integracionProvisionServicio.traerIntegraciones()),0);
            return new ResponseEntity(resultadoDTO, HttpStatus.OK);
        } catch (ExcepcionNegocio en) {
            resultadoDTO.setMensaje(en.getMessage(), en.getTipoMensaje());
            return new ResponseEntity(resultadoDTO, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            resultadoDTO.setMensaje(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
            return new ResponseEntity(resultadoDTO, HttpStatus.OK);
        }
    }
    
     @RequestMapping(value = "traerAsiento", method = RequestMethod.GET)
    public ResponseEntity<ResultadoDTO> traerAsiento(
            @RequestParam("tipoAsientoId") int tipoAsientoId,
            @RequestParam("fecha") String fecha) {
        ResultadoDTO resultadoDTO = new ResultadoDTO();
        try {
            List<DetalleSysoneDTO> asientoSysone = integracionProvisionServicio.traerAsiento(tipoAsientoId, fecha);
            resultadoDTO.setListaObjetos(new ArrayList<Object>(asientoSysone), 0);
            return new ResponseEntity<ResultadoDTO>(resultadoDTO, HttpStatus.OK);
        } catch (ExcepcionNegocio en) {
            resultadoDTO.setMensaje(en.getMessage(), en.getTipoMensaje());
            return new ResponseEntity(resultadoDTO, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            resultadoDTO.setMensaje(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
            return new ResponseEntity(resultadoDTO, HttpStatus.OK);
        }
    }
    
    @RequestMapping(value = "integrarAsiento", method = RequestMethod.GET)
    public ResponseEntity<ResultadoDTO> integrarAsiento(
            @RequestParam("tipoAsientoId") int tipoAsientoId,
            @RequestParam("fecha") String fecha, 
            @RequestParam("agenciaId") int agenciaId,
            Authentication autenticacion) {
        ResultadoDTO resultadoDTO = new ResultadoDTO();
        try {
            integracionProvisionServicio.integrarAsiento(tipoAsientoId, fecha, agenciaId , ((UsuarioDTO) autenticacion.getPrincipal()).getId());
            return new ResponseEntity<ResultadoDTO>(resultadoDTO, HttpStatus.OK);
        } catch (ExcepcionNegocio en) {
            resultadoDTO.setMensaje(en.getMessage(), en.getTipoMensaje());
            return new ResponseEntity(resultadoDTO, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            resultadoDTO.setMensaje(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
            return new ResponseEntity(resultadoDTO, HttpStatus.OK);
        }
    }
    
    @RequestMapping(value = "traerNumerosAsientos/{integracionId}",method = RequestMethod.GET)
    public ResponseEntity traerNumerosAsientos(@PathVariable("integracionId") int integracionId){
        ResultadoDTO resultadoDTO = new ResultadoDTO();
        try {
            List<Integer> listaNumeros=integracionProvisionServicio.traerNumeroVoucher(integracionId);
            resultadoDTO.setObjeto(listaNumeros);
            resultadoDTO.setMensaje(Mensaje.OPERACION_CORRECTA, Mensaje.TIPO_EXITO);
            return new ResponseEntity(resultadoDTO, HttpStatus.OK);
        } catch (ExcepcionNegocio en) {
            resultadoDTO.setMensaje(en.getMessage(), en.getTipoMensaje());
            return new ResponseEntity(resultadoDTO, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            resultadoDTO.setMensaje(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
            return new ResponseEntity(resultadoDTO, HttpStatus.OK);
        }
    }
    
    @RequestMapping(value = "cambiarEstadoAsiento/{id}", method = RequestMethod.POST)
    public ResponseEntity cambiarEstadoAsiento(@PathVariable("id") int id, Authentication autenticacion) {
        ResultadoDTO resultadoDTO = new ResultadoDTO();
        try {
            integracionProvisionServicio.cambiarEstado(id, ((UsuarioDTO) autenticacion.getPrincipal()).getId());
            resultadoDTO.setMensaje(Mensaje.OPERACION_CORRECTA, Mensaje.TIPO_EXITO);
            return new ResponseEntity(resultadoDTO, HttpStatus.OK);
        } catch (ExcepcionNegocio en) {
            resultadoDTO.setMensaje(en.getMessage(), en.getTipoMensaje());
            return new ResponseEntity(resultadoDTO, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            resultadoDTO.setMensaje(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
            return new ResponseEntity(resultadoDTO, HttpStatus.OK);
        }

    }
    
    //<editor-fold defaultstate="collapsed" desc="metodos privados"> 
    private List<Object> transformaObjetos(List<IntegracionDTO> integraciones) throws ExcepcionNegocio {
        List<Object> listaObjetos = new ArrayList<>();
        for (IntegracionDTO integracionDTO : integraciones) {
            integracionDTO.setFechaString(Helper.convertirAFecha(integracionDTO.getFecha()));
            Object objeto = integracionDTO;
            listaObjetos.add(objeto);
        }
        return listaObjetos;
    }

    //</editor-fold>
}
