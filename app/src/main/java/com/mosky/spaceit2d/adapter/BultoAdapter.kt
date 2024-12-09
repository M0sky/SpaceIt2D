package com.mosky.spaceit2d.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mosky.spaceit2d.provider.Bulto
import com.mosky.spaceit2d.R

/**
 * Adapter para manejar y mostrar una lista de objetos de tipo `Bulto` en un `RecyclerView`.
 *
 * @property bultosArrayList Lista de objetos `Bulto` que serán mostrados.
 * @property onClickListener Función lambda que se ejecutará al hacer clic en un elemento de la lista.
 */
class BultoAdapter (private val bultosArrayList:ArrayList<Bulto>, private val onClickListener:(Bulto) -> Unit) : RecyclerView.Adapter<BultoViewHolder>() {

    /**
     * Crea un nuevo ViewHolder para representar un elemento en el RecyclerView.
     *
     * @param parent ViewGroup al cual se añadirá el nuevo ViewHolder.
     * @param viewType Tipo de vista del nuevo ViewHolder (no se utiliza en este caso).
     * @return Una instancia de `BultoViewHolder` que contiene la vista inflada.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BultoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return BultoViewHolder(layoutInflater.inflate(R.layout.item_bulto, parent, false))
    }

    /**
     * Retorna el número total de elementos en la lista de `bultosArrayList`.
     *
     * @return El tamaño de la lista de `bultosArrayList`.
     */
    override fun getItemCount(): Int = bultosArrayList.size

    /**
     * Asocia un `Bulto` a su correspondiente `BultoViewHolder`.
     *
     * @param holder El `BultoViewHolder` que debe ser actualizado para mostrar los datos del elemento.
     * @param position La posición del elemento dentro del adaptador.
     */
    override fun onBindViewHolder(holder: BultoViewHolder, position: Int) {
        val item = bultosArrayList[position]
        holder.render(item, onClickListener)
    }
}

