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
import android.util.Log
import android.view.View
import com.hangloose.R
import com.hangloose.databinding.ActivitySelectionBinding
import com.hangloose.model.RestaurantList
import com.hangloose.ui.adapter.ViewPagerAdapter
import com.hangloose.ui.fragment.ActivitiesFragment
import com.hangloose.ui.fragment.AdventuresFragment
import com.hangloose.ui.model.ActivitiesDetails
import com.hangloose.ui.model.AdventuresDetails
import com.hangloose.ui.model.RestaurantData
import com.hangloose.ui.model.SelectionList
import com.hangloose.utils.KEY_RESTAURANT_DATA
import com.hangloose.utils.PreferenceHelper
import com.hangloose.utils.showSnackBar
import com.hangloose.viewmodel.SelectionViewModel
import kotlinx.android.synthetic.main.activity_selection.indicator
import kotlinx.android.synthetic.main.activity_selection.ll_selection
import kotlinx.android.synthetic.main.activity_selection.tvSelectionHeading
import kotlinx.android.synthetic.main.activity_selection.viewPager
import retrofit2.Response

class SelectionActivity : BaseActivity() {

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
    private var mRestaurantData = ArrayList<RestaurantData>()
    private var mPreference: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPreference = PreferenceHelper.defaultPrefs(this)
        initBinding()
    }

    override fun init() {}

    //override fun onBackPressed() {}

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
        viewPagerAdapter.registerDataSetObserver(indicator.dataSetObserver)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                if (viewPager.currentItem == 0) {
                    tvSelectionHeading.text = getString(R.string.select_your_activities)
                    //btRefresh.visibility = View.GONE
                } else {
                    tvSelectionHeading.text = getString(R.string.select_your_adventure)
                    //btRefresh.visibility = View.VISIBLE
                }
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
            }
        })
    }

    private fun initBinding() {
        mActivitySelectionBinding = DataBindingUtil.setContentView(this, R.layout.activity_selection)
        mActivitySelectionBinding!!.clickHandler = this
        mSelectionViewModel = ViewModelProviders.of(this).get(SelectionViewModel::class.java)
        mActivitySelectionBinding!!.selectionViewModel = mSelectionViewModel
        mSelectionViewModel.selectionListApiRequest()
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
            onNavigateToTabsScreen(mRestaurantData)
        })

        mSelectionViewModel.mShowErrorSnackBar.observe(this, Observer { t ->
            showSnackBar(
                ll_selection,
                t.toString(),
                ContextCompat.getColor(this, R.color.white),
                ContextCompat.getColor(this, R.color.colorPrimary)
            )
        })
    }

    fun oNextClick(view: View?) {
        //didClickNextButton?.invoke()
        mActivitiesSelectedList.addAll(mActivitiesFragment!!.getSelectedActivities())
        mAdventuresSelectedList.addAll(mAdventuresFragment!!.getSelectedAdventures())
        if (mActivitiesSelectedList.size != 0 && mAdventuresSelectedList.size != 0) {
            mSelectionViewModel.restaurantListApiRequest(mActivitiesSelectedList, mAdventuresSelectedList)
        }
    }

    private fun onNavigateToTabsScreen(restaurantData: ArrayList<RestaurantData>) {

        if (mActivitiesFragment!!.getSelectedActivities().size > 0 && mAdventuresFragment!!.getSelectedAdventures().size > 0) {
            val intent = Intent(this, TabsActivity::class.java)
            intent.putParcelableArrayListExtra(KEY_RESTAURANT_DATA, restaurantData)
            startActivity(intent)
        }
    }
}