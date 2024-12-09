package com.mosky.spaceit2d.provider

/**
 * Proveedor de cajas que mantiene una lista de instancias de [Caja].
 *
 * Este objeto singleton se utiliza para almacenar y acceder a las cajas de manera global.
 */
class CajaProvider {
    companion object {
        /**
         * Lista de cajas actualmente disponibles.
         *
         * Se inicializa como una lista vacía y se puede modificar para agregar o eliminar cajas.
         */
        var myCajas = arrayListOf<Caja>()

        /**
         * Contador de cajas.
         *
         * Se utiliza para realizar un seguimiento del número total de cajas en la lista.
         */
        var myCajasCount : Int = 0
    }
}