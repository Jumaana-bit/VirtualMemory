package com.example.virtualmemory;

import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.CancellationSignal; // Correct import
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

@RequiresApi(api = Build.VERSION_CODES.P)
public class FingerprintActivity extends AppCompatActivity {

    private BiometricPrompt biometricPrompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint);

        // Initialize the biometric prompt
        biometricPrompt = new BiometricPrompt.Builder(this)
                .setTitle("Fingerprint Authentication")
                .setSubtitle("Login with Fingerprint")
                .setDescription("Please authenticate to log in")
                .setNegativeButton("Cancel", getMainExecutor(), (dialog, which) -> {
                    Toast.makeText(getApplicationContext(), "Authentication canceled", Toast.LENGTH_SHORT).show();
                })
                .build();

        // Call authenticate without the CryptoObject for simple authentication
        biometricPrompt.authenticate(new CancellationSignal(), getMainExecutor(),
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        // Handle successful authentication
                        Toast.makeText(getApplicationContext(), "Authentication succeeded", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        // Handle authentication failure
                        Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        // Handle error
                        Toast.makeText(getApplicationContext(), "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
