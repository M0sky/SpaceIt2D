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
import com.mosky.spaceit2d.util.MyInterfaceEditCajas
import com.mosky.spaceit2d.R
import com.mosky.spaceit2d.provider.Caja

class MenuCajasEditActivity : AppCompatActivity() {

    private var myEditableCaja : Caja? = null

    companion object {
        private var myInterfaceCajasEditActivity : MyInterfaceEditCajas? = null
        fun setInterfaceInstanceCajasEditActivity(contex: Context?) {
            myInterfaceCajasEditActivity = contex as MyInterfaceEditCajas?
        }
    }

    @Suppress("DEPRECATION")
    private inline fun <reified T: Parcelable> Intent.getParcelableExtraProvider(identifierParameter: String): T? {
        return if (Build.VERSION.SDK_INT >= 33) {
            this.getParcelableExtra(identifierParameter, T::class.java)
        } else {
            this.getParcelableExtra(identifierParameter)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu_cajas_edit)

        val btnConfirmarCaja : Button = findViewById(R.id.button_confirmEditCaja)
        val txtEditCajaNombre : TextView = findViewById(R.id.text_editCajaNombre)
        val txtEditCajaPesoMax : TextView = findViewById(R.id.text_editCajaPesoMax)
        val txtEditCajaLimite : TextView = findViewById(R.id.text_editCajaLimite)
        val txtEditCajaDimensionX : TextView = findViewById(R.id.text_editCajaDX)
        val txtEditCajaDimensionY : TextView = findViewById(R.id.text_editCajaDY)

        val toggle = supportActionBar
        toggle!!.setDisplayHomeAsUpEnabled(true)
        toggle.title = "Cajas"

        if (intent.hasExtra(MenuCajasActivity.TESTEDITABLE)) {
            myEditableCaja = intent.getParcelableExtraProvider(MenuCajasActivity.TESTEDITABLE)
        } else {
            Toast.makeText(baseContext, "Caja vacía", Toast.LENGTH_SHORT).show()
        }

        btnConfirmarCaja.setOnClickListener {
            val aux1 = txtEditCajaNombre.text.toString()
            val aux2 = txtEditCajaPesoMax.text.toString()
            val aux3 = txtEditCajaLimite.text.toString()
            val aux4 = txtEditCajaDimensionX.text.toString()
            val aux5 = txtEditCajaDimensionY.text.toString()

            if (aux1.isStringNotEmpty()) {
                myEditableCaja!!.nombre = aux1
            }

            if (aux2.isStringNotEmpty() && isNumeric(aux2)) {
                val v2 : Int = aux2.toInt()
                if (v2 > 0) {
                    myEditableCaja!!.pesoMax = v2
                }
            }

            if (aux3.isStringNotEmpty() && isNumeric(aux3)) {
                val v3 : Int = aux3.toInt()
                if (v3 > 0) {
                    myEditableCaja!!.limBultos = v3
                }
            }

            if (aux4.isStringNotEmpty() && isNumeric(aux4)) {
                val v4 : Int = aux4.toInt()
                if (v4 > 0) {
                    myEditableCaja!!.dimensionX = v4
                }
            }

            if (aux5.isStringNotEmpty() && isNumeric(aux5)) {
                val v5 : Int = aux5.toInt()
                if (v5 > 0) {
                    myEditableCaja!!.dimensionY = v5
                }
            }

            finish()
            myInterfaceCajasEditActivity!!.callBackCajas(myNewCaja = myEditableCaja)
        }
    }

    private fun isNumeric(toCheck: String): Boolean {
        val v = toCheck.toIntOrNull()
        return when(v) {
            null -> false
            else -> true
        }
    }

    private fun String?.isStringNotEmpty(): Boolean {
        return !this.isNullOrEmpty()
    }

    override fun onSupportNavigateUp(): Boolean{
        finish()
        myInterfaceCajasEditActivity!!.callBackCajas(myNewCaja = myEditableCaja)
        return true
    }
}