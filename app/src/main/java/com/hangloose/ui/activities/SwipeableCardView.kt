package com.hangloose.ui.activities

import android.content.Context
import android.content.Intent
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

    @Resolve
    private fun onResolved() {
        Glide.with(context).load(R.drawable.ic_restaurant_view).into(image!!)
        textRestaurantName!!.text = data.name
        textRestoAddress!!.text = data.address + " . " + data.restaurantType
        textRating!!.text = data.ratings
        textAbout!!.text = data.about
        textOffer!!.text = data.offer
        like!!.setImageDrawable(context.resources.getDrawable(R.drawable.ic_unlike, null))
        like.setOnClickListener {
            it.isSelected = !it.isSelected
            if (it.isSelected) {
                swipeView.doSwipe(true)
                like.setImageDrawable(context.resources.getDrawable(R.drawable.ic_like, null))
            } else {
                like.setImageDrawable(context.resources.getDrawable(R.drawable.ic_unlike, null))
            }
        }
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
    }

    @SwipeCancelState
    private fun onSwipeCancelState() {
        Log.d("EVENT", "onSwipeCancelState")
    }

    @SwipeIn
    private fun onSwipeIn() {
        Log.d("EVENT", "onSwipedIn")
        getDataHandler()!!.insertRestaurantData(data, listener)
    }

    @SwipeInState
    private fun onSwipeInState() {
        Log.d("EVENT", "onSwipeInState")
    }

    @SwipeOutState
    private fun onSwipeOutState() {
        Log.d("EVENT", "onSwipeOutState")
    }
}