function loadReporteConfiguracion() {
    $("#btnTraerReporteConfiguracion").click(traerReporteConfiguracion);
    $("#mensajeReporteConfiguracion").html("");
    $(".barraMenu").removeClass('active');
    $("#menuReportes").addClass('active');
}
function traerReporteConfiguracion() {
    var reporteId = $("#cmbReporteTipo").val();
    var nombreReporte = $("#cmbReporteTipo :selected").html();
    $("#tablaReporteConfiguracion,#tablaReporteCuentas").bootstrapTable('destroy');
    mostrarMensajeCargando(Mensajes.general_cargando, "#mensajeReporteConfiguracion", "h4");
    $("#btnTraerReporteConfiguracion").attr('disabled', 'disabled');
    $.ajax({
        url: urlBaseGeneral + 'reportes/traerConfiguracionReporte/' + reporteId,
        success: function (data, textStatus, jqXHR) {
            if (data.tipoMensaje != tipoMensaje.EXITO) {
                mostrarMensajesContenedor(data.tipoMensaje, data.mensaje, "#mensajeReporteConfiguracion");
                return;
            }
            $("#mensajeReporteConfiguracion").html("");
            $("#btnTraerReporteConfiguracion").removeAttr('disabled');
            $("#tablaReporteCuentas").bootstrapTable({
                columns: [
                    {
                        field: 'cuenta',
                        title: 'Cuenta',
                        halign: 'center'
                    }
                ],
                height: 460
            });
            $("#tablaReporteConfiguracion").bootstrapTable({
                columns: [
                    [
                        {
                            title: '<span class="text-info">' + nombreReporte + '</span>',
                            colspan: 5,
                            align: 'center'
                        }
                    ],
                    [
                        {
                            field: 'fila',
                            title: 'Item',
                            width: '2%',
                            formatter: mostrarIndex,
                            align: 'center'
                        },
                        {
                            field: 'descripcion',
                            title: 'Descripcion',
                            width: '47%',
                            halign: 'center'
                        },
                        {
                            field: 'rango',
                            title: 'Cálculo',
                            width: '17%',
                            formatter: mostrarCalculo,
                            align: 'center'

                        },
                        {
                            field: 'operacionSoles',
                            title: 'Op.Soles',
                            width: '12%',
                            align: 'center'
                        },
                        {
                            field: 'operacionDolares',
                            title: 'Op.Dolares',
                            width: '12%',
                            align: 'center'
                        }
                    ]],
                height: 460

            }).on('click-row.bs.table', function (e, row, $element) {
                $("#tablaReporteCuentas").bootstrapTable('load', row.listaPorFecha);
                $($element).addClass('filaSeleccionada').siblings().removeClass("filaSeleccionada");
            });
            $("#tablaReporteConfiguracion").bootstrapTable('load', data.listaObjetos);
        },
        error: function () {
            mostrarMensajesContenedor(tipoMensaje.ERROR, Mensajes.general_error_inesperado, "#mensajeReporteConfiguracion");
        }


    });
    $("#contenedorTabla").removeClass('hide');


}
function mostrarCalculo(value, row, index) {
    if (value == null) {
        return "Saldo Final";
    }
    return value;
}