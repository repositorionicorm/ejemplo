/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.dao.test;

import java.math.BigDecimal;
import java.util.List;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import pe.cajapaita.backerp.contabilidad.configuracion.SpringHibernateConfiguracion;
import pe.cajapaita.backerp.contabilidad.configuracion.WebInitializer;
import pe.cajapaita.backerp.contabilidad.dto.CuentaDTO;
import pe.cajapaita.backerp.contabilidad.entidad.Cuenta;
import pe.cajapaita.backerp.contabilidad.entidad.Estado;
import pe.cajapaita.backerp.contabilidad.entidad.Moneda;
import pe.cajapaita.backerp.contabilidad.entidad.Procedencia;
import pe.cajapaita.backerp.contabilidad.entidad.TipoCuenta;
import pe.cajapaita.backerp.contabilidad.dto.EstadoDTO;
import pe.cajapaita.backerp.contabilidad.dto.MonedaDTO;
import pe.cajapaita.backerp.contabilidad.dto.PeriodoDTO;
import pe.cajapaita.backerp.contabilidad.dto.ProcedenciaDTO;
import pe.cajapaita.backerp.contabilidad.dto.TipoAsientoDTO;
import pe.cajapaita.backerp.contabilidad.dto.TipoCuentaDTO;
import pe.cajapaita.backerp.contabilidad.entidad.Periodo;
import pe.cajapaita.backerp.contabilidad.entidad.TipoAsiento;
import pe.cajapaita.backerp.contabilidad.consulta.Alias;
import pe.cajapaita.backerp.contabilidad.consulta.Equivalencia;
import pe.cajapaita.backerp.contabilidad.infraestructura.EstadoEnum;
import pe.cajapaita.backerp.contabilidad.consulta.Consulta;
import pe.cajapaita.backerp.contabilidad.consulta.Grupo;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionBetweenDate;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionIgual;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionLike;
import pe.cajapaita.backerp.contabilidad.dao.IRepositorioBaseDao;
import pe.cajapaita.backerp.contabilidad.dto.AsientoDTO;
import pe.cajapaita.backerp.contabilidad.dto.ParametroDTO;
import pe.cajapaita.backerp.contabilidad.dto.TotalAsientoDTO;
import pe.cajapaita.backerp.contabilidad.entidad.Asiento;
import pe.cajapaita.backerp.contabilidad.entidad.Detalle;
import pe.cajapaita.backerp.contabilidad.entidad.Parametro;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionIn;
import pe.cajapaita.backerp.contabilidad.consulta.Suma;
import pe.cajapaita.backerp.contabilidad.dto.MayorizaDTO;
import pe.cajapaita.backerp.contabilidad.entidad.Saldo;

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
@Transactional
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RepositoriosTest {

    @Autowired
    private IRepositorioBaseDao<Moneda> repositorioMoneda;

    @Autowired
    private IRepositorioBaseDao<TipoCuenta> repositorioTipoCuenta;

    @Autowired
    private IRepositorioBaseDao<Estado> repositorioEstado;

    @Autowired
    private IRepositorioBaseDao<Procedencia> repositorioProcedencia;

    @Autowired
    private IRepositorioBaseDao<Cuenta> repositorioCuenta;

    @Autowired
    private IRepositorioBaseDao<Periodo> repositorioPeriodo;

    @Autowired
    private IRepositorioBaseDao<TipoAsiento> repositorioTipoAsiento;

    @Autowired
    private IRepositorioBaseDao<Parametro> repositorioParametro;

    @Autowired
    private IRepositorioBaseDao<Detalle> repositorioDetalle;

    @Autowired
    private IRepositorioBaseDao<Asiento> repositorioAsiento;
    
    @Autowired
    private IRepositorioBaseDao<Saldo> repositorioSaldo;

    /**
     * verificacion de monedas
     */
    @Test
    public void test01_moneda_traerTodo() throws ExcepcionNegocio {

        Consulta consulta = new Consulta();
        consulta.agregaOrdenAscendente("id");
        consulta.agregaEquivalencia(new Equivalencia("id", "id"));
        consulta.agregaEquivalencia(new Equivalencia("descripcion", "descripcion"));
        consulta.agregaEquivalencia(new Equivalencia("denominacionIso", "denominacionIso"));
        consulta.agregaEquivalencia(new Equivalencia("simbolo", "simbolo"));

        List<MonedaDTO> monedas = repositorioMoneda.traerTodo(Moneda.class, consulta, MonedaDTO.class);

        Assert.assertEquals(3, monedas.size());

        Assert.assertEquals("CONSOLIDADO", monedas.get(0).getDescripcion());
        Assert.assertEquals("CON", monedas.get(0).getDenominacionIso());
        Assert.assertEquals("C", monedas.get(0).getSimbolo());

        Assert.assertEquals("SOLES", monedas.get(1).getDescripcion());
        Assert.assertEquals("PEN", monedas.get(1).getDenominacionIso());
        Assert.assertEquals("S/", monedas.get(1).getSimbolo());

        Assert.assertEquals("DOLARES", monedas.get(2).getDescripcion());
        Assert.assertEquals("USD", monedas.get(2).getDenominacionIso());
        Assert.assertEquals("$", monedas.get(2).getSimbolo());
    }

    /**
     * verificacion de monedas activas
     */
    @Test
    public void test02_moneda_traerActivos() throws ExcepcionNegocio {

        Consulta consulta = new Consulta();
        consulta.agregaAlias(new Alias("estado", "estado"));
        consulta.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));
        consulta.agregaOrdenAscendente("id");
        consulta.agregaEquivalencia(new Equivalencia("id", "id"));
        consulta.agregaEquivalencia(new Equivalencia("descripcion", "descripcion"));
        consulta.agregaEquivalencia(new Equivalencia("denominacionIso", "denominacionIso"));
        consulta.agregaEquivalencia(new Equivalencia("simbolo", "simbolo"));

        List<MonedaDTO> monedas = repositorioMoneda.traerTodo(Moneda.class, consulta, MonedaDTO.class);

        Assert.assertEquals(2, monedas.size());

        Assert.assertEquals("SOLES", monedas.get(0).getDescripcion());
        Assert.assertEquals("PEN", monedas.get(0).getDenominacionIso());
        Assert.assertEquals("S/", monedas.get(0).getSimbolo());

        Assert.assertEquals("DOLARES", monedas.get(1).getDescripcion());
        Assert.assertEquals("USD", monedas.get(1).getDenominacionIso());
        Assert.assertEquals("$", monedas.get(1).getSimbolo());
    }

    /**
     * Verificar los primeros 2 estados
     */
    @Test
    public void test03_estado_traerTodo() throws ExcepcionNegocio {

        Consulta consulta = new Consulta();
        consulta.agregaOrdenAscendente("id");
        consulta.agregaEquivalencia(new Equivalencia("id", "id"));
        consulta.agregaEquivalencia(new Equivalencia("descripcion", "descripcion"));

        List<EstadoDTO> estados = repositorioEstado.traerTodo(Estado.class, consulta, EstadoDTO.class);

        Assert.assertEquals(2, estados.size());
        Assert.assertEquals("ACTIVO", estados.get(0).getDescripcion());
        Assert.assertEquals("INACTIVO", estados.get(1).getDescripcion());
    }

    /**
     * verificar las primeras 2 procedencias activas
     */
    @Test
    public void test04_procedencia_traerActivos() throws ExcepcionNegocio {

        Consulta consulta = new Consulta();
        consulta.agregaAlias(new Alias("estado", "estado"));
        consulta.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));
        consulta.agregaOrdenAscendente("id");
        consulta.agregaEquivalencia(new Equivalencia("id", "id"));
        consulta.agregaEquivalencia(new Equivalencia("descripcion", "descripcion"));

        List<ProcedenciaDTO> procedencias = repositorioProcedencia.traerTodo(Procedencia.class, consulta, ProcedenciaDTO.class);

        Assert.assertEquals(2, procedencias.size());
        Assert.assertEquals("CONTABILIDAD", procedencias.get(0).getDescripcion());
        Assert.assertEquals("SYSONE", procedencias.get(1).getDescripcion());
    }

    /**
     * verificar los tipos de cuenta
     */
    @Test
    public void test05_tipoCuenta_traerTodo() throws ExcepcionNegocio {

        Consulta consulta = new Consulta();

        consulta.agregaOrdenAscendente("id");
        consulta.agregaEquivalencia(new Equivalencia("id", "id"));
        consulta.agregaEquivalencia(new Equivalencia("descripcion", "descripcion"));
        consulta.agregaEquivalencia(new Equivalencia("signoDebe", "signoDebe"));
        consulta.agregaEquivalencia(new Equivalencia("signoHaber", "signoHaber"));

        List<TipoCuentaDTO> tiposCuenta = repositorioTipoCuenta.traerTodo(TipoCuenta.class, consulta, TipoCuentaDTO.class);

        Assert.assertEquals(2, tiposCuenta.size());
        Assert.assertEquals("DEUDOR", tiposCuenta.get(0).getDescripcion());
        Assert.assertEquals("+", tiposCuenta.get(0).getSignoDebe());
        Assert.assertEquals("-", tiposCuenta.get(0).getSignoHaber());
        Assert.assertEquals("ACREEDOR", tiposCuenta.get(1).getDescripcion());
        Assert.assertEquals("-", tiposCuenta.get(1).getSignoDebe());
        Assert.assertEquals("+", tiposCuenta.get(1).getSignoHaber());
    }

    /**
     * verificar busqueda por cuenta contable cuenta existente
     */
    @Test
    public void test06_cuenta_traerPorCuenta() throws ExcepcionNegocio {

        Consulta consulta = new Consulta();
        consulta.agregaAlias(new Alias("estado", "estado"));
        consulta.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));
        consulta.agregaOrdenAscendente("cuenta");
        consulta.agregaAlias(new Alias("padre", "padre"));
        consulta.agregaAlias(new Alias("tipoCuenta", "tipoCuenta"));
        consulta.agregaEquivalencia(new Equivalencia("id", "id"));
        consulta.agregaEquivalencia(new Equivalencia("cuenta", "cuenta"));
        consulta.agregaEquivalencia(new Equivalencia("descripcion", "descripcion"));
        consulta.agregaEquivalencia(new Equivalencia("esSbs", "esSbs"));
        consulta.agregaEquivalencia(new Equivalencia("esAnalitica", "esAnalitica"));
        consulta.agregaEquivalencia(new Equivalencia("tipoCuenta.descripcion", "tipoCuenta"));
        consulta.agregaEquivalencia(new Equivalencia("padre.id", "padreId"));
        consulta.agregaRestriccionLike(new RestriccionLike("cuenta", "1"));
        consulta.setPagina(1);

        List<CuentaDTO> cuentas = repositorioCuenta.traerTodo(Cuenta.class, consulta, CuentaDTO.class);

        Assert.assertEquals("1", cuentas.get(0).getCuenta());
        Assert.assertEquals("ACTIVO", cuentas.get(0).getDescripcion());
        Assert.assertEquals("N", cuentas.get(0).getEsAnalitica());
        Assert.assertEquals(1, cuentas.get(0).getPadreId());

        Assert.assertEquals("11", cuentas.get(1).getCuenta());
        Assert.assertEquals("DISPONIBLE", cuentas.get(1).getDescripcion());
        Assert.assertEquals("N", cuentas.get(1).getEsAnalitica());
        Assert.assertEquals(1, cuentas.get(1).getPadreId());

        Assert.assertEquals("1101", cuentas.get(2).getCuenta());
        Assert.assertEquals("CAJA", cuentas.get(2).getDescripcion());
        Assert.assertEquals("N", cuentas.get(2).getEsAnalitica());
        Assert.assertEquals(2, cuentas.get(2).getPadreId());

        Assert.assertEquals("110101", cuentas.get(3).getCuenta());
        Assert.assertEquals("OFICINA PRINCIPAL", cuentas.get(3).getDescripcion());
        Assert.assertEquals("N", cuentas.get(3).getEsAnalitica());
        Assert.assertEquals(3, cuentas.get(3).getPadreId());

        Assert.assertEquals("11010101", cuentas.get(4).getCuenta());
        Assert.assertEquals("BILLETES Y MONEDAS", cuentas.get(4).getDescripcion());
        Assert.assertEquals("S", cuentas.get(4).getEsAnalitica());
        Assert.assertEquals(4, cuentas.get(4).getPadreId());

        Assert.assertEquals("110102", cuentas.get(5).getCuenta());
        Assert.assertEquals("AGENCIAS", cuentas.get(5).getDescripcion());
        Assert.assertEquals("N", cuentas.get(5).getEsAnalitica());
        Assert.assertEquals(3, cuentas.get(5).getPadreId());

        Assert.assertEquals("11010202", cuentas.get(6).getCuenta());
        Assert.assertEquals("Agencia Tarapoto", cuentas.get(6).getDescripcion());
        Assert.assertEquals("S", cuentas.get(6).getEsAnalitica());
        Assert.assertEquals(6, cuentas.get(6).getPadreId());
    }

    /**
     * verificar busqueda por cuenta contable cuenta no existente
     */
    @Test
    public void test07_cuenta_traerPorCuenta() throws ExcepcionNegocio {

        Consulta consulta = new Consulta();
        consulta.agregaAlias(new Alias("estado", "estado"));
        consulta.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));
        consulta.agregaOrdenAscendente("cuenta");
        consulta.agregaAlias(new Alias("padre", "padre"));
        consulta.agregaAlias(new Alias("tipoCuenta", "tipoCuenta"));
        consulta.agregaEquivalencia(new Equivalencia("id", "id"));
        consulta.agregaEquivalencia(new Equivalencia("cuenta", "cuenta"));
        consulta.agregaEquivalencia(new Equivalencia("descripcion", "descripcion"));
        consulta.agregaEquivalencia(new Equivalencia("esSbs", "esSbs"));
        consulta.agregaEquivalencia(new Equivalencia("tipoCuenta.descripcion", "tipoCuenta"));
        consulta.agregaEquivalencia(new Equivalencia("padre.id", "padreId"));
        consulta.agregaRestriccionLike(new RestriccionLike("cuenta", "99"));
        consulta.setPagina(1);

        List<CuentaDTO> cuentas = repositorioCuenta.traerTodo(Cuenta.class, consulta, CuentaDTO.class);

        Assert.assertEquals(0, cuentas.size());
    }

    /**
     * verificar busqueda en general
     */
    @Test
    public void test08_cuenta_traerPorId() throws ExcepcionNegocio {

        Consulta consulta = new Consulta();
        consulta.agregaAlias(new Alias("padre", "padre"));
        consulta.agregaAlias(new Alias("tipoCuenta", "tipoCuenta"));
        consulta.agregaEquivalencia(new Equivalencia("id", "id"));
        consulta.agregaEquivalencia(new Equivalencia("cuenta", "cuenta"));
        consulta.agregaEquivalencia(new Equivalencia("descripcion", "descripcion"));
        consulta.agregaEquivalencia(new Equivalencia("esSbs", "esSbs"));
        consulta.agregaEquivalencia(new Equivalencia("esAnalitica", "esAnalitica"));
        consulta.agregaEquivalencia(new Equivalencia("tipoCuenta.descripcion", "tipoCuenta"));
        consulta.agregaEquivalencia(new Equivalencia("padre.id", "padreId"));
        consulta.agregaRestriccionIgual(new RestriccionIgual("id", 7));

        CuentaDTO cuenta = (CuentaDTO) repositorioCuenta.traerUnico(Cuenta.class, consulta, CuentaDTO.class);

        Assert.assertEquals("11010201", cuenta.getCuenta());
        Assert.assertEquals("Agencia Chulucanas", cuenta.getDescripcion());
        Assert.assertEquals("S", cuenta.getEsAnalitica());
    }

    /**
     * verificar busqueda por id
     */
    @Test
    public void test09_cuenta_traerPorId() throws ExcepcionNegocio {

        Cuenta cuenta = repositorioCuenta.traerPorId(Cuenta.class, 7);

        Assert.assertEquals("11010201", cuenta.getCuenta());
        Assert.assertEquals("Agencia Chulucanas", cuenta.getDescripcion());
        Assert.assertEquals("S", cuenta.getEsAnalitica());
    }

    /**
     * traemos las hijas de un padre
     */
    @Test
    public void test10_cuenta_traerHijas() throws ExcepcionNegocio {

        Consulta consulta = new Consulta();
        consulta.agregaAlias(new Alias("estado", "estado"));
        consulta.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));
        consulta.agregaAlias(new Alias("padre", "padre"));
        consulta.agregaAlias(new Alias("tipoCuenta", "tipoCuenta"));
        consulta.agregaEquivalencia(new Equivalencia("id", "id"));
        consulta.agregaEquivalencia(new Equivalencia("cuenta", "cuenta"));
        consulta.agregaEquivalencia(new Equivalencia("descripcion", "descripcion"));
        consulta.agregaEquivalencia(new Equivalencia("esSbs", "esSbs"));
        consulta.agregaEquivalencia(new Equivalencia("esAnalitica", "esAnalitica"));
        consulta.agregaEquivalencia(new Equivalencia("tipoCuenta.descripcion", "tipoCuenta"));
        consulta.agregaEquivalencia(new Equivalencia("padre.id", "padreId"));
        consulta.agregaRestriccionIgual(new RestriccionIgual("padre.id", 6));
        consulta.agregaRestriccionSql("length({alias}.cuenta)>1");
        consulta.agregaOrdenAscendente("cuenta");
        consulta.setPagina(1);

        List<CuentaDTO> cuentas = repositorioCuenta.traerTodo(Cuenta.class, consulta, CuentaDTO.class);

        Assert.assertEquals(2, cuentas.size());
        Assert.assertEquals("11010202", cuentas.get(0).getCuenta());
        Assert.assertEquals("S", cuentas.get(0).getEsAnalitica());
        Assert.assertEquals("11010203", cuentas.get(1).getCuenta());
        Assert.assertEquals("S", cuentas.get(1).getEsAnalitica());
    }

    /**
     * verificamos la extraccion de cuentas de nivel 1
     */
    @Test
    public void test11_cuenta_traerPrimerNivel() throws ExcepcionNegocio {

        Consulta consulta = new Consulta();
        consulta.agregaAlias(new Alias("estado", "estado"));
        consulta.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));
        consulta.agregaAlias(new Alias("padre", "padre"));
        consulta.agregaAlias(new Alias("tipoCuenta", "tipoCuenta"));
        consulta.agregaEquivalencia(new Equivalencia("id", "id"));
        consulta.agregaEquivalencia(new Equivalencia("cuenta", "cuenta"));
        consulta.agregaEquivalencia(new Equivalencia("descripcion", "descripcion"));
        consulta.agregaEquivalencia(new Equivalencia("esSbs", "esSbs"));
        consulta.agregaEquivalencia(new Equivalencia("esAnalitica", "esAnalitica"));
        consulta.agregaEquivalencia(new Equivalencia("tipoCuenta.descripcion", "tipoCuenta"));
        consulta.agregaEquivalencia(new Equivalencia("padre.id", "padreId"));
        consulta.agregaRestriccionSql("length({alias}.cuenta)=1");
        consulta.agregaOrdenAscendente("cuenta");

        List<CuentaDTO> cuentas = repositorioCuenta.traerTodo(Cuenta.class, consulta, CuentaDTO.class);

        Assert.assertEquals("1", cuentas.get(0).getCuenta());
        Assert.assertEquals("N", cuentas.get(0).getEsAnalitica());

        Assert.assertEquals("2", cuentas.get(1).getCuenta());
        Assert.assertEquals("N", cuentas.get(1).getEsAnalitica());

        Assert.assertEquals("3", cuentas.get(2).getCuenta());
        Assert.assertEquals("N", cuentas.get(2).getEsAnalitica());

        Assert.assertEquals("4", cuentas.get(3).getCuenta());
        Assert.assertEquals("N", cuentas.get(3).getEsAnalitica());

        Assert.assertEquals("5", cuentas.get(4).getCuenta());
        Assert.assertEquals("N", cuentas.get(4).getEsAnalitica());

        Assert.assertEquals("6", cuentas.get(5).getCuenta());
        Assert.assertEquals("N", cuentas.get(5).getEsAnalitica());

        Assert.assertEquals("7", cuentas.get(6).getCuenta());
        Assert.assertEquals("N", cuentas.get(6).getEsAnalitica());

        Assert.assertEquals("8", cuentas.get(7).getCuenta());
        Assert.assertEquals("N", cuentas.get(7).getEsAnalitica());
    }

    /**
     * verificamos el periodo vigente
     */
    @Test
    public void test12_periodo_traerVigente() throws ExcepcionNegocio {

        Consulta consulta = new Consulta();
        consulta.agregaAlias(new Alias("estado", "estado"));
        consulta.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));
        consulta.agregaOrdenAscendente("id");
        consulta.agregaEquivalencia(new Equivalencia("id", "id"));
        consulta.agregaEquivalencia(new Equivalencia("descripcion", "descripcion"));
        consulta.agregaEquivalencia(new Equivalencia("fechaInicial", "fechaInicial"));
        consulta.agregaEquivalencia(new Equivalencia("fechaFinal", "fechaFinal"));
        consulta.agregaEquivalencia(new Equivalencia("ultimoAsiento", "ultimoAsiento"));

        PeriodoDTO periodoVigente = (PeriodoDTO) repositorioPeriodo.traerUnico(Periodo.class, consulta, PeriodoDTO.class);

        Assert.assertEquals("201605", periodoVigente.getDescripcion());
    }

    /**
     * verificamos todos los periodos
     */
    @Test
    public void test13_periodo_traerTodos() throws ExcepcionNegocio {

        Consulta consulta = new Consulta();
        consulta.agregaOrdenAscendente("id");
        consulta.agregaEquivalencia(new Equivalencia("id", "id"));
        consulta.agregaEquivalencia(new Equivalencia("descripcion", "descripcion"));
        consulta.agregaEquivalencia(new Equivalencia("fechaInicial", "fechaInicial"));
        consulta.agregaEquivalencia(new Equivalencia("fechaFinal", "fechaFinal"));
        consulta.agregaEquivalencia(new Equivalencia("ultimoAsiento", "ultimoAsiento"));

        List<PeriodoDTO> periodos = repositorioPeriodo.traerTodo(Periodo.class, consulta, PeriodoDTO.class);

        Assert.assertEquals("201604", periodos.get(0).getDescripcion());
        Assert.assertEquals("201605", periodos.get(1).getDescripcion());
    }

    /**
     * verificamos los 41 tipos de asiento sin estado
     */
    @Test
    public void test14_tipoAsiento_traerTodos() throws ExcepcionNegocio {

        Consulta consulta = new Consulta();
        consulta.agregaOrdenAscendente("id");
        consulta.agregaEquivalencia(new Equivalencia("id", "id"));
        consulta.agregaEquivalencia(new Equivalencia("descripcion", "descripcion"));
        consulta.agregaEquivalencia(new Equivalencia("glosa", "glosa"));

        List<TipoAsientoDTO> tipos = repositorioTipoAsiento.traerTodo(TipoAsiento.class, consulta, TipoAsientoDTO.class);

        Assert.assertEquals(41, tipos.size());
    }

    /**
     * verificamos los 39 tipos de asiento solo activos
     */
    @Test
    public void test15_tipoAsiento_traerTodos() throws ExcepcionNegocio {

        Consulta consulta = new Consulta();
        consulta.agregaAlias(new Alias("estado", "estado"));
        consulta.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));
        consulta.agregaOrdenAscendente("id");
        consulta.agregaEquivalencia(new Equivalencia("id", "id"));
        consulta.agregaEquivalencia(new Equivalencia("descripcion", "descripcion"));
        consulta.agregaEquivalencia(new Equivalencia("glosa", "glosa"));

        List<TipoAsientoDTO> tipos = repositorioTipoAsiento.traerTodo(TipoAsiento.class, consulta, TipoAsientoDTO.class);

        Assert.assertEquals(39, tipos.size());
    }

    /**
     * verificamos las variables del modulo contable
     */
    @Test
    public void test16_variable_traerTodo() throws ExcepcionNegocio {
        int moduloContable = 1;

        Consulta consulta = new Consulta();
        consulta.agregaAlias(new Alias("modulo", "modulo"));
        consulta.agregaRestriccionIgual(new RestriccionIgual("modulo.id", moduloContable));
        consulta.agregaOrdenAscendente("id");
        consulta.agregaEquivalencia(new Equivalencia("descripcion", "descripcion"));
        consulta.agregaEquivalencia(new Equivalencia("valor", "valor"));

        List<ParametroDTO> variables = repositorioParametro.traerTodo(Parametro.class, consulta, ParametroDTO.class);

        Assert.assertEquals("registrosPorPagina", variables.get(0).getDescripcion());
        Assert.assertEquals("10", variables.get(0).getValor());
    }

    /**
     * verificamos el conteo de cuentas
     */
    @Test
    public void test17_cuenta_contar() throws ExcepcionNegocio {
        Consulta consulta = new Consulta();

        consulta.agregaAlias(new Alias("padre", "padre"));
        consulta.agregaAlias(new Alias("tipoCuenta", "tipoCuenta"));
        consulta.agregaAlias(new Alias("estado", "estado"));

        consulta.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));
        consulta.agregaRestriccionIgual(new RestriccionIgual("cuenta", "1101"));

        consulta.agregaEquivalencia(new Equivalencia("id", "id"));
        consulta.agregaEquivalencia(new Equivalencia("cuenta", "cuenta"));
        consulta.agregaEquivalencia(new Equivalencia("descripcion", "descripcion"));
        consulta.agregaEquivalencia(new Equivalencia("esSbs", "esSbs"));
        consulta.agregaEquivalencia(new Equivalencia("esAnalitica", "esAnalitica"));
        consulta.agregaEquivalencia(new Equivalencia("tipoCuenta.descripcion", "tipoCuenta"));
        consulta.agregaEquivalencia(new Equivalencia("padre.id", "padreId"));

        Assert.assertEquals(1, repositorioCuenta.contar(Cuenta.class, consulta));
    }

    /**
     * verificamos la suma de totales
     */
    @Test
    public void test18_asiento_traerAsiento() throws ExcepcionNegocio {
        int asientoId1 = 26, asientoId2 = 52;
        BigDecimal totalAsiento1 = new BigDecimal(0);
        BigDecimal totalAsiento2 = new BigDecimal(0);

        Asiento asiento1 = repositorioAsiento.traerPorId(Asiento.class, asientoId1);
        for (Detalle detalle : asiento1.getDetalles()) {
            totalAsiento1 = totalAsiento1.add(detalle.getDebe());
        }

        Asiento asiento2 = repositorioAsiento.traerPorId(Asiento.class, asientoId2);
        for (Detalle detalle : asiento2.getDetalles()) {
            totalAsiento2 = totalAsiento2.add(detalle.getDebe());
        }

        RestriccionIn restriccionIn = new RestriccionIn("asiento.id");
        restriccionIn.agregarRestriccion(asientoId1);
        restriccionIn.agregarRestriccion(asientoId2);

        Consulta consulta = new Consulta();
        consulta.agregaAlias(new Alias("asiento", "asiento"));
        consulta.setRestriccionIn(restriccionIn);
        consulta.agregaSuma(new Suma("debe", "totalDebe"));
        consulta.agregaSuma(new Suma("haber", "totalHaber"));
        consulta.agregaGrupo(new Grupo("asiento.id", "id"));

        List<TotalAsientoDTO> totalAsientos = repositorioDetalle.traerAgrupado(Detalle.class, consulta, TotalAsientoDTO.class);

        Assert.assertEquals(totalAsiento1, totalAsientos.get(0).getTotalDebe());
        Assert.assertEquals(totalAsiento2, totalAsientos.get(1).getTotalDebe());
    }

    /**
     * verificamos la suma de totales para mayorizar
     */
    @Test
    public void test19_asiento_traerAsiento() throws ExcepcionNegocio {
        int detalleId1 = 63, detalleId2 = 105;
        BigDecimal totalCuentaDebe131 = new BigDecimal(0);
        BigDecimal totalCuentaHaber131 = new BigDecimal(0);

        Detalle detalle1 = repositorioDetalle.traerPorId(Detalle.class, detalleId1);
        totalCuentaDebe131 = totalCuentaDebe131.add(detalle1.getDebe());
        totalCuentaHaber131 = totalCuentaHaber131.add(detalle1.getHaber());

        Detalle detalle2 = repositorioDetalle.traerPorId(Detalle.class, detalleId2);
        totalCuentaDebe131 = totalCuentaDebe131.add(detalle2.getDebe());
        totalCuentaHaber131 = totalCuentaHaber131.add(detalle2.getHaber());

        Periodo periodo201604 = repositorioPeriodo.traerPorId(Periodo.class, 1);
        Consulta consulta = new Consulta();
        consulta.agregaAlias(new Alias("asiento", "asiento"));
        consulta.agregaAlias(new Alias("cuenta", "cuenta"));
        consulta.agregaRestriccionBetweenDate(new RestriccionBetweenDate("asiento.fecha", periodo201604.getFechaInicial(), periodo201604.getFechaFinal()));
        consulta.agregaSuma(new Suma("debe", "totalDebe"));
        consulta.agregaSuma(new Suma("haber", "totalHaber"));
        consulta.agregaGrupo(new Grupo("cuenta.id", "cuentaId"));
        consulta.agregaGrupo(new Grupo("asiento.moneda.id", "monedaId"));

        List<MayorizaDTO> totalCuentas = repositorioDetalle.traerAgrupado(Detalle.class, consulta, MayorizaDTO.class);
        MayorizaDTO mayorizaDTO131 = totalCuentas.stream().filter(x -> x.getCuentaId() == 131).findFirst().get();

        Assert.assertEquals(totalCuentaDebe131, mayorizaDTO131.getTotalDebe());
        Assert.assertEquals(totalCuentaHaber131, mayorizaDTO131.getTotalHaber());
    }

    /**
     * Traer Unica devuelve null sino encuentra
     */
    @Test
    public void test20_traerUnica() throws ExcepcionNegocio {
        int asientoId = 0;
        Consulta consulta = new Consulta();
        consulta.agregaEquivalencia(new Equivalencia("id", "id"));
        consulta.agregaRestriccionIgual(new RestriccionIgual("id", asientoId));

        Object asiento = repositorioAsiento.traerUnico(Asiento.class, consulta, AsientoDTO.class);
        Assert.assertEquals(null, asiento);
    }

    /**
     * Verifica consulta para traer saldo por codigos int
     */
    @Test
    public void test21_traerSaldo() throws ExcepcionNegocio {
        int cuentaId=1, monedaId=1, periodoId=2;
        Consulta consulta = new Consulta();
        consulta.agregaAlias(new Alias("cuenta", "cuenta"));
        consulta.agregaAlias(new Alias("moneda", "moneda"));
        consulta.agregaAlias(new Alias("periodo", "periodo"));
        consulta.agregaRestriccionIgual(new RestriccionIgual("cuenta.id", cuentaId));
        consulta.agregaRestriccionIgual(new RestriccionIgual("moneda.id", monedaId));
        consulta.agregaRestriccionIgual(new RestriccionIgual("periodo.id", periodoId));
        
        Saldo saldo = (Saldo) repositorioSaldo.traerUnico(Saldo.class, consulta);
        
        Assert.assertEquals(new BigDecimal(2000),saldo.getSaldoInicial());
        Assert.assertEquals(new BigDecimal(10000),saldo.getTotalDebe());
        Assert.assertEquals(new BigDecimal(9000),saldo.getTotalHaber());
        Assert.assertEquals(new BigDecimal(3000),saldo.getSaldoFinal());        
    }

}
