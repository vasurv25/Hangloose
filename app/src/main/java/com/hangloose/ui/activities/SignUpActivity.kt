package com.hangloose.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.Profile
import com.facebook.ProfileTracker
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.hangloose.R
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.Arrays

class SignUpActivity : BaseActivity(), View.OnClickListener {

    private val TAG: String = "SignUpActivity"
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val RC_SIGN_IN = 9001
    private var mFBCallbackManager: CallbackManager? = null
    private var mProfileTracker: ProfileTracker? = null

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        intializeGoogleSignInOptions()
        signInWithFacebook()
        setSpannableString()
    }

    override fun init() {
        btnGoogleSignIn.setOnClickListener(this)
        //btnGoogleSignOut.setOnClickListener(this)
        btnCustomSignInFB.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            btnGoogleSignIn.id -> signIn()
            //btnGoogleSignOut.id -> signOut()
            btnCustomSignInFB.id -> btnSignInFB.performClick()
        }
    }

    override fun onStart() {
        super.onStart()
        mGoogleSignInClient!!.silentSignIn().addOnCompleteListener {
            handleSignInResult(it)
        }
    }

    /**
     * method to intialize google signIn options
     */
    private fun intializeGoogleSignInOptions() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_client_id))
            .requestEmail()
            .build()
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signInWithFacebook() {
//        val loggedOut = AccessToken.getCurrentAccessToken() == null
//
//        if (!loggedOut) {
//            Log.d(TAG, "Username is: " + Profile.getCurrentProfile().name)
//            //Using Graph API
//            getFacebookUserProfile(AccessToken.getCurrentAccessToken())
//        }
//
//        val fbTracker = object : AccessTokenTracker() {
//            override fun onCurrentAccessTokenChanged(accessToken: AccessToken, accessToken2: AccessToken?) {
//                if (accessToken2 == null) {
//                    Toast.makeText(applicationContext, "User Logged Out.", Toast.LENGTH_LONG).show()
//                }
//            }
//        }
//        fbTracker.startTracking()

        mFBCallbackManager = CallbackManager.Factory.create()

        btnSignInFB.setReadPermissions(Arrays.asList("email", "public_profile"))

        btnSignInFB.registerCallback(mFBCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult?) {

                if (Profile.getCurrentProfile() == null) {
                    mProfileTracker = object : ProfileTracker() {
                        override fun onCurrentProfileChanged(oldProfile: Profile?, currentProfile: Profile?) {
                            Log.v(TAG, currentProfile!!.firstName)
                            getFacebookUserProfile(AccessToken.getCurrentAccessToken())
                            mProfileTracker!!.stopTracking()
                        }
                    }
                } else {
                    Log.d(TAG, "Username is: " + Profile.getCurrentProfile().name)
                    getFacebookUserProfile(AccessToken.getCurrentAccessToken())
                }
                mProfileTracker!!.startTracking()
            }

            override fun onCancel() {
                Log.i(TAG, "onCancel")
            }

            override fun onError(error: FacebookException?) {
                Log.i(TAG, "onError")
            }
        })
    }

    fun getFacebookUserProfile(accessToken: AccessToken) {
        val dataRequest = GraphRequest.newMeRequest(
            accessToken
        ) { jsonObject, response ->
            val first_name = jsonObject!!.getString("first_name")
            val last_name = jsonObject.getString("last_name")
            val email = jsonObject.getString("email")
            val id = jsonObject.getString("id")
            val image_url = "https://graph.facebook.com/$id/picture?type=normal"

            Log.i(TAG, "$first_name $last_name $email $image_url")
        }

        val parameters = Bundle()
        parameters.putString("fields", "first_name,last_name,email,id")
        dataRequest.parameters = parameters
        dataRequest.executeAsync()
    }

    private fun signIn() {
        var signInIntent = mGoogleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...)
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            var task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        } else {
            mFBCallbackManager!!.onActivityResult(requestCode, resultCode, data)
        }
    }

    /**
     * method to get successful/failure google login
     */
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val idToken = account!!.idToken
            val name = account.displayName
            val mail = account.email
            val id = account.id
            val expired = account.isExpired
            val url = account.photoUrl
            // Signed in successfully, show authenticated UI.
            Log.i(
                TAG,
                "signInResult:success token= $idToken , id= $id , displayName= $name , mail= $mail, expired= $expired, url= $url"
            )
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.i(TAG, """signInResult:failed code=${e.statusCode}""")
        }
    }

    @SuppressLint("NewApi")
    private fun setSpannableString() {
        // Make links in the EditText clickable
        etPhone.movementMethod = LinkMovementMethod.getInstance()
        // Setup my Spannable with clickable URLs
        val spannable = SpannableString("IN +91 | ")
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                etPhone.post {
                    Selection.setSelection(etPhone.text, etPhone.text.length)
                }
                Toast.makeText(this@SignUpActivity, "Spannable Clicked", Toast.LENGTH_SHORT).show()
            }

            override fun updateDrawState(ds: TextPaint?) {
                super.updateDrawState(ds)
                ds!!.isUnderlineText = false
                ds.bgColor = android.R.color.white
            }
        }

        spannable.setSpan(RelativeSizeSpan(1.5f), 7, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        spannable.setSpan(clickableSpan, 0, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(
            ForegroundColorSpan(resources.getColor(android.R.color.white, null)),
            0,
            spannable.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        //etPhoneNumber.setText(spannable, TextView.BufferType.NORMAL)
        //Selection.setSelection(etPhoneNumber.text, etPhoneNumber.text.length)
        //etPhoneNumber.post { etPhoneNumber.setSelection(etPhoneNumber.length()) }
        //tvCountryCode.text = spannable
        etPhone.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!s.toString().startsWith(spannable)) {
                    etPhone.setText(spannable, TextView.BufferType.SPANNABLE)
                    Selection.setSelection(etPhone.text, etPhone.text.length)
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
                // TODO Auto-generated method stub
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
    }
}