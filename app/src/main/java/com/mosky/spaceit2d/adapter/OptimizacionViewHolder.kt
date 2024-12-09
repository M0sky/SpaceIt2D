package com.mosky.spaceit2d.adapter

import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mosky.spaceit2d.R
import com.mosky.spaceit2d.databinding.ItemOptimizacionBinding
import com.mosky.spaceit2d.provider.Optimizacion

/**
 * ViewHolder que representa un solo elemento `Optimizacion` en un `RecyclerView`.
 *
 * @constructor Crea un ViewHolder para un elemento de tipo `Optimizacion`.
 * @param itemView La vista que representa un solo elemento del RecyclerView.
 */
class OptimizacionViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
    private val binding = ItemOptimizacionBinding.bind(itemView)
    private val imageView = itemView.findViewById<ImageView>(R.id.ivOptiFavorito)

    /**
     * Renderiza los datos de un objeto `Optimizacion` en la vista.
     *
     * @param optimizacionModel El modelo de datos de tipo `Optimizacion` que se va a mostrar.
     * @param onClickListener Función lambda que se ejecutará cuando se haga clic en el elemento.
     */
    fun render(optimizacionModel: Optimizacion, onClickListener: (Optimizacion) -> Unit) {
        val context = itemView.context

        if (optimizacionModel.fav == 1) {
            imageView.setImageResource(R.drawable.ic_baseline_fav)
            context.getColor(R.color.primary)
            itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.caja_item_background))
        } else {
            imageView.setImageResource(R.drawable.ic_baseline_no_fav)
            itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.caja_item_background))
        }

        binding.tvOptimizacionId.text = optimizacionModel.id.toString()

        // Establece el fondo predeterminado para el item
        itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.optimizacion_item_background))

        itemView.setOnClickListener {
            onClickListener(optimizacionModel)
        }
    }
}