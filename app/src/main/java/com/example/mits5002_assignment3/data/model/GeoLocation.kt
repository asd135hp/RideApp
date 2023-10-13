package com.example.mits5002_assignment3.data.model

import com.google.android.gms.maps.model.LatLng

data class GeoLocation(var lat: Double, var long: Double) {
    override fun toString(): String{ return "$lat,$long" }

    fun toLatLng(): LatLng { return LatLng(lat, long) }
}
