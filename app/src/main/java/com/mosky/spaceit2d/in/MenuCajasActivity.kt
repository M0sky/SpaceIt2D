package com.mosky.spaceit2d.`in`

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mosky.spaceit2d.util.MyInterfaceCajas
import com.mosky.spaceit2d.util.MyInterfaceEditCajas
import com.mosky.spaceit2d.R
import com.mosky.spaceit2d.adapter.CajaAdapter
import com.mosky.spaceit2d.databinding.ActivityMenuCajasBinding
import com.mosky.spaceit2d.`in`.MenuCajasEditActivity.Companion.setInterfaceInstanceCajasEditActivity
import com.mosky.spaceit2d.provider.Caja
import com.mosky.spaceit2d.provider.CajaProvider

class MenuCajasActivity : AppCompatActivity() , MyInterfaceEditCajas {

    private lateinit var binding: ActivityMenuCajasBinding

    private var miCajaSelected : Caja? = null
    private var cajasList : ArrayList<Caja>? = null
    private var contadorCajas : Int = 0

    companion object {
        var TESTEDITABLE = "miEditableKey"
        private var myInterfaceCajasActivity: MyInterfaceCajas? = null
        fun setInterfaceInstanceCajasActivity(contex: Context) {
            myInterfaceCajasActivity = contex as MyInterfaceCajas?
        }
    }

    @Suppress("DEPRECATION")
    private inline fun <reified T: Parcelable> Intent.getParcelableArrayListExtraProvider(identifierParameter: String): ArrayList<T>? {
        return if (Build.VERSION.SDK_INT >= 33) {
            this.getParcelableArrayListExtra(identifierParameter, T::class.java)
        } else {
            this.getParcelableArrayListExtra(identifierParameter)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuCajasBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initRecyclerView()
        enableEdgeToEdge()

        val toggle = supportActionBar
        toggle!!.setDisplayHomeAsUpEnabled(true)
        toggle.title = "Cajas"

        contadorCajas = intent.getIntExtra("contador", 0)
        if (intent.hasExtra(MenuActivity.TESTCAJAS)) {
            cajasList = intent.getParcelableArrayListExtraProvider(MenuActivity.TESTCAJAS)
        } else {
            Toast.makeText(baseContext, "Lista vacia", Toast.LENGTH_SHORT).show()
        }

        val txtNombreCaja : TextView = findViewById(R.id.text_nombreCaja)
        val txtPesoMaxCaja : TextView = findViewById(R.id.text_pesoMaxCaja)
        val txtLimiteCaja : TextView = findViewById(R.id.text_limiteCaja)
        val btnAddCaja : Button = findViewById(R.id.button_addCajas)
        val btnEditCaja : Button = findViewById(R.id.button_editCajas)
        val btnDeleteCaja : Button = findViewById(R.id.button_deleteCajas)


        btnEditCaja.setOnClickListener {
            if (miCajaSelected != null) {
                setInterfaceInstanceCajasEditActivity(this@MenuCajasActivity)
                val intent = Intent(this@MenuCajasActivity, MenuCajasEditActivity::class.java)
                intent.putExtra(TESTEDITABLE, miCajaSelected)
                startActivity(intent)
            } else {
                Toast.makeText(baseContext, "Error: Seleccione una caja",Toast.LENGTH_SHORT).show()
            }
        }

        btnDeleteCaja.setOnClickListener {
            if (miCajaSelected != null) {
                cajasList?.remove(miCajaSelected)
                miCajaSelected = null

                if (cajasList != null) {
                    CajaProvider.myCajas = cajasList as ArrayList<Caja>
                }
                initRecyclerView()
            } else {
                Toast.makeText(baseContext, "Error: Seleccione una caja",Toast.LENGTH_SHORT).show()
            }
        }

        btnAddCaja.setOnClickListener {
            val nombre = txtNombreCaja.text.toString()
            val pesoMax = txtPesoMaxCaja.text.toString()
            val limite = txtLimiteCaja.text.toString()

            if (nombre.isStringNotEmpty()) {
                if (pesoMax.isStringNotEmpty() && isNumeric(pesoMax)) {
                    if (limite.isStringNotEmpty() && isNumeric(limite)) {
                        addCaja(nombre, pesoMax.toInt(), limite.toInt(), contadorCajas, cajasList)
                    } else {
                        Toast.makeText(baseContext, "Error : Límite inválido", Toast.LENGTH_SHORT).show()
                        txtLimiteCaja.requestFocus()
                    }
                } else {
                    Toast.makeText(baseContext, "Error : Peso Max inválido", Toast.LENGTH_SHORT).show()
                    txtPesoMaxCaja.requestFocus()
                }
            } else {
                Toast.makeText(baseContext, "Error : Nombre inválido", Toast.LENGTH_SHORT).show()
                txtNombreCaja.requestFocus()
            }
        }
    }

    private fun initRecyclerView() {
        if (CajaProvider.myCajas.size > 1) CajaProvider.myCajas.sortByDescending {it.pesoMax}

        binding.recyclerCajas.layoutManager = LinearLayoutManager(this.binding.recyclerCajas.context)
        binding.recyclerCajas.adapter = CajaAdapter(CajaProvider.myCajas){ caja ->
            onItemSelected(
                caja
            )
        }
    }

    private fun onItemSelected(caja: Caja) {
        miCajaSelected = caja
    }

    private fun addCaja(nombre: String, pesoMax: Int, limite: Int, contadorCajasAdd: Int, listaCajasAdd: ArrayList<Caja>?) {
        if (pesoMax < 1) {
            Toast.makeText(baseContext, "Error: Peso inválido", Toast.LENGTH_SHORT).show()
        } else {
            val auxCountCaja = contadorCajasAdd + 1
            val auxCaja = Caja(auxCountCaja, pesoMax, 0, 40, 40, 1600, 0, limite, null, "@drawable/ic_baseline_caja.xml", nombre)

            val auxListCaja : ArrayList<Caja> = ArrayList()
            auxListCaja.add(auxCaja)

            if (listaCajasAdd != null) {
                auxListCaja.addAll(listaCajasAdd)
                cajasList = auxListCaja
                CajaProvider.myCajas = auxListCaja
                contadorCajas = auxCountCaja
                CajaProvider.myCajasCount = auxCountCaja
            }
            initRecyclerView()
        }
    }

    override fun onSupportNavigateUp(): Boolean{
        finish()
        myInterfaceCajasActivity!!.callBackCajas(cajasListAux = cajasList, cajasCountAux = contadorCajas)
        return true
    }

    private fun String?.isStringNotEmpty(): Boolean {
        return !this.isNullOrEmpty()
    }

    private fun isNumeric(toCheck: String): Boolean {
        val v = toCheck.toIntOrNull()
        return when(v) {
            null -> false
            else -> true
        }
    }

    override fun callBackCajas(myNewCaja: Caja?) {

        for (item in cajasList!!) {
            if (item.idC == myNewCaja!!.idC) {
                if (!item.nombre.equals(myNewCaja.nombre)) item.nombre = myNewCaja.nombre
                if (item.pesoMax != myNewCaja.pesoMax) item.pesoMax = myNewCaja.pesoMax
                if (item.limBultos != myNewCaja.limBultos) item.limBultos = myNewCaja.limBultos
                if (item.dimensionX != myNewCaja.dimensionX || item.dimensionY != myNewCaja.dimensionY) {
                    item.dimensionX = myNewCaja.dimensionX
                    item.dimensionY = myNewCaja.dimensionY
                    item.areaMax = item.dimensionX * item.dimensionY
                }
            }
        }

        CajaProvider.myCajas = cajasList as ArrayList<Caja>

        initRecyclerView()
        miCajaSelected = null
    }

}