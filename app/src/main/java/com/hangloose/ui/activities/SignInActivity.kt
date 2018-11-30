package com.hangloose.ui.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.Observable
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.MotionEvent
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
import com.hangloose.utils.LOGIN_VALID_PASSWORD
import com.hangloose.utils.VALID_PHONE
import com.hangloose.utils.hideSoftKeyboard
import com.hangloose.utils.showSnackBar
import com.hangloose.viewmodel.ConsumerLoginViewModel
import kotlinx.android.synthetic.main.activity_sign_in.btnFacebook
import kotlinx.android.synthetic.main.activity_sign_in.btnGoogle
import kotlinx.android.synthetic.main.activity_sign_in.btnSignInFB
import kotlinx.android.synthetic.main.activity_sign_in.editPhone
import kotlinx.android.synthetic.main.activity_sign_in.ll_signin
import kotlinx.android.synthetic.main.activity_sign_in.textForgotPassword
import kotlinx.android.synthetic.main.activity_sign_in.textLayoutPassword
import kotlinx.android.synthetic.main.activity_sign_in.textLayoutPhone
import kotlinx.android.synthetic.main.activity_sign_in.textView4
import java.util.Arrays

class SignInActivity : BaseActivity(), View.OnClickListener {

    private val TAG = "SignInActivity"
    private lateinit var mConsumerLoginViewModel: ConsumerLoginViewModel
    private var mActivitySignInBinding: ActivitySignInBinding? = null
    private var mFBCallbackManager: CallbackManager? = null
    var mProfileTracker: ProfileTracker? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        intializeGoogleSignInOptions()
        signInWithFacebook()
        editPhone.setOnTouchListener(View.OnTouchListener { _, event ->
            val DRAWABLE_END = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (editPhone.right - editPhone.compoundDrawables[DRAWABLE_END].bounds.width())) {
                    editPhone.text.clear()
                    return@OnTouchListener true
                }
            }
            false
        })
    }

    override fun init() {
        btnFacebook.setOnClickListener(this)
        btnGoogle.setOnClickListener(this)
        makeSignUpClickable()
    }

    private fun makeSignUpClickable() {
        val spannable = SpannableString(resources.getString(R.string.dont_have_an_account))
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View?) {
                onNavigateToSignUpClick()
            }
        }
        spannable.setSpan(clickableSpan, 23, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView4.text = spannable
        textView4.movementMethod = LinkMovementMethod.getInstance()

        textForgotPassword.setOnClickListener(this)
    }

    private fun initBinding() {
        mActivitySignInBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)
        mActivitySignInBinding!!.clickHandler = this
        mConsumerLoginViewModel = ViewModelProviders.of(this).get(ConsumerLoginViewModel::class.java)
        mActivitySignInBinding!!.consumerLoginViewModel = mConsumerLoginViewModel
        mConsumerLoginViewModel.loginResponse()
            .observe(this, Observer<retrofit2.Response<ConsumerAuthDetailResponse>> { t ->
                Log.i(TAG, "onChanged")
                onNavigateLocationScreen()
            })
        mConsumerLoginViewModel.mShowErrorSnackBar.observe(this, Observer { t ->
            showSnackBar(
                ll_signin,
                t.toString(),
                ContextCompat.getColor(this, R.color.white),
                ContextCompat.getColor(this, R.color.colorPrimary)
            )
        })
        mConsumerLoginViewModel.isPhoneValid.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (!(sender as ObservableBoolean).get()) {
                    textLayoutPhone.error = VALID_PHONE
                } else {
                    textLayoutPhone.error = null
                }
            }
        })
        mConsumerLoginViewModel.isPasswordValid.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (!(sender as ObservableBoolean).get()) {
                    textLayoutPassword.error = LOGIN_VALID_PASSWORD
                } else {
                    textLayoutPassword.error = null
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
            textForgotPassword.id -> {
                val intent = Intent(this@SignInActivity, ForgotPasswordActivity::class.java)
                startActivity(intent)
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
            // Signed in successfully, show authenticated UI.
            mConsumerLoginViewModel.onGoogleSignInClick(
                ConsumerLoginRequest(
                    AUTH_TYPE.GOOGLE.name,
                    account!!.id,
                    account.idToken
                )
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
            val id = jsonObject.getString("id")

            mConsumerLoginViewModel.onFacebookSignInClick(
                ConsumerLoginRequest(
                    AUTH_TYPE.FACEBOOK.name,
                    accessToken.userId,
                    accessToken.token
                )
            )
        }

        val parameters = Bundle()
        parameters.putString("fields", "email")
        dataRequest.parameters = parameters
        dataRequest.executeAsync()
    }

    fun onNavigateToSignUpClick() {
        val intent = Intent(this@SignInActivity, SignUpActivity::class.java)
        startActivity(intent)
    }

    /**
     * method to dismiss keyboard on outside touch
     */
    fun onOutsideTouch(view: View) {
        hideSoftKeyboard(this)
    }

    private fun onNavigateLocationScreen() {
        var intent = Intent(this@SignInActivity, EnableLocationActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        mConsumerLoginViewModel.reset()
    }
}