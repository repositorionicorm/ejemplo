/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.servicio.impl;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionLike;
import pe.cajapaita.backerp.contabilidad.entidad.Usuario;
import pe.cajapaita.backerp.contabilidad.infraestructura.Helper;
import pe.cajapaita.backerp.contabilidad.servicio.IAsientoServicio;
import pe.cajapaita.backerp.contabilidad.servicio.ICuentaServicio;
import pe.cajapaita.backerp.contabilidad.servicio.IUtilitarioServicio;

/**
 *
 * @author hnole
 */
@Service
@Transactional
public class CuentaServicioImpl implements ICuentaServicio {

    @Autowired
    private IRepositorioBaseDao<Cuenta> cuentaDAO;

    @Autowired
    private IUtilitarioServicio utilitarioServicio;

    @Autowired
    private IAsientoServicio asientoServicio;

    @Autowired
    private IRepositorioBaseDao<Usuario> usuarioDAO;

    private final Logger logger = Logger.getLogger(CuentaServicioImpl.class);
    private int totalPaginas;

    @Override
    public List<CuentaDTO> traerPorCuenta(String cuenta, int pagina) throws ExcepcionNegocio {
        try {
            Consulta consulta = prepararConsulta();
            consulta.setRegistrosPorPagina(utilitarioServicio.traerRegistrosPorPagina());

            if (consulta.getRegistrosPorPagina() == 0) {
                throw new ExcepcionNegocio(Mensaje.VARIABLES_NO_CARGADAS, Mensaje.TIPO_ERROR);
            }

            consulta.agregaRestriccionLike(new RestriccionLike("cuenta", cuenta));
            consulta.agregaOrdenAscendente("cuenta");
            consulta.setPagina(pagina);

            List<CuentaDTO> cuentas = cuentaDAO.traerTodo(Cuenta.class, consulta, CuentaDTO.class);
            if (cuentas.isEmpty()) {
                throw new ExcepcionNegocio(Mensaje.CUENTA_NO_EXISTEN, Mensaje.TIPO_ALERTA);
            }
            this.asignaTotalPaginas(consulta);

            return cuentas;
        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    @Override
    public int traerCuentaId(String numeroCuenta) throws ExcepcionNegocio {
        try {
            Consulta consulta = new Consulta();
            consulta.agregaRestriccionIgual(new RestriccionIgual("cuenta", numeroCuenta));
            consulta.agregaEquivalencia(new Equivalencia("id", "id"));
            CuentaDTO cuenta = (CuentaDTO) cuentaDAO.traerUnico(Cuenta.class, consulta, CuentaDTO.class);
            
            if (cuenta == null) {
                throw new ExcepcionNegocio(Mensaje.CUENTA_NO_EXISTE + " " + numeroCuenta, Mensaje.TIPO_ALERTA);
            }
            return cuenta.getId();

        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    @Override
    public List<CuentaDTO> traerHijas(int cuentaPadreId, int pagina) throws ExcepcionNegocio {
        try {
            Consulta consulta = prepararConsulta();
            consulta.setRegistrosPorPagina(utilitarioServicio.traerRegistrosPorPagina());

            if (consulta.getRegistrosPorPagina() == 0) {
                throw new ExcepcionNegocio(Mensaje.VARIABLES_NO_CARGADAS, Mensaje.TIPO_ERROR);
            }

            consulta.agregaRestriccionIgual(new RestriccionIgual("padre.id", cuentaPadreId));
            consulta.agregaRestriccionSql("length({alias}.cuenta)>1");
            consulta.agregaOrdenAscendente("cuenta");
            consulta.setPagina(pagina);

            List<CuentaDTO> cuentas = cuentaDAO.traerTodo(Cuenta.class, consulta, CuentaDTO.class);
            if (cuentas.isEmpty()) {
                throw new ExcepcionNegocio(Mensaje.CUENTA_NO_EXISTEN, Mensaje.TIPO_ALERTA);
            }
            this.asignaTotalPaginas(consulta);

            return cuentas;
        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }

    }

    @Override
    public List<CuentaDTO> traerPrimerNivel() throws ExcepcionNegocio {
        try {
            Consulta consulta = prepararConsulta();
            consulta.agregaRestriccionSql("length({alias}.cuenta)=1");
            consulta.agregaOrdenAscendente("cuenta");

            List<CuentaDTO> cuentas = cuentaDAO.traerTodo(Cuenta.class, consulta, CuentaDTO.class);
            if (cuentas.isEmpty()) {
                throw new ExcepcionNegocio(Mensaje.CUENTA_NO_EXISTEN, Mensaje.TIPO_ALERTA);
            }
            this.totalPaginas = 1;

            return cuentas;
        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }

    }

    @Override
    public int traerTotalPaginas() {
        return this.totalPaginas;
    }

    @Override
    public void guardar(CuentaDTO cuentaDTO, int usuarioId) throws ExcepcionNegocio {
        try {
            cuentaDTO.validarPropiedades();

            if (this.existeCuenta(cuentaDTO.getCuenta())) {
                throw new ExcepcionNegocio(Mensaje.CUENTA_CONTABLE_EXISTE, Mensaje.TIPO_ALERTA);
            }

            Cuenta cuentaPadre = traerCuentaPorId(cuentaDTO.getPadreId());
            if (cuentaPadre == null) {
                throw new ExcepcionNegocio(Mensaje.CUENTA_PADRE_NO_EXISTE, Mensaje.TIPO_ALERTA);
            }

            if (!tieneRelacionPadre(cuentaDTO.getCuenta(), cuentaPadre.getCuenta())) {
                throw new ExcepcionNegocio(Mensaje.CUENTA_NO_COINCIDE_CON_PADRE, Mensaje.TIPO_ALERTA);
            }

            Cuenta cuenta = armarCuenta(cuentaDTO, cuentaPadre);

            Usuario usuario = usuarioDAO.traerPorId(Usuario.class, usuarioId);
            if (usuario == null) {
                throw new ExcepcionNegocio(Mensaje.ASIENTO_USUARIO_NO_EXISTE, Mensaje.TIPO_ALERTA);
            }
            cuenta.setUsuario(usuario);

            cuentaDAO.guardar(cuenta);
        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    @Override
    public void actualizar(CuentaDTO cuentaDTO) throws ExcepcionNegocio {
        try {
            if (Helper.esNuloVacio(cuentaDTO.getDescripcion())) {
                throw new ExcepcionNegocio(Mensaje.CUENTA_DESCRIPCION_VACIA, Mensaje.TIPO_ALERTA);
            }

            Cuenta cuenta = this.traerCuentaPorId(cuentaDTO.getId());
            if (cuenta == null) {
                throw new ExcepcionNegocio(Mensaje.CUENTA_CONTABLE_NO_EXISTE, Mensaje.TIPO_ALERTA);
            }

            cuenta.setDescripcion(cuentaDTO.getDescripcion().toUpperCase().trim());
            cuentaDAO.guardar(cuenta);

        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    @Override
    public void eliminar(int cuentaId) throws ExcepcionNegocio {
        try {
            Cuenta cuenta = this.traerCuentaPorId(cuentaId);
            if (cuenta == null) {
                throw new ExcepcionNegocio(Mensaje.CUENTA_CONTABLE_NO_EXISTE, Mensaje.TIPO_ALERTA);
            }

            cuenta.validarPropiedades();

            if (asientoServicio.cuentaTieneMovimientos(cuenta.getId())) {
                throw new ExcepcionNegocio(Mensaje.CUENTA_TIENE_MOVIMIENTOS, Mensaje.TIPO_ALERTA);
            }

            Cuenta cuentaPadre = cuenta.getPadre();
            if (this.numeroHijas(cuentaPadre.getId()) == 1) {
                cuentaPadre.setEsAnalitica("S");
            }

            cuentaDAO.eliminar(cuenta);

        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="metodos privados"> 
    private Consulta prepararConsulta() {
        Consulta consulta = new Consulta();

        consulta.agregaAlias(new Alias("padre", "padre"));
        consulta.agregaAlias(new Alias("tipoCuenta", "tipoCuenta"));
        consulta.agregaAlias(new Alias("estado", "estado"));

        consulta.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));

        consulta.agregaEquivalencia(new Equivalencia("id", "id"));
        consulta.agregaEquivalencia(new Equivalencia("cuenta", "cuenta"));
        consulta.agregaEquivalencia(new Equivalencia("descripcion", "descripcion"));
        consulta.agregaEquivalencia(new Equivalencia("esSbs", "esSbs"));
        consulta.agregaEquivalencia(new Equivalencia("esAnalitica", "esAnalitica"));
        consulta.agregaEquivalencia(new Equivalencia("tipoCuenta.descripcion", "tipoCuentaDescripcion"));
        consulta.agregaEquivalencia(new Equivalencia("padre.id", "padreId"));

        return consulta;
    }

    private void asignaTotalPaginas(Consulta parametro) throws ExcepcionNegocio {
        this.totalPaginas = (int) Math.ceil(cuentaDAO.contar(Cuenta.class, parametro) / (double) (utilitarioServicio.traerRegistrosPorPagina()));
    }

    private boolean existeCuenta(String cuenta) throws ExcepcionNegocio {
        try {
            Consulta consulta = prepararConsulta();
            consulta.agregaRestriccionIgual(new RestriccionIgual("cuenta", cuenta));

            return (cuentaDAO.contar(Cuenta.class, consulta) > 0);
        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    private Cuenta traerCuentaPorId(int id) throws ExcepcionNegocio {
        return cuentaDAO.traerPorId(Cuenta.class, id);
    }

    private boolean tieneRelacionPadre(String cuentaHijo, String cuentaPadre) {
        int longitud = cuentaHijo.trim().length();
        String cuentaComparacion = longitud >= 4
                ? cuentaHijo.substring(0, longitud - 2)
                : cuentaHijo.substring(0, longitud - 1);

        return (cuentaComparacion.equals(cuentaPadre));
    }

    private int numeroHijas(int cuentaId) throws ExcepcionNegocio {
        Consulta consulta = this.prepararConsulta();
        consulta.setRestriccionesIguales(new ArrayList<RestriccionIgual>());
        consulta.agregaRestriccionIgual(new RestriccionIgual("padre.id", cuentaId));

        return cuentaDAO.contar(Cuenta.class, consulta);
    }

    private Cuenta armarCuenta(CuentaDTO cuentaDTO, Cuenta cuentaPadre) {
        if (cuentaPadre.getEsAnalitica().equals("S")) {
            cuentaPadre.setEsAnalitica("N");
        }

        Cuenta cuenta = new Cuenta();
        cuenta.setCuenta(cuentaDTO.getCuenta());
        cuenta.setDescripcion(cuentaDTO.getDescripcion().toUpperCase().trim());
        cuenta.setEsSbs("N");
        cuenta.setEsAnalitica("S");
        cuenta.setTipoCuenta(cuentaPadre.getTipoCuenta());
        cuenta.setPadre(cuentaPadre);
        cuenta.setEstado(cuentaPadre.getEstado());

        return cuenta;
    }

    //</editor-fold> 
}
