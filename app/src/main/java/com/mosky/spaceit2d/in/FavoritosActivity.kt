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
import com.mosky.spaceit2d.util.MyInterfaceFavoritos
import com.mosky.spaceit2d.util.MyInterfaceVisualizaOpti
import com.mosky.spaceit2d.R
import com.mosky.spaceit2d.adapter.OptimizacionAdapter
import com.mosky.spaceit2d.databinding.ActivityFavoritosBinding
import com.mosky.spaceit2d.`in`.VisualizaOptiActivity.Companion.setInterfaceInstanceVisualizaOptiActivity
import com.mosky.spaceit2d.provider.Optimizacion
import com.mosky.spaceit2d.provider.Resultado
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

/**
 * Actividad que maneja la visualización y gestión de elementos favoritos.
 * Implementa `MyInterfaceVisualizaOpti` para manejar interacciones relacionadas con la visualización de optimizaciones.
 */
class FavoritosActivity : AppCompatActivity(), MyInterfaceVisualizaOpti {

    private lateinit var binding: ActivityFavoritosBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var user: FirebaseUser

    private val gson = Gson()
    private var uid : String = ""
    private var contador : Int = 0
    private var myFavOptimizaciones = mutableListOf<Optimizacion>()
    private var miFavOptimizacionSelected : Optimizacion? = null

    companion object {
        private var myInterfaceFavoritos: MyInterfaceFavoritos? = null

        /**
         * Configura la instancia de `MyInterfaceFavoritos` para la actividad de favoritos.
         *
         * @param context El contexto de la actividad que implementa `MyInterfaceFavoritos`.
         */
        fun setInterfaceInstanceFavoritosActivity(contex: Context) {
            myInterfaceFavoritos = contex as MyInterfaceFavoritos?
        }
    }

    /**
     * Se llama cuando la actividad es creada. Aquí se inicializan los elementos de la interfaz de usuario,
     * se configuran las referencias a Firebase y se manejan los eventos de los botones.
     *
     * @param savedInstanceState Estado guardado de la actividad, si está disponible.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Infla el diseño de la actividad usando el binding generado
        binding = ActivityFavoritosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Activa la visualización de contenido en pantalla completa
        enableEdgeToEdge()

        // Configura Firebase
        firebaseAuth = Firebase.auth
        storage = Firebase.storage
        user = firebaseAuth.currentUser!!
        user.let {
            uid = it.uid
        }

        // Obtiene referencias a los botones
        val btnUpdate : Button = findViewById(R.id.button_updateFav)
        val btnVisualizar : Button = findViewById(R.id.button_visualizarFav)
        val btnDelete : Button = findViewById(R.id.button_deleteFav)

        // Configura la barra de acción
        val toggle = supportActionBar
        toggle!!.setDisplayHomeAsUpEnabled(true)
        toggle.title = "Favoritos"

        // Obtiene el número de optimizaciones desde el intent
        contador = intent.getIntExtra("optiCount", 0)

        // Descarga los archivos de optimización favoritos
        downloadFavFiles(contador)

        // Configura el botón de actualización para inicializar el RecyclerView
        btnUpdate.setOnClickListener {
            initRecyclerView()
        }

        // Configura el botón de eliminación para eliminar la optimización seleccionada
        btnDelete.setOnClickListener{
            val storageRef = storage.reference
            var aux : Optimizacion? = null
            if (miFavOptimizacionSelected != null) {
                for (item in myFavOptimizaciones) {
                    if (item.id == miFavOptimizacionSelected!!.id) {
                        item.fav = 0
                        val favRef: StorageReference = storageRef.child("$uid/${item.id}/fav.txt")
                        val byteArray = item.fav.toString().toByteArray(StandardCharsets.UTF_8)
                        val inputStream = ByteArrayInputStream(byteArray)
                        favRef.putStream(inputStream)
                        aux = item
                    }
                }
                if (aux != null) {
                    myFavOptimizaciones.remove(aux)
                }
                miFavOptimizacionSelected = null
                initRecyclerView()
            } else {
                Toast.makeText(baseContext,"Error: Seleccione una optimizacion", Toast.LENGTH_SHORT).show()
            }
        }

        // Configura el botón de visualización para mostrar la optimización seleccionada
        btnVisualizar.setOnClickListener {
            if (miFavOptimizacionSelected != null) {
                setInterfaceInstanceVisualizaOptiActivity(this@FavoritosActivity)
                val intent = Intent(this@FavoritosActivity, VisualizaOptiActivity::class.java)
                intent.putExtra("miOpti", miFavOptimizacionSelected)
                startActivity(intent)
            } else {
                Toast.makeText(baseContext, "Error: Seleccione una optimizacion",Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Descarga los archivos de optimización favoritos y los agrega a la lista.
     *
     * @param contador Número de optimizaciones a descargar.
     */
    private fun downloadFavFiles(contador: Int) {
        for(count in 1..contador) {
            loadOptimizacionFav(count) { isFav ->
                if (isFav == 1) {
                    loadOptimizacion(count) { array ->
                        if (array != null) {
                            val optimizacion = Optimizacion(count, isFav, array)
                            myFavOptimizaciones.add(optimizacion)
                        }
                    }
                }
            }
            initRecyclerView()
        }
    }

    /**
     * Carga el estado de favorito de una optimización desde Firebase Storage.
     *
     * @param count Identificador de la optimización.
     * @param callback Función lambda que recibe el estado de favorito.
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
     * @param count Identificador de la optimización.
     * @param callback Función lambda que recibe una lista de `Resultado` o `null` si falla la carga.
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
     * Inicializa y configura el `RecyclerView` para mostrar la lista de optimizaciones favoritas.
     */
    private fun initRecyclerView() {
        if (myFavOptimizaciones.size > 1) {
            myFavOptimizaciones.sortByDescending { it.id }
        }
        binding.recyclerFavoritos.layoutManager = LinearLayoutManager(this.binding.recyclerFavoritos.context)
        binding.recyclerFavoritos.adapter = OptimizacionAdapter(myFavOptimizaciones) { optimizacion ->
            onItemSelected(
                optimizacion
            )
        }
    }

    /**
     * Maneja la selección de una optimización en el `RecyclerView`.
     *
     * @param optimizacion El objeto `Optimizacion` seleccionado.
     */
    private fun onItemSelected(optimizacion: Optimizacion) {
        miFavOptimizacionSelected = optimizacion
    }

    /**
     * Maneja la navegación hacia arriba en la barra de acción.
     *
     * @return `true` si se maneja la navegación hacia arriba, `false` en caso contrario.
     */
    override fun onSupportNavigateUp(): Boolean{
        myFavOptimizaciones.clear()
        contador = 0
        finish()
        myInterfaceFavoritos!!.callBackFavoritos(data = "Vuelta")
        return true
    }

    /**
     * Callback de `MyInterfaceVisualizaOpti`, no implementado en esta actividad.
     *
     * @param data Datos opcionales recibidos en el callback.
     */
    override fun callBackOpti(data: String?) {
    }
}