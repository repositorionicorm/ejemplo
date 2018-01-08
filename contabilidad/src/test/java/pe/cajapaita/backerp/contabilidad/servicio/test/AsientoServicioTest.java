/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.servicio.test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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
import pe.cajapaita.backerp.contabilidad.dao.IRepositorioBaseDao;
import pe.cajapaita.backerp.contabilidad.dto.AsientoDTO;
import pe.cajapaita.backerp.contabilidad.entidad.Asiento;
import pe.cajapaita.backerp.contabilidad.entidad.Moneda;
import pe.cajapaita.backerp.contabilidad.entidad.Periodo;
import pe.cajapaita.backerp.contabilidad.entidad.TipoAsiento;
import pe.cajapaita.backerp.contabilidad.consulta.Alias;
import pe.cajapaita.backerp.contabilidad.consulta.Equivalencia;
import pe.cajapaita.backerp.contabilidad.infraestructura.EstadoEnum;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;
import pe.cajapaita.backerp.contabilidad.infraestructura.Mensaje;
import pe.cajapaita.backerp.contabilidad.consulta.Consulta;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionBetweenDate;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionIgual;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionNoIgual;
import pe.cajapaita.backerp.contabilidad.dao.IRepositorioBaseBatchDao;
import pe.cajapaita.backerp.contabilidad.dto.AgenciaDTO;
import pe.cajapaita.backerp.contabilidad.dto.AsientoRenumeracionDTO;
import pe.cajapaita.backerp.contabilidad.dto.CuentaBatchDTO;
import pe.cajapaita.backerp.contabilidad.dto.DetalleDTO;
import pe.cajapaita.backerp.contabilidad.dto.MonedaDTO;
import pe.cajapaita.backerp.contabilidad.dto.PeriodoDTO;
import pe.cajapaita.backerp.contabilidad.dto.SaldoDTO;
import pe.cajapaita.backerp.contabilidad.dto.TipoAsientoDTO;
import pe.cajapaita.backerp.contabilidad.dto.UsuarioDTO;
import pe.cajapaita.backerp.contabilidad.entidad.Agencia;
import pe.cajapaita.backerp.contabilidad.entidad.Cuenta;
import pe.cajapaita.backerp.contabilidad.entidad.Detalle;
import pe.cajapaita.backerp.contabilidad.entidad.Estado;
import pe.cajapaita.backerp.contabilidad.entidad.Procedencia;
import pe.cajapaita.backerp.contabilidad.entidad.Saldo;
import pe.cajapaita.backerp.contabilidad.entidad.Usuario;
import pe.cajapaita.backerp.contabilidad.infraestructura.Helper;
import pe.cajapaita.backerp.contabilidad.infraestructura.MonedaEnum;
import pe.cajapaita.backerp.contabilidad.infraestructura.ProcedenciaEnum;
import pe.cajapaita.backerp.contabilidad.infraestructura.TipoCuentaEnum;
import pe.cajapaita.backerp.contabilidad.servicio.IAccesoServicio;
import pe.cajapaita.backerp.contabilidad.servicio.ICuentaServicio;
import pe.cajapaita.backerp.contabilidad.servicio.IUtilitarioServicio;
import pe.cajapaita.backerp.contabilidad.servicio.impl.AsientoServicioImpl;

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
public class AsientoServicioTest {

    @InjectMocks
    private AsientoServicioImpl asientoServicio;

    @Mock
    private IRepositorioBaseDao<Asiento> asientoDAO;

    @Mock
    private IRepositorioBaseDao<Periodo> periodoDAO;

    @Mock
    private IRepositorioBaseDao<Moneda> monedaDAO;

    @Mock
    private IRepositorioBaseDao<TipoAsiento> tipoAsientoDAO;

    @Mock
    private IRepositorioBaseDao<Procedencia> procedenciaDAO;

    @Mock
    private IRepositorioBaseDao<Estado> estadoDAO;

    @Mock
    private IRepositorioBaseDao<Cuenta> cuentaDAO;

    @Mock
    private IRepositorioBaseDao<Detalle> detalleDAO;

    @Mock
    private IRepositorioBaseDao<Saldo> saldoDAO;

    @Mock
    private IUtilitarioServicio utilitarioServicio;

    @Mock
    private ICuentaServicio cuentaServicio;

    @Mock
    private IRepositorioBaseDao<Usuario> usuarioDAO;

    @Mock
    private IRepositorioBaseDao<Agencia> agenciaDAO;

    @Mock
    private IAccesoServicio accesoServicio;

    @Mock
    private IRepositorioBaseBatchDao repositorioBaseBatchDao;

    private int usuarioId;

    @Rule
    public final ExpectedException excepcion = ExpectedException.none();

    private int monedaId;
    private int tipoAsientoId;
    private int cuentaId;

    private AsientoDTO asientoDTO;
    private UsuarioDTO usuarioDTO;
    private List<DetalleDTO> detallesDTO;
    private DetalleDTO detalleDTO;
    private DetalleDTO detalleExcel;
    private Consulta consulta = new Consulta();
    List<DetalleDTO> listaDetallesExcel = new ArrayList<>();
    List<MonedaDTO> modenasActivas = new ArrayList<>();
    List<TipoAsientoDTO> tiposAsiento = new ArrayList<>();
    List<Integer> listaTipoAsientoBiMoneda = new ArrayList<>();
    List<AgenciaDTO> listaAgencias = new ArrayList<>();

    @Before
    public void inicio() throws ExcepcionNegocio {
        MockitoAnnotations.initMocks(this);

        MonedaDTO soles = new MonedaDTO();
        soles.setId(1);

        MonedaDTO dolares = new MonedaDTO();
        dolares.setId(2);
          usuarioDTO=new UsuarioDTO();
        usuarioDTO.setId(1);

        modenasActivas.add(soles);
        modenasActivas.add(dolares);

        monedaId = 1;
        tipoAsientoId = 1;
        cuentaId = 1;
        usuarioId = 1;

        detalleDTO = new DetalleDTO(1, "1001", new BigDecimal(100), new BigDecimal(100),1);
        detallesDTO = new ArrayList<>();
        detallesDTO.add(detalleDTO);

        detalleExcel = new DetalleDTO(0, "1182", BigDecimal.TEN, BigDecimal.TEN,1);
        listaDetallesExcel.add(detalleExcel);

        TipoAsientoDTO tipo1 = new TipoAsientoDTO();
        tipo1.setId(11);
        tipo1.setGlosa("GLOSA DE PRUEBA");

        TipoAsientoDTO tipo2 = new TipoAsientoDTO();
        tipo2.setId(12);
        tipo2.setGlosa("ASIENTO DE ACTUALIZACIÓN MONEDA EXTRANJERA");

        TipoAsientoDTO tipo3 = new TipoAsientoDTO();
        tipo3.setId(30);
        tipo3.setGlosa("ASIENTO POR DIFERENCIA DE CAMBIO");

        tiposAsiento.add(tipo1);
        tiposAsiento.add(tipo2);
        tiposAsiento.add(tipo3);

        asientoDTO = new AsientoDTO();
        asientoDTO.setFechaString("01/05/2016");
        asientoDTO.setMonedaId(monedaId);
        asientoDTO.setTipoAsientoId(tipoAsientoId);
        asientoDTO.setGlosa("glosa");
        asientoDTO.setDetalles(detallesDTO);
        asientoDTO.setAgenciaId(1);

        PeriodoDTO periodoDTO = new PeriodoDTO();
        periodoDTO.setFechaInicial(Helper.convertirAFecha("01/05/2016"));
        periodoDTO.setFechaFinal(Helper.convertirAFecha("31/05/2016"));
        periodoDTO.setFechaLimite(Helper.convertirAFecha("30/06/2016"));
        periodoDTO.setPeriodoAnteriorId(2);
        periodoDTO.setId(3);

        consulta.agregaAlias(new Alias("tipoAsiento", "tipoAsiento"));
        consulta.agregaAlias(new Alias("estado", "estado"));

        consulta.agregaRestriccionBetweenDate(new RestriccionBetweenDate("fecha", periodoDTO.getFechaInicial(), periodoDTO.getFechaFinal()));
        consulta.agregaRestriccionIgual(new RestriccionIgual("tipoAsiento.id", 12));
        consulta.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));

        listaTipoAsientoBiMoneda.add(5);
        listaTipoAsientoBiMoneda.add(30);

        AgenciaDTO agenciaDTO1 = new AgenciaDTO();
        AgenciaDTO agenciaDTO2 = new AgenciaDTO();
        agenciaDTO1.setId(1);
        agenciaDTO2.setId(2);

        listaAgencias.add(agenciaDTO1);
        listaAgencias.add(agenciaDTO2);
        
        when(utilitarioServicio.traerPeriodoVigente()).thenReturn(periodoDTO);
        when(utilitarioServicio.traerRegistrosPorPagina()).thenReturn(10);
        when(utilitarioServicio.traerListaMonedaActiva()).thenReturn(modenasActivas);
        when(periodoDAO.traerPorId(Periodo.class, 1)).thenReturn(new Periodo());
        when(monedaDAO.traerPorId(Moneda.class, monedaId)).thenReturn(new Moneda());
        when(tipoAsientoDAO.traerPorId(TipoAsiento.class, tipoAsientoId)).thenReturn(new TipoAsiento());
        when(procedenciaDAO.traerPorId(Procedencia.class, ProcedenciaEnum.CONTABILIDAD.ordinal())).thenReturn(new Procedencia());
        when(estadoDAO.traerPorId(Estado.class, EstadoEnum.ACTIVO.ordinal())).thenReturn(new Estado());
        when(accesoServicio.traerUsuario("user")).thenReturn(new UsuarioDTO());
        when(cuentaDAO.traerPorId(Cuenta.class, cuentaId)).thenReturn(new Cuenta());
        when(utilitarioServicio.traerTipoAsientoActualizacionME()).thenReturn(12);
        when(asientoDAO.contar(Asiento.class, consulta)).thenReturn(1);
        when(usuarioDAO.traerPorId(Usuario.class, usuarioId)).thenReturn(new Usuario());
        when(agenciaDAO.traerPorId(Agencia.class, asientoDTO.getAgenciaId())).thenReturn(new Agencia());
    }

    /**
     * metodo: traerAsientos(). Si no se envía periodo. Mostrar mensaje
     * "Seleccionar periodo"
     */
    @Test
    public void test_traerAsientos_01() throws ExcepcionNegocio {
        // Arrange        
        int tipoAsientoId = 0, monedaId = 0, procedenciaId = 0, periodoId = 0, pagina = 1;
       

        //Assert                        
        excepcion.expectMessage(Mensaje.ASIENTO_SELECCIONE_PERIODO);

        // Action       
        asientoServicio.traerAsientos(periodoId, tipoAsientoId, monedaId, procedenciaId, pagina, 7, 1,usuarioDTO);
        
    }

    /**
     * metodo: traerAsientos(). Sin no existe periodo. Mostrar mensaje "Periodo
     * no existe"
     */
    @Test
    public void test_traerAsientos_02() throws ExcepcionNegocio {
        // Arrange        
        int tipoAsientoId = 0, usuarioId = 0, procedenciaId = 0, periodoId = 1, pagina = 1;
        when(periodoDAO.traerPorId(Periodo.class, periodoId)).thenReturn(null);

        //Assert                        
        excepcion.expectMessage(Mensaje.ASIENTO_PERIODO_NO_EXISTE);

        // Action       
        asientoServicio.traerAsientos(periodoId, tipoAsientoId, usuarioId, procedenciaId, pagina, 0, 1,usuarioDTO);
    }

    /**
     * metodo: traerAsientos(). Dado un periodo y no existen asientos. Mostrar
     * mensaje "No se encontraron asientos con estos criterios"
     */
    @Test
    public void test_traerAsientos_03() throws ExcepcionNegocio {
        //Arrange        
        int tipoAsientoId = 0, usuarioId = 0, procedenciaId = 0, periodoId = 1, pagina = 1;
        List<AsientoDTO> asientosEsperados = new ArrayList<>();

        Consulta parametro = new Consulta();
        parametro.agregaAlias(new Alias("estado", "estado"));
        parametro.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));
        parametro.agregaAlias(new Alias("periodo", "periodo"));
        parametro.agregaRestriccionIgual(new RestriccionIgual("periodo.id", periodoId));
        parametro.agregaOrdenAscendente("fecha");

        when(asientoDAO.traerTodo(Asiento.class, parametro, AsientoDTO.class)).thenReturn(asientosEsperados);

        //Assert                        
        excepcion.expectMessage(Mensaje.ASIENTO_NO_ENCONTRADOS);

        // Action       
        asientoServicio.traerAsientos(periodoId, tipoAsientoId, usuarioId, procedenciaId, pagina, 7, 1,usuarioDTO);

    }

    /**
     * metodo: traerAsientos().Si el asiento no tiene detalles. Mostrar "No se
     * encontraron detalles"
     */
    @Test
    public void test_traerDetalle_01() throws ExcepcionNegocio {
        //Arrange
        int asientoId = 0;
        when(detalleDAO.traerTodo(Detalle.class, new Consulta(), DetalleDTO.class)).thenReturn(new ArrayList());

        //Assert
        excepcion.expectMessage(Mensaje.ASIENTO_DETALLE_NO_ENCONTRADO);

        //Action
        asientoServicio.traerDetalle(asientoId);
    }

    /**
     * metodo: guardar(). Si fecha es nula. Mostrar mensaje "Seleccione fecha de
     * asiento"
     */
    @Test
    public void test_guardar_01() throws ExcepcionNegocio {
        // Arrange        
        asientoDTO.setFechaString(null);

        //Assert
        excepcion.expectMessage(Mensaje.ASIENTO_SELECCIONE_FECHA);

        //Action
        asientoServicio.guardar(asientoDTO, ProcedenciaEnum.CONTABILIDAD, usuarioId);
    }

    /**
     * metodo: guardar(). Si fecha es vacia. Mostra mensaje "Seleccione fecha de
     * asiento"
     */
    @Test
    public void test_guardar_02() throws ExcepcionNegocio {
        // Arrange        
        asientoDTO.setFechaString("");

        //Assert
        excepcion.expectMessage(Mensaje.ASIENTO_SELECCIONE_FECHA);

        //Action
        asientoServicio.guardar(asientoDTO, ProcedenciaEnum.CONTABILIDAD, usuarioId);
    }

    /**
     * metodo: guardar(). Si glosa es nula. Mostra mensaje "Ingresar glosa"
     */
    @Test
    public void test_guardar_03() throws ExcepcionNegocio {
        // Arrange      
        asientoDTO.setGlosa(null);

        //Assert
        excepcion.expectMessage(Mensaje.ASIENTO_INGRESE_GLOSA);

        //Action
        asientoServicio.guardar(asientoDTO, ProcedenciaEnum.CONTABILIDAD, usuarioId);
    }

    /**
     * metodo: guardar(). Si glosa esta vacia. Mostra mensaje "Ingresar glosa"
     */
    @Test
    public void test_guardar_04() throws ExcepcionNegocio {
        // Arrange    
        asientoDTO.setGlosa("");

        //Assert
        excepcion.expectMessage(Mensaje.ASIENTO_INGRESE_GLOSA);

        //Action
        asientoServicio.guardar(asientoDTO, ProcedenciaEnum.CONTABILIDAD, usuarioId);
    }

    /**
     * metodo: guardar(). Si no seleccionamos moneda. Mostrar mensaje
     * "Seleccionar moneda"
     */
    @Test
    public void test_guardar_05() throws ExcepcionNegocio {
        // Arrange
        asientoDTO.setMonedaId(0);
        List<DetalleDTO> listaDetalleDTO=asientoDTO.getDetalles();
        listaDetalleDTO.get(0).setMonedaId(0);

        //Assert
        excepcion.expectMessage(Mensaje.ASIENTO_MONEDA_NO_EXISTE);

        //Action
        asientoServicio.guardar(asientoDTO, ProcedenciaEnum.CONTABILIDAD, usuarioId);

    }

    /**
     * metodo: guardar(). Si no seleccionamos tipo de asiento. Mostrar mensaje
     * "Seleccionar tipo de asiento"
     */
    @Test
    public void test_guardar_06() throws ExcepcionNegocio {
        // Arrange        
        asientoDTO.setTipoAsientoId(0);

        //Assert
        excepcion.expectMessage(Mensaje.ASIENTO_SELECCIONE_TIPO_ASIENTO);

        //Action
        asientoServicio.guardar(asientoDTO, ProcedenciaEnum.CONTABILIDAD, usuarioId);
    }

    /**
     * metodo: guardar(). Si formato fecha es incorrecto (mm/dd/yyyy). Mostrar
     * mensaje "Formato de fecha incorrecto"
     */
    @Test
    public void test_guardar_07() throws ExcepcionNegocio {
        // Arrange        
        asientoDTO.setFechaString("15/13/2016");

        //Assert
        excepcion.expectMessage(Mensaje.ASIENTO_FORMATO_FECHA_INCORRECTA);

        //Action
        asientoServicio.guardar(asientoDTO, ProcedenciaEnum.CONTABILIDAD, usuarioId);
    }

    /**
     * metodo: guardar(). Si moneda no existe. Mostrar mensaje "Moneda no
     * existe"
     */
    @Test
    public void test_guardar_08() throws ExcepcionNegocio {
        // Arrange       
        List<DetalleDTO> listaDetalleDTO=asientoDTO.getDetalles();
        listaDetalleDTO.get(0).setMonedaId(0);

        //Assert
        excepcion.expectMessage(Mensaje.ASIENTO_MONEDA_NO_EXISTE);

        //Action
        asientoServicio.guardar(asientoDTO, ProcedenciaEnum.CONTABILIDAD, usuarioId);
    }

    /**
     * metodo: guardar(). Si tipo de asiento no existe. Mostrar mensaje "Tipo de
     * asiento no existe"
     */
    @Test
    public void test_guardar_09() throws ExcepcionNegocio {
        // Arrange        
        when(tipoAsientoDAO.traerPorId(TipoAsiento.class, tipoAsientoId)).thenReturn(null);

        //Assert
        excepcion.expectMessage(Mensaje.ASIENTO_TIPO_ASIENTO_NO_EXISTE);

        //Action
        asientoServicio.guardar(asientoDTO, ProcedenciaEnum.CONTABILIDAD, usuarioId);
    }

    /**
     * metodo: guardar(). Si procedencia no existe. Mostrar mensaje "Procedencia
     * no existe"
     */
    @Test
    public void test_guardar_10() throws ExcepcionNegocio {
        //Arrange
        asientoDTO.setTipoAsientoId(11);
        when(procedenciaDAO.traerPorId(Procedencia.class, ProcedenciaEnum.CONTABILIDAD.ordinal())).thenReturn(null);
        when(utilitarioServicio.traerListaTipoAsiento()).thenReturn(tiposAsiento);
        //Assert
        excepcion.expectMessage(Mensaje.ASIENTO_PROCEDENCIA_NO_EXISTE);

        //Action
        asientoServicio.guardar(asientoDTO, ProcedenciaEnum.CONTABILIDAD, usuarioId);
    }

    /**
     * metodo: guardar(). Si estado no existe. Mostrar mensaje "Estado no
     * existe"
     */
    @Test
    public void test_guardar_11() throws ExcepcionNegocio {
        //Arrange
        Estado estado = new Estado();
        estado.setId(EstadoEnum.ACTIVO.ordinal());

        asientoDTO.setTipoAsientoId(11);

        CuentaBatchDTO cuentaBatchDTO = new CuentaBatchDTO();
        cuentaBatchDTO.setEsAnalitica("S");
        cuentaBatchDTO.setEstadoId(EstadoEnum.ACTIVO.ordinal());

        when(estadoDAO.traerPorId(Estado.class, EstadoEnum.ACTIVO.ordinal())).thenReturn(null);
        when(utilitarioServicio.traerListaTipoAsiento()).thenReturn(tiposAsiento);
        when(utilitarioServicio.traerListaAgencia()).thenReturn(listaAgencias);
        when(repositorioBaseBatchDao.traerCuentaPorId(cuentaId)).thenReturn(cuentaBatchDTO);

        //Assert
        excepcion.expectMessage(Mensaje.ASIENTO_ESTADO_NO_EXISTE);

        //Action
        asientoServicio.guardar(asientoDTO, ProcedenciaEnum.CONTABILIDAD, usuarioId);
    }

    /**
     * metodo: guardar(). Si usuario no existe. Mostrar mensaje "Usuario no
     * existe"
     */
    @Test
    public void test_guardar_12() throws ExcepcionNegocio {
        asientoDTO.setTipoAsientoId(11);

        //Arrange
        when(usuarioDAO.traerPorId(Usuario.class, usuarioId)).thenReturn(null);
        when(utilitarioServicio.traerListaTipoAsiento()).thenReturn(tiposAsiento);
        when(utilitarioServicio.traerListaAgencia()).thenReturn(listaAgencias);

        //Assert
        excepcion.expectMessage(Mensaje.ASIENTO_USUARIO_NO_EXISTE);

        //Action
        asientoServicio.guardar(asientoDTO, ProcedenciaEnum.CONTABILIDAD, usuarioId);
    }

    /**
     * metodo: guardar(). Si detalle es nulo. Mostrar mensaje "No se encontraron
     * detalles"
     */
    @Test
    public void test_guardar_13() throws ExcepcionNegocio {
        // Arrange
        asientoDTO.setDetalles(null);

        //Assert
        excepcion.expectMessage(Mensaje.ASIENTO_DETALLE_NO_ENCONTRADO);

        //Action
        asientoServicio.guardar(asientoDTO, ProcedenciaEnum.CONTABILIDAD, usuarioId);
    }

    /**
     * metodo: guardar(). Si detalle esta vacio. Mostrar mensaje "No se
     * encontraron detalles"
     */
    @Test
    public void test_guardar_14() throws ExcepcionNegocio {
        // Arrange
        asientoDTO.setDetalles(new ArrayList<>());

        //Assert
        excepcion.expectMessage(Mensaje.ASIENTO_DETALLE_NO_ENCONTRADO);

        //Action
        asientoServicio.guardar(asientoDTO, ProcedenciaEnum.CONTABILIDAD, usuarioId);
    }

    /**
     * metodo: guardar(). Si cuenta no fue ingresada. Mostrar mensaje "Cuenta
     * contable vacía"
     */
    @Test
    public void test_guardar_15() throws ExcepcionNegocio {
        // Arrange
        detalleDTO.setCuentaId(0);
        detalleDTO.setCuentaCuenta("");

        //Assert
        excepcion.expectMessage(Mensaje.CUENTA_CONTABLE_VACIA);

        //Action
        asientoServicio.guardar(asientoDTO, ProcedenciaEnum.CONTABILIDAD, usuarioId);
    }

    /**
     * metodo: guardar(). Si debe y haber son cero. Mostrar mensaje "Debe y
     * haber deben ser mayor a cero"
     */
    @Test
    public void test_guardar_16() throws ExcepcionNegocio {
        // Arrange
        asientoDTO.setTipoAsientoId(11);
        detalleDTO.setDebe(new BigDecimal(0));
        detalleDTO.setHaber(new BigDecimal(0));
        CuentaBatchDTO cuentaBatchDTO = new CuentaBatchDTO();
        cuentaBatchDTO.setEsAnalitica("S");
        cuentaBatchDTO.setEstadoId(EstadoEnum.ACTIVO.ordinal());

        when(utilitarioServicio.traerListaTipoAsiento()).thenReturn(tiposAsiento);
        when(utilitarioServicio.traerListaAgencia()).thenReturn(listaAgencias);
        when(repositorioBaseBatchDao.traerCuentaPorId(cuentaId)).thenReturn(cuentaBatchDTO);

        //Assert
        excepcion.expectMessage(Mensaje.ASIENTO_DEBE_HABER_INVALIDO);

        //Action
        asientoServicio.guardar(asientoDTO, ProcedenciaEnum.CONTABILIDAD, usuarioId);

    }

    /**
     * metodo: guardar(). Si haber es negativo. Mostrar mensaje "Debe y haber
     * deben ser mayor a cero"
     */
    @Test
    public void test_guardar_17() throws ExcepcionNegocio {
        // Arrange
        asientoDTO.setTipoAsientoId(11);
        detalleDTO.setDebe(new BigDecimal(100));
        detalleDTO.setHaber(new BigDecimal(-10));
        CuentaBatchDTO cuentaBatchDTO = new CuentaBatchDTO();
        cuentaBatchDTO.setEsAnalitica("S");
        cuentaBatchDTO.setEstadoId(EstadoEnum.ACTIVO.ordinal());

        when(utilitarioServicio.traerListaTipoAsiento()).thenReturn(tiposAsiento);
        when(utilitarioServicio.traerListaAgencia()).thenReturn(listaAgencias);
        when(repositorioBaseBatchDao.traerCuentaPorId(cuentaId)).thenReturn(cuentaBatchDTO);

        //Assert
        excepcion.expectMessage(Mensaje.ASIENTO_DEBE_HABER_INVALIDO);

        //Action
        asientoServicio.guardar(asientoDTO, ProcedenciaEnum.CONTABILIDAD, usuarioId);

    }

    /**
     * metodo: guardar(). Si cuenta no existe. Mostrar mensaje "Cuenta no
     * existe"
     */
    @Test
    public void test_guardar_18() throws ExcepcionNegocio {
        // Arrange
        asientoDTO.setTipoAsientoId(11);
        when(utilitarioServicio.traerListaTipoAsiento()).thenReturn(tiposAsiento);
        when(utilitarioServicio.traerListaAgencia()).thenReturn(listaAgencias);
        when(repositorioBaseBatchDao.traerCuentaPorId(cuentaId)).thenReturn(null);

        //Assert
        excepcion.expectMessage(Mensaje.CUENTA_CONTABLE_NO_EXISTE);

        //Action
        asientoServicio.guardar(asientoDTO, ProcedenciaEnum.CONTABILIDAD, usuarioId);
    }

    /**
     * metodo: guardar(). Si cuenta no es analitica. Mostrar mensaje "Cuenta no
     * es analítica"
     */
    @Test
    public void test_guardar_19() throws ExcepcionNegocio {
        // Arrange        
        asientoDTO.setTipoAsientoId(11);
        CuentaBatchDTO cuentaBatchDTO = new CuentaBatchDTO();
        cuentaBatchDTO.setCuenta("1001");
        cuentaBatchDTO.setEsAnalitica("N");
        cuentaBatchDTO.setEstadoId(EstadoEnum.ACTIVO.ordinal());

        when(utilitarioServicio.traerListaTipoAsiento()).thenReturn(tiposAsiento);
        when(utilitarioServicio.traerListaAgencia()).thenReturn(listaAgencias);
        when(repositorioBaseBatchDao.traerCuentaPorId(cuentaId)).thenReturn(cuentaBatchDTO);

        //Assert
        excepcion.expectMessage(cuentaBatchDTO.getCuenta() + " " + Mensaje.CUENTA_NO_ES_ANALITICA);

        //Action
        asientoServicio.guardar(asientoDTO, ProcedenciaEnum.CONTABILIDAD, usuarioId);
    }

    /**
     * metodo: guardar(). Si cuenta no esta activa. Mostrar mensaje "Cuenta no
     * esta activa"
     */
    @Test
    public void test_guardar_20() throws ExcepcionNegocio {
        // Arrange        

        asientoDTO.setTipoAsientoId(11);
        CuentaBatchDTO cuentaBatchDTO = new CuentaBatchDTO();
        cuentaBatchDTO.setEsAnalitica("S");
        cuentaBatchDTO.setEstadoId(EstadoEnum.INACTIVO.ordinal());

        when(utilitarioServicio.traerListaTipoAsiento()).thenReturn(tiposAsiento);
        when(utilitarioServicio.traerListaAgencia()).thenReturn(listaAgencias);
        when(repositorioBaseBatchDao.traerCuentaPorId(cuentaId)).thenReturn(cuentaBatchDTO);

        //Assert
        excepcion.expectMessage(Mensaje.CUENTA_NO_ACTIVA);

        //Action
        asientoServicio.guardar(asientoDTO, ProcedenciaEnum.CONTABILIDAD, usuarioId);
    }

    /**
     * metodo: guardar(). Si total debe difiere del total haber y tipo de
     * asiento no es compra-venta. Mostrar mensaje "Debe y haber no cuadra"
     */
    @Test
    public void test_guardar_21() throws ExcepcionNegocio {
        // Arrange

        asientoDTO.setTipoAsientoId(11);
        CuentaBatchDTO cuentaBatchDTO = new CuentaBatchDTO();
        cuentaBatchDTO.setEsAnalitica("S");
        cuentaBatchDTO.setEstadoId(EstadoEnum.ACTIVO.ordinal());

        when(utilitarioServicio.traerListaTipoAsiento()).thenReturn(tiposAsiento);
        when(utilitarioServicio.traerListaAgencia()).thenReturn(listaAgencias);
        when(repositorioBaseBatchDao.traerCuentaPorId(cuentaId)).thenReturn(cuentaBatchDTO);

        detalleDTO.setDebe(new BigDecimal(100));
        detalleDTO.setHaber(new BigDecimal(101));

        //Assert
        excepcion.expectMessage(Mensaje.ASIENTO_DEBE_HABER_NO_CUADRA);

        //Action
        asientoServicio.guardar(asientoDTO, ProcedenciaEnum.CONTABILIDAD, usuarioId);
    }

    /**
     * metodo: guardar(). Si fecha no corresponde a periodo vigente. Mostrar
     * mensaje "Asiento no corresponde a periodo vigente"
     */
    @Test
    public void test_guardar_22() throws ExcepcionNegocio {
        // Arrange        
        Consulta consulta = new Consulta();
        consulta.agregaAlias(new Alias("estado", "estado"));
        consulta.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));
        consulta.agregaEquivalencia(new Equivalencia("id", "id"));
        consulta.agregaEquivalencia(new Equivalencia("ultimoAsiento", "ultimoAsiento"));
        consulta.agregaEquivalencia(new Equivalencia("fechaInicial", "fechaInicial"));
        consulta.agregaEquivalencia(new Equivalencia("fechaInicial", "fechaFinal"));

        PeriodoDTO periodoDTO = new PeriodoDTO();
        periodoDTO.setFechaInicial(Helper.convertirAFecha("01/07/2016"));
        periodoDTO.setFechaFinal(Helper.convertirAFecha("31/07/2016"));
        when(periodoDAO.traerUnico(Periodo.class, consulta, PeriodoDTO.class)).thenReturn(periodoDTO);

        asientoDTO.setFechaString("15/07/2016");

        //Assert
        excepcion.expectMessage(Mensaje.ASIENTO_DE_PERIODO_NO_VIGENTE);

        //Action
        asientoServicio.guardar(asientoDTO, ProcedenciaEnum.CONTABILIDAD, usuarioId);
    }

    /**
     * metodo: traerAsiento(). Si el asiento no existe. Mostrar "Asiento no
     * existe"
     */
    @Test
    public void test_traerAsiento_01() throws ExcepcionNegocio {
        //Arrange
        int asientoId = 0;
        Consulta consulta = new Consulta();

        when(asientoDAO.traerUnico(Asiento.class, consulta, AsientoDTO.class)).thenReturn(null);

        //Assert
        excepcion.expectMessage(Mensaje.ASIENTO_NO_EXISTE);

        //Action
        asientoServicio.traerAsiento(asientoId);
    }

    /**
     * metodo: traerAsiento(). Si existe el asiento pero no tiene detalles.
     * Mostrar "No se encontraron detalles"
     */
    @Test
    public void test_traerAsiento_02() throws ExcepcionNegocio {
        //Arrange
        int asientoId = 1;

        AsientoDTO asientoDTO = new AsientoDTO();
        asientoDTO.setId(1);
        asientoDTO.setFecha(new Date());

        List<DetalleDTO> detalleDTO = new ArrayList<>();

        Consulta consulta = new Consulta();
        consulta.agregaAlias(new Alias("tipoAsiento", "tipoAsiento"));
        consulta.agregaAlias(new Alias("moneda", "moneda"));
        consulta.agregaRestriccionIgual(new RestriccionIgual("id", asientoId));
        consulta.agregaEquivalencia(new Equivalencia("id", "id"));
        consulta.agregaEquivalencia(new Equivalencia("numero", "numero"));
        consulta.agregaEquivalencia(new Equivalencia("fecha", "fecha"));
        consulta.agregaEquivalencia(new Equivalencia("glosa", "glosa"));
        consulta.agregaEquivalencia(new Equivalencia("tipoAsiento.id", "tipoAsientoId"));
        consulta.agregaEquivalencia(new Equivalencia("moneda.id", "monedaId"));
        when(asientoDAO.traerUnico(Asiento.class, consulta, AsientoDTO.class)).thenReturn(asientoDTO);

        when(detalleDAO.traerTodo(Detalle.class, consulta, DetalleDTO.class)).thenReturn(detalleDTO);

        //Assert
        excepcion.expectMessage(Mensaje.ASIENTO_DETALLE_NO_ENCONTRADO);

        //Action
        asientoServicio.traerAsiento(asientoId);

    }

    /**
     * metodo: traerAsiento(). Si existe el asiento y si tiene detalles. Mostrar
     * el asiento con su detalle
     */
    @Test
    public void test_traerAsiento_03() throws ExcepcionNegocio {
        //Arrange
        int asientoId = 1;

        AsientoDTO asientoDTO = new AsientoDTO();
        asientoDTO.setFecha(new Date());
        asientoDTO.setMonedaId(1);

        List<DetalleDTO> detalleDTOs = new ArrayList<>();
        DetalleDTO dto1 = new DetalleDTO();
        DetalleDTO dto2 = new DetalleDTO();
        detalleDTOs.add(dto1);
        detalleDTOs.add(dto2);

        Consulta consulta = new Consulta();
        consulta.agregaAlias(new Alias("tipoAsiento", "tipoAsiento"));
        consulta.agregaAlias(new Alias("moneda", "moneda"));
        consulta.agregaRestriccionIgual(new RestriccionIgual("id", asientoId));
        consulta.agregaEquivalencia(new Equivalencia("id", "id"));
        consulta.agregaEquivalencia(new Equivalencia("numero", "numero"));
        consulta.agregaEquivalencia(new Equivalencia("fecha", "fecha"));
        consulta.agregaEquivalencia(new Equivalencia("glosa", "glosa"));
        consulta.agregaEquivalencia(new Equivalencia("tipoAsiento.id", "tipoAsientoId"));
        consulta.agregaEquivalencia(new Equivalencia("moneda.id", "monedaId"));

        when(asientoDAO.traerUnico(Asiento.class, consulta, AsientoDTO.class)).thenReturn(asientoDTO);
        consulta = new Consulta();
        consulta.agregaAlias(new Alias("asiento", "asiento"));
        consulta.agregaAlias(new Alias("cuenta", "cuenta"));
        consulta.agregaRestriccionIgual(new RestriccionIgual("asiento.id", asientoId));
        consulta.agregaEquivalencia(new Equivalencia("id", "id"));
        consulta.agregaEquivalencia(new Equivalencia("cuenta.cuenta", "cuenta"));
        consulta.agregaEquivalencia(new Equivalencia("debe", "debe"));
        consulta.agregaEquivalencia(new Equivalencia("haber", "haber"));
        consulta.agregaEquivalencia(new Equivalencia("cuenta.id", "cuentaId"));
        consulta.agregaEquivalencia(new Equivalencia("moneda.id","monedaId"));
        consulta.agregaOrdenAscendente("moneda.id");
        consulta.agregaOrdenAscendente("cuenta");
        when(detalleDAO.traerTodo(Detalle.class, consulta, DetalleDTO.class)).thenReturn(detalleDTOs);

        //Action
        AsientoDTO asientoEsperado = asientoServicio.traerAsiento(asientoId);

        //Assert
        Assert.assertNotNull(asientoEsperado);
        Assert.assertEquals(1, asientoEsperado.getMonedaId());
        Assert.assertEquals(2, asientoEsperado.getDetalles().size());
    }

    /**
     * metodo: eliminar(). Si el asiento no existe. Mostrar "El asiento no
     * existe"
     */
    @Test
    public void test_eliminar_01() throws ExcepcionNegocio {
        //Arrange
        int asientoId = 1;

        when(asientoDAO.traerPorId(Asiento.class, asientoId)).thenReturn(null);

        //Assert
        excepcion.expectMessage(Mensaje.ASIENTO_NO_EXISTE);

        //Action
        asientoServicio.eliminar(asientoId, usuarioId);

    }

    /**
     * metodo: eliminar(). Si el asiento no corresponde al periodo vigente.
     * Mostrar "No se puede alterar asientos de un periodo cerrado"
     */
    @Test
    public void test_eliminar_02() throws ExcepcionNegocio {
        //Arrange
        int asientoId = 1;
        Procedencia procedencia = new Procedencia();
        procedencia.setId(1);

        Asiento asiento = new Asiento();
        asiento.setId(asientoId);
        asiento.setFecha(Helper.convertirAFecha("31/05/2016"));
        asiento.setProcedencia(procedencia);

        PeriodoDTO periodoDTO = new PeriodoDTO();
        periodoDTO.setFechaInicial(Helper.convertirAFecha("01/06/2016"));
        periodoDTO.setFechaFinal(Helper.convertirAFecha("30/06/2016"));

        when(asientoDAO.traerPorId(Asiento.class, asientoId)).thenReturn(asiento);
        when(utilitarioServicio.traerPeriodoVigente()).thenReturn(periodoDTO);

        //Assert
        excepcion.expectMessage(Mensaje.ASIENTO_DE_PERIODO_NO_VIGENTE);

        //Action
        asientoServicio.eliminar(asientoId, usuarioId);

    }

    /**
     * metodo eliminar() si la procedencia del asiento es SYSONE mostrar: "Este
     * asiento procede de una integración del Sysone; cancele la integración"
     *
     */
    @Test
    public void test_eliminar_03() throws ExcepcionNegocio {
        //Arrange
        int asientoId = 1;
        Procedencia procedencia = new Procedencia();
        procedencia.setId(2);

        Asiento asiento = new Asiento();
        asiento.setId(asientoId);
        asiento.setFecha(Helper.convertirAFecha("31/05/2016"));
        asiento.setProcedencia(procedencia);

        PeriodoDTO periodoDTO = new PeriodoDTO();
        periodoDTO.setFechaInicial(Helper.convertirAFecha("01/06/2016"));
        periodoDTO.setFechaFinal(Helper.convertirAFecha("30/06/2016"));

        when(asientoDAO.traerPorId(Asiento.class, asientoId)).thenReturn(asiento);
        when(utilitarioServicio.traerPeriodoVigente()).thenReturn(periodoDTO);

        //Assert
        excepcion.expectMessage(Mensaje.ASIENTO_NECESITA_CANCELAR_INTEGRACION);

        //Action
        asientoServicio.eliminar(asientoId, usuarioId);
    }

    /**
     * metodo: guadar(). Cuando edito y no existe el asiento. Mostrar mensaje
     * "Asiento no existe"
     */
    @Test
    public void test_guardar_23() throws ExcepcionNegocio {
        //Arrange
        asientoDTO.setId(1);
        when(asientoDAO.traerPorId(Asiento.class, asientoDTO.getId())).thenReturn(null);

        //Assert
        excepcion.expectMessage(Mensaje.ASIENTO_NO_EXISTE);

        //Action
        asientoServicio.guardar(asientoDTO, ProcedenciaEnum.CONTABILIDAD, usuarioId);
    }

    /**
     * metodo: actualizar(). Cuando edito y el asiento no corresponda al periodo
     * vigente. Mostrar mensaje "No se puede alterar asientos de un periodo
     * cerrado"
     */
    @Test
    public void test_guardar_24() throws ExcepcionNegocio {
        //Arrange
        asientoDTO.setId(1);

        Procedencia procedencia = new Procedencia();
        procedencia.setDescripcion("SIAFC");
        procedencia.setId(1);

        Asiento asiento = new Asiento();
        asiento.setId(asientoDTO.getId());
        asiento.setFecha(Helper.convertirAFecha("31/05/2016"));
        asiento.setProcedencia(procedencia);

        PeriodoDTO periodoDTO = new PeriodoDTO();
        periodoDTO.setFechaInicial(Helper.convertirAFecha("01/06/2016"));
        periodoDTO.setFechaFinal(Helper.convertirAFecha("30/06/2016"));

        when(asientoDAO.traerPorId(Asiento.class, asientoDTO.getId())).thenReturn(asiento);
        when(utilitarioServicio.traerPeriodoVigente()).thenReturn(periodoDTO);

        //Assert
        excepcion.expectMessage(Mensaje.ASIENTO_DE_PERIODO_NO_VIGENTE);

        //Action
        asientoServicio.guardar(asientoDTO, ProcedenciaEnum.CONTABILIDAD, usuarioId);
    }

    /**
     * metodo actualizar(). Cuando edito y el asiento procede del SYSONE O SIAFC
     * mostrar "No puede editar asiento generado por una integración"
     */
    @Test
    public void test_guardar_25() throws ExcepcionNegocio {
        //Arrange
        asientoDTO.setId(1);

        Procedencia procedencia = new Procedencia();
        procedencia.setDescripcion("SIAFC");
        procedencia.setId(3);

        Asiento asiento = new Asiento();
        asiento.setId(asientoDTO.getId());
        asiento.setFecha(Helper.convertirAFecha("31/05/2016"));
        asiento.setProcedencia(procedencia);

        PeriodoDTO periodoDTO = new PeriodoDTO();
        periodoDTO.setFechaInicial(Helper.convertirAFecha("01/06/2016"));
        periodoDTO.setFechaFinal(Helper.convertirAFecha("30/06/2016"));

        when(asientoDAO.traerPorId(Asiento.class, asientoDTO.getId())).thenReturn(asiento);
        when(utilitarioServicio.traerPeriodoVigente()).thenReturn(periodoDTO);

        //Assert
        excepcion.expectMessage(Mensaje.INTEGRACION_SYSONE_NO_PUEDE_EDITAR);

        //Action
        asientoServicio.guardar(asientoDTO, ProcedenciaEnum.CONTABILIDAD, usuarioId);
    }

    /**
     * metodo: renumerarAsientos()
     *
     * @throws ExcepcionNegocio
     */
    @Test
    public void test_renumerarAsientos_01() throws ExcepcionNegocio {
        //Arrange
        List<AsientoRenumeracionDTO> asientos = new ArrayList<>();
        AsientoRenumeracionDTO asiento1 = new AsientoRenumeracionDTO();
        asiento1.setId(1);
        asiento1.setFecha(Helper.convertirAFecha("14/06/2016"));
        asiento1.setNumero(110);

        Asiento asiento_1 = new Asiento();
        asiento_1.setId(1);
        asiento_1.setFecha(Helper.convertirAFecha("14/06/2016"));
        asiento_1.setNumero(110);

        AsientoRenumeracionDTO asiento2 = new AsientoRenumeracionDTO();
        asiento2.setId(2);
        asiento2.setFecha(Helper.convertirAFecha("18/06/2016"));
        asiento2.setNumero(116);

        Asiento asiento_2 = new Asiento();
        asiento_2.setId(2);
        asiento_2.setFecha(Helper.convertirAFecha("18/06/2016"));
        asiento_2.setNumero(116);

        asientos.add(asiento1);
        asientos.add(asiento2);

        PeriodoDTO periodoDTO = new PeriodoDTO();
        periodoDTO.setId(3);
        periodoDTO.setFechaInicial(Helper.convertirAFecha("01/06/2016"));
        periodoDTO.setFechaFinal(Helper.convertirAFecha("30/06/2016"));
        periodoDTO.setUltimoAsiento(120);
        periodoDTO.setPeriodoAnteriorId(2);

        Periodo periodo = new Periodo();
        periodo.setId(3);
        periodo.setFechaInicial(Helper.convertirAFecha("01/06/2016"));
        periodo.setFechaFinal(Helper.convertirAFecha("30/06/2016"));
        periodo.setUltimoAsiento(120);

        Periodo periodoAnt = new Periodo();
        periodoAnt.setUltimoAsiento(100);

        Consulta consulta = new Consulta();
        consulta.agregaAlias(new Alias("procedencia", "procedencia"));
        consulta.agregaAlias(new Alias("tipoAsiento", "tipoAsiento"));
        consulta.agregaAlias(new Alias("estado", "estado"));
        consulta.agregaRestriccionBetweenDate(new RestriccionBetweenDate("fecha", periodoDTO.getFechaInicial(), periodoDTO.getFechaFinal()));
        consulta.agregaEquivalencia(new Equivalencia("id", "id"));
        consulta.agregaEquivalencia(new Equivalencia("fecha", "fecha"));
        consulta.agregaEquivalencia(new Equivalencia("numero", "numero"));
        consulta.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));
        consulta.agregaOrdenAscendente("fecha");
        consulta.agregaOrdenAscendente("procedencia.id");
        consulta.agregaOrdenAscendente("tipoAsiento.id");

        Consulta consulta1 = new Consulta();
        consulta1.agregaRestriccionBetweenDate(new RestriccionBetweenDate("fecha", periodoDTO.getFechaInicial(), periodoDTO.getFechaFinal()));

        when(utilitarioServicio.traerPeriodoVigente()).thenReturn(periodoDTO);
        when(asientoDAO.contar(Asiento.class, consulta1)).thenReturn(2);
        when(periodoDAO.traerPorId(Periodo.class, 2)).thenReturn(periodoAnt);
        when(periodoDAO.traerPorId(Periodo.class, 3)).thenReturn(periodo);
        when(asientoDAO.traerPorId(Asiento.class, 1)).thenReturn(asiento_1);
        when(asientoDAO.traerPorId(Asiento.class, 2)).thenReturn(asiento_2);
        when(asientoDAO.traerTodo(Asiento.class, consulta, AsientoRenumeracionDTO.class)).thenReturn(asientos);

        //Assert
        excepcion.expectMessage(Mensaje.OPERACION_CORRECTA);
        //Action
        asientoServicio.renumerarAsientos();
    }

    @Test
    public void test_renumerarAsientos_02() throws ExcepcionNegocio {
        //Arrange
        List<AsientoRenumeracionDTO> asientos = new ArrayList<>();

        Asiento asiento_1 = new Asiento();
        asiento_1.setId(1);
        asiento_1.setFecha(Helper.convertirAFecha("14/06/2016"));
        asiento_1.setNumero(110);

        Asiento asiento_2 = new Asiento();
        asiento_2.setId(2);
        asiento_2.setFecha(Helper.convertirAFecha("18/06/2016"));
        asiento_2.setNumero(116);

        PeriodoDTO periodoDTO = new PeriodoDTO();
        periodoDTO.setId(3);
        periodoDTO.setFechaInicial(Helper.convertirAFecha("01/06/2016"));
        periodoDTO.setFechaFinal(Helper.convertirAFecha("30/06/2016"));
        periodoDTO.setUltimoAsiento(120);
        periodoDTO.setPeriodoAnteriorId(2);

        Periodo periodo = new Periodo();
        periodo.setId(3);
        periodo.setFechaInicial(Helper.convertirAFecha("01/06/2016"));
        periodo.setFechaFinal(Helper.convertirAFecha("30/06/2016"));
        periodo.setUltimoAsiento(120);

        Periodo periodoAnt = new Periodo();
        periodoAnt.setUltimoAsiento(100);

        Consulta consulta = new Consulta();
        consulta.agregaRestriccionBetweenDate(new RestriccionBetweenDate("fecha", periodoDTO.getFechaInicial(), periodoDTO.getFechaFinal()));
        consulta.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));
        consulta.agregaEquivalencia(new Equivalencia("id", "id"));
        consulta.agregaEquivalencia(new Equivalencia("fecha", "fecha"));
        consulta.agregaEquivalencia(new Equivalencia("numero", "numero"));
        consulta.agregaOrdenDescentente("numero");

        Consulta consulta1 = new Consulta();
        consulta1.agregaRestriccionBetweenDate(new RestriccionBetweenDate("fecha", periodoDTO.getFechaInicial(), periodoDTO.getFechaFinal()));

        when(utilitarioServicio.traerPeriodoVigente()).thenReturn(periodoDTO);
        when(asientoDAO.contar(Asiento.class, consulta1)).thenReturn(2);
        when(periodoDAO.traerPorId(Periodo.class, 2)).thenReturn(periodoAnt);
        when(periodoDAO.traerPorId(Periodo.class, 3)).thenReturn(periodo);
        when(asientoDAO.traerPorId(Asiento.class, 1)).thenReturn(asiento_1);
        when(asientoDAO.traerPorId(Asiento.class, 2)).thenReturn(asiento_2);
        when(asientoDAO.traerTodo(Asiento.class, consulta, AsientoRenumeracionDTO.class)).thenReturn(asientos);

        //Assert
        excepcion.expectMessage(Mensaje.ASIENTO_NO_ENCONTRADOS_PARA_PERIODO);
        //Action
        asientoServicio.renumerarAsientos();
    }

    /**
     * metodo: ultimaFechaRenumeracion() si nunca se ha hecho la renumeración
     * mostrar "No se ha procesado la renumeración en este periodo"
     */
    @Test
    public void test_ultimaFechaRenumeracion_01() throws ExcepcionNegocio {
        //Arrange
        PeriodoDTO periodoDTO = new PeriodoDTO();
        periodoDTO.setId(3);

        PeriodoDTO periodoDTO2 = new PeriodoDTO();
        periodoDTO2.setId(3);
        periodoDTO2.setFechaRenumeracion(null);

        Consulta consulta = new Consulta();
        consulta.agregaEquivalencia(new Equivalencia("fechaRenumeracion", "fechaRenumeracion"));
        consulta.agregaRestriccionIgual(new RestriccionIgual("id", 3));

        when(utilitarioServicio.traerPeriodoVigente()).thenReturn(periodoDTO);
        when(periodoDAO.traerUnico(Periodo.class, consulta, PeriodoDTO.class)).thenReturn(periodoDTO2);

        //Asert
        String mensaje = asientoServicio.ultimaFechaRenumeracion();

        //Action
        Assert.assertEquals(Mensaje.ASIENTO_RENUMERACION_NO_PROCESADA, mensaje);
    }

    /**
     * metodo: ultimaFechaRenumeracion() . Si existe una fecha de renumeracion
     * mostrar: "La última fecha de renumeración fue el --fecha--"
     */
    @Test
    public void test_ultimaFechaRenumeracion_02() throws ExcepcionNegocio {
        //Arrange
        PeriodoDTO periodoDTO = new PeriodoDTO();
        periodoDTO.setId(3);

        PeriodoDTO periodoDTO2 = new PeriodoDTO();
        periodoDTO2.setId(3);
        periodoDTO2.setFechaRenumeracion(Helper.convertirAFecha("15/06/2016"));

        Consulta consulta = new Consulta();
        consulta.agregaEquivalencia(new Equivalencia("fechaRenumeracion", "fechaRenumeracion"));
        consulta.agregaRestriccionIgual(new RestriccionIgual("id", 3));

        when(utilitarioServicio.traerPeriodoVigente()).thenReturn(periodoDTO);
        when(periodoDAO.traerUnico(Periodo.class, consulta, PeriodoDTO.class)).thenReturn(periodoDTO2);

        //Action
        String mensaje = asientoServicio.ultimaFechaRenumeracion();

        Assert.assertEquals(Mensaje.ASIENTO_ULTIMA_FECHA_RENUMERACION + "15/06/2016 12:00AM", mensaje);
    }

    @Test
    public void test_ultimaFechaRenumeracion_03() throws ExcepcionNegocio {
        PeriodoDTO periodoDTO = new PeriodoDTO();
        periodoDTO.setId(3);

        Consulta consulta = new Consulta();
        consulta.agregaEquivalencia(new Equivalencia("fechaRenumeracion", "fechaRenumeracion"));
        consulta.agregaRestriccionIgual(new RestriccionIgual("id", 3));

        when(utilitarioServicio.traerPeriodoVigente()).thenReturn(periodoDTO);
        when(periodoDAO.traerUnico(Periodo.class, consulta, PeriodoDTO.class)).thenReturn(null);

        excepcion.expectMessage(Mensaje.ASIENTO_PERIODO_NO_EXISTE);

        //Action
        asientoServicio.ultimaFechaRenumeracion();

    }

    /**
     * metodo cambiarTC(). Cuando ya existe un asiento por tipo de cambio de
     * moneda "No se puede actualizar Tipo de Cambio, ya existe un asiento por
     * Tipo de Cambio"
     */
    @Test
    public void Test_cambiarTC_01() throws ExcepcionNegocio {

        excepcion.expectMessage(Mensaje.ASIENTO_NO_SE_PUEDO_ACTUALIZAR);

        asientoServicio.cambiarTC(BigDecimal.ZERO);
    }

    /**
     * metodo cambiarTC(). Cuando el monto enviado sea cero mostrar "El monto
     * ingresado debe ser mayor a cero"
     */
    @Test
    public void Test_cambiarTC_02() throws ExcepcionNegocio {

        when(asientoDAO.contar(Asiento.class, consulta)).thenReturn(0);
        excepcion.expectMessage(Mensaje.ASIENTO_NUMERO_MAYOR_CERO);

        asientoServicio.cambiarTC(BigDecimal.ZERO);
    }

    /**
     * metodo cambiarTC(). cuando no existe el periodo anterior no existe
     * mostrar "Periodo no existe"
     */
    @Test
    public void Test_cambiarTC_03() throws ExcepcionNegocio {
        PeriodoDTO periodoDTO = new PeriodoDTO();
        periodoDTO.setPeriodoAnteriorId(2);

        when(utilitarioServicio.traerPeriodoVigente()).thenReturn(periodoDTO);
        when(periodoDAO.traerPorId(Periodo.class, periodoDTO.getPeriodoAnteriorId())).thenReturn(null);
        when(asientoDAO.contar(Asiento.class, consulta)).thenReturn(0);
        excepcion.expectMessage(Mensaje.ASIENTO_PERIODO_NO_EXISTE);

        asientoServicio.cambiarTC(BigDecimal.ONE);
    }

    /**
     * metodo cambiarTC(). si el tipo de cambio es igual al anterior mostrar "El
     * nuevo tipo de cambio es igual al anterior"
     */
    @Test
    public void Test_cambiarTC_04() throws ExcepcionNegocio {
        Periodo periodo = new Periodo();
        periodo.setTcFijo(BigDecimal.TEN);

        PeriodoDTO periodoDTO = new PeriodoDTO();
        periodoDTO.setPeriodoAnteriorId(2);

        when(utilitarioServicio.traerPeriodoVigente()).thenReturn(periodoDTO);
        when(periodoDAO.traerPorId(Periodo.class, periodoDTO.getPeriodoAnteriorId())).thenReturn(periodo);
        when(asientoDAO.contar(Asiento.class, consulta)).thenReturn(0);
        excepcion.expectMessage(Mensaje.CIERRE_NUEVO_TC_IGUAL);

        asientoServicio.cambiarTC(BigDecimal.TEN);
    }

    /**
     * metodo cambiarTC().
     *
     */
    @Test
    public void Test_cambiarTC_05() throws ExcepcionNegocio {

        Periodo periodo = new Periodo();
        periodo.setTcFijo(new BigDecimal("3.12"));

        Consulta consulta1 = new Consulta();
        consulta1.agregaAlias(new Alias("periodo", "periodo"));
        consulta1.agregaAlias(new Alias("moneda", "moneda"));
        consulta1.agregaAlias(new Alias("cuenta", "cuenta"));

        consulta1.agregaRestriccionIgual(new RestriccionIgual("periodo.id", 2));
        consulta1.agregaRestriccionIgual(new RestriccionIgual("moneda.id", MonedaEnum.DOLARES.ordinal()));
        consulta1.agregaRestriccionIgual(new RestriccionIgual("cuenta.esAnalitica", "S"));

        consulta1.agregaRestriccionNoIgual(new RestriccionNoIgual("saldoFinal", BigDecimal.ZERO));
        consulta1.agregaRestriccionSql("({alias}.totalDebe>0 or {alias}.totalHaber>0)");

        consulta1.agregaEquivalencia(new Equivalencia("saldoFinal", "saldoFinal"));
        consulta1.agregaEquivalencia(new Equivalencia("cuenta.id", "cuentaId"));
        consulta1.agregaEquivalencia(new Equivalencia("cuenta.tipoCuenta.id", "tipoCuentaId"));
        consulta1.agregaEquivalencia(new Equivalencia("cuenta.cuenta", "cuenta"));

        List<SaldoDTO> saldos = new ArrayList<>();
        SaldoDTO saldo1 = new SaldoDTO();
        saldo1.setSaldoFinal(new BigDecimal("100.50"));
        saldo1.setTipoCuentaId(TipoCuentaEnum.ACREEDOR.ordinal());
        saldo1.setCuentaId(10);

        SaldoDTO saldo2 = new SaldoDTO();
        saldo2.setSaldoFinal(new BigDecimal("200.50"));
        saldo2.setTipoCuentaId(TipoCuentaEnum.ACREEDOR.ordinal());
        saldo2.setCuentaId(11);

        SaldoDTO saldo3 = new SaldoDTO();
        saldo3.setSaldoFinal(new BigDecimal("26.58"));
        saldo3.setTipoCuentaId(TipoCuentaEnum.ACREEDOR.ordinal());
        saldo3.setCuentaId(12);

        saldos.add(saldo1);
        saldos.add(saldo2);
        saldos.add(saldo3);

        List<TipoAsientoDTO> tiposAsiento = new ArrayList<>();
        TipoAsientoDTO tipo1 = new TipoAsientoDTO();
        tipo1.setId(11);
        tipo1.setGlosa("GLOSA DE PRUEBA");

        TipoAsientoDTO tipo2 = new TipoAsientoDTO();
        tipo2.setId(12);
        tipo2.setGlosa("ASIENTO DE ACTUALIZACIÓN MONEDA EXTRANJERA");

        TipoAsientoDTO tipo3 = new TipoAsientoDTO();
        tipo3.setId(30);
        tipo3.setGlosa("ASIENTO POR DIFERENCIA DE CAMBIO");

        tiposAsiento.add(tipo1);
        tiposAsiento.add(tipo2);

        when(periodoDAO.traerPorId(Periodo.class, 2)).thenReturn(periodo);
        when(periodoDAO.traerPorId(Periodo.class, 3)).thenReturn(new Periodo());
        when(saldoDAO.traerTodo(Saldo.class, consulta1, SaldoDTO.class)).thenReturn(saldos);
        when(utilitarioServicio.traerListaTipoAsiento()).thenReturn(tiposAsiento);
        when(utilitarioServicio.traerCuentaIngreso()).thenReturn("5108040999");
        when(utilitarioServicio.traerCuentaEgresos()).thenReturn("4108040999");
        when(cuentaServicio.traerCuentaId("5108040999")).thenReturn(1);
        when(cuentaServicio.traerCuentaId("4108040999")).thenReturn(2);
        when(utilitarioServicio.traerTipoAsientoActualizacionME()).thenReturn(12);
        when(asientoDAO.contar(Asiento.class, consulta)).thenReturn(0);

        asientoServicio.cambiarTC(new BigDecimal("3.25"));
    }

    /**
     * metodo: traerNumeroAsientos()
     *
     */
    @Test
    public void test_traerNumeroAsientos_01() throws ExcepcionNegocio {
        AsientoDTO asiento1 = new AsientoDTO();
        asiento1.setNumero(11042);

        AsientoDTO asiento2 = new AsientoDTO();
        asiento2.setNumero(45260);

        List<AsientoDTO> asientos = new ArrayList<>();
        asientos.add(asiento1);
        asientos.add(asiento2);

        List<Integer> asientosId = new ArrayList<>();
        asientosId.add(new Integer("11042"));
        asientosId.add(new Integer("45260"));

        Consulta consulta1 = new Consulta();
        consulta1.agregaEquivalencia(new Equivalencia("numero", "numero"));
        consulta1.agregaRestriccionSql("({alias}.id=11042 or {alias}.id=45260)");

        when(asientoDAO.traerTodo(Asiento.class, consulta1, AsientoDTO.class)).thenReturn(asientos);

        List<Integer> listaAsientos = asientoServicio.traerNumeroAsientos(asientosId);
        Assert.assertEquals(2, listaAsientos.size());
    }

    /**
     * metodo guardar() cuando las cuentas contienen las monedas si encuentra un
     * moneda invalida mostrar: "Asiento contiene moneda inválida"
     */
    @Test
    public void test_guardar_26() throws ExcepcionNegocio {

        asientoDTO.setDetalles(listaDetallesExcel);

        excepcion.expectMessage(Mensaje.INTEGRACION_SYSONE_MONEDA_INVALIDA);
        asientoServicio.guardar(asientoDTO, ProcedenciaEnum.CONTABILIDAD, usuarioId);
    }

    /**
     * metodo guardar() cuando el debe y haber TOTAL no cuadra mostrar Debe y
     * haber no cuadra
     */
    @Test
    public void test_guardar_27() throws ExcepcionNegocio {

        detalleExcel.setCuentaCuenta("1112");

        listaDetallesExcel.set(0, detalleExcel);

        DetalleDTO nuevoDetalle = new DetalleDTO();
        nuevoDetalle.setCuentaCuenta("112302");
        nuevoDetalle.setDebe(BigDecimal.ZERO);
        nuevoDetalle.setHaber(new BigDecimal("13"));

        listaDetallesExcel.add(nuevoDetalle);

        asientoDTO.setDetalles(listaDetallesExcel);

        excepcion.expectMessage(Mensaje.ASIENTO_DEBE_HABER_NO_CUADRA);

        asientoServicio.guardar(asientoDTO, ProcedenciaEnum.CONTABILIDAD, usuarioId);
    }

    /**
     * metodo guardar() cuando el debe y el haber no cuadra y el tipo de asiento
     * es diferente a ASIENTO POR DIFERENCIA DE CAMBIO (id=30) mostrar Debe y
     * haber no cuadra
     *
     */
    @Test
    public void test_guardar_28() throws ExcepcionNegocio {

        detalleExcel.setCuentaCuenta("1112");
        detalleExcel.setId(1);
        detalleExcel.setDebe(new BigDecimal("10.01"));

        CuentaBatchDTO cuenta = new CuentaBatchDTO();
        cuenta.setCuenta("1102");
        cuenta.setEsAnalitica("S");
        cuenta.setEstadoId(EstadoEnum.ACTIVO.ordinal());

        listaDetallesExcel.set(0, detalleExcel);

        DetalleDTO nuevoDetalle = new DetalleDTO();
        nuevoDetalle.setCuentaCuenta("112302");
        nuevoDetalle.setDebe(new BigDecimal("13"));
        nuevoDetalle.setHaber(new BigDecimal("13.05"));

        CuentaBatchDTO cuenta2 = new CuentaBatchDTO();
        cuenta2.setCuenta("110302");
        cuenta2.setEsAnalitica("S");
        cuenta2.setEstadoId(EstadoEnum.ACTIVO.ordinal());

        listaDetallesExcel.add(nuevoDetalle);

        asientoDTO.setDetalles(listaDetallesExcel);
        asientoDTO.setTipoAsientoId(11);

        when(utilitarioServicio.traerListaTipoAsiento()).thenReturn(tiposAsiento);
        when(tipoAsientoDAO.traerPorId(TipoAsiento.class, 11)).thenReturn(new TipoAsiento());

        when(repositorioBaseBatchDao.traerCuentaPorId(1)).thenReturn(cuenta);
        when(repositorioBaseBatchDao.traerCuentaPorId(2)).thenReturn(cuenta2);

        when(cuentaServicio.traerCuentaId("1102")).thenReturn(1);
        when(cuentaServicio.traerCuentaId("110302")).thenReturn(2);
        when(utilitarioServicio.traerListaAgencia()).thenReturn(listaAgencias);

        excepcion.expectMessage(Mensaje.ASIENTO_DEBE_HABER_NO_CUADRA);

        asientoServicio.guardar(asientoDTO, ProcedenciaEnum.CONTABILIDAD, usuarioId);
    }

    /**
     * metodo guardar() cuando el debe y el haber no cuadra y el tipo de asiento
     * es diferente a ASIENTO POR DIFERENCIA DE CAMBIO (id=30) mostrar Debe y
     * haber no cuadra
     *
     */
    @Test
    public void test_guardar_29() throws ExcepcionNegocio {

        detalleExcel.setCuentaCuenta("1112");
        detalleExcel.setId(1);
        detalleExcel.setDebe(new BigDecimal("10.01"));

        CuentaBatchDTO cuenta = new CuentaBatchDTO();
        cuenta.setCuenta("1102");
        cuenta.setEsAnalitica("S");
        cuenta.setEstadoId(EstadoEnum.ACTIVO.ordinal());

        listaDetallesExcel.set(0, detalleExcel);

        DetalleDTO nuevoDetalle = new DetalleDTO();
        nuevoDetalle.setCuentaCuenta("112302");
        nuevoDetalle.setDebe(new BigDecimal("13"));
        nuevoDetalle.setHaber(new BigDecimal("13.01"));

        CuentaBatchDTO cuenta2 = new CuentaBatchDTO();
        cuenta2.setCuenta("110302");
        cuenta2.setEsAnalitica("S");
        cuenta2.setEstadoId(EstadoEnum.ACTIVO.ordinal());

        listaDetallesExcel.add(nuevoDetalle);

        asientoDTO.setDetalles(listaDetallesExcel);
        asientoDTO.setTipoAsientoId(30);

        Periodo periodo = new Periodo();
        periodo.setUltimoAsiento(15);

        when(utilitarioServicio.traerListaTipoAsiento()).thenReturn(tiposAsiento);
        when(tipoAsientoDAO.traerPorId(TipoAsiento.class, 30)).thenReturn(new TipoAsiento());
        when(repositorioBaseBatchDao.traerCuentaPorId(1)).thenReturn(cuenta);
        when(repositorioBaseBatchDao.traerCuentaPorId(2)).thenReturn(cuenta2);
        when(cuentaServicio.traerCuentaId("1102")).thenReturn(1);
        when(cuentaServicio.traerCuentaId("110302")).thenReturn(2);
        when(periodoDAO.traerPorId(Periodo.class, 3)).thenReturn(periodo);
        when(monedaDAO.traerPorId(Moneda.class, 2)).thenReturn(new Moneda());
        when(utilitarioServicio.traerListaAgencia()).thenReturn(listaAgencias);

        excepcion.expectMessage(Mensaje.OPERACION_CORRECTA + ". Asientos generados : 16");

        asientoServicio.guardar(asientoDTO, ProcedenciaEnum.CONTABILIDAD, usuarioId);
    }

}
