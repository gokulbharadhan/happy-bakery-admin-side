package com.example.bakery.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random.Default.nextLong

@Parcelize
data class Order(
    val orderStatus:String="",
    val totalPrice:Float=0f,
    val date:String="",
    val time:String="",
    val products:List<CartProduct> = emptyList(),
    val address:Address=Address(),
    val orderdate:String= SimpleDateFormat("yyyy-MM-dd", Locale.UK).format(Date()),
    val orderId:Long=nextLong(0,100_000_000_000)+totalPrice.toLong(),
    val userId:String=""
):Parcelable

