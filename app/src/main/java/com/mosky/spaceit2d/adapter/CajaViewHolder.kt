package com.mosky.spaceit2d.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mosky.spaceit2d.databinding.ItemCajaBinding
import com.mosky.spaceit2d.provider.Caja

/**
 * ViewHolder que representa un solo elemento `Caja` en un `RecyclerView`.
 *
 * @constructor Crea un ViewHolder para un elemento de tipo `Caja`.
 * @param view La vista que representa un solo elemento del RecyclerView.
 */
class CajaViewHolder(view: View):RecyclerView.ViewHolder(view) {

    val binding = ItemCajaBinding.bind(view)

    /**
     * Renderiza los datos de un objeto `Caja` en la vista.
     *
     * @param cajaModel El modelo de datos de tipo `Caja` que se va a mostrar.
     * @param onClickListener Función lambda que se ejecutará cuando se haga clic en el elemento.
     */
    fun render(cajaModel: Caja, onClickListener: (Caja) -> Unit) {
        binding.tvCajaNombre.text = cajaModel.nombre
        binding.tvCajaPeso.text = cajaModel.pesoMax.toString()
        binding.tvCajaLimite.text = cajaModel.limBultos.toString()

        itemView.setOnClickListener {
            onClickListener(cajaModel)
        }
    }
}