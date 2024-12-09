package com.mosky.spaceit2d.provider

import android.os.Parcel
import android.os.Parcelable

/**
 * Clase de datos que representa un bulto con varias propiedades.
 *
 * @property id Identificador único del bulto.
 * @property peso Peso del bulto.
 * @property dimensionX Dimensión en el eje X del bulto.
 * @property dimensionY Dimensión en el eje Y del bulto.
 * @property area Área del bulto.
 * @property imagen Ruta de la imagen asociada al bulto.
 * @property nombre Nombre del bulto.
 * @property color Array de flotantes que representa el color del bulto en formato RGBA.
 */
data class Bulto(
    val id: Int,
    var peso: Int,
    var dimensionX: Int,
    var dimensionY: Int,
    var area: Int,
    val imagen: String?,
    var nombre: String?,
    var color: FloatArray
): Parcelable {

    /**
     * Constructor que crea una instancia de [Bulto] a partir de un [Parcel].
     *
     * @param parcel El parcel del que se crean los datos.
     */
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.createFloatArray() ?: floatArrayOf(0f,0f,0f,0f) //Default if null
    )

    /**
     * Describe los contenidos del parcelable.
     *
     * @return Un entero que describe los tipos de objetos especiales contenidos en el parcelable.
     */
    override fun describeContents(): Int {
        return 0
    }

    /**
     * Escribe los datos del bulto al parcel.
     *
     * @param parcel El parcel en el que se escribirán los datos.
     * @param flags Flags adicionales sobre cómo se deben escribir los datos.
     */
    override fun writeToParcel(parcel: Parcel, p1: Int) {
        parcel.writeInt(id)
        parcel.writeInt(peso)
        parcel.writeInt(dimensionX)
        parcel.writeInt(dimensionY)
        parcel.writeInt(area)
        parcel.writeString(imagen)
        parcel.writeString(nombre)
        parcel.writeFloatArray(color)
    }

    /**
     * Creador de parcelables para la clase [Bulto].
     */
    companion object  CREATOR : Parcelable.Creator<Bulto> {

        /**
         * Crea una nueva instancia de [Bulto] a partir de un [Parcel].
         *
         * @param parcel El parcel del que se crea la instancia.
         * @return La instancia de [Bulto].
         */
        override fun createFromParcel(parcel: Parcel): Bulto {
            return Bulto(parcel)
        }

        /**
         * Crea un array de objetos [Bulto].
         *
         * @param size El tamaño del array.
         * @return Un array de objetos [Bulto].
         */
        override fun newArray(size: Int): Array<Bulto?> {
            return arrayOfNulls(size)
        }
    }
}
