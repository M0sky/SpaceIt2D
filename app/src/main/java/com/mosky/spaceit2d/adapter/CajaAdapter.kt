package com.mosky.spaceit2d.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mosky.spaceit2d.provider.Caja
import com.mosky.spaceit2d.R

/**
 * Adapter para manejar y mostrar una lista de objetos de tipo `Caja` en un `RecyclerView`.
 *
 * @property cajasArrayList Lista de objetos `Caja` que serán mostrados.
 * @property onClickListener Función lambda que se ejecutará al hacer clic en un elemento de la lista.
 */
class CajaAdapter (private val cajasArrayList:ArrayList<Caja>, private val onClickListener:(Caja) -> Unit) : RecyclerView.Adapter<CajaViewHolder>(){

    /**
     * Crea un nuevo ViewHolder para representar un elemento en el RecyclerView.
     *
     * @param parent ViewGroup al cual se añadirá el nuevo ViewHolder.
     * @param viewType Tipo de vista del nuevo ViewHolder (no se utiliza en este caso).
     * @return Una instancia de `CajaViewHolder` que contiene la vista inflada.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CajaViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CajaViewHolder(layoutInflater.inflate(R.layout.item_caja, parent, false))
    }

    /**
     * Retorna el número total de elementos en la lista de `cajasArrayList`.
     *
     * @return El tamaño de la lista de `cajasArrayList`.
     */
    override fun getItemCount(): Int = cajasArrayList.size

    /**
     * Asocia un `Caja` a su correspondiente `CajaViewHolder`.
     *
     * @param holder El `CajaViewHolder` que debe ser actualizado para mostrar los datos del elemento.
     * @param position La posición del elemento dentro del adaptador.
     */
    override fun onBindViewHolder(holder: CajaViewHolder, position: Int) {
        val item = cajasArrayList[position]
        holder.render(item, onClickListener)
    }
}