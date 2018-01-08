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
import pe.cajapaita.backerp.contabilidad.dto.AsientoDTO;
import pe.cajapaita.backerp.contabilidad.dto.DetalleDTO;
import pe.cajapaita.backerp.contabilidad.dto.ResultadoDTO;
import pe.cajapaita.backerp.contabilidad.dto.UsuarioDTO;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;
import pe.cajapaita.backerp.contabilidad.infraestructura.Helper;
import pe.cajapaita.backerp.contabilidad.infraestructura.Mensaje;
import pe.cajapaita.backerp.contabilidad.infraestructura.ProcedenciaEnum;
import pe.cajapaita.backerp.contabilidad.servicio.IAsientoServicio;
import pe.cajapaita.backerp.contabilidad.servicio.IUtilitarioServicio;

/**
 *
 * @author dev-out-02
 */
@Controller
@RequestMapping("asientos")
public class AsientoControlador {

    @Autowired
    private IAsientoServicio asientoServicio;
    
    @Autowired
    private IUtilitarioServicio utilitarioServicio;

    private final Logger logger = Logger.getLogger(AsientoControlador.class);

    @RequestMapping(value = "traerAsientos/{periodoId}/{tipoAsientoId}/{monedaId}/{procedenciaId}/{numero}/{agenciaId}/{pagina}", method = RequestMethod.GET)
    public ResponseEntity<ResultadoDTO> traerAsientos(
            @PathVariable("periodoId") int periodoId,
            @PathVariable("tipoAsientoId") int tipoAsientoId,
            @PathVariable("monedaId") int monedaId,
            @PathVariable("procedenciaId") int procedenciaId,
            @PathVariable("numero") int numero,
            @PathVariable("agenciaId") int agenciaId,
            @PathVariable("pagina") int pagina,
            Authentication authentication) {
        ResultadoDTO resultadoDTO = new ResultadoDTO();
        try {
            UsuarioDTO usuario=(UsuarioDTO) authentication.getPrincipal();
            resultadoDTO.setObjeto(usuario.getEditar());
            resultadoDTO.setListaObjetos(
                    this.transformaObjetos(asientoServicio.traerAsientos(periodoId, tipoAsientoId, monedaId, procedenciaId, pagina,numero,agenciaId, usuario)),
                    asientoServicio.traerTotalPaginas());
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

    @RequestMapping(value = "traerDetalle/{asientoId}", method = RequestMethod.GET)
    public ResponseEntity<DetalleDTO> traerDetalle(@PathVariable("asientoId") int asientoId) {
        ResultadoDTO resultadoDTO = new ResultadoDTO();
        try {
            resultadoDTO.setListaObjetos(
                    this.transformaObjectosDetalle(asientoServicio.traerDetalle(asientoId)), asientoServicio.traerTotalPaginas());
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

    @RequestMapping(value = "eliminar/{asientoId}", method = RequestMethod.DELETE)
    public ResponseEntity<ResultadoDTO> eliminar(@PathVariable("asientoId") int asientoId, Authentication autenticacion) {
        ResultadoDTO resultadoDTO = new ResultadoDTO();
        try {
            asientoServicio.eliminar(asientoId, ((UsuarioDTO) autenticacion.getPrincipal()).getId());
            resultadoDTO.setMensaje(Mensaje.OPERACION_CORRECTA, Mensaje.TIPO_EXITO);
            return new ResponseEntity(resultadoDTO, HttpStatus.OK);
        } catch (ExcepcionNegocio en) {
            resultadoDTO.setMensaje(en.getMessage(), en.getTipoMensaje());
            return new ResponseEntity(resultadoDTO, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error(ex.getMessage() );
            resultadoDTO.setMensaje(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
            return new ResponseEntity(resultadoDTO, HttpStatus.OK);
        }

    }

    @RequestMapping(value = "guardar", method = RequestMethod.POST)
    public ResponseEntity<ResultadoDTO> guardar(@RequestBody AsientoDTO asientoDTO, Authentication autenticacion) {
        ResultadoDTO resultadoDTO = new ResultadoDTO();
        try {
            UsuarioDTO usuario=((UsuarioDTO) autenticacion.getPrincipal());
            asientoDTO.setAgenciaId(usuario.getAgenciaId());
            asientoServicio.guardar(asientoDTO, ProcedenciaEnum.CONTABILIDAD, ((UsuarioDTO) autenticacion.getPrincipal()).getId());
        } catch (ExcepcionNegocio en) {
            resultadoDTO.setMensaje(en.getMessage(), en.getTipoMensaje());
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            resultadoDTO.setMensaje(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }

        return new ResponseEntity(resultadoDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "traerAsiento/{asientoId}", method = RequestMethod.GET)
    public ResponseEntity<AsientoDTO> traerAsiento(@PathVariable("asientoId") int asientoId) {
        ResultadoDTO resultadoDTO = new ResultadoDTO();
        try {
            resultadoDTO.setObjeto(asientoServicio.traerAsiento(asientoId));
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
    
    @RequestMapping(value = "renumerar", method = RequestMethod.GET)
    public ResponseEntity renumeracion() {
        ResultadoDTO resultadoDTO = new ResultadoDTO();
        try {
            asientoServicio.renumerarAsientos();
        } catch (ExcepcionNegocio en) {
            resultadoDTO.setMensaje(en.getMessage(), en.getTipoMensaje());
        } catch (Exception ex) {
            resultadoDTO.setMensaje(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
        return new ResponseEntity(resultadoDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "fechaRenumeracion", method = RequestMethod.GET)
    public ResponseEntity modalRenumerar() {
        ResultadoDTO resultadoDTO = new ResultadoDTO();
        try {
            String fecha = asientoServicio.ultimaFechaRenumeracion();
            resultadoDTO.setMensaje(Mensaje.OPERACION_CORRECTA, Mensaje.TIPO_OCULTO);
            resultadoDTO.setObjeto(fecha);
            return new ResponseEntity(resultadoDTO, HttpStatus.OK);

        } catch (ExcepcionNegocio en) {
            resultadoDTO.setMensaje(en.getMessage(), en.getTipoMensaje());
            return new ResponseEntity(resultadoDTO, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            resultadoDTO.setMensaje(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
            return new ResponseEntity(Mensaje.ERROR_GENERAL, HttpStatus.CONFLICT);
        }

    }
    @RequestMapping(value = "fechaBalance", method = RequestMethod.GET)
    public ResponseEntity ultimaFechaBalance(){
        ResultadoDTO resultadoDTO = new ResultadoDTO();
        try {
            String fecha = asientoServicio.ultimaFechaBalance();
            resultadoDTO.setMensaje(Mensaje.OPERACION_CORRECTA, Mensaje.TIPO_OCULTO);
            resultadoDTO.setObjeto(fecha);
            return new ResponseEntity(resultadoDTO, HttpStatus.OK);

        } catch (ExcepcionNegocio en) {
            resultadoDTO.setMensaje(en.getMessage(), en.getTipoMensaje());
            return new ResponseEntity(resultadoDTO, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return new ResponseEntity(Mensaje.ERROR_GENERAL, HttpStatus.CONFLICT);
        }
    }
    
    @RequestMapping(value = "listar", method = RequestMethod.GET)
    public ModelAndView listar( Authentication authentication) {
        ModelAndView modelAndView = null;
        UsuarioDTO usuario= (UsuarioDTO) authentication.getPrincipal();       
        modelAndView = new ModelAndView("asientos/listar");
        modelAndView.addObject("editar", usuario.getEditar());
        return modelAndView;
    }

    @RequestMapping(value = "editar/{idAsiento}")
    public ModelAndView editar(@PathVariable("idAsiento") String idAsiento,Authentication authentication) {
        ModelAndView modelAndView = null;
        modelAndView = new ModelAndView("asientos/grabar");
        modelAndView.addObject("titulo", "Editar Asiento ");
        modelAndView.addObject("monedas",utilitarioServicio.traerListaMonedaActiva());
        modelAndView.addObject("idAsiento", idAsiento);
         UsuarioDTO usuario= (UsuarioDTO) authentication.getPrincipal();  
        modelAndView.addObject("editar", usuario.getEditar());
        return modelAndView;
    }

    @RequestMapping(value = "crear")
    public ModelAndView crear( Authentication authentication) {
        ModelAndView modelAndView = null;
        modelAndView = new ModelAndView("asientos/grabar");
        UsuarioDTO usuario= (UsuarioDTO) authentication.getPrincipal();  
        modelAndView.addObject("editar", usuario.getEditar());
        modelAndView.addObject("monedas",utilitarioServicio.traerListaMonedaActiva());
        modelAndView.addObject("titulo", "Crear Asiento");
        return modelAndView;
    }

    private List<Object> transformaObjetos(List<AsientoDTO> asientos) throws ExcepcionNegocio {
        List<Object> listaObjetos = new ArrayList<>();
        for (AsientoDTO asiento : asientos) {
            asiento.setFechaString(Helper.convertirAFecha(asiento.getFecha()));
            Object objeto = asiento;
            listaObjetos.add(objeto);
        }
        return listaObjetos;
    }

    private List<Object> transformaObjectosDetalle(List<DetalleDTO> listaDetalle) {
        List<Object> listaObjetos = new ArrayList<>();
        for (DetalleDTO detalle : listaDetalle) {
            Object objeto = detalle;
            listaObjetos.add(objeto);
        }
        return listaObjetos;
    }
}
