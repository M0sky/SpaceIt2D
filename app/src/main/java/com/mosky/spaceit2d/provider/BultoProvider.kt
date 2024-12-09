package com.mosky.spaceit2d.provider

/**
 * Proveedor de datos de bultos que almacena una lista de objetos [Bulto] y su cantidad total.
 */
class BultoProvider {
    companion object {
        /**
         * Lista que contiene todos los objetos [Bulto] disponibles.
         */
        var myBultos = arrayListOf<Bulto>()

        /**
         * Contador que representa la cantidad total de bultos en la lista [myBultos].
         */
        var myBultosCount : Int = 0
    }
}