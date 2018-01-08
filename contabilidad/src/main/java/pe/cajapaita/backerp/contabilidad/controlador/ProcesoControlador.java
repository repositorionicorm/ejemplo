/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.controlador;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import pe.cajapaita.backerp.contabilidad.dto.AsientoDTO;
import pe.cajapaita.backerp.contabilidad.dto.EstadoCierreDTO;
import pe.cajapaita.backerp.contabilidad.dto.ResultadoDTO;
import pe.cajapaita.backerp.contabilidad.dto.TipoCambioDTO;
import pe.cajapaita.backerp.contabilidad.dto.UsuarioDTO;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;
import pe.cajapaita.backerp.contabilidad.infraestructura.Mensaje;
import pe.cajapaita.backerp.contabilidad.infraestructura.ProcedenciaEnum;
import pe.cajapaita.backerp.contabilidad.servicio.IAsientoServicio;
import pe.cajapaita.backerp.contabilidad.servicio.ICierreServicio;
import pe.cajapaita.backerp.contabilidad.servicio.IMayorizacionServicio;
import pe.cajapaita.backerp.contabilidad.servicio.IUtilitarioServicio;

/**
 *
 * @author dev-out-02
 */
@Controller
@RequestMapping(value = "proceso")
public class ProcesoControlador {

    @Autowired
    private IMayorizacionServicio mayorizacionServicio;
    
    @Autowired
    private ICierreServicio cierreServicio;
    
    @Autowired
    private IAsientoServicio asientoServicio;
    
    @Autowired
    private IUtilitarioServicio utilitarioServicio;

    private final Logger logger = Logger.getLogger(ProcesoControlador.class);

    @RequestMapping(value = "mayorizar", method = RequestMethod.GET)
    public ResponseEntity mayorizar() {
        ResultadoDTO resultadoDTO = new ResultadoDTO();
        try {
            mayorizacionServicio.mayorizar();
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
    @RequestMapping(value = "validarCierre",method = RequestMethod.POST)
    public ResponseEntity cierrePeriodo(){
        ResultadoDTO resultadoDTO= new ResultadoDTO();
        try{
            List<EstadoCierreDTO> listaEstado=cierreServicio.validacionCierre();
            resultadoDTO.setListaObjetos(new ArrayList<Object>(listaEstado), 0);
            resultadoDTO.setObjeto(!listaEstado.stream().anyMatch(p->p.isEstado()==false));
            return new ResponseEntity(resultadoDTO,HttpStatus.OK);
        }catch(ExcepcionNegocio en){
            resultadoDTO.setMensaje(en.getMessage(), en.getTipoMensaje());
            return new ResponseEntity(resultadoDTO,HttpStatus.OK);
        }
        catch(Exception ex){
            logger.error(ex.getMessage());
            resultadoDTO.setMensaje(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
            return new ResponseEntity(Mensaje.ERROR_GENERAL, HttpStatus.CONFLICT);
        }
    }
    @RequestMapping(value = "procesarCierre",method = RequestMethod.POST)
    public ResponseEntity ejecutarCierre(){
        ResultadoDTO resultadoDTO= new ResultadoDTO();
        try{
            cierreServicio.ejecutarCierre();
            resultadoDTO.setMensaje(Mensaje.OPERACION_CORRECTA, Mensaje.TIPO_EXITO);
            return new ResponseEntity(resultadoDTO,HttpStatus.OK);
        }
        catch(ExcepcionNegocio en){
           resultadoDTO.setMensaje(en.getMessage(), en.getTipoMensaje());
           return new ResponseEntity(resultadoDTO,HttpStatus.OK);
        }
        catch(Exception ex){
            logger.error(ex.getMessage());
            resultadoDTO.setMensaje(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
            return new ResponseEntity(resultadoDTO, HttpStatus.OK);
        }
    }
    @RequestMapping(value = "datosTCambio",method = RequestMethod.GET)
    public ModelAndView datosTipoCambio(Authentication authentication){
        ModelAndView modelAndView=new ModelAndView("/procesos/tipoCambio");
        modelAndView.addObject("periodo", utilitarioServicio.traerPeriodoVigente().getDescripcion());
        modelAndView.addObject("tipoCambio", utilitarioServicio.traerPeriodoVigente().getTipoCambio());
        modelAndView.addObject("periodoAnterior", utilitarioServicio.traerPeriodoVigente().getDescripcionAnterior());
        modelAndView.addObject("tipoCambioAnterior", utilitarioServicio.traerPeriodoVigente().getTipoCambioAnterior());
        UsuarioDTO usuario= (UsuarioDTO) authentication.getPrincipal();  
        modelAndView.addObject("editar", usuario.getEditar());
        return modelAndView;
    }
    @RequestMapping(value = "cambiarTC",method = RequestMethod.POST)
    public ResponseEntity cambiarTipoCambio(@RequestParam("nuevoTC") String nuevoTC){
        ResultadoDTO resultadoDTO= new ResultadoDTO();
        
        try{
            resultadoDTO.setObjeto((Object) asientoServicio.cambiarTC(new BigDecimal(nuevoTC)));
            resultadoDTO.setMensaje(Mensaje.OPERACION_CORRECTA, Mensaje.TIPO_EXITO);
            return new ResponseEntity(resultadoDTO,HttpStatus.OK);
        }
       catch(ExcepcionNegocio en){
           resultadoDTO.setMensaje(en.getMessage(), en.getTipoMensaje());
           return new ResponseEntity(resultadoDTO,HttpStatus.OK);
        }
        catch(Exception ex){
            logger.error(ex.getMessage());
            resultadoDTO.setMensaje(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
            return new ResponseEntity(resultadoDTO, HttpStatus.OK);
        }
        
    }
    @RequestMapping(value = "integrarTipoCambio", method = RequestMethod.POST)
    public ResponseEntity integrarTC(@RequestBody AsientoDTO asientoDTO,@RequestParam("nuevoTC") String nuevoTC, Authentication autenticacion){
        ResultadoDTO resultadoDTO = new ResultadoDTO();
        try {
            
            UsuarioDTO usuario=((UsuarioDTO) autenticacion.getPrincipal());
            asientoDTO.setAgenciaId(usuario.getAgenciaId());
            asientoServicio.integrarTipoCambio(new BigDecimal(nuevoTC), asientoDTO,ProcedenciaEnum.CONTABILIDAD, usuario.getId());
            resultadoDTO.setMensaje(Mensaje.OPERACION_CORRECTA, Mensaje.TIPO_EXITO);
            return new ResponseEntity(resultadoDTO, HttpStatus.OK);
        } catch (ExcepcionNegocio en) {
            resultadoDTO.setMensaje(en.getMessage(), en.getTipoMensaje());
            return new ResponseEntity(resultadoDTO, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return new ResponseEntity(Mensaje.ERROR_GENERAL, HttpStatus.CONFLICT);
        }
    }
    @RequestMapping(value = "traerHistoricoTC",method = RequestMethod.GET)
    public ResponseEntity historicoTC(){
        ResultadoDTO resultadoDTO= new ResultadoDTO();
        try{
            List<TipoCambioDTO> listaHistorico=utilitarioServicio.traerHistoricoTC();
            resultadoDTO.setMensaje(Mensaje.OPERACION_CORRECTA, Mensaje.TIPO_EXITO);
            resultadoDTO.setListaObjetos(new ArrayList<Object>(listaHistorico), 0);
        } catch (ExcepcionNegocio en) {
            resultadoDTO.setMensaje(en.getMessage(), en.getTipoMensaje());
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            resultadoDTO.setMensaje(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
        return new ResponseEntity(resultadoDTO, HttpStatus.OK);
    }
}
