package pe.cajapaita.backerp.contabilidad.servicio.test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import pe.cajapaita.backerp.contabilidad.configuracion.SpringHibernateConfiguracion;
import pe.cajapaita.backerp.contabilidad.configuracion.WebInitializer;
import pe.cajapaita.backerp.contabilidad.consulta.Alias;
import pe.cajapaita.backerp.contabilidad.consulta.Consulta;
import pe.cajapaita.backerp.contabilidad.consulta.Equivalencia;
import pe.cajapaita.backerp.contabilidad.consulta.Maximo;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionBetweenDate;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionIgual;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionLike;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionNoIgual;
import pe.cajapaita.backerp.contabilidad.dao.IRepositorioBaseBatchDao;
import pe.cajapaita.backerp.contabilidad.dao.IRepositorioBaseDao;
import pe.cajapaita.backerp.contabilidad.dto.AsientoRenumeracionDTO;
import pe.cajapaita.backerp.contabilidad.dto.DetalleDTO;
import pe.cajapaita.backerp.contabilidad.dto.EstadoCierreDTO;
import pe.cajapaita.backerp.contabilidad.dto.PeriodoDTO;
import pe.cajapaita.backerp.contabilidad.dto.SaldoDTO;
import pe.cajapaita.backerp.contabilidad.dto.TipoAsientoDTO;
import pe.cajapaita.backerp.contabilidad.entidad.Asiento;
import pe.cajapaita.backerp.contabilidad.entidad.Clave;
import pe.cajapaita.backerp.contabilidad.entidad.Cuenta;
import pe.cajapaita.backerp.contabilidad.entidad.Detalle;
import pe.cajapaita.backerp.contabilidad.entidad.Estado;
import pe.cajapaita.backerp.contabilidad.entidad.Integracion;
import pe.cajapaita.backerp.contabilidad.entidad.Moneda;
import pe.cajapaita.backerp.contabilidad.entidad.Periodo;
import pe.cajapaita.backerp.contabilidad.entidad.Procedencia;
import pe.cajapaita.backerp.contabilidad.entidad.Saldo;
import pe.cajapaita.backerp.contabilidad.entidad.TipoAsiento;
import pe.cajapaita.backerp.contabilidad.infraestructura.EstadoEnum;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;
import pe.cajapaita.backerp.contabilidad.infraestructura.Helper;
import pe.cajapaita.backerp.contabilidad.infraestructura.Mensaje;
import pe.cajapaita.backerp.contabilidad.infraestructura.ProcedenciaEnum;
import pe.cajapaita.backerp.contabilidad.infraestructura.ProcesosEnum;
import pe.cajapaita.backerp.contabilidad.servicio.IUtilitarioServicio;
import pe.cajapaita.backerp.contabilidad.servicio.impl.CierreServicioImpl;

/**
 *
 * @author dev-out-02
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(
        classes = {
            WebInitializer.class,
            SpringHibernateConfiguracion.class
        },
        loader = AnnotationConfigWebContextLoader.class
)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CierreServicioTest {

    @InjectMocks
    private CierreServicioImpl cierreServicio;

    @Mock
    private IRepositorioBaseDao<Periodo> periodoDAO;

    @Mock
    private IRepositorioBaseDao<Asiento> asientoDAO;

    @Mock
    private IRepositorioBaseDao<Estado> estadoDAO;

    @Mock
    private IRepositorioBaseDao<Integracion> integracionDAO;

    @Mock
    private IRepositorioBaseDao<TipoAsiento> tipoAsientoDAO;

    @Mock
    private IRepositorioBaseDao<Procedencia> procedenciaDAO;

    @Mock
    private IRepositorioBaseDao<Saldo> saldoDAO;

    @Mock
    private IUtilitarioServicio utilitarioServicio;

    @Mock
    private IRepositorioBaseDao<Clave> claveDAO;

    @Mock
    private IRepositorioBaseBatchDao repositorioBaseBatchDao;

    @Mock
    private IRepositorioBaseDao<Detalle> detalleDAO;

    @Rule
    public final ExpectedException excepcion = ExpectedException.none();

    private Consulta consulta = new Consulta();
    Consulta consultaDetalles = new Consulta();
    private Consulta traerTodoAsiento = new Consulta();
    private int cuentaOrden = 71;
    private List<DetalleDTO> listaDetalleDebe;

    @Before
    public void inicio() throws ExcepcionNegocio {
        MockitoAnnotations.initMocks(this);

        PeriodoDTO periodoDTO = new PeriodoDTO();
        periodoDTO.setId(3);
        periodoDTO.setPeriodoAnteriorId(2);

        this.consulta.agregaAlias(new Alias("estado", "estado"));
        this.consulta.agregaRestriccionBetweenDate(new RestriccionBetweenDate("fecha", Helper.convertirAFecha("01/06/2016"), Helper.convertirAFecha("30/06/2016")));
        this.consulta.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));

        this.consultaDetalles.agregaAlias(new Alias("cuenta", "cuenta"));
        this.consultaDetalles.agregaAlias(new Alias("asiento", "asiento"));
        this.consultaDetalles.agregaAlias(new Alias("asiento.estado", "estado"));
        this.consultaDetalles.agregaRestriccionBetweenDate(new RestriccionBetweenDate("asiento.fecha", Helper.convertirAFecha("01/06/2016"), Helper.convertirAFecha("30/06/2016")));
        this.consultaDetalles.agregaRestriccionLike(new RestriccionLike("cuenta.cuenta", String.valueOf(this.cuentaOrden)));
        this.consultaDetalles.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));
        this.consultaDetalles.agregaEquivalencia(new Equivalencia("debe", "debe"));
        this.consultaDetalles.agregaEquivalencia(new Equivalencia("haber", "haber"));

        Asiento asiento_1 = new Asiento();
        asiento_1.setId(1);
        asiento_1.setFecha(Helper.convertirAFecha("14/06/2016"));
        asiento_1.setNumero(101);

        Asiento asiento_2 = new Asiento();
        asiento_2.setId(2);
        asiento_2.setFecha(Helper.convertirAFecha("18/06/2016"));
        asiento_2.setNumero(102);

        this.traerTodoAsiento.agregaAlias(new Alias("procedencia", "procedencia"));
        this.traerTodoAsiento.agregaAlias(new Alias("tipoAsiento", "tipoAsiento"));
        this.traerTodoAsiento.agregaAlias(new Alias("estado", "estado"));
        this.traerTodoAsiento.agregaRestriccionBetweenDate(new RestriccionBetweenDate("fecha", Helper.convertirAFecha("01/06/2016"), Helper.convertirAFecha("30/06/2016")));
        this.traerTodoAsiento.agregaEquivalencia(new Equivalencia("id", "id"));
        this.traerTodoAsiento.agregaEquivalencia(new Equivalencia("fecha", "fecha"));
        this.traerTodoAsiento.agregaEquivalencia(new Equivalencia("numero", "numero"));
        this.traerTodoAsiento.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));
        this.traerTodoAsiento.agregaOrdenAscendente("fecha");
        this.traerTodoAsiento.agregaOrdenAscendente("numero");
        this.traerTodoAsiento.agregaOrdenAscendente("procedencia.id");
        this.traerTodoAsiento.agregaOrdenAscendente("tipoAsiento.id");

        DetalleDTO detalleDTO1 = new DetalleDTO();
        detalleDTO1.setDebe(BigDecimal.TEN);
        detalleDTO1.setHaber(BigDecimal.ZERO);

        Periodo periodo = new Periodo();
        periodo.setId(3);
        periodo.setUltimoAsiento(120);
        periodo.setFechaRenumeracion(Helper.convertirAFecha("18/06/2016"));
        periodo.setFechaBalance(Helper.convertirAFecha("22/06/2016"));
        periodo.setFechaInicial(Helper.convertirAFecha("01/06/2016"));
        periodo.setFechaFinal(Helper.convertirAFecha("30/06/2016"));
        Consulta consultaMaximo = new Consulta();
        consultaMaximo.agregaAlias(new Alias("estado", "estado"));
        consultaMaximo.agregaRestriccionBetweenDate(new RestriccionBetweenDate("fecha", periodo.getFechaInicial(), periodo.getFechaFinal()));
        consultaMaximo.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));
        consultaMaximo.ObtenerMaximoDe(new Maximo("fechaEdicion"));

        when(utilitarioServicio.traerPeriodoVigente()).thenReturn(periodoDTO);
        when(asientoDAO.traerMaximo(Asiento.class, consultaMaximo)).thenReturn(Helper.convertirAFecha("08/06/2016"));

    }

    /**
     * Cuando se necesita renumerar
     *
     * @throws ExcepcionNegocio
     */
    @Test
    public void test_validacionCierre_01() throws ExcepcionNegocio {

        Periodo periodo = new Periodo();
        periodo.setId(3);
        periodo.setUltimoAsiento(120);
        periodo.setFechaRenumeracion(Helper.convertirAFecha("18/06/2016"));

        Periodo periodo2 = new Periodo();
        periodo2.setId(2);
        periodo2.setUltimoAsiento(100);
        List<DetalleDTO> listaDetalle= new ArrayList<>();

        when(periodoDAO.traerPorId(Periodo.class, 3)).thenReturn(periodo);
        when(periodoDAO.traerPorId(Periodo.class, 2)).thenReturn(periodo2);
        when(asientoDAO.contar(Asiento.class, this.consulta)).thenReturn(0);
        when(detalleDAO.traerTodo(Detalle.class, consultaDetalles, DetalleDTO.class)).thenReturn(listaDetalle);

        //Action
        List<EstadoCierreDTO> resultado = cierreServicio.validacionCierre();

        Assert.assertEquals(false, resultado.get(0).isEstado());
        Assert.assertEquals(6, resultado.size());
        Assert.assertEquals(Mensaje.ASIENTO_NO_ENCONTRADOS_PARA_PERIODO, resultado.get(0).getRespuesta());
    }

    /**
     * Cuando se necesita renumerar
     *
     * @throws ExcepcionNegocio
     */
    @Test
    public void test_validacionCierre_02() throws ExcepcionNegocio {

        Periodo periodo = new Periodo();
        periodo.setId(3);
        periodo.setUltimoAsiento(120);
        periodo.setFechaRenumeracion(Helper.convertirAFecha("18/06/2016"));

        Periodo periodo2 = new Periodo();
        periodo2.setId(2);
        periodo2.setUltimoAsiento(100);

        List<AsientoRenumeracionDTO> asientos = new ArrayList<>();

        AsientoRenumeracionDTO asiento1 = new AsientoRenumeracionDTO();
        asiento1.setNumero(0);

        AsientoRenumeracionDTO asiento2 = new AsientoRenumeracionDTO();
        asiento1.setNumero(10);
        asientos.add(asiento1);
        asientos.add(asiento2);

        when(periodoDAO.traerPorId(Periodo.class, 3)).thenReturn(periodo);
        when(periodoDAO.traerPorId(Periodo.class, 2)).thenReturn(periodo2);
        when(asientoDAO.contar(Asiento.class, this.consulta)).thenReturn(2);
        when(asientoDAO.traerTodo(Asiento.class, this.traerTodoAsiento, AsientoRenumeracionDTO.class)).thenReturn(asientos);

        //Action
        List<EstadoCierreDTO> resultado = cierreServicio.validacionCierre();

        Assert.assertEquals(false, resultado.get(0).isEstado());
        Assert.assertEquals(6, resultado.size());
        Assert.assertEquals(Mensaje.ASIENTO_NECESITA_RENUMERACION, resultado.get(0).getRespuesta());
    }

    /**
     * Renumeracion y balance correctos pero el balance no ha sido procesado
     */
    @Test
    public void test_validacionCierre_03() throws ExcepcionNegocio {
        PeriodoDTO periodoDTO = new PeriodoDTO();
        periodoDTO.setId(3);
        periodoDTO.setPeriodoAnteriorId(2);

        Periodo periodo = new Periodo();
        periodo.setId(3);
        periodo.setUltimoAsiento(120);
        periodo.setFechaRenumeracion(Helper.convertirAFecha("18/06/2016"));
        periodo.setFechaBalance(null);

        Periodo periodo2 = new Periodo();
        periodo2.setId(2);
        periodo2.setUltimoAsiento(100);
        periodo2.setFechaRenumeracion(null);
        periodo2.setFechaBalance(null);

        when(periodoDAO.traerPorId(Periodo.class, 3)).thenReturn(periodo);
        when(periodoDAO.traerPorId(Periodo.class, 2)).thenReturn(periodo2);
        when(asientoDAO.contar(Asiento.class, this.consulta)).thenReturn(10);

        //Action
        List<EstadoCierreDTO> resultado = cierreServicio.validacionCierre();

        Assert.assertEquals(false, resultado.get(1).isEstado());
        Assert.assertEquals(6, resultado.size());
        Assert.assertEquals(Mensaje.CIERRE_BALANCE_NO_PROCESADO, resultado.get(1).getRespuesta());
    }

    /**
     * La renumeracion y el balance estan correctos
     */
    @Test
    public void test_validacionCierre_04() throws ExcepcionNegocio {
        PeriodoDTO periodoDTO = new PeriodoDTO();
        periodoDTO.setId(3);
        periodoDTO.setPeriodoAnteriorId(2);

        Periodo periodo = new Periodo();
        periodo.setId(3);
        periodo.setUltimoAsiento(120);
        periodo.setFechaRenumeracion(Helper.convertirAFecha("18/06/2016"));
        periodo.setFechaBalance(Helper.convertirAFecha("22/06/2016"));

        Periodo periodo2 = new Periodo();
        periodo2.setId(2);
        periodo2.setUltimoAsiento(100);
        periodo2.setFechaRenumeracion(null);
        periodo2.setFechaBalance(null);

        when(utilitarioServicio.traerPeriodoVigente()).thenReturn(periodoDTO);
        when(periodoDAO.traerPorId(Periodo.class, 3)).thenReturn(periodo);
        when(periodoDAO.traerPorId(Periodo.class, 2)).thenReturn(periodo2);
        when(asientoDAO.contar(Asiento.class, this.consulta)).thenReturn(20);

        //Action
        List<EstadoCierreDTO> resultado = cierreServicio.validacionCierre();

        Assert.assertEquals(true, resultado.get(0).isEstado());
        Assert.assertEquals(Mensaje.ASIENTO_ULTIMA_FECHA_RENUMERACION + "18/06/2016 12:00AM", resultado.get(0).getRespuesta());
        Assert.assertEquals(true, resultado.get(1).isEstado());
        Assert.assertEquals(6, resultado.size());
        Assert.assertEquals(Mensaje.CIERRE_BALANCE_PROCESADO + "22/06/2016 12:00AM", resultado.get(1).getRespuesta());
    }

    /**
     * metodo validacionCierre() Cuando no estan integrados los asientos Sysone
     */
    @Test
    public void test_validacionCierre_05() throws ExcepcionNegocio {

        Consulta consultaSysone = new Consulta();
        consultaSysone.agregaAlias(new Alias("estado", "estado"));
        consultaSysone.agregaAlias(new Alias("procedencia", "procedencia"));
        consultaSysone.agregaRestriccionBetweenDate(new RestriccionBetweenDate("fecha", Helper.convertirAFecha("01/06/2016"), Helper.convertirAFecha("30/06/2016")));
        consultaSysone.agregaRestriccionIgual(new RestriccionIgual("procedencia.id", ProcedenciaEnum.SYSONE.ordinal()));
        consultaSysone.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.PENDIENTE.ordinal()));

        PeriodoDTO periodoDTO = new PeriodoDTO();
        periodoDTO.setId(3);
        periodoDTO.setPeriodoAnteriorId(2);

        Periodo periodo = new Periodo();
        periodo.setId(3);
        periodo.setUltimoAsiento(120);
        periodo.setFechaRenumeracion(Helper.convertirAFecha("18/06/2016"));
        periodo.setFechaBalance(Helper.convertirAFecha("22/06/2016"));

        Periodo periodo2 = new Periodo();
        periodo2.setId(2);
        periodo2.setUltimoAsiento(100);
        periodo2.setFechaRenumeracion(null);
        periodo2.setFechaBalance(null);

        when(utilitarioServicio.traerPeriodoVigente()).thenReturn(periodoDTO);
        when(periodoDAO.traerPorId(Periodo.class, 3)).thenReturn(periodo);
        when(periodoDAO.traerPorId(Periodo.class, 2)).thenReturn(periodo2);
        when(asientoDAO.contar(Asiento.class, this.consulta)).thenReturn(20);
        when(integracionDAO.contar(Integracion.class, consultaSysone)).thenReturn(10);

        //Action
        List<EstadoCierreDTO> resultado = cierreServicio.validacionCierre();

        Assert.assertEquals(false, resultado.get(2).isEstado());
        Assert.assertEquals(Mensaje.INTEGRACION_SYSONE_INCOMPLETA, resultado.get(2).getRespuesta());
        Assert.assertEquals(6, resultado.size());
    }

    /**
     * metodo validacionCierre() Cuando no estan integrados los asientos
     * Provision
     */
    @Test
    public void test_validacionCierre_06() throws ExcepcionNegocio {

        Consulta consultaSysone = new Consulta();
        consultaSysone.agregaAlias(new Alias("estado", "estado"));
        consultaSysone.agregaAlias(new Alias("procedencia", "procedencia"));
        consultaSysone.agregaRestriccionBetweenDate(new RestriccionBetweenDate("fecha", Helper.convertirAFecha("01/06/2016"), Helper.convertirAFecha("30/06/2016")));
        consultaSysone.agregaRestriccionIgual(new RestriccionIgual("procedencia.id", ProcedenciaEnum.PROVISION.ordinal()));
        consultaSysone.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.PENDIENTE.ordinal()));

        PeriodoDTO periodoDTO = new PeriodoDTO();
        periodoDTO.setId(3);
        periodoDTO.setPeriodoAnteriorId(2);

        Periodo periodo = new Periodo();
        periodo.setId(3);
        periodo.setUltimoAsiento(120);
        periodo.setFechaRenumeracion(Helper.convertirAFecha("18/06/2016"));
        periodo.setFechaBalance(Helper.convertirAFecha("22/06/2016"));

        Periodo periodo2 = new Periodo();
        periodo2.setId(2);
        periodo2.setUltimoAsiento(100);
        periodo2.setFechaRenumeracion(null);
        periodo2.setFechaBalance(null);

        when(utilitarioServicio.traerPeriodoVigente()).thenReturn(periodoDTO);
        when(periodoDAO.traerPorId(Periodo.class, 3)).thenReturn(periodo);
        when(periodoDAO.traerPorId(Periodo.class, 2)).thenReturn(periodo2);
        when(asientoDAO.contar(Asiento.class, this.consulta)).thenReturn(20);
        when(integracionDAO.contar(Integracion.class, consultaSysone)).thenReturn(10);

        //Action
        List<EstadoCierreDTO> resultado = cierreServicio.validacionCierre();

        Assert.assertEquals(false, resultado.get(2).isEstado());
        Assert.assertEquals(Mensaje.INTEGRACION_SYSONE_INCOMPLETA, resultado.get(3).getRespuesta());
        Assert.assertEquals(6, resultado.size());
    }

    /**
     * metodo validacionCierre() Cuando TODAS las validaciones estan correctas
     */
    @Test
    public void test_validacionCierre_07() throws ExcepcionNegocio {

        Consulta consultaSysone = new Consulta();
        consultaSysone.agregaAlias(new Alias("estado", "estado"));
        consultaSysone.agregaRestriccionBetweenDate(new RestriccionBetweenDate("fecha", Helper.convertirAFecha("01/06/2016"), Helper.convertirAFecha("30/06/2016")));
        consultaSysone.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.PENDIENTE.ordinal()));

        PeriodoDTO periodoDTO = new PeriodoDTO();
        periodoDTO.setId(3);
        periodoDTO.setPeriodoAnteriorId(2);

        Periodo periodo = new Periodo();
        periodo.setId(3);
        periodo.setUltimoAsiento(120);
        periodo.setFechaRenumeracion(Helper.convertirAFecha("18/06/2016"));
        periodo.setFechaBalance(Helper.convertirAFecha("22/06/2016"));

        Periodo periodo2 = new Periodo();
        periodo2.setId(2);
        periodo2.setUltimoAsiento(100);
        periodo2.setFechaRenumeracion(null);
        periodo2.setFechaBalance(null);

        when(utilitarioServicio.traerPeriodoVigente()).thenReturn(periodoDTO);
        when(periodoDAO.traerPorId(Periodo.class, 3)).thenReturn(periodo);
        when(periodoDAO.traerPorId(Periodo.class, 2)).thenReturn(periodo2);
        when(asientoDAO.contar(Asiento.class, this.consulta)).thenReturn(20);
        when(integracionDAO.contar(Integracion.class, consultaSysone)).thenReturn(0);

        //Action
        List<EstadoCierreDTO> resultado = cierreServicio.validacionCierre();

        Assert.assertEquals(true, resultado.get(0).isEstado());
        Assert.assertEquals(Mensaje.ASIENTO_ULTIMA_FECHA_RENUMERACION + Helper.convertirAFecha(periodo.getFechaRenumeracion()) + " 12:00AM", resultado.get(0).getRespuesta());
        Assert.assertEquals(true, resultado.get(1).isEstado());
        Assert.assertEquals(6, resultado.size());
        Assert.assertEquals(Mensaje.CIERRE_BALANCE_PROCESADO + Helper.convertirAFecha(periodo.getFechaBalance()) + " 12:00AM", resultado.get(1).getRespuesta());
        Assert.assertEquals(true, resultado.get(2).isEstado());
        Assert.assertEquals(Mensaje.CIERRE_PROCESADO, resultado.get(2).getRespuesta());
        Assert.assertEquals(true, resultado.get(3).isEstado());
        Assert.assertEquals(Mensaje.CIERRE_PROCESADO, resultado.get(3).getRespuesta());
        Assert.assertEquals(6, resultado.size());
    }

    /*
    *metodo ejecutarCierre() cuando alguien esta procesando el cierre mostrar
    *"No puede procesar cierre. En estos momentos otro usuario lo está ejecutando"
     */
    @Test
    public void test_ejecutarCierre_00() throws ExcepcionNegocio {
        ProcesosEnum.CIERRE.setProcesando(true);
        excepcion.expectMessage(Mensaje.CIERRE_PROCESANDO);
        cierreServicio.ejecutarCierre();
    }

    /**
     * metodo ejecutarCierre() Cuando las validaciones no se cumplen mostrar "No
     * se puede ejecutar el cierre"
     */
    @Test
    public void test_ejecutarCierre_01() throws ExcepcionNegocio {
        Periodo periodo = new Periodo();
        periodo.setId(3);
        periodo.setUltimoAsiento(120);
        periodo.setFechaRenumeracion(null);

        Periodo periodo2 = new Periodo();
        periodo2.setId(2);
        periodo2.setUltimoAsiento(100);
        periodo2.setFechaRenumeracion(null);

        Consulta consultaIntegracion = this.consulta;
        consultaIntegracion.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.PENDIENTE.ordinal()));

        when(periodoDAO.traerPorId(Periodo.class, 3)).thenReturn(periodo);
        when(periodoDAO.traerPorId(Periodo.class, 2)).thenReturn(periodo2);
        when(integracionDAO.contar(Integracion.class, consultaIntegracion)).thenReturn(0);

        excepcion.expectMessage(Mensaje.CIERRE_NO_PUEDE_PROCESAR);

        cierreServicio.ejecutarCierre();

    }

    @Test
    public void test_ejecutarCierre_02() throws ExcepcionNegocio {
        Consulta consultaSysone = new Consulta();
        consultaSysone.agregaAlias(new Alias("estado", "estado"));
        consultaSysone.agregaRestriccionBetweenDate(new RestriccionBetweenDate("fecha", Helper.convertirAFecha("01/06/2016"), Helper.convertirAFecha("30/06/2016")));
        consultaSysone.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.PENDIENTE.ordinal()));

        PeriodoDTO periodoDTO = new PeriodoDTO();
        periodoDTO.setId(3);
        periodoDTO.setPeriodoAnteriorId(2);
        periodoDTO.setFechaInicial(Helper.convertirAFecha("01/02/2016"));
        periodoDTO.setFechaFinal(Helper.convertirAFecha("29/02/2016"));

        Periodo periodo = new Periodo();
        periodo.setId(3);
        periodo.setUltimoAsiento(120);
        periodo.setFechaRenumeracion(Helper.convertirAFecha("18/06/2016"));
        periodo.setFechaBalance(Helper.convertirAFecha("22/06/2016"));
        periodo.setFechaInicial(Helper.convertirAFecha("01/06/2016"));
        periodo.setFechaFinal(Helper.convertirAFecha("30/06/2016"));

        Periodo periodo2 = new Periodo();
        periodo2.setId(2);
        periodo2.setUltimoAsiento(100);
        periodo2.setFechaRenumeracion(null);
        periodo2.setFechaBalance(null);

        Estado estado = new Estado();
        estado.setId(1);

        Consulta consultaSaldo = new Consulta();
        consultaSaldo.agregaAlias(new Alias("periodo", "periodo"));
        consultaSaldo.agregaAlias(new Alias("cuenta", "cuenta"));
        consultaSaldo.agregaAlias(new Alias("moneda", "moneda"));

        consultaSaldo.agregaEquivalencia(new Equivalencia("cuenta.id", "cuentaId"));
        consultaSaldo.agregaEquivalencia(new Equivalencia("moneda.id", "monedaId"));
        consultaSaldo.agregaEquivalencia(new Equivalencia("saldoFinal", "saldoFinal"));
        consultaSaldo.agregaRestriccionNoIgual(new RestriccionNoIgual("saldoFinal", BigDecimal.ZERO));
        consultaSaldo.agregaRestriccionIgual(new RestriccionIgual("periodo.id", 3));

        List<SaldoDTO> listSaldos = new ArrayList<>();

        SaldoDTO saldoDTO1 = new SaldoDTO();
        saldoDTO1.setId(1);

        SaldoDTO saldoDTO2 = new SaldoDTO();
        saldoDTO2.setId(2);

        listSaldos.add(saldoDTO1);
        listSaldos.add(saldoDTO2);

        Saldo saldo1 = new Saldo();
        saldo1.setCuenta(new Cuenta());
        saldo1.setMoneda(new Moneda());
        saldo1.setSaldoFinal(java.math.BigDecimal.ONE);

        Saldo saldo2 = new Saldo();
        saldo2.setCuenta(new Cuenta());
        saldo2.setMoneda(new Moneda());
        saldo2.setSaldoFinal(java.math.BigDecimal.ONE);

        Consulta consultaTipoAsiento = new Consulta();
        consultaTipoAsiento.agregaAlias(new Alias("procedencia", "procedencia"));
        consultaTipoAsiento.agregaAlias(new Alias("estado", "estado"));
        consultaTipoAsiento.agregaEquivalencia(new Equivalencia("id", "id"));
        consultaTipoAsiento.agregaEquivalencia(new Equivalencia("procedencia.id", "procedenciaId"));
        consultaTipoAsiento.agregaRestriccionIgual(new RestriccionIgual("procedencia.id", ProcedenciaEnum.SYSONE.ordinal()));
        consultaTipoAsiento.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));

        List<TipoAsientoDTO> tipoAsientos = new ArrayList<>();

        TipoAsientoDTO tipo1 = new TipoAsientoDTO();
        tipo1.setId(1);

        TipoAsientoDTO tipo2 = new TipoAsientoDTO();
        tipo2.setId(2);

        tipoAsientos.add(tipo1);
        tipoAsientos.add(tipo2);

        Clave clave = new Clave();
        clave.setSiguienteid(94414);

        Clave claveIntegracion = new Clave();
        claveIntegracion.setSiguienteid(7);

        Consulta consultaMaximo = new Consulta();
        consultaMaximo.agregaAlias(new Alias("estado", "estado"));
        consultaMaximo.agregaRestriccionBetweenDate(new RestriccionBetweenDate("fecha", periodo.getFechaInicial(), periodo.getFechaFinal()));
        consultaMaximo.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));
        consultaMaximo.ObtenerMaximoDe(new Maximo("fechaEdicion"));

        Consulta consultaME = new Consulta();
        consultaME.agregaAlias(new Alias("estado", "estado"));
        consultaME.agregaRestriccionBetweenDate(new RestriccionBetweenDate("fecha",Helper.convertirAFecha("01/06/2016"), Helper.convertirAFecha("30/06/2016")));
        consultaME.agregaRestriccionIgual(new RestriccionIgual("tipoAsiento.id", utilitarioServicio.traerTipoAsientoActualizacionME()));
        consultaME.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));

        Consulta consultaPeriodo = new Consulta();
        consultaPeriodo.agregaAlias(new Alias("estado", "estado"));
        consultaPeriodo.agregaAlias(new Alias("periodoAnterior", "periodoAnterior"));
        consultaPeriodo.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.PENDIENTE.ordinal()));
        consultaPeriodo.agregaRestriccionIgual(new RestriccionIgual("periodoAnterior.id", 3));
        
        Periodo periodoActivar= new Periodo();
        periodoActivar.setFechaInicial(Helper.convertirAFecha("01/07/2016"));
        periodoActivar.setFechaFinal(Helper.convertirAFecha("31/07/2016"));
        
         Consulta consultaNumero = new Consulta();
        consultaNumero.agregaEquivalencia(new Equivalencia("estado", "estado"));
        consultaNumero.agregaRestriccionBetweenDate(new RestriccionBetweenDate("fecha", periodo.getFechaInicial(), periodo.getFechaFinal()));
        consultaNumero.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));
        consultaNumero.ObtenerMaximoDe(new Maximo("numero"));

        when(asientoDAO.traerMaximo(Asiento.class, consultaNumero)).thenReturn(120);
        when(periodoDAO.traerUnico(Periodo.class, consultaPeriodo)).thenReturn(periodoActivar);
        when(utilitarioServicio.traerPeriodoVigente()).thenReturn(periodoDTO);
        when(utilitarioServicio.traerSiguienteIdSaldo()).thenReturn(clave);
        when(utilitarioServicio.traerSiguienteIdIntegracion()).thenReturn(claveIntegracion);
        when(utilitarioServicio.traerListaTipoAsientoSysone()).thenReturn(tipoAsientos);
        when(utilitarioServicio.traerListaTipoAsientoProvision()).thenReturn(tipoAsientos);
        when(periodoDAO.traerPorId(Periodo.class, 3)).thenReturn(periodo);
        when(periodoDAO.traerPorId(Periodo.class, 2)).thenReturn(periodo2);
        when(asientoDAO.contar(Asiento.class, this.consulta)).thenReturn(20);
        when(integracionDAO.contar(Integracion.class, consultaSysone)).thenReturn(0);
        when(estadoDAO.traerPorId(Estado.class, 1)).thenReturn(estado);
        when(saldoDAO.traerTodo(Saldo.class, consultaSaldo, SaldoDTO.class)).thenReturn(listSaldos);
        when(saldoDAO.traerPorId(Saldo.class, 1)).thenReturn(saldo1);
        when(saldoDAO.traerPorId(Saldo.class, 2)).thenReturn(saldo2);
        when(tipoAsientoDAO.traerTodo(TipoAsiento.class, consultaTipoAsiento, TipoAsientoDTO.class)).thenReturn(tipoAsientos);
        when(tipoAsientoDAO.traerPorId(TipoAsiento.class, 1)).thenReturn(new TipoAsiento());
        when(tipoAsientoDAO.traerPorId(TipoAsiento.class, 2)).thenReturn(new TipoAsiento());
        when(procedenciaDAO.traerPorId(Procedencia.class, ProcedenciaEnum.SYSONE.ordinal())).thenReturn(new Procedencia());
        when(estadoDAO.traerPorId(Estado.class, EstadoEnum.PENDIENTE.ordinal())).thenReturn(new Estado());
        when(asientoDAO.traerMaximo(Asiento.class, consultaMaximo)).thenReturn(Helper.convertirAFecha("08/06/2016"));
        when(asientoDAO.contar(Asiento.class, consultaME)).thenReturn(1);
        cierreServicio.ejecutarCierre();
    }
}
