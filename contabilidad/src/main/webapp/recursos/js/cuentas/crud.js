var cuentaActual = {};
var cuentaOperacion={};
var CuentaDTO = function (id, cuenta, descripcion, idPadre,tipoCuenta,Sbs,analitica) {
    this.id = id;
    this.cuenta = cuenta;
    this.descripcion = descripcion;
    this.tipoCuentaDescripcion=tipoCuenta;
    this.esSbs=Sbs;
    this.padreId = idPadre; 
    this.esAnalitica=analitica;
};
function actualizarCuenta(campoBuscar, pagina) {
    $formActualizar.validate();
    var padreId=cuentaOperacion.padreId;
    var tipoCuenta=cuentaOperacion.tipoCuentaDescripcion;
    var Sbs=cuentaOperacion.esSbs;
    var analitica=cuentaOperacion.analitica;
    var id = cuentaOperacion.id;
    var numCuenta = $("#numCuentaEditar").val();
    var descripcion = $("#descripcionEditar").val();
    var cuentaJson = null;
    if ($formActualizar.isValid()) {
         cuentaJson = JSON.stringify(new CuentaDTO(id, numCuenta, descripcion, padreId,tipoCuenta,Sbs,analitica));
        $.ajax({
            type: 'PUT',
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            url: "actualizarCuenta",
            data: cuentaJson,
            success: function (data) {
                mostrarMensajes(data.tipoMensaje, data.mensaje,  'body');
                cargarTabla(campoBuscar, pagina)
            },
            error: function (err) {
                mostrarMensajes("Ocurrio un error: "+err);
            }
        });
        $("#cuentasListadas,#cuentasSeleccionadas").html("");
        $("#modalEditarCuenta").modal("hide");
        return true;
    }
    return false;  
};
function eliminarCuenta(campoBuscar, pagina) {
    var id = cuentaOperacion.id;
    $.ajax({
        type: 'DELETE',
        url: "eliminarCuenta/" + id,
        success: function (data) {
            mostrarMensajes(data.tipoMensaje, data.mensaje,  'body');
            cargarTabla(campoBuscar,pagina);
        },
        error: function (er) {
            mostrarMensajes("Ha ocurrido un error: " + er);
        }
    });    
}