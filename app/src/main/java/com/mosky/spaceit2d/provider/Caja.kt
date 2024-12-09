package com.mosky.spaceit2d.provider

import android.os.Parcel
import android.os.Parcelable

/**
 * Representa una caja que puede contener objetos de tipo [Bulto].
 *
 * @property idC Identificador único de la caja.
 * @property pesoMax Peso máximo que la caja puede soportar.
 * @property pesoActual Peso total actual de los objetos en la caja.
 * @property dimensionX Dimensión horizontal de la caja.
 * @property dimensionY Dimensión vertical de la caja.
 * @property areaMax Área máxima que la caja puede ocupar.
 * @property areaActual Área total actual ocupada por los objetos en la caja.
 * @property limBultos Límite máximo de bultos que la caja puede contener.
 * @property misBultos Lista de bultos actualmente contenidos en la caja. Puede ser nula.
 * @property imagen Ruta o identificador de la imagen asociada con la caja.
 * @property nombre Nombre de la caja.
 */
data class Caja(
    val idC: Int,
    var pesoMax: Int,
    var pesoActual: Int,
    var dimensionX: Int,
    var dimensionY: Int,
    var areaMax: Int,
    var areaActual: Int,
    var limBultos: Int,
    var misBultos: ArrayList<Bulto>?,
    val imagen: String?,
    var nombre: String?
): Parcelable {

    /**
     * Constructor para crear una instancia de [Caja] desde un [Parcel].
     *
     * @param parcel [Parcel] que contiene los datos para inicializar la caja.
     */
    constructor(parcel: Parcel) : this (
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.createTypedArrayList(Bulto.CREATOR),
        parcel.readString(),
        parcel.readString()
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
     * Escribe los datos de la caja en un [Parcel].
     *
     * @param parcel [Parcel] en el que se escribirán los datos.
     * @param p1 Flags adicionales para el proceso de escritura.
     */
    override fun writeToParcel(parcel: Parcel, p1: Int) {
        parcel.writeInt(idC)
        parcel.writeInt(pesoMax)
        parcel.writeInt(pesoActual)
        parcel.writeInt(dimensionX)
        parcel.writeInt(dimensionY)
        parcel.writeInt(areaMax)
        parcel.writeInt(areaActual)
        parcel.writeInt(limBultos)
        parcel.writeTypedList(misBultos)
        parcel.writeString(imagen)
        parcel.writeString(nombre)
    }

    /**
     * Creador para crear instancias de [Caja] a partir de un [Parcel].
     */
    companion object CREATOR : Parcelable.Creator<Caja> {
        /**
         * Crea una nueva instancia de [Caja] a partir de un [Parcel].
         *
         * @param parcel El parcel del que se crea la instancia.
         * @return La instancia de [Caja].
         */
        override fun createFromParcel(parcel: Parcel): Caja {
            return Caja(parcel)
        }

        /**
         * Crea un array de objetos [Caja].
         *
         * @param size El tamaño del array.
         * @return Un array de objetos [Caja].
         */
        override fun newArray(size: Int): Array<Caja?> {
            return arrayOfNulls(size)
        }
    }

    /**
     * Verifica si un [Bulto] puede ser agregado en una posición específica dentro de la caja.
     *
     * @param objeto El [Bulto] que se desea agregar.
     * @param x Coordenada X de la posición de adición.
     * @param y Coordenada Y de la posición de adición.
     * @param rotado Indica si el [Bulto] está rotado (por defecto es false).
     * @return True si el [Bulto] puede ser agregado, false en caso contrario.
     */
    private fun puedeAgregarEnPosicion(objeto: Bulto, x: Int, y: Int, rotado: Boolean = false): Boolean {
        val dimensionX = if (rotado) objeto.dimensionY else objeto.dimensionX
        val dimensionY = if (rotado) objeto.dimensionX else objeto.dimensionY

        if (x + dimensionX > this.dimensionX || y + dimensionY > this.dimensionY) {
            return false
        }

        if (colisionaConOtrosBultos(objeto, x, y, rotado)) {
            return false
        }

        return pesoActual + objeto.peso <= pesoMax &&
                areaActual + objeto.area <= areaMax &&
                (misBultos == null || misBultos!!.size < limBultos)
    }

    /**
     * Agrega un [Bulto] en una posición específica dentro de la caja si es posible.
     *
     * @param objeto El [Bulto] que se desea agregar.
     * @param x Coordenada X de la posición de adición.
     * @param y Coordenada Y de la posición de adición.
     * @param rotado Indica si el [Bulto] está rotado (por defecto es false).
     * @return True si el [Bulto] fue agregado exitosamente, false en caso contrario.
     */
    fun agregarObjetoEnPosicion(objeto: Bulto, x: Int, y: Int, rotado: Boolean = false): Boolean {
        if (puedeAgregarEnPosicion(objeto, x, y, rotado)) {
            if (misBultos == null) {
                misBultos = ArrayList()
            }
            misBultos!!.add(objeto)
            pesoActual += objeto.peso
            areaActual += objeto.area
            return true
        }
        return false
    }

    /**
     * Verifica si un [Bulto] colisiona con otros bultos ya presentes en la caja.
     *
     * @param objeto El [Bulto] que se desea verificar.
     * @param x Coordenada X de la posición del [Bulto].
     * @param y Coordenada Y de la posición del [Bulto].
     * @param rotado Indica si el [Bulto] está rotado (por defecto es false).
     * @return True si hay colisión con otros bultos, false en caso contrario.
     */
    private fun colisionaConOtrosBultos(objeto: Bulto, x: Int, y: Int, rotado: Boolean = false): Boolean {
        val dimensionX = if (rotado) objeto.dimensionY else objeto.dimensionX
        val dimensionY = if (rotado) objeto.dimensionX else objeto.dimensionY

        for (bulto in misBultos.orEmpty()) {
            val bultoX = bulto.dimensionX
            val bultoY = bulto.dimensionY

            if (x < bultoX && x + dimensionX > 0 && y < bultoY && y + dimensionY > 0) {
                return true
            }
        }
        return false
    }

    /**
     * Elimina un [Bulto] de la caja y actualiza el peso y área actuales.
     *
     * @param objeto El [Bulto] que se desea eliminar.
     */
    fun eliminarObjeto(objeto: Bulto) {
        if (misBultos != null) {
            misBultos!!.remove(objeto)
            pesoActual -= objeto.peso
            areaActual -= objeto.area
        }
    }

}