package com.hangloose.ui.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
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
import com.hangloose.databinding.ActivitySignInBinding
import com.hangloose.model.ConsumerAuthDetailResponse
import com.hangloose.model.ConsumerLoginRequest
import com.hangloose.utils.AUTH_TYPE
import com.hangloose.viewmodel.ConsumerViewModel
import kotlinx.android.synthetic.main.activity_sign_in.*
import java.util.Arrays

class SignInActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = "SignInActivity"
    private var consumerViewModel: ConsumerViewModel = ConsumerViewModel()
    private var activitySignInBinding: ActivitySignInBinding? = null
    private var mFBCallbackManager: CallbackManager? = null
    var mProfileTracker: ProfileTracker? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        initBinding()
        btnFacebook.setOnClickListener(this)
        btnGoogle.setOnClickListener(this)
        intializeGoogleSignInOptions()
        signInWithFacebook()
    }

    private fun initBinding() {
        activitySignInBinding = DataBindingUtil.setContentView<ActivitySignInBinding>(this, R.layout.activity_sign_in)
        activitySignInBinding!!.consumerViewModel = consumerViewModel

        consumerViewModel = ViewModelProviders.of(this).get(ConsumerViewModel::class.java)

        consumerViewModel.loginResponse()?.observe(this, Observer<ConsumerAuthDetailResponse> {
            fun onChanged(t: ConsumerAuthDetailResponse?) {
                if (t != null) {
                    Log.i(TAG, "onChanged")
                }
            }
        })
    }

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
        mFBCallbackManager = CallbackManager.Factory.create()

        btnSignInFB.setReadPermissions(Arrays.asList("email", "public_profile"))

        btnSignInFB.registerCallback(mFBCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult?) {
                Log.i(TAG, "onSuccess")

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

    override fun onClick(view: View?) {
        when (view!!.id) {
            btnFacebook.id -> {
                btnSignInFB.performClick()
            }
            btnGoogle.id -> {
                val signInIntent = mGoogleSignInClient?.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...)
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
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

    fun getFacebookUserProfile(accessToken: AccessToken) {
        val dataRequest = GraphRequest.newMeRequest(
            accessToken
        ) { jsonObject, response ->
            val email = jsonObject.getString("email")

            consumerViewModel.onFacebookSignInClick(
                ConsumerLoginRequest(
                    AUTH_TYPE.FACEBOOK.name,
                    email,
                    accessToken.token
                )
            )
        }

        val parameters = Bundle()
        parameters.putString("fields", "email")
        dataRequest.parameters = parameters
        dataRequest.executeAsync()
    }

    override fun onDestroy() {
        super.onDestroy()
        consumerViewModel.reset()
    }
}