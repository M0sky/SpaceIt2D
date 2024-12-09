package com.mosky.spaceit2d.provider

import android.os.Parcel
import android.os.Parcelable

/**
 * Clase que representa un resultado en el sistema, que incluye propiedades como el tipo, peso, límite, y la posición.
 *
 * @property tipo Tipo de resultado, representado como un entero.
 * @property id Identificador único del resultado.
 * @property peso Peso del resultado.
 * @property limite Límite asociado al resultado.
 * @property imagen URL o ruta de la imagen asociada al resultado.
 * @property nombre Nombre del resultado.
 * @property x Coordenada X del resultado.
 * @property y Coordenada Y del resultado.
 * @property area Área ocupada por el resultado.
 * @property centroX Coordenada X del centro del resultado.
 * @property centroY Coordenada Y del centro del resultado.
 * @property posX Posición X del resultado.
 * @property posY Posición Y del resultado.
 * @property color Array de flotantes que representa el color del resultado en formato RGBA.
 */
data class Resultado (
    var tipo: Int,
    var id : Int,
    var peso : Int,
    var limite : Int,
    var imagen: String,
    var nombre: String,
    var x: Float,
    var y: Float,
    var area: Int,
    var centroX: Float,
    var centroY: Float,
    var posX: Float,
    var posY: Float,
    var color: FloatArray
): Parcelable {

    /**
     * Constructor que crea una instancia de [Resultado] a partir de un [Parcel].
     *
     * @param parcel El [Parcel] que contiene los datos para reconstruir la instancia.
     */
    constructor(parcel: Parcel) : this (
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readInt(),
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.createFloatArray() ?: floatArrayOf(0f,0f,0f,0f) //Default if null
    )

    /**
     * Devuelve un valor de descripción de los contenidos del [Parcel].
     *
     * @return Un entero que describe los contenidos del [Parcel]. Generalmente devuelve 0.
     */
    override fun describeContents(): Int {
        return 0
    }

    /**
     * Escribe los datos de la instancia en un [Parcel].
     *
     * @param parcel El [Parcel] en el que se deben escribir los datos.
     * @param p1 Banderas adicionales para la serialización.
     */
    override fun writeToParcel(parcel: Parcel, p1: Int) {
        parcel.writeInt(tipo)
        parcel.writeInt(id)
        parcel.writeInt(peso)
        parcel.writeInt(limite)
        parcel.writeString(imagen)
        parcel.writeString(nombre)
        parcel.writeFloat(x)
        parcel.writeFloat(y)
        parcel.writeInt(area)
        parcel.writeFloat(centroX)
        parcel.writeFloat(centroY)
        parcel.writeFloat(posX)
        parcel.writeFloat(posY)
        parcel.writeFloatArray(color)
    }

    /**
     * Creador para la serialización y deserialización de [Resultado] usando [Parcel].
     */
    companion object CREATOR : Parcelable.Creator<Resultado> {
        /**
         * Crea una instancia de [Resultado] a partir de un [Parcel].
         *
         * @param parcel El [Parcel] que contiene los datos.
         * @return Una nueva instancia de [Resultado].
         */
        override fun createFromParcel(parcel: Parcel): Resultado {
            return Resultado(parcel)
        }

        /**
         * Crea un array de [Resultado] con un tamaño específico.
         *
         * @param size El tamaño del array.
         * @return Un array de [Resultado] con el tamaño especificado.
         */
        override fun newArray(size: Int): Array<Resultado?> {
            return arrayOfNulls(size)
        }
    }
}