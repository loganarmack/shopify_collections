package logan.example.com.shopifycollections

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.collection_list_element.view.*


class CollectionAdapter(private val collectionsDataset: List<CustomCollection>, private val clickListener: (CustomCollection) -> Unit):
    RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder>() {

    inner class CollectionViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(collection: CustomCollection, clickListener: (CustomCollection) -> Unit) = with(itemView) {
            title.text = collection.title
            image.loadUrl(collection.image.src)
            setOnClickListener { clickListener(collection) }
        }
    }

    //create new view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionAdapter.CollectionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.collection_list_element, parent, false) as View

        //set view size, margins, paddings, layout parameters


        return CollectionViewHolder(itemView)
    }

    //replace contents of view
    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        holder.bind(collectionsDataset[position], clickListener)
    }

    //return size of dataset
    override fun getItemCount() = collectionsDataset.size

    fun ImageView.loadUrl(url: String) {
        Picasso.get()
            .load(url)
            .into(this)
    }
}