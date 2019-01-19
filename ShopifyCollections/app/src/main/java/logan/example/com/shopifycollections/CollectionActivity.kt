package logan.example.com.shopifycollections

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.widget.ImageView
import android.widget.ProgressBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CollectionActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ProductAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var collection: CustomCollection

    private var products = ArrayList<Product>()

    private val accessor = APIAccessor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //toolbar
        val v: View = findViewById(R.id.toolbar)
        val toolbar = v.findViewById<android.support.v7.widget.Toolbar>(R.id.toolbar)

        //back arrow
        v.findViewById<ImageView>(R.id.hidden_back_button).visibility = View.GONE
        toolbar.setNavigationIcon(R.drawable.back)
        toolbar.setNavigationOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
        }

        viewManager = LinearLayoutManager(this)
        //needs to be changed
        viewAdapter = ProductAdapter(products) {
            MainActivity.toast("Nice!", this@CollectionActivity)
        }

        recyclerView = findViewById<RecyclerView>(R.id.collection_recycler).apply {
            layoutManager = viewManager
            addItemDecoration(SimpleDividerItemDecoration(this@CollectionActivity))
            itemAnimator = DefaultItemAnimator()
            adapter = viewAdapter
        }

        collection = intent.getParcelableExtra(COLLECTION)

        loadProductIds()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun loadProductIds() {
        var productIds: String = ""
        val call = accessor.apiService.loadCollectionProducts(collection.id)

        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        progressBar.visibility = View.VISIBLE

        call.enqueue(object: Callback<ProductIdList> {
            override fun onResponse(call: Call<ProductIdList>, response: Response<ProductIdList>) {
                if (response.isSuccessful) {
                    val apiResponse: ProductIdList? = response.body()
                    if (apiResponse == null) {
                        //Api returned no data but received request
                        progressBar.visibility = View.GONE
                        MainActivity.toast("Failed to load API!", this@CollectionActivity)
                    }
                    //data is loaded successfully
                    else {
                        for (i in apiResponse.collects) {
                            productIds += i.productId.toString() + ","
                        }
                        productIds = productIds.substring(0, productIds.length-1)
                        loadProducts(productIds)
                    }
                }
                else {
                    //fail
                    progressBar.visibility = View.GONE
                    MainActivity.toast("An unknown error has occurred.", this@CollectionActivity)
                }
            }
            override fun onFailure(call: Call<ProductIdList>, t: Throwable) {
                MainActivity.toast("${t.javaClass.canonicalName}: ${t.message}", this@CollectionActivity)
            }
        })
    }

    fun loadProducts(ids: String) {
        val call = accessor.apiService.loadProducts(ids)

        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        progressBar.visibility = View.VISIBLE

        call.enqueue(object: Callback<ProductList> {
            override fun onResponse(call: Call<ProductList>, response: Response<ProductList>) {
                if (response.isSuccessful) {
                    val apiResponse: ProductList? = response.body()
                    if (apiResponse == null) {
                        //Api returned no data but received request
                        progressBar.visibility = View.GONE
                        MainActivity.toast("Failed to load API!", this@CollectionActivity)
                    }
                    //data is loaded successfully
                    else {
                        progressBar.visibility = View.GONE
                        for (i in apiResponse.products) {
                            products.add(i)
                        }
                        viewAdapter.notifyDataSetChanged()
                        findViewById<View>(R.id.shadow).bringToFront()
                    }
                }
                else {
                    //fail
                    progressBar.visibility = View.GONE
                    MainActivity.toast("An unknown error has occurred.", this@CollectionActivity)
                }
            }
            override fun onFailure(call: Call<ProductList>, t: Throwable) {
                MainActivity.toast("${t.javaClass.canonicalName}: ${t.message}", this@CollectionActivity)
            }
        })
    }

    companion object {
        const val COLLECTION = "collection"
    }
}
