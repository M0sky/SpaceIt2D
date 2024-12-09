package com.mosky.spaceit2d.util

import android.content.Context
import android.graphics.Color
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import androidx.core.content.ContextCompat
import com.mosky.spaceit2d.R
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Renderer para el uso con [GLSurfaceView] en OpenGL ES 2.0.
 *
 * Este renderer se encarga de configurar los shaders, la matriz de proyección y de vista, y
 * renderizar los objetos en la superficie de OpenGL.
 *
 * @param context Contexto de la aplicación para acceder a los recursos.
 */
class MyGLRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private var program: Int = 0

    private var rectangulos: List<Rectangulo> = emptyList()
    private var zoomFactor = 2.0f

    //Desplazamiento en el surfaceView
    private var offsetX = 0f
    private var offsetY = 0f

    /**
     * Configura el entorno OpenGL y compila los shaders.
     *
     * Este método se llama una vez cuando se crea la superficie OpenGL.
     *
     * @param gl Contexto OpenGL.
     * @param config Configuración de EGL.
     */
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        val colorBackground = getColorArrayFromResources(context, R.color.background)
        GLES20.glClearColor(colorBackground[0], colorBackground[1], colorBackground[2], colorBackground[3])

        // Código del shader de vértices
        val vertexShaderCode = """
            attribute vec4 vPosition;
            uniform mat4 uMVPMatrix;
            void main() {
                gl_Position = uMVPMatrix * vPosition;
            }
        """.trimIndent()

        // Código del shader de fragmentos
        val fragmentShaderCode = """
            precision mediump float;
            uniform vec4 vColor;
            void main() {
                gl_FragColor = vColor;
            }
        """.trimIndent()

        // Compilar y crear el programa de shaders
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }

        // Inicializar la matriz de vista
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -5f, 0f, 0f, 0f, 0f, 1f, 0f)
    }

    /**
     * Se llama para dibujar el contenido de la superficie.
     *
     * Este método se llama para cada frame de la superficie de OpenGL.
     *
     * @param gl Contexto OpenGL.
     */
    override fun onDrawFrame(gl: GL10?) {
        Log.e("OPENGL", "onDrawFrame coords X: $offsetX Y: $offsetY")
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glUseProgram(program)

        //Configurar la matriz de vista
        val mvpMatrix = FloatArray(16)
        Matrix.setIdentityM(mvpMatrix, 0)
        Matrix.translateM(mvpMatrix, 0, offsetX, offsetY, 0f)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0)

        // Dibujar todos los rectángulos
        synchronized(rectangulos) {
            for (rectangulo in rectangulos) {
                rectangulo.draw(program, viewMatrix, projectionMatrix)
            }
        }
    }

    /**
     * Se llama cuando cambia el tamaño de la superficie OpenGL.
     *
     * @param gl Contexto OpenGL.
     * @param width Nuevo ancho de la superficie.
     * @param height Nuevo alto de la superficie.
     */
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        updateProjectionMatrix(width, height)
    }

    /**
     * Actualiza la matriz de proyección con base en las dimensiones de la superficie.
     *
     * @param width Ancho de la superficie.
     * @param height Alto de la superficie.
     */
    private fun updateProjectionMatrix(width: Int, height: Int) {
        val ratio = width.toFloat() / height.toFloat()
        val left = -ratio * zoomFactor
        val right = ratio * zoomFactor
        val bottom = -zoomFactor
        val top = zoomFactor
        Matrix.orthoM(projectionMatrix, 0, left, right, bottom, top, 3f, 7f)
    }

    /**
     * Establece la lista de rectángulos que se deben dibujar.
     *
     * @param rectangulos Lista de rectángulos a dibujar.
     */
    fun setRectangulos(rectangulos: List<Rectangulo>) {
        synchronized(rectangulos) {
            this.rectangulos = rectangulos
        }
    }

    /**
     * Establece el factor de zoom para la proyección.
     *
     * @param zoom Factor de zoom a aplicar.
     */
    fun setZoomFactor(zoom: Float) {
        zoomFactor = zoom
    }

    /**
     * Reinicia el renderer y establece el color de fondo desde los recursos.
     *
     * @param context Contexto de la aplicación para acceder a los recursos.
     */
    fun reset(context: Context) {
        synchronized(rectangulos) {
            rectangulos = emptyList()
        }

        val colorBackground = getColorArrayFromResources(context, R.color.background)
        GLES20.glClearColor(colorBackground[0], colorBackground[1], colorBackground[2], colorBackground[3])
    }

    /**
     * Carga y compila un shader.
     *
     * @param type Tipo de shader (vértice o fragmento).
     * @param shaderCode Código fuente del shader.
     * @return ID del shader compilado.
     */
    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)

            // Verificar errores de compilación
            val compileStatus = IntArray(1)
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
            if (compileStatus[0] == 0) {
                Log.e("OPENGL", "Error compiling shader: ${GLES20.glGetShaderInfoLog(shader)}")
                GLES20.glDeleteShader(shader)
            }
        }
    }

    /**
     * Convierte un código de color entero (Int) a un array de Float.
     *
     * @param colorCode Código del color.
     * @return Array de flotantes representando el color (r, g, b, a).
     */
    private fun colorCodeToArrayOfFloat(colorCode: Int): FloatArray {
        val r = Color.red(colorCode) / 255f
        val g = Color.green(colorCode) / 255f
        val b = Color.blue(colorCode) / 255f
        val a = Color.alpha(colorCode) / 255f
        return floatArrayOf(r, g, b, a)
    }

    /**
     * Obtiene el color desde los recursos y lo convierte a un array de Float.
     *
     * @param context Contexto de la aplicación para acceder a los recursos.
     * @param colorResId ID del recurso de color.
     * @return Array de flotantes representando el color (r, g, b, a).
     */
    private fun getColorArrayFromResources(context: Context, colorResId: Int): FloatArray {
        val colorInt = ContextCompat.getColor(context, colorResId)
        return colorCodeToArrayOfFloat(colorInt)
    }
}

