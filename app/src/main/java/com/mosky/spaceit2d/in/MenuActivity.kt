package com.mosky.spaceit2d.`in`

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.initialize
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import com.google.gson.Gson
import com.mosky.spaceit2d.R
import com.mosky.spaceit2d.adapter.MenuAdapter
import com.mosky.spaceit2d.databinding.ActivityMenuBinding
import com.mosky.spaceit2d.`in`.FavoritosActivity.Companion.setInterfaceInstanceFavoritosActivity
import com.mosky.spaceit2d.`in`.HistorialActivity.Companion.setInterfaceInstanceHistorialActivity
import com.mosky.spaceit2d.`in`.MenuBultosActivity.Companion.setInterfaceInstanceBultosActivity
import com.mosky.spaceit2d.`in`.MenuCajasActivity.Companion.setInterfaceInstanceCajasActivity
import com.mosky.spaceit2d.`in`.VisualizaOpti2DActivity.Companion.setInterfaceInstanceVisualizaOpti2DActivity
import com.mosky.spaceit2d.out.MainActivity
import com.mosky.spaceit2d.provider.Bulto
import com.mosky.spaceit2d.provider.BultoProvider
import com.mosky.spaceit2d.provider.Caja
import com.mosky.spaceit2d.provider.CajaProvider
import com.mosky.spaceit2d.provider.Resultado
import com.mosky.spaceit2d.provider.ResultadoProvider
import com.mosky.spaceit2d.util.Empaquetador
import com.mosky.spaceit2d.util.MyInterfaceBultos
import com.mosky.spaceit2d.util.MyInterfaceCajas
import com.mosky.spaceit2d.util.MyInterfaceFavoritos
import com.mosky.spaceit2d.util.MyInterfaceHistorial
import com.mosky.spaceit2d.util.MyInterfaceVisualizaOpti
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

/**
 * Actividad principal para la pantalla del menú de la aplicación. Esta actividad maneja la navegación entre
 * varias características de la aplicación, incluyendo bultos, cajas, historial, favoritos y visualización en 3D.
 * También gestiona la configuración del modo oscuro y se comunica con Firebase para el almacenamiento y recuperación de datos.
 */
class MenuActivity : AppCompatActivity(), MyInterfaceBultos, MyInterfaceCajas, MyInterfaceHistorial,
    MyInterfaceFavoritos, MyInterfaceVisualizaOpti, NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMenuBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var storage : FirebaseStorage
    private lateinit var user : FirebaseUser

    private lateinit var navigationView : NavigationView

    private val gson = Gson()
    private var optiCount : Int = 0
    private var uid : String = ""
    private var fav : Int = 0

    private lateinit var toggle : ActionBarDrawerToggle

    companion object {
        var TESTBULTOS = "miKey"
        var TESTCAJAS = "miKey"
        var TESTRESULTADOS = "miKey"
    }

    /**
     * Se llama cuando la actividad se crea por primera vez.
     * Inicializa los componentes de la interfaz de usuario, configura Firebase, maneja la configuración del modo oscuro
     * y establece los escuchadores de clic para los botones y elementos de navegación.
     *
     * @param savedInstanceState Bundle que contiene el estado guardado previamente de la actividad, si existe.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configura el enlace con el diseño de la actividad
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Habilita la funcionalidad de pantalla completa (modo edge-to-edge)
        enableEdgeToEdge()

        // Inicializa Firebase Authentication y Storage
        firebaseAuth = Firebase.auth
        storage = Firebase.storage
        user = firebaseAuth.currentUser!!
        user.let {
            uid = it.uid
        }
        Firebase.initialize(this)

        // Carga y aplica la configuración del modo oscuro desde SharedPreferences
        val sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val isNightMode = sharedPreferences.getBoolean("night_mode", false)
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
        }

        // Configura el DrawerLayout y los botones de la interfaz
        val drawerLayout : DrawerLayout = findViewById(R.id.drawerLayout)
        val btnGoBultos : Button = findViewById(R.id.button_confiBultos)
        val btnGoCajas : Button = findViewById(R.id.button_confiCajas)
        val btnVer3D: Button = findViewById(R.id.button_ver3D)
        val btnOptimiza : Button = findViewById(R.id.button_optimiza)
        val btnGoHistorial : Button = findViewById(R.id.button_historial)

        // Configura el NavigationView y el ActionBarDrawerToggle
        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener (this)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Actualiza el menú del modo oscuro y el contador
        updateDarkModeMenuItem(isNightMode)
        updateCounter()

        // Habilita el botón de retroceso en la barra de acción
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Configura el comportamiento de los botones
        btnGoBultos.setOnClickListener {
            setInterfaceInstanceBultosActivity(this@MenuActivity)
            val intent = Intent(this@MenuActivity, MenuBultosActivity::class.java)
            intent.putExtra("contador", BultoProvider.myBultosCount)
            intent.putParcelableArrayListExtra(TESTBULTOS, BultoProvider.myBultos)
            startActivity(intent)
        }

        btnGoCajas.setOnClickListener {
            setInterfaceInstanceCajasActivity(this@MenuActivity)
            val intent = Intent(this@MenuActivity, MenuCajasActivity::class.java)
            intent.putExtra("contador", CajaProvider.myCajasCount)
            intent.putParcelableArrayListExtra(TESTCAJAS, CajaProvider.myCajas)
            startActivity(intent)
        }

        btnVer3D.setOnClickListener {
            setInterfaceInstanceVisualizaOpti2DActivity(this@MenuActivity)
            val intent = Intent(this@MenuActivity, VisualizaOpti2DActivity::class.java)
            intent.putParcelableArrayListExtra(TESTRESULTADOS, ResultadoProvider.myResultados as ArrayList<Resultado>)
            startActivity(intent)
        }

        btnOptimiza.setOnClickListener {
            if (BultoProvider.myBultos.size > 0) {
                if (CajaProvider.myCajas.size > 0) {
                    //optimizar()
                    //LLAMADA
                    clearIt()
                    val empaquetador = Empaquetador()
                    val empaquetado = empaquetador.empaquetarObjetos(BultoProvider.myBultos, CajaProvider.myCajas)

                    if (empaquetado) {
                        optiCount += 1
                        Toast.makeText(baseContext,"Se puede empaquetar", Toast.LENGTH_SHORT).show()
                        updateResults()
                        initRecyclerView()
                        uploadFiles()
                    } else {
                        Toast.makeText(baseContext,"No se pudo empaquetar", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(baseContext,"Necesito al menos 1 caja", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(baseContext,"Necesito al menos 1 bulto", Toast.LENGTH_SHORT).show()
            }
        }

        btnGoHistorial.setOnClickListener {
            setInterfaceInstanceHistorialActivity(this@MenuActivity)
            val intent = Intent(this@MenuActivity, HistorialActivity::class.java)
            intent.putExtra("optiCount", optiCount)
            startActivity(intent)
        }
    }

    /**
     * Maneja la selección de elementos del menú de navegación.
     *
     * @param item El elemento del menú que fue seleccionado.
     * @return true si el elemento fue manejado, false en caso contrario.
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                Toast.makeText(baseContext, "Click home", Toast.LENGTH_SHORT).show()
                //throw RuntimeException("Test Crash") // Force a crash
            }
            R.id.nav_settings -> Toast.makeText(baseContext, "Click settings", Toast.LENGTH_SHORT).show()
            R.id.nav_dark_mode -> {
                toggleDarkMode()
                return true
            }
            R.id.nav_fav -> {
                setInterfaceInstanceFavoritosActivity(this@MenuActivity)
                val intent = Intent(this@MenuActivity, FavoritosActivity::class.java)
                intent.putExtra("optiCount", optiCount)
                startActivity(intent)
            }
            R.id.nav_logout -> exitApp()
            R.id.nav_share -> Toast.makeText(baseContext, "Click share", Toast.LENGTH_SHORT).show()
        }
        return false
    }

    /**
     * Alterna la configuración del modo oscuro y actualiza el elemento del menú correspondiente.
     */
    private fun toggleDarkMode() {
        val sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
            editor.putBoolean("night_mode", false)
            updateDarkModeMenuItem(false)
        } else {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
            editor.putBoolean("night_mode", true)
            updateDarkModeMenuItem(true)
        }
        editor.apply()
    }

    /**
     * Actualiza el título y el ícono del elemento del menú de modo oscuro en función del modo actual.
     *
     * @param isNightMode Booleano que indica si el modo nocturno está habilitado.
     */
    private fun updateDarkModeMenuItem(isNightMode: Boolean) {
        val darkModeItem = navigationView.menu.findItem(R.id.nav_dark_mode)
        if (isNightMode) {
            darkModeItem.title = "Disable Dark Mode"
            darkModeItem.setIcon(R.drawable.ic_baseline_light_mode)
        } else {
            darkModeItem.title = "Enable Dark Mode"
            darkModeItem.setIcon(R.drawable.ic_baseline_dark_mode)
        }
    }

    /**
     * Sube los resultados de la optimización y otros datos relacionados a Firebase Storage.
     */
    private fun uploadFiles() {
        val storageRef = storage.reference

        //Subir la optimizacion realizada
        val intRef: StorageReference = storageRef.child("$uid/${optiCount}/results.json")

        //Serializamos resultados a JSON
        val jsonData = gson.toJson(ResultadoProvider.myResultados)

        //Convertimos JSON a flujo de bytes
        var byteArray = jsonData.toByteArray(StandardCharsets.UTF_8)
        var inputStream = ByteArrayInputStream(byteArray)

        intRef.putStream(inputStream)
            .addOnSuccessListener { _ ->
                Toast.makeText(baseContext,"Results upload successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(baseContext,"Results upload failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

        //Optimizacion Fav o no Fav
        val favRef: StorageReference = storageRef.child("$uid/${optiCount}/fav.txt")
        byteArray = fav.toString().toByteArray(StandardCharsets.UTF_8)
        inputStream = ByteArrayInputStream(byteArray)
        favRef.putStream(inputStream)

        //Actualizar contador
        val optiCountRef: StorageReference = storageRef.child("$uid/OptiCount.txt")
        byteArray = optiCount.toString().toByteArray(StandardCharsets.UTF_8)
        inputStream = ByteArrayInputStream(byteArray)
        optiCountRef.putStream(inputStream)

    }

    /**
     * Actualiza el contador de optimizaciones desde Firebase Storage. Si el archivo del contador no existe,
     * se crea con el valor actual del contador de optimizaciones.
     */
    private fun updateCounter() {
        val storageRef = storage.reference
        val intRef: StorageReference = storageRef.child("$uid/OptiCount.txt")

        intRef.getBytes(Long.MAX_VALUE)
            .addOnSuccessListener { bytes ->
                val data = String(bytes)
                val integerValue = data.toIntOrNull()
                if (integerValue != null) {
                    optiCount = integerValue
                }
            }
            .addOnFailureListener { _ ->
                //Contador no ha sido creado aun
                val byteArray = optiCount.toString().toByteArray(StandardCharsets.UTF_8)
                val inputStream = ByteArrayInputStream(byteArray)

                intRef.putStream(inputStream)
                    .addOnSuccessListener { _ ->
                    }
                    .addOnFailureListener { excptn ->
                        Toast.makeText(baseContext,"Opticount creation failed: ${excptn.message}", Toast.LENGTH_SHORT).show()
                    }
            }
    }

    /**
     * Limpia los datos actuales antes de realizar una nueva optimización.
     * - Limpia la lista de resultados.
     * - Reinicia el peso actual y el área actual de todas las cajas.
     * - Limpia los bultos asociados a cada caja.
     */
    private fun clearIt() {
        ResultadoProvider.myResultados.clear()
        for (item in CajaProvider.myCajas) {
            item.pesoActual = 0
            item.areaActual = 0
            item.misBultos = null
        }
    }

    /**
     * Actualiza la lista de resultados con la información de las cajas y bultos.
     * - Agrega resultados para cada caja.
     * - Agrega resultados para cada bulto asociado a las cajas.
     */
    private fun updateResults() {
        //Creamos lista de resultados a imprimir
        for (caja in CajaProvider.myCajas) {
            ResultadoProvider.myResultados.add(Resultado(0, caja.idC,caja.pesoMax, caja.limBultos, "@drawable/ic_baseline_caja.xml", caja.nombre!!, caja.dimensionX.toFloat(), caja.dimensionY.toFloat(), caja.dimensionX * caja.dimensionY, 0f, 0f, Float.MIN_VALUE, Float.MIN_VALUE, floatArrayOf(0f,0f,0f,0f)))
            if (caja.misBultos != null) {
                for (bulto in caja.misBultos!!) {
                    ResultadoProvider.myResultados.add(Resultado(1, bulto.id, bulto.peso, -1, "@drawable/ic_baseline_bulto.xml", bulto.nombre!!, bulto.dimensionX.toFloat(), bulto.dimensionY.toFloat(), bulto.dimensionX * bulto.dimensionY, 0f, 0f, Float.MIN_VALUE, Float.MIN_VALUE, bulto.color))
                }
            }
        }
    }

    /**
     * Inicializa el RecyclerView para mostrar los resultados de la optimización.
     * - Ordena las cajas por peso máximo de forma descendente.
     * - Configura el LayoutManager y el Adapter del RecyclerView.
     */
    private fun initRecyclerView() {
        if (CajaProvider.myCajas.size > 1) CajaProvider.myCajas.sortByDescending {it.pesoMax}

        binding.recyclerMenu.layoutManager = LinearLayoutManager(this.binding.recyclerMenu.context)
        binding.recyclerMenu.adapter = MenuAdapter(ResultadoProvider.myResultados)
    }

    /**
     * Maneja las selecciones de los elementos del menú de opciones.
     *
     * @param item El ítem del menú que ha sido seleccionado.
     * @return true si el ítem ha sido manejado, false en caso contrario.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Sale de la aplicación cerrando la sesión del usuario y reiniciando la actividad principal.
     * - Limpia las listas de cajas y bultos.
     * - Cierra la sesión del usuario en Firebase.
     * - Inicia la actividad principal.
     */
    private fun exitApp() {
        CajaProvider.myCajas.clear()
        BultoProvider.myBultos.clear()
        firebaseAuth.signOut()
        startActivity(Intent(this@MenuActivity, MainActivity::class.java))
    }

    /**
     * Maneja el evento de presionar el botón de retroceso en la actividad.
     * Este método está obsoleto y se recomienda usar el control de eventos del botón de retroceso proporcionado por el OnBackPressedDispatcher.
     */
    @Deprecated("This method has been deprecated in favor of using the OnBackPressedDispatcher controls how back button events are dispatched to one or more")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        return
    }

    /**
     * Callback para actualizar la lista de bultos y el conteo desde otra actividad o fragmento.
     *
     * @param bultosListAux Lista de bultos recibida.
     * @param bultosCountAux Conteo de bultos recibido.
     */
    override fun callBackBultos(bultosListAux: ArrayList<Bulto>?, bultosCountAux: Int) {

        val nuevaListaBultos : ArrayList<Bulto> = ArrayList()
        if (bultosListAux != null) {
            nuevaListaBultos.addAll(bultosListAux)
        }
        BultoProvider.myBultos = nuevaListaBultos
        BultoProvider.myBultosCount = bultosCountAux
    }

    /**
     * Callback para actualizar la lista de cajas y el conteo desde otra actividad o fragmento.
     *
     * @param cajasListAux Lista de cajas recibida.
     * @param cajasCountAux Conteo de cajas recibido.
     */
    override fun callBackCajas(cajasListAux: ArrayList<Caja>?, cajasCountAux: Int) {
        val nuevaListaCajas : ArrayList<Caja> = ArrayList()
        if (cajasListAux != null) {
            nuevaListaCajas.addAll(cajasListAux)
        }
        CajaProvider.myCajas = nuevaListaCajas
        CajaProvider.myCajasCount = cajasCountAux
    }

    /**
     * Callback para actualizar el historial desde otra actividad o fragmento.
     *
     * @param data Datos recibidos para el historial.
     */
    override fun callBackHistorial(data: String?) {
    }

    /**
     * Callback para actualizar los favoritos desde otra actividad o fragmento.
     *
     * @param data Datos recibidos para los favoritos.
     */
    override fun callBackFavoritos(data: String?) {
    }

    /**
     * Callback para actualizar la optimización desde otra actividad o fragmento.
     *
     * @param data Datos recibidos para la optimización.
     */
    override fun callBackOpti(data: String?) {
    }

}
