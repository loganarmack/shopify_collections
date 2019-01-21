package logan.example.com.shopifycollections

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.product_list_element.view.*

//adapter for the product list recyclerview
class ProductAdapter(private val productDataset: List<Product>,
                     private val collection: CustomCollection,
                     private val context: Context): RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    //sets content of view
    inner class ProductViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(product: Product) {
            itemView.product_name.text = product.title

            //sets each element to be correct size while image is loading
            val params: ViewGroup.LayoutParams = itemView.product_image.layoutParams
            params.height = product.image.height
            params.width = product.image.width
            itemView.product_image.layoutParams = params
            itemView.product_image.loadUrl(product.image.src)

            //calculates the total stock across all variants of a product
            var numStock = 0
            for (i in product.variants) {
                numStock += i.inventoryQuantity
            }

            itemView.stock.text = context.getString(R.string.items_in_stock, numStock)
            itemView.collection_title.text = collection.title
            itemView.collection_image.loadUrl(collection.image.src)
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
        holder.bind(productDataset[position])
    }

    //return size of dataset
    override fun getItemCount() = productDataset.size

    //sets image from url
    fun ImageView.loadUrl(url: String) {
        Picasso.get()
            .load(url)
            .into(this)
    }
}