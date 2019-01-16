package logan.example.com.shopifycollections

import com.google.gson.annotations.SerializedName

class CollectionsList {
    @SerializedName("custom_collections")
    var customCollections:  List<CustomCollection> = listOf()
}