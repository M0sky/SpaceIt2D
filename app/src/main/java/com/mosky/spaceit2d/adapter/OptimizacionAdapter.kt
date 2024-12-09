package com.mosky.spaceit2d.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mosky.spaceit2d.R
import com.mosky.spaceit2d.provider.Optimizacion

/**
 * Adapter para manejar y mostrar una lista de objetos de tipo `Optimizacion` en un `RecyclerView`.
 *
 * @property listaOptimizaciones Lista de objetos `Optimizacion` que serán mostrados.
 * @property onClickListener Función lambda que se ejecutará al hacer clic en un elemento de la lista.
 */
class OptimizacionAdapter(private val listaOptimizaciones: List<Optimizacion>, private val onClickListener:(Optimizacion) -> Unit) : RecyclerView.Adapter<OptimizacionViewHolder>() {

    /**
     * Crea un nuevo ViewHolder para representar un elemento en el RecyclerView.
     *
     * @param parent ViewGroup al cual se añadirá el nuevo ViewHolder.
     * @param viewType Tipo de vista del nuevo ViewHolder (no se utiliza en este caso).
     * @return Una instancia de `OptimizacionViewHolder` que contiene la vista inflada.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptimizacionViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return OptimizacionViewHolder(layoutInflater.inflate(R.layout.item_optimizacion, parent, false))
    }

    /**
     * Retorna el número total de elementos en la lista de `listaOptimizaciones`.
     *
     * @return El tamaño de la lista de `listaOptimizaciones`.
     */
    override fun getItemCount(): Int = listaOptimizaciones.size

    /**
     * Asocia un `Optimizacion` a su correspondiente `OptimizacionViewHolder`.
     *
     * @param holder El `OptimizacionViewHolder` que debe ser actualizado para mostrar los datos del elemento.
     * @param position La posición del elemento dentro del adaptador.
     */
    override fun onBindViewHolder(holder: OptimizacionViewHolder, position: Int) {
        val item = listaOptimizaciones[position]
        holder.render(item, onClickListener)
    }
}