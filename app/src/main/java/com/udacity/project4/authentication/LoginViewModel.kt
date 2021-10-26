package com.udacity.project4.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

class LoginViewModel : ViewModel() {

    // encapsulation
    val authenticationState: LiveData<AuthenticationState>
        get()= _authenticationState

    //create authState variable based on the FirebaseUserLiveData object
    private var _authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }

    }

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED
    }

}