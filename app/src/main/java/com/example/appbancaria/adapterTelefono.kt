package com.example.appbancaria

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class adapterTelefono(
    var context: Context?,
    var arrayList: ArrayList<UsuariosClass>,
    var miCorreo: String
) : BaseAdapter() {

    init {
        arrayList = ArrayList(arrayList.filter { it.correo == miCorreo })
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
        var row = inflater.inflate(R.layout.row_list_telefono, null)
        var numero_telefono = row.findViewById<TextView>(R.id.tvNumeroTelefono)
        var fondo = row.findViewById<TextView>(R.id.textView3)
        var nombreCuenta = row.findViewById<TextView>(R.id.textView4)

        numero_telefono.text = arrayList[position].numero_telefono.toString()

        return row
    }
}



