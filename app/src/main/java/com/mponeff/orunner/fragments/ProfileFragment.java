package com.mponeff.orunner.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mponeff.orunner.R;
import com.mponeff.orunner.data.entities.Total;
import com.mponeff.orunner.utils.DateTimeUtils;
import com.mponeff.orunner.viewmodels.TotalsViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    @BindView(R.id.tv_log_out)
    TextView mTvLogOut;
    @BindView(R.id.profile_picture_placeholder)
    TextView mProfilePicPlaceholder;
    @BindView(R.id.tv_email)
    TextView mEmailTv;
    @BindView(R.id.iv_profile_pic)
    CircleImageView mProfileImage;
    @BindView(R.id.tv_activities)
    TextView mTvActivities;
    @BindView(R.id.tv_distance)
    TextView mTvDistance;
    @BindView(R.id.tv_duration)
    TextView mTvDuration;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TotalsViewModel totalsViewModel = ViewModelProviders.of(this).get(TotalsViewModel.class);
        totalsViewModel.getTotals().observe(this, totals -> {
            this.showTotals(totals);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, rootView);

        setProfileInfo();
        /* TODO Use real values */
        mTvActivities.setText("0");
        mTvDistance.setText("0.0");
        mTvDuration.setText("00:00:00");
        /* End of TODO */
        mTvLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
            }
        });

        return rootView;
    }

    private void setProfileInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Uri userPhoto = user.getPhotoUrl();
            String email = user.getEmail();
            mProfilePicPlaceholder.setText(String.valueOf(email.charAt(0))); // TODO Consider exception handling
            mEmailTv.setText(email);
            if (userPhoto != null) {
                Glide.with(this)
                        .load(userPhoto)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                mProfilePicPlaceholder.setVisibility(View.GONE);
                                mProfileImage.setVisibility(View.VISIBLE);
                                return false;
                            }
                        })
                        .into(mProfileImage);
            }
        }
    }

    private void showTotals(Total totals) {
        mTvActivities.setText(String.valueOf(totals.getActivities()));
        mTvDistance.setText(String.format("%.1f", totals.getDistance()));
        mTvDuration.setText(DateTimeUtils.convertSecondsToTimeString(totals.getDuration()));
    }
}
