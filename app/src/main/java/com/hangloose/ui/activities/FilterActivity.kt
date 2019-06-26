package com.hangloose.ui.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.CheckBox
import com.hangloose.R
import kotlinx.android.synthetic.main.activity_filter.*

class FilterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        setSupportActionBar(findViewById(R.id.my_toolbar))

        ibFilter.setOnClickListener { finish() }
    }

    fun onCheckClick(v: View) {
        v as CheckBox
        if (v.isChecked) {
            v.setTextColor(ContextCompat.getColor(this, R.color.white))
        } else {
            v.setTextColor(ContextCompat.getColor(this, R.color.black))
        }
    }
}
