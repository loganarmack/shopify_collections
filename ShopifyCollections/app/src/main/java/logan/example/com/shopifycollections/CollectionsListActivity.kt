package logan.example.com.shopifycollections

import android.app.ActionBar
import android.graphics.Movie
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: CollectionAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var collections = ArrayList<CustomCollection>()

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.custom_actionbar)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collections_list)

        viewManager = LinearLayoutManager(this)
        viewAdapter = CollectionAdapter(collections) {
            onCollectionClick(it)
        }

        recyclerView = findViewById<RecyclerView>(R.id.collection_recycler).apply {
            layoutManager = viewManager
            addItemDecoration(SimpleDividerItemDecoration(this@MainActivity))
            itemAnimator = DefaultItemAnimator()
            adapter = viewAdapter
        }
        loadCollections()
    }

    private fun loadCollections() {
        val accessor = APIAccessor()
        val call = accessor.apiService.loadAllCollections()

        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        progressBar.visibility = View.VISIBLE

        call.enqueue(object: Callback<CollectionsList> {
            override fun onResponse(call: Call<CollectionsList>, response: Response<CollectionsList>) {
              if (response.isSuccessful) {
                  val apiResponse: CollectionsList? = response.body()
                  if (apiResponse == null) {
                      //Api returned no data but received request
                      progressBar.visibility = View.GONE
                      Toast.makeText(this@MainActivity, "Failed to load API!", Toast.LENGTH_SHORT).show()
                  }
                  //data is loaded successfully
                  else {
                      progressBar.visibility = View.GONE
                      for (i in apiResponse.customCollections) {
                          collections.add(i)
                      }
                      viewAdapter.notifyDataSetChanged()
                  }
              }
              else {
                  //fail
                  progressBar.visibility = View.GONE
                  Toast.makeText(this@MainActivity, "Unknown error", Toast.LENGTH_SHORT).show()
              }
            }
            override fun onFailure(call: Call<CollectionsList>, t: Throwable) {
                Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun onCollectionClick(collection: CustomCollection) {
        toast("${collection.title} Clicked")

    }

    private fun toast(s: String) {
        Toast.makeText(this@MainActivity, s, Toast.LENGTH_SHORT).show()
    }
}
