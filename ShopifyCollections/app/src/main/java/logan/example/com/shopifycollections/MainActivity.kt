package logan.example.com.shopifycollections

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
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

        //sets up toolbar
        val toolbar = findViewById<android.support.v7.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        //sets up recyclerview
        viewManager = LinearLayoutManager(this)
        viewAdapter = CollectionAdapter(collections) {
            onCollectionClick(it) //sets onClick function for each item in the list
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
        //sends call to get collections from api
        val call = accessor.apiService.loadAllCollections()

        //starts loading icon
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        progressBar.visibility = View.VISIBLE

        //receives response from server
        call.enqueue(object: Callback<CollectionsList> {
            override fun onResponse(call: Call<CollectionsList>, response: Response<CollectionsList>) {
              if (response.isSuccessful) {
                  val apiResponse: CollectionsList? = response.body()
                  if (apiResponse == null) {
                      //Api returned no data but received request
                      progressBar.visibility = View.GONE
                      toast(getString(R.string.failed_access_api), this@MainActivity)
                  }
                  //data is loaded successfully
                  else {
                      progressBar.visibility = View.GONE

                      //adds collections into recyclerview
                      for (i in apiResponse.customCollections) {
                          collections.add(i)
                      }
                      viewAdapter.notifyDataSetChanged()
                      findViewById<View>(R.id.shadow).bringToFront()
                  }
              }
              else {
                  //server doesn't accept request
                  progressBar.visibility = View.GONE
                  toast(getString(R.string.unknown_error), this@MainActivity)
              }
            }
            //no internet
            override fun onFailure(call: Call<CollectionsList>, t: Throwable) {
                toast(getString(R.string.no_internet), this@MainActivity)
            }
        })
    }

    //starts new activity when collection is clicked
    private fun onCollectionClick(collection: CustomCollection) {
        //sets intent for new collection, passing the collection to the new activity
        val collectionIntent = Intent(this, CollectionActivity::class.java)
        collectionIntent.putExtra(CollectionActivity.COLLECTION, collection)
        startActivity(collectionIntent)
        //adds transition animation
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
    }

    companion object {
        //reduces boilerplate code when displaying a toast
        fun toast(s: String, context: Context) {
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
        }
    }
}
