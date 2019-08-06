package com.hangloose.ui.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import com.hangloose.R
import android.support.v4.app.FragmentPagerAdapter
import android.view.ViewGroup
import android.view.LayoutInflater
import android.support.v4.app.FragmentManager
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_menu_view.*


class MenuViewActivity : BaseActivity() {
    override fun init() {

    }


    var imageFragmentPagerAdapter: ImageFragmentPagerAdapter? = null
    val IMAGE_NAME = arrayListOf<Int>(R.drawable.ic_restaurant_view, R.drawable.ic_restaurant_view)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_view)
        imageFragmentPagerAdapter = ImageFragmentPagerAdapter(supportFragmentManager, IMAGE_NAME)
        pager.adapter = imageFragmentPagerAdapter
        ic_next.setOnClickListener {
            pager.currentItem = pager.currentItem + 1
        }
        ic_before.setOnClickListener {
            pager.currentItem = pager.currentItem - 1
        }
    }

    class ImageFragmentPagerAdapter(fm: FragmentManager, val IMAGE_NAME: ArrayList<Int>) :
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
            val imageList = bundle.getIntegerArrayList("MENU_IMAGES")
            imageView.setImageResource(imageList[position])
            return swipeView
        }

        companion object {
            internal fun newInstance(position: Int, IMAGE_NAME: ArrayList<Int>): SwipeFragment {
                val swipeFragment = SwipeFragment()
                val bundle = Bundle()
                bundle.putInt("position", position)
                bundle.putIntegerArrayList("MENU_IMAGES", IMAGE_NAME)
                swipeFragment.setArguments(bundle)
                return swipeFragment
            }
        }
    }
}
