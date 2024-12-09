package com.mosky.spaceit2d.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mosky.spaceit2d.R
import com.mosky.spaceit2d.provider.Resultado

/**
 * Adapter para manejar y mostrar una lista de objetos de tipo `Resultado` en un `RecyclerView`.
 *
 * @property listaResultados Lista de objetos `Resultado` que serán mostrados.
 */
class MenuAdapter(private val listaResultados: List<Resultado>) : RecyclerView.Adapter<MenuViewHolder>(){

    /**
     * Crea un nuevo ViewHolder para representar un elemento en el RecyclerView.
     *
     * @param parent ViewGroup al cual se añadirá el nuevo ViewHolder.
     * @param viewType Tipo de vista del nuevo ViewHolder (no se utiliza en este caso).
     * @return Una instancia de `MenuViewHolder` que contiene la vista inflada.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MenuViewHolder(layoutInflater.inflate(R.layout.item_resultado, parent, false))
    }

    /**
     * Retorna el número total de elementos en la lista de `listaResultados`.
     *
     * @return El tamaño de la lista de `listaResultados`.
     */
    override fun getItemCount(): Int = listaResultados.size

    /**
     * Asocia un `Resultado` a su correspondiente `MenuViewHolder`.
     *
     * @param holder El `MenuViewHolder` que debe ser actualizado para mostrar los datos del elemento.
     * @param position La posición del elemento dentro del adaptador.
     */
    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val item = listaResultados[position]
        holder.render(item)
    }

}