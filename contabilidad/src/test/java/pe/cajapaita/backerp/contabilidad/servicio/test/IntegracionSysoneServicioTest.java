/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.servicio.test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
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
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionBetweenDate;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionIgual;
import pe.cajapaita.backerp.contabilidad.dao.IRepositorioBaseDao;
import pe.cajapaita.backerp.contabilidad.dto.AsientoDTO;
import pe.cajapaita.backerp.contabilidad.dto.DetalleSysoneDTO;
import pe.cajapaita.backerp.contabilidad.dto.IntegracionDTO;
import pe.cajapaita.backerp.contabilidad.dto.MonedaDTO;
import pe.cajapaita.backerp.contabilidad.dto.PeriodoDTO;
import pe.cajapaita.backerp.contabilidad.dto.TipoAsientoDTO;
import pe.cajapaita.backerp.contabilidad.dto.TransaccionDTO;
import pe.cajapaita.backerp.contabilidad.entidad.Estado;
import pe.cajapaita.backerp.contabilidad.entidad.Integracion;
import pe.cajapaita.backerp.contabilidad.entidad.Periodo;
import pe.cajapaita.backerp.contabilidad.entidad.Procedencia;
import pe.cajapaita.backerp.contabilidad.entidad.TipoAsiento;
import pe.cajapaita.backerp.contabilidad.infraestructura.EstadoEnum;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;
import pe.cajapaita.backerp.contabilidad.infraestructura.Helper;
import pe.cajapaita.backerp.contabilidad.infraestructura.Mensaje;
import pe.cajapaita.backerp.contabilidad.infraestructura.ProcedenciaEnum;
import pe.cajapaita.backerp.contabilidad.servicio.IAsientoServicio;
import pe.cajapaita.backerp.contabilidad.servicio.ICuentaServicio;
import pe.cajapaita.backerp.contabilidad.servicio.IUtilitarioServicio;
import pe.cajapaita.backerp.contabilidad.servicio.IWebServicio;
import pe.cajapaita.backerp.contabilidad.servicio.impl.IntegracionSysoneServicioImpl;

/**
 *
 * @author hnole
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
public class IntegracionSysoneServicioTest {

    @InjectMocks
    private IntegracionSysoneServicioImpl integracionSysoneServicio;

    @Mock
    private IRepositorioBaseDao<Integracion> integracionDAO;

    @Mock
    private IRepositorioBaseDao<Estado> estadoDAO;

    @Mock
    private IRepositorioBaseDao<Procedencia> procedenciaDAO;

    @Mock
    private IRepositorioBaseDao<TipoAsiento> tipoAsientoDAO;
    
    @Mock
    private IRepositorioBaseDao<Periodo> periodoDAO;

    @Mock
    private IUtilitarioServicio utilitarioServicio;

    @Mock
    private IAsientoServicio asientoServicio;

    @Mock
    private IWebServicio webServicio;

    @Mock
    private ICuentaServicio cuentaServicio;

    private int usuarioId;
    private int agenciaId;

    @Rule
    public final ExpectedException excepcion = ExpectedException.none();

    private Estado estado;
    private Consulta consulta;
    int tipoAsientoId;
    String fecha;
    Integracion integracion;
    List<MonedaDTO> monedasDTOActivas;

    @Before
    public void inicio() throws ExcepcionNegocio {
        MockitoAnnotations.initMocks(this);
        usuarioId = 1;
        agenciaId = 1;

        PeriodoDTO periodoDTO = new PeriodoDTO();
        periodoDTO.setFechaInicial(Helper.convertirAFecha("01/06/2010"));
        periodoDTO.setFechaFinal(Helper.convertirAFecha("30/06/2010"));
        periodoDTO.setFechaLimite(Helper.convertirAFecha("30/07/2010"));
        periodoDTO.setId(1);
        
        tipoAsientoId = 1;
        fecha = "27/06/2016";
        monedasDTOActivas = new ArrayList<>();
        MonedaDTO monedaSoles = new MonedaDTO();
        monedaSoles.setId(1);
        MonedaDTO monedaDolares = new MonedaDTO();
        monedaDolares.setId(2);
        monedasDTOActivas.add(monedaSoles);
        monedasDTOActivas.add(monedaDolares);

        estado = new Estado();
        estado.setId(EstadoEnum.PENDIENTE.ordinal());
        integracion = new Integracion();
        integracion.setEstado(estado);

        consulta = new Consulta();
        consulta.agregaAlias(new Alias("tipoAsiento", "tipoAsiento"));
        consulta.agregaAlias(new Alias("procedencia", "procedencia"));
        consulta.agregaAlias(new Alias("agencia", "agencia"));

        consulta.agregaRestriccionIgual(new RestriccionIgual("fecha", fecha));
        consulta.agregaRestriccionIgual(new RestriccionIgual("tipoAsiento.id", tipoAsientoId));
        consulta.agregaRestriccionIgual(new RestriccionIgual("procedencia.id", ProcedenciaEnum.SYSONE.ordinal()));
        consulta.agregaRestriccionIgual(new RestriccionIgual("agencia.id", agenciaId));

        when(utilitarioServicio.traerPeriodoVigente()).thenReturn(periodoDTO);
        when(integracionDAO.traerUnico(Integracion.class, consulta)).thenReturn(integracion);
        when(utilitarioServicio.traerListaMonedaActiva()).thenReturn(monedasDTOActivas);
    }

    /**
     * metodo: traerIntegracion(). Cuando no hay registros de integracion.
     * Mostrar mensaje "No existen integraciones"
     */
    @Test
    public void test_traerIntegracion_01() throws ExcepcionNegocio {
        // Arrange   
        consulta = new Consulta();
        consulta.agregaAlias(new Alias());
        consulta.agregaAlias(new Alias());
        consulta.agregaAlias(new Alias());
        consulta.agregaRestriccionBetweenDate(new RestriccionBetweenDate());
        consulta.agregaRestriccionIgual(new RestriccionIgual());
        consulta.agregaEquivalencia(new Equivalencia());
        consulta.agregaEquivalencia(new Equivalencia());
        consulta.agregaEquivalencia(new Equivalencia());
        consulta.agregaEquivalencia(new Equivalencia());
        consulta.agregaEquivalencia(new Equivalencia());
        consulta.agregaEquivalencia(new Equivalencia());
        consulta.agregaEquivalencia(new Equivalencia());
        consulta.agregaEquivalencia(new Equivalencia());
        consulta.agregaEquivalencia(new Equivalencia());
        
        Periodo periodo=new Periodo();
        periodo.setFechaFinal(Helper.convertirAFecha("25/09/2016"));
        periodo.setFechaIntegracion(Helper.convertirAFecha("06/09/206"));
        
        when(integracionDAO.traerTodo(Integracion.class, consulta, IntegracionDTO.class)).thenReturn(new ArrayList<>());
        when(periodoDAO.traerPorId(Periodo.class, usuarioId)).thenReturn(periodo);
        //Assert                        
        excepcion.expectMessage(Mensaje.INTEGRACION_SYSONE_NO_ENCONTRADAS);

        // Action
        integracionSysoneServicio.traerIntegraciones(tipoAsientoId,1);
    }

    /**
     * metodo: integrarAsiento(). Cuando asiento ya fue integrado. Mostrar
     * mensaje "Asiento ya fue integrado"
     */
    @Test
    public void test_integrarAsiento_01() throws ExcepcionNegocio {
        // Arrange   
        when(asientoServicio.existenAsientos(fecha, tipoAsientoId, ProcedenciaEnum.SYSONE.ordinal(),agenciaId)).thenReturn(true);

        //Assert                        
        excepcion.expectMessage(Mensaje.INTEGRACION_SYSONE_ASIENTO_YA_INTEGRADO);

        // Action
        integracionSysoneServicio.integrarAsiento(tipoAsientoId, fecha, agenciaId, usuarioId);
    }

    /**
     * metodo: integrarAsiento(). Cuando log de integracion no se encuentra
     * pendiente. Mostrar mensaje "Integracion no se encuentra pendiente"
     */
    @Test
    public void test_integrarAsiento_02() throws ExcepcionNegocio {
        // Arrange        
        estado.setId(EstadoEnum.INTEGRADO.ordinal());
        integracion.setEstado(estado);

        //Assert                        
        excepcion.expectMessage(Mensaje.INTEGRACION_SYSONE_NO_PENDIENTE);

        //Action
        integracionSysoneServicio.integrarAsiento(tipoAsientoId, fecha, agenciaId, usuarioId);
    }

    /**
     * metodo: integrarAsiento(). Cuando asiento sysone contiene monedas
     * invalidas. Mostrar mensaje "Asiento Sysone contiene moneda invalida"
     */
    @Test
    public void test_integrarAsiento_03() throws ExcepcionNegocio {
        // Arrange
        estado.setId(EstadoEnum.PENDIENTE.ordinal());

        List<DetalleSysoneDTO> asientoSysoneDTO = new ArrayList<>();
        DetalleSysoneDTO detalle1 = new DetalleSysoneDTO();
        detalle1.setMoneda("1");
        DetalleSysoneDTO detalle2 = new DetalleSysoneDTO();
        detalle2.setMoneda("3");
        asientoSysoneDTO.add(detalle1);
        asientoSysoneDTO.add(detalle2);

        when(webServicio.traerAsientoSysone(tipoAsientoId, fecha,agenciaId)).thenReturn(asientoSysoneDTO);

        //Assert                        
        excepcion.expectMessage(Mensaje.INTEGRACION_SYSONE_MONEDA_INVALIDA);

        //Action
        integracionSysoneServicio.integrarAsiento(tipoAsientoId, fecha, agenciaId, usuarioId);
    }

    /**
     * metodo: integrarAsientoo(). Cuando total de asiento no cuadra debe y
     * haber. Mostrar mensaje "Debe y haber no cuadra"
     */
    @Test
    public void test_integrarAsiento_04() throws ExcepcionNegocio {
        // Arrange
        estado.setId(EstadoEnum.PENDIENTE.ordinal());
        List<TipoAsientoDTO> tiposAsiento = new ArrayList<>();
        TipoAsientoDTO tipoAsientoDTO = new TipoAsientoDTO();
        tipoAsientoDTO.setId(tipoAsientoId);
        tiposAsiento.add(tipoAsientoDTO);

        List<DetalleSysoneDTO> asientoSysoneDTO = new ArrayList<>();
        DetalleSysoneDTO detalle1 = new DetalleSysoneDTO();
        detalle1.setMoneda("1");
        detalle1.setDebe(BigDecimal.TEN);
        detalle1.setHaber(BigDecimal.ZERO);
        detalle1.setCuenta("1011");

        DetalleSysoneDTO detalle2 = new DetalleSysoneDTO();
        detalle2.setMoneda("1");
        detalle2.setDebe(BigDecimal.ZERO);
        detalle2.setHaber(BigDecimal.ONE);
        detalle2.setCuenta("1014");

        asientoSysoneDTO.add(detalle1);
        asientoSysoneDTO.add(detalle2);

        when(utilitarioServicio.traerListaTipoAsiento()).thenReturn(tiposAsiento);
        when(cuentaServicio.traerCuentaId("1011")).thenReturn(1);
        when(cuentaServicio.traerCuentaId("1014")).thenReturn(2);
        when(webServicio.traerAsientoSysone(tipoAsientoId, fecha, agenciaId)).thenReturn(asientoSysoneDTO);

        //Assert                        
        excepcion.expectMessage(Mensaje.ASIENTO_DEBE_HABER_NO_CUADRA);

        //Action
        integracionSysoneServicio.integrarAsiento(tipoAsientoId, fecha, agenciaId, usuarioId);
    }

    /**
     * metodo: traerDetalleIntegracion(). Cuando no se encuentran detalles.
     * Mostrar mensaje "No se encontraron detalles"
     */
    @Test
    public void test_traerDetalleIntegracion_01() throws ExcepcionNegocio {
        // Arrange 
        String fecha = "01/07/2016";
        String cuenta = "150101";
        char moneda = '1';
        int tipoAsientoId = 4;

        when(webServicio.traerDetalleIntegracionSysone(fecha, cuenta, tipoAsientoId, agenciaId)).thenReturn(new ArrayList<>());

        //Assert                        
        excepcion.expectMessage(Mensaje.ASIENTO_DETALLE_NO_ENCONTRADO);

        //Action
        integracionSysoneServicio.traerDetalleIntegracion(tipoAsientoId, fecha, cuenta, moneda, agenciaId);
    }

    /**
     * metodo: traerDetalleIntegracion(). Cuando no existe datos. Mostrar
     * mensaje "Transaccion no encontrada"
     */
    @Test
    public void test_traerDetalleTransaccion_01() throws ExcepcionNegocio {
        // Arrange 
        BigDecimal transaccion = new BigDecimal("123456");
        when(webServicio.traerDetalleTransaccion(transaccion)).thenReturn(new ArrayList<>());

        // Assert
        excepcion.expectMessage(Mensaje.INTEGRACION_SYSONE_TRANSACCION_NO_ENCONTRADA);

        // Action
        integracionSysoneServicio.traerDetalleTransaccion(transaccion);
    }

    /**
     * metodo: traerDetalleIntegracion(). Cuando no existe 2 registros. Mostrar
     * 2 registros.
     */
    @Test
    public void test_traerDetalleTransaccion_02() throws ExcepcionNegocio {
        // Arrange 
        BigDecimal transaccion = new BigDecimal("123456");
        List<TransaccionDTO> transacciones = new ArrayList<>();
        TransaccionDTO transaccionDTO1 = new TransaccionDTO();
        TransaccionDTO transaccionDTO2 = new TransaccionDTO();
        transacciones.add(transaccionDTO1);
        transacciones.add(transaccionDTO2);

        when(webServicio.traerDetalleTransaccion(transaccion)).thenReturn(transacciones);

        // Action
        List<TransaccionDTO> detalles = integracionSysoneServicio.traerDetalleTransaccion(transaccion);

        // Assert               
        Assert.assertEquals(2, detalles.size());
    }

    /**
     * metodo: cambiarEstadoIntegracionSysone(). Cuando el asiento no existe
     * mostrar ""No existe la integración solicitada"
     */
    @Test
    public void test_cambiarEstadoAsientoSysone_01() throws ExcepcionNegocio {

        when(integracionDAO.traerPorId(Integracion.class, 1)).thenReturn(null);

        excepcion.expectMessage(Mensaje.INTEGRACION_SYSONE_NO_EXISTE);

        integracionSysoneServicio.cambiarEstado(1, usuarioId);
    }

    /**
     * metodo: cambiarEstadoIntegracionSysone().
     *
     */
    @Test
    public void test_cambiarEstadoAsientoSysone_02() throws ExcepcionNegocio {
        Integracion integracion = new Integracion();
        integracion.setFecha(Helper.convertirAFecha("10/05/2010"));

        PeriodoDTO periodoDTO = new PeriodoDTO();
        periodoDTO.setFechaInicial(Helper.convertirAFecha("01/06/2010"));
        periodoDTO.setFechaFinal(Helper.convertirAFecha("30/06/2010"));

        when(utilitarioServicio.traerPeriodoVigente()).thenReturn(periodoDTO);
        when(integracionDAO.traerPorId(Integracion.class, 1)).thenReturn(integracion);

        excepcion.expectMessage(Mensaje.INTEGRACION_SYSONE_OTRO_PERIODO);

        integracionSysoneServicio.cambiarEstado(1, usuarioId);
    }

    /**
     * metodo: cambiarEstadoIntegracionSysone(). Cuando el asiento tiene estado
     * no valido mostrar "El estado del asiento no esta permitido para esta
     * operación"
     */
    @Test
    public void test_cambiarEstadoAsientoSysone_03() throws ExcepcionNegocio {
        estado = new Estado();
        estado.setId(EstadoEnum.ACTIVO.ordinal());
        integracion = new Integracion();
        integracion.setEstado(estado);
        integracion.setFecha(Helper.convertirAFecha("12/06/2010"));

        PeriodoDTO periodoDTO = new PeriodoDTO();
        periodoDTO.setFechaInicial(Helper.convertirAFecha("01/06/2010"));
        periodoDTO.setFechaFinal(Helper.convertirAFecha("30/06/2010"));
        periodoDTO.setFechaLimite(Helper.convertirAFecha("30/07/2010"));

        when(utilitarioServicio.traerPeriodoVigente()).thenReturn(periodoDTO);
        when(integracionDAO.traerPorId(Integracion.class, 1)).thenReturn(integracion);
        when(estadoDAO.traerPorId(Estado.class, 3)).thenReturn(estado);

        excepcion.expectMessage(Mensaje.INTEGRACION_SYSONE_ESTADO_NO_PERMITIDO);
        integracionSysoneServicio.cambiarEstado(1, usuarioId);
    }
    /**
     * metodo: traerNumeroVaucher(). Cuando la integracion no exista mostrar
     * "No existe la integración solicitada"
     * @throws ExcepcionNegocio 
     */
    @Test
    public void test_traerNumeroVaucher_01() throws ExcepcionNegocio{
        
        Consulta consulta=new Consulta();
        consulta.agregaRestriccionIgual(new RestriccionIgual("id", 1));
        consulta.agregaRestriccionIgual(new RestriccionIgual("estadoId",EstadoEnum.INTEGRADO.ordinal() ));
        consulta.agregaEquivalencia(new Equivalencia("asientoId","asientoId"));
        
        when(integracionDAO.traerUnico(Integracion.class, consulta, IntegracionDTO.class)).thenReturn(null);
        
        excepcion.expectMessage(Mensaje.INTEGRACION_SYSONE_NO_EXISTE);
        integracionSysoneServicio.traerNumeroVoucher(1);
    }
    
    @Test
    public void test_traerNumeroVaucher_02() throws ExcepcionNegocio{
        IntegracionDTO integracionDTO= new IntegracionDTO();
        integracionDTO.setAsientoId("11042,45260");
        
        List<Integer> asientosId= new ArrayList<>();
        asientosId.add(new Integer("11042"));
        asientosId.add(new Integer("45260"));
        
         AsientoDTO asiento1=new AsientoDTO();
        asiento1.setNumero(11042);
        
         AsientoDTO asiento2=new AsientoDTO();
        asiento2.setNumero(45260);
        
         List<Integer> asientos= new ArrayList<>();
        asientos.add(new Integer("11042"));
        asientos.add(new Integer("45260"));
        
        Consulta consulta=new Consulta();
        consulta.agregaAlias(new Alias("estado", "estado"));
        consulta.agregaRestriccionIgual(new RestriccionIgual("id", 1));
        consulta.agregaRestriccionIgual(new RestriccionIgual("estado.id",EstadoEnum.INTEGRADO.ordinal() ));
        consulta.agregaEquivalencia(new Equivalencia("asientoId","asientoId"));
        
        Consulta consulta2 = new Consulta();
        consulta2.agregaEquivalencia(new Equivalencia("numero", "numero"));
        consulta2.agregaRestriccionSql("({alias}.id=11042 or {alias}.id=45260)");
        
        when(integracionDAO.traerUnico(Integracion.class, consulta, IntegracionDTO.class)).thenReturn(integracionDTO);
        when(asientoServicio.traerNumeroAsientos(asientosId)).thenReturn(asientos);
                
        integracionSysoneServicio.traerNumeroVoucher(1);
    }

    
}
