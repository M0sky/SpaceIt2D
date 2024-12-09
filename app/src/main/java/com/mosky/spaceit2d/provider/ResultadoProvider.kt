package com.mosky.spaceit2d.provider

/**
 * Clase sellada para proporcionar una lista mutable de resultados.
 *
 * La clase contiene una lista estática de resultados que puede ser utilizada
 * para almacenar y acceder a instancias de [Resultado].
 */
sealed class ResultadoProvider {
    companion object {
        /**
         * Lista mutable de instancias de [Resultado].
         *
         * Esta lista almacena los resultados que se pueden agregar, eliminar o modificar.
         */
        var myResultados = mutableListOf<Resultado>()
    }
}