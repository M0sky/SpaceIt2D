package com.mosky.spaceit2d.util

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

/**
 * Una vista personalizada que usa OpenGL ES 2.0 para renderizar gráficos.
 *
 * Esta clase extiende [GLSurfaceView] y se encarga de configurar el contexto de OpenGL,
 * establecer el renderer para dibujar en la vista y manejar la actualización de la vista.
 *
 * @param context Contexto de la aplicación para acceder a recursos y servicios del sistema.
 * @param attrs Atributos del XML para la vista.
 */
class MyGLSurfaceView (context: Context, attrs: AttributeSet): GLSurfaceView(context, attrs) {

    private val renderer: MyGLRenderer = MyGLRenderer(context)

    init {
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer)
        // Render the view only when there is a change in the drawing data
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    /**
     * Reinicia la vista de superficie OpenGL.
     *
     * Esto reinicia el estado del renderer y solicita un nuevo renderizado.
     */
    fun resetSurfaceView() {
        queueEvent {
            renderer.reset(context)
            requestRender()
        }
    }

    /**
     * Establece la lista de rectángulos que se deben dibujar en la vista.
     *
     * @param rectangulos Lista de rectángulos a renderizar.
     */
    fun setRectangulos(rectangulos: List<Rectangulo>) {
        queueEvent {
            renderer.setRectangulos(rectangulos)
            requestRender()
        }
    }

    /**
     * Establece el factor de zoom para la vista de OpenGL.
     *
     * @param zoom Factor de zoom a aplicar.
     */
    fun setZoomFactor(zoom: Float) {
        renderer.setZoomFactor(zoom)
        requestRender()
    }
}