package com.example.products.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bakery.data.CartProduct
import com.example.bakery.data.Product
import com.example.bakery.data.User
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
class AllUserViewModel @Inject constructor(
    private val firestore:FirebaseFirestore,
):ViewModel() {
    private val _Products = MutableStateFlow<Resource<List<User>>>(Resource.Unspecified())
    val Products=_Products.asStateFlow()


    init {
        getCartProduct()
    }
     fun getCartProduct(){

        viewModelScope.launch { _Products.emit(Resource.Loading()) }
         Log.e("error","somthing wrong")
        firestore.collection("user")
            .addSnapshotListener{ value,error->
                if(error!=null || value==null){
                    Log.e("error","${error}")
                    viewModelScope.launch { _Products.emit(Resource.Error(error?.message.toString())) }
                }else{
                    val user=value.toObjects(User::class.java)
                    viewModelScope.launch { _Products.emit(Resource.Success(user)) }
                }

            }
    }

}