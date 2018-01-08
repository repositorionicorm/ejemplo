package pe.cajapaita.backerp.contabilidad.dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import pe.cajapaita.backerp.contabilidad.dto.CuentaBatchDTO;
import pe.cajapaita.backerp.contabilidad.dto.DetalleDTO;
import pe.cajapaita.backerp.contabilidad.entidad.Integracion;
import pe.cajapaita.backerp.contabilidad.entidad.Saldo;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;
import pe.cajapaita.backerp.contabilidad.infraestructura.Helper;
import pe.cajapaita.backerp.contabilidad.infraestructura.Mensaje;

/**
 *
 * @author dev-out-02
 */
public class RepositorioBaseBatchDao implements IRepositorioBaseBatchDao {

    private JdbcTemplate jdbcTemplate;
    private final Logger logger = Logger.getLogger(RepositorioBaseBatchDao.class);

    public RepositorioBaseBatchDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void insertarBatchSaldo(List<Saldo> listaSaldos) throws ExcepcionNegocio {
        String sql = "INSERT INTO erp.cntsaldo "
                + "(ID,SALDOINICIAL,TOTALDEBE,TOTALHABER,SALDOFINAL,CUENTAID,MONEDAID,PERIODOID) "
                + "VALUES (?,?,?,?,?,?,?,?)";

        this.jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                try {
                    Saldo saldo = listaSaldos.get(i);
                    ps.setInt(1, saldo.getId());
                    ps.setBigDecimal(2, saldo.getSaldoFinal());
                    ps.setBigDecimal(3, saldo.getTotalDebe());
                    ps.setBigDecimal(4, saldo.getTotalHaber());
                    ps.setBigDecimal(5, saldo.getSaldoFinal());
                    ps.setInt(6, saldo.getCuenta().getId());
                    ps.setInt(7, saldo.getMoneda().getId());
                    ps.setInt(8, saldo.getPeriodo().getId());
                } catch (SQLException ex) {
                    logger.error(ex.getMessage());
                    throw ex;
                }
            }

            @Override
            public int getBatchSize() {
                return listaSaldos.size();
            }

        });
    }

    @Override
    public void insertarBatchIntegracion(List<Integracion> listaIntegracion) throws ExcepcionNegocio {
        String sql = "INSERT INTO erp.cntintegracion "
                + "(ID,PROCEDENCIAID,TIPOASIENTOID,FECHA,OBSERVACION,ESTADOID,CODIGOASIENTO,AGENCIAID)"
                + " VALUES(?,?,?,?,?,?,?,?)";

        this.jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                try {
                    Integracion integracion = listaIntegracion.get(i);
                    ps.setInt(1, integracion.getId());
                    ps.setInt(2, integracion.getProcedencia().getId());
                    ps.setInt(3, integracion.getTipoAsiento().getId());
                    ps.setDate(4, new java.sql.Date(integracion.getFecha().getTime()));
                    ps.setString(5, integracion.getObservacion());
                    ps.setInt(6, integracion.getEstado().getId());
                    ps.setString(7, integracion.getAsientoId());
                    ps.setInt(8, integracion.getAgencia().getId());
                } catch (SQLException ex) {
                    logger.error(ex.getMessage());
                    throw ex;
                }

            }

            @Override
            public int getBatchSize() {
                return listaIntegracion.size();
            }
        });

    }

    @Override
    public CuentaBatchDTO traerCuentaPorId(int id) {
        try {
            String sql = "SELECT id,esanalitica,estadoid,cuenta from  erp.cntcuenta where id=?";
            CuentaBatchDTO cuenta = (CuentaBatchDTO) this.jdbcTemplate.queryForObject(sql, new Object[]{id},
                    new BeanPropertyRowMapper(CuentaBatchDTO.class));
            return cuenta;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }

    }

    @Override
    public CuentaBatchDTO traerCuentaPorCuenta(String cuenta) {
        try {
            String sql = "SELECT id,esanalitica,estadoid,cuenta from  erp.cntcuenta where cuenta=?";
            CuentaBatchDTO cuentaDTO = (CuentaBatchDTO) this.jdbcTemplate.queryForObject(sql, new Object[]{cuenta},
                    new BeanPropertyRowMapper(CuentaBatchDTO.class));
            return cuentaDTO;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }

    }

    @Override
    public List<DetalleDTO> traerListaDetalle(int cuenta, Date fechaInicial, Date fechaFinal, int estadoId) throws ExcepcionNegocio{
        String sql = "select det.debe,det.haber"
                + "  from erp.cntdetalle det, erp.cntcuenta cue,erp.cntasiento asi "
                + " where det.cuentaid = cue.id "
                + "   and cue.cuenta like '"+cuenta+"%' "
                + "   and asi.fecha between '"+Helper.convertirAFecha(fechaInicial)+"' and '"+Helper.convertirAFecha(fechaFinal)+"' "
                + "   and asi.id= det.asientoid "
                + " and asi.estadoid="+estadoId;
         List<DetalleDTO> listaDetalle= new ArrayList<>();
        try{
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
       
        for (Map row : rows) {

            DetalleDTO detalle = new DetalleDTO();

            

            detalle.setDebe((BigDecimal)row.get("debe"));

            detalle.setHaber((BigDecimal) row.get("haber"));

            listaDetalle.add(detalle);

        }}
        catch(Exception ex){
            logger.error(ex.getMessage());
            logger.info("Error Jdbc Para cuentas de orden");
            throw  new ExcepcionNegocio(Mensaje.ERROR_GENERAL,Mensaje.TIPO_ERROR);
        }

        
       // List<DetalleDTO> listaDetalle =(List<DetalleDTO>) this.jdbcTemplate.queryForList(sql, DetalleDTO.class) ;

        return listaDetalle;
    }
}
