package pe.cajapaita.backerp.contabilidad.servicio;

import java.util.List;
import pe.cajapaita.backerp.contabilidad.dto.AgenciaDTO;
import pe.cajapaita.backerp.contabilidad.dto.MonedaDTO;
import pe.cajapaita.backerp.contabilidad.dto.PeriodoDTO;
import pe.cajapaita.backerp.contabilidad.dto.ProcedenciaDTO;
import pe.cajapaita.backerp.contabilidad.dto.ReporteDTO;
import pe.cajapaita.backerp.contabilidad.dto.TipoAsientoDTO;
import pe.cajapaita.backerp.contabilidad.dto.TipoCambioDTO;
import pe.cajapaita.backerp.contabilidad.entidad.Clave;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;

/** 
 * @author hnole
 */
public interface IUtilitarioServicio {
    void cargarDatos();
    List<PeriodoDTO> traerListaPeriodo();    
    List<TipoAsientoDTO> traerListaTipoAsiento();
    List<ProcedenciaDTO> traerListaProcedencia();
    List<MonedaDTO> traerListaMoneda();
    List<MonedaDTO> traerListaMonedaActiva();
    List<TipoAsientoDTO> traerListaTipoAsientoSiafc();
    List<TipoAsientoDTO> traerListaTipoAsientoSysone();
    List<TipoAsientoDTO> traerListaTipoAsientoContabilidad();
    List<TipoAsientoDTO> traerListaTipoAsientoProvision();
    List<AgenciaDTO> traerListaAgencia();
    List<ReporteDTO> traerReportesVarios();
    List<ReporteDTO> traerListaReportesEncaje();
    List<ReporteDTO> traerListaReporteConfigurables();    
    
    int traerRegistrosPorPagina();
    String traerCuentaIngreso();
    String traerCuentaEgresos();
    int traerTipoAsientoActualizacionME(); 
    PeriodoDTO traerPeriodoVigente();       
    
    void actualizarPeriodo();
    List<TipoCambioDTO> traerHistoricoTC() throws ExcepcionNegocio;
    
    Clave traerSiguienteIdSaldo() throws ExcepcionNegocio;
    Clave traerSiguienteIdIntegracion() throws ExcepcionNegocio;
    int traerIdUsuarioIntegracion();   
}
