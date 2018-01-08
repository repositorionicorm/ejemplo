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
import pe.cajapaita.backerp.contabilidad.dto.CuentaDTO;
import pe.cajapaita.backerp.contabilidad.dto.ResultadoDTO;
import pe.cajapaita.backerp.contabilidad.dto.UsuarioDTO;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;
import pe.cajapaita.backerp.contabilidad.infraestructura.Mensaje;
import pe.cajapaita.backerp.contabilidad.servicio.ICuentaServicio;

/**
 *
 * @author hnole
 */
@Controller
@RequestMapping("/cuentas/")
public class CuentaControlador {

    @Autowired
    private ICuentaServicio cuentaServicio;

    private final Logger logger = Logger.getLogger(CuentaControlador.class);

    @RequestMapping(value = "traerHijas/{cuentaPadreId}/{pagina}", method = RequestMethod.GET)
    public ResponseEntity<ResultadoDTO> traerHijas(
            @PathVariable("cuentaPadreId") int cuentaPadreId,
            @PathVariable("pagina") int pagina,
            Authentication authentication
            ) {
        ResultadoDTO resultadoDTO = new ResultadoDTO();
        try {
            UsuarioDTO usuario= (UsuarioDTO) authentication.getPrincipal();
            resultadoDTO.setObjeto(usuario.getEditar());
            resultadoDTO.setListaObjetos(transformaObjetos(cuentaServicio.traerHijas(cuentaPadreId, pagina)),
                    cuentaServicio.traerTotalPaginas());

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

    @RequestMapping(value = "traerPorCuenta/{cuenta}/{pagina}", method = RequestMethod.GET)
    public ResponseEntity<ResultadoDTO> traerPorCuenta(
            @PathVariable("cuenta") String cuenta,
            @PathVariable("pagina") int pagina,
            Authentication authentication) {
        ResultadoDTO resultadoDTO = new ResultadoDTO();
        try {
            UsuarioDTO usuario=(UsuarioDTO) authentication.getPrincipal();
            resultadoDTO.setObjeto(usuario.getEditar());
            resultadoDTO.setListaObjetos(transformaObjetos(cuentaServicio.traerPorCuenta(cuenta, pagina)),
                    cuentaServicio.traerTotalPaginas());

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

    @RequestMapping(value = "traerPrimerNivel", method = RequestMethod.GET)
    public ResponseEntity<ResultadoDTO> traerPrimerNivel() {
        ResultadoDTO resultadoDTO = new ResultadoDTO();
        try {
            resultadoDTO.setListaObjetos(transformaObjetos(cuentaServicio.traerPrimerNivel()),
                    cuentaServicio.traerTotalPaginas());

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

    @RequestMapping(value = "grabarCuenta", method = RequestMethod.POST)
    public ResponseEntity<ResultadoDTO> guardarCuenta(@RequestBody CuentaDTO cuentaDTO, Authentication autenticacion) {
        ResultadoDTO resultadoDTO = new ResultadoDTO();
        try {
            cuentaServicio.guardar(cuentaDTO, ((UsuarioDTO) autenticacion.getPrincipal()).getId());
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

    @RequestMapping(value = "eliminarCuenta/{cuentaId}", method = RequestMethod.DELETE)
    public ResponseEntity<ResultadoDTO> eliminarCuenta(@PathVariable int cuentaId) {
        ResultadoDTO resultadoDTO = new ResultadoDTO();
        try {
            cuentaServicio.eliminar(cuentaId);
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

    @RequestMapping(value = "actualizarCuenta", method = RequestMethod.PUT)
    public ResponseEntity<ResultadoDTO> actualizarCuenta(@RequestBody CuentaDTO cuentaDTO) {
        ResultadoDTO resultadoDTO = new ResultadoDTO();
        try {
            cuentaServicio.actualizar(cuentaDTO);
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

    @RequestMapping(value = "planContable", method = RequestMethod.GET)
    public ModelAndView planContable( Authentication authentication) {
        UsuarioDTO usuario= (UsuarioDTO) authentication.getPrincipal();
        ModelAndView modelAndView = null;
        modelAndView = new ModelAndView("/cuentas/mantenimientoCuenta");
        modelAndView.addObject("editar", usuario.getEditar());
        return modelAndView;

    }

    @RequestMapping(value = "buscarCuenta", method = RequestMethod.GET)
    public ModelAndView buscarEnPlan( Authentication authentication) {
        ModelAndView modelAndView = null;
        modelAndView = new ModelAndView("cuentas/buscarPlan");
        UsuarioDTO usuario= (UsuarioDTO) authentication.getPrincipal();
        modelAndView.addObject("editar", usuario.getEditar());
        return modelAndView;
    }

    private List<Object> transformaObjetos(List<CuentaDTO> cuentas) {
        List<Object> listaObjetos = new ArrayList<>();
        for (CuentaDTO cuenta : cuentas) {
            Object objeto = cuenta;
            listaObjetos.add(objeto);
        }
        return listaObjetos;
    }

}
