package com.hangloose.ui.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v4.app.FragmentActivity
import android.text.Html
import android.util.Log
import android.widget.*
import com.bumptech.glide.Glide
import com.hangloose.HanglooseApp.Companion.getDataHandler
import com.hangloose.R
import com.hangloose.database.dbmodel.LikedRestaurant
import com.hangloose.database.dbmodel.SavedRestaurant
import com.hangloose.listener.LikedInsertionListener
import com.hangloose.listener.SavedInsertionListener
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
    val likedListener: LikedInsertionListener,
    val savedListener: SavedInsertionListener
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

    @View(R.id.ibSave)
    private val save: CheckBox? = null

    @View(R.id.tvSwipe)
    private val textSwipe: TextView? = null

    @View(R.id.ibShare)
    private val btnShare: ImageButton? = null

    @Resolve
    private fun onResolved() {
        Glide.with(context).load(R.drawable.ic_restaurant_view).into(image!!)
        textRestaurantName!!.text = data.name
        textRestoAddress!!.text = data.address + " . " + data.restaurantType
        textRating!!.text = data.ratings
        textAbout!!.text = data.about
        textOffer!!.text = data.offer
        //save!!.setImageDrawable(context.resources.getDrawable(R.drawable.ic_unlike, null))
        val savedViewModel = ViewModelProviders.of(context as FragmentActivity).get(SavedViewModel::class.java)
        savedViewModel.getPersistedSavedRestaurant(data.id!!).get().observe(context, Observer<SavedRestaurant> { t ->
            t?.let {
                if (it != null) {
                    save!!.isChecked = true
                } else {
                    save!!.isChecked = false
                }
            }
        })
//        if (data.liked) {
//            save!!.setImageDrawable(context.resources.getDrawable(R.drawable.ic_like, null))
//        } else {
//            save!!.setImageDrawable(context.resources.getDrawable(R.drawable.ic_unlike, null))
//        }
//        save.setOnClickListener {
//            it.isSelected = !it.isSelected
//            if (it.isSelected) {
//                swipeView.doSwipe(true)
//                save.setImageDrawable(context.resources.getDrawable(R.drawable.ic_like, null))
//            } else {
//                save.setImageDrawable(context.resources.getDrawable(R.drawable.ic_unlike, null))
//            }
//        }
        //textOffer!!.text = data.offer
        btnBookTable!!.setOnClickListener {
            val intent = Intent(context, RestaurantDetailsActivity::class.java)
            intent.putExtra(EXTRA_RESTAURANT_DETAILS_DATA, data)
            context.startActivity(intent)
        }

        save!!.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(view: CompoundButton?, isCheck: Boolean) {
                if (isCheck) {
                    getDataHandler()!!.insertSavedRestaurantData(data, savedListener)
                } else {
                    getDataHandler()!!.deleteUnsavedRestaurant(data)
                }
            }

        })
        btnShare!!.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/html"
            intent.putExtra(Intent.EXTRA_SUBJECT, "Sharing Via")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.putExtra(
                    Intent.EXTRA_TEXT,
                    Html.fromHtml(
                        "<a href= \"app://hangloose.com/restaurant?id=${data.id}\">app://hangloose.com/restaurant?id=${data.id}</a>",
                        Html.FROM_HTML_MODE_LEGACY
                    )
                )
//                intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(context.resources.getString(R.string.readyandroid)))
            } else {
                intent.putExtra(
                    Intent.EXTRA_TEXT,
                    Html.fromHtml("<a href= \"app://hangloose.com/restaurant?id=${data.id}\">app://hangloose.com/restaurant?id=${data.id}</a>")
                )
            }
            context.startActivity(intent)
        }
    }

    @SwipeOut
    private fun onSwipedOut() {
        Log.d("EVENT", "onSwipedOut")
        swipeView.addView(this)
        getDataHandler()!!.deleteUnlikedRestaurant(data)
    }

    @SwipeCancelState
    private fun onSwipeCancelState() {
        Log.d("EVENT", "onSwipeCancelState")
        textSwipe!!.visibility = android.view.View.GONE
    }

    @SwipeIn
    private fun onSwipeIn() {
        Log.d("EVENT", "onSwipedIn")
        getDataHandler()!!.insertLikedRestaurantData(data, likedListener)
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