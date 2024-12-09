package com.mosky.spaceit2d.adapter

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mosky.spaceit2d.R
import com.mosky.spaceit2d.databinding.ItemResultadoBinding
import com.mosky.spaceit2d.provider.Resultado

/**
 * ViewHolder que representa un solo elemento `Resultado` en un `RecyclerView`.
 *
 * @constructor Crea un ViewHolder para un elemento de tipo `Resultado`.
 * @param view La vista que representa un solo elemento del RecyclerView.
 */
class MenuViewHolder (view: View): RecyclerView.ViewHolder(view) {

    private val binding = ItemResultadoBinding.bind(view)
    private val imageView = view.findViewById<ImageView>(R.id.ivResultado)
    private val max = "max"

    /**
     * Renderiza los datos de un objeto `Resultado` en la vista.
     *
     * @param resultadoModel El modelo de datos de tipo `Resultado` que se va a mostrar.
     */
    fun render(resultadoModel: Resultado) {
        val context = itemView.context


        if (resultadoModel.tipo == 0) {
            imageView.setImageResource(R.drawable.ic_baseline_caja)
            binding.tvResultadoLim.text = resultadoModel.limite.toString()
            binding.tvResultadoLimiteInfo.text = max
            itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.caja_item_background))
        } else {
            imageView.setImageResource(R.drawable.ic_baseline_bulto)
            binding.ivResultadoColor.setBackgroundColor(floatArrayToColor(resultadoModel.color))
            itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.bulto_item_background))

        }
        binding.tvResultadoNombre.text = resultadoModel.nombre
        binding.tvResultadoPeso.text = resultadoModel.peso.toString()
    }

    // Function to convert FloatArray to Int color

    /**
     * Convierte un array de flotantes (FloatArray) a un color entero (Int).
     *
     * @param floatArray Array de flotantes que representa los componentes RGBA del color.
     * @return Un entero que representa el color en formato ARGB.
     */
    private fun floatArrayToColor(floatArray: FloatArray): Int {
        val r = (floatArray[0] * 255).toInt()
        val g = (floatArray[1] * 255).toInt()
        val b = (floatArray[2] * 255).toInt()
        val a = (floatArray[3] * 255).toInt()

        return Color.argb(a, r, g, b)
    }
}