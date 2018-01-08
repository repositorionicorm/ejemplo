var $formBuscarCtas;
var $tablaCuentas;
var cuentaGlobal = "";
var tipoUsuario="S";
function iniciar() {
    $("#menuCuentas").addClass('active');
    $formBuscarCtas = $("#formBuscarCtaPlan").data('bootstrapValidator');
    $tablaCuentas = $("#tablaCuentasPlan");
    $formActualizar = $("#formEditarCuenta").data('bootstrapValidator');
    $("#btnEditarCuenta").click(llamarActualizar);
    $('#modalEditarCuenta').on('shown.bs.modal', function () {
        $("#descripcionEditar").focus();
    })
    
    $("#cuenTaInicioPlan").keydown(function(e){
         if (!e)
            e = window.event;
        var keyCode = e.keyCode || e.which;
        if (keyCode == '13') {
            buscarPlanContable();
            return false;
        } 
    });
}
$("#buscarPlanContable").click(buscarPlanContable);
$("#btnEliminarCuenta").click(llamarEliminar);

$(document).ready(iniciar);
function buscarPlanContable() {
    $("#contenedorTabla").addClass("hide");
    var cuenta = $("#cuenTaInicioPlan").val();

    $tablaCuentas.bootstrapTable('destroy');
    cuentaGlobal = cuenta;
    $formBuscarCtas.validate();
    if ($formBuscarCtas.isValid()) {
        cargarTabla(cuenta, 1);
    }
}
function cargarTabla(cuenta, pagina) {
    var miData;
    $("#contenedorTabla").addClass('hide');
    $("#paginacion").off("page");
    $.getJSON("/contabilidad/cuentas/traerPorCuenta/" + cuenta + "/" + pagina, function (data) {
        miData = data.listaObjetos;
        mostrarMensajesContenedor(data.tipoMensaje, data.mensaje, "#errorConsulta");
        if (miData != null)
        {
            if (data.listaObjetos != null)
                $("#contenedorTabla").removeClass('hide');
                tipoUsuario=data.objeto;
            $tablaCuentas.bootstrapTable({
                columns: [
                    {
                        field: 'id',
                        title: '',
                        visible: false,
                    },
                    {
                        field: 'cuenta',
                        title: 'N° Cuenta',
                        width: '20%'
                    },
                    {
                        field: 'descripcion',
                        title: 'Descripcion'
                    },
                    {
                        title: 'Analítica',
                        field:'esAnalitica',
                        align: 'center'
                    },
                    {
                        field: 'operate',
                        title: '',
                        formatter: operateListaCuentas,
                        align: 'center',
                        width: '5%',
                        events: operaciones
                    }
                ],
                uniqueId: 'id',
                search: false,
                height: 450
            });
            $tablaCuentas.bootstrapTable('load', miData);
            $tablaCuentas.bootstrapTable('refresh');
            $("#contenedorTabla").removeClass("hide");
            $("#paginacion").bootpag({
                total: data.totalPaginas,
                maxVisible: 10,
                page: pagina
            }
            ).on("page", function (event, num) {
                cargarTabla(cuenta, num);
            });
        }
    });
}

function llamarEliminar() {
    eliminarCuenta(cuentaGlobal, 1);
}
var operaciones = {
    'click .editar': function (e, value, row, index) {
        cuentaOperacion = row;
            $("#numCuentaEditar").attr("disabled", "disabled");

        $(".spanCodigoCuenta").html(row.cuenta);
        $(".spanCodigoCuenta").data("id", row.id);
        $("#numCuentaEditar").val(row.cuenta);
        $("#descripcionEditar").val(row.descripcion);
    },
    'click .eliminar': function (e, value, row, index) {
        cuentaOperacion = row;
        $(".spanCodigoCuenta").html(row.cuenta + ' - ' + row.descripcion);
        $(".spanCodigoCuenta").data("id", row.id);
    }
};
function operateListaCuentas(value, row, index) {
    var eliminar;
    if(tipoUsuario=='S'){
        return '';
    }
    if (row.esAnalitica == 'S') {
        eliminar = '<li><a data-toggle="modal" data-target="#modalEliminarCuenta" href="#" class="eliminar"  >Eliminar</a></li>';
    } else
        eliminar = ''
    return [
        '<div class="dropdown " >',
        '<button class="btn btn-xs btn-posicion btn-default dropdownMenu1 dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" >',
        '<span class="glyphicon glyphicon-cog"></span>',
        '<span class="caret"></span>',
        '</button>',
        '<ul class="dropdown-menu" style="position: absolute;left: -120px;"  aria-labelledby="dropdownMenu1">',
        '<li><a  class="editar" data-toggle="modal"  data-target="#modalEditarCuenta" href="#">Editar </a></li>',
        eliminar,
        '</ul>',
        '</div>'
    ].join('');
}
function llamarActualizar() {
    actualizarCuenta(cuentaGlobal, 1);
};