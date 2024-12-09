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
import com.mosky.spaceit2d.util.MyInterfaceBultos
import com.mosky.spaceit2d.util.MyInterfaceEditBultos
import com.mosky.spaceit2d.R
import com.mosky.spaceit2d.`in`.MenuBultosEditActivity.Companion.setInterfaceInstanceBultosEditActivity
import com.mosky.spaceit2d.adapter.BultoAdapter
import com.mosky.spaceit2d.databinding.ActivityMenuBultosBinding
import com.mosky.spaceit2d.provider.Bulto
import com.mosky.spaceit2d.provider.BultoProvider
import kotlin.random.Random

/**
 * Actividad que maneja la interfaz de usuario para la configuración y edición de bultos.
 * Implementa [MyInterfaceEditBultos] para manejar las interacciones de edición de bultos.
 */
class MenuBultosActivity : AppCompatActivity() , MyInterfaceEditBultos {

    // Vínculo con el diseño de la actividad
    private lateinit var binding: ActivityMenuBultosBinding

    // Variables para manejar el bulto seleccionado, la lista de bultos y el contador de bultos
    private var miBultoSelected : Bulto? = null
    private var bultosList : ArrayList<Bulto>? = null
    private var contadorBultos : Int = 0

    companion object {
        // Clave para el Intent extra que contiene el bulto editable
        var TESTEDITABLE = "miEditableKey"
        // Variable para la interfaz de bultos
        private var myInterfaceBultosActivity: MyInterfaceBultos? = null

        /**
         * Configura la instancia de la interfaz de bultos.
         *
         * @param contex El contexto de la actividad.
         */
        fun setInterfaceInstanceBultosActivity(contex: Context?) {
            myInterfaceBultosActivity = contex as MyInterfaceBultos?
        }
    }

    /**
     * Extensión de Intent para obtener una lista de objetos Parcelable según la versión de Android.
     *
     * @param identifierParameter La clave del extra en el Intent.
     * @return La lista de objetos Parcelable o null si no existe.
     */
    @Suppress("DEPRECATION")
    private inline fun <reified T: Parcelable> Intent.getParcelableArrayListExtraProvider(identifierParameter: String): ArrayList<T>? {
        return if (Build.VERSION.SDK_INT >= 33) {
            this.getParcelableArrayListExtra(identifierParameter, T::class.java)
        } else {
            this.getParcelableArrayListExtra(identifierParameter)
        }
    }

    /**
     * Se llama cuando la actividad es creada. Realiza la configuración inicial de la actividad,
     * incluyendo la inicialización de la interfaz de usuario y la configuración de los botones.
     *
     * @param savedInstanceState El estado guardado de la instancia, si existe.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configura el enlace con el diseño de la actividad
        binding = ActivityMenuBultosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura el RecyclerView y habilita la funcionalidad de pantalla completa
        initRecyclerView()
        enableEdgeToEdge()

        // Configura la barra de acción
        val toggle = supportActionBar
        toggle!!.setDisplayHomeAsUpEnabled(true)
        toggle.title = "Objetos"

        // Obtiene el contador de bultos del Intent
        contadorBultos = intent.getIntExtra("contador", 0)

        // Obtiene la lista de bultos del Intent si está disponible
        if (intent.hasExtra(MenuActivity.TESTBULTOS)) {
            bultosList = intent.getParcelableArrayListExtraProvider(MenuActivity.TESTBULTOS)
        } else {
            Toast.makeText(baseContext, "Lista vacia",Toast.LENGTH_SHORT).show()
        }

        // Referencias a los elementos de la interfaz de usuario
        val txtPesoBulto: TextView = findViewById(R.id.text_pesobulto)
        val txtNombreBulto: TextView = findViewById(R.id.text_nombreBulto)
        val btnAddBulto: Button = findViewById(R.id.button_addBultos)
        val btnEditBulto: Button = findViewById(R.id.button_editBultos)
        val btnDeleteBulto: Button = findViewById(R.id.button_deleteBultos)

        // Configura el comportamiento del botón de editar bulto
        btnEditBulto.setOnClickListener {
            if (miBultoSelected != null) {
                setInterfaceInstanceBultosEditActivity(this@MenuBultosActivity)
                val intent = Intent(this@MenuBultosActivity, MenuBultosEditActivity::class.java)
                intent.putExtra(TESTEDITABLE, miBultoSelected)
                startActivity(intent)
            } else {
                Toast.makeText(baseContext, "Error: Seleccione un objeto",Toast.LENGTH_SHORT).show()
            }
        }

        // Configura el comportamiento del botón de eliminar bulto
        btnDeleteBulto.setOnClickListener {
            if (miBultoSelected != null) {
                bultosList?.remove(miBultoSelected)
                miBultoSelected = null

                if (bultosList != null) {
                    BultoProvider.myBultos = bultosList as ArrayList<Bulto>
                }
                initRecyclerView()
            } else {
                Toast.makeText(baseContext, "Error: Seleccione un objeto",Toast.LENGTH_SHORT).show()
            }
        }

        // Configura el comportamiento del botón de añadir bulto
        btnAddBulto.setOnClickListener {
            val peso = txtPesoBulto.text.toString()
            val nombre = txtNombreBulto.text.toString()
            if (nombre.isStringNotEmpty()) {
                if (peso.isStringNotEmpty() && isNumeric(peso)) {
                    addBulto(peso.toInt(), nombre, contadorBultos, bultosList)
                } else {
                    Toast.makeText(baseContext, "Error : Peso inválido", Toast.LENGTH_SHORT).show()
                    txtPesoBulto.requestFocus()
                }
            } else {
                Toast.makeText(baseContext, "Error : Nombre inválido", Toast.LENGTH_SHORT).show()
                txtNombreBulto.requestFocus()
            }
        }
    }

    /**
     * Inicializa el RecyclerView para mostrar la lista de bultos.
     * Ordena los bultos por peso de manera descendente antes de configurar el adaptador.
     */
    private fun initRecyclerView() {
        if (BultoProvider.myBultos.size > 1) BultoProvider.myBultos.sortByDescending {it.peso}

        binding.recyclerBultos.layoutManager = LinearLayoutManager(this.binding.recyclerBultos.context)
        binding.recyclerBultos.adapter = BultoAdapter(BultoProvider.myBultos) { bulto ->
            onItemSelected(
                bulto
            )
        }
    }

    /**
     * Maneja la selección de un bulto en el RecyclerView.
     *
     * @param bulto El bulto que ha sido seleccionado.
     */
    private fun onItemSelected(bulto : Bulto) {
        miBultoSelected = bulto
    }

    /**
     * Añade un nuevo bulto a la lista de bultos.
     *
     * @param peso El peso del nuevo bulto.
     * @param nombre El nombre del nuevo bulto.
     * @param contadorBultosadd El contador actual de bultos.
     * @param listaBultosadd La lista actual de bultos.
     */
    private fun addBulto(peso: Int, nombre: String, contadorBultosadd: Int, listaBultosadd: ArrayList<Bulto>?) {
        if (peso < 1) {
            Toast.makeText(baseContext, "Error: Peso inválido", Toast.LENGTH_SHORT).show()
        } else {
            val auxCountBulto = contadorBultosadd + 1
            val auxBulto = Bulto(auxCountBulto, peso, 10, 10,100, "@drawable/ic_baseline_bulto.xml", nombre, generarColorAleatorio())

            val auxListBulto : ArrayList<Bulto> = ArrayList()
            auxListBulto.add(auxBulto)

            if (listaBultosadd != null) {
                auxListBulto.addAll(listaBultosadd)
                bultosList = auxListBulto
                BultoProvider.myBultos = auxListBulto
                contadorBultos = auxCountBulto
                BultoProvider.myBultosCount = auxCountBulto
            }

            initRecyclerView()
        }
    }

    /**
     * Maneja la navegación hacia atrás. Finaliza la actividad y notifica a la interfaz de los cambios en la lista de bultos.
     *
     * @return Retorna verdadero para indicar que la acción de navegación hacia atrás ha sido manejada.
     */
    override fun onSupportNavigateUp(): Boolean{
        finish()
        myInterfaceBultosActivity!!.callBackBultos(bultosListAux = bultosList, bultosCountAux = contadorBultos)
        return true
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
     * Actualiza los detalles de un bulto en la lista de bultos.
     *
     * @param myNewBulto El bulto con los nuevos detalles.
     */
    override fun callBackBultos(myNewBulto: Bulto?) {

        for (item in bultosList!!) {
            if (item.id == myNewBulto!!.id) {
                if (!item.nombre.equals(myNewBulto.nombre)) item.nombre = myNewBulto.nombre
                if (item.peso != myNewBulto.peso) item.peso = myNewBulto.peso
                if (item.dimensionX != myNewBulto.dimensionX || item.dimensionY != myNewBulto.dimensionY) {
                    item.dimensionX = myNewBulto.dimensionX
                    item.dimensionY = myNewBulto.dimensionY
                    item.area = item.dimensionX * item.dimensionY
                }
            }
        }

        BultoProvider.myBultos = bultosList as ArrayList<Bulto>

        initRecyclerView()
        miBultoSelected = null
    }

    /**
     * Genera un color aleatorio en formato RGBA.
     *
     * @return Un array de flotantes que representa el color en formato RGBA.
     */
    private fun generarColorAleatorio(): FloatArray {
        val r = Random.nextFloat()
        val g = Random.nextFloat()
        val b = Random.nextFloat()
        val a = 1.0f // Puedes ajustar esto si necesitas transparencia

        return floatArrayOf(r, g, b, a)
    }
}