package com.unisocsu.bluetoothcontroller;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView statusText;
    private TextView logOutput;
    private Button btnCheckRoot;
    private Button btnEnableSplit;
    private Button btnDisableSplit;
    private Button btnScanTinyMix;
    private Button btnStartService;
    private Button btnStopService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        android.widget.LinearLayout mainLayout = new android.widget.LinearLayout(this);
        mainLayout.setOrientation(android.widget.LinearLayout.VERTICAL);
        mainLayout.setPadding(32, 32, 32, 32);

        statusText = new TextView(this);
        statusText.setText("Root Status: Unknown");
        statusText.setTextSize(18);
        statusText.setPadding(0, 16, 0, 16);
        mainLayout.addView(statusText);

        btnCheckRoot = new Button(this);
        btnCheckRoot.setText("Check Root Access");
        mainLayout.addView(btnCheckRoot);

        btnEnableSplit = new Button(this);
        btnEnableSplit.setText("Enable Audio Splitting");
        mainLayout.addView(btnEnableSplit);

        btnDisableSplit = new Button(this);
        btnDisableSplit.setText("Disable Audio Splitting");
        mainLayout.addView(btnDisableSplit);

        btnScanTinyMix = new Button(this);
        btnScanTinyMix.setText("Scan TinyMix Audio Controls");
        mainLayout.addView(btnScanTinyMix);

        // Service controls
        android.widget.LinearLayout serviceButtons = new android.widget.LinearLayout(this);
        serviceButtons.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        serviceButtons.setWeightSum(2.0f);

        btnStartService = new Button(this);
        btnStartService.setText("Start Monitor");
        android.widget.LinearLayout.LayoutParams p1 = new android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        btnStartService.setLayoutParams(p1);
        serviceButtons.addView(btnStartService);

        btnStopService = new Button(this);
        btnStopService.setText("Stop Monitor");
        android.widget.LinearLayout.LayoutParams p2 = new android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        btnStopService.setLayoutParams(p2);
        serviceButtons.addView(btnStopService);

        mainLayout.addView(serviceButtons);

        // Log container
        ScrollView scrollView = new ScrollView(this);
        logOutput = new TextView(this);
        logOutput.setText("Logs:\n");
        logOutput.setTextSize(14);
        scrollView.addView(logOutput);
        
        android.widget.LinearLayout.LayoutParams scrollParams = new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f);
        scrollParams.setMargins(0, 32, 0, 0);
        scrollView.setLayoutParams(scrollParams);
        mainLayout.addView(scrollView);

        setContentView(mainLayout);

        // Click listeners
        btnCheckRoot.setOnClickListener(v -> {
            boolean hasRoot = ShellUtils.checkRoot();
            statusText.setText("Root Status: " + (hasRoot ? "GRANTED 🔓" : "DENIED 🔒"));
            Toast.makeText(this, hasRoot ? "Root Access Granted!" : "No Root Found!", Toast.LENGTH_SHORT).show();
        });

        btnEnableSplit.setOnClickListener(v -> {
            String output = AudioRouter.routeViaTinyMix(true);
            logOutput.append("\nEnabling split:\n" + output);
        });

        btnDisableSplit.setOnClickListener(v -> {
            String output = AudioRouter.routeViaTinyMix(false);
            logOutput.append("\nDisabling split:\n" + output);
        });

        btnScanTinyMix.setOnClickListener(v -> {
            logOutput.append("\nScanning tinymix controls...\n");
            List<String> controls = AudioRouter.scanTinyMixControls();
            if (controls.isEmpty()) {
                logOutput.append("No relevant controls found or tinymix is not supported.\n");
            } else {
                for (String control : controls) {
                    logOutput.append(control + "\n");
                }
            }
        });

        btnStartService.setOnClickListener(v -> {
            Intent intent = new Intent(this, AudioRoutingService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
            Toast.makeText(this, "Audio Routing Monitor Started", Toast.LENGTH_SHORT).show();
        });

        btnStopService.setOnClickListener(v -> {
            Intent intent = new Intent(this, AudioRoutingService.class);
            stopService(intent);
            Toast.makeText(this, "Audio Routing Monitor Stopped", Toast.LENGTH_SHORT).show();
        });
    }
}
