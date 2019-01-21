package logan.example.com.shopifycollections

import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

const val BASE_URL = "https://shopicruit.myshopify.com/admin/"

//Requests that can be sent to the api
interface CollectionsInterface {
    //list of collections
    @GET("custom_collections.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6")
    fun loadAllCollections(): Call<CollectionsList>
    //specific collection
    @GET("collects.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6")
    fun loadCollectionProducts(@Query("collection_id") id: Long): Call<ProductIdList>
    //products
    @GET("products.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6")
    fun loadProducts(@Query("ids") id: String): Call<ProductList>
}

//initializes the standard api accessor that is reused throughout the code
class APIAccessor {
    var apiService: CollectionsInterface
    init {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(CollectionsInterface::class.java)
    }
}