package com.example.bakery.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id:String,
    val name:String,
    val category:String,
    val specialization: String,
    val price:Float,
    val price1:Float?=null,
    val offerPercentage:Float?=null,
    val description:String?=null,
    val details: String,
    val images:List<String>
):Parcelable{
    constructor():this("0","","","",0f,price1=null,offerPercentage=null,description=null,
        "",images= emptyList())
}