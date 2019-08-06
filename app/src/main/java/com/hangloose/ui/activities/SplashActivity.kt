package com.hangloose.ui.activities

import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.hangloose.R
import com.hangloose.utils.PreferenceHelper
import com.hangloose.utils.PreferenceHelper.get
import com.hangloose.utils.X_AUTH_TOKEN

class SplashActivity : BaseActivity() {
    override fun init() {

    }

    private var mDelayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 3000 //3 seconds

    var mHeader: String? = null
    private var mPreference: SharedPreferences? = null

    internal val mRunnable: Runnable = Runnable {
        if (!isFinishing) {
            if (mHeader == null) {
                val intent = Intent(applicationContext, SignInActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(applicationContext, SelectionActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        //Initialize the Handler
        mDelayHandler = Handler()
        mPreference = PreferenceHelper.defaultPrefs(this)
        mHeader = mPreference!![X_AUTH_TOKEN]
        //Navigate with delay
        mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mDelayHandler != null) {
            mDelayHandler!!.removeCallbacks(mRunnable)
        }
    }
}
