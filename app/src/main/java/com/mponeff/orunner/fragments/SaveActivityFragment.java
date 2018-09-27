package com.mponeff.orunner.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mponeff.orunner.R;
import com.mponeff.orunner.data.entities.Activity;
import com.mponeff.orunner.data.entities.Map;
import com.mponeff.orunner.utils.Calculations;
import com.mponeff.orunner.utils.DateTimeUtils;
import com.mponeff.orunner.viewmodels.ActivitiesModel;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.google.common.base.Preconditions.checkNotNull;

public class SaveActivityFragment extends Fragment implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {
    private static final String TAG = SaveActivityFragment.class.getSimpleName();
    private static final int DURATION_INPUT_LIMIT = 8;
    private static final int PICK_PHOTO_CODE = 1046;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PERMISSIONS_CODE = 3;

    /* Common views*/
    @BindView(R.id.main_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.tv_date_picker)
    TextView mTvDatePicker;
    @BindView(R.id.tv_time_picker)
    TextView mTvTimePicker;
    @BindView(R.id.title_ti_layout)
    TextInputLayout mTiTitleLayout;
    @BindView(R.id.et_title)
    EditText mEtTitle;
    @BindView(R.id.location_ti_layout)
    TextInputLayout mTiLocationLayout;
    @BindView(R.id.et_location)
    EditText mEtLocation;
    @BindView(R.id.tv_distance_title)
    TextView mTvDistanceTitle;
    @BindView(R.id.et_distance)
    EditText mEtDistance;
    @BindView(R.id.et_duration)
    EditText mEtDuration;
    @BindView(R.id.tv_total_pace)
    TextView mTvPace;
    @BindView(R.id.controls_ti_layout)
    TextInputLayout mTiControlsLayout;
    @BindView(R.id.et_controls)
    EditText mEtControls;
    @BindView(R.id.ll_add_map)
    LinearLayout mLlAddMap;
    @BindView(R.id.iv_map)
    ImageView mIvMap;
    @BindView(R.id.ib_clear_map)
    ImageButton mIbClearMap;
    @BindView(R.id.ib_select_photo)
    ImageButton mIbAddMapFromStorage;
    @BindView(R.id.ib_take_photo)
    ImageButton mIbTakePhotoOfMap;
    @BindView(R.id.et_details)
    EditText mEtDetails;
    @BindView(R.id.btn_save)
    Button mBtnSave;

    /* Competition views */
    @BindView(R.id.ll_class)
    LinearLayout mLlClass;
    @BindView(R.id.ll_winning_time)
    LinearLayout mLlWinningTime;
    @BindView(R.id.winning_time_ti_layout)
    TextInputLayout mTiWinningTimeLayout;
    @BindView(R.id.et_winning_time)
    EditText mEtWinningTime;
    @BindView(R.id.classes_spinner)
    Spinner mClassesSpinner;
    @BindView(R.id.ll_position_view)
    LinearLayout mLlPositionView;
    @BindView(R.id.et_position)
    EditText mEtPosition;
    @BindView(R.id.tv_time_diff)
    TextView mTvTimeDiff;
    @BindView(R.id.iv_medal)
    ImageView mIvMedal;
    @BindView(R.id.vertical_divider_competition_1)
    View mVerticalDivider_1;
    @BindView(R.id.vertical_divider_competition_2)
    View mVerticalDivider_2;
    @BindView(R.id.horizontal_divider_competition_1)
    View mHorizontalDivider_1;

    private ProgressDialog mProgressDialog;
    private Activity mActivity;
    private String mType;
    private String mMode;
    private String mTitle;
    private String mLocation;
    private String mDetails;
    private String mClass;
    private String mCurrentPhotoPath;
    private String mPace;
    private int mPosition = 0;
    private int mControls = 0;
    private float mDistance = 0;
    private long mDuration = 0;
    private long mWinningTime = 0;

    private ArrayAdapter<String> mSpinnerAdapter;
    private boolean mIsMapPhotoSet = false;

    private Uri mMapPhotoURI;
    private ActivitiesModel mActivitiesModel;
    private StringBuilder mDurationDigits;
    private StringBuilder mWinningTimeDigits;

    private boolean mIsTitleFilled = false;
    private boolean mIsLocationFilled = false;
    private boolean mIsDistanceFilled = false;
    private boolean mIsDurationFilled = false;
    private boolean mIsControlsFilled = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mActivitiesModel = ViewModelProviders.of(this).get(ActivitiesModel.class);
        mDurationDigits = new StringBuilder("");
        mWinningTimeDigits = new StringBuilder("");
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_save_activity, container, false);
        ButterKnife.bind(this, rootView);

        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.setSupportActionBar(mToolbar);
        appCompatActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbarTitle.setText(R.string.save_activity);
        mToolbar.setNavigationIcon(R.drawable.ic_clear_map);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        /** Hide the soft input keyboard by default */
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mMode = checkNotNull(getArguments().getString("mode", "Save"));
        mType = checkNotNull(getArguments().getString("type", getString(R.string.type_training)));

        setDateTimeView(Calendar.getInstance().getTimeInMillis());

        mTvTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        mTvDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        mEtTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                if (text.length() == 0) {
                    mIsTitleFilled = false;
                    mTiTitleLayout.setErrorEnabled(false);
                } else if (text.length() > 0 && text.length() < 3) {
                    mTiTitleLayout.setError("Title should be at least 3 characters");
                    mTiTitleLayout.setErrorEnabled(true);
                } else {
                    mTiTitleLayout.setErrorEnabled(false);
                    mTitle = text.toString();
                    mIsTitleFilled = true;
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mEtLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                if (text.length() == 0) {
                    mIsLocationFilled = false;
                    mTiLocationLayout.setErrorEnabled(false);
                } else if (text.length() > 0 && text.length() < 3) {
                    mTiLocationLayout.setError("Location should be at least 3 characters");
                    mTiLocationLayout.setErrorEnabled(true);
                } else {
                    mTiLocationLayout.setErrorEnabled(false);
                    mLocation = text.toString();
                    mIsLocationFilled = true;
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mEtDistance.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                if (text.length() == 0) {
                    setPaceTextView(mDistance, mDuration);
                    mIsDistanceFilled = false;
                } else if (text.length() > 0) {
                    mDistance = Float.parseFloat(text.toString().trim());
                    mIsDistanceFilled = true;
                    setPaceTextView(mDistance, mDuration);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mEtDuration.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                mEtDuration.setSelection(text.length());
                if (text.length() == 1) {
                    mDurationDigits = new StringBuilder(text.toString());
                } else if (text.length() > 1 && text.length() < 8) {
                    // Set maxLength to 9
                    mEtDuration.setFilters(new InputFilter[]
                            {new InputFilter.LengthFilter(DURATION_INPUT_LIMIT + 1)});
                    if (start == 7) {
                        mDurationDigits.deleteCharAt(mDurationDigits.length() - 1);
                    } else if (start == 6 && mDurationDigits.length() > 1) {
                        mDurationDigits.deleteCharAt(mDurationDigits.length() - 2);
                    } else if (start == 4 && mDurationDigits.length() > 2) {
                        mDurationDigits.deleteCharAt(mDurationDigits.length() - 3);
                    } else if (start == 3 && mDurationDigits.length() > 3) {
                        mDurationDigits.deleteCharAt(mDurationDigits.length() - 4);
                    } else if (start == 1 && mDurationDigits.length() > 4) {
                        mDurationDigits.deleteCharAt(mDurationDigits.length() - 5);
                    } else if (start == 0 && mDurationDigits.length() > 5) {
                        mDurationDigits.deleteCharAt(mDurationDigits.length() - 6);
                    }
                } else if (text.length() > 8) {
                    mDurationDigits.append(text.charAt(start));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int len = s.toString().length();
                if (len == 0) {
                    mIsDurationFilled = false;
                    setPaceTextView(mDistance, 0L);
                } else if (len != DURATION_INPUT_LIMIT) {
                    mEtDuration.setText(getDurationViewFmt(mDurationDigits.toString()));
                } else {
                    if (mDurationDigits.length() == 6) {
                        mEtDuration.setFilters(new InputFilter[]
                                {new InputFilter.LengthFilter(DURATION_INPUT_LIMIT)});
                    }

                    mDuration = DateTimeUtils.convertTimeStringToSeconds(s.toString());
                    mIsDurationFilled = true;
                    setPaceTextView(mDistance, mDuration);
                    setWinningTimeError(mWinningTime, mDuration);
                }
            }
        });

        mEtControls.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                if (text.toString().startsWith("0")) {
                    mEtControls.setText("");
                } else if (text.toString().length() == 0) {
                    mIsControlsFilled = false;
                } else if (text.toString().length() > 0) {
                    mControls = Integer.parseInt(text.toString().trim());
                    mIsControlsFilled = true;
                    mTiControlsLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        if (mType.equals(getString(R.string.type_competition))) {
            showCompetitionViews();

            List<String> ageClasses = Arrays.asList(getResources().getStringArray(R.array.classes));
            mSpinnerAdapter = new ArrayAdapter<>(getContext(),
                    R.layout.age_class_spinner_single_item, ageClasses);
            mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mClassesSpinner.setAdapter(mSpinnerAdapter);
            mClassesSpinner.setSelection(7);
            mClassesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    mClass = (String) mClassesSpinner.getSelectedItem();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            mEtWinningTime.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence text, int start, int before, int count) {
                    mEtWinningTime.setSelection(text.length());
                    if (text.length() == 1) {
                        mWinningTimeDigits = new StringBuilder(text.toString());
                    } else if (text.length() > 1 && text.length() < 8) {
                        // Set maxLength to 9
                        mEtWinningTime.setFilters(new InputFilter[]
                                {new InputFilter.LengthFilter(DURATION_INPUT_LIMIT + 1)});
                        if (start == 7) {
                            mWinningTimeDigits.deleteCharAt(mWinningTimeDigits.length() - 1);
                        } else if (start == 6 && mWinningTimeDigits.length() > 1) {
                            mWinningTimeDigits.deleteCharAt(mWinningTimeDigits.length() - 2);
                        } else if (start == 4 && mWinningTimeDigits.length() > 2) {
                            mWinningTimeDigits.deleteCharAt(mWinningTimeDigits.length() - 3);
                        } else if (start == 3 && mWinningTimeDigits.length() > 3) {
                            mWinningTimeDigits.deleteCharAt(mWinningTimeDigits.length() - 4);
                        } else if (start == 1 && mWinningTimeDigits.length() > 4) {
                            mWinningTimeDigits.deleteCharAt(mWinningTimeDigits.length() - 5);
                        } else if (start == 0 && mWinningTimeDigits.length() > 5) {
                            mWinningTimeDigits.deleteCharAt(mWinningTimeDigits.length() - 6);
                        }
                    } else if (text.length() > 8) {
                        mWinningTimeDigits.append(text.charAt(start));
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {

                    int len = s.toString().length();
                    if (len == 0) {
                        mTiWinningTimeLayout.setErrorEnabled(false);
                        mTvTimeDiff.setText("");
                    } else if (len != DURATION_INPUT_LIMIT) {
                        mEtWinningTime.setText(getDurationViewFmt(mWinningTimeDigits.toString()));
                    } else {
                        if (mWinningTimeDigits.length() == 6) {
                            mEtWinningTime.setFilters(new InputFilter[]
                                    {new InputFilter.LengthFilter(DURATION_INPUT_LIMIT)});
                        }

                        mWinningTime = DateTimeUtils.convertTimeStringToSeconds(s.toString());
                        setWinningTimeError(mWinningTime, mDuration);
                    }
                }
            });

            mEtPosition.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence text, int start, int before, int count) {
                    if (text.toString().startsWith("0")) {
                        mEtPosition.setText("");
                    } else if (text.length() > 0) {
                        mPosition = Integer.parseInt(text.toString().trim());
                        switch (mPosition) {
                            case 1:
                                mIvMedal.setImageResource(R.drawable.first_place);
                                break;
                            case 2:
                                mIvMedal.setImageResource(R.drawable.second_place);
                                break;
                            case 3:
                                mIvMedal.setImageResource(R.drawable.third_place);
                                break;
                            default:
                                mIvMedal.setImageResource(0);
                                break;
                        }
                    } else {
                        mIvMedal.setImageResource(0);
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }

        mEtDetails.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_SCROLL:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        return true;
                }

                return false;
            }
        });

        mEtDetails.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                if (text.length() > 0) {
                    mDetails = text.toString().trim();
                } else {
                    mDetails = "";
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mIbAddMapFromStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickMap();
            }
        });

        mIbTakePhotoOfMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasPermissions()) {
                    dispatchTakePictureIntent();
                }
            }
        });

        mActivity = new Activity();
        if (mMode.equals(getResources().getString(R.string.edit_mode))) {

            mActivity = checkNotNull(getArguments().getParcelable("data"));

            /* Set initial values to all views */
            setDateTimeView(mActivity.getStartDateTime());
            setDurationView(mActivity.getDuration());
            mEtTitle.setText(mActivity.getTitle());
            mEtLocation.setText(mActivity.getLocation());
            mEtControls.setText(String.valueOf(mActivity.getControls()));
            mEtDistance.setText(String.format(Locale.US, "%.1f", mActivity.getDistance()));
            mTvPace.setText(mActivity.getPace());

            if (mType.equals(getString(R.string.type_competition))) {
                setWinningTimeView(mActivity.getWinningTime());
                mClassesSpinner.setSelection(mSpinnerAdapter.getPosition(mActivity.getAgeGroup()));
                mEtPosition.setText(String.valueOf(mActivity.getPosition()));
                mTvTimeDiff.setText(DateTimeUtils.getTimeDiff(mWinningTime, mDuration));
            }

            if (mActivity.getMap() != null) {
                String downloadUri = mActivity.getMap().getDownloadUri();
                /**
                 * Consider loading a map from the phone storage if it is available, for the future.
                 * Now use only downloading from the cloud storage.
                 */
                if (downloadUri != null) {
                    loadMapImage(Uri.parse(downloadUri));
                }
            }

            mEtDetails.setText(mActivity.getDetails());
        }

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
                Date date = new Date();
                try {
                    date = format.parse(mTvDatePicker.getText().toString() + " "
                            + mTvTimePicker.getText().toString());
                } catch (ParseException e) {
                    /* Exception here is not expected */
                    throw new RuntimeException();
                }

                mActivity.setType(mType);
                mActivity.setStartDateTime(date.getTime());
                mActivity.setTitle(mTitle);
                mActivity.setLocation(mLocation);
                mActivity.setDistance(mDistance);
                mActivity.setDuration(mDuration);
                mActivity.setControls(mControls);
                mActivity.setPace(mPace);
                mActivity.setDetails(mDetails);

                if (mType.equals(getString(R.string.type_competition))) {
                    mActivity.setAgeGroup(mClass);
                    mActivity.setPosition(mPosition);
                    mActivity.setWinningTime(mWinningTime);
                }

                if (mIsMapPhotoSet) {
                    Map activityMap = new Map();
                    activityMap.setFileUri(mCurrentPhotoPath);
                    activityMap.setLocation(mActivity.getLocation());
                    activityMap.setTitle(mActivity.getTitle());
                    activityMap.setDate(mActivity.getStartDateTime());
                    mActivity.setMap(activityMap);
                }

                if (mMode.equalsIgnoreCase(getResources().getString(R.string.save_mode))) {
                    saveActivity(mActivity, false);
                } else {
                    saveActivity(mActivity, true);
                }
            }
        });

        return rootView;
    }

    private void pickMap() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File mapPhotoFile = null;
            try {
                mapPhotoFile = createImageFile(mActivity.getTitle());
            } catch (IOException ex) {
                Log.e(TAG, ex.toString()); // TODO What to do on exception
            }

            if (mapPhotoFile != null) {
                mMapPhotoURI = FileProvider.getUriForFile(getContext(),
                        getString(R.string.fileprovider_authorities),
                        mapPhotoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMapPhotoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private boolean hasPermissions() {
        List<String> permissionsList = new ArrayList<>();
        if (getActivity().checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.CAMERA);

        }
        if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permissionsList.isEmpty()) {
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_PERMISSIONS_CODE);
            return false;
        }
        return true;
    }
    private boolean checkRequiredFields() {
        return mIsTitleFilled && mIsLocationFilled && mIsDistanceFilled && mIsDurationFilled && mIsControlsFilled;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            mIsMapPhotoSet = true;
            loadMapImage(mMapPhotoURI);
        } else if (requestCode == PICK_PHOTO_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            mIsMapPhotoSet = true;
            Log.e(TAG, "Photo choosen: fileUri: " + data.getData().toString());
            mCurrentPhotoPath = getRealPathFromUri(getContext(), data.getData());
            loadMapImage(data.getData());
        }

        Log.e(TAG, mCurrentPhotoPath);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            if (grantResults.length > 0) {
                boolean granted = true;
                for(int res : grantResults) {
                    if (res == PackageManager.PERMISSION_DENIED) {
                        granted = false;
                        break;
                    }
                }

                if (granted) {
                    Log.e(TAG, "Permission granted");
                    dispatchTakePictureIntent();
                } else {
                    Log.e(TAG, "Permission not granted"); // TODO What to do???
                }
            }
        }
    }

    private File createImageFile(String fileName) throws IOException {
        String timeStamp = new Date().toString();
        String imageFileName = String.format("%s_%s", fileName, timeStamp);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private String getRealPathFromUri(Context context, Uri fileUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(fileUri, proj, null, null, null);
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(columnIndex);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void showProgress() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("Saving activity...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void showActivitySavedMessage(String message) {
        /* TODO What happens after successful saving */
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        Handler delay = new Handler();
        delay.postDelayed(new Runnable() {
            @Override
            public void run() {
                getActivity().finish();
            }
        }, 1000);
    }

    private void setWinningTimeError(long winningTimeSeconds, long durationSeconds) {
        if (durationSeconds > 0 && winningTimeSeconds > 0) {
            if (winningTimeSeconds > durationSeconds) {
                mTiWinningTimeLayout.setError("Invalid winning time");
                mTiWinningTimeLayout.setErrorEnabled(true);
                mTvTimeDiff.setText("");
            } else {
                mTiWinningTimeLayout.setErrorEnabled(false);
                mWinningTime = winningTimeSeconds;
                mTvTimeDiff.setText(DateTimeUtils.getTimeDiff(winningTimeSeconds, durationSeconds));
            }
        }
    }

    private String getDurationViewFmt(String digits) {
        if (digits.length() == 0) {
            return "";
        } else if (digits.length() == 1) {
            return String.format("00:00:0%s", digits);
        } else if (digits.length() == 2) {
            return String.format("00:00:%s", digits);
        } else if (digits.length() == 3) {
            return String.format("00:0%s:%s",
                    digits.charAt(0),
                    digits.subSequence(1, 3));
        } else if (digits.length() == 4) {
            return String.format("00:%s:%s",
                    digits.subSequence(0, 2),
                    digits.subSequence(2, 4));
        } else if (digits.length() == 5) {
            return String.format("0%s:%s:%s",
                    digits.charAt(0), digits.subSequence(1, 3),
                    digits.subSequence(3, 5));
        } else if (digits.length() == 6) {
            return String.format("%s:%s:%s",
                    digits.subSequence(0, 2),
                    digits.subSequence(2, 4),
                    digits.subSequence(4, 6));
        } else {
            return null;
        }
    }

    private void loadMapImage(Uri imageUri) {
        showMapView();
        Glide.with(this)
                .load(imageUri)
                .into(mIvMap);
        setClearButton();
    }

    private void showMapView() {
        mLlAddMap.setVisibility(View.GONE);
        mIvMap.setVisibility(View.VISIBLE);
    }

    private void hideMapView() {
        mIvMap.setVisibility(View.INVISIBLE);
        mLlAddMap.setVisibility(View.VISIBLE);
    }

    private void setClearButton() {
        mIbClearMap.setVisibility(View.VISIBLE);
        mIbClearMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "Clear map click");
                hideMapView();
                mIbClearMap.setVisibility(View.INVISIBLE);
                mIsMapPhotoSet = false;
                mMapPhotoURI = null;
                // Clear the click listener
                mIbClearMap.setOnClickListener(null);
            }
        });
    }

    private void saveActivity(Activity activity, boolean update) {
        showProgress();
        mActivitiesModel.saveActivity(activity, update);
        mActivitiesModel.isActivitySaved().observe(this, saved -> {
            Log.e(TAG, "Value of bool livedata has changes to " + saved);
            if (saved) {
                showActivitySavedMessage("Activity saved.");
            } else {
                showActivitySavedMessage("Failed to save activity.");
            }
            hideProgress();
        });
    }

    private void setWinningTimeView(long seconds) {
        char[] secondsArr = DateTimeUtils.convertSecondsToTimeString(seconds).replace(":","").toCharArray();
        mWinningTimeDigits.append(secondsArr);
        /* Delete all leading zeroes if any */
        int i = 0;
        while(i < mWinningTimeDigits.length() && mWinningTimeDigits.charAt(i) == '0') {
            i++;
        }
        mWinningTimeDigits.delete(0, i);
        mEtWinningTime.setText(mWinningTimeDigits);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String startDate = String.format(new Locale("bg", "BG"), "%02d-%s-%d", dayOfMonth,
                DateTimeUtils.getMonthShortName(monthOfYear), year);
        mTvDatePicker.setText(startDate);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String startTime = String.format(new Locale("bg", "BG"), "%02d:%02d", hourOfDay, minute);
        mTvTimePicker.setText(startTime);
    }

    private void setPaceTextView(float distance, long duration) {
        mPace = Calculations.calculatePace(distance, duration).trim();
        mTvPace.setText(mPace);
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                this,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        /* Do not allow future time choice */
        timePickerDialog.setMaxTime(
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND));
        timePickerDialog.show(getActivity().getFragmentManager(), "Timepicker");
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        /* Do not allow future date choice */
        datePickerDialog.setMaxDate(calendar);
        datePickerDialog.show(getActivity().getFragmentManager(), "Datepicker");
    }

    private void setDateTimeView(long startDateTimeMillis) {

        Calendar startDateTime = Calendar.getInstance();
        startDateTime.setTimeInMillis(startDateTimeMillis);
        int hourOfDay = startDateTime.get(Calendar.HOUR_OF_DAY);
        int minutes = startDateTime.get(Calendar.MINUTE);
        int month = startDateTime.get(Calendar.MONTH);
        int dayOfMonth = startDateTime.get(Calendar.DAY_OF_MONTH);
        int year = startDateTime.get(Calendar.YEAR);

        mTvDatePicker.setText(String.format(new Locale("bg", "BG"), "%02d-%s-%d", dayOfMonth,
                DateTimeUtils.getMonthShortName(month), year));
        mTvTimePicker.setText(String.format("%02d:%02d", hourOfDay, minutes));
    }

    private void setDurationView(long seconds) {
        char[] secondsArr = DateTimeUtils.convertSecondsToTimeString(seconds).replace(":","").toCharArray();
        mDurationDigits.append(secondsArr);
        /* Delete all leading zeroes if any */
        int i = 0;
        while(i < mDurationDigits.length() && mDurationDigits.charAt(i) == '0') {
           i++;
        }
        mDurationDigits.delete(0, i);
        mEtDuration.setText(mDurationDigits);
    }

    private void showCompetitionViews() {
        mVerticalDivider_1.setVisibility(View.VISIBLE);
        mVerticalDivider_2.setVisibility(View.VISIBLE);
        mHorizontalDivider_1.setVisibility(View.VISIBLE);
        mLlClass.setVisibility(View.VISIBLE);
        mLlWinningTime.setVisibility(View.VISIBLE);
        mLlPositionView.setVisibility(View.VISIBLE);
    }

}
