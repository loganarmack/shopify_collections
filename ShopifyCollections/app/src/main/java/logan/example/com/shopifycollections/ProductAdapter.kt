package logan.example.com.shopifycollections

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.product_list_element.view.*

class ProductAdapter(private val productDataset: List<Product>, private val clickListener: (Product) -> Unit):
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(product: Product, clickListener: (Product) -> Unit) = with(itemView) {
            title.text = product.title
            image.loadUrl(product.image.src)
            stock.text = "512 in stock"
            collection_title.text = "Aerodynamic Collection"
            collection_image.loadUrl("https://cdn.shopify.com/s/files/1/1000/7970/collections/Aerodynamic_20Cotton_20Keyboard_grande_b213aa7f-9a10-4860-8618-76d5609f2c19.png?v=1545072718")
            setOnClickListener { clickListener(product) }
        }
    }

    //create new view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductAdapter.ProductViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_list_element, parent, false) as View

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