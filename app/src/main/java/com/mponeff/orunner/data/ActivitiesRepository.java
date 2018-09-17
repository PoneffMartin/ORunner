package com.mponeff.orunner.data;

import android.app.Application;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mponeff.orunner.data.entities.Activity;
import com.mponeff.orunner.data.entities.Map;
import com.mponeff.orunner.utils.DateTimeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ActivitiesRepository implements ActivitiesDataSource {

    private static final String TAG = ActivitiesRepository.class.getSimpleName();
    private Application mApp;
    private static final String FIREBASE_STORAGE_URL = "gs://orunner-85139.appspot.com";
    private ValueEventListener mValueEventListener;
    private DatabaseReference mActivitiesRef;
    private StorageReference mMapsRef;

    public ActivitiesRepository(Application application) {
        this.mApp = application;
        /** Create database and storage references */
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.mActivitiesRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userId)
                .child("activities");

        this.mMapsRef = FirebaseStorage.getInstance().getReferenceFromUrl(FIREBASE_STORAGE_URL).child(userId);

        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null && mValueEventListener != null) {
                    mActivitiesRef.removeEventListener(mValueEventListener);
                }
            }
        });
    }

    @Override
    public void getActivities(@NonNull final GetActivitiesCallback callback) {
        getActivities(callback, -1, -1);
    }

    @Override
    public void getActivities(@NonNull GetActivitiesCallback callback, int year, int month) {
        this.mValueEventListener = this.createListener(callback);

        // TODO Optimize
        if (year == -1 && month == -1) {
            this.mActivitiesRef
                    .orderByChild("startDateTime")
                    .addValueEventListener(this.mValueEventListener);
        } else {
            long startMillis = DateTimeUtils.getStartMillis(year, month);
            long endMillis = DateTimeUtils.getEndMillis(year, month);

            this.mActivitiesRef
                    .orderByChild("startDateTime")
                    .startAt(startMillis)
                    .endAt(endMillis)
                    .addValueEventListener(this.mValueEventListener);
        }
    }

    @Override
    public void saveActivity(@NonNull final Activity activity, boolean update, @NonNull final SaveActivityCallback callback) {

        if (!update) {
            String activityID = this.mActivitiesRef.push().getKey();
            activity.set_ID(activityID);
        }

        /** Upload the map first, if present */
        if (activity.getMap() != null) {
            uploadMapToStorage(activity.getMap(), new SaveActivityCallback() {
                @Override
                public void onSuccess() {
                    saveActivityToDatabase(activity, callback);
                }

                @Override
                public void onFailure() {
                    callback.onFailure();
                }
            });
        } else {
            saveActivityToDatabase(activity, callback);
        }
    }

    @Override
    public void deleteActivity(@NonNull Activity activity, DeleteActivityCallback callback) {
        /** Delete the map first, if present */
        if (activity.getMap() != null) {
            deleteMapFromStorage(activity.getMap(), new DeleteActivityCallback() {
                @Override
                public void onSuccess() {
                    deleteActivityFromDatabase(activity, callback);
                }

                @Override
                public void onFailure() {
                    callback.onFailure();
                }
            });
        }

        deleteActivityFromDatabase(activity, callback);
    }

    private void deleteActivityFromDatabase(@NonNull Activity activity, @NonNull DeleteActivityCallback callback) {
        this.mActivitiesRef.child(activity.get_ID()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    callback.onFailure();
                } else {
                    callback.onSuccess();
                }
            }
        });
    }

    private void deleteMapFromStorage(@NonNull Map map, @NonNull DeleteActivityCallback callback) {
        Uri file = Uri.parse(map.getFileUri());
        this.mMapsRef.child("maps/" + file.getLastPathSegment()).delete().addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            callback.onSuccess();
                        } else {
                            callback.onFailure();
                        }
                    }
                });
    }

    private void saveActivityToDatabase(@NonNull final Activity activity, @NonNull final SaveActivityCallback callback) {
        this.mActivitiesRef.child(activity.get_ID()).setValue(
                activity, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            callback.onFailure();
                        } else {
                            callback.onSuccess();
                        }
                    }
                });
    }

    private void uploadMapToStorage(Map map, @NonNull final SaveActivityCallback callback) {
        InputStream stream;
        File file;
        try {
            file = new File(map.getFileUri());
            stream = new FileInputStream(file);
        } catch (FileNotFoundException fne) {
            Log.e(TAG, fne.toString());
            throw new RuntimeException();
        }
        this.mMapsRef.child("maps/" + file.getName()).putStream(stream).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        callback.onFailure();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String downloadUri = taskSnapshot.getDownloadUrl().toString();
                        map.setDownloadUri(downloadUri);
                        callback.onSuccess();
                    }
                });
    }

    private ValueEventListener createListener(@NonNull final GetActivitiesCallback callback) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Activity> activities = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Activity activity = snapshot.getValue(Activity.class);
                    activities.add(activity);
                }
                callback.onSuccess(activities);
            }

            /* This callback is called both on network error and listener removal */
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error while fetching activities: " + databaseError.getMessage());
                callback.onFailure();
            }
        };
    }
}
