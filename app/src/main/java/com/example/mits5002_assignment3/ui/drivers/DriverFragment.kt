package com.example.mits5002_assignment3.ui.drivers

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.mits5002_assignment3.R
import com.example.mits5002_assignment3.data.model.dao.GenericDAO
import com.example.mits5002_assignment3.data.model.dao.UserDAO
import com.example.mits5002_assignment3.database.RideDatabase
import com.example.mits5002_assignment3.ui.drivers.placeholder.PlaceholderContent

/**
 * A fragment representing a list of Items.
 */
class DriverFragment : Fragment() {

    private var columnCount = 1
    private lateinit var dbInstance: RideDatabase
    private lateinit var userDao: UserDAO
    private lateinit var genericDao: GenericDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }

        dbInstance = RideDatabase.getInstance(requireContext())
        userDao = dbInstance.userDao
        genericDao = dbInstance.genericDao
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_driver_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MyDriverRecyclerViewAdapter(userDao, genericDao) {
                    Toast.makeText(context, "Preparing for your trip...", Toast.LENGTH_LONG).show()
                }
            }
        }
        return view
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            DriverFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}