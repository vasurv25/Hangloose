package com.hangloose.ui.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.SharedPreferences
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v4.view.ViewPager.SCROLL_STATE_SETTLING
import android.util.Log
import android.view.View
import com.hangloose.HanglooseApp
import com.hangloose.R
import com.hangloose.database.dbmodel.LikedRestaurant
import com.hangloose.databinding.ActivitySelectionBinding
import com.hangloose.listener.LikedInsertionListener
import com.hangloose.listener.SavedInsertionListener
import com.hangloose.model.RestaurantList
import com.hangloose.ui.adapter.ViewPagerAdapter
import com.hangloose.ui.fragment.ActivitiesFragment
import com.hangloose.ui.fragment.AdventuresFragment
import com.hangloose.ui.model.ActivitiesDetails
import com.hangloose.ui.model.AdventuresDetails
import com.hangloose.ui.model.RestaurantData
import com.hangloose.ui.model.SelectionList
import com.hangloose.utils.*
import com.hangloose.utils.PreferenceHelper.get
import com.hangloose.viewmodel.SelectionViewModel
import kotlinx.android.synthetic.main.activity_selection.*
import retrofit2.Response

class SelectionActivity : BaseActivity(), SavedInsertionListener, LikedInsertionListener, View.OnClickListener {

    private var TAG = "SelectionActivity"
    var didClickNextButton: (() -> Unit)? = null
    private lateinit var mSelectionViewModel: SelectionViewModel
    private var mActivitiesList = ArrayList<ActivitiesDetails>()
    private var mAdventuresList = ArrayList<AdventuresDetails>()
    private var mActivitySelectionBinding: ActivitySelectionBinding? = null
    private var mActivitiesSelectedList = ArrayList<String>()
    private var mAdventuresSelectedList = ArrayList<String>()
    private var mActivitiesFragment: ActivitiesFragment? = null
    private var mAdventuresFragment: AdventuresFragment? = null
    private var mPreference: SharedPreferences? = null
    private var mHeader: String? = null

    private var mRestaurantData = ArrayList<RestaurantData>()
    private var mEntireRestaurantData = ArrayList<RestaurantData>()

    private var mLatitude: Double? = null
    private var mLongitude: Double? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPreference = PreferenceHelper.defaultPrefs(this)
        val headerToken: String? = mPreference!![X_AUTH_TOKEN]
        Log.i(TAG, """Header : $headerToken""")
        mHeader = headerToken
        initBinding()
//        if (savedInstanceState == null) {
////            getIntentData()
////        } else {
////            var hmm = savedInstanceState!!.getInt("123")
////            Log.i(TAG, "SavedInstance : " + hmm)
////            mActivitiesList = savedInstanceState.getParcelableArrayList<ActivitiesDetails>(KEY_ACTIVITIES_LIST)
////            mAdventuresList = savedInstanceState.getParcelableArrayList<AdventuresDetails>(KEY_ADVENTURES_LIST)
////            setFragments()
////        }
    }

    override fun init() {}

    override fun onBackPressed() {
    }

    private fun getIntentData() {
        mActivitiesList = intent!!.getParcelableArrayListExtra(KEY_ACTIVITIES_LIST)
        mAdventuresList = intent!!.getParcelableArrayListExtra(KEY_ADVENTURES_LIST)
        setFragments()
    }

    private fun setFragments() {
        Log.i(TAG, "init")
        tvSelectionHeading.text = getString(R.string.select_your_activities)
        val listOfFragment = ArrayList<Fragment>()
        mActivitiesFragment = ActivitiesFragment.newInstance(mActivitiesList)
        mAdventuresFragment = AdventuresFragment.newInstance(mAdventuresList)
        listOfFragment.add(mActivitiesFragment!!)
        listOfFragment.add(mAdventuresFragment!!)
        val viewPagerAdapter = ViewPagerAdapter(applicationContext, supportFragmentManager, listOfFragment)
        viewPager.adapter = viewPagerAdapter
        indicator.setViewPager(viewPager)
        viewPager.currentItem = 0
        setTutorialVisibility(KEY_ACTIVITIES_TUTORIAL)
        viewPagerAdapter.registerDataSetObserver(indicator.dataSetObserver)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                when(state){
                    SCROLL_STATE_SETTLING -> {
                        if (viewPager.currentItem == 0) {
                            tvSelectionHeading.text = getString(R.string.select_your_activities)
                        } else {
                            tvSelectionHeading.text = getString(R.string.select_your_adventure)
                            setTutorialVisibility(KEY_ADVENTURES_TUTORIAL)
                        }
                    }
                }
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
            }
        })
    }

    override fun onClick(view: View?) {
        view!!.visibility = View.GONE
    }

    private fun setTutorialVisibility(key: String) {
        if (isTutorialShown(this@SelectionActivity, key)) {
            tutorial_view.visibility = View.GONE
            tutorial_view.setOnClickListener(null)
        } else {
            tutorial_view.visibility = View.VISIBLE
            tutorial_view.setOnClickListener(this)
            setTutorialShown(this@SelectionActivity, key)
        }
    }

    private fun initBinding() {
        mActivitySelectionBinding = DataBindingUtil.setContentView(this, R.layout.activity_selection)
        mActivitySelectionBinding!!.clickHandler = this
        mSelectionViewModel = ViewModelProviders.of(this).get(SelectionViewModel::class.java)
        mActivitySelectionBinding!!.selectionViewModel = mSelectionViewModel
        if (intent!!.getParcelableArrayListExtra<ActivitiesDetails>(KEY_ACTIVITIES_LIST) != null) {
            getIntentData()
        } else {
            mSelectionViewModel.selectionListApiRequest(mHeader)
        }
        mSelectionViewModel.getSelectionList().observe(this, Observer<SelectionList> { t ->
            Log.i(TAG, "onChanged")
            (0 until t!!.activities.size).forEach { i ->
                val list = t.activities
                Log.i(TAG, "Activities : " + list[i].id!!)
                mActivitiesList.add(
                    ActivitiesDetails(
                        list[i].createdAt!!,
                        list[i].updatedAt!!,
                        list[i].id!!,
                        list[i].name!!,
                        list[i].image!!
                    )
                )
            }
            (0 until t.adventures.size).forEach { i ->
                val list = t.adventures
                Log.i(TAG, "Adventures : " + list[i].id!!)
                mAdventuresList.add(
                    AdventuresDetails(
                        list[i].createdAt!!,
                        list[i].updatedAt!!,
                        list[i].id!!,
                        list[i].name!!,
                        list[i].image!!
                    )
                )
            }
            setFragments()
        })

        mSelectionViewModel.getRestaurantList().observe(this, Observer<Response<List<RestaurantList>>> { t ->
            val data = t!!.body()
            (0 until data!!.size).forEach { i ->
                if (data[i].distanceFromLocation!! <= 50) {
                    var logo: String? = null
                    val ambienceList: ArrayList<String>? = ArrayList()
                    var menuList: ArrayList<String>? = ArrayList()
                    (0 until data[i].documents!!.size).forEach { j ->
                        when {
                            data[i].documents!![j].documentType.equals("LOGO") -> logo = data[i].documents!![j].location
                            data[i].documents!![j].documentType.equals("AMBIENCE") -> ambienceList?.add(data[i].documents!![j].location!!)
                            else -> menuList?.add(data[i].documents!![j].location!!)
                        }
                    }
                    mRestaurantData.add(
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

                var logo: String? = null
                val ambienceList: ArrayList<String>? = ArrayList()
                val menuList: ArrayList<String>? = ArrayList()
                (0 until data[i].documents!!.size).forEach { j ->
                    if (data[i].documents!![j].documentType.equals("LOGO")) {
                        logo = data[i].documents!![j].location
                    } else if (data[i].documents!![j].documentType.equals("AMBIENCE")) {
                        ambienceList?.add(data[i].documents!![j].location!!)
                    } else {
                        menuList?.add(data[i].documents!![j].location!!)
                    }
                }
                mEntireRestaurantData.add(
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
                if (doesDatabaseExist(SAVED_TABLE_RESTAURANT)) {
                    HanglooseApp.getDataHandler()!!.updateSavedRestaurantData(mEntireRestaurantData[i], this)
                }

                if (doesDatabaseExist(LIKED_TABLE_RESTAURANT)) {
                    HanglooseApp.getDataHandler()!!.updateLikedRestaurantData(mEntireRestaurantData[i], this)
                }
            }
            onNavigateToTabsActivity()
        })

        /*mSelectionViewModel.getRestaurantList().observe(this, Observer<Response<List<RestaurantList>>> { t ->
            val data = t!!.body()
            (0 until data!!.size).forEach { i ->
                mRestaurantData.add(
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
                        data[i].updatedAt
                    )
                )
            }
            //checkLocationPermission()
            //onNavigateToLocationScreen()
        })*/

        mSelectionViewModel.mShowErrorSnackBar.observe(this, Observer { t ->
            showSnackBar(
                ll_selection,
                t.toString(),
                ContextCompat.getColor(this, R.color.white),
                ContextCompat.getColor(this, R.color.colorPrimary)
            )
        })
    }

    private fun doesDatabaseExist(dbName: String): Boolean {
        var dbFile = this.getDatabasePath(dbName);
        return dbFile.exists()
    }

    override fun onSavedRecordInserted(id: Long) {
        Log.i("Hangloose", "onSavedRecordInserted")
    }

    override fun onLikedRecordError(msg: String, data: RestaurantData) {
    }

    override fun getLikeData(data: LikedRestaurant, restaurantData: RestaurantData, type: String) {
    }

    override fun getLikeDataIfNull(data: RestaurantData, type: String) {
    }

    override fun onLikedRecordInserted(id: Long) {
        Log.i("Hangloose", "onLikedRecordInserted")
    }

    fun oNextClick(view: View?) {
        //didClickNextButton?.invoke()
        when (viewPager.currentItem) {
            0 -> {
                if (mActivitiesFragment!!.getSelectedActivities().size > 0) {
                    viewPager.currentItem = 1
                } else {
                    showSnackBar(
                        ll_selection,
                        "Please Select at least one Activity!",
                        ContextCompat.getColor(this, R.color.white),
                        ContextCompat.getColor(this, R.color.colorPrimary)
                    )
                }

            }
            1 -> {
                mActivitiesSelectedList.clear()
                mAdventuresSelectedList.clear()
                mActivitiesSelectedList.addAll(mActivitiesFragment!!.getSelectedActivities())
                mAdventuresSelectedList.addAll(mAdventuresFragment!!.getSelectedAdventures())
                if (mActivitiesSelectedList.size != 0 && mAdventuresSelectedList.size != 0) {
                    //TODO : We can use let and run scoping function instead of if else
                    val address: String? = mPreference!![PREFERENCE_ADDRESS]
                    if (address == null) {
                        onNavigateToLocationScreen(mActivitiesSelectedList, mAdventuresSelectedList)
                    } else {
                        val latLongFromLocationName = getLatLongFromLocationName(this, address)
                        Log.d(TAG, "latLongFromLocationName : $latLongFromLocationName")
                        latLongFromLocationName?.let {
                            mLatitude = latLongFromLocationName.latitude
                            mLongitude = latLongFromLocationName.longitude
                            mSelectionViewModel.restaurantListApiRequest(
                                mActivitiesSelectedList, mAdventuresSelectedList
                                , ""
                                , ""
                                , latLongFromLocationName.latitude
                                , latLongFromLocationName.longitude, mHeader
                            )
                        }
                    }
                } else {
                    if (mActivitiesSelectedList.size == 0 && mAdventuresSelectedList.size == 0) {
                        showSnackBar(
                            ll_selection,
                            "Please Select at least one Activities and Adventure!",
                            ContextCompat.getColor(this, R.color.white),
                            ContextCompat.getColor(this, R.color.colorPrimary)
                        )
                    } else if (mActivitiesSelectedList.size == 0) {
                        showSnackBar(
                            ll_selection,
                            "Please Select at least one Activities!",
                            ContextCompat.getColor(this, R.color.white),
                            ContextCompat.getColor(this, R.color.colorPrimary)
                        )
                    } else {
                        showSnackBar(
                            ll_selection,
                            "Please Select at least one Adventure!",
                            ContextCompat.getColor(this, R.color.white),
                            ContextCompat.getColor(this, R.color.colorPrimary)
                        )
                    }
                }
            }
        }
    }

    private fun onNavigateToLocationScreen(
        activitiesSelectedList: ArrayList<String>,
        adventuresSelectedList: ArrayList<String>
    ) {
        val intent = Intent(this, LocationSettingActivity::class.java)
        intent.putStringArrayListExtra(KEY_ACTIVITIES_LIST, activitiesSelectedList)
        intent.putStringArrayListExtra(KEY_ADVENTURES_LIST, adventuresSelectedList)
        intent.putExtra(FLAG_LOCATION_NAVIGATION, 0)
        startActivity(intent)
    }

    private fun onNavigateToTabsActivity() {
        val intent = Intent(this, TabsActivity::class.java)
        intent.putStringArrayListExtra(KEY_ACTIVITIES_LIST, mActivitiesSelectedList)
        intent.putStringArrayListExtra(KEY_ADVENTURES_LIST, mAdventuresSelectedList)
        setRestaurantData(mRestaurantData)
        setEntireRestaurantData(mEntireRestaurantData)
        intent.putExtra(KEY_LATITUDE, mLatitude)
        intent.putExtra(KEY_LONGTITUDE, mLongitude)
        startActivity(intent)
    }

//    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
//        super.onRestoreInstanceState(savedInstanceState)
//        var hmm = savedInstanceState!!.getInt ("123")
//        mActivitiesList = savedInstanceState!!.getParcelableArrayList<ActivitiesDetails>(KEY_ACTIVITIES_LIST)
//        mAdventuresList =savedInstanceState!!.getParcelableArrayList<AdventuresDetails>(KEY_ADVENTURES_LIST)
//        Log.d(TAG, "onRestoreInstanceState : " + hmm)
//        setFragments()
//    }
//
//    override fun onSaveInstanceState(outState: Bundle?) {
//        outState!!.putParcelableArrayList(KEY_ACTIVITIES_LIST, mActivitiesList)
//        outState!!.putParcelableArrayList(KEY_ADVENTURES_LIST, mAdventuresList)
//        outState!!.putInt("123", 1)
//        Log.d(TAG, "onSaveInstanceState : " + outState)
//        super.onSaveInstanceState(outState)
//    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.clear()
    }
}