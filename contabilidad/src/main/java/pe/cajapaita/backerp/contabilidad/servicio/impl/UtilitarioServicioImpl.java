package pe.cajapaita.backerp.contabilidad.servicio.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.cajapaita.backerp.contabilidad.dao.RepositorioBaseDao;
import pe.cajapaita.backerp.contabilidad.dto.MonedaDTO;
import pe.cajapaita.backerp.contabilidad.dto.PeriodoDTO;
import pe.cajapaita.backerp.contabilidad.dto.ProcedenciaDTO;
import pe.cajapaita.backerp.contabilidad.dto.TipoAsientoDTO;
import pe.cajapaita.backerp.contabilidad.entidad.Moneda;
import pe.cajapaita.backerp.contabilidad.entidad.Periodo;
import pe.cajapaita.backerp.contabilidad.entidad.Procedencia;
import pe.cajapaita.backerp.contabilidad.entidad.TipoAsiento;
import pe.cajapaita.backerp.contabilidad.consulta.Alias;
import pe.cajapaita.backerp.contabilidad.consulta.Consulta;
import pe.cajapaita.backerp.contabilidad.consulta.Equivalencia;
import pe.cajapaita.backerp.contabilidad.infraestructura.EstadoEnum;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionIgual;
import pe.cajapaita.backerp.contabilidad.dao.IRepositorioBaseDao;
import pe.cajapaita.backerp.contabilidad.dto.AgenciaDTO;
import pe.cajapaita.backerp.contabilidad.dto.ParametroDTO;
import pe.cajapaita.backerp.contabilidad.dto.ReporteDTO;
import pe.cajapaita.backerp.contabilidad.dto.TipoCambioDTO;
import pe.cajapaita.backerp.contabilidad.entidad.Agencia;
import pe.cajapaita.backerp.contabilidad.entidad.Clave;
import pe.cajapaita.backerp.contabilidad.entidad.Parametro;
import pe.cajapaita.backerp.contabilidad.entidad.Reporte;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;
import pe.cajapaita.backerp.contabilidad.infraestructura.Helper;
import pe.cajapaita.backerp.contabilidad.infraestructura.ModuloEnum;
import pe.cajapaita.backerp.contabilidad.infraestructura.ProcedenciaEnum;
import pe.cajapaita.backerp.contabilidad.infraestructura.TipoReporteEnum;
import pe.cajapaita.backerp.contabilidad.servicio.IUtilitarioServicio;

/**
 *
 * @author dev-out-02
 */
@Service
@Transactional
public class UtilitarioServicioImpl implements IUtilitarioServicio {

    @Autowired
    private RepositorioBaseDao<Periodo> periodoDAO;

    @Autowired
    private RepositorioBaseDao<TipoAsiento> tipoAsientoDAO;

    @Autowired
    private RepositorioBaseDao<Procedencia> procedenciaDAO;

    @Autowired
    private RepositorioBaseDao<Moneda> monedaDAO;

    @Autowired
    private RepositorioBaseDao<Parametro> parametroDAO;

    @Autowired
    private RepositorioBaseDao<Agencia> agenciaDAO;

    @Autowired
    private RepositorioBaseDao<Reporte> reporteDAO;

    @Autowired
    private IRepositorioBaseDao<Clave> claveDAO;

    private List<PeriodoDTO> listaPeriodo = new ArrayList<>();
    private List<TipoAsientoDTO> listaTipoAsiento = new ArrayList<>();
    private List<ProcedenciaDTO> listaProcedencia = new ArrayList<>();
    private List<MonedaDTO> listaMoneda = new ArrayList<>();
    private List<MonedaDTO> listaMonedaActiva = new ArrayList<>();
    private List<ParametroDTO> parametros = new ArrayList<>();
    private List<TipoAsientoDTO> listaTipoAsientoSIAFC = new ArrayList<>();
    private List<TipoAsientoDTO> listaTipoAsientoSysone = new ArrayList<>();
    private List<TipoAsientoDTO> listaTipoAsiientoContabilidad = new ArrayList<>();
    private List<TipoAsientoDTO> listaTipoAsiientoProvision = new ArrayList<>();
    private List<AgenciaDTO> listaAgencias = new ArrayList<>();
    private List<ReporteDTO> listaReportesEstado = new ArrayList<>();
    private List<ReporteDTO> listaReportesEncaje = new ArrayList<>();
    private List<ReporteDTO> listaReporteConfiguracion;

    private PeriodoDTO periodoVigenteDTO;

    private final Logger logger = Logger.getLogger(UtilitarioServicioImpl.class);

    @Override
    public void cargarDatos() {
        llenarPeriodo();
        llenarTipoAsiento();
        llenarProcedencia();
        llenarMoneda();
        llenarParametros();
        llenarAgencias();
        cargarPeriodoVigente();
        cargarListaReportes();
    }

    @Override
    public List<PeriodoDTO> traerListaPeriodo() {
        return listaPeriodo;
    }

    @Override
    public List<TipoAsientoDTO> traerListaTipoAsiento() {
        return listaTipoAsiento;
    }

    @Override
    public List<ProcedenciaDTO> traerListaProcedencia() {
        return listaProcedencia;
    }

    @Override
    public List<MonedaDTO> traerListaMoneda() {
        return listaMoneda;
    }

    @Override
    public List<MonedaDTO> traerListaMonedaActiva() {
        return listaMonedaActiva;
    }

    @Override
    public int traerRegistrosPorPagina() {
        return Integer.parseInt(this.traerParametro("registrosPorPagina"));
    }

    @Override
    public String traerCuentaIngreso() {
        return this.traerParametro("cuentaIngresosFinancieros");
    }

    @Override
    public String traerCuentaEgresos() {
        return this.traerParametro("cuentaEgresosFinancieros");
    }

    @Override
    public int traerTipoAsientoActualizacionME() {
        return Integer.parseInt(this.traerParametro("tipoAsientoActualizacionME"));
    }

    @Override
    public int traerIdUsuarioIntegracion() {
        return Integer.parseInt(this.traerParametro("idUsuarioIntegracionAut"));
    }

    @Override
    public PeriodoDTO traerPeriodoVigente() {
        return this.periodoVigenteDTO;
    }

    @Override
    public List<TipoAsientoDTO> traerListaTipoAsientoSiafc() {
        return this.listaTipoAsientoSIAFC;
    }

    @Override
    public void actualizarPeriodo() {
        this.cargarPeriodoVigente();
        this.llenarPeriodo();
    }

    @Override
    public List<TipoAsientoDTO> traerListaTipoAsientoSysone() {
        return listaTipoAsientoSysone;
    }

    @Override
    public List<TipoAsientoDTO> traerListaTipoAsientoContabilidad() {
        return this.listaTipoAsiientoContabilidad;
    }

    @Override
    public List<TipoAsientoDTO> traerListaTipoAsientoProvision() {
        return this.listaTipoAsiientoProvision;
    }

    @Override
    public List<AgenciaDTO> traerListaAgencia() {
        return this.listaAgencias;
    }

    @Override
    public List<ReporteDTO> traerReportesVarios() {
        return this.listaReportesEstado;
    }

    @Override
    public List<ReporteDTO> traerListaReportesEncaje() {
        return this.listaReportesEncaje;
    }

    @Override
    public List<TipoCambioDTO> traerHistoricoTC() throws ExcepcionNegocio {
        List<TipoCambioDTO> listaHistorico = new ArrayList<>();

        int cantidadRegistros = Integer.parseInt(this.traerParametro("registrosHistoricoTC"));

        Consulta consulta = new Consulta();
        consulta.setRegistrosPorPagina(cantidadRegistros);
        consulta.setPagina(1);
        consulta.agregaEquivalencia(new Equivalencia("descripcion", "periodoDescripcion"));
        consulta.agregaEquivalencia(new Equivalencia("tcFijo", "tipoCambio"));
        consulta.agregaOrdenDescentente("fechaInicial");

        listaHistorico = periodoDAO.traerTodo(Periodo.class, consulta, TipoCambioDTO.class);

        return listaHistorico;
    }

    @Override
    public Clave traerSiguienteIdSaldo() throws ExcepcionNegocio {
        return this.traerSiguenteId("CNTSALDO");
    }

    @Override
    public Clave traerSiguienteIdIntegracion() throws ExcepcionNegocio {
        return this.traerSiguenteId("CNTINTEGRACION");
    }

    @Override
    public List<ReporteDTO> traerListaReporteConfigurables() {
        listaReporteConfiguracion = new ArrayList<>();
        this.listaReporteConfiguracion.addAll(listaReportesEncaje);
        this.listaReporteConfiguracion.addAll(listaReportesEstado);
        return this.listaReporteConfiguracion;
    }

//<editor-fold defaultstate="collapsed" desc="metodos privados">
    private Clave traerSiguenteId(String entidad) throws ExcepcionNegocio {
        Consulta consultaClaveSaldo = new Consulta();
        consultaClaveSaldo.agregaRestriccionIgual(new RestriccionIgual("entidad", entidad));
        Clave claveSaldo = (Clave) claveDAO.traerUnico(Clave.class, consultaClaveSaldo);

        return claveSaldo;
    }

    private void llenarMoneda() {
        try {
            Consulta consulta = new Consulta();
            consulta.agregaAlias(new Alias("estado", "estado"));
            consulta.agregaEquivalencia(new Equivalencia("id", "id"));
            consulta.agregaEquivalencia(new Equivalencia("descripcion", "descripcion"));
            consulta.agregaEquivalencia(new Equivalencia("simbolo", "simbolo"));
            consulta.agregaEquivalencia(new Equivalencia("denominacionIso", "denominacionIso"));
            consulta.agregaEquivalencia(new Equivalencia("estado.id", "estadoId"));

            this.listaMoneda = monedaDAO.traerTodo(Moneda.class, consulta, MonedaDTO.class);
            this.listaMonedaActiva = new ArrayList<>();
            for (MonedaDTO monedaActiva : listaMoneda) {

                if (monedaActiva.getEstadoId() == EstadoEnum.ACTIVO.ordinal()) {
                    this.listaMonedaActiva.add(monedaActiva);
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    private void llenarProcedencia() {
        try {
            Consulta consulta = new Consulta();
            consulta.agregaAlias(new Alias("estado", "estado"));
            consulta.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));
            consulta.agregaEquivalencia(new Equivalencia("id", "id"));
            consulta.agregaEquivalencia(new Equivalencia("descripcion", "descripcion"));
            consulta.agregaOrdenAscendente("id");

            this.listaProcedencia = procedenciaDAO.traerTodo(Procedencia.class, consulta, ProcedenciaDTO.class);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    private void llenarPeriodo() {
        try {
            Consulta consulta = new Consulta();
            consulta.agregaOrdenDescentente("descripcion");
            consulta.agregaAlias(new Alias("estado", "estado"));
            consulta.agregaEquivalencia(new Equivalencia("id", "id"));
            consulta.agregaEquivalencia(new Equivalencia("descripcion", "descripcion"));
            consulta.agregaEquivalencia(new Equivalencia("estado.id", "estadoId"));

            this.listaPeriodo = periodoDAO.traerTodo(Periodo.class, consulta, PeriodoDTO.class);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

    }

    private void llenarAgencias() {
        try {
            Consulta consulta = new Consulta();
            consulta.agregaOrdenAscendente("id");
            consulta.agregaEquivalencia(new Equivalencia("id", "id"));
            consulta.agregaEquivalencia(new Equivalencia("descripcion", "descripcion"));
            consulta.agregaEquivalencia(new Equivalencia("abreviatura", "abreviatura"));

            this.listaAgencias = agenciaDAO.traerTodo(Agencia.class, consulta, AgenciaDTO.class);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

    }

    private void llenarTipoAsiento() {
        try {
            Consulta consulta = new Consulta();
            consulta.agregaAlias(new Alias("estado", "estado"));
            consulta.agregaAlias(new Alias("procedencia", "procedencia"));

            consulta.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));

            consulta.agregaEquivalencia(new Equivalencia("id", "id"));
            consulta.agregaEquivalencia(new Equivalencia("descripcion", "descripcion"));
            consulta.agregaEquivalencia(new Equivalencia("glosa", "glosa"));
            consulta.agregaEquivalencia(new Equivalencia("procedencia.id", "procedenciaId"));
            consulta.agregaEquivalencia(new Equivalencia("modulo", "modulo"));

            consulta.agregaOrdenAscendente("descripcion");

            this.listaTipoAsiento = tipoAsientoDAO.traerTodo(TipoAsiento.class, consulta, TipoAsientoDTO.class);

            this.listaTipoAsiientoContabilidad = this.listaTipoAsiento.stream().filter(x -> x.getProcedenciaId() == ProcedenciaEnum.CONTABILIDAD.ordinal()).collect(Collectors.toList());
            this.listaTipoAsientoSIAFC = this.listaTipoAsiento.stream().filter(x -> x.getModulo() != null && x.getProcedenciaId() == ProcedenciaEnum.SIAFC.ordinal()).collect(Collectors.toList());
            this.listaTipoAsientoSysone = this.listaTipoAsiento.stream().filter(x -> x.getProcedenciaId() == ProcedenciaEnum.SYSONE.ordinal()).collect(Collectors.toList());
            this.listaTipoAsiientoProvision = this.listaTipoAsiento.stream().filter(x -> x.getProcedenciaId() == ProcedenciaEnum.PROVISION.ordinal()).collect(Collectors.toList());

        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    private void llenarParametros() {
        try {
            int moduloContable = 1;
            Consulta consulta = new Consulta();
            consulta.agregaAlias(new Alias("modulo", "modulo"));
            consulta.agregaRestriccionIgual(new RestriccionIgual("modulo.id", moduloContable));
            consulta.agregaEquivalencia(new Equivalencia("descripcion", "descripcion"));
            consulta.agregaEquivalencia(new Equivalencia("valor", "valor"));

            this.parametros = parametroDAO.traerTodo(Parametro.class, consulta, ParametroDTO.class);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

    }

    private String traerParametro(String parametro) {
        try {
            return parametros.stream().filter(x -> x.getDescripcion()
                    .toUpperCase().trim().equals(parametro.toUpperCase().trim()))
                    .findFirst().get().getValor();
        } catch (Exception ex) {
            logger.error(ex.getMessage() + "- No se encontró parametro " + parametro);
            return "";
        }
    }

    private void cargarPeriodoVigente() {
        try {
            Consulta consulta = new Consulta();
            consulta.agregaAlias(new Alias("estado", "estado"));
            consulta.agregaAlias(new Alias("periodoAnterior", "periodoAnterior"));
            consulta.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));
            consulta.agregaEquivalencia(new Equivalencia("id", "id"));
            consulta.agregaEquivalencia(new Equivalencia("descripcion", "descripcion"));
            consulta.agregaEquivalencia(new Equivalencia("fechaInicial", "fechaInicial"));
            consulta.agregaEquivalencia(new Equivalencia("fechaFinal", "fechaFinal"));
            consulta.agregaEquivalencia(new Equivalencia("ultimoAsiento", "ultimoAsiento"));
            consulta.agregaEquivalencia(new Equivalencia("periodoAnterior.id", "periodoAnteriorId"));
            consulta.agregaEquivalencia(new Equivalencia("tcFijo", "tipoCambio"));
            consulta.agregaEquivalencia(new Equivalencia("periodoAnterior.tcFijo", "tipoCambioAnterior"));
            consulta.agregaEquivalencia(new Equivalencia("periodoAnterior.descripcion", "descripcionAnterior"));

            this.periodoVigenteDTO = (PeriodoDTO) periodoDAO.traerUnico(Periodo.class, consulta, PeriodoDTO.class);

            if (this.periodoVigenteDTO != null) {
                Consulta consultaPeriodo = new Consulta();
                consultaPeriodo.agregaAlias(new Alias("periodoAnterior", "periodoAnterior"));
                consultaPeriodo.agregaRestriccionIgual(new RestriccionIgual("periodoAnterior.id", this.periodoVigenteDTO.getId()));
                consultaPeriodo.agregaEquivalencia(new Equivalencia("fechaFinal", "fechaFinal"));
                PeriodoDTO periodo = (PeriodoDTO) periodoDAO.traerUnico(Periodo.class, consultaPeriodo, PeriodoDTO.class);
                this.periodoVigenteDTO.setFechaLimite(periodo.getFechaFinal());

            }

            this.periodoVigenteDTO.setFechaInicialString(Helper.convertirAFecha(this.periodoVigenteDTO.getFechaInicial()));
            this.periodoVigenteDTO.setFechaFinalString(Helper.convertirAFecha(this.periodoVigenteDTO.getFechaFinal()));
            this.periodoVigenteDTO.setFechaLimiteString(Helper.convertirAFecha(this.periodoVigenteDTO.getFechaLimite()));
        } catch (Exception ex) {
            logger.error(ex.getMessage() + "- Error al cargar periodo vigente");
        }
    }

    private void cargarListaReportes() {
        try {
            Consulta consulta = new Consulta();
            consulta.agregaAlias(new Alias("modulo", "modulo"));
            consulta.agregaRestriccionIgual(new RestriccionIgual("modulo.id", ModuloEnum.CONTABILIDAD.ordinal()));
            consulta.agregaEquivalencia(new Equivalencia("id", "id"));
            consulta.agregaEquivalencia(new Equivalencia("descripcion", "descripcion"));
            consulta.agregaEquivalencia(new Equivalencia("tipo", "tipo"));

            List<ReporteDTO> listaReporte = reporteDAO.traerTodo(Reporte.class, consulta, ReporteDTO.class);
            this.listaReportesEncaje = listaReporte.stream().filter(x -> x.getTipo() == TipoReporteEnum.ENCAJE.ordinal()).collect(Collectors.toList());
            this.listaReportesEstado = listaReporte.stream().filter(x -> x.getTipo() == TipoReporteEnum.VARIOS.ordinal()).collect(Collectors.toList());
        } catch (Exception ex) {
            logger.error(ex.getMessage() + "- Error al cargar listado de reportes");
        }
    }
//</editor-fold>

}
