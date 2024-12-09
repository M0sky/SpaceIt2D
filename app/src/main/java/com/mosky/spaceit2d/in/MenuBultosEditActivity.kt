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
import com.mosky.spaceit2d.util.MyInterfaceEditBultos
import com.mosky.spaceit2d.R
import com.mosky.spaceit2d.provider.Bulto

/**
 * Actividad para editar los detalles de un bulto.
 */
class MenuBultosEditActivity : AppCompatActivity() {

    private var myEditableBulto : Bulto? = null

    companion object {
        private var myInterfaceBultosEditActivity : MyInterfaceEditBultos? = null

        /**
         * Establece la instancia de la interfaz para la actividad de edición de bultos.
         *
         * @param context El contexto en el que se encuentra la interfaz.
         */
        fun setInterfaceInstanceBultosEditActivity(contex: Context?) {
            myInterfaceBultosEditActivity = contex as MyInterfaceEditBultos?
        }
    }

    /**
     * Extensión para obtener un extra de tipo Parcelable de un Intent, con soporte para versiones de API >= 33.
     *
     * @param identifierParameter El identificador del parámetro del Intent.
     * @return El objeto Parcelable del Intent, o null si no está presente.
     */
    @Suppress("DEPRECATION")
    private inline fun <reified T: Parcelable> Intent.getParcelableExtraProvider(identifierParameter: String): T? {
        return if (Build.VERSION.SDK_INT >= 33) {
            this.getParcelableExtra(identifierParameter, T::class.java)
        } else {
            this.getParcelableExtra(identifierParameter)
        }
    }

    /**
     * Se ejecuta cuando se crea la actividad.
     *
     * @param savedInstanceState El estado guardado de la actividad, si existe. Este parámetro puede ser nulo.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Habilita el ajuste de pantalla completo
        enableEdgeToEdge()

        // Establece el diseño de la actividad
        setContentView(R.layout.activity_menu_bultos_edit)

        // Inicializa los elementos de la interfaz de usuario
        val btnConfirmarBulto : Button = findViewById(R.id.button_confirmEditBulto)
        val txtEditBultoNombre : TextView = findViewById(R.id.text_editBultoNombre)
        val txtEditBultoPeso : TextView = findViewById(R.id.text_editBultoPeso)
        val txtEditBultoDimensionX : TextView = findViewById(R.id.text_editBultoDX)
        val txtEditBultoDimensionY : TextView = findViewById(R.id.text_editBultoDY)

        // Configura la barra de acción para mostrar el botón de retroceso y establecer el título
        val toggle = supportActionBar
        toggle!!.setDisplayHomeAsUpEnabled(true)
        toggle.title = "Objetos"

        // Verifica si el intent tiene datos para editar un bulto
        if (intent.hasExtra(MenuBultosActivity.TESTEDITABLE)) {
            myEditableBulto = intent.getParcelableExtraProvider(MenuBultosActivity.TESTEDITABLE)
        } else {
            Toast.makeText(baseContext, "Bulto vacío", Toast.LENGTH_SHORT).show()
        }

        // Configura el listener para el botón de confirmar
        btnConfirmarBulto.setOnClickListener {
            // Obtiene los valores de los campos de texto
            val aux1 = txtEditBultoNombre.text.toString()
            val aux2 = txtEditBultoPeso.text.toString()
            val aux3 = txtEditBultoDimensionX.text.toString()
            val aux4 = txtEditBultoDimensionY.text.toString()

            // Actualiza el nombre del bulto si no está vacío
            if (aux1.isStringNotEmpty()) {
                myEditableBulto!!.nombre = aux1
            }

            // Actualiza el peso del bulto si es un número positivo
            if (aux2.isStringNotEmpty() && isNumeric(aux2)) {
                val v2 : Int = aux2.toInt()
                if (v2 > 0) {
                    myEditableBulto!!.peso = v2
                }
            }

            // Actualiza la dimensión X del bulto si es un número positivo
            if (aux3.isStringNotEmpty() && isNumeric(aux3)) {
                val v3 : Int = aux3.toInt()
                if (v3 > 0) {
                    myEditableBulto!!.dimensionX = v3
                }
            }

            // Actualiza la dimensión Y del bulto si es un número positivo
            if (aux4.isStringNotEmpty() && isNumeric(aux4)) {
                val v4 : Int = aux4.toInt()
                if (v4 > 0) {
                    myEditableBulto!!.dimensionY = v4
                }
            }

            // Finaliza la actividad y devuelve el bulto editado a la actividad anterior
            finish()
            myInterfaceBultosEditActivity!!.callBackBultos(myNewBulto = myEditableBulto)
        }
    }

    /**
     * Verifica si una cadena de texto puede ser convertida a un número entero.
     *
     * @param toCheck La cadena de texto a verificar.
     * @return Verdadero si la cadena puede ser convertida a un número entero, falso en caso contrario.
     */
    private fun isNumeric(toCheck: String): Boolean {
        val v = toCheck.toIntOrNull()
        return when(v) {
            null -> false
            else -> true
        }
    }

    /**
     * Verifica si una cadena de texto no es nula ni vacía.
     *
     * @return Verdadero si la cadena no es nula ni vacía, falso en caso contrario.
     */
    private fun String?.isStringNotEmpty(): Boolean {
        return !this.isNullOrEmpty()
    }

    /**
     * Maneja la navegación hacia atrás. Finaliza la actividad y notifica a la interfaz sobre los cambios en el bulto.
     *
     * @return Retorna verdadero para indicar que la acción de navegación hacia atrás ha sido manejada.
     */
    override fun onSupportNavigateUp(): Boolean{
        finish()
        myInterfaceBultosEditActivity!!.callBackBultos(myNewBulto = myEditableBulto)
        return true
    }
}