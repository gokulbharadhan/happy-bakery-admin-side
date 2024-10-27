package com.example.bakery.util

sealed class RegisterValidation(){
    object Success:RegisterValidation()
    data class Failed(val message:String):RegisterValidation()
}
data class RegisterFieldState(
    val email:RegisterValidation,
    val password:RegisterValidation,
    val firstName:RegisterValidation,
    val SecondName:RegisterValidation
)
