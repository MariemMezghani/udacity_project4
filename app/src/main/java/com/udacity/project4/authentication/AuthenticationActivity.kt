package com.udacity.project4.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity
import com.google.firebase.auth.FirebaseAuth
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse


/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationBinding

    //Get a reference to the viewmodel scoped to this activity
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.button.setOnClickListener{launchSignIn()}
        observeAuthenticationState()
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


    // observes authentication state and make changes accordingly
    private fun observeAuthenticationState() {
        viewModel.authenticationState.observe(this, Observer { authenticationState ->
            when (authenticationState) {
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {

                    val intent = Intent(this, RemindersActivity::class.java)
                    startActivity(intent)

                }
                else -> {
                    //launchSignIn()

                }
            }
        })
    }

    private fun launchSignIn() {
        //give users the option to sign in or register with email/google account
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
        )

        // create and launch sign-in intent
        //we listen to the response with SIGH_IN_REQUEST_CODE
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().
            setTheme(R.style.LoginTheme)
                .setAvailableProviders(providers).build(),
            AuthenticationActivity.SIGN_IN_REQUEST_CODE
        )

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            // we start by getting the result of our resulting intent
            val respnse = IdpResponse.fromResultIntent(data)
            // then we check the resultCode to see what the result of the login was
            if (resultCode == Activity.RESULT_OK) {
                Log.i(TAG, "Successfully signed in user ${FirebaseAuth.getInstance().currentUser?.displayName}")
            } else {
                // if the response is null, the user canceled the sign in flow by pressing the back button.
                // otherwise check the error code to handle it
                Log.i(TAG, "sign in unsuccessfull ${respnse?.error?.errorCode}")

            }
        }
    }

    companion object {
        const val TAG = "AuthenticationActivity"
        const val SIGN_IN_REQUEST_CODE = 1001
    }
    }

