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
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionBetweenDate;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionIgual;
import pe.cajapaita.backerp.contabilidad.dao.IRepositorioBaseDao;
import pe.cajapaita.backerp.contabilidad.dto.ArchivoSIAFCDTO;
import pe.cajapaita.backerp.contabilidad.dto.AsientoDTO;
import pe.cajapaita.backerp.contabilidad.dto.DetalleSIAFCDTO;
import pe.cajapaita.backerp.contabilidad.dto.MonedaDTO;
import pe.cajapaita.backerp.contabilidad.dto.PeriodoDTO;
import pe.cajapaita.backerp.contabilidad.dto.TipoAsientoDTO;
import pe.cajapaita.backerp.contabilidad.entidad.Asiento;
import pe.cajapaita.backerp.contabilidad.infraestructura.EstadoEnum;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;
import pe.cajapaita.backerp.contabilidad.infraestructura.Helper;
import pe.cajapaita.backerp.contabilidad.infraestructura.Mensaje;
import pe.cajapaita.backerp.contabilidad.servicio.IAsientoServicio;
import pe.cajapaita.backerp.contabilidad.servicio.ICuentaServicio;
import pe.cajapaita.backerp.contabilidad.servicio.IUtilitarioServicio;
import pe.cajapaita.backerp.contabilidad.servicio.IWebServicio;
import pe.cajapaita.backerp.contabilidad.servicio.impl.IntegracionSiafcServicioImpl;

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
public class IntegracionSiafcServicioTest {

    @InjectMocks
    private IntegracionSiafcServicioImpl integracionSiafcServicio;

    @Mock
    private IWebServicio webServicio;

    @Mock
    private IUtilitarioServicio utilitarioServicio;

    @Mock
    private ICuentaServicio cuentaServicio;

    @Mock
    private IRepositorioBaseDao<Asiento> asientoDAO;
    
    @Mock
    private IAsientoServicio asientoServicio;
    
    private int usuarioId;

    @Rule
    public final ExpectedException excepcion = ExpectedException.none();

    @Before
    public void inicio() throws ExcepcionNegocio {
        MockitoAnnotations.initMocks(this);
        usuarioId = 1;

        List<AsientoDTO> asientosEnBase = new ArrayList<>();
        
        Consulta consulta = new Consulta();
        consulta.agregaAlias(new Alias("tipoAsiento", "tipoAsiento"));
        consulta.agregaAlias(new Alias("estado", "estado"));
        consulta.agregaEquivalencia(new Equivalencia("numero", "numero"));
        consulta.agregaRestriccionBetweenDate(new RestriccionBetweenDate("fecha", Helper.convertirAFecha("01/06/2016"), Helper.convertirAFecha("01/06/2016")));
        consulta.agregaRestriccionIgual(new RestriccionIgual("tipoAsiento.id",1));
        consulta.agregaRestriccionIgual(new RestriccionIgual("estado.id",  EstadoEnum.ACTIVO.ordinal()));
        
       when( asientoDAO.traerTodo(Asiento.class, consulta, AsientoDTO.class)).thenReturn(asientosEnBase);

    }

    /**
     * metodo listaArchivosSIAFC() si la carpeta no tiene archivos dbf mostrar
     * "No se encontraron archivos disponibles"
     */
    @Test
    public void test_listaArchivosSIAFC_01() throws ExcepcionNegocio {
        List<String> listaArchivos = new ArrayList<>();

        List<TipoAsientoDTO> listaAsiento = new ArrayList<>();
        TipoAsientoDTO tipoAsiento = new TipoAsientoDTO();
        tipoAsiento.setId(1);
        tipoAsiento.setModulo("G");
        tipoAsiento.setProcedenciaId(2);

        TipoAsientoDTO tipoAsiento2 = new TipoAsientoDTO();
        tipoAsiento2.setId(1);
        tipoAsiento2.setModulo("B");
        tipoAsiento2.setProcedenciaId(2);

        listaAsiento.add(tipoAsiento);
        listaAsiento.add(tipoAsiento2);

        when(utilitarioServicio.traerListaTipoAsientoSiafc()).thenReturn(listaAsiento);
        when(webServicio.traerArchivosSiafc()).thenReturn(listaArchivos);

        excepcion.expectMessage(Mensaje.INTEGRACION_SIAFC_ARCHIVOS_DISPONIBLES);

        integracionSiafcServicio.listaArchivosSIAFC();
    }

    /**
     * metodo listaArchivosSIAFC() debe devolver solo los archivos que esten
     * dentro de las categorias En este caso 1
     */
    @Test
    public void test_listaArchivosSIAFC_02() throws ExcepcionNegocio {
        List<String> listaArchivos = new ArrayList<>();
        listaArchivos.add("A0010616");
        listaArchivos.add("G0011016");

        List<TipoAsientoDTO> listaAsiento = new ArrayList<>();
        TipoAsientoDTO tipoAsiento = new TipoAsientoDTO();
        tipoAsiento.setId(1);
        tipoAsiento.setModulo("G");
        tipoAsiento.setProcedenciaId(3);

        TipoAsientoDTO tipoAsiento2 = new TipoAsientoDTO();
        tipoAsiento2.setId(1);
        tipoAsiento2.setModulo("B");
        tipoAsiento2.setProcedenciaId(3);

        listaAsiento.add(tipoAsiento);
        listaAsiento.add(tipoAsiento2);

        when(utilitarioServicio.traerListaTipoAsientoSiafc()).thenReturn(listaAsiento);
        when(webServicio.traerArchivosSiafc()).thenReturn(listaArchivos);

        List<ArchivoSIAFCDTO> respuesta = integracionSiafcServicio.listaArchivosSIAFC();
        Assert.assertEquals(1, respuesta.size());

    }

    /**
     * metodo detalleArchivoSIAFC()
     *
     */
    @Test
    public void test_detalleArchivoSIAFC_01() throws ExcepcionNegocio {
        String nombreArchivo = "G0010116";

        List<DetalleSIAFCDTO> listaDetalle = new ArrayList<>();

        DetalleSIAFCDTO detalle1 = new DetalleSIAFCDTO();
        DetalleSIAFCDTO detalle2 = new DetalleSIAFCDTO();
        listaDetalle.add(detalle1);
        listaDetalle.add(detalle2);

        when(webServicio.traerDetalleArchivoSiafc(nombreArchivo)).thenReturn(listaDetalle);
        List<DetalleSIAFCDTO> respuesta = integracionSiafcServicio.detalleArchivoSIAFC(nombreArchivo);
        Assert.assertEquals(2, respuesta.size());

    }

    @Test
    public void test_integrarArchivosSIAFC_01() throws ExcepcionNegocio {
        String nombreArchivo1 = "A0010116";
        String nombreArchivo2 = "G0040416";

        List<ArchivoSIAFCDTO> miDetalle = new ArrayList<>();

        ArchivoSIAFCDTO archivo1 = new ArchivoSIAFCDTO();
        archivo1.setNombre("A0010116");
        archivo1.setTipoAsientoId(0);

        ArchivoSIAFCDTO archivo2 = new ArchivoSIAFCDTO();
        archivo2.setNombre("G0040416");
        archivo2.setTipoAsientoId(1);

        ArchivoSIAFCDTO archivo3 = new ArchivoSIAFCDTO();
        archivo3.setNombre("B0040416");
        archivo3.setTipoAsientoId(2);

        miDetalle.add(archivo1);
        miDetalle.add(archivo2);

        List<DetalleSIAFCDTO> listaDetalle = new ArrayList<>();

        DetalleSIAFCDTO detalle1 = new DetalleSIAFCDTO();
        detalle1.setCuentaCuenta("111101");
        detalle1.setFechaString("01/06/2016");
        detalle1.setMonedaId(1);
        detalle1.setOficina("001");
        detalle1.setDebe(new BigDecimal(20));
        detalle1.setHaber(new BigDecimal(100));

        DetalleSIAFCDTO detalle2 = new DetalleSIAFCDTO();
        detalle2.setCuentaCuenta("111101");
        detalle2.setFechaString("01/06/2016");
        detalle2.setMonedaId(1);
        detalle2.setOficina("001");
        detalle2.setDebe(new BigDecimal(100));
        detalle2.setHaber(new BigDecimal(20));

        listaDetalle.add(detalle1);
        listaDetalle.add(detalle2);

        List<TipoAsientoDTO> listaAsiento = new ArrayList<>();
        TipoAsientoDTO tipoAsiento = new TipoAsientoDTO();
        tipoAsiento.setId(1);
        tipoAsiento.setModulo("G");
        tipoAsiento.setGlosa("Archivo con la letra G");
        tipoAsiento.setProcedenciaId(3);

        TipoAsientoDTO tipoAsiento2 = new TipoAsientoDTO();
        tipoAsiento2.setId(2);
        tipoAsiento2.setModulo("B");
        tipoAsiento2.setProcedenciaId(3);
        tipoAsiento.setGlosa("Archivo con la letra B");

        listaAsiento.add(tipoAsiento);
        listaAsiento.add(tipoAsiento2);

        List<MonedaDTO> listaMoneda = new ArrayList<>();

        MonedaDTO moneda1 = new MonedaDTO();
        moneda1.setId(1);
        MonedaDTO moneda2 = new MonedaDTO();
        moneda2.setId(2);

        listaMoneda.add(moneda1);
        listaMoneda.add(moneda2);
        
        PeriodoDTO periodoDTO= new PeriodoDTO();
        periodoDTO.setFechaInicial(Helper.convertirAFecha("01/06/2016"));
        periodoDTO.setFechaFinal(Helper.convertirAFecha("30/06/2016"));

        when(utilitarioServicio.traerListaTipoAsientoSiafc()).thenReturn(listaAsiento);
        when(webServicio.traerDetalleArchivoSiafc(nombreArchivo1)).thenReturn(listaDetalle);
        when(webServicio.traerDetalleArchivoSiafc(nombreArchivo2)).thenReturn(listaDetalle);
        when(utilitarioServicio.traerListaMonedaActiva()).thenReturn(listaMoneda);
        when(cuentaServicio.traerCuentaId("110101")).thenReturn(1);
        when(utilitarioServicio.traerListaTipoAsiento()).thenReturn(listaAsiento);
        when(utilitarioServicio.traerPeriodoVigente()).thenReturn(periodoDTO);
        integracionSiafcServicio.integrarArchivosSIAFC(miDetalle, usuarioId);
    }
}
