package com.mosky.spaceit2d.provider

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

/**
 * Clase que representa una optimización en el sistema, que incluye un identificador, un valor favorito y una lista de resultados.
 *
 * @property id Identificador único para la optimización.
 * @property fav Valor de favorito asociado a la optimización.
 * @property miLista Lista de resultados asociados a la optimización. Puede ser nula.
 */
data class Optimizacion(
    var id: Int,
    var fav: Int,
    var miLista: ArrayList<Resultado>?
): Parcelable {

    /**
     * Constructor que crea una instancia de [Optimizacion] a partir de un [Parcel].
     *
     * @param parcel El [Parcel] que contiene los datos para reconstruir la instancia.
     */
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.createTypedArrayList(Resultado.CREATOR)
    )

    /**
     * Escribe los datos de la instancia en un [Parcel].
     *
     * @param parcel El [Parcel] en el que se deben escribir los datos.
     * @param flags Banderas adicionales para la serialización.
     */
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(fav)
        parcel.writeTypedList(miLista)
    }

    /**
     * Devuelve un valor de descripción de los contenidos del [Parcel].
     *
     * @return Un entero que describe los contenidos del [Parcel]. Generalmente devuelve 0.
     */
    override fun describeContents(): Int {
        return 0
    }

    /**
     * Creador para la serialización y deserialización de [Optimizacion] usando [Parcel].
     */
    companion object CREATOR : Parcelable.Creator<Optimizacion> {
        /**
         * Crea una instancia de [Optimizacion] a partir de un [Parcel].
         *
         * @param parcel El [Parcel] que contiene los datos.
         * @return Una nueva instancia de [Optimizacion].
         */
        override fun createFromParcel(parcel: Parcel): Optimizacion {
            return Optimizacion(parcel)
        }

        /**
         * Crea un array de [Optimizacion] con un tamaño específico.
         *
         * @param size El tamaño del array.
         * @return Un array de [Optimizacion] con el tamaño especificado.
         */
        override fun newArray(size: Int): Array<Optimizacion?> {
            return arrayOfNulls(size)
        }
    }
}