package com.hangloose.ui.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.hangloose.R
import com.hangloose.database.dbmodel.LikedRestaurant
import com.hangloose.database.dbmodel.SavedRestaurant
import com.hangloose.ui.adapter.SavedAdapter
import com.hangloose.ui.model.RestaurantData
import com.hangloose.utils.LIKED_RESTAURANT
import com.hangloose.utils.SAVED_RESTAURANT
import com.hangloose.viewmodel.LikedViewModel
import com.hangloose.viewmodel.SavedViewModel
import kotlinx.android.synthetic.main.activity_saved_restaurant.*

class SavedRestaurantActivity : AppCompatActivity() {

    private var mAdpater: SavedAdapter? = null
    private var mRestaurantData = ArrayList<RestaurantData>()
    private var isSaved: Boolean? = null
    private var isLiked: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_restaurant)
        isSaved = intent.getBooleanExtra(SAVED_RESTAURANT, false)
        isLiked = intent.getBooleanExtra(LIKED_RESTAURANT, false)

        if (isSaved!!) {
            tvSelectionHeading.text = resources.getString(R.string.saved_restaurant)
            getSavedData()
        }
        if (isLiked!!) {
            tvSelectionHeading.text = resources.getString(R.string.liked_restaurant)
            getLikedData()
        }
    }

    private fun getLikedData() {
        val likedViewModel = ViewModelProviders.of(this).get(LikedViewModel::class.java)
        likedViewModel.getLikedRestaurants().get().observe(this, Observer<List<LikedRestaurant>> { t ->
            if (t != null) {
                mRestaurantData.clear()
                t.mapTo(mRestaurantData) {
                    RestaurantData(
                        it.address,
                        it.createdAt,
                        it.discount,
                        it.id,
                        it.images,
                        it.latitude,
                        it.longitude,
                        it.name,
                        it.offer,
                        it.priceFortwo,
                        it.ratings,
                        it.restaurantType,
                        it.updatedAt,
                        it.distanceFromLocation,
                        it.about,
                        it.tags,
                        it.openCloseTime
                    )
                }
                setUpAdapter()
            }
        })
    }

    private fun getSavedData() {
        val savedViewModel = ViewModelProviders.of(this).get(SavedViewModel::class.java)
        savedViewModel.getSavedRestaurants().get().observe(this, Observer<List<SavedRestaurant>> { t ->
            if (t != null) {
                mRestaurantData.clear()
                t.mapTo(mRestaurantData) {
                    RestaurantData(
                        it.address,
                        it.createdAt,
                        it.discount,
                        it.id,
                        it.images,
                        it.latitude,
                        it.longitude,
                        it.name,
                        it.offer,
                        it.priceFortwo,
                        it.ratings,
                        it.restaurantType,
                        it.updatedAt,
                        it.distanceFromLocation,
                        it.about,
                        it.tags,
                        it.openCloseTime
                    )
                }
                setUpAdapter()
            }
        })
    }

    private fun setUpAdapter() {
        mAdpater = SavedAdapter(this, mRestaurantData)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvSavedRestaurant!!.layoutManager = layoutManager
        rvSavedRestaurant!!.adapter = mAdpater
    }
}
