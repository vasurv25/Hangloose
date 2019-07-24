package com.hangloose.ui.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hangloose.R
import com.hangloose.ui.model.RestaurantData
import com.hangloose.utils.EXTRA_RESTAURANT_DETAILS_DATA
import kotlinx.android.synthetic.main.activity_restaurant_details.*
import kotlinx.android.synthetic.main.restaurant_menu_item.view.*
import kotlinx.android.synthetic.main.restaurant_tag_item.view.*


class RestaurantDetailsActivity : AppCompatActivity() {

    private val REQUEST_CALL_PHONE = 11
    private val TAG = "RestaurantDetails"
    private var restaurantData: RestaurantData? = null
    private var mMenuRecyclerViewAdapter: MenuRecyclerViewAdapter? = null
    private var mMenuUrlList: List<Int>? = arrayListOf(R.drawable.ic_restaurant_view, R.drawable.ic_restaurant_view)
    private var mTagList: List<String>? = ArrayList()
    private var mTagRecyclerViewAdapter: TagRecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_details)
        restaurantData = intent.getParcelableExtra<RestaurantData>(EXTRA_RESTAURANT_DETAILS_DATA)
        Log.i(TAG, restaurantData.toString())
        setUpViews()
    }

    private fun setUpViews() {
        if (restaurantData != null) {
            textName.text = restaurantData!!.name
            textPlace.text = restaurantData!!.address
            textRatingValue.text = restaurantData!!.ratings
            expand_text_view.text = restaurantData!!.about
            textOpenCloseTime.text = restaurantData!!.openCloseTime
            textChargesValue.text = restaurantData!!.priceFortwo
            mTagList = restaurantData!!.tags
            if (restaurantData!!.restaurantType!!.equals("VEGETERIAN")) {
                textNonVeg.visibility = View.GONE
            } else {
                textNonVeg.visibility = View.VISIBLE
            }
            textOfferBill.text = restaurantData!!.offer
            textAddress.text = restaurantData!!.address
            btnCall.setOnClickListener {

                checkCallPermission()


            }
            initMenuRecyclerView()
            initTagRecyclerView()
        }
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
                callIntent.data = Uri.parse("tel:" + 2839127392)//change the number.
                startActivity(callIntent)
            }
        } else {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:" + 2839127392)//change the number.
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

    private fun initMenuRecyclerView() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvMenu.layoutManager = layoutManager
        mMenuRecyclerViewAdapter = mMenuUrlList?.let { MenuRecyclerViewAdapter(this, it) }
        rvMenu.adapter = mMenuRecyclerViewAdapter
    }

    private fun initTagRecyclerView() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvTags.layoutManager = layoutManager
        mTagRecyclerViewAdapter = mTagList?.let { TagRecyclerViewAdapter(it) }
        rvTags.adapter = mTagRecyclerViewAdapter
    }

    private class MenuRecyclerViewAdapter(val context: Context, val urlList: List<Int>) :
        RecyclerView.Adapter<MenuRecyclerViewAdapter.MenuViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MenuViewHolder {
            val view = LayoutInflater.from(parent!!.context).inflate(R.layout.restaurant_menu_item, parent, false)
            return MenuViewHolder(view)
        }

        override fun getItemCount(): Int {
            return urlList.size
        }

        override fun onBindViewHolder(holder: MenuViewHolder?, position: Int) {
            holder!!.bindMenuItem(urlList[position])
        }

        inner class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bindMenuItem(menuUrl: Int) {
//                Picasso.with(context).load(menuUrl).into(itemView.ivMenu)
                itemView.ivMenu.setImageResource(menuUrl)
                itemView.setOnClickListener {
                    val intent = Intent(context, MenuViewActivity::class.java)
                    context.startActivity(intent)
                }
            }
        }
    }

    private class TagRecyclerViewAdapter(val tagList: List<String>) :
        RecyclerView.Adapter<TagRecyclerViewAdapter.TagViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TagViewHolder {
            val view = LayoutInflater.from(parent!!.context).inflate(R.layout.restaurant_tag_item, parent, false)
            return TagViewHolder(view)
        }

        override fun getItemCount(): Int {
            return tagList.size
        }

        override fun onBindViewHolder(holder: TagViewHolder?, position: Int) {
            holder!!.bindMenuItem(tagList[position])
        }

        inner class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bindMenuItem(tagText: String) {
                itemView.tvTag.text = tagText
            }
        }
    }
}
