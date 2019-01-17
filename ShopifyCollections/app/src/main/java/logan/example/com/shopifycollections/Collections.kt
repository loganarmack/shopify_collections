package logan.example.com.shopifycollections

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class CollectionsList(@SerializedName("custom_collections")
                      var customCollections:  List<CustomCollection> = listOf())

@Parcelize
class CustomCollection(var id: Long,
                       var title: String,
                       var image: CollectionImage,
                       @SerializedName("body_html")
                       var body: String): Parcelable

@Parcelize
class CollectionImage(var width: Int,
                      var height: Int,
                      var src: String): Parcelable