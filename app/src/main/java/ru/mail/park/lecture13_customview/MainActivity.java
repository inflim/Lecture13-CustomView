package ru.mail.park.lecture13_customview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    private ToggleButton toggle;
    private CustomView customView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        customView = (CustomView) findViewById(R.id.customView);

        toggle = (ToggleButton) findViewById(R.id.switchButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ViewGroup.LayoutParams lp = customView.getLayoutParams();

                if (isChecked) {
                    lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
                } else {
                    lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                }
            }
        });

    }
}
