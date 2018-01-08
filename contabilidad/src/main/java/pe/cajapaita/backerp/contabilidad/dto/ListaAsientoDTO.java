/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.dto;

import java.util.List;

/**
 *
 * @author dev-out-02
 */
public class ListaAsientoDTO {
    private List<ArchivoSIAFCDTO> listaAsientos;

    public List<ArchivoSIAFCDTO> getListaAsientos() {
        return listaAsientos;
    }

    public void setListaAsientos(List<ArchivoSIAFCDTO> listaAsientos) {
        this.listaAsientos = listaAsientos;
    }
    
}
