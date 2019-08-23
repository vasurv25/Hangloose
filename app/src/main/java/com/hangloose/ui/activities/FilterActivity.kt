package com.hangloose.ui.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import com.hangloose.utils.PreferenceHelper.get
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.hangloose.R
import com.hangloose.model.RestaurantList
import com.hangloose.ui.adapter.FilterAdapter
import com.hangloose.ui.model.RestaurantData
import com.hangloose.utils.*
import com.hangloose.viewmodel.LocationViewModel
import io.apptik.widget.MultiSlider
import kotlinx.android.synthetic.main.activity_filter.*
import retrofit2.Response


class FilterActivity : BaseActivity() {
    override fun init() {

    }

    private var TAG = "FilterActivity"
    private val musicList = arrayListOf("Live", "Soft", "Live Instrument", "DJ Night")
    private val comedyList = arrayListOf("Live Comedy Show", "Open Mic")
    private val diningList = arrayListOf("Fancy", "Casual", "Daba Style", "Something Casual")
    private val somethingNewList = arrayListOf("New Dish", "New In Town")
    private val featuresList = arrayListOf("Hookah", "Hookah & Bar", "Bar")

    private var adapterMusic: FilterAdapter? = null
    private var adapterComedy: FilterAdapter? = null
    private var adapterDining: FilterAdapter? = null
    private var adapterSomethingNew: FilterAdapter? = null
    private var adapterFeatures: FilterAdapter? = null

    private var mLatitude: Double? = null
    private var mLongitude: Double? = null
    private var mActivitiesSelectedList = ArrayList<String>()
    private var mAdventuresSelectedList = ArrayList<String>()

    private var mLocationViewModel: LocationViewModel? = null
    private var mPreference: SharedPreferences? = null
    private var mHeaderToken: String? = null
    private var mRestaurantData: ArrayList<RestaurantData>? = ArrayList()
    private var mSelectedTagList: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        setSupportActionBar(findViewById(R.id.my_toolbar))

        mActivitiesSelectedList = intent.getStringArrayListExtra(KEY_ACTIVITIES_LIST)
        mAdventuresSelectedList = intent.getStringArrayListExtra(KEY_ADVENTURES_LIST)
        mLatitude = intent.getDoubleExtra(KEY_LATITUDE, 0.0)
        mLongitude = intent.getDoubleExtra(KEY_LONGTITUDE, 0.0)
        mPreference = PreferenceHelper.defaultPrefs(this)
        mHeaderToken = mPreference!![X_AUTH_TOKEN]

        mLocationViewModel = ViewModelProviders.of(this).get(LocationViewModel::class.java)

        setAdapters()
        ibFilter.setOnClickListener { finish() }

//        minDiscount.text = discount_range.getThumb(0).value.toString()
//        maxDiscount.text = discount_range.getThumb(1).value.toString()

        discount_range.setOnThumbValueChangeListener(object : MultiSlider.SimpleChangeListener() {
            override fun onValueChanged(
                multiSlider: MultiSlider?,
                thumb: MultiSlider.Thumb?,
                thumbIndex: Int,
                value: Int
            ) {
                if (thumbIndex == 0) {
                    minDiscount.text = "$value%"
                } else {
                    maxDiscount.text = "$value%"
                }
            }
        })

        btnClearFilter.setOnClickListener {
            rv_music.adapter = adapterMusic
            rv_features.adapter = adapterFeatures
            rv_somethingNew.adapter = adapterSomethingNew
            rv_dining.adapter = adapterDining
            rv_comedy.adapter = adapterComedy
            minDiscount.text = "0%"
            maxDiscount.text = "100%"
            discount_range.clearThumbs()
            discount_range.addThumb(0)
            discount_range.addThumb(100)
            clearTagList()
            doApiCallForTags("")

        }

        btnApplyFilter.setOnClickListener {
            //clearTagList()
            Log.d(TAG, "Tag List : " + getAllSelectedTagsList())
            doApiCallForTags(mLocationViewModel!!.convertToCSV(getAllSelectedTagsList()))
        }
    }

    private fun doApiCallForTags(tagsList: String) {
        mRestaurantData!!.clear()
        mLocationViewModel!!.restaurantListApiRequest(
            mActivitiesSelectedList, mAdventuresSelectedList
            , mLatitude!!, mLongitude!!, mHeaderToken, tagsList
        )

        mLocationViewModel!!.getRestaurantList().observe(this, Observer<Response<List<RestaurantList>>> { t ->
            val data = t!!.body()
            (0 until data!!.size).forEach { i ->
                if (data[i].distanceFromLocation!! <= 50) {
                    var logo: String? = null
                    var ambienceList: ArrayList<String>? = ArrayList()
                    var menuList: ArrayList<String>? = ArrayList()
                    (0 until data[i].documents!!.size).forEach { j ->
                        if (data[i].documents!![j].documentType.equals("LOGO")) {
                            Log.d(TAG, "Logo : " + data[i].documents!![j].location)
                            logo = data[i].documents!![j].location
                        } else if (data[i].documents!![j].documentType.equals("AMBIENCE")) {
                            ambienceList!!.add(data[i].documents!![j].location!!)
                        } else {
                            menuList!!.add(data[i].documents!![j].location!!)
                        }
                    }
                    mRestaurantData!!.add(
                        RestaurantData(
                            data[i].address!!,
                            data[i].createdAt,
                            data[i].discount,
                            data[i].id,
                            data[i].images,
                            data[i].latitude,
                            data[i].longitude,
                            data[i].name,
                            data[i].offer,
                            data[i].priceFortwo,
                            data[i].ratings,
                            data[i].restaurantType,
                            data[i].updatedAt,
                            data[i].distanceFromLocation,
                            data[i].about,
                            data[i].tags,
                            data[i].openCloseTime,
                            data[i].number,
                            logo,
                            ambienceList,
                            menuList
                        )
                    )
                }
            }
            mSelectedTagList.clear()
            val intent = Intent(this, TabsActivity::class.java)
            intent.putStringArrayListExtra(KEY_ACTIVITIES_LIST, mActivitiesSelectedList)
            intent.putStringArrayListExtra(KEY_ADVENTURES_LIST, mAdventuresSelectedList)
            intent.putParcelableArrayListExtra(KEY_RESTAURANT_DATA, mRestaurantData)
            startActivity(intent)
        })
    }

    private fun setAdapters() {
        val lLMMusic = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapterMusic = FilterAdapter(this, musicList)
        rv_music.layoutManager = lLMMusic
        rv_music.adapter = adapterMusic

        val lLMComedy = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapterComedy = FilterAdapter(this, comedyList)
        rv_comedy.layoutManager = lLMComedy
        rv_comedy.adapter = adapterComedy

        val lLMDining = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapterDining = FilterAdapter(this, diningList)
        rv_dining.layoutManager = lLMDining
        rv_dining.adapter = adapterDining

        val lLMSomethingNew = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapterSomethingNew = FilterAdapter(this, somethingNewList)
        rv_somethingNew.layoutManager = lLMSomethingNew
        rv_somethingNew.adapter = adapterSomethingNew

        val lLMFeatures = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapterFeatures = FilterAdapter(this, featuresList)
        rv_features.layoutManager = lLMFeatures
        rv_features.adapter = adapterFeatures
    }

    private fun getAllSelectedTagsList(): ArrayList<String> {
        adapterMusic!!.getTagsList()
        adapterComedy!!.getTagsList()
        adapterDining!!.getTagsList()
        adapterSomethingNew!!.getTagsList()
        adapterFeatures!!.getTagsList()
        mSelectedTagList.addAll(adapterMusic!!.getTagsList())
        mSelectedTagList.addAll(adapterComedy!!.getTagsList())
        mSelectedTagList.addAll(adapterDining!!.getTagsList())
        mSelectedTagList.addAll(adapterSomethingNew!!.getTagsList())
        mSelectedTagList.addAll(adapterFeatures!!.getTagsList())

        return mSelectedTagList
    }

    private fun clearTagList() {
        adapterMusic!!.getTagsList().clear()
        adapterComedy!!.getTagsList().clear()
        adapterDining!!.getTagsList().clear()
        adapterSomethingNew!!.getTagsList().clear()
        adapterFeatures!!.getTagsList().clear()
        mSelectedTagList.clear()
    }
}
