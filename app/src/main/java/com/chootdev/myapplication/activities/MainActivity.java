package com.chootdev.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.chootdev.myapplication.services.LocationManagerService;
import com.chootdev.myapplication.R;
import com.chootdev.myapplication.events.LocationEvent;

import java.util.List;

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

    private void populateFromDB(){
        try{
            List<LocationEvent> events = LocationEvent.listAll(LocationEvent.class);
            if (events != null && events.size() > 0){
                tvMessage.setText("");
                for (LocationEvent event : events) {
                    String currentText = tvMessage.getText().toString();
                    String newText = (currentText + "\n\n" + event.getTimeStamp() +
                            "\nLat : " + event.getLat() + " | Lon : " + event.getLan() +
                            "\nDistence to : " + event.getDistence());

                    tvMessage.setText(newText);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
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
        populateFromDB();
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

                LocationEvent.deleteAll(LocationEvent.class);
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
                LocationEvent.deleteAll(LocationEvent.class);
                break;
            case R.id.btnEnd:
                stopService(new Intent(getApplicationContext(), LocationManagerService.class));
                break;
            case R.id.btnReload:
                populateFromDB();
                break;
        }
    }
}
