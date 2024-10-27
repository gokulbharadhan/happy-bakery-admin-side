package com.example.bakery.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import androidx.lifecycle.ViewModelProvider

import com.example.bakery.util.Constent.INTRODUCTION_SP
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFirebaseFirestoreDatabase()= Firebase.firestore



    @Provides
    fun provideIntroductionSP(
        application:Application
    )=application.getSharedPreferences(INTRODUCTION_SP,MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideStorage()=FirebaseStorage.getInstance().reference

}