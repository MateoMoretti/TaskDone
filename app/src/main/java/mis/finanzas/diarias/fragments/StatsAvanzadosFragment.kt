package mis.finanzas.diarias.fragments

import android.annotation.SuppressLint
import androidx.annotation.RequiresApi
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import mis.finanzas.diarias.Ads
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.taskdone.R
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.taskdone.databinding.FragmentStatsAvanzadosBinding
import mis.finanzas.diarias.Utils
import mis.finanzas.diarias.activities.ActivityFinanzas
import mis.finanzas.diarias.viewmodels.DatabaseViewModel
import mis.finanzas.diarias.viewmodels.DatabaseViewmodelFactory
import java.util.*

class StatsAvanzadosFragment : Fragment() {
    private var binding: FragmentStatsAvanzadosBinding? = null
    private val databaseViewModel: DatabaseViewModel by viewModels{ DatabaseViewmodelFactory(requireContext()) }
    var monedas = ArrayList<String>()
    var cantidades = ArrayList<Float>()
    var simbolos = ArrayList<String>()
    var moneda_seleccionada = 0
    var year_seleccionado = Integer.toString(Calendar.getInstance()[Calendar.YEAR])
    var mes_seleccionado = Integer.toString(Calendar.getInstance()[Calendar.MONTH] + 1)

    //Se utiliza para que los spinners no ejecuten OnClickListener al iniciar el fragment
    var check_for_spinners = 0
    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStatsAvanzadosBinding.inflate(inflater, container, false)
        Ads.getInstance().cargarAnuncio(requireContext())

        /*val data = database!!.getMonedasByUserId(userViewModel.getId())
        while (data.moveToNext()) {
            monedas.add(data.getString(1))
            cantidades.add(data.getFloat(2))
            simbolos.add(data.getString(3))
        }*/
        if (!monedas.isEmpty()) {
            /*graficoBarrasGastosMensuales(
                year_seleccionado,
                "0",
                binding!!.chartGastosMensuales,
                monedas[0]
            )
            graficoBarrasGastosMensuales(
                year_seleccionado,
                "1",
                binding!!.chartIngresosMensuales,
                monedas[0]
            )*/
            /*graficoTortaGastosPorTags(
                year_seleccionado,
                mes_seleccionado,
                "0",
                binding!!.piechartGastosTag,
                monedas[0]
            )
            graficoTortaGastosPorTags(
                year_seleccionado,
                mes_seleccionado,
                "1",
                binding!!.piechartIngresosTag,
                monedas[0]
            )*/
        }
        val adapterSpinnerMonedas: ArrayAdapter<String?> = object : ArrayAdapter<String?>(
            requireActivity(),
            android.R.layout.simple_spinner_dropdown_item,
            monedas as List<String?>
        ) {
            @RequiresApi(api = Build.VERSION_CODES.O)
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                (v as TextView).textSize = 20f
                v.setTextAlignment(View.TEXT_ALIGNMENT_CENTER)
                return v
            }
        }
        adapterSpinnerMonedas.setDropDownViewResource(R.layout.spinner_dropdown)
        binding!!.spinnerMoneda.adapter = adapterSpinnerMonedas
        binding!!.spinnerMoneda.background =
            resources.getDrawable(R.drawable.fondo_blanco_redondeado)
        binding!!.spinnerMoneda.gravity = Gravity.CENTER
        binding!!.spinnerMoneda.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View,
                    i: Int,
                    l: Long
                ) {
                    if (check_for_spinners > 0) {
                        moneda_seleccionada = i
                        if (!monedas.isEmpty()) {
                            /*graficoBarrasGastosMensuales(
                                year_seleccionado,
                                "0",
                                binding!!.chartGastosMensuales,
                                monedas[moneda_seleccionada]
                            )
                            graficoBarrasGastosMensuales(
                                year_seleccionado,
                                "1",
                                binding!!.chartIngresosMensuales,
                                monedas[moneda_seleccionada]
                            )*/
                            /*graficoTortaGastosPorTags(
                                year_seleccionado,
                                mes_seleccionado,
                                "0",
                                binding!!.piechartGastosTag,
                                monedas[moneda_seleccionada]
                            )
                            graficoTortaGastosPorTags(
                                year_seleccionado,
                                mes_seleccionado,
                                "1",
                                binding!!.piechartIngresosTag,
                                monedas[moneda_seleccionada]
                            )*/
                        }
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }
        val years = ArrayList<String>()
        for (x in 2021 until Calendar.getInstance()[Calendar.YEAR] + 1) {
            years.add(Integer.toString(x))
        }
        val adapterSpinnerYear: ArrayAdapter<String?> = object : ArrayAdapter<String?>(
            requireActivity(),
            android.R.layout.simple_spinner_dropdown_item,
            years as List<String?>
        ) {
            @RequiresApi(api = Build.VERSION_CODES.O)
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                (v as TextView).textSize = 20f
                v.setTextAlignment(View.TEXT_ALIGNMENT_CENTER)
                return v
            }
        }
        adapterSpinnerYear.setDropDownViewResource(R.layout.spinner_dropdown)
        binding!!.spinnerYear.adapter = adapterSpinnerYear
        binding!!.spinnerYear.background = resources.getDrawable(R.drawable.fondo_blanco_redondeado)
        binding!!.spinnerYear.gravity = Gravity.CENTER
        binding!!.spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                if (check_for_spinners > 0) {
                    year_seleccionado = binding!!.spinnerYear.selectedItem.toString()
                    if (!monedas.isEmpty()) {
                        /*graficoBarrasGastosMensuales(
                            year_seleccionado,
                            "0",
                            binding!!.chartGastosMensuales,
                            monedas[moneda_seleccionada]
                        )
                        graficoBarrasGastosMensuales(
                            year_seleccionado,
                            "1",
                            binding!!.chartIngresosMensuales,
                            monedas[moneda_seleccionada]
                        )*/
                        /*graficoTortaGastosPorTags(
                            year_seleccionado,
                            mes_seleccionado,
                            "0",
                            binding!!.piechartGastosTag,
                            monedas[moneda_seleccionada]
                        )
                        graficoTortaGastosPorTags(
                            year_seleccionado,
                            mes_seleccionado,
                            "1",
                            binding!!.piechartIngresosTag,
                            monedas[moneda_seleccionada]
                        )*/
                    }
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        binding!!.spinnerYear.setSelection(Calendar.getInstance()[Calendar.YEAR] - 2021)
        val meses = ArrayList<String>()
        for (x in 0..11) {
            meses.add(Utils.getMesPorNumero(x))
        }
        val adapterSpinnerMeses: ArrayAdapter<String?> = object : ArrayAdapter<String?>(
            requireActivity(),
            android.R.layout.simple_spinner_dropdown_item,
            meses as List<String?>
        ) {
            @RequiresApi(api = Build.VERSION_CODES.O)
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                (v as TextView).textSize = 20f
                v.setTextAlignment(View.TEXT_ALIGNMENT_CENTER)
                return v
            }
        }
        adapterSpinnerMeses.setDropDownViewResource(R.layout.spinner_dropdown)
        binding!!.spinnerMes.adapter = adapterSpinnerMeses
        binding!!.spinnerMes.background = resources.getDrawable(R.drawable.fondo_blanco_redondeado)
        binding!!.spinnerMes.gravity = Gravity.CENTER
        binding!!.spinnerMes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                if (check_for_spinners > 0) {
                    mes_seleccionado = Integer.toString(i + 1)
                    if (!monedas.isEmpty()) {
                       /* graficoTortaGastosPorTags(
                            year_seleccionado,
                            mes_seleccionado,
                            "0",
                            binding!!.piechartGastosTag,
                            monedas[moneda_seleccionada]
                        )
                        graficoTortaGastosPorTags(
                            year_seleccionado,
                            mes_seleccionado,
                            "1",
                            binding!!.piechartIngresosTag,
                            monedas[moneda_seleccionada]
                        )*/
                    }
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        binding!!.spinnerMes.setSelection(mes_seleccionado.toInt() - 1)
        check_for_spinners = 1
        binding!!.back.setOnClickListener { v: View? ->
            (activity as ActivityFinanzas).onBackPressed()
        }
        return binding!!.root
    }

    /*@SuppressLint("SetTextI18n", "InflateParams")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun graficoBarrasGastosMensuales(
        year: String,
        ingreso: String,
        chart: BarChart,
        nombre_moneda: String
    ) {
        val gastos_por_mes =
            database!!.getTotalGastosMensualesPorYearMoneda(year, ingreso, nombre_moneda)
        val fecha_total_gasto = ArrayList<HashMap<Int, Float>>()
        while (gastos_por_mes.moveToNext()) {
            val fecha = gastos_por_mes.getString(1)
            val mes = fecha.substring(5, 7).toInt()
            val h = HashMap<Int, Float>()
            h[mes] = gastos_por_mes.getFloat(0)
            fecha_total_gasto.add(h)
        }
        val meses = ArrayList<String>()
        val gastos_mensuales_grafico = ArrayList<BarEntry>()
        for (x in 0..11) {
            val mes = Utils.getMesPorNumero(x).substring(0, 3)
            meses.add(mes.substring(0, 1).uppercase(Locale.getDefault()) + mes.substring(1))
            var gasto_mensual = 0f
            for (h in fecha_total_gasto) {
                //Ignorar warning, pues si lo contiene nunca dará error
                if (h.containsKey(x + 1)) {
                    gasto_mensual = h[x + 1]!!
                }
            }
            gastos_mensuales_grafico.add(BarEntry(x.toFloat(), gasto_mensual))
        }
        var barDataSet =
            BarDataSet(gastos_mensuales_grafico, resources.getString(R.string.gastos_por_mes))
        if (ingreso == "1") {
            barDataSet =
                BarDataSet(gastos_mensuales_grafico, resources.getString(R.string.ingresos_por_mes))
        }
        barDataSet.setColors(*ColorTemplate.MATERIAL_COLORS)
        barDataSet.valueTextColor = Color.BLACK
        barDataSet.valueTextSize = 16f
        val barData = BarData(barDataSet)
        chart.setFitBars(true)
        chart.data = barData
        chart.description.text = ""
        chart.animateY(2000)
        chart.setNoDataTextColor(Color.BLACK)
        val xAxis = chart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(meses)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.labelCount = meses.size
    }*/

    /*fun graficoTortaGastosPorTags(
        year: String?,
        mes: String?,
        ingreso: String,
        chart: PieChart,
        nombre_moneda: String?
    ) {
        val total_gastos_mes = database!!.getTotalGastosPorYearMesMoneda(
            year!!, mes!!, nombre_moneda!!, ingreso
        )
        var total_mes = 0f
        while (total_gastos_mes.moveToNext()) {
            total_mes = total_gastos_mes.getFloat(0)
        }
        val total_gastos_mes_por_tag = database!!.getTotalGastosPorYearMesMonedaGroupByTags(
            year, mes, nombre_moneda, ingreso
        )
        val entries = ArrayList<PieEntry>()
        while (total_gastos_mes_por_tag.moveToNext()) {
            entries.add(
                PieEntry(
                    total_gastos_mes_por_tag.getFloat(0),
                    total_gastos_mes_por_tag.getString(1)
                )
            )
            total_mes -= total_gastos_mes_por_tag.getFloat(0)
        }
        if (total_mes != 0f) {
            entries.add(PieEntry(total_mes, resources.getString(R.string.sin_tag)))
        }
        if (!entries.isEmpty()) {
            var pieDataSet = PieDataSet(entries, resources.getString(R.string.gastos_por_tags))
            chart.centerText = resources.getString(R.string.gastos_por_tags)
            chart.setCenterTextColor(Color.RED)
            pieDataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
            if (ingreso == "1") {
                pieDataSet = PieDataSet(entries, resources.getString(R.string.ingresos_por_tags))
                chart.centerText = resources.getString(R.string.ingresos_por_tags)
                pieDataSet.setColors(*ColorTemplate.MATERIAL_COLORS)
                chart.setCenterTextColor(Color.GREEN)
            }
            pieDataSet.valueTextColor = Color.WHITE
            pieDataSet.valueTextSize = 16f
            val pieData = PieData(pieDataSet)
            chart.setEntryLabelColor(Color.WHITE)
            chart.setHoleColor(Color.BLACK)
            chart.data = pieData
            chart.description.isEnabled = false
            chart.animate()
        } else {
            chart.setNoDataTextColor(Color.BLACK)
            chart.clear()
        }
    }*/

/*
    private void cargarTags(Calendar desde, Calendar hasta){
        Cursor tags = database.getTagsByUserId(UsuarioSingleton.getInstance().getID());
        while(tags.moveToNext()){
            tags_creados_por_usuario.add(tags.getString(1));
        }

        if(tags_seleccionados.isEmpty()){
            binding.tagsSeleccionados.setText(getResources().getString(R.string.sin_seleccionados));
        }
        else {
            binding.tagsSeleccionados.setText(Utils.arrayListToString(tags_seleccionados));
        }

        */
    /*Cursor total_by_tags = database.getTotalGastosGroupByTagIngresoMoneda(Utils.calendarToString(desde), Utils.calendarToString(hasta));
        Cursor gastos = database.getGastosBySessionUser(Utils.calendarToString(desde), Utils.calendarToString(hasta));
        while (gastos.moveToNext()) {
            Cursor tag = database.getTagsByGastoId(gastos.getInt(0));
            while (tag.moveToNext()){
                tags_array.add(tag.getString(1));
            }
        }*/
    /*
        tags_checkeados = new boolean[tags_creados_por_usuario.size()];
    }




    private void abrirSpinnerTags(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(requireActivity());
        mBuilder.setTitle(getResources().getString(R.string.seleciona));
        //i es la posicion del click, b es booleano. Por si ya está checkeado
        mBuilder.setMultiChoiceItems(tags_creados_por_usuario.toArray(new CharSequence[0]), tags_checkeados, (dialogInterface, i, b) -> {
            if(b) {
                tags_seleccionados_index.add(i);
            }
            else{
                tags_seleccionados_index.remove(Integer.valueOf(i));
            }

            tags_seleccionados.clear();
            for(int x =0;x!=tags_seleccionados_index.size();x++){
                tags_seleccionados.add(tags_creados_por_usuario.get(tags_seleccionados_index.get(x)));
            }
            if(tags_seleccionados.isEmpty()){
                binding.tagsSeleccionados.setText(getResources().getString(R.string.sin_seleccionados));
            }
            else {
                binding.tagsSeleccionados.setText(Utils.arrayListToString(tags_seleccionados));
            }
        });
        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton("OK", (dialogInterface, i) -> {
            StringBuilder item = new StringBuilder();
            for(int x = 0; x < tags_seleccionados_index.size(); x++){
                item.append(tags_creados_por_usuario.get(tags_seleccionados_index.get(x)));
                if(x != tags_seleccionados_index.size()-1){
                    item.append(", ");
                }

            }
        });

        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }*/
}