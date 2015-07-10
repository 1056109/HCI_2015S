/*
 * Copyright 2014 Alex Curran
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.at.tuwien.hci.hciss2015;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ApiUtils;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

/**
 * Created by Alex on 26/10/13.
 */
public class AnimationSampleActivity extends Activity implements View.OnClickListener {

    private ShowcaseView showcaseView;
    private int counter = 0;
    private ImageButton btnColleague;
    private ImageButton btnWeapon;
    private ImageButton btnMap;
    private ImageButton btnFeature;
    private TextView startIcon, help0;
    private TextView help1;
    private TextView help2;
    private TextView help3;
    private TextView help3b;
    private TextView help4;
    private TextView help5;
    private ImageView imageMurder, imagePolice, imageHospital, imagePark, imageSuspect;
    private TextView colleagueState;
    private TextView weaponProgress;
    private TextView featureProgress;
    private TextView mapProgress;
    private final ApiUtils apiUtils = new ApiUtils();
    float alphaVal = 0.5f;
    float alphaReset = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);

        btnColleague = (ImageButton) findViewById(R.id.btnColleague);
        btnWeapon = (ImageButton) findViewById(R.id.btnWeapon);
        btnMap = (ImageButton) findViewById(R.id.btnMap);
        btnFeature = (ImageButton) findViewById(R.id.btnFeature);

        startIcon = (TextView) findViewById(R.id.startIcon);
        help0 = (TextView) findViewById(R.id.help0);
        help1 = (TextView) findViewById(R.id.help1);
        help2 = (TextView) findViewById(R.id.help2);
        help3 = (TextView) findViewById(R.id.help3);
        help3b = (TextView) findViewById(R.id.help3b);
        help4 = (TextView) findViewById(R.id.help4);
        help5 = (TextView) findViewById(R.id.help5);

        imageMurder = (ImageView) findViewById(R.id.imageMurder);
        imagePolice = (ImageView) findViewById(R.id.imagePolice);
        imageHospital = (ImageView) findViewById(R.id.imageHospital);
        imagePark = (ImageView) findViewById(R.id.imagePark);
        imageSuspect = (ImageView) findViewById(R.id.imageSuspect);

        colleagueState = (TextView) findViewById(R.id.colleagueState);
        weaponProgress = (TextView) findViewById(R.id.weaponProgress);
        featureProgress = (TextView) findViewById(R.id.featureProgress);
        mapProgress = (TextView) findViewById(R.id.mapProgress);

        showcaseView = new ShowcaseView.Builder(this,true)
                .setTarget(new ViewTarget(imageMurder))
                .setOnClickListener(this)
                .build();
        //layoutParams = (RelativeLayout.LayoutParams) btnColleague.getLayoutParams();
        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        int margin = 10;
        lps.setMargins(margin, margin, margin, margin);
        showcaseView.setButtonPosition(lps);
        showcaseView.setButtonText(getString(R.string.next));
        setAlpha(alphaVal,colleagueState,btnColleague,imagePolice,imageSuspect,imageHospital,imagePark,btnMap,btnWeapon,btnFeature,weaponProgress,featureProgress,mapProgress);

    }


    private void setAlpha(float alpha, View... views) {
        if (apiUtils.isCompatWithHoneycomb()) {
            for (View view : views) {
                view.setAlpha(alpha);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (counter) {
            case 0:
                showcaseView.setShowcase(new ViewTarget(imagePolice), true);
                startIcon.setVisibility(View.GONE);
                help0.setVisibility(View.VISIBLE);
                setAlpha(alphaVal,imageMurder);
                setAlpha(alphaReset,imagePolice,imageHospital,imagePark);
                break;
            case 1:
                showcaseView.setShowcase(new ViewTarget(btnColleague), true);
                help0.setVisibility(View.GONE);
                help1.setVisibility(View.VISIBLE);
                setAlpha(alphaVal,imagePolice,imageHospital,imagePark);
                setAlpha(alphaReset,colleagueState,btnColleague);
                break;
            case 2:
                showcaseView.setShowcase(new ViewTarget(btnWeapon), true);
                help1.setVisibility(View.GONE);
                help2.setVisibility(View.VISIBLE);
                setAlpha(alphaVal,colleagueState,btnColleague);
                setAlpha(alphaReset,weaponProgress,btnWeapon);
                break;

            case 3:
                showcaseView.setShowcase(new ViewTarget(btnMap), true);
                help2.setVisibility(View.GONE);
                help3.setVisibility(View.VISIBLE);
                setAlpha(alphaVal,weaponProgress,btnWeapon);
                setAlpha(alphaReset,mapProgress,btnMap);
                break;
            case 4:
                showcaseView.setShowcase(new ViewTarget(imageSuspect), true);
                help3.setVisibility(View.GONE);
                help3b.setVisibility(View.VISIBLE);
                imageSuspect.setVisibility(View.VISIBLE);
                setAlpha(alphaVal,mapProgress,btnMap);
                setAlpha(alphaReset,imageSuspect);
                break;

            case 5:
                showcaseView.setShowcase(new ViewTarget(btnFeature), true);
                help3b.setVisibility(View.GONE);
                help4.setVisibility(View.VISIBLE);
                setAlpha(alphaVal,imageSuspect);
                setAlpha(alphaReset,featureProgress,btnFeature);
                break;
            case 6:
                showcaseView.setShowcase(new ViewTarget(btnFeature), true);
                help4.setVisibility(View.GONE);
                help5.setVisibility(View.VISIBLE);
                showcaseView.setButtonText(getString(R.string.close));
                break;

            case 7:
                closeTut(v);
                break;
        }
        counter++;
    }
    public void closeTut(View v) {
        showcaseView.hide();

        Intent intent = new Intent(AnimationSampleActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
