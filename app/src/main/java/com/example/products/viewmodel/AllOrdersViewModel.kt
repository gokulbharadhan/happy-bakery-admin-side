package com.example.products.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bakery.data.CartProduct
import com.example.bakery.data.Order
import com.example.bakery.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllOrdersViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
): ViewModel() {

    private val _allOrders = MutableStateFlow<Resource<List<Order>>>(Resource.Unspecified())
    val allOrders = _allOrders.asStateFlow()
    private val _editOrders= MutableStateFlow<Resource<Order>>(Resource.Unspecified())
    val editORders=_editOrders.asStateFlow()
    private var orderDocumentSnapshot= emptyList<DocumentSnapshot>()

    init {
        getAllOrders()
    }

    fun getAllOrders(){
        viewModelScope.launch {
            _allOrders.emit(Resource.Loading())
        }
        firestore.collection("orders")
            .addSnapshotListener { value, error ->
                if(error!=null || value==null){
                    Log.e("eroor","error")
                }else{
                    orderDocumentSnapshot=value.documents
                    val orders=value.toObjects(Order::class.java)
                    viewModelScope.launch { _allOrders.emit(Resource.Success(orders))
                    }
                }
            }
    }
    fun updateOrders(order:Order,status:String){
        val index = allOrders.value.data?.indexOf(order)
        if (index != null && index != -1) {
            val documentId = orderDocumentSnapshot[index].id
            viewModelScope.launch { _editOrders.emit(Resource.Loading()) }
            firestore.runTransaction { transaction ->
                Log.e("document id","${documentId}")

                val collection =
                    firestore.collection("orders")
                val documentRef=collection.document(documentId)
                val document=transaction.get(documentRef)
                val productObject=document.toObject(Order::class.java)
                productObject?.let { orderState ->
                    val newProductObject = orderState.copy(orderStatus = status)
                    transaction.set(documentRef, newProductObject)
                }
            }.addOnSuccessListener {
                viewModelScope.launch { _editOrders.emit(Resource.Success(order)) }
            }.addOnFailureListener {e->
                viewModelScope.launch { _editOrders.emit(Resource.Error(e.message ?: "Unknown error")) }

            }


        }
        var documentId=""
        firestore.collection("user").document(order.userId).collection("orders").whereEqualTo("orderId",order.orderId)
            .get()
                .addOnSuccessListener {querySnapshot->
                    for (document in querySnapshot.documents) {
                         documentId = document.id
                        Log.e("User Order",documentId)

                    }
                    viewModelScope.launch { _editOrders.emit(Resource.Loading()) }
                    firestore.runTransaction { transaction ->

                        val collection =
                            firestore.collection("user").document(order.userId).collection("orders")
                        val documentRef=collection.document(documentId)
                        val document=transaction.get(documentRef)
                        val productObject=document.toObject(Order::class.java)
                        productObject?.let { orderState ->
                            val newProductObject = orderState.copy(orderStatus = status)
                            transaction.set(documentRef, newProductObject)
                        }
                    }.addOnSuccessListener {
                        viewModelScope.launch { _editOrders.emit(Resource.Success(order)) }
                    }.addOnFailureListener {e->
                        viewModelScope.launch { _editOrders.emit(Resource.Error(e.message ?: "Unknown error")) }

                    }

                }.addOnFailureListener { exception ->
                Log.e("User Order","something wrong")
            }



    }

}