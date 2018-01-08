/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.servicio.test;

import java.util.ArrayList;
import java.util.List;
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
import pe.cajapaita.backerp.contabilidad.dto.CuentaDTO;
import pe.cajapaita.backerp.contabilidad.entidad.Cuenta;
import pe.cajapaita.backerp.contabilidad.consulta.Alias;
import pe.cajapaita.backerp.contabilidad.consulta.Equivalencia;
import pe.cajapaita.backerp.contabilidad.infraestructura.EstadoEnum;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;
import pe.cajapaita.backerp.contabilidad.infraestructura.Mensaje;
import pe.cajapaita.backerp.contabilidad.consulta.Consulta;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionIgual;
import pe.cajapaita.backerp.contabilidad.servicio.IAsientoServicio;
import pe.cajapaita.backerp.contabilidad.servicio.IUtilitarioServicio;
import pe.cajapaita.backerp.contabilidad.servicio.impl.CuentaServicioImpl;


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
public class CuentaServicioTest {

    private CuentaDTO cuentaDTO;
    private int usuarioId;

    @InjectMocks
    private CuentaServicioImpl cuentaServicio;

    @Mock
    private IUtilitarioServicio utilitarioServicio;

    @Mock
    private IAsientoServicio asientoServicioFalso;

    @Mock
    private IRepositorioBaseDao<Cuenta> repositorioCuentaFalso;

    @Rule
    public final ExpectedException excepcion = ExpectedException.none();

    @Before
    public void inicio() {
        MockitoAnnotations.initMocks(this);
        when(utilitarioServicio.traerRegistrosPorPagina()).thenReturn(10);
        cuentaDTO = new CuentaDTO();
        usuarioId=1;
    }

    /**
     * metodo: traerPorCuenta(). Si no existen cuentas. Mensaje "No existen
     * cuentas"
     */
    @Test
    public void test_traerPorCuenta_01() throws ExcepcionNegocio {
        //Arrange 
        List<CuentaDTO> cuentasAsignadas = new ArrayList<>();
        String cuentaBuscar = "1101";
        when(repositorioCuentaFalso.traerTodo(Cuenta.class, new Consulta(), CuentaDTO.class)).thenReturn(cuentasAsignadas);

        //Assert                
        excepcion.expectMessage("No existen cuentas");

        // Action       
        cuentaServicio.traerPorCuenta(cuentaBuscar, 1);
    }

    /**
     * metodo: traerPorCuenta(). Si no esta configurado registrosPorpagina.
     * Mensaje "Variables no han sido cargadas. Comunicarse con TI"
     */
    @Test
    public void test_traerPorCuenta_02() throws ExcepcionNegocio {
        // Arrange
        when(utilitarioServicio.traerRegistrosPorPagina()).thenReturn(0);

        //Assert                
        excepcion.expectMessage("Variables no han sido cargadas. Comunicarse con TI");

        // Action       
        cuentaServicio.traerPorCuenta("1101", 1);
    }

    /**
     * metodo: traerHijas(). Si no existen cuentas. Mensaje "No existen cuentas"
     */
    @Test
    public void test_traerHijas_01() throws ExcepcionNegocio {
        //Arrange
        List<CuentaDTO> cuentasAsignadas = new ArrayList<>();
        when(repositorioCuentaFalso.traerTodo(Cuenta.class, new Consulta(), CuentaDTO.class)).thenReturn(cuentasAsignadas);

        //Assert                
        excepcion.expectMessage("No existen cuentas");

        // Action
        cuentaServicio.traerHijas(1, 1);
    }

    /**
     * metodo: traerHijas(). Si no esta configurado registrosPorpagina. Mensaje
     * "Variables no han sido cargadas. Comunicarse con TI"
     */
    @Test
    public void test_traerHijas_02() throws ExcepcionNegocio {
        // Arrange
        when(utilitarioServicio.traerRegistrosPorPagina()).thenReturn(0);

        //Assert                
        excepcion.expectMessage("Variables no han sido cargadas. Comunicarse con TI");

        // Action
        cuentaServicio.traerHijas(1, 1);
    }

    /**
     * metodo: traerPrimerNivel(). Si no existen cuentas. Mensaje "No existen
     * cuentas"
     */
    @Test
    public void test_traerPrimerNivel_01() throws ExcepcionNegocio {
        //Arrange
        List<CuentaDTO> cuentasAsignadas = new ArrayList<>();
        when(repositorioCuentaFalso.traerTodo(Cuenta.class, new Consulta(), CuentaDTO.class)).thenReturn(cuentasAsignadas);

        //Assert                
        excepcion.expectMessage("No existen cuentas");

        // Action
        cuentaServicio.traerPrimerNivel();
    }

    /**
     * metodo: guardarCuenta(). Cuando cuenta es nula. Mensaje "Cuenta contable
     * vacia"
     */
    @Test
    public void test_guardarCuenta_01() throws ExcepcionNegocio {
        //Arrange
        cuentaDTO.setCuenta(null);

        //Assert
        excepcion.expectMessage(Mensaje.CUENTA_CONTABLE_VACIA);

        // Action
        cuentaServicio.guardar(cuentaDTO,usuarioId);
    }

    /**
     * metodo: guardarCuenta(). Cuando cuenta esta vacía. Mensaje "Cuenta
     * contable vacía"
     */
    @Test
    public void test_guardarCuenta_02() throws ExcepcionNegocio {
        //Arrange
        cuentaDTO.setCuenta("");

        //Assert
        excepcion.expectMessage(Mensaje.CUENTA_CONTABLE_VACIA);

        // Action
        cuentaServicio.guardar(cuentaDTO,usuarioId);
    }

    /**
     * metodo: guardarCuenta(). Cuando cuenta no son dígitos. Mensaje "Ingresar
     * sólo dígitos en cuenta contable"
     */
    @Test
    public void test_guardarCuenta_03() throws ExcepcionNegocio {
        //Arrange
        cuentaDTO.setCuenta("10A01");

        //Assert
        excepcion.expectMessage(Mensaje.CUENTA_CONTABLE_SOLO_DIGITOS);

        // Action
        cuentaServicio.guardar(cuentaDTO, usuarioId);
    }

    /**
     * metodo: guardarCuenta(). Cuando cuenta es impar. Mensaje "Sólo se acepta
     * cuentas pares"
     */
    @Test
    public void test_guardarCuenta_04() throws ExcepcionNegocio {
        //Arrange
        cuentaDTO.setCuenta("101");

        //Assert
        excepcion.expectMessage(Mensaje.CUENTA_CONTABLE_SOLO_PARES);

        // Action
        cuentaServicio.guardar(cuentaDTO, usuarioId);
    }

    /**
     * metodo: guardarCuenta(). Cuando tercer digito es distinto de cero
     * mostrar. Mensaje "Tercer dígito de cuenta debe ser cero"
     */
    @Test
    public void test_guardarCuenta_05() throws ExcepcionNegocio {
        //Arrange
        cuentaDTO.setCuenta("1111");

        //Assert
        excepcion.expectMessage(Mensaje.CUENTA_CONTABLE_TERCER_DIGITO_CERO);

        // Action
        cuentaServicio.guardar(cuentaDTO, usuarioId);
    }

    /**
     * metodo: guardarCuenta(). Cuando descripcion de cuenta es nula. Mensaje
     * "Descripción de cuenta vacía"
     */
    @Test
    public void test_guardarCuenta_06() throws ExcepcionNegocio {
        //Arrange
        cuentaDTO.setCuenta("1101");
        cuentaDTO.setDescripcion(null);

        //Assert
        excepcion.expectMessage(Mensaje.CUENTA_DESCRIPCION_VACIA);

        // Action
        cuentaServicio.guardar(cuentaDTO, usuarioId);
    }

    /**
     * metodo: guardarCuenta(). Cuando descripcion de cuenta es vacia. Mensaje
     * "Descripción de cuenta vacía"
     */
    @Test
    public void test_guardarCuenta_07() throws ExcepcionNegocio {
        //Arrange
        cuentaDTO.setCuenta("1101");
        cuentaDTO.setDescripcion("");

        //Assert
        excepcion.expectMessage(Mensaje.CUENTA_DESCRIPCION_VACIA);

        // Action
        cuentaServicio.guardar(cuentaDTO, usuarioId);
    }

    /**
     * metodo: guardarCuenta(). Cuando cuenta contable exista. Mensaje "Cuenta
     * contable ya existe"
     */
    @Test
    public void test_guardarCuenta_08() throws ExcepcionNegocio {
        //Arrange
        cuentaDTO.setCuenta("1101");
        cuentaDTO.setDescripcion("Mi Cuenta");
        Consulta parametro = preparaParametros();
        parametro.agregaRestriccionIgual(new RestriccionIgual("cuenta", cuentaDTO.getCuenta()));
        when(repositorioCuentaFalso.contar(Cuenta.class, parametro)).thenReturn(1);

        //Assert
        excepcion.expectMessage(Mensaje.CUENTA_CONTABLE_EXISTE);

        // Action
        cuentaServicio.guardar(cuentaDTO, usuarioId);
    }

    /**
     * metodo: guardarCuenta(). Cuando cuenta padre no existe. Mensaje "Cuenta
     * superior no existe"
     */
    @Test
    public void test_guardarCuenta_09() throws ExcepcionNegocio {
        //Arrange  
        cuentaDTO.setId(1);
        cuentaDTO.setCuenta("1101");
        cuentaDTO.setDescripcion("Mi Cuenta");

        Consulta parametro = preparaParametros();
        parametro.agregaRestriccionIgual(new RestriccionIgual("cuenta", cuentaDTO.getCuenta()));
        when(repositorioCuentaFalso.contar(Cuenta.class, parametro)).thenReturn(0);

        when(repositorioCuentaFalso.traerPorId(Cuenta.class, cuentaDTO.getId())).thenReturn(null);

        //Assert
        excepcion.expectMessage(Mensaje.CUENTA_PADRE_NO_EXISTE);

        // Action
        cuentaServicio.guardar(cuentaDTO, usuarioId);
    }

    /**
     * metodo: guardarCuenta(). Si cuenta hija es 1101 y cuenta padre es 10.
     * Mensaje "Cuenta contable no coincide con cuenta superior"
     */
    @Test
    public void test_guardarCuenta_10() throws ExcepcionNegocio {
        //Arrange
        Cuenta cuentaEntidad = new Cuenta();
        cuentaEntidad.setCuenta("10");

        cuentaDTO.setCuenta("1101");
        cuentaDTO.setDescripcion("Mi Cuenta");
        cuentaDTO.setPadreId(1);

        Consulta parametro = preparaParametros();
        parametro.agregaRestriccionIgual(new RestriccionIgual("cuenta", cuentaDTO.getCuenta()));
        when(repositorioCuentaFalso.contar(Cuenta.class, parametro)).thenReturn(0);

        when(repositorioCuentaFalso.traerPorId(Cuenta.class, cuentaDTO.getPadreId())).thenReturn(cuentaEntidad);

        //Assert
        excepcion.expectMessage(Mensaje.CUENTA_NO_COINCIDE_CON_PADRE);

        // Action
        cuentaServicio.guardar(cuentaDTO, usuarioId);
    }

    /**
     * metodo: guardarCuenta(). Si cuenta hija es 11 y cuenta padre es 2.
     * Mensaje "Cuenta contable no coincide con cuenta superior"
     */
    @Test
    public void test_guardarCuenta_11() throws ExcepcionNegocio {
        //Arrange
        Cuenta cuentaEntidad = new Cuenta();
        cuentaEntidad.setCuenta("2");

        cuentaDTO.setCuenta("11");
        cuentaDTO.setDescripcion("Mi Cuenta");
        cuentaDTO.setPadreId(1);

        Consulta parametro = preparaParametros();
        parametro.agregaRestriccionIgual(new RestriccionIgual("cuenta", cuentaDTO.getCuenta()));
        when(repositorioCuentaFalso.contar(Cuenta.class, parametro)).thenReturn(0);

        when(repositorioCuentaFalso.traerPorId(Cuenta.class, cuentaDTO.getPadreId())).thenReturn(cuentaEntidad);

        //Assert
        excepcion.expectMessage(Mensaje.CUENTA_NO_COINCIDE_CON_PADRE);

        // Action
        cuentaServicio.guardar(cuentaDTO, usuarioId);
    }

    /**
     * metodo: eliminar(). Cuando cuenta no existe. Mostrar mensaje "Cuenta no
     * existe"
     */
    @Test
    public void test_eliminar_01() throws ExcepcionNegocio {
        // Arrange     
        int cuentaId = 1;
        when(repositorioCuentaFalso.traerPorId(Cuenta.class, cuentaId)).thenReturn(null);

        //Assert
        excepcion.expectMessage(Mensaje.CUENTA_CONTABLE_NO_EXISTE);

        //Action
        cuentaServicio.eliminar(cuentaId);
    }

    /**
     * metodo: eliminar(). Cuando cuenta es SBS. Mostrar mensaje "Cuenta
     * considerada en Plan SBS"
     */
    @Test
    public void test_eliminar_02() throws ExcepcionNegocio {
        // Arrange
        Cuenta cuentaConsultada = new Cuenta();
        cuentaConsultada.setId(1);
        cuentaConsultada.setEsSbs("S");

        when(repositorioCuentaFalso.traerPorId(Cuenta.class, cuentaConsultada.getId())).thenReturn(cuentaConsultada);

        //Assert
        excepcion.expectMessage(Mensaje.CUENTA_ES_SBS);

        //Action
        cuentaServicio.eliminar(cuentaConsultada.getId());
    }

    /**
     * metodo: eliminar(). Cuando cuenta tiene hijas. Mostrar mensaje "Cuenta no
     * es analítica"
     */
    @Test
    public void test_eliminar_03() throws ExcepcionNegocio {
        // Arrange        
        Cuenta cuentaConsultada = new Cuenta();
        cuentaConsultada.setId(1);
        cuentaConsultada.setEsSbs("N");
        cuentaConsultada.setEsAnalitica("N");

        Consulta parametro = preparaParametros();
        parametro.agregaRestriccionIgual(new RestriccionIgual("padre.id", cuentaConsultada.getId()));

        when(repositorioCuentaFalso.traerPorId(Cuenta.class, cuentaConsultada.getId())).thenReturn(cuentaConsultada);

        //Assert
        excepcion.expectMessage(Mensaje.CUENTA_NO_ES_ANALITICA);

        //Action
        cuentaServicio.eliminar(cuentaConsultada.getId());
    }

    /**
     * metodo: eliminar(). Cuando cuenta tiene movimientos. Mostrar mensaje
     * "Cuenta tiene movimientos"
     */
    @Test
    public void test_eliminar_04() throws ExcepcionNegocio {
        // Arrange
        Cuenta cuentaConsultada = new Cuenta();
        cuentaConsultada.setId(1);
        cuentaConsultada.setEsSbs("N");
        cuentaConsultada.setEsAnalitica("S");

        Consulta parametroCuenta = preparaParametros();
        parametroCuenta.agregaRestriccionIgual(new RestriccionIgual("padre.id", cuentaConsultada.getId()));

        when(repositorioCuentaFalso.traerPorId(Cuenta.class, cuentaConsultada.getId())).thenReturn(cuentaConsultada);
        when(asientoServicioFalso.cuentaTieneMovimientos(cuentaConsultada.getId())).thenReturn(true);

        //Assert
        excepcion.expectMessage(Mensaje.CUENTA_TIENE_MOVIMIENTOS);

        //Action
        cuentaServicio.eliminar(cuentaConsultada.getId());
    }

    /**
     * metodo: actualizar(). Cuando descripcion de cuenta es nula. Mensaje
     * "Descripción de cuenta vacía"
     */
    @Test
    public void test_actualizar_01() throws ExcepcionNegocio {
        //Arrange        
        cuentaDTO.setDescripcion(null);

        //Assert
        excepcion.expectMessage(Mensaje.CUENTA_DESCRIPCION_VACIA);

        // Action
        cuentaServicio.actualizar(cuentaDTO);
    }

    /**
     * metodo: actualizar(). Cuando descripcion de cuenta esta vacía. Mensaje
     * "Descripción de cuenta vacía"
     */
    @Test
    public void test_actualizar_02() throws ExcepcionNegocio {
        //Arrange        
        cuentaDTO.setDescripcion("");

        //Assert
        excepcion.expectMessage(Mensaje.CUENTA_DESCRIPCION_VACIA);

        // Action
        cuentaServicio.actualizar(cuentaDTO);
    }

    /**
     * metodo: actualizar(). Cuando cuenta no existe. Mostrar mensaje "Cuenta no
     * existe"
     */
    @Test
    public void test_actualizar_03() throws ExcepcionNegocio {
        // Arrange     
        cuentaDTO.setDescripcion("Nueva cuenta");
        int cuentaId = 1;
        when(repositorioCuentaFalso.traerPorId(Cuenta.class, cuentaId)).thenReturn(null);

        //Assert
        excepcion.expectMessage(Mensaje.CUENTA_CONTABLE_NO_EXISTE);

        //Action
        cuentaServicio.actualizar(cuentaDTO);
    }

    private Consulta preparaParametros() {
        Consulta parametro = new Consulta();

        parametro.agregaAlias(new Alias("padre", "padre"));
        parametro.agregaAlias(new Alias("tipoCuenta", "tipoCuenta"));
        parametro.agregaAlias(new Alias("estado", "estado"));

        parametro.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));

        parametro.agregaEquivalencia(new Equivalencia("id", "id"));
        parametro.agregaEquivalencia(new Equivalencia("cuenta", "cuenta"));
        parametro.agregaEquivalencia(new Equivalencia("descripcion", "descripcion"));
        parametro.agregaEquivalencia(new Equivalencia("esSbs", "esSbs"));
        parametro.agregaEquivalencia(new Equivalencia("esAnalitica", "esAnalitica"));
        parametro.agregaEquivalencia(new Equivalencia("tipoCuenta.descripcion", "tipoCuenta"));
        parametro.agregaEquivalencia(new Equivalencia("padre.id", "padreId"));

        return parametro;
    }
}
