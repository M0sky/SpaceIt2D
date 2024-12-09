package com.mosky.spaceit2d.`in`

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mosky.spaceit2d.R
import com.mosky.spaceit2d.adapter.MenuAdapter
import com.mosky.spaceit2d.databinding.ActivityVisualizaOptiBinding
import com.mosky.spaceit2d.`in`.MenuActivity.Companion.TESTRESULTADOS
import com.mosky.spaceit2d.`in`.VisualizaOpti2DActivity.Companion.setInterfaceInstanceVisualizaOpti2DActivity
import com.mosky.spaceit2d.provider.Optimizacion
import com.mosky.spaceit2d.provider.Resultado
import com.mosky.spaceit2d.util.MyInterfaceVisualizaOpti

/**
 * Actividad que visualiza la optimización y maneja la navegación a la vista 2D.
 *
 * Implementa la interfaz [MyInterfaceVisualizaOpti] para recibir callbacks.
 */
class VisualizaOptiActivity : AppCompatActivity(), MyInterfaceVisualizaOpti {

    private lateinit var binding: ActivityVisualizaOptiBinding
    private lateinit var myOptimizacion : Optimizacion

    companion object {
        private var myInterfaceVisualizaOptiActivity : MyInterfaceVisualizaOpti? = null

        /**
         * Configura la instancia de la interfaz para la actividad.
         *
         * @param context El contexto de la actividad.
         */
        fun setInterfaceInstanceVisualizaOptiActivity(contex: Context?) {
            myInterfaceVisualizaOptiActivity = contex as MyInterfaceVisualizaOpti?
        }
    }

    /**
     * Obtiene un objeto Parcelable extra de un Intent, manejando la compatibilidad con diferentes versiones de SDK.
     *
     * @param identifierParameter El identificador del extra en el Intent.
     * @return El objeto Parcelable asociado al identificador, o null si no existe.
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
     * Configura la actividad cuando se crea, incluyendo la inicialización del layout y la configuración de la vista.
     *
     * @param savedInstanceState El estado guardado de la actividad, si lo hay.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisualizaOptiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        val toggle = supportActionBar
        toggle!!.setDisplayHomeAsUpEnabled(true)
        toggle.title = "Optimizacion"

        myOptimizacion = intent.getParcelableExtraProvider("miOpti")!!

        val btnVer2D: Button = findViewById(R.id.button_ver2D)

        // Inicializa el RecyclerView con la lista de resultados
        initRecyclerView(myOptimizacion.miLista!!)

        // Configura el botón para iniciar la actividad de visualización en 2D
        btnVer2D.setOnClickListener {
            setInterfaceInstanceVisualizaOpti2DActivity(this@VisualizaOptiActivity)
            val intent = Intent(this@VisualizaOptiActivity, VisualizaOpti2DActivity::class.java)
            intent.putParcelableArrayListExtra(TESTRESULTADOS, myOptimizacion.miLista as ArrayList<Resultado>)
            startActivity(intent)
        }
    }

    /**
     * Inicializa el RecyclerView con una lista de resultados.
     *
     * @param miLista La lista de resultados a mostrar en el RecyclerView.
     */
    private fun initRecyclerView(miLista: ArrayList<Resultado>) {
        binding.recyclerVisualizaOpti.layoutManager = LinearLayoutManager(this.binding.recyclerVisualizaOpti.context)
        binding.recyclerVisualizaOpti.adapter = MenuAdapter(miLista)
    }

    /**
     * Maneja la acción de retroceso en la barra de navegación.
     *
     * Finaliza la actividad y llama al método `callBackOpti` en la interfaz configurada.
     *
     * @return `true` si la acción de retroceso se manejó correctamente.
     */
    override fun onSupportNavigateUp(): Boolean{
        finish()
        myInterfaceVisualizaOptiActivity!!.callBackOpti("Back to history")
        return true
    }

    /**
     * Método de callback de la interfaz [MyInterfaceVisualizaOpti].
     *
     * Implementado para cumplir con la interfaz, pero no realiza ninguna acción en esta clase.
     *
     * @param data Datos proporcionados en el callback.
     */
    override fun callBackOpti(data: String?) {
    }
}