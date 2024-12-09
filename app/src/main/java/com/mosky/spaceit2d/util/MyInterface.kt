package com.mosky.spaceit2d.util

import com.mosky.spaceit2d.provider.Bulto
import com.mosky.spaceit2d.provider.Caja

/**
 * Interfaz para la comunicación de datos genéricos.
 *
 * Implementar esta interfaz permite manejar la devolución de una cadena de datos.
 */
internal interface MyInterface {
    /**
     * Callback para recibir una cadena de datos.
     *
     * @param data La cadena de datos recibida. Puede ser `null`.
     */
    fun callBack(data: String?)
}

/**
 * Interfaz para manejar la lista de bultos y su conteo.
 *
 * Implementar esta interfaz permite recibir una lista de bultos y su cantidad.
 */
interface MyInterfaceBultos {
    /**
     * Callback para recibir una lista de bultos y su cantidad.
     *
     * @param bultosListAux La lista de bultos. Puede ser `null`.
     * @param bultosCountAux La cantidad de bultos.
     */
    fun callBackBultos(bultosListAux: ArrayList<Bulto>?, bultosCountAux: Int)
}

/**
 * Interfaz para manejar la edición de bultos.
 *
 * Implementar esta interfaz permite recibir un bulto que ha sido editado o añadido.
 */
interface MyInterfaceEditBultos {
    /**
     * Callback para recibir un bulto editado o nuevo.
     *
     * @param myNewBulto El bulto editado o nuevo. Puede ser `null`.
     */
    fun callBackBultos(myNewBulto: Bulto?)
}

/**
 * Interfaz para manejar la lista de cajas y su conteo.
 *
 * Implementar esta interfaz permite recibir una lista de cajas y su cantidad.
 */
interface MyInterfaceCajas {
    /**
     * Callback para recibir una lista de cajas y su cantidad.
     *
     * @param cajasListAux La lista de cajas. Puede ser `null`.
     * @param cajasCountAux La cantidad de cajas.
     */
    fun callBackCajas(cajasListAux: ArrayList<Caja>?, cajasCountAux: Int)
}

/**
 * Interfaz para manejar la edición de cajas.
 *
 * Implementar esta interfaz permite recibir una caja que ha sido editada o añadida.
 */
interface MyInterfaceEditCajas {
    /**
     * Callback para recibir una caja editada o nueva.
     *
     * @param myNewCaja La caja editada o nueva. Puede ser `null`.
     */
    fun callBackCajas(myNewCaja: Caja?)
}

/**
 * Interfaz para manejar el historial.
 *
 * Implementar esta interfaz permite recibir datos relacionados con el historial.
 */
interface MyInterfaceHistorial {
    /**
     * Callback para recibir datos del historial.
     *
     * @param data La cadena de datos del historial. Puede ser `null`.
     */
    fun callBackHistorial(data : String?)
}

/**
 * Interfaz para manejar los favoritos.
 *
 * Implementar esta interfaz permite recibir datos relacionados con los favoritos.
 */
interface MyInterfaceFavoritos {
    /**
     * Callback para recibir datos de los favoritos.
     *
     * @param data La cadena de datos de los favoritos. Puede ser `null`.
     */
    fun callBackFavoritos(data: String?)
}

/**
 * Interfaz para manejar la visualización de optimizaciones.
 *
 * Implementar esta interfaz permite recibir datos relacionados con las optimizaciones.
 */
interface MyInterfaceVisualizaOpti {
    /**
     * Callback para recibir datos de optimización.
     *
     * @param data La cadena de datos de optimización. Puede ser `null`.
     */
    fun callBackOpti(data: String?)
}