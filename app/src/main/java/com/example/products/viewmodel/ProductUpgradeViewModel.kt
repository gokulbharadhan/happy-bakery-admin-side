package com.example.products.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bakery.data.Address
import com.example.bakery.data.Product
import com.example.bakery.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class ProductUpgradeViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
): ViewModel() {
    private val _updateSingle= MutableStateFlow<Resource<Product>>(Resource.Unspecified())
    val updateSingle:StateFlow<Resource<Product>> = _updateSingle
    private var productDocument = emptyList<DocumentSnapshot>()

    private val _Products = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val Products=_Products.asStateFlow()



    init {
        getCartProduct()
    }
    fun getCartProduct() {
        viewModelScope.launch { _Products.emit(Resource.Loading()) }
        firestore.collection("Products")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    viewModelScope.launch { _Products.emit(Resource.Error(error.message ?: "Unknown error")) }
                } else {
                    value?.let { snapshot ->
                        productDocument = snapshot.documents // Update productDocument here
                        Log.e("getproduct", "updated recycle")
                        val products = snapshot.toObjects(Product::class.java)
                        viewModelScope.launch { _Products.emit(Resource.Success(products)) }
                    } ?: kotlin.run {
                        Log.e("getproduct", "Snapshot is null")
                        viewModelScope.launch { _Products.emit(Resource.Error("Snapshot is null")) }
                    }
                }
            }
    }


}

