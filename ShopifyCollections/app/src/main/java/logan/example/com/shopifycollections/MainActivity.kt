package logan.example.com.shopifycollections

import android.app.ActionBar
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import android.widget.Toolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: CollectionAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var collections = ArrayList<CustomCollection>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<android.support.v7.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

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
                      toast("Failed to load API!", this@MainActivity)
                  }
                  //data is loaded successfully
                  else {
                      progressBar.visibility = View.GONE
                      for (i in apiResponse.customCollections) {
                          collections.add(i)
                      }
                      viewAdapter.notifyDataSetChanged()
                      findViewById<View>(R.id.shadow).bringToFront()
                  }
              }
              else {
                  //fail
                  progressBar.visibility = View.GONE
                  toast("An unknown error has occurred.", this@MainActivity)
              }
            }
            override fun onFailure(call: Call<CollectionsList>, t: Throwable) {
                toast("${t.javaClass.canonicalName}: ${t.message}", this@MainActivity)
            }
        })
    }

    private fun onCollectionClick(collection: CustomCollection) {
        val collectionIntent = Intent(this, CollectionActivity::class.java)
        collectionIntent.putExtra(CollectionActivity.COLLECTION, collection)
        startActivity(collectionIntent)
    }

    companion object {
        fun toast(s: String, context: Context) {
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
        }
    }
}
