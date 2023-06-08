package mis.finanzas.diarias.model

data class CompleteRecord(
    var ID: Int,
    var fecha: String,
    var id_moneda: String?,
    var amount: Float = 0f,
    var motivo: String,
    var ingreso: String,
    var signo: String? = null,
    var currencyName: String?,
    var currencySymbol: String?,
    var tag: String?,
    var formattedAmount: String?
)