package com.mosky.spaceit2d.`in`

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.common.reflect.TypeToken
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import com.google.gson.Gson
import com.mosky.spaceit2d.util.MyInterfaceHistorial
import com.mosky.spaceit2d.util.MyInterfaceVisualizaOpti
import com.mosky.spaceit2d.R
import com.mosky.spaceit2d.adapter.OptimizacionAdapter
import com.mosky.spaceit2d.databinding.ActivityHistorialBinding
import com.mosky.spaceit2d.`in`.VisualizaOptiActivity.Companion.setInterfaceInstanceVisualizaOptiActivity
import com.mosky.spaceit2d.provider.Optimizacion
import com.mosky.spaceit2d.provider.Resultado
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

/**
 * Actividad que muestra el historial de optimizaciones. Permite actualizar la lista,
 * marcar una optimización como favorita y visualizar los detalles de una optimización seleccionada.
 *
 * @constructor Crea una instancia de `HistorialActivity`.
 */
class HistorialActivity : AppCompatActivity(), MyInterfaceVisualizaOpti {

    private lateinit var binding: ActivityHistorialBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var storage : FirebaseStorage
    private lateinit var user : FirebaseUser

    private val gson = Gson()
    private var uid : String = ""
    private var contador : Int = 0
    private var myOptimizaciones = mutableListOf<Optimizacion>()
    private var miOptimizacionSelected : Optimizacion? = null

    companion object {
        private var myInterfaceHistorial: MyInterfaceHistorial? = null

        /**
         * Configura la instancia de la interfaz para la actividad de historial.
         *
         * @param context Contexto de la aplicación que implementa `MyInterfaceHistorial`.
         */
        fun setInterfaceInstanceHistorialActivity(contex: Context?) {
            myInterfaceHistorial = contex as MyInterfaceHistorial?
        }
    }

    /**
     * Se llama cuando la actividad es creada. Inicializa la vista, configura Firebase,
     * maneja eventos de botones y carga los datos necesarios.
     *
     * @param savedInstanceState Estado guardado de la actividad, si está disponible.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistorialBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        // Configura Firebase
        firebaseAuth = Firebase.auth
        storage = Firebase.storage
        user = firebaseAuth.currentUser!!
        user.let {
            uid = it.uid
        }

        // Obtiene referencias a los botones
        val btnUpdate : Button = findViewById(R.id.button_updateHistory)
        val btnFavorito : Button = findViewById(R.id.button_favHistory)
        val btnVisualizar : Button = findViewById(R.id.button_visualizarOpti)

        // Configura la barra de acción
        val toggle = supportActionBar
        toggle!!.setDisplayHomeAsUpEnabled(true)
        toggle.title = "Historial"

        // Obtiene el número de optimizaciones desde el intent
        contador = intent.getIntExtra("optiCount", 0)

        // Descarga los archivos de optimización
        downloadFiles(contador)

        // Configura el botón de actualización para inicializar el RecyclerView
        btnUpdate.setOnClickListener {
            initRecyclerView()
        }

        // Configura el botón de favorito para marcar o desmarcar la optimización seleccionada
        btnFavorito.setOnClickListener{
            val storageRef = storage.reference
            //FALTA -> CAMBIAR EN FIREBASE STORAGE SI ES FAV O NO
            
            if (miOptimizacionSelected != null) {
                for (item in myOptimizaciones) {
                    if (item.id == miOptimizacionSelected!!.id) {
                        if (item.fav == 1) {
                            item.fav = 0
                            val favRef: StorageReference = storageRef.child("$uid/${item.id}/fav.txt")
                            val byteArray = item.fav.toString().toByteArray(StandardCharsets.UTF_8)
                            val inputStream = ByteArrayInputStream(byteArray)
                            favRef.putStream(inputStream)
                        } else {
                            item.fav = 1
                            val favRef: StorageReference = storageRef.child("$uid/${item.id}/fav.txt")
                            val byteArray = item.fav.toString().toByteArray(StandardCharsets.UTF_8)
                            val inputStream = ByteArrayInputStream(byteArray)
                            favRef.putStream(inputStream)
                        }
                    }
                }
                miOptimizacionSelected = null
                initRecyclerView()
            } else {
                Toast.makeText(baseContext,"Error: Seleccione una optimizacion", Toast.LENGTH_SHORT).show()
            }
        }

        // Configura el botón de visualización para mostrar los detalles de la optimización seleccionada
        btnVisualizar.setOnClickListener {
            if (miOptimizacionSelected != null) {
                setInterfaceInstanceVisualizaOptiActivity(this@HistorialActivity)
                val intent = Intent(this@HistorialActivity, VisualizaOptiActivity::class.java)
                intent.putExtra("miOpti", miOptimizacionSelected)
                startActivity(intent)
            } else {
                Toast.makeText(baseContext, "Error: Seleccione una optimizacion",Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Descarga los archivos de optimización basados en el número de optimizaciones.
     *
     * @param contador Número de optimizaciones a descargar.
     */
    private fun downloadFiles(contador: Int) {
        for(count in 1..contador) {
            loadOptimizacionFav(count) { isFav ->
                loadOptimizacion(count) { array ->
                    if (array != null) {
                        val optimizacion = Optimizacion(count, isFav, array)
                        myOptimizaciones.add(optimizacion)
                    }
                }
            }
            initRecyclerView()
        }
    }

    /**
     * Carga el estado de favorito de una optimización desde Firebase Storage.
     *
     * @param count ID de la optimización.
     * @param callback Función de retorno que recibe el estado de favorito.
     */
    private fun loadOptimizacionFav(count: Int, callback: (Int) -> Unit) {
        val storageRef = storage.reference
        val favRef = storageRef.child("$uid/$count/fav.txt")

        favRef.getBytes(Long.MAX_VALUE)
            .addOnSuccessListener { bytes ->
                val data = String(bytes)
                val integerValue = data.toIntOrNull()
                if (integerValue != null) {
                    callback(integerValue)
                }
            }
            .addOnFailureListener { _ ->
                callback(0)
            }
    }

    /**
     * Carga los datos de una optimización desde Firebase Storage.
     *
     * @param count ID de la optimización.
     * @param callback Función de retorno que recibe un array de resultados o `null` si falla.
     */
    private fun loadOptimizacion(count: Int, callback: (ArrayList<Resultado>?) -> Unit) {
        val storageRef = storage.reference
        val optiRef = storageRef.child("$uid/$count/results.json")

        optiRef.getBytes(Long.MAX_VALUE)
            .addOnSuccessListener { bytes ->
                val jsonData = String(bytes, StandardCharsets.UTF_8)
                val arrayType = object : TypeToken<ArrayList<Resultado>>() {}.type
                val dataArray: ArrayList<Resultado> = gson.fromJson(jsonData, arrayType)
                //Devolvemos el array a traves del callback
                callback(dataArray)
            }
            .addOnFailureListener { _ ->
                callback(null)
            }
    }

    /**
     * Inicializa el `RecyclerView` con la lista de optimizaciones.
     * Ordena la lista por ID en orden descendente antes de asignarla al adaptador.
     */
    private fun initRecyclerView() {
        if (myOptimizaciones.size > 1) {
            myOptimizaciones.sortedByDescending{ it.id }
        }
        binding.recyclerHistorial.layoutManager = LinearLayoutManager(this.binding.recyclerHistorial.context)
        binding.recyclerHistorial.adapter = OptimizacionAdapter(myOptimizaciones) { optimizacion ->
            onItemSelected(
                optimizacion
            )
        }
    }

    /**
     * Maneja la selección de una optimización.
     *
     * @param optimizacion Optimización seleccionada.
     */
    private fun onItemSelected(optimizacion: Optimizacion) {
        miOptimizacionSelected = optimizacion
    }

    /**
     * Maneja la acción de navegación hacia arriba en la barra de acción.
     * Limpia la lista de optimizaciones y finaliza la actividad.
     *
     * @return `true` para indicar que se manejó la acción de navegación.
     */
    override fun onSupportNavigateUp(): Boolean{
        myOptimizaciones.clear()
        contador = 0
        finish()
        myInterfaceHistorial!!.callBackHistorial(data = "Vuelta")
        return true
    }

    /**
     * Método de la interfaz `MyInterfaceVisualizaOpti`, no implementado en esta actividad.
     *
     * @param data Datos pasados a través de la interfaz.
     */
    override fun callBackOpti(data: String?) {
    }
}