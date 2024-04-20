package com.example.stepcounter;

import static androidx.core.app.ActivityCompat.recreate;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MeasuresFragment extends Fragment {
    TextView res;
    android.widget.Button resBtn;
    TextView mCurrentHeight, mCurrentAge, mCurrentWeight;
    ImageView mIncrementWeight, mDecrementWeight, mIncrementAge, mDecrementAge;
    SeekBar mSeekBarHeight;
    RelativeLayout mMale, mFemale;
    int intWeight=65;
    int intAge=22;
    int currentProgress;
    String mIntProgress="170";
    String typeOfUser="0";
    String weight="65";
    String age="22";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_measures, container, false);

        res = rootView.findViewById(R.id.resultBMI);
        resBtn = rootView.findViewById(R.id.calculateBMI);
        mCurrentAge = rootView.findViewById(R.id.currentAge);
        mCurrentWeight = rootView.findViewById(R.id.currentWeight);
        mCurrentHeight = rootView.findViewById(R.id.currentHeight);
        mIncrementAge = rootView.findViewById(R.id.incrementAge);
        mDecrementAge = rootView.findViewById(R.id.decrementAge);
        mIncrementWeight = rootView.findViewById(R.id.incrementWeight);
        mDecrementWeight = rootView.findViewById(R.id.decrementWeight);
        mSeekBarHeight = rootView.findViewById(R.id.seekBarHeight);
        mMale = rootView.findViewById(R.id.male);
        mFemale = rootView.findViewById(R.id.female);

        Button btnChangeLanguage = rootView.findViewById(R.id.changeLanguageButton);
        btnChangeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLanguageSelectionDialog();
            }
        });

        mMale.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mMale.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.malefemalefocus));
                mFemale.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.malefemalenotfocus));
                typeOfUser="Male";
            }
        });

        mFemale.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mFemale.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.malefemalefocus));
                mMale.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.malefemalenotfocus));
                typeOfUser="Female";
            }
        });

        mSeekBarHeight.setMax(300);
        mSeekBarHeight.setProgress(170);
        mSeekBarHeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentProgress=progress;
                mIntProgress=String.valueOf(currentProgress);
                mCurrentHeight.setText(mIntProgress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mIncrementAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intAge=intAge+1;
                age=String.valueOf(intAge);
                mCurrentAge.setText(age);
            }
        });

        mDecrementAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intAge=intAge-1;
                age=String.valueOf(intAge);
                mCurrentAge.setText(age);
            }
        });

        mIncrementWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intWeight=intWeight+1;
                weight=String.valueOf(intWeight);
                mCurrentWeight.setText(weight);
            }
        });

        mDecrementWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intWeight=intWeight-1;
                weight=String.valueOf(intWeight);
                mCurrentWeight.setText(weight);
            }
        });

        resBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (typeOfUser.equals("0")){
                    Toast.makeText(getContext(),"Select Your Gender First !",Toast.LENGTH_SHORT).show();
                }
                else if (mIntProgress.equals("0")){
                    Toast.makeText(getContext(),"Select Your Height First !",Toast.LENGTH_SHORT).show();
                }
                else if (intAge==0 || intAge<0){
                    Toast.makeText(getContext(),"Age Is Incorrect !",Toast.LENGTH_SHORT).show();
                } else if (intWeight==0 || intWeight<0) {
                    Toast.makeText(getContext(),"Weight Is Incorrect !",Toast.LENGTH_SHORT).show();
                }
                else {
                    double heightInMeters = currentProgress / 100.0;
                    double bmi = intWeight / (heightInMeters * heightInMeters);
                    String result = String.format("Your BMI Is : %.2f", bmi);
                    res.setText(result);
                    String condition = getCondition(bmi);
                    res.append("\n"+condition);
                }
            }
            private String getCondition(double bmi) {
                if (bmi < 18.5) {
                    return "Underweight";
                } else if (bmi >= 18.5 && bmi < 24.9) {
                    return "Normal Weight";
                } else if (bmi >= 25 && bmi < 29.9) {
                    return "Overweight";
                } else if (bmi >= 30 && bmi < 34.9) {
                    return "Obese Class I";
                } else if (bmi >= 35 && bmi < 39.9) {
                    return "Obese Class II";
                } else {
                    return "Obese Class III";
                }
            }
        });



        return rootView;
    }
    private void showLanguageSelectionDialog() {
        final String[] languages = {"English", "العربية", "Français", "中文"};

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Language")
                .setItems(languages, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                setLocale("en");
                                break;
                            case 1:
                                setLocale("ar");
                                break;
                            case 2:
                                setLocale("fr");
                                break;
                            case 3:
                                setLocale("zh");
                                break;
                        }
                    }
                })
                .show();
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);


        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        if (getActivity() != null) {
            getActivity().recreate();
        }
    }
}