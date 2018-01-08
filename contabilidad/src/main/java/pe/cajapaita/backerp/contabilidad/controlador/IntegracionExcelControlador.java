package pe.cajapaita.backerp.contabilidad.controlador;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import pe.cajapaita.backerp.contabilidad.dto.DetalleDTO;
import pe.cajapaita.backerp.contabilidad.dto.MonedaDTO;
import pe.cajapaita.backerp.contabilidad.dto.ResultadoDTO;
import pe.cajapaita.backerp.contabilidad.dto.TipoAsientoDTO;
import pe.cajapaita.backerp.contabilidad.entidad.Detalle;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;
import pe.cajapaita.backerp.contabilidad.infraestructura.Mensaje;
import pe.cajapaita.backerp.contabilidad.servicio.IIntegracionExcel;
import pe.cajapaita.backerp.contabilidad.servicio.IUtilitarioServicio;

/**
 *
 * @author dev-out-02
 */
@Controller
@RequestMapping("integracionExcel")
public class IntegracionExcelControlador {

    @Autowired
    private IUtilitarioServicio utilitarioServicio;
    
    @Autowired
    private IIntegracionExcel integracionExcel;

    private final Logger logger = Logger.getLogger(this.getClass());

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView cargar() {
        ModelAndView model = new ModelAndView("/integracion/excel");
        List<MonedaDTO> listaMoneda = new ArrayList<>();
        List<TipoAsientoDTO> listaTipoAsiento = new ArrayList<>();
        try {

            listaMoneda = utilitarioServicio.traerListaMonedaActiva();
            listaTipoAsiento = utilitarioServicio.traerListaTipoAsientoContabilidad();
            model.addObject("monedas", listaMoneda);
            model.addObject("tipoAsientos", listaTipoAsiento);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return model;
    }
    @RequestMapping(value = "leerExcel", method = RequestMethod.POST)
    public ResponseEntity leerExcel(@RequestParam("fileArchivoExcel") MultipartFile archivoExcel){
        List<DetalleDTO> listaDetalle;
        ResultadoDTO resultadoDTO=new ResultadoDTO();
        
        try{
            listaDetalle=integracionExcel.generarDetalle(archivoExcel);
            resultadoDTO.setListaObjetos( new ArrayList<Object>(listaDetalle), 0);
            resultadoDTO.setTipoMensaje(Mensaje.TIPO_EXITO);
        }
        catch(ExcepcionNegocio exn){            
            resultadoDTO.setMensaje(exn.getMessage(), exn.getTipoMensaje());
        }
        catch(Exception ex){
            resultadoDTO.setMensaje(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
        
        return new ResponseEntity(resultadoDTO,HttpStatus.OK);
    }
}
