package pe.cajapaita.backerp.contabilidad.controlador;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import pe.cajapaita.backerp.contabilidad.dto.GrupoDTO;
import pe.cajapaita.backerp.contabilidad.dto.ResultadoDTO;
import pe.cajapaita.backerp.contabilidad.dto.UsuarioDTO;
import pe.cajapaita.backerp.contabilidad.infraestructura.EstadoEnum;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;
import pe.cajapaita.backerp.contabilidad.infraestructura.Helper;
import pe.cajapaita.backerp.contabilidad.infraestructura.Mensaje;
import pe.cajapaita.backerp.contabilidad.servicio.IReporteServicio;
import pe.cajapaita.backerp.contabilidad.servicio.IUtilitarioServicio;

/**
 *
 * @author dev-out-02
 */
@Controller
@RequestMapping("reportes")
public class ReporteControlador {

    @Autowired
    private IReporteServicio reporteServicio;

    @Autowired
    private IUtilitarioServicio utilitarioServicio;
    
    @Value("${url.Reportes}")
    private String urlReporte;

    private final Logger logger = Logger.getLogger(ReporteControlador.class);

    @RequestMapping(value = "diario", method = RequestMethod.GET)
    public ModelAndView libroDiario(Authentication authentication) {
        ModelAndView modelAndView ;

        modelAndView = new ModelAndView("reportes/libroDiario");
        modelAndView.addObject("titulo", "Libro Diario");
        UsuarioDTO usuario= (UsuarioDTO) authentication.getPrincipal();  
        modelAndView.addObject("editar", usuario.getEditar());
        modelAndView.addObject("periodos", utilitarioServicio.traerListaPeriodo());
        modelAndView.addObject("monedas",utilitarioServicio.traerListaMoneda());
        return modelAndView;
    }

    @RequestMapping(value = "mayor", method = RequestMethod.GET)
    public ModelAndView libroMayor(Authentication authentication) {
        ModelAndView modelAndView = null;
        modelAndView = new ModelAndView("reportes/libroMayor");
        modelAndView.addObject("titulo", "Libro Mayor");
        UsuarioDTO usuario= (UsuarioDTO) authentication.getPrincipal();  
        modelAndView.addObject("editar", usuario.getEditar());
        modelAndView.addObject("periodos", utilitarioServicio.traerListaPeriodo());
        modelAndView.addObject("monedas",utilitarioServicio.traerListaMoneda());
        return modelAndView;
    }

    @RequestMapping(value = "balance", method = RequestMethod.GET)
    public ModelAndView balanceComprobacion(Authentication authentication) {
        ModelAndView modelAndView = null;
        modelAndView = new ModelAndView("reportes/balanceComprobacion");
        UsuarioDTO usuario= (UsuarioDTO) authentication.getPrincipal();  
        modelAndView.addObject("editar", usuario.getEditar());
        modelAndView.addObject("periodos", utilitarioServicio.traerListaPeriodo());
        modelAndView.addObject("monedas",utilitarioServicio.traerListaMoneda());
        return modelAndView;
    }

    @RequestMapping(value = "balanceSituacion", method = RequestMethod.GET)
    public ModelAndView balanceSituacion(Authentication authentication) {
        ModelAndView modelAndView = null;
        modelAndView = new ModelAndView("reportes/balanceSituacion");
        UsuarioDTO usuario= (UsuarioDTO) authentication.getPrincipal();  
        modelAndView.addObject("editar", usuario.getEditar());
        modelAndView.addObject("periodos", utilitarioServicio.traerListaPeriodo());
        modelAndView.addObject("monedas",utilitarioServicio.traerListaMoneda());
        return modelAndView;
    }

    @RequestMapping(value = "reporteEstadoResultados")
    public ModelAndView estadoResultados(Authentication authentication) {
        ModelAndView modelAndView = new ModelAndView("/reportes/estadoResultados");
        try {
            modelAndView.addObject("periodos", utilitarioServicio.traerListaPeriodo());
            modelAndView.addObject("reportes", utilitarioServicio.traerReportesVarios());
            UsuarioDTO usuario= (UsuarioDTO) authentication.getPrincipal();  
        modelAndView.addObject("editar", usuario.getEditar());
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return modelAndView;
    }
    
    @RequestMapping(value = "reporteEncaje")
    public ModelAndView pantallaReporteEncaje(Authentication authentication) {
        ModelAndView modelAndView = new ModelAndView("/reportes/encaje");
        try {
            modelAndView.addObject("reportes", utilitarioServicio.traerListaReportesEncaje());
            modelAndView.addObject("monedas",utilitarioServicio.traerListaMonedaActiva());
            modelAndView.addObject("fechaInicio", Helper.convertirAFecha( utilitarioServicio.traerPeriodoVigente().getFechaInicial()));
            UsuarioDTO usuario= (UsuarioDTO) authentication.getPrincipal();  
            modelAndView.addObject("editar", usuario.getEditar());
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return modelAndView;
    }
    @RequestMapping(value = "traerReporteEncaje/{reporteId}/{monedaId}")
    public void traerReporteEncaje(
            @PathVariable("reporteId") int reporteId,
            @PathVariable("monedaId") int monedaId,
            @RequestParam("fecha") String fecha,
             HttpServletResponse response,
             HttpServletRequest request
        ) throws IOException {
        try{
            reporteServicio.traerReporteEncaje(reporteId, fecha, monedaId,response);
           
        }
        catch(ExcepcionNegocio en){
            logger.error(en.getMessage());
            response.sendRedirect(request.getContextPath()+"/ocurrioError?mensaje="+en.getMessage());
        }
        catch(Exception ex){
            response.sendRedirect(request.getContextPath()+"/ocurrioError?mensaje="+Mensaje.ERROR_GENERAL);
            logger.error(ex.getMessage());
        }
    }
    @RequestMapping(value = "reporteDiario/{periodo}/{monedaId}/{tipo}", method = RequestMethod.GET)
    public void getReporteLibroDiario(
            @PathVariable("periodo") int periodo,
            @PathVariable("monedaId") int monedaId,
            @PathVariable("tipo") String tipo,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("periodo", periodo);
        parametros.put("estado", EstadoEnum.ACTIVO.ordinal());
        parametros.put("monedaId",monedaId);
        try {
            reporteServicio.generarLibroDiario(parametros, tipo, response);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

    }

    @RequestMapping(value = "reporteMayor", method = RequestMethod.GET)
    public void getReporteLibroMayor(
            @RequestParam("periodo") int periodo,
            @RequestParam("numCta") String numCta,
            @RequestParam("tipo") String tipo,
            @RequestParam("monedaId") int monedaId,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("periodo", periodo);
        parametros.put("numCuenta", numCta);
        parametros.put("estado", EstadoEnum.ACTIVO.ordinal());
        parametros.put("monedaId", monedaId);
        try {
            reporteServicio.generarLibroMayor(parametros, tipo, response);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

    }

    @RequestMapping(value = "reporteBalance/{periodo}/{moneda}/{tipo}", method = RequestMethod.GET)
    public void getReporteBalance(
            @PathVariable("periodo") int periodo,
            @PathVariable("moneda") int moneda,
            @PathVariable("tipo") String tipo,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("periodo", periodo);
        parametros.put("moneda", moneda);
        try {
            reporteServicio.generarBalanceComprobacion(parametros, tipo, response);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

    }

    @RequestMapping(value = "reporteBalanceSituacion/{periodo}/{moneda}/{reporte}/{tipo}", method = RequestMethod.GET)
    public void getReporteBalanceSituacion(
            @PathVariable("periodo") int periodo,
            @PathVariable("moneda") int moneda,
            @PathVariable("tipo") String tipo,
            @PathVariable("reporte") int reporte,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("periodoId", periodo);
        parametros.put("monedaId", moneda);
        parametros.put("reporteId", reporte);
        parametros.put("SUBREPORT_DIR", urlReporte);
        try {
            reporteServicio.generarBalanceSituacion(parametros, tipo, response);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }
    @RequestMapping(value = "traerReporteEstado/{periodoId}/{reporteId}",method = RequestMethod.POST)
    public ResponseEntity traerEstado(
            @PathVariable("periodoId") int periodoId,
            @PathVariable("reporteId")int reporteId){
        ResultadoDTO resultadoDTO=new ResultadoDTO();
        List<GrupoDTO> listaGrupos= new ArrayList<>();
        try{
            listaGrupos=reporteServicio.generarReporteEstado(periodoId, reporteId);
            resultadoDTO.setListaObjetos(new ArrayList<Object>(listaGrupos),0);
            resultadoDTO.setMensaje(Mensaje.OPERACION_CORRECTA, Mensaje.TIPO_EXITO);
        }
        catch(ExcepcionNegocio en){
            resultadoDTO.setMensaje(en.getMessage(),en.getTipoMensaje());
        }
        catch(Exception ex){
            logger.error(ex.getMessage());
            resultadoDTO.setMensaje(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
        return new ResponseEntity(resultadoDTO,HttpStatus.OK);
    }
   @RequestMapping(value = "getExcelReporteEstado/{nombreReporte}/{periodoNombre}/{reporteId}/{periodoId}", method = RequestMethod.GET)
    public void getExcel(
            @PathVariable("nombreReporte")String nombreReporte,
            @PathVariable("periodoNombre") String periodo,
            @PathVariable("reporteId") int reporteId,
            @PathVariable("periodoId") int periodoId,
            HttpServletResponse response
            ){
        String empresa="CMAC PAITA SA";
         byte[] byteExcel=null;
        
        try{
            List<GrupoDTO> listaGrupoDTO = reporteServicio.generarReporteEstado(periodoId, reporteId);
            reporteServicio.generarExcelReporteEstados(response, nombreReporte, empresa, nombreReporte, periodo, listaGrupoDTO);
        }
       catch(ExcepcionNegocio en){
            logger.error(en.getMessage());
        }
        catch(Exception ex){
            
            logger.error(ex.getMessage());
        }
    }
    @RequestMapping(value = "configuracion")
    public ModelAndView vistaConfigurarReporte(){
        ModelAndView modelAndView=new ModelAndView("reportes/configuracion");
        modelAndView.addObject("reportes", utilitarioServicio.traerListaReporteConfigurables());
        return modelAndView;
    }
    
    @RequestMapping(value="traerConfiguracionReporte/{reporteId}")
    public ResponseEntity traerConfiguracionReporte(@PathVariable("reporteId")int reporteId){
        ResultadoDTO resultadoDTO=new ResultadoDTO();
        List<GrupoDTO> listaGrupos;
        try {
            listaGrupos=reporteServicio.traerConfiguracionRepote(reporteId);
            resultadoDTO.setListaObjetos(new ArrayList<>(listaGrupos),0);
            resultadoDTO.setMensaje(Mensaje.OPERACION_CORRECTA, Mensaje.TIPO_EXITO);
        }catch(ExcepcionNegocio en){
            logger.error(en.getMessage());
            resultadoDTO.setMensaje(en.getMessage(), en.getTipoMensaje());
        }
        catch(Exception ex){
            logger.error(ex.getMessage());
            resultadoDTO.setMensaje(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
         return new ResponseEntity(resultadoDTO,HttpStatus.OK);
    }
    
    

}
