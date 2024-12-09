package com.mosky.spaceit2d.util

import com.mosky.spaceit2d.provider.Bulto
import com.mosky.spaceit2d.provider.Caja

/**
 * Clase encargada de empaquetar objetos en cajas utilizando un algoritmo de backtracking.
 *
 * La clase intenta optimizar el empaquetado de una lista de [Bulto] en una lista de [Caja]
 * buscando una solución que cumpla con las restricciones de peso y área de cada caja.
 */
class Empaquetador {

    /**
     * Empaqueta una lista de bultos en una lista de cajas.
     *
     * Ordena los bultos por peso y área, y luego utiliza un algoritmo de backtracking para intentar
     * empaquetar cada bulto en una caja disponible.
     *
     * @param bultos Lista de bultos a empaquetar.
     * @param cajas Lista de cajas disponibles para empaquetar los bultos.
     * @return `true` si el empaquetado es exitoso para todos los bultos; `false` en caso contrario.
     */
    fun empaquetarObjetos(bultos: List<Bulto>, cajas: List<Caja>): Boolean {
        val bultosOrdenados = bultos.sortedWith(compareByDescending<Bulto> { it.peso }.thenBy { it.area })
        val cajasEstado = cajas.map { CajaEstado(it) }.toMutableList()

        //Intentar empaquetar usando backtracking
        return empaquetarBultosConBacktracking(bultosOrdenados, cajasEstado, 0)
    }

    /**
     * Intenta empaquetar los bultos en las cajas usando un algoritmo de backtracking.
     *
     * @param bultos Lista de bultos a empaquetar, ya ordenada.
     * @param cajasEstado Lista de estados de las cajas, que mantiene el seguimiento del peso actual en cada caja.
     * @param index Índice del bulto actual a empaquetar en la lista de bultos.
     * @return `true` si el empaquetado de todos los bultos es posible; `false` en caso contrario.
     */
    private fun empaquetarBultosConBacktracking(
        bultos: List<Bulto>,
        cajasEstado: MutableList<CajaEstado>,
        index: Int
    ): Boolean {
        //Si hemos asignado todos los bultos, verifica si la solución es válida
        if (index == bultos.size) {
            // Verifica si todas las cajas están llenas (ninguna caja debe estar vacía)
            return cajasEstado.all { it.caja.misBultos!!.isNotEmpty() }
        }

        val bulto = bultos[index]
        //Ordenar las cajas por peso acumulado
        val cajasOrdenadasPorPeso = cajasEstado.sortedBy { it.pesoActual }

        //Intentar agregar el bulto en cada caja, empezando por la que tenga menor peso acumulado
        for (cajaEstado in cajasOrdenadasPorPeso) {
            val caja = cajaEstado.caja
            if (intentarAgregarBulto(bulto, caja, cajaEstado)) {
                //Intentar empaquetar el siguiente bulto
                if (empaquetarBultosConBacktracking(bultos, cajasEstado, index + 1)) {
                    return true
                }

                //Si la recursión falla, revertir el cambio
                caja.eliminarObjeto(bulto)
                cajaEstado.pesoActual -= bulto.peso
            }
        }

        return false
    }

    /**
     * Intenta agregar un bulto en una caja en todas las posiciones posibles y con todas las orientaciones.
     *
     * @param bulto El bulto que se intentará agregar a la caja.
     * @param caja La caja en la que se intentará agregar el bulto.
     * @param cajaEstado El estado actual de la caja, incluyendo el peso acumulado.
     * @return `true` si el bulto se agrega exitosamente; `false` en caso contrario.
     */
    private fun intentarAgregarBulto(
        bulto: Bulto,
        caja: Caja,
        cajaEstado: CajaEstado
    ): Boolean {
        // Verificar todas las posiciones y orientaciones posibles
        for (x in 0 until caja.dimensionX) {
            for (y in 0 until caja.dimensionY) {
                if (caja.agregarObjetoEnPosicion(bulto, x, y, rotado = false) || caja.agregarObjetoEnPosicion(bulto, x, y, rotado = true)) {
                    cajaEstado.pesoActual += bulto.peso
                    return true
                }
            }
        }
        return false
    }

    /**
     * Clase de datos que representa el estado de una caja durante el proceso de empaquetado.
     *
     * @param caja La caja cuyo estado se está representando.
     * @param pesoActual El peso acumulado actual en la caja.
     */
    data class CajaEstado(val caja: Caja, var pesoActual: Float = 0f)
}