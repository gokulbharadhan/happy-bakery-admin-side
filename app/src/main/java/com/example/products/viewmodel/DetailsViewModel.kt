package com.example.products.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewModelScope
import com.example.bakery.data.CartProduct
import com.example.bakery.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val firestore:FirebaseFirestore,

): ViewModel(){
    private val _addToCart= MutableStateFlow<Resource<CartProduct>>(Resource.Unspecified())
    val addToCart=_addToCart.asStateFlow()


    fun addUpdateProductInCart(cartProduct: CartProduct){
        viewModelScope.launch { _addToCart.emit(Resource.Loading()) }
        firestore.collection("user").get()
            .addOnSuccessListener {
                it.documents.let{
                    var productExists=false
                    for(docs in it){
                        val product=docs.toObject(CartProduct::class.java)
                        if(product?.type==cartProduct.type){
                            Log.e("increase","${product}")
                            Log.e("increase","${cartProduct}")
                            val documentId=docs.id
                            productExists=true
                            break
                        }
                    }
                    if (!productExists) {
                        Log.e("new","${cartProduct}")
                        // The cart product does not exist in Firebase, so you can add it.
                    }

                }
            }.addOnFailureListener{
                viewModelScope.launch { _addToCart.emit(Resource.Error(it.message.toString())) }
            }
    }


}