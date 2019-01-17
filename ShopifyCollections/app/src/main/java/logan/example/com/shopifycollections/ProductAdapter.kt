package logan.example.com.shopifycollections

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.collection_list_element.view.*

class ProductAdapter(private val productDataset: List<Product>, private val clickListener: (Product) -> Unit):
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(product: Product, clickListener: (Product) -> Unit) = with(itemView) {
            title.text = product.title
            image.loadUrl(product.image.src)
            setOnClickListener { clickListener(product) }
        }
    }

    //create new view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductAdapter.ProductViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.collection_list_element, parent, false) as View

        return ProductViewHolder(itemView)
    }

    //replace contents of view
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(productDataset[position], clickListener)
    }

    //return size of dataset
    override fun getItemCount() = productDataset.size

    fun ImageView.loadUrl(url: String) {
        Picasso.get()
            .load(url)
            .into(this)
    }
}