package com.orelzman.auth.domain.model

import com.google.firebase.auth.FirebaseUser


class User {
    var firebaseUser: FirebaseUser? = null

    /**
     * In the future more authentications will be added so there will be more constructors
     */
    @Suppress("ConvertSecondaryConstructorToPrimary")
    constructor(firebaseUser: FirebaseUser? = null) {
        this.firebaseUser = firebaseUser
    }

}