package com.example.mits5002_assignment3.ui.drivers

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.mits5002_assignment3.data.model.Driver
import com.example.mits5002_assignment3.data.model.dao.GenericDAO
import com.example.mits5002_assignment3.data.model.dao.UserDAO

import com.example.mits5002_assignment3.ui.drivers.placeholder.PlaceholderContent.PlaceholderItem
import com.example.mits5002_assignment3.databinding.FragmentDriverItemBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyDriverRecyclerViewAdapter(
    private val userDao: UserDAO,
    private val genericDAO: GenericDAO,
    private val onClickListener: OnClickListener
) : RecyclerView.Adapter<MyDriverRecyclerViewAdapter.ViewHolder>() {

    private var driverList: List<Driver> = userDao.getDrivers()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        driverList = userDao.getDrivers()
        return ViewHolder(
            FragmentDriverItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val driver = driverList[position]
        val vehicle = runBlocking(Dispatchers.IO) { genericDAO.getVehicle(driver.vehicleId) }
        val user = runBlocking(Dispatchers.IO) { userDao.getUser(driver.userId) }

        val fullName = "${user.firstName} ${user.lastName}"
        val description = "${driver.rating} stars. ${driver.distance} away"
        val regNum = vehicle.registrationNumber

        holder.nameView.text = fullName
        holder.ratingView.text = description
        holder.regNumberView.text = regNum
    }

    override fun getItemCount(): Int = driverList.size

    inner class ViewHolder(binding: FragmentDriverItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val nameView: TextView = binding.driverName
        val ratingView: TextView = binding.rating
        val regNumberView: TextView = binding.registrationNumber
        val cardView: CardView = binding.cardView

        init {
            cardView.setOnClickListener(onClickListener)
        }


        override fun toString(): String {
            return super.toString() + " '" + nameView.text + "'"
        }
    }

}