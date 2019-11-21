package com.hangloose.ui.activities

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.*
import com.bumptech.glide.Glide
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.hangloose.HanglooseApp.Companion.getDataHandler
import com.hangloose.R
import com.hangloose.database.dbmodel.SavedRestaurant
import com.hangloose.listener.LikedInsertionListener
import com.hangloose.listener.SavedInsertionListener
import com.hangloose.model.RestaurantConsumerRating
import com.hangloose.ui.model.RestaurantData
import com.hangloose.utils.EXTRA_RESTAURANT_DETAILS_DATA
import com.hangloose.utils.KEY_CONSUMER_ID
import com.hangloose.utils.LIKE_DISLIKE
import com.hangloose.utils.PreferenceHelper
import com.hangloose.viewmodel.LikedViewModel
import com.hangloose.viewmodel.SavedViewModel
import com.mindorks.placeholderview.SwipePlaceHolderView
import com.mindorks.placeholderview.annotations.Layout
import com.mindorks.placeholderview.annotations.Resolve
import com.mindorks.placeholderview.annotations.View
import com.mindorks.placeholderview.annotations.swipe.*
import java.util.*

@Layout(R.layout.swipeable_card_view)
class SwipeableCardView(
    val context: Context,
    val data: RestaurantData,
    private val swipeView: SwipePlaceHolderView,
    val likedListener: LikedInsertionListener,
    val savedListener: SavedInsertionListener,
    val mLikedViewModel: LikedViewModel
) {

    @View(R.id.ivRestaurant)
    private val image: ImageView? = null

    @View(R.id.tvRestaurantName)
    private val textRestaurantName: TextView? = null

    @View(R.id.tvRestoAddress)
    private val textRestoAddress: TextView? = null

    @View(R.id.tvRating)
    private val textRating: TextView? = null

    @View(R.id.btBookTable)
    private val btnBookTable: Button? = null

    @View(R.id.tvRestoDesp)
    private val textAbout: TextView? = null

    @View(R.id.ibSave)
    private val save: CheckBox? = null

    @View(R.id.tvLikeSwipe)
    private val textLikeSwipe: TextView? = null

    @View(R.id.tvUnLikeSwipe)
    private val textUnLikeSwipe: TextView? = null

    @View(R.id.swipeLayout)
    private val swipeLayout: FrameLayout? = null

    @View(R.id.ibShare)
    private val btnShare: ImageButton? = null

    private var likeCount = 0
    private var unLikeCount = 0

    @Resolve
    private fun onResolved() {
        Glide.with(context).load(data.logo).into(image!!)
        textRestaurantName!!.text = data.name
        textRestoAddress!!.text = data.address + " . " + data.restaurantType
        textRating!!.text = data.ratings
        textAbout!!.text = data.about
        //save!!.setImageDrawable(context.resources.getDrawable(R.drawable.ic_unlike, null))
        val savedViewModel = ViewModelProviders.of(context as FragmentActivity).get(SavedViewModel::class.java)
        savedViewModel.getPersistedSavedRestaurant(data.id!!).get().observe(context, Observer<SavedRestaurant> { t ->
            t?.let {
                save!!.isChecked = true
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

        save!!.setOnCheckedChangeListener { view, isCheck ->
            if (isCheck) {
                getDataHandler()!!.insertSavedRestaurantData(data, savedListener)
            } else {
                getDataHandler()!!.deleteUnsavedRestaurant(data)
            }
        }
        btnShare!!.setOnClickListener {
            Log.d("SwipeableCardView", "getDynamicLink")
            FirebaseDynamicLinks.getInstance()
                .getDynamicLink(context.intent)
                .addOnSuccessListener { pendingDynamicLinkData ->
                    // Get deep link from result (may be null if no link is found)
                    var deepLink: Uri? = null
                    if (pendingDynamicLinkData != null) {
                        deepLink = pendingDynamicLinkData.link
                        Log.d("SwipeableCardView", "getDynamicLink:onSuccess$deepLink")
                    }
                }
                .addOnFailureListener { e -> Log.d("SwipeableCardView", "getDynamicLink:onFailure", e) }

            val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://hangloose.page.link?hotelID=${data.id}"))
                .setDomainUriPrefix("https://hangloose.page.link")
                // Open links with this app on Android
                //.setAndroidParameters(DynamicLink.AndroidParameters.Builder().build())
                .buildDynamicLink()

            val dynamicLinkUri = dynamicLink.uri
            Log.d("SwipeableCardView", "getDynamicLink:onSuccess$dynamicLinkUri")
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            val url = dynamicLinkUri
            intent.putExtra(Intent.EXTRA_SUBJECT, "Sharing Via")
            intent.putExtra(Intent.EXTRA_TEXT, "Check Out: $url ")
            context.startActivity(intent)
        }
    }

    @SwipeOut
    private fun onSwipedOut() {
        Log.d("EVENT", "onSwipedOut")
        swipeView.addView(this)
//        getDataHandler()!!.deleteLikeUnlikeRestaurant(data)
        val mPreference = PreferenceHelper.defaultPrefs(context)
        val consumerID = mPreference.getString(KEY_CONSUMER_ID, null)
        if (consumerID != null) {
            mLikedViewModel.syncLikeUnlikeRestaurants(
                data,
                RestaurantConsumerRating(consumerID, LIKE_DISLIKE.DISLIKE.name, data.id),
                likedListener
            )
        }
    }

    @SwipeCancelState
    private fun onSwipeCancelState() {
        Log.d("EVENT", "onSwipeCancelState")
        textLikeSwipe!!.visibility = android.view.View.GONE
        textUnLikeSwipe!!.visibility = android.view.View.GONE
        swipeLayout!!.visibility = android.view.View.GONE
        textLikeSwipe.clearAnimation()
        textUnLikeSwipe.clearAnimation()
        likeCount = 0
        unLikeCount = 0
    }

    @SwipeIn
    private fun onSwipeIn() {
        Log.d("EVENT", "onSwipedIn")
//        getDataHandler()!!.insertLikeUnlikeRestaurantData(data, likedListener)
        val mPreference = PreferenceHelper.defaultPrefs(context)
        val consumerID = mPreference.getString(KEY_CONSUMER_ID, null)
        if (consumerID != null) {
            mLikedViewModel.syncLikeUnlikeRestaurants(
                data,
                RestaurantConsumerRating(consumerID, LIKE_DISLIKE.LIKE.name, data.id),
                likedListener
            )
        }
    }

    @SwipeInState
    private fun onSwipeInState() {
        Log.d("EVENT", "onSwipeInState")
        textLikeSwipe!!.visibility = android.view.View.VISIBLE
        textUnLikeSwipe!!.visibility = android.view.View.GONE
        swipeLayout!!.visibility = android.view.View.VISIBLE
        if (likeCount == 0) {
            textLikeSwipe.animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        }
        likeCount = 1
    }

    @SwipeOutState
    private fun onSwipeOutState() {
        Log.d("EVENT", "onSwipeOutState")
        textUnLikeSwipe!!.visibility = android.view.View.VISIBLE
        textLikeSwipe!!.visibility = android.view.View.GONE
        swipeLayout!!.visibility = android.view.View.VISIBLE
        if (unLikeCount == 0) {
            textUnLikeSwipe.animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        }
        unLikeCount = 1
    }
}