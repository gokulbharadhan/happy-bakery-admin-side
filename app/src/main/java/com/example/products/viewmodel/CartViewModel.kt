package com.example.products.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bakery.data.CartProduct
import com.example.bakery.data.Product
import com.example.bakery.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class CartViewModel @Inject constructor(
    private val firestore:FirebaseFirestore,
):ViewModel() {
    private val _Products = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val Products=_Products.asStateFlow()

    private val _updateSingle= MutableStateFlow<Resource<Product>>(Resource.Unspecified())
    val updateSingle: StateFlow<Resource<Product>> = _updateSingle
    private var productDocument = emptyList<DocumentSnapshot>()

    init {
        getCartProduct()
    }
     fun getCartProduct(){

        viewModelScope.launch { _Products.emit(Resource.Loading()) }
        firestore.collection("Products")
            .addSnapshotListener{ value,error->
                if(error!=null || value==null){
                    viewModelScope.launch { _Products.emit(Resource.Error(error?.message.toString())) }
                }else{
                    productDocument = value.documents
                    Log.e("getproduct","${productDocument}}")
                    val productes=value.toObjects(Product::class.java)
                    viewModelScope.launch { _Products.emit(Resource.Success(productes)) }
                }

            }
    }
    fun productUpdate(product: Product, product1: Product) {
        val index = Products.value.data?.indexOf(product1)
        if (index != null && index != -1) {
            val documentId = productDocument[index].id
            viewModelScope.launch { _updateSingle.emit(Resource.Loading()) }
            firestore.runTransaction { transaction ->
                val collection =
                    firestore.collection("Products")
                val documentRef=collection.document(documentId)
                transaction.set(documentRef, product)
            }.addOnSuccessListener {
                viewModelScope.launch { _updateSingle.emit(Resource.Success(product)) }
            }.addOnFailureListener {e->
                viewModelScope.launch { _updateSingle.emit(Resource.Error(e.message ?: "Unknown error")) }

            }
        }
    }
    fun deleteAddress(product: Product) {
        Log.e("productid","document id")
        val index = Products.value.data?.indexOf(product)
        Log.e("index","index ${index}")
        if (index != null && index != -1) {
            val documentId = productDocument[index].id
            Log.e("productid","document id ${documentId}")
            firestore.collection("Products").document(documentId).delete()
        }
    }


}