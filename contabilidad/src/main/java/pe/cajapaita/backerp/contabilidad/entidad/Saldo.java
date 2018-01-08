/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.entidad;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author hnole
 */
@Entity
@Table(name = "CNTSALDO")
@XmlRootElement
public class Saldo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    @TableGenerator(name = "tableGenerator",
            table = "clave",
            pkColumnName = "entidad",
            valueColumnName = "siguienteId",
            pkColumnValue = "CNTSALDO",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "tableGenerator")
    private int id;

    @Basic(optional = false)
    @NotNull
    @Column(name = "SALDOINICIAL")
    private BigDecimal saldoInicial;

    @Basic(optional = false)
    @NotNull
    @Column(name = "TOTALDEBE")
    private BigDecimal totalDebe;

    @Basic(optional = false)
    @NotNull
    @Column(name = "TOTALHABER")
    private BigDecimal totalHaber;

    @Basic(optional = false)
    @NotNull
    @Column(name = "SALDOFINAL")
    private BigDecimal saldoFinal;

    @JoinColumn(name = "PERIODOID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Periodo periodo;

    @JoinColumn(name = "MONEDAID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Moneda moneda;

    @JoinColumn(name = "CUENTAID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Cuenta cuenta;

    public Saldo() {
        this.saldoInicial = new BigDecimal(0);
        this.totalDebe = new BigDecimal(0);
        this.totalHaber = new BigDecimal(0);
        this.saldoFinal = new BigDecimal(0);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public BigDecimal getTotalDebe() {
        return totalDebe;
    }

    public void setTotalDebe(BigDecimal totalDebe) {
        this.totalDebe = totalDebe;
    }

    public BigDecimal getTotalHaber() {
        return totalHaber;
    }

    public void setTotalHaber(BigDecimal totalHaber) {
        this.totalHaber = totalHaber;
    }

    public BigDecimal getSaldoFinal() {
        return saldoFinal;
    }

    public void setSaldoFinal(BigDecimal saldoFinal) {
        this.saldoFinal = saldoFinal;
    }

    public Periodo getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Periodo periodo) {
        this.periodo = periodo;
    }

    public Moneda getMoneda() {
        return moneda;
    }

    public void setMoneda(Moneda moneda) {
        this.moneda = moneda;
    }

    public Cuenta getCuenta() {
        return cuenta;
    }

    public void setCuenta(Cuenta cuenta) {
        this.cuenta = cuenta;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Saldo)) {
            return false;
        }
        Saldo other = (Saldo) object;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + this.id;
        return hash;
    }

    @Override
    public String toString() {
        return "pe.cajapaita.backerp.contabilidad.entidad.Saldo[ id=" + id + " ]";
    }

    public void calcularSaldoFinal() {
        this.saldoFinal = this.saldoInicial;
        if (this.cuenta.getTipoCuenta().getSignoDebe().equals("+")) {
            this.saldoFinal = this.saldoFinal.add(this.totalDebe);
        } else {
            this.saldoFinal = this.saldoFinal.subtract(this.totalDebe);
        }

        if (this.cuenta.getTipoCuenta().getSignoHaber().equals("+")) {
            this.saldoFinal = this.saldoFinal.add(this.totalHaber);
        } else {
            this.saldoFinal = this.saldoFinal.subtract(this.totalHaber);
        }
    }

}
