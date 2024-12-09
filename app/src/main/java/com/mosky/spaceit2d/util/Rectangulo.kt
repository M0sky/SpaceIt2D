package com.mosky.spaceit2d.util

import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * Representa un rectángulo en OpenGL ES 2.0 con un color sólido y un borde opcional.
 *
 * @property x La coordenada X de la posición del rectángulo en el espacio de la vista.
 * @property y La coordenada Y de la posición del rectángulo en el espacio de la vista.
 * @property width El ancho del rectángulo.
 * @property height La altura del rectángulo.
 * @property color El color del rectángulo en formato RGBA como un array de flotantes.
 * @property borderColor El color del borde del rectángulo en formato RGBA como un array de flotantes. Puede ser `null` si no se desea un borde.
 * @property borderWidth El ancho del borde del rectángulo.
 */
data class Rectangulo(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val color: FloatArray,
    val borderColor: FloatArray? = null,
    val borderWidth: Float   // Ajusta este valor según sea necesario
) {
    // Buffer para los vértices del rectángulo
    private val vertexBuffer: FloatBuffer
    // Buffer para los índices del rectángulo
    private val indexBuffer: ByteBuffer
    // Estride de los vértices (it is the size in bytes used to store the vertex in the array)
    private val vertexStride = 3 * 4 // 3 valores por vértice * 4 bytes por float
    // Buffer para los vértices del borde del rectángulo
    private val borderVertexBuffer: FloatBuffer
    // Buffer para los índices del borde del rectángulo
    private val borderIndexBuffer: ByteBuffer

    init {
        // Definir los vértices del rectángulo
        val rectCoords = floatArrayOf(
            -0.5f,  0.5f, 0.0f,  // Top-left
            -0.5f, -0.5f, 0.0f,  // Bottom-left
            0.5f, -0.5f, 0.0f,  // Bottom-right
            0.5f,  0.5f, 0.0f   // Top-right
        )

        // Configurar el buffer de índices para el rectángulo
        indexBuffer = ByteBuffer.allocateDirect(6 * 2)
            .order(ByteOrder.nativeOrder())
            .put(byteArrayOf(0, 1, 2, 0, 2, 3))
        indexBuffer.position(0)

        // Configurar el buffer de vértices
        val bb = ByteBuffer.allocateDirect(rectCoords.size * 4) // 4 bytes por float
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(rectCoords)
        vertexBuffer.position(0)

        // Definir los vértices del borde del rectángulo
        val borderCoords = floatArrayOf(
            -0.5f,  0.5f, 0.0f,  // Top-left
            -0.5f, -0.5f, 0.0f,  // Bottom-left
            0.5f, -0.5f, 0.0f,  // Bottom-right
            0.5f,  0.5f, 0.0f,  // Top-right
            -0.5f,  0.5f, 0.0f   // Cierre del borde
        )

        // Configurar el buffer de índices del borde
        borderIndexBuffer = ByteBuffer.allocateDirect(8 * 2)
            .order(ByteOrder.nativeOrder())
            .put(byteArrayOf(0, 1, 2, 3, 4))
        borderIndexBuffer.position(0)

        // Configurar el buffer de vértices del borde
        val bbb = ByteBuffer.allocateDirect(borderCoords.size * 4) // 4 bytes por float
        bbb.order(ByteOrder.nativeOrder())
        borderVertexBuffer = bbb.asFloatBuffer()
        borderVertexBuffer.put(borderCoords)
        borderVertexBuffer.position(0)
    }

    /**
     * Dibuja el rectángulo utilizando el programa de shader especificado.
     *
     * @param program El identificador del programa de shader.
     * @param viewMatrix La matriz de vista que transforma las coordenadas del objeto al espacio de vista.
     * @param projectionMatrix La matriz de proyección que define la perspectiva de la cámara.
     */
    fun draw(program: Int, viewMatrix: FloatArray, projectionMatrix: FloatArray) {
        // Obtener la ubicación del atributo de posición
        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)

        // Obtener la ubicación de los uniformes de color y matriz MVP
        val colorHandle = GLES20.glGetUniformLocation(program, "vColor")
        val mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")

        // Configurar el atributo de posición con el buffer de vértices
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer)

        // Configurar el color del rectángulo
        GLES20.glUniform4fv(colorHandle, 1, color, 0)

        // Crear la matriz de transformación para posicionar el rectángulo (Matrix MVP)
        val mvpMatrix = FloatArray(16)
        val modelMatrix = FloatArray(16)
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, x, y, 0f)
        Matrix.scaleM(modelMatrix, 0, width, height, 1f)
        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0)

        // Pasar la matriz MVP al shader
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        // Dibujar el rectángulo
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_BYTE, indexBuffer)

        // Dibujar el borde si está definido
        if (borderColor != null) {
            GLES20.glLineWidth(borderWidth)
            GLES20.glUniform4fv(colorHandle, 1, borderColor, 0)

            GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, vertexStride, borderVertexBuffer)
            GLES20.glDrawElements(GLES20.GL_LINE_LOOP, 5, GLES20.GL_UNSIGNED_BYTE, borderIndexBuffer)
        }
        // Desactivar el atributo de posición
        GLES20.glDisableVertexAttribArray(positionHandle)
    }
}