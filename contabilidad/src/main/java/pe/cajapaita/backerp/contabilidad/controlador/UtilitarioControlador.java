package pe.cajapaita.backerp.contabilidad.controlador;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pe.cajapaita.backerp.contabilidad.dto.AgenciaDTO;
import pe.cajapaita.backerp.contabilidad.dto.MonedaDTO;
import pe.cajapaita.backerp.contabilidad.dto.PeriodoDTO;
import pe.cajapaita.backerp.contabilidad.dto.ProcedenciaDTO;
import pe.cajapaita.backerp.contabilidad.dto.TipoAsientoDTO;
import pe.cajapaita.backerp.contabilidad.servicio.IUtilitarioServicio;

/**
 *
 * @author dev-out-02
 */
@Controller
@RequestMapping("utilitario")
public class UtilitarioControlador {

    @Autowired
    private IUtilitarioServicio utilitarioServicio;

    private final Logger logger = Logger.getLogger(UtilitarioControlador.class);

    @RequestMapping(value = "periodo", method = RequestMethod.GET)
    public ResponseEntity<PeriodoDTO> periodo() {
        List<PeriodoDTO> listaPeriodo = new ArrayList<>();
        try {
            listaPeriodo = utilitarioServicio.traerListaPeriodo();
            return new ResponseEntity(listaPeriodo, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return new ResponseEntity(listaPeriodo, HttpStatus.CONFLICT);
        }
    }
    @RequestMapping(value = "agencias", method = RequestMethod.GET)
    public ResponseEntity<AgenciaDTO> getAgencias(){
        List<AgenciaDTO> listaAgencias= new ArrayList<>();
        try{
            listaAgencias=utilitarioServicio.traerListaAgencia();
            return new ResponseEntity(listaAgencias, HttpStatus.OK);
        }
        catch (Exception ex) {
            logger.error(ex.getMessage());
            return new ResponseEntity(listaAgencias, HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "tipoAsiento", method = RequestMethod.GET)
    public ResponseEntity<TipoAsientoDTO> tipoAsiento() {
        List<TipoAsientoDTO> listaTipoPeriodo = new ArrayList<>();
        try {
            listaTipoPeriodo = utilitarioServicio.traerListaTipoAsientoContabilidad();
            return new ResponseEntity(listaTipoPeriodo, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return new ResponseEntity(listaTipoPeriodo, HttpStatus.CONFLICT);
        }
    }    

    @RequestMapping(value = "procedencia", method = RequestMethod.GET)
    public ResponseEntity<ProcedenciaDTO> procedencia() {
        List<ProcedenciaDTO> listaProcedencia = new ArrayList<>();
        try {
            listaProcedencia = utilitarioServicio.traerListaProcedencia();
            return new ResponseEntity(listaProcedencia, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return new ResponseEntity(listaProcedencia, HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "moneda", method = RequestMethod.GET)
    public ResponseEntity<MonedaDTO> moneda() {
        List<MonedaDTO> listaMoneda = new ArrayList<>();
        try {
            listaMoneda = utilitarioServicio.traerListaMoneda();
            return new ResponseEntity(listaMoneda, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return new ResponseEntity(listaMoneda, HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "monedaActiva", method = RequestMethod.GET)
    public ResponseEntity<MonedaDTO> monedaActiva() {
        List<MonedaDTO> listaMoneda = new ArrayList<>();
        try {
            listaMoneda = utilitarioServicio.traerListaMonedaActiva();
            return new ResponseEntity(listaMoneda, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return new ResponseEntity(listaMoneda, HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "periodoVigente", method = RequestMethod.GET)
    public ResponseEntity<PeriodoDTO> periodoVigente() {
        PeriodoDTO periodoDTO = new PeriodoDTO();
        try {
            periodoDTO = utilitarioServicio.traerPeriodoVigente();
            return new ResponseEntity(periodoDTO, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return new ResponseEntity(periodoDTO, HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "modulos", method = RequestMethod.GET)
    public ResponseEntity<List<TipoAsientoDTO>> modulos() {
        List<TipoAsientoDTO> listaModulos = new ArrayList<TipoAsientoDTO>();
        try {
            listaModulos = utilitarioServicio.traerListaTipoAsientoSiafc();
            return new ResponseEntity<List<TipoAsientoDTO>>(listaModulos, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return new ResponseEntity<List<TipoAsientoDTO>>(listaModulos, HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "tipoAsientoSysone", method = RequestMethod.GET)
    public ResponseEntity<TipoAsientoDTO> tipoAsientoSysone() {
        List<TipoAsientoDTO> tiposAsientoSysone = new ArrayList<>();
        try {
            tiposAsientoSysone = utilitarioServicio.traerListaTipoAsientoSysone();
            return new ResponseEntity(tiposAsientoSysone, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return new ResponseEntity(tiposAsientoSysone, HttpStatus.CONFLICT);
        }
    }
    @RequestMapping(value = "todosTipoAsiento")
    public ResponseEntity<TipoAsientoDTO> todosTipoAsiento(){
        List<TipoAsientoDTO> tiposAsientoSysone = new ArrayList<>();
        try {
            tiposAsientoSysone = utilitarioServicio.traerListaTipoAsiento();
            return new ResponseEntity(tiposAsientoSysone, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return new ResponseEntity(tiposAsientoSysone, HttpStatus.CONFLICT);
        }
    }

}
