package com.mponeff.orunner.activities;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.mponeff.orunner.data.entities.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class ViewMapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ZoomableMapPhoto zoomableMapPhoto = new ZoomableMapPhoto(this);

        Bundle state = checkNotNull(getIntent().getExtras());
        Map map = checkNotNull((Map) state.getParcelable("data"));

        final SimpleTarget target = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                zoomableMapPhoto.setImageBitmap(resource);
            }
        };

        Glide.with(this)
                .asBitmap()
                .load(Uri.parse(map.getDownloadUri()))
                .into(target);

        setContentView(zoomableMapPhoto);
    }

}
