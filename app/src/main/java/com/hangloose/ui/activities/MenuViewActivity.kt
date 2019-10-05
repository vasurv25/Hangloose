package com.hangloose.ui.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.hangloose.R
import kotlinx.android.synthetic.main.activity_menu_view.*
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.chrisbanes.photoview.PhotoView


class MenuViewActivity : BaseActivity() {

    override fun init() {

    }
    private var imageFragmentPagerAdapter: ImageFragmentPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_view)
        val menuImageList = intent.getStringArrayListExtra("IMAGE")
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
            val imageView = swipeView.findViewById(R.id.imageView) as PhotoView
            val bundle = arguments
            val position = bundle!!.getInt("position")
            val imageList = bundle.getStringArrayList("MENU_IMAGES")
            Glide.with(context!!)
                .load(imageList[position])
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontTransform()
                .into(imageView)
            return swipeView
        }

        companion object {
            internal fun newInstance(position: Int, IMAGE_NAME: ArrayList<String>): SwipeFragment {
                val swipeFragment = SwipeFragment()
                val bundle = Bundle()
                bundle.putInt("position", position)
                bundle.putStringArrayList("MENU_IMAGES", IMAGE_NAME)
                swipeFragment.arguments = bundle
                return swipeFragment
            }
        }
    }
}
