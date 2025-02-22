package com.nitish.privacyindicator;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.github.dhaval2404.colorpicker.ColorPickerDialog;
import com.github.dhaval2404.colorpicker.listener.ColorListener;
import com.github.dhaval2404.colorpicker.model.ColorShape;

public class FragmentHome extends Fragment {

    //Root Views
    private View root;
    private View contentServiceEnabled;
    private View contentServiceDisabled;

    private SharedPrefManager sharedPrefManager;

    private SwitchCompat mainSwitch, micSwitch, camSwitch, notifSwitch, vibSwitch;
    private RadioGroup radioGroup;
    private RadioButton rb_tl, rb_tr, rb_bl, rb_br;
    private ImageView iv_cam, iv_mic;

    @Override
    public void onResume() {
        setMainContentLayouts();
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home,container, false);

        fetchData();
        fetchViews();
        setMainContentLayouts();
        setupViews();
        setUpListeners();
        return root;
    }

    private void fetchData() {
        sharedPrefManager = SharedPrefManager.getInstance(getContext());
    }

    private void fetchViews() {
        mainSwitch = root.findViewById(R.id.main_switch);
        contentServiceEnabled = root.findViewById(R.id.content_service_enabled);
        contentServiceDisabled = root.findViewById(R.id.content_service_disabled);
        camSwitch = root.findViewById(R.id.switch_cam);
        micSwitch = root.findViewById(R.id.switch_mic);
        notifSwitch = root.findViewById(R.id.switch_notif);
        vibSwitch = root.findViewById(R.id.switch_vibration);
        radioGroup = root.findViewById(R.id.rg_location);
        rb_tl = root.findViewById(R.id.rb_tl);
        rb_tr = root.findViewById(R.id.rb_tr);
        rb_br = root.findViewById(R.id.rb_br);
        rb_bl = root.findViewById(R.id.rb_bl);
        iv_cam = root.findViewById(R.id.iv_cam_color);
        iv_mic = root.findViewById(R.id.iv_mic_color);
    }

    private void setupViews() {
        if (sharedPrefManager.isCameraIndicatorEnabled()){
            camSwitch.setChecked(true);
        }else {
            camSwitch.setChecked(false);
        }

        if (sharedPrefManager.isMicIndicatorEnabled()){
            micSwitch.setChecked(true);
        }else {
            micSwitch.setChecked(false);
        }

        if (sharedPrefManager.isVibrationEnabled()){
            vibSwitch.setChecked(true);
        }else {
            vibSwitch.setChecked(false);
        }

        if (sharedPrefManager.isNotificationEnabled()){
            notifSwitch.setChecked(true);
        }else {
            notifSwitch.setChecked(false);
        }

        setLocationRadioButton();
        setViewTint(iv_cam, sharedPrefManager.getCameraIndicatorColor());
        setViewTint(iv_mic, sharedPrefManager.getMicIndicatorColor());
    }

    private void setViewTint(ImageView imageView, String hex){
        imageView.setColorFilter(Color.parseColor(hex), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    //0-TopRight 1-BotRight 2-BotLeft 3-TopLeft
    private void setLocationRadioButton() {
        int position = sharedPrefManager.getPosition();
        if (position == 0){
            rb_tr.setChecked(true);
        }else if (position == 1){
            rb_br.setChecked(true);
        }else if (position == 2){
            rb_bl.setChecked(true);
        }else if (position == 3){
            rb_tl.setChecked(true);
        }
    }

    private void setMainContentLayouts() {
        if (isAccessibilityEnabled()){
            mainSwitch.setChecked(true);
            mainSwitch.setText("Enabled");
            contentServiceEnabled.setVisibility(View.VISIBLE);
            contentServiceDisabled.setVisibility(View.GONE);
        }
        else {
            mainSwitch.setChecked(false);
            mainSwitch.setText("Disabled");
            contentServiceEnabled.setVisibility(View.GONE);
            contentServiceDisabled.setVisibility(View.VISIBLE);
        }
    }

    private void setUpListeners() {
        mainSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            }
        });

        camSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
        micSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
        vibSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
        notifSwitch.setOnCheckedChangeListener(onCheckedChangeListener);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                //0-TopRight 1-BotRight 2-BotLeft 3-TopLeft
                if (i == R.id.rb_tr) {
                    sharedPrefManager.setPosition(0);
                } else if (i == R.id.rb_br) {
                    sharedPrefManager.setPosition(1);
                }else if (i == R.id.rb_bl) {
                    sharedPrefManager.setPosition(2);
                }else if (i == R.id.rb_tl) {
                    sharedPrefManager.setPosition(3);
                }
            }
        });

        iv_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorListener colorListener = new ColorListener() {
                    @Override
                    public void onColorSelected(int i, String s) {
                        sharedPrefManager.setCameraIndicatorColor(s);
                        setViewTint(iv_cam, s);
                    }
                };
                new ColorPickerDialog.Builder(getContext(),
                        "Camera Indicator Color",
                        "ok", "cancel",
                        colorListener,
                        sharedPrefManager.getCameraIndicatorColor(),
                        ColorShape.CIRCLE).show();
            }
        });

        iv_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorListener colorListener = new ColorListener() {
                    @Override
                    public void onColorSelected(int i, String s) {
                        sharedPrefManager.setMicIndicatorColor(s);
                        setViewTint(iv_mic, s);
                    }
                };
                new ColorPickerDialog.Builder(getContext(),
                        "Microphone Indicator Color",
                        "ok", "cancel",
                        colorListener,
                        sharedPrefManager.getMicIndicatorColor(),
                        ColorShape.CIRCLE).show();
            }
        });
    }

    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (compoundButton.equals(camSwitch)) sharedPrefManager.setCameraIndicatorEnabled(b);
            if (compoundButton.equals(micSwitch)) sharedPrefManager.setMicIndicatorEnabled(b);
            if (compoundButton.equals(vibSwitch)) sharedPrefManager.setVibrationEnabled(b);
            if (compoundButton.equals(notifSwitch)) sharedPrefManager.setNotificationEnabled(b);
        }
    };

    private boolean isAccessibilityEnabled() {
        String LOGTAG = "ACCESSIBILITY_ERROR";
        int accessibilityEnabled = 0;
        final String ACCESSIBILITY_SERVICE = "com.nitish.privacyindicator/com.nitish.privacyindicator.IndicatorService";
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(getActivity().getContentResolver(),android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            Log.d(LOGTAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
        }

        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled==1) {

            String settingValue = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            Log.d(LOGTAG, "Setting: " + settingValue);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();
                    if (accessabilityService.equalsIgnoreCase(ACCESSIBILITY_SERVICE)){
                        return true;
                    }
                }
            }
        }
        else {
            Log.d(LOGTAG, "***ACCESSIBILIY IS DISABLED***");
        }
        return accessibilityFound;
    }
}
