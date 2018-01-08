/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.consulta;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hnole
 */
public class Consulta {

    private List<Equivalencia> equivalencias;
    private List<String> ordenAscendentes;
    private List<String> ordenDescendentes;
    private List<RestriccionLike> restriccionesLike;
    private List<RestriccionIgual> restriccionesIguales;
    private List<String> restriccionesSql;
    private List<RestriccionBetweenDate> restriccionesBetweenDate;
    private List<Alias> alias;
    private List<Suma> sumas;
    private List<Grupo> grupos;
    private List<RestriccionNoIgual> restriccionesNoIgual;

    private int pagina;
    private int registrosPorPagina;    
    private RestriccionIn restriccionIn;
    private Maximo obtenerMaximoDe;

    public Consulta() {
        this.equivalencias = new ArrayList<>();
        this.ordenAscendentes = new ArrayList<>();
        this.ordenDescendentes = new ArrayList<>();
        this.restriccionesLike = new ArrayList<>();
        this.restriccionesIguales = new ArrayList<>();
        this.restriccionesNoIgual= new ArrayList<>();
        this.restriccionesSql = new ArrayList<>();
        this.restriccionesBetweenDate = new ArrayList<>();
        this.alias = new ArrayList<>();
        this.sumas = new ArrayList<>();
        this.grupos = new ArrayList<>();
        this.pagina = 0;
        this.registrosPorPagina = 0;
    }

    public List<RestriccionLike> getRestriccionesLike() {
        return restriccionesLike;
    }

    public List<RestriccionIgual> getRestriccionesIguales() {
        return restriccionesIguales;
    }

    public List<String> getRestriccionesSql() {
        return restriccionesSql;
    }

    public List<RestriccionBetweenDate> getRestriccionesBetweenDate() {
        return restriccionesBetweenDate;
    }

    public List<Equivalencia> getEquivalencias() {
        return equivalencias;
    }

    public List<String> getOrdenAscendentes() {
        return ordenAscendentes;
    }

    public List<String> getOrdenDescendentes() {
        return ordenDescendentes;
    }

    public List<Alias> getAlias() {
        return alias;
    }  

    public List<Suma> getSumas() {
        return sumas;
    }

    public List<Grupo> getGrupos() {
        return grupos;
    }

    public RestriccionIn getRestriccionIn() {
        return restriccionIn;
    }

    public void setRestriccionIn(RestriccionIn restriccionIn) {
        this.restriccionIn = restriccionIn;
    }

    public Maximo getObtenerMaximoDe() {
        return obtenerMaximoDe;
    }

    public void ObtenerMaximoDe(Maximo obtenerMaximoDe) {
        this.obtenerMaximoDe = obtenerMaximoDe;
    }


    
    public void agregaOrdenAscendente(String orden) {
        this.ordenAscendentes.add(orden);
    }

    public void agregaOrdenDescentente(String orden) {
        this.ordenDescendentes.add(orden);
    }

    public void agregaEquivalencia(Equivalencia equivalencia) {
        this.equivalencias.add(equivalencia);
    }

    public void agregaRestriccionLike(RestriccionLike restricccion) {
        this.restriccionesLike.add(restricccion);
    }

    public void agregaRestriccionIgual(RestriccionIgual restriccion) {
        this.restriccionesIguales.add(restriccion);
    }
    public void agregaRestriccionNoIgual(RestriccionNoIgual restriccion){
        this.restriccionesNoIgual.add(restriccion);
    }

    public void agregaRestriccionSql(String restriccion) {
        this.restriccionesSql.add(restriccion);
    }

    public void agregaRestriccionBetweenDate(RestriccionBetweenDate restriccionBetweenDate) {
        this.restriccionesBetweenDate.add(restriccionBetweenDate);
    }

    public void agregaAlias(Alias alias) {
        this.alias.add(alias);
    }
    
    public void agregaSuma(Suma suma) {
        this.sumas.add(suma);
    }
    
    public void agregaGrupo(Grupo grupo) {
        this.grupos.add(grupo);
    }

    public int getPagina() {
        return pagina;
    }

    public void setPagina(int pagina) {
        this.pagina = pagina;
    }

    public int getRegistrosPorPagina() {
        return registrosPorPagina;
    }

    public void setRegistrosPorPagina(int registrosPorPagina) {
        this.registrosPorPagina = registrosPorPagina;
    }

    public void setRestriccionesIguales(List<RestriccionIgual> restriccionesIguales) {
        this.restriccionesIguales = restriccionesIguales;
    }

    public List<RestriccionNoIgual> getRestriccionesNoIgual() {
        return restriccionesNoIgual;
    }

    public void setRestriccionesNoIgual(List<RestriccionNoIgual> restriccionesNoIgual) {
        this.restriccionesNoIgual = restriccionesNoIgual;
    }
    

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Consulta)) {
            return false;
        }
        Consulta other = (Consulta) object;
        boolean esIgual = this.getEquivalencias().size() == other.getEquivalencias().size()
                && this.getOrdenAscendentes().size() == other.getOrdenAscendentes().size()
                && this.getAlias().size() == other.getAlias().size()
                && this.getRestriccionesIguales().size() == other.getRestriccionesIguales().size()
                && this.getRestriccionesNoIgual().size() == other.getRestriccionesNoIgual().size()
                && this.getRestriccionesLike().size() == other.getRestriccionesLike().size()
                && this.getRestriccionesSql().size() == other.getRestriccionesSql().size()
                && this.getRestriccionesBetweenDate().size() == other.getRestriccionesBetweenDate().size()                
                && this.getGrupos().size()==other.grupos.size()
                && this.getSumas().size()==other.sumas.size();

        return esIgual;
    }

}
