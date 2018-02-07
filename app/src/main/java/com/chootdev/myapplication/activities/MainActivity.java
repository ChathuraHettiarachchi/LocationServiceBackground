package com.chootdev.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.chootdev.myapplication.events.LocationEvent;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvMessage;
    private Button btnStart, btnClear, btnEnd, btnReload;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        tvMessage = (TextView) findViewById(R.id.tvMessage);
        btnStart = (Button) findViewById(R.id.btnStart);
        btnClear = (Button) findViewById(R.id.btnClear);
        btnEnd = (Button) findViewById(R.id.btnEnd);
        btnReload = (Button) findViewById(R.id.btnReload);

        btnStart.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnEnd.setOnClickListener(this);
        btnReload.setOnClickListener(this);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
    }

    public void onEvent(LocationEvent location) {
        String currentText = tvMessage.getText().toString();
        String newText = (currentText + "\n\n" + location.getTimeStamp() +
                "\nLat : " + location.getLat() + " | Lon : " + location.getLan() +
                "\nDistence to : " + location.getDistence());

        tvMessage.setText(newText);

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStart:

                tvMessage.setText("Starting service...");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tvMessage.setText(tvMessage.getText().toString() + "\nLocation manager starts now...");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getApplicationContext(), LocationManagerService.class);
                                startService(intent);
                            }
                        }, 1000);
                    }
                }, 1000);

                break;
            case R.id.btnClear:
                tvMessage.setText("");
                // clear db
                break;
            case R.id.btnEnd:
                stopService(new Intent(getApplicationContext(), LocationManagerService.class));
                break;
            case R.id.btnReload:
                // reload table data to view
                break;
        }
    }
}
