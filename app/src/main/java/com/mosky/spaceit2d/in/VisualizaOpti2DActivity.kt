package com.mosky.spaceit2d.`in`

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mosky.spaceit2d.R
import com.mosky.spaceit2d.adapter.MenuAdapter
import com.mosky.spaceit2d.provider.Resultado
import com.mosky.spaceit2d.util.MyGLSurfaceView
import com.mosky.spaceit2d.util.MyInterfaceVisualizaOpti
import com.mosky.spaceit2d.util.Rectangulo

/**
 * Actividad para visualizar la optimización en 2D.
 *
 * Esta actividad muestra una visualización de optimización en 2D utilizando una vista OpenGL y una lista de resultados.
 * Permite el zoom y la navegación a través de diferentes cajas de resultados.
 */
class VisualizaOpti2DActivity : AppCompatActivity() {

    private var resultadosList : ArrayList<Resultado>? = null
    private var sublistas : List<List<Resultado>> = listOf()
    private var currentIndex = 0

    private lateinit var recyclerView: RecyclerView
    private val dataList: MutableList<Resultado> = mutableListOf()

    private lateinit var glSurfaceView: MyGLSurfaceView

    private var flag: Boolean = false
    private var rectangulos : MutableList<Rectangulo> = mutableListOf()

    companion object {
        private var myInterfaceVisualizaOpti2DActivity : MyInterfaceVisualizaOpti? = null

        /**
         * Configura la instancia de la interfaz para esta actividad.
         *
         * @param context El contexto en el que se implementa la interfaz. Debe ser una instancia de [MyInterfaceVisualizaOpti].
         */
        fun setInterfaceInstanceVisualizaOpti2DActivity(contex: Context?) {
            myInterfaceVisualizaOpti2DActivity = contex as MyInterfaceVisualizaOpti
        }
    }

    /**
     * Extensión de [Intent] para obtener una lista de objetos [Parcelable] basada en la versión del SDK.
     *
     * @param identifierParameter El identificador del parámetro extra en el intent.
     * @return Una lista de objetos [T] o nula si no se encuentra el extra.
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
     * Se ejecuta cuando se crea la actividad.
     *
     * @param savedInstanceState El estado guardado de la actividad, si existe. Este parámetro puede ser nulo.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Habilita el ajuste de pantalla completo
        enableEdgeToEdge()
        // Establece el diseño de la actividad
        setContentView(R.layout.activity_visualiza_opti3d_dactivity)

        // Inicializa la vista OpenGL y el RecyclerView
        glSurfaceView = findViewById(R.id.glSurfaceView)

        initRecyclerView()

        // Configura la barra de acción para mostrar el botón de retroceso y establecer el título
        val toggle = supportActionBar
        toggle!!.setDisplayHomeAsUpEnabled(true)
        toggle.title = "Optimizacion 2D"

        // Obtiene colores desde los recursos
        val colorCaja = getColorArrayFromResources(this, R.color.primary)
        val colorBorde = getColorArrayFromResources(this, R.color.caja_border_color)

        // Verifica si el Intent tiene resultados para visualizar
        if (intent.hasExtra(MenuActivity.TESTRESULTADOS)) {
            resultadosList = intent.getParcelableArrayListExtraProvider(MenuActivity.TESTBULTOS)
        } else {
            Toast.makeText(baseContext, "Lista vacia", Toast.LENGTH_SHORT).show()
        }

        // Inicializa elementos de interfaz de usuario
        val zoomSeekBar: SeekBar = findViewById(R.id.zoom_seekbar)
        val btnNext: Button = findViewById(R.id.button_nextPaint)

        // Divide la lista de resultados en sublistas si no es nula
        if (resultadosList != null) {
            sublistas = dividirListaEnSublistas(resultadosList!!)
        }

        // Configurar zoom usando un SeekBar
        zoomSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Ajustar el zoom en función del progreso del SeekBar
                val zoomFactor = 1.0f + progress / 10.0f
                glSurfaceView.setZoomFactor(zoomFactor)
                resizeGLSurfaceView(glSurfaceView)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Configura el listener para el botón de pintar la siguiente caja
        btnNext.setOnClickListener {

            // Limpia la pantalla antes de pintar la siguiente caja
            glSurfaceView.resetSurfaceView()

            // Ir leyendo los resultados enviando a pintar unicamente cada caja con sus bultos
            // La funcion itera volviendo a mandar la siguiente caja
            if (currentIndex < sublistas.size) {
                // Ordena y pinta la caja actual
                val aPintar = sublistas[currentIndex].sortedWith(
                    compareBy<Resultado> { it.tipo }
                        .thenByDescending { it.area }
                )
                Toast.makeText(
                    baseContext,
                    "Pintando caja $currentIndex de tamaño ${aPintar.size}",
                    Toast.LENGTH_SHORT
                ).show()
                val ubicaciones = pintaResultados(aPintar)

                // Actualiza la lista de datos para el RecyclerView
                dataList.clear()

                for (index in aPintar) {
                    if (index.tipo == 1) {
                        dataList.add(index)
                    }
                }

                // Notificar al adaptador después de haber actualizado toda la lista
                recyclerView.adapter!!.notifyDataSetChanged()

                // Agrega rectángulos para la vista OpenGL
                for (res in ubicaciones) {
                    if (res.tipo == 0) {
                        rectangulos.add(
                            Rectangulo(
                                x = res.centroX,
                                y = res.centroY,
                                width = res.x,
                                height = res.y,
                                color = colorCaja,
                                borderColor = colorBorde,
                                borderWidth = 5.0f
                            )
                        )
                    } else {
                        rectangulos.add(
                            Rectangulo(
                                x = res.centroX,
                                y = res.centroY,
                                width = res.x,
                                height = res.y,
                                color = res.color,
                                borderColor = colorBorde,
                                borderWidth = 5.0f
                            )
                        )
                    }
                }

                glSurfaceView.setRectangulos(rectangulos)
                currentIndex++
            } else {
                // Limpiar la lista al final y notificar al adaptador
                dataList.clear()
                recyclerView.adapter!!.notifyDataSetChanged()
                Toast.makeText(baseContext, "Se han pintado todas las cajas", Toast.LENGTH_SHORT)
                    .show()
                currentIndex = 0
            }
        }
    }

    /**
     * Divide una lista de resultados en sublistas basadas en el tipo de cada resultado.
     *
     * Los elementos de tipo `0` inician una nueva sublista, mientras que los elementos de tipo `1`
     * se añaden a la sublista actual.
     *
     * @param lista La lista de resultados a dividir.
     * @return Una lista de sublistas, cada una conteniendo resultados agrupados.
     */
    private fun dividirListaEnSublistas(lista: List<Resultado>): List<List<Resultado>> {
        val sublistas = mutableListOf<MutableList<Resultado>>()
        var sublistaActual: MutableList<Resultado>? = null

        for (elemento in lista) {
            if (elemento.tipo == 0) {
                // Si encontramos una nueva caja, iniciamos una nueva sublista
                sublistaActual = mutableListOf()
                sublistaActual.add(elemento)
                sublistas.add(sublistaActual)
            } else {
                // Si encontramos un bulto, lo agregamos a la sublista actual
                sublistaActual?.add(elemento)
            }
        }
        return sublistas
    }

    /**
     * Ajusta el tamaño del `GLSurfaceView` para forzar una nueva renderización.
     *
     * Alterna entre reducir y aumentar el tamaño de la vista para solicitar una nueva renderización.
     *
     * @param glSurfaceView La vista OpenGL que se redimensionará.
     */
    private fun resizeGLSurfaceView(glSurfaceView: GLSurfaceView) {
        if (flag) {
            val width = glSurfaceView.width - 1
            val height = glSurfaceView.height - 1
            flag = false
            glSurfaceView.layout(0, 0, width, height)
        } else {
            val width = glSurfaceView.width + 1
            val height = glSurfaceView.height + 1
            flag = true
            glSurfaceView.layout(0, 0, width, height)
        }

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
        myInterfaceVisualizaOpti2DActivity!!.callBackOpti(data = "Vuelta")
        return true
    }

    /**
     * Dibuja los resultados en la vista OpenGL utilizando un algoritmo de colocación.
     *
     * Utiliza un algoritmo de backtracking para colocar bultos en una caja, tratando de maximizar
     * el uso del espacio. Los resultados se actualizan con las posiciones y dimensiones de los bultos.
     *
     * @param resultadosList La lista de resultados a pintar.
     * @return La lista actualizada de resultados con posiciones y dimensiones calculadas.
     *
     * @throws IllegalArgumentException Si no se encuentra la caja en los resultados proporcionados.
     */
    private fun pintaResultados(resultadosList: List<Resultado>): List<Resultado> {

        rectangulos.clear()
        val mejorSolucion = mutableListOf<Resultado>()

        // Convertir la lista a mutable
        val resultadosMutables = resultadosList.toMutableList()

        var caja: Resultado? = null
        val bultos = mutableListOf<Resultado>()

        for (res in resultadosMutables) {
            if (res.tipo == 0) {
                caja = res
                res.centroX = res.x / 2
                res.centroY = res.y / 2
            } else if (res.tipo == 1) {
                bultos.add(res)
            }
        }

        if (caja == null) {
            throw IllegalArgumentException("No se encontró la caja en los resultados proporcionados")
        }

        bultos.sortByDescending { it.x * it.y }

        // Función para verificar si una posición está ocupada
        fun esPosicionValida(x: Float, y: Float, ancho: Float, alto: Float): Boolean {
            if (x < 0 || y < 0 || x + ancho > caja.x || y + alto > caja.y) {
                return false
            }
            return bultos.none { bulto ->
                bulto.posX != Float.MIN_VALUE && bulto.posY != Float.MIN_VALUE &&
                        !(x + ancho <= bulto.posX || x >= bulto.posX + bulto.x ||
                                y + alto <= bulto.posY || y >= bulto.posY + bulto.y)
            }
        }

        // Función de backtracking para colocar bultos
        fun backtrack(index: Int): Boolean {
            if (index == bultos.size) {
                mejorSolucion.clear()
                mejorSolucion.addAll(bultos.map { it.copy() })
                return true
            }

            val bulto = bultos[index]

            for ((ancho, alto) in listOf(Pair(bulto.x, bulto.y), Pair(bulto.y, bulto.x))) {
                // Intentar colocar el bulto en todas las posiciones posibles
                for (y in 0 until caja.y.toInt()) {
                    for (x in 0 until caja.x.toInt()) {
                        if (esPosicionValida(x.toFloat(), y.toFloat(), ancho, alto)) {
                            bulto.posX = x.toFloat()
                            bulto.posY = y.toFloat()
                            bulto.centroX = x.toFloat() + ancho / 2
                            bulto.centroY = y.toFloat() + alto / 2

                            val originalAncho = bulto.x
                            val originalAlto = bulto.y
                            bulto.x = ancho
                            bulto.y = alto

                            // Probar el siguiente bulto
                            if (backtrack(index + 1)) {
                                return true
                            }

                            // Deshacer cambios si no funciona
                            bulto.x = originalAncho
                            bulto.y = originalAlto
                        }
                    }
                }
            }

            return false
        }

        backtrack(0)

        for (i in mejorSolucion.indices) {
            val bulto = mejorSolucion[i]
            resultadosMutables[i + 1].apply {
                posX = bulto.posX
                posY = bulto.posY
                centroX = bulto.centroX
                centroY = bulto.centroY
                x = bulto.x
                y = bulto.y
            }
        }

        return resultadosMutables
    }

    /**
     * Convierte un código de color entero en un array de valores flotantes.
     *
     * El array resultante contiene los valores RGBA del color, normalizados en el rango [0, 1].
     *
     * @param colorCode El código de color en formato entero.
     * @return Un array de valores flotantes representando el color.
     */
    private fun colorCodeToArrayOfFloat(colorCode: Int): FloatArray {
        val r = Color.red(colorCode) / 255f
        val g = Color.green(colorCode) / 255f
        val b = Color.blue(colorCode) / 255f
        val a = Color.alpha(colorCode) / 255f
        return floatArrayOf(r, g, b, a)
    }

    /**
     * Obtiene un array de valores flotantes del color desde los recursos.
     *
     * @param context El contexto para acceder a los recursos.
     * @param colorResId El ID del recurso de color.
     * @return Un array de valores flotantes representando el color.
     */
    private fun getColorArrayFromResources(context: Context, colorResId: Int): FloatArray {
        val colorInt = ContextCompat.getColor(context, colorResId)
        return colorCodeToArrayOfFloat(colorInt)
    }

    /**
     * Inicializa el RecyclerView para mostrar los resultados.
     */
    private fun initRecyclerView() {
        recyclerView = findViewById(R.id.recycler2D)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = MenuAdapter(dataList)
    }
}