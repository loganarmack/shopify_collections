package logan.example.com.shopifycollections

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class CollectionActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ProductAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var collection: CustomCollection //stores the collection that is being displayed
    private var expandedState = false //stores whether title card is expanded or not
    private var products = ArrayList<Product>()
    private val accessor = APIAccessor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection)

        collection = intent.getParcelableExtra(COLLECTION)

        //sets up collection data onto title card
        findViewById<TextView>(R.id.collection_title).text = collection.title
        val body = findViewById<TextView>(R.id.collection_body)
        body.text = collection.body
        findViewById<ImageView>(R.id.collection_image).loadUrl(collection.image.src)

        //only shows expand button if necessary (body text is being ellipsized)
        val expandCollapseButton = findViewById<ImageButton>(R.id.expand_collapse_button)
        val runnable = Runnable {
            val lineCount = body.lineCount
            if (lineCount > 0) {
                val ellipsisCount = body.layout.getEllipsisCount(lineCount-1)
                if (ellipsisCount > 0) {
                    expandCollapseButton.visibility = View.VISIBLE
                }
            }
        }
        body.post(runnable)

        //expand/collapse body text button
        expandCollapseButton.setOnClickListener {
            onExpandCollapseClick(it)
        }

        //back arrow
        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
        }

        //sets up recyclerview to display products
        viewManager = LinearLayoutManager(this)
        viewAdapter = ProductAdapter(products, collection, this)
        recyclerView = findViewById<RecyclerView>(R.id.product_recycler).apply {
            layoutManager = viewManager
            addItemDecoration(SimpleDividerItemDecoration(this@CollectionActivity))
            itemAnimator = DefaultItemAnimator()
            adapter = viewAdapter
        }

        loadProductIds()
    }

    //processes what to do when expand/collapse button is clicked
    fun onExpandCollapseClick(view: View) {
        expandedState = !expandedState //alternates state between collapsed/expanded
        val button = findViewById<ImageButton>(R.id.expand_collapse_button)
        val body = findViewById<TextView>(R.id.collection_body)

        //changes maxlines depending on state of expand arrow
        body.maxLines = if (expandedState) Integer.MAX_VALUE else 1

        //animates expansion/collapsion of body text
        val startHeight = body.measuredHeight
        body.measure(View.MeasureSpec.makeMeasureSpec(
            body.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val endHeight = body.measuredHeight
        val expandAnimation = ObjectAnimator.ofInt(
            body,
            "maxHeight",
            startHeight,
            endHeight
        )
        expandAnimation.duration = 300
        expandAnimation.start()

        //starts animation to rotate collapse/expand arrow
        button.startAnimation(
            if (expandedState)
                AnimationUtils.loadAnimation(this, R.anim.rotate_expand)
            else
                AnimationUtils.loadAnimation(this, R.anim.rotate_collapse)
        )
    }

    //adds animation when returning to main activity
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    //loads the ids of the products, but doesn't load the products themselves
    private fun loadProductIds() {
        var productIds = ""
        //sends API call to get ids
        val call = accessor.apiService.loadCollectionProducts(collection.id)

        //starts loading icon
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        progressBar.visibility = View.VISIBLE

        //receives callback from api
        call.enqueue(object: Callback<ProductIdList> {
            override fun onResponse(call: Call<ProductIdList>, response: Response<ProductIdList>) {
                if (response.isSuccessful) {
                    //server gives successful response
                    val apiResponse: ProductIdList? = response.body()
                    if (apiResponse == null) {
                        //Api returned no data but received request
                        progressBar.visibility = View.GONE
                        MainActivity.toast(getString(R.string.failed_access_api), this@CollectionActivity)
                    }
                    //data is loaded successfully
                    else {
                        //adds all product ids into a string to be used to call api for actual products
                        for (i in apiResponse.collects) {
                            productIds += i.productId.toString() + ","
                        }
                        productIds = productIds.substring(0, productIds.length-1) //removes trailing comma
                        loadProducts(productIds)
                    }
                }
                else {
                    //error with api call
                    progressBar.visibility = View.GONE
                    MainActivity.toast(getString(R.string.unknown_error), this@CollectionActivity)
                }
            }
            //no internet
            override fun onFailure(call: Call<ProductIdList>, t: Throwable) {
                MainActivity.toast(getString(R.string.no_internet), this@CollectionActivity)
            }
        })
    }

    //loads the actual products into the recyclerview
    fun loadProducts(ids: String) {
        //sends request for products to api
        val call = accessor.apiService.loadProducts(ids)

        //turns on loading icon
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        progressBar.visibility = View.VISIBLE

        //receives request
        call.enqueue(object: Callback<ProductList> {
            override fun onResponse(call: Call<ProductList>, response: Response<ProductList>) {
                if (response.isSuccessful) {
                    val apiResponse: ProductList? = response.body()
                    if (apiResponse == null) {
                        //Api returned no data but received request
                        progressBar.visibility = View.GONE
                        MainActivity.toast(getString(R.string.failed_access_api), this@CollectionActivity)
                    }
                    //data is loaded successfully
                    else {
                        //adds products into recyclerview
                        progressBar.visibility = View.GONE
                        for (i in apiResponse.products) {
                            products.add(i)
                        }
                        viewAdapter.notifyDataSetChanged()
                    }
                }
                else {
                    //error with request
                    progressBar.visibility = View.GONE
                    MainActivity.toast(getString(R.string.unknown_error), this@CollectionActivity)
                }
            }
            //no internet
            override fun onFailure(call: Call<ProductList>, t: Throwable) {
                MainActivity.toast(getString(R.string.no_internet), this@CollectionActivity)
            }
        })
    }

    //sets image of imageview from url
    fun ImageView.loadUrl(url: String) {
        Picasso.get()
            .load(url)
            .into(this)
    }

    companion object {
        const val COLLECTION = "collection"
    }
}
