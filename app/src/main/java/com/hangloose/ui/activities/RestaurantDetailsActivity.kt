package com.hangloose.ui.activities

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.hangloose.R
import com.hangloose.model.RestaurantList
import com.hangloose.ui.adapter.AmbienceImageAdapter
import com.hangloose.ui.model.RestaurantData
import com.hangloose.utils.EXTRA_RESTAURANT_DETAILS_DATA
import com.hangloose.viewmodel.SelectionViewModel
import kotlinx.android.synthetic.main.activity_restaurant_details.*
import kotlinx.android.synthetic.main.restaurant_menu_item.view.*
import kotlinx.android.synthetic.main.restaurant_tag_item.view.*
import retrofit2.Response
import com.hangloose.databinding.ActivityRestaurantDetailsBinding
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class RestaurantDetailsActivity : BaseActivity() {
    override fun init() {

    }

    private val REQUEST_CALL_PHONE = 11
    private val TAG = "RestaurantDetails"
    private var restaurantData: RestaurantData? = null
    private var mMenuRecyclerViewAdapter: MenuRecyclerViewAdapter? = null
    private var mTagRecyclerViewAdapter: TagRecyclerViewAdapter? = null
    private var mActivityRestaurantDetailsBinding: ActivityRestaurantDetailsBinding? = null
    private lateinit var mSelectionViewModel: SelectionViewModel

    private var mAmbienceAdapter: AmbienceImageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityRestaurantDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_restaurant_details)
        mSelectionViewModel = ViewModelProviders.of(this).get(SelectionViewModel::class.java)
        mActivityRestaurantDetailsBinding!!.selectionViewModel = mSelectionViewModel

        val data = intent.data
        if (data == null) {
            layoutScroll.visibility = View.VISIBLE
            restaurantData = intent.getParcelableExtra(EXTRA_RESTAURANT_DETAILS_DATA)
            Log.i(TAG, restaurantData.toString())
            setUpViews()
        } else {
            val id = data.getQueryParameter("id")
            Log.i("Anjani", id)
            mSelectionViewModel!!.restaurantDetailsByIdApiRequest(id)
            mSelectionViewModel!!.getRestaurantById().observe(this, Observer<Response<RestaurantList>> { t ->
                val data = t!!.body()
                var logo: String? = null
                val ambienceList: ArrayList<String>? = ArrayList()
                var menuList: ArrayList<String>? = ArrayList()
                (0 until data!!.documents!!.size).forEach { i->
                    when {
                        data.documents!![i].documentType.equals("LOGO") -> logo = data.documents!![i].location
                        data.documents!![i].documentType.equals("AMBIENCE") -> ambienceList!!.add(data.documents!![i].location!!)
                        else -> menuList!!.add(data.documents!![i].location!!)
                    }
                }
                restaurantData = RestaurantData(
                    data.address,
                    data.createdAt,
                    data.discount,
                    data.id,
                    data.images,
                    data.latitude,
                    data.longitude,
                    data.name,
                    data.offer,
                    data.priceFortwo,
                    data.ratings,
                    data.restaurantType,
                    data.updatedAt,
                    data.distanceFromLocation,
                    data.about,
                    data.tags,
                    data.openCloseTime,
                    data.number,
                    logo,
                    ambienceList,
                    menuList
                )
                layoutScroll.visibility = View.VISIBLE
                setUpViews()
            })
        }
    }

    private fun setUpViews() {
        if (restaurantData != null) {
            restaurantData!!.ambienceList?.let { setUpAmbienceViewAdapter(restaurantData!!.ambienceList) }
            textName.text = restaurantData!!.name
            textPlace.text = restaurantData!!.address
            textRatingValue.text = restaurantData!!.ratings
            expand_text_view.text = restaurantData!!.about
            textOpenCloseTime.text = restaurantData!!.openCloseTime
            textChargesValue.text = restaurantData!!.priceFortwo
            textContact.text = restaurantData!!.number
            if (restaurantData!!.restaurantType!! == "VEGETARIAN") {
                textNonVeg.visibility = View.GONE
            } else {
                textNonVeg.visibility = View.VISIBLE
            }
            textOfferBill.text = restaurantData!!.offer
            textAddress.text = restaurantData!!.address
            btnCall.setOnClickListener {
                checkCallPermission()
            }
            restaurantData!!.menuList?.let { initMenuRecyclerView(restaurantData!!.menuList) }
            restaurantData!!.tags?.let { initTagRecyclerView(restaurantData!!.tags) }
        }
    }

    private fun setUpAmbienceViewAdapter(ambienceList: List<String>?) {
        mAmbienceAdapter = AmbienceImageAdapter(this, ambienceList as ArrayList<String>)
        vpAmbience.adapter = mAmbienceAdapter
//        vpAmbience.currentItem = 0
        ambienceIndicator.setViewPager(vpAmbience)
        vpAmbience.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
            }
        })
    }

    private fun checkCallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.CALL_PHONE),
                    REQUEST_CALL_PHONE
                )

            } else {
                val callIntent = Intent(Intent.ACTION_CALL)
                callIntent.data = Uri.parse("tel:" + restaurantData!!.number)//change the number.
                startActivity(callIntent)
            }
        } else {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:" + restaurantData!!.number)//change the number.
            startActivity(callIntent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CALL_PHONE -> {
                checkCallPermission()
            }
        }
    }

    private fun initMenuRecyclerView(menuList: List<String>?) {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvMenu.layoutManager = layoutManager
        mMenuRecyclerViewAdapter = menuList?.let { MenuRecyclerViewAdapter(this, it) }
        rvMenu.adapter = mMenuRecyclerViewAdapter
    }

    private fun initTagRecyclerView(tagList: List<String>?) {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvTags.layoutManager = layoutManager
        mTagRecyclerViewAdapter = tagList?.let { TagRecyclerViewAdapter(it) }
        rvTags.adapter = mTagRecyclerViewAdapter
    }

    private class MenuRecyclerViewAdapter(val context: Context, val urlList: List<String>) :
        RecyclerView.Adapter<MenuRecyclerViewAdapter.MenuViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.restaurant_menu_item, parent, false)
            return MenuViewHolder(view)
        }

        override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
            holder.bindMenuItem(urlList[position])
        }

        override fun getItemCount(): Int {
            return urlList.size
        }

        inner class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bindMenuItem(menuUrl: String) {
                Glide.with(context)
                    .load(menuUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontTransform()
                    .into(itemView.ivMenu)
                itemView.setOnClickListener {
                    val intent = Intent(context, MenuViewActivity::class.java)
                    intent.putStringArrayListExtra("IMAGE", urlList as java.util.ArrayList<String>?)
                    context.startActivity(intent)
                }
            }
        }
    }

    private class TagRecyclerViewAdapter(val tagList: List<String>) :
        RecyclerView.Adapter<TagRecyclerViewAdapter.TagViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.restaurant_tag_item, parent, false)
            return TagViewHolder(view)
        }

        override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
            holder.bindMenuItem(tagList[position])
        }

        override fun getItemCount(): Int {
            return tagList.size
        }

        inner class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bindMenuItem(tagText: String) {
                itemView.tvTag.text = tagText
            }
        }
    }
}
