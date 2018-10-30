package com.hangloose.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.Profile
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import kotlinx.android.synthetic.main.activity_sign_up.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.hangloose.R
import java.util.Arrays

class SignUpActivity : BaseActivity(), View.OnClickListener {

    private val TAG: String = "SignUpActivity"
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val RC_SIGN_IN = 9001
    private var mFBCallbackManager: CallbackManager? = null
    private var mAccessToken: AccessToken? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_client_id))
            .requestEmail()
            .build()
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    override fun init() {
        btnSignIn.setOnClickListener(this)
        signInWithFacebook()
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            btnSignIn.id -> signIn()
            btnSignInFB.id -> {
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"))
            }
        }
    }

    private fun signInWithFacebook() {
        mFBCallbackManager = CallbackManager.Factory.create()

        btnSignInFB.setReadPermissions(Arrays.asList("email"))

        btnSignInFB.registerCallback(mFBCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult?) {
                mAccessToken = loginResult!!.accessToken
                val isLogin = mAccessToken != null && !mAccessToken!!.isExpired
                val mProfile = Profile.getCurrentProfile()
                Log.i(TAG, "$isLogin, ${mProfile.firstName}")
            }

            override fun onCancel() {
            }

            override fun onError(error: FacebookException?) {
            }
        })
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
            if (mFBCallbackManager != null) {
                mFBCallbackManager!!.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val idToken = account!!.idToken
            val name = account!!.displayName
            val mail = account!!.email
            val id = account.id
            val token = account.idToken
            val expired = account.isExpired
            val url = account.photoUrl
            // Signed in successfully, show authenticated UI.
            Log.i(TAG, "signInResult:success token= $idToken")
            //updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.i(TAG, """signInResult:failed code=${e.statusCode}""")
            //updateUI(null)
        }
    }
}