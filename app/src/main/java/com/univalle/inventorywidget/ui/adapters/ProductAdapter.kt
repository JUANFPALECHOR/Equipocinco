package com.univalle.inventorywidget.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.univalle.inventorywidget.R
import com.univalle.inventorywidget.data.Product


class ProductAdapter(private val listaProductos: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {


    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNombre: TextView = itemView.findViewById(R.id.txtNombreProducto)
        val txtId: TextView = itemView.findViewById(R.id.txtIdProducto)
        val txtPrecio: TextView = itemView.findViewById(R.id.txtPrecioProducto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val producto = listaProductos[position]

        // Nombre e ID
        holder.txtNombre.text = producto.nombre
        holder.txtId.text = "Código: ${producto.codigo}"

        // y en formato colombiano con separadores de miles y dos decimales
        val formato = java.text.NumberFormat.getNumberInstance(java.util.Locale("es", "CO"))
        formato.minimumFractionDigits = 2
        formato.maximumFractionDigits = 2
        holder.txtPrecio.text = "$ ${formato.format(producto.precio)}"

        // Cambiar color del precio a naranja (HU 1.0 y HU 5)
        holder.txtPrecio.setTextColor(
            holder.itemView.context.getColor(R.color.naranja_principal)
        )
    }
 
    override fun getItemCount(): Int = listaProductos.size
}
