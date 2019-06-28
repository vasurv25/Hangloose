package com.hangloose.ui.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.hangloose.R
import com.hangloose.ui.adapter.FilterAdapter
import io.apptik.widget.MultiSlider
import kotlinx.android.synthetic.main.activity_filter.*


class FilterActivity : AppCompatActivity() {

    private val musicList = arrayListOf("Live", "Soft", "Live Instrument", "DJ Night")
    private val comedyList = arrayListOf("Live Comedy Show", "Open Mic")
    private val diningList = arrayListOf("Fancy", "Casual", "Daba Style", "Something Casual")
    private val somethingNewList = arrayListOf("New Dish", "New Town")
    private val featuresList = arrayListOf("Hookah", "Hookah & Bar", "Bar")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        setSupportActionBar(findViewById(R.id.my_toolbar))

        setAdapters()

        ibFilter.setOnClickListener { finish() }

        minDiscount.text = discount_range.getThumb(0).value.toString()
        maxDiscount.text = discount_range.getThumb(1).value.toString()

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
    }

    private fun setAdapters() {
        val lLMMusic = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val adapterMusic = FilterAdapter(this, musicList)
        rv_music.layoutManager = lLMMusic
        rv_music.adapter = adapterMusic

        val lLMComedy = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val adapterComedy = FilterAdapter(this, comedyList)
        rv_comedy.layoutManager = lLMComedy
        rv_comedy.adapter = adapterComedy

        val lLMDining = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val adapterDining = FilterAdapter(this, diningList)
        rv_dining.layoutManager = lLMDining
        rv_dining.adapter = adapterDining

        val lLMSomethingNew = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val adapterSomethingNew = FilterAdapter(this, somethingNewList)
        rv_somethingNew.layoutManager = lLMSomethingNew
        rv_somethingNew.adapter = adapterSomethingNew

        val lLMFeatures = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val adapterFeatures = FilterAdapter(this, featuresList)
        rv_features.layoutManager = lLMFeatures
        rv_features.adapter = adapterFeatures
    }
}
