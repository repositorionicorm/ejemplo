/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.dao;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import pe.cajapaita.backerp.contabilidad.consulta.Alias;
import pe.cajapaita.backerp.contabilidad.consulta.Equivalencia;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;
import pe.cajapaita.backerp.contabilidad.infraestructura.Mensaje;
import pe.cajapaita.backerp.contabilidad.consulta.Consulta;
import pe.cajapaita.backerp.contabilidad.consulta.Grupo;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionBetweenDate;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionIgual;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionLike;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionNoIgual;
import pe.cajapaita.backerp.contabilidad.consulta.Suma;

/**
 *
 * @author hnole
 * @param <Entidad>
 */
@Repository
public class RepositorioBaseDao<Entidad extends Serializable> implements IRepositorioBaseDao<Entidad> {

    @Autowired
    private SessionFactory sessionFactory;
    private final Logger logger = Logger.getLogger(RepositorioBaseDao.class);

    @Override
    public Entidad traerPorId(Class<Entidad> claseEntidad, int id) throws ExcepcionNegocio {
        try {
            return (Entidad) session().get(claseEntidad.getName(), id);
        } catch (DataAccessException ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_CONEXION_BASE, Mensaje.TIPO_ERROR);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    @Override
    public List traerTodo(Class<Entidad> claseEntidad, Consulta consulta, Class claseDTO) throws ExcepcionNegocio {
        try {
            Criteria criteria = this.construirTraerTodo(claseEntidad, consulta);

            criteria.setResultTransformer(new AliasToBeanResultTransformer(claseDTO));

            return criteria.list();
        } catch (DataAccessException ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_CONEXION_BASE, Mensaje.TIPO_ERROR);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }

    }

    @Override
    public List traerAgrupado(Class<Entidad> claseEntidad, Consulta consulta, Class claseDTO) throws ExcepcionNegocio {
        try {
            Criteria criteria = session().createCriteria(claseEntidad.getName());

            for (Alias alias : consulta.getAlias()) {
                criteria.createAlias(alias.getPropiedadEntidad(), alias.getAlias());
            }

            for (RestriccionIgual restriccion : consulta.getRestriccionesIguales()) {
                criteria.add(Restrictions.eq(restriccion.getPropiedad(), restriccion.getValor()));
            }
            for (RestriccionNoIgual restriccionNE : consulta.getRestriccionesNoIgual()) {
                criteria.add(Restrictions.ne(restriccionNE.getPropiedad(), restriccionNE.getValue()));
            }

            for (RestriccionLike restriccion : consulta.getRestriccionesLike()) {
                criteria.add(Restrictions.like(restriccion.getPropiedad(), restriccion.getValor() + "%"));
            }

            for (String restriccionSql : consulta.getRestriccionesSql()) {
                criteria.add(Restrictions.sqlRestriction(restriccionSql));
            }

            for (RestriccionBetweenDate restriccionBetweenDate : consulta.getRestriccionesBetweenDate()) {
                criteria.add(Restrictions.between(restriccionBetweenDate.getPropiedad(),
                        restriccionBetweenDate.getValorInicial(),
                        restriccionBetweenDate.getValorFinal()));
            }

            if (consulta.getRestriccionIn() != null) {
                criteria.add(Restrictions.in(consulta.getRestriccionIn().getPropiedad(), consulta.getRestriccionIn().getRestricciones()));
            }

            if (!consulta.getGrupos().isEmpty()) {
                ProjectionList proyeccion = Projections.projectionList();
                for (Suma suma : consulta.getSumas()) {
                    proyeccion.add(Projections.sum(suma.getPropiedadSumar()).as(suma.getAliasDeSuma()));
                }
                for (Grupo grupo : consulta.getGrupos()) {
                    proyeccion.add(Projections.groupProperty(grupo.getPropiedadAgrupar()).as(grupo.getAliasDeGrupo()));
                }
                criteria.setProjection(proyeccion);
            }

            for (String orden : consulta.getOrdenAscendentes()) {
                criteria.addOrder(Order.asc(orden));
            }

            for (String orden : consulta.getOrdenDescendentes()) {
                criteria.addOrder(Order.desc(orden));
            }

            criteria.setResultTransformer(new AliasToBeanResultTransformer(claseDTO));

            return criteria.list();
        } catch (DataAccessException ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_CONEXION_BASE, Mensaje.TIPO_ERROR);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    @Override
    public Object traerUnico(Class<Entidad> claseEntidad, Consulta consulta, Class claseDTO) throws ExcepcionNegocio {
        try {
            Criteria criteria = session().createCriteria(claseEntidad.getName());

            for (Alias alias : consulta.getAlias()) {
                criteria.createAlias(alias.getPropiedadEntidad(), alias.getAlias());
            }

            for (RestriccionIgual restriccion : consulta.getRestriccionesIguales()) {
                criteria.add(Restrictions.eq(restriccion.getPropiedad(), restriccion.getValor()));
            }
            for (RestriccionNoIgual restriccionNE : consulta.getRestriccionesNoIgual()) {
                criteria.add(Restrictions.ne(restriccionNE.getPropiedad(), restriccionNE.getValue()));
            }

            for (String restriccionSql : consulta.getRestriccionesSql()) {
                criteria.add(Restrictions.sqlRestriction(restriccionSql));
            }

            if (consulta.getRestriccionIn() != null) {
                criteria.add(Restrictions.in(consulta.getRestriccionIn().getPropiedad(), consulta.getRestriccionIn().getRestricciones()));
            }

            if (consulta.getEquivalencias().size() > 0) {
                ProjectionList proyeccion = Projections.projectionList();
                for (Equivalencia equivalencia : consulta.getEquivalencias()) {
                    proyeccion.add(Property.forName(equivalencia.getPropiedadEntidad()).as(equivalencia.getPropiedadDto()));
                }
                criteria.setProjection(proyeccion);
            }

            criteria.setResultTransformer(new AliasToBeanResultTransformer(claseDTO));
            criteria.setMaxResults(1);

            return (Object) criteria.uniqueResult();
        } catch (DataAccessException ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_CONEXION_BASE, Mensaje.TIPO_ERROR);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }

    }

    @Override
    public Object traerUnico(Class<Entidad> claseEntidad, Consulta consulta) throws ExcepcionNegocio {
        try {
            Criteria criteria = session().createCriteria(claseEntidad.getName());

            for (Alias alias : consulta.getAlias()) {
                criteria.createAlias(alias.getPropiedadEntidad(), alias.getAlias());
            }

            for (RestriccionIgual restriccion : consulta.getRestriccionesIguales()) {
                criteria.add(Restrictions.eq(restriccion.getPropiedad(), restriccion.getValor()));
            }
            for (RestriccionNoIgual restriccionNE : consulta.getRestriccionesNoIgual()) {
                criteria.add(Restrictions.ne(restriccionNE.getPropiedad(), restriccionNE.getValue()));
            }

            for (String restriccionSql : consulta.getRestriccionesSql()) {
                criteria.add(Restrictions.sqlRestriction(restriccionSql));
            }

            if (consulta.getRestriccionIn() != null) {
                criteria.add(Restrictions.in(consulta.getRestriccionIn().getPropiedad(), consulta.getRestriccionIn().getRestricciones()));
            }

            if (consulta.getEquivalencias().size() > 0) {
                ProjectionList proyeccion = Projections.projectionList();
                for (Equivalencia equivalencia : consulta.getEquivalencias()) {
                    proyeccion.add(Property.forName(equivalencia.getPropiedadEntidad()).as(equivalencia.getPropiedadDto()));
                }
                criteria.setProjection(proyeccion);
            }

            criteria.setMaxResults(1);

            return (Object) criteria.uniqueResult();
        } catch (DataAccessException ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_CONEXION_BASE, Mensaje.TIPO_ERROR);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    @Override
    public int contar(Class<Entidad> claseEntidad, Consulta consulta) throws ExcepcionNegocio {
        try {
            Criteria criteria = session().createCriteria(claseEntidad.getName());

            for (Alias alias : consulta.getAlias()) {
                criteria.createAlias(alias.getPropiedadEntidad(), alias.getAlias());
            }

            for (RestriccionIgual restriccion : consulta.getRestriccionesIguales()) {
                criteria.add(Restrictions.eq(restriccion.getPropiedad(), restriccion.getValor()));
            }
            for (RestriccionNoIgual restriccionNE : consulta.getRestriccionesNoIgual()) {
                criteria.add(Restrictions.ne(restriccionNE.getPropiedad(), restriccionNE.getValue()));
            }

            for (RestriccionLike restriccion : consulta.getRestriccionesLike()) {
                criteria.add(Restrictions.like(restriccion.getPropiedad(), restriccion.getValor() + "%"));
            }

            for (String restriccion : consulta.getRestriccionesSql()) {
                criteria.add(Restrictions.sqlRestriction(restriccion));
            }

            for (RestriccionBetweenDate restriccionBetweenDate : consulta.getRestriccionesBetweenDate()) {
                criteria.add(Restrictions.between(restriccionBetweenDate.getPropiedad(),
                        restriccionBetweenDate.getValorInicial(),
                        restriccionBetweenDate.getValorFinal()));
            }

            if (consulta.getRestriccionIn() != null) {
                criteria.add(Restrictions.in(consulta.getRestriccionIn().getPropiedad(), consulta.getRestriccionIn().getRestricciones()));
            }

            return Integer.parseInt(criteria.setProjection(Projections.rowCount()).uniqueResult().toString());

        } catch (DataAccessException ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_CONEXION_BASE, Mensaje.TIPO_ERROR);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }

    }

    @Override
    public void guardar(Entidad entidad) throws ExcepcionNegocio {
        try {
            session().saveOrUpdate(entidad);

        } catch (DataAccessException ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_CONEXION_BASE, Mensaje.TIPO_ERROR);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    @Override
    public void guardar(List<Entidad> listaEntidad) throws ExcepcionNegocio {
        try {
            for (Entidad entidad : listaEntidad) {
                session().saveOrUpdate(entidad);
            }

        } catch (DataAccessException ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_CONEXION_BASE, Mensaje.TIPO_ERROR);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    @Override
    public void eliminar(Entidad entidad) throws ExcepcionNegocio {
        try {
            session().delete(entidad);

        } catch (DataAccessException ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_CONEXION_BASE, Mensaje.TIPO_ERROR);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    @Override
    public void ejecutarConsulta(String consulta) throws ExcepcionNegocio {
        try {
            SQLQuery query = session().createSQLQuery(consulta);
            query.executeUpdate();
        } catch (DataAccessException ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_CONEXION_BASE, Mensaje.TIPO_ERROR);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    @Override
    public List traerTodo(Class<Entidad> claseEntidad, Consulta consulta) throws ExcepcionNegocio {
        try {
            Criteria criteria = this.construirTraerTodo(claseEntidad, consulta);
            return criteria.list();
        } catch (DataAccessException ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_CONEXION_BASE, Mensaje.TIPO_ERROR);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }

    }

    @Override
    public Object traerMaximo(Class<Entidad> claseEntidad, Consulta consulta) throws ExcepcionNegocio {

        try {
            Criteria criteria = session().createCriteria(claseEntidad.getName());

            for (Alias alias : consulta.getAlias()) {
                criteria.createAlias(alias.getPropiedadEntidad(), alias.getAlias());
            }

            for (RestriccionIgual restriccion : consulta.getRestriccionesIguales()) {
                criteria.add(Restrictions.eq(restriccion.getPropiedad(), restriccion.getValor()));
            }

            for (RestriccionNoIgual restriccionNE : consulta.getRestriccionesNoIgual()) {
                criteria.add(Restrictions.ne(restriccionNE.getPropiedad(), restriccionNE.getValue()));
            }

            for (RestriccionLike restriccion : consulta.getRestriccionesLike()) {
                criteria.add(Restrictions.like(restriccion.getPropiedad(), restriccion.getValor() + "%"));
            }

            for (String restriccionSql : consulta.getRestriccionesSql()) {
                criteria.add(Restrictions.sqlRestriction(restriccionSql));
            }

            for (RestriccionBetweenDate restriccionBetweenDate : consulta.getRestriccionesBetweenDate()) {
                criteria.add(Restrictions.between(restriccionBetweenDate.getPropiedad(),
                        restriccionBetweenDate.getValorInicial(),
                        restriccionBetweenDate.getValorFinal()));
            }

            if (consulta.getRestriccionIn() != null) {
                criteria.add(Restrictions.in(consulta.getRestriccionIn().getPropiedad(), consulta.getRestriccionIn().getRestricciones()));
            }
            if (consulta.getObtenerMaximoDe() != null) {
                criteria.setProjection(Projections.projectionList().add(
                        Projections.max(consulta.getObtenerMaximoDe().getPropiedad())
                ));
            }

            return criteria.uniqueResult();

        } catch (DataAccessException ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_CONEXION_BASE, Mensaje.TIPO_ERROR);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="metodos privados"> 
    private Session session() {
        return this.sessionFactory.getCurrentSession();
    }

    private Criteria construirTraerTodo(Class<Entidad> claseEntidad, Consulta consulta) throws ExcepcionNegocio {
        Criteria criteria;
        try {
            criteria = session().createCriteria(claseEntidad.getName());

            for (Alias alias : consulta.getAlias()) {
                criteria.createAlias(alias.getPropiedadEntidad(), alias.getAlias());
            }

            for (RestriccionIgual restriccion : consulta.getRestriccionesIguales()) {
                criteria.add(Restrictions.eq(restriccion.getPropiedad(), restriccion.getValor()));
            }

            for (RestriccionNoIgual restriccionNE : consulta.getRestriccionesNoIgual()) {
                criteria.add(Restrictions.ne(restriccionNE.getPropiedad(), restriccionNE.getValue()));
            }

            for (RestriccionLike restriccion : consulta.getRestriccionesLike()) {
                criteria.add(Restrictions.like(restriccion.getPropiedad(), restriccion.getValor() + "%"));
            }

            for (String restriccionSql : consulta.getRestriccionesSql()) {
                criteria.add(Restrictions.sqlRestriction(restriccionSql));
            }

            for (RestriccionBetweenDate restriccionBetweenDate : consulta.getRestriccionesBetweenDate()) {
                criteria.add(Restrictions.between(restriccionBetweenDate.getPropiedad(),
                        restriccionBetweenDate.getValorInicial(),
                        restriccionBetweenDate.getValorFinal()));
            }

            if (consulta.getRestriccionIn() != null) {
                criteria.add(Restrictions.in(consulta.getRestriccionIn().getPropiedad(), consulta.getRestriccionIn().getRestricciones()));
            }

            for (String orden : consulta.getOrdenAscendentes()) {
                criteria.addOrder(Order.asc(orden));
            }

            for (String orden : consulta.getOrdenDescendentes()) {
                criteria.addOrder(Order.desc(orden));
            }

            if (consulta.getEquivalencias().size() > 0) {
                ProjectionList proyeccion = Projections.projectionList();
                for (Equivalencia equivalencia : consulta.getEquivalencias()) {
                    proyeccion.add(Property.forName(equivalencia.getPropiedadEntidad()).as(equivalencia.getPropiedadDto()));
                }
                criteria.setProjection(proyeccion);
            }

            if (consulta.getPagina() > 0) {
                criteria.setMaxResults(consulta.getRegistrosPorPagina());
                criteria.setFirstResult((consulta.getPagina() - 1) * consulta.getRegistrosPorPagina());
            }
        } catch (DataAccessException ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_CONEXION_BASE, Mensaje.TIPO_ERROR);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
        return criteria;
    }

    //</editor-fold>
}
