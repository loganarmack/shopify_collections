package logan.example.com.shopifycollections

import com.google.gson.annotations.SerializedName

class ProductIdList(var collects: List<ProductId>) {
    class ProductId(@SerializedName("product_id") var productId: Long)
}

class ProductList(var products: List<Product>)

class Product(var id: Long,
              var title: String,
              @SerializedName("body_html")
              var body: String,
              var vendor: String,
              var tags: String,
              var varients: List<Variant>,
              var options: List<Option>,
              var image: CollectionImage)

class Variant(var title: String,
              var price: Double,
              var option1: String,
              var grams: Int,
              @SerializedName("inventory_quantity")
              var inventoryQuantity: Int)

class Option(var values: List<String>)
