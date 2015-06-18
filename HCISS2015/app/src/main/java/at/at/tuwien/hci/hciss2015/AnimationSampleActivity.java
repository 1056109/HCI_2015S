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
    private TextView help1;
    private TextView help2;
    private TextView help3;
    private TextView help4;
    private final ApiUtils apiUtils = new ApiUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);

        btnColleague = (ImageButton) findViewById(R.id.btnColleague);
        btnWeapon = (ImageButton) findViewById(R.id.btnWeapon);
        btnMap = (ImageButton) findViewById(R.id.btnMap);
        btnFeature = (ImageButton) findViewById(R.id.btnFeature);
        help1 = (TextView) findViewById(R.id.help1);
        help2 = (TextView) findViewById(R.id.help2);
        help3 = (TextView) findViewById(R.id.help3);
        help4 = (TextView) findViewById(R.id.help4);

        showcaseView = new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(btnColleague))
                .setOnClickListener(this)
                .build();
        //layoutParams = (RelativeLayout.LayoutParams) btnColleague.getLayoutParams();
        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.CENTER_IN_PARENT);
        lps.addRule(RelativeLayout.ALIGN_BASELINE);
        int margin = 10;
        lps.setMargins(margin, margin, margin, margin);
        showcaseView.setButtonPosition(lps);
        showcaseView.setButtonText(getString(R.string.next));
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
                showcaseView.setShowcase(new ViewTarget(btnWeapon), true);
                help1.setVisibility(View.GONE);
                help2.setVisibility(View.VISIBLE);
                break;

            case 1:
                showcaseView.setShowcase(new ViewTarget(btnMap), true);
                help2.setVisibility(View.GONE);
                help3.setVisibility(View.VISIBLE);
                break;

            case 2:
                showcaseView.setShowcase(new ViewTarget(btnFeature), true);
                //showcaseView.setTarget(Target.NONE);
                help3.setVisibility(View.GONE);
                help4.setVisibility(View.VISIBLE);
                showcaseView.setButtonText(getString(R.string.close));
                //setAlpha(0.4f, textView1, textView2, textView3);
                break;

            case 3:
                showcaseView.hide();

                Intent intent = new Intent(AnimationSampleActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        counter++;
    }
}
