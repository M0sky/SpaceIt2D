package com.mosky.spaceit2d.adapter

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mosky.spaceit2d.databinding.ItemBultoBinding
import com.mosky.spaceit2d.provider.Bulto

/**
 * ViewHolder que representa un solo elemento `Bulto` en un `RecyclerView`.
 *
 * @constructor Crea un ViewHolder para un elemento de tipo `Bulto`.
 * @param view La vista que representa un solo elemento del RecyclerView.
 */
class BultoViewHolder(view: View):RecyclerView.ViewHolder(view) {

    private val binding = ItemBultoBinding.bind(view)

    /**
     * Renderiza los datos de un objeto `Bulto` en la vista.
     *
     * @param bultoModel El modelo de datos de tipo `Bulto` que se va a mostrar.
     * @param onClickListener Función lambda que se ejecutará cuando se haga clic en el elemento.
     */
    fun render(bultoModel: Bulto, onClickListener: (Bulto) -> Unit){
        binding.tvBultoNombre.text = bultoModel.nombre
        binding.tvBultoPeso.text = bultoModel.peso.toString()
        binding.ivBultoColor

        binding.ivBultoColor.setBackgroundColor(floatArrayToColor(bultoModel.color))

        itemView.setOnClickListener {
            onClickListener(bultoModel)
        }
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