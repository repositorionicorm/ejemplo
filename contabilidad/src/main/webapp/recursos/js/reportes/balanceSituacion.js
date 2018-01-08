function loadReporteSituacion(){
    $(".barraMenu").removeClass('active');
     $("#menuReportes").addClass('active');
     $("#cmbPeriodo,#cmbMoneda").change(function(){
        $("#btnExportarExcel").addClass('hide');
    });
    $("#btnBuscarBalance").click(buscarBalance);
}
function buscarBalance(){
    var periodo= $("#cmbPeriodo").val();
    var moneda=$("#cmbMoneda").val();
    var url= "/contabilidad/reportes/reporteBalanceSituacion/"+ periodo +"/"+moneda+"/1"+ "/";
    var id="rptBalanceSituacion";
    buscarReporte(url,id,"#btnBuscarBalance","#btnExportarExcel");
}