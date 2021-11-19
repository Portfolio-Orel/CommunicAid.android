package com.orelzman.auth.domain.model

import com.google.firebase.auth.FirebaseUser

class User {

    companion object {
        fun firebaseUserToUser(firebaseUser: FirebaseUser?): User {
            return User()
        }
    }
}