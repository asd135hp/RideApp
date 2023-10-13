package com.example.mits5002_assignment3.ui.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mits5002_assignment3.data.model.GeoLocation

class MapsViewModel: ViewModel() {
    private val mutableOrigin = MutableLiveData<GeoLocation>()
    private val mutableDestination = MutableLiveData<GeoLocation>()

    val origin: LiveData<GeoLocation> get() = mutableOrigin
    val destination: LiveData<GeoLocation> get() = mutableDestination

    fun setOrigin(origin: GeoLocation){
        mutableOrigin.value = origin
        Log.i("abc", "setOrigin")
    }
    fun setOrigin(lat: Double, lng: Double){
        mutableOrigin.value = GeoLocation(lat, lng)
        Log.i("abc", "setOrigin")
    }

    fun setDestination(dest: GeoLocation){
        mutableDestination.value = dest
        Log.i("abc", "setDest")
    }
    fun setDestination(lat: Double, lng: Double){
        mutableDestination.value = GeoLocation(lat, lng)
        Log.i("abc", "setDest")
    }
}