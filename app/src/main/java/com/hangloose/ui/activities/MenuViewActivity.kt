package com.hangloose.ui.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import com.hangloose.R
import android.support.v4.app.FragmentPagerAdapter
import android.view.ViewGroup
import android.view.LayoutInflater
import android.support.v4.app.FragmentManager
import android.view.View
import android.widget.ImageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_menu_view.*


class MenuViewActivity : BaseActivity() {
    override fun init() {

    }
    var imageFragmentPagerAdapter: ImageFragmentPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_view)
        var menuImageList = intent.getStringArrayListExtra("IMAGE")
        imageFragmentPagerAdapter = ImageFragmentPagerAdapter(supportFragmentManager, menuImageList)
        pager.adapter = imageFragmentPagerAdapter
        ic_next.setOnClickListener {
            pager.currentItem = pager.currentItem + 1
        }
        ic_before.setOnClickListener {
            pager.currentItem = pager.currentItem - 1
        }
    }

    class ImageFragmentPagerAdapter(fm: FragmentManager, val IMAGE_NAME: ArrayList<String>) :
        FragmentPagerAdapter(fm) {
        override fun getCount(): Int {
            return IMAGE_NAME.size
        }

        override fun getItem(position: Int): Fragment {
            return SwipeFragment.newInstance(position, IMAGE_NAME)
        }
    }

    class SwipeFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val swipeView = inflater.inflate(R.layout.layout_swipe_fragment, container, false)
            val imageView = swipeView.findViewById(R.id.imageView) as ImageView
            val bundle = arguments
            val position = bundle!!.getInt("position")
            val imageList = bundle.getStringArrayList("MENU_IMAGES")
            Picasso.with(context).load(imageList[position]).into(imageView)
            return swipeView
        }

        companion object {
            internal fun newInstance(position: Int, IMAGE_NAME: ArrayList<String>): SwipeFragment {
                val swipeFragment = SwipeFragment()
                val bundle = Bundle()
                bundle.putInt("position", position)
                bundle.putStringArrayList("MENU_IMAGES", IMAGE_NAME)
                swipeFragment.setArguments(bundle)
                return swipeFragment
            }
        }
    }
}
