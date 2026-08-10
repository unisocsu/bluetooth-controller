package com.unisocsu.bluetoothcontroller;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView statusText;
    private TextView logOutput;
    private Button btnCheckRoot;
    private Button btnEnableSplit;
    private Button btnDisableSplit;
    private Button btnRestartAudio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);

        statusText = new TextView(this);
        statusText.setText("Root Status: Unknown");
        statusText.setTextSize(18);
        statusText.setPadding(0, 16, 0, 16);
        layout.addView(statusText);

        btnCheckRoot = new Button(this);
        btnCheckRoot.setText("Check Root Access");
        layout.addView(btnCheckRoot);

        btnEnableSplit = new Button(this);
        btnEnableSplit.setText("Enable Audio Splitting (TinyMix)");
        layout.addView(btnEnableSplit);

        btnDisableSplit = new Button(this);
        btnDisableSplit.setText("Disable Audio Splitting (TinyMix)");
        layout.addView(btnDisableSplit);

        btnRestartAudio = new Button(this);
        btnRestartAudio.setText("Restart Audioserver");
        layout.addView(btnRestartAudio);

        logOutput = new TextView(this);
        logOutput.setText("Logs:\n");
        logOutput.setTextSize(14);
        logOutput.setPadding(0, 32, 0, 0);
        layout.addView(logOutput);

        setContentView(layout);

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

        btnRestartAudio.setOnClickListener(v -> {
            String output = AudioRouter.forceAudioSystemReload();
            logOutput.append("\nRestarting Audio:\n" + output);
        });
    }
}
