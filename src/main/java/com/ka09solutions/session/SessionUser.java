package com.ka09solutions.session;

import com.google.firebase.auth.FirebaseToken;

/**
 * Created by test on 03-07-2018.
 */
public class SessionUser {

    private FirebaseToken firebaseToken;

    public SessionUser(FirebaseToken firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    public String getUserId()
    {
        return firebaseToken.getUid();
    }

    public String getEmail()
    {
        return firebaseToken.getEmail();
    }
}
