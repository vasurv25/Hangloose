package com.hangloose.ui.activities

import android.content.SharedPreferences
import android.os.Bundle
import com.hangloose.R
import com.hangloose.utils.KEY_EMAIL_ID
import com.hangloose.utils.KEY_USER_NAME
import com.hangloose.utils.PreferenceHelper
import kotlinx.android.synthetic.main.activity_queries_suggestions.*
import android.content.Intent
import androidx.core.content.ContextCompat
import android.view.View
import com.hangloose.utils.showSnackBar

class QueriesSuggestionsActivity : BaseActivity() {
    override fun init() {

    }

    private var mPreference: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_queries_suggestions)
        getPrefData()
        btnSend.setOnClickListener {
            if (editQueries.text!!.isNotEmpty() || editSuggestions.text!!.isNotEmpty()) {
                val subject = resources.getString(R.string.queries_suggestions)
                val bodyText = resources.getString(R.string.queries) + ":" +
                        editQueries.text.toString() + "\n\n" +
                resources.getString(R.string.suggestions) + ":" +
                        editSuggestions.text.toString()

                val i = Intent(Intent.ACTION_SEND)
                i.type = "message/rfc822"
                i.putExtra(Intent.EXTRA_EMAIL, arrayOf("hangloose.helpline@gmail.com"))
                i.putExtra(Intent.EXTRA_SUBJECT, subject)
                i.putExtra(Intent.EXTRA_TEXT, bodyText)
                i.setPackage("com.google.android.gm")
                startActivity(i)
            } else {
                showSnackBar(
                    llQueriesSuggestions,
                    "Please enter either Query or Suggestion!",
                    ContextCompat.getColor(this, R.color.white),
                    ContextCompat.getColor(this, R.color.red)
                )
            }
        }
    }

    private fun getPrefData() {
        mPreference = PreferenceHelper.defaultPrefs(this)
        val username = mPreference!!.getString(KEY_USER_NAME, null)
        if (username != null) {
            editUsername.setText(username)
        } else {
            textUsername.visibility = View.GONE
        }
        val email = mPreference!!.getString(KEY_EMAIL_ID, null)
        if (email != null) {
            editEmail.setText(email)
        } else {
            textEmail.visibility = View.GONE
        }
    }
}
