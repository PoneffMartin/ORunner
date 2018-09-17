package com.mponeff.orunner.data;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Helper methods for access to common FirebaseAPI specific classes
 */
public class FirebaseDB {

    public static String getFirebaseUserId() {
        String userId = null;
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        return userId;
    }

    public static String getFirebaseUserEmail() {
        String email = null;
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        }

        return email;
    }
}
