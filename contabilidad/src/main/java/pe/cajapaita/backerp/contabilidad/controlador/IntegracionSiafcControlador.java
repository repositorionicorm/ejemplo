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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import pe.cajapaita.backerp.contabilidad.dto.ArchivoSIAFCDTO;
import pe.cajapaita.backerp.contabilidad.dto.DetalleSIAFCDTO;
import pe.cajapaita.backerp.contabilidad.dto.ListaAsientoDTO;
import pe.cajapaita.backerp.contabilidad.dto.RespuestaIntegracionDTO;
import pe.cajapaita.backerp.contabilidad.dto.ResultadoDTO;
import pe.cajapaita.backerp.contabilidad.dto.UsuarioDTO;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;
import pe.cajapaita.backerp.contabilidad.infraestructura.Mensaje;
import pe.cajapaita.backerp.contabilidad.servicio.IIntegracionSiafcServicio;


@Controller
@RequestMapping("integracion")
public class IntegracionSiafcControlador {

    @Autowired
    private IIntegracionSiafcServicio integracionSIAFCServicio;

    private final Logger logger = Logger.getLogger(IntegracionSiafcControlador.class);

    @RequestMapping(value = "siafc", method = RequestMethod.GET)
    public ModelAndView pantallaIntegracion(Authentication authentication) {
        ModelAndView modelAndView = null;
        modelAndView = new ModelAndView("integracion/siafc");
        UsuarioDTO usuario= (UsuarioDTO) authentication.getPrincipal();  
        modelAndView.addObject("editar", usuario.getEditar());
        return modelAndView;
    }

    @RequestMapping(value = "traerArchivos", method = RequestMethod.GET)
    public ResponseEntity<ResultadoDTO> listaArchivos() {
        ResultadoDTO resultadoDTO = new ResultadoDTO();
        List<ArchivoSIAFCDTO> archivos = new ArrayList<ArchivoSIAFCDTO>();
        try {
            archivos = integracionSIAFCServicio.listaArchivosSIAFC();
            resultadoDTO.setListaObjetos(new ArrayList<Object>(archivos), 0);
            return new ResponseEntity<ResultadoDTO>(resultadoDTO, HttpStatus.OK);
        } catch (ExcepcionNegocio en) {
            resultadoDTO.setMensaje(en.getMessage(), en.getTipoMensaje());
            return new ResponseEntity<ResultadoDTO>(resultadoDTO, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error( ex.getMessage());
            resultadoDTO.setMensaje(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
            return new ResponseEntity<ResultadoDTO>(resultadoDTO, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "traerDetalle/{nombre}", method = RequestMethod.GET)
    public ResponseEntity<ResultadoDTO> detalleArchivo(@PathVariable("nombre") String nombre) {
        ResultadoDTO resultadoDTO = new ResultadoDTO();
        try {
            List<DetalleSIAFCDTO> detalle = new ArrayList<DetalleSIAFCDTO>();
            detalle = integracionSIAFCServicio.detalleArchivoSIAFC(nombre);
            resultadoDTO.setListaObjetos(new ArrayList<Object>(detalle), 0);
        } catch (ExcepcionNegocio en) {
            resultadoDTO.setMensaje(en.getMessage(), en.getTipoMensaje());
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            resultadoDTO.setMensaje(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        } finally {
            return new ResponseEntity<ResultadoDTO>(resultadoDTO, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "integrarArchivos", method = RequestMethod.POST)
    public ResponseEntity integrar(@RequestBody ListaAsientoDTO listaAsientoDTO, Authentication autenticacion) {
        try {            
            List<RespuestaIntegracionDTO> mensajesSalida = integracionSIAFCServicio.integrarArchivosSIAFC(
                    listaAsientoDTO.getListaAsientos(), 
                    ((UsuarioDTO) autenticacion.getPrincipal()).getId());
            
            return new ResponseEntity(mensajesSalida, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error( ex.getMessage());
            return new ResponseEntity(Mensaje.ERROR_GENERAL,HttpStatus.CONFLICT);
            
        }

    }
}
