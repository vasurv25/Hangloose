package com.hangloose.ui.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.hangloose.HanglooseApp.Companion.getDataHandler
import com.hangloose.R
import com.hangloose.listener.RecordInsertionListener
import com.hangloose.ui.model.RestaurantData
import com.hangloose.utils.EXTRA_RESTAURANT_DETAILS_DATA
import com.hangloose.viewmodel.SavedViewModel
import com.mindorks.placeholderview.SwipePlaceHolderView
import com.mindorks.placeholderview.annotations.Layout
import com.mindorks.placeholderview.annotations.Resolve
import com.mindorks.placeholderview.annotations.View
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState
import com.mindorks.placeholderview.annotations.swipe.SwipeIn
import com.mindorks.placeholderview.annotations.swipe.SwipeInState
import com.mindorks.placeholderview.annotations.swipe.SwipeOut
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState

@Layout(R.layout.swipeable_card_view)
class SwipeableCardView(
    val context: Context,
    val data: RestaurantData,
    val swipeView: SwipePlaceHolderView,
    val listener: RecordInsertionListener
) {

    @View(R.id.ivRestaurant)
    private val image: ImageView? = null

    @View(R.id.tvRestaurantName)
    private val textRestaurantName: TextView? = null

    @View(R.id.tvRestoAddress)
    private val textRestoAddress: TextView? = null

    @View(R.id.tvRating)
    private val textRating: TextView? = null

    @View(R.id.tvOffer)
    private val textOffer: TextView? = null

    @View(R.id.btBookTable)
    private val btnBookTable: Button? = null

    @View(R.id.tvRestoDesp)
    private val textAbout: TextView? = null

    @View(R.id.ibLike)
    private val like: ImageButton? = null

    @View(R.id.tvSwipe)
    private val textSwipe: TextView? = null

    @Resolve
    private fun onResolved() {
        Glide.with(context).load(R.drawable.ic_restaurant_view).into(image!!)
        textRestaurantName!!.text = data.name
        textRestoAddress!!.text = data.address + " . " + data.restaurantType
        textRating!!.text = data.ratings
        textAbout!!.text = data.about
        textOffer!!.text = data.offer
        //like!!.setImageDrawable(context.resources.getDrawable(R.drawable.ic_unlike, null))
        val savedViewModel = ViewModelProviders.of(context as FragmentActivity).get(SavedViewModel::class.java)
        savedViewModel.getPersistedSavedRestaurant(data.id!!).get().observe(context, Observer<Int> { t ->
            t?.let {
                if (it == 1) {
                    like!!.setImageDrawable(context.resources.getDrawable(R.drawable.ic_like, null))
                } else {
                    like!!.setImageDrawable(context.resources.getDrawable(R.drawable.ic_unlike, null))
                }
            }
        })
//        if (data.saved) {
//            like!!.setImageDrawable(context.resources.getDrawable(R.drawable.ic_like, null))
//        } else {
//            like!!.setImageDrawable(context.resources.getDrawable(R.drawable.ic_unlike, null))
//        }
//        like.setOnClickListener {
//            it.isSelected = !it.isSelected
//            if (it.isSelected) {
//                swipeView.doSwipe(true)
//                like.setImageDrawable(context.resources.getDrawable(R.drawable.ic_like, null))
//            } else {
//                like.setImageDrawable(context.resources.getDrawable(R.drawable.ic_unlike, null))
//            }
//        }
        //textOffer!!.text = data.offer
        btnBookTable!!.setOnClickListener {
            val intent = Intent(context, RestaurantDetailsActivity::class.java)
            intent.putExtra(EXTRA_RESTAURANT_DETAILS_DATA, data)
            context.startActivity(intent)
        }
    }

    @SwipeOut
    private fun onSwipedOut() {
        Log.d("EVENT", "onSwipedOut")
        swipeView.addView(this)
        data.saved = false
        getDataHandler()!!.deleteUnsavedRestaurant(data)
    }

    @SwipeCancelState
    private fun onSwipeCancelState() {
        Log.d("EVENT", "onSwipeCancelState")
        textSwipe!!.visibility = android.view.View.GONE
    }

    @SwipeIn
    private fun onSwipeIn() {
        Log.d("EVENT", "onSwipedIn")
        data.saved = true
        getDataHandler()!!.insertRestaurantData(data, listener)
    }

    @SwipeInState
    private fun onSwipeInState() {
        Log.d("EVENT", "onSwipeInState")
        textSwipe!!.visibility = android.view.View.VISIBLE
        textSwipe.text = context.getString(R.string.liked)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textSwipe.setTextColor(context.resources.getColor(R.color.colorGreen, null))
        } else {
            textSwipe.setTextColor(context.resources.getColor(R.color.colorGreen))
        }
    }

    @SwipeOutState
    private fun onSwipeOutState() {
        Log.d("EVENT", "onSwipeOutState")
        textSwipe!!.visibility = android.view.View.VISIBLE
        textSwipe.text = context.getString(R.string.unliked)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textSwipe.setTextColor(context.resources.getColor(R.color.red, null))
        } else {
            textSwipe.setTextColor(context.resources.getColor(R.color.red))
        }
    }
}