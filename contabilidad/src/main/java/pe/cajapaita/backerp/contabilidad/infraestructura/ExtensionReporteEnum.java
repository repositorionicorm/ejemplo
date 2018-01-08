/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.infraestructura;

/**
 *
 * @author dev-out-02
 */
public enum ExtensionReporteEnum {
    PDF("PDF", "application/pdf", "pdf",""),
    EXCEL("EXCEL", "application/vnd.ms-excel", "xls",""),
    ZIP_X("EXCEL", "application/zip", "zip",".xls");
    
    private String valor;
    private String content;
    private String extension;
    private String extensionComprimido;

    private ExtensionReporteEnum(String valor, String content, String extension,String extensionComprimido) {
        this.valor = valor;
        this.content = content;
        this.extension = extension;
        this.extensionComprimido=extensionComprimido;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getExtensionComprimido() {
        return extensionComprimido;
    }

    public void setExtensionComprimido(String extensionComprimido) {
        this.extensionComprimido = extensionComprimido;
    }
    

}
