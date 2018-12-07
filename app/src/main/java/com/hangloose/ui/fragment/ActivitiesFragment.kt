package com.hangloose.ui.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hangloose.R

class ActivitiesFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_activities, null)
    }

    object Instance {
        fun newInstance(context: Context): Fragment {
            return ActivitiesFragment()
        }
    }
}