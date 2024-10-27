package com.example.bakery.data

sealed class OrderStatus(val status: String){
    object Ordered:OrderStatus("Ordered")
    object Canceled:OrderStatus("Canceled")
    object Confirmed:OrderStatus("Confirmed")
    object Delivered:OrderStatus("Delivered")
}
fun getOrderStatus(status: String): OrderStatus {
    return when (status) {
        "Ordered" -> {
            OrderStatus.Ordered
        }
        "Canceled" -> {
            OrderStatus.Canceled
        }
        "Confirmed" -> {
            OrderStatus.Confirmed
        }
        else ->  OrderStatus.Delivered
    }
}