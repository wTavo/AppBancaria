package com.example.appbancaria

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class adapterMisCuentas(
    var context: Context?,
    var arrayList: ArrayList<CuentasClass>,
    var correo: String
) : BaseAdapter() {

    init {
        arrayList = ArrayList(arrayList.filter { it.aux_correo == correo })
    }

    override fun getCount(): Int {
        return arrayList.size
    }

    override fun getItem(position: Int): Any {
        return arrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
        var inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var row = inflater.inflate(R.layout.row_list_mis_cuentas, null)
        var numero_cuenta = row.findViewById<TextView>(R.id.tvNumeroCuenta)
        var saldo_inicial = row.findViewById<TextView>(R.id.tvSaldo)
        var fondo = row.findViewById<TextView>(R.id.textView3)
        var saldoDisponible = row.findViewById<TextView>(R.id.textView6)
        var nombreCuenta = row.findViewById<TextView>(R.id.textView4)

        saldo_inicial.text = "$" + arrayList[position].saldo_inicial
        numero_cuenta.text = arrayList[position].numero_cuenta.toString()

        return row
    }
}


