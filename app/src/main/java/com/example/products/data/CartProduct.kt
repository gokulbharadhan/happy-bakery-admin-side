package com.example.bakery.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartProduct(
    val product:Product,
    var quantity:Double,
    val type:String?=""
):Parcelable{
    constructor():this(Product(),1.0,null)
}
