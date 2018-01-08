/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.servicio.test;

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
import pe.cajapaita.backerp.contabilidad.consulta.Consulta;
import pe.cajapaita.backerp.contabilidad.consulta.Equivalencia;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionIgual;
import pe.cajapaita.backerp.contabilidad.dao.IRepositorioBaseDao;
import pe.cajapaita.backerp.contabilidad.dto.GrupoDTO;
import pe.cajapaita.backerp.contabilidad.dto.MonedaDTO;
import pe.cajapaita.backerp.contabilidad.dto.PeriodoDTO;
import pe.cajapaita.backerp.contabilidad.dto.ReporteDTO;
import pe.cajapaita.backerp.contabilidad.entidad.Detalle;
import pe.cajapaita.backerp.contabilidad.entidad.Detallegrupo;
import pe.cajapaita.backerp.contabilidad.entidad.Grupo;
import pe.cajapaita.backerp.contabilidad.entidad.GrupoEncaje;
import pe.cajapaita.backerp.contabilidad.entidad.Periodo;
import pe.cajapaita.backerp.contabilidad.entidad.Saldo;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;
import pe.cajapaita.backerp.contabilidad.infraestructura.Helper;
import pe.cajapaita.backerp.contabilidad.infraestructura.Mensaje;
import pe.cajapaita.backerp.contabilidad.servicio.IUtilitarioServicio;
import pe.cajapaita.backerp.contabilidad.servicio.impl.ReporteServicioImpl;

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
public class ReporteServicioTest {

    @InjectMocks
    private ReporteServicioImpl reporteServicio;

    @Mock
    private IUtilitarioServicio utilitarioServicio;

    @Mock
    private IRepositorioBaseDao<Periodo> periodoDAO;

    @Mock
    private IRepositorioBaseDao<Grupo> grupoDAO;
    
    @Mock
    private IRepositorioBaseDao<GrupoEncaje> grupoEncajeDAO;

    @Mock
    private IRepositorioBaseDao<Detallegrupo> detalleGrupoDAO;

    @Mock
    private IRepositorioBaseDao<Saldo> saldoDAO;
    
     @Mock
    private IRepositorioBaseDao<Detalle> detalleDAO;

    @Rule
    public final ExpectedException excepcion = ExpectedException.none();

    private Periodo periodo;

    @Before
    public void inicio() throws ExcepcionNegocio {
        MockitoAnnotations.initMocks(this);
        List<ReporteDTO> listaReportes = new ArrayList<>();
        ReporteDTO reporte1 = new ReporteDTO();
        reporte1.setDescripcion("Mi reporte 1");
        reporte1.setId(1);

        ReporteDTO reporte2 = new ReporteDTO();
        reporte2.setDescripcion("Mi reporte 1");
        reporte2.setId(2);

        listaReportes.add(reporte1);
        listaReportes.add(reporte2);

        periodo = new Periodo();
        periodo.setId(7);

        Consulta consulta = new Consulta();
        consulta.agregaEquivalencia(new Equivalencia("id", "id"));
        consulta.agregaRestriccionIgual(new RestriccionIgual("id", periodo.getId()));

        PeriodoDTO periodoDTO = new PeriodoDTO();
        periodoDTO.setFechaInicial(Helper.convertirAFecha("01/07/2016"));
        periodoDTO.setFechaFinal(Helper.convertirAFecha("30/07/2016"));

        List<MonedaDTO> listaMonedas = new ArrayList<>();
        MonedaDTO monedaDTO1 = new MonedaDTO();
        monedaDTO1.setId(1);

        MonedaDTO monedaDTO2 = new MonedaDTO();
        monedaDTO2.setId(1);

        listaMonedas.add(monedaDTO1);
        listaMonedas.add(monedaDTO2);

        when(utilitarioServicio.traerReportesVarios()).thenReturn(listaReportes);
        when(utilitarioServicio.traerListaReportesEncaje()).thenReturn(listaReportes);
        when(utilitarioServicio.traerPeriodoVigente()).thenReturn(periodoDTO);
        when(utilitarioServicio.traerListaMonedaActiva()).thenReturn(listaMonedas);
        when(periodoDAO.traerUnico(Periodo.class, consulta, PeriodoDTO.class)).thenReturn(periodo);

    }

    /**
     * Si el reporte no existe mostrar "El reporte solicitado no existe"
     */
    @Test
    public void test_generarReporteEstado_01() throws ExcepcionNegocio {
        excepcion.expectMessage(Mensaje.REPORTE_NO_EXISTE);
        reporteServicio.generarReporteEstado(1, 10);
    }

    /**
     * Si no se encuentran detalles del reporte devolver un arreglo vacio
     */
    @Test
    public void test_traerConfiguracionRepote_01() throws ExcepcionNegocio {
        List<GrupoDTO> listaGrupoDTO= new ArrayList<>();
        Consulta consultaGrupo = new Consulta();
        consultaGrupo.agregaRestriccionIgual(new RestriccionIgual("reporte.id", 4));
        consultaGrupo.agregaEquivalencia(new Equivalencia("id", "id"));
        consultaGrupo.agregaEquivalencia(new Equivalencia("descripcion", "descripcion"));
        consultaGrupo.agregaEquivalencia(new Equivalencia("rango", "rango"));
        
        when(grupoDAO.traerTodo(Grupo.class, consultaGrupo, GrupoDTO.class)).thenReturn(listaGrupoDTO);
        List<GrupoDTO>listaEsperada=reporteServicio.traerConfiguracionRepote(4);
        Assert.assertEquals(0, listaEsperada.size());
        
    }
    @Test
    public void test_traerConfiguracionRepote_02() throws ExcepcionNegocio{
        List<GrupoDTO> listaGrupoDTO= new ArrayList<>();
        
        GrupoDTO grupoDTO= new GrupoDTO();
        grupoDTO.setId(213);
        grupoDTO.setRango("+213,+215");
       listaGrupoDTO.add(grupoDTO);
        Consulta consultaGrupo = new Consulta();
        consultaGrupo.agregaRestriccionIgual(new RestriccionIgual("reporte.id", 4));
        consultaGrupo.agregaEquivalencia(new Equivalencia("id", "id"));
        consultaGrupo.agregaEquivalencia(new Equivalencia("descripcion", "descripcion"));
        consultaGrupo.agregaEquivalencia(new Equivalencia("rango", "rango"));
        
         when(grupoDAO.traerTodo(Grupo.class, consultaGrupo, GrupoDTO.class)).thenReturn(listaGrupoDTO);
        List<GrupoDTO>listaEsperada=reporteServicio.traerConfiguracionRepote(4);
        Assert.assertEquals("+1, +3", listaEsperada.get(0).getRango());
        
    }

}
