package com.example.bakery.data

sealed class Category(val category:String){
    object Bread:Category("Bread")
    object Cakes:Category("Cakes")
    object Chips:Category("Chips")
    object Chocolate:Category("Chocolates")
    object Mixture:Category("Mixture")
    object IceCream:Category("IceCream")
    object CoolDrinks:Category("CoolDrinks")
    object Snacks:Category("Snacks")
    object Sweets:Category("Sweets")
}
