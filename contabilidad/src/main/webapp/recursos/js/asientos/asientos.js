var totalDebe = 0;
var totalHaber = 0;

function llenaCombosExtraData(contenedor, url, id, value, addData,funcionEjecutar) {
   
    $.when($.getJSON(url,function(data){
        $.each(data, function (i, item) {
            $(contenedor).append('<option id="' + addData + i + '" value="' + item[id] + '">' + item[value] + '</option>');
            $("#" + addData + i).data(addData, item[addData]);
            
        });
        funcionEjecutar();
    }));
}

//FUNCION PARA CALCULAR EL TOTAL DE UN DETALLE DE ASIENTOS
function sumFormatter(data) {
    var field = this.field;
    var total_sum = 0;
    var i = 0;
    total_sum = data.reduce(function (sum, row) {
        i++;
        return (sum) + ((row[field]) || 0);
    }, 0);
    if (field === "debe")
        totalDebe = formatoMoneda(total_sum);
    else
        totalHaber = formatoMoneda(total_sum);
    if (total_sum === 0 && i > 0)
        total_sum = "0.00";
    if (total_sum>0 && i > 0)
        return formatoMoneda(total_sum);
    else
        return total_sum;
}