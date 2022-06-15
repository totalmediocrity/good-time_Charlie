package com.example.sqlightnews;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;
public class LogInActivity extends AppCompatActivity {

    CheckBox showPassword;
    EditText txtLogin, txtPassword;
    DatabaseHelper databaseHelper;
    Executor executor;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        initialize();
        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    txtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                else
                    txtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
    }

    private void initialize() {
        executor = ContextCompat.getMainExecutor(this);
        AppCompatActivity active = this;

        biometricPrompt = new BiometricPrompt(LogInActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Log.e("ErrorAUTH", errString.toString());
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                startActivity(new Intent(active, AllNewsActivity.class));
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Log.e("FailedAUTH", "FAIL!!!");
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Авторизация")
                .setSubtitle("Прислоните палец")
                .setNegativeButtonText("Отмена")
                .build();
        showPassword = findViewById(R.id.showPassword);
        txtLogin = findViewById(R.id.txtLogin);
        txtPassword = findViewById(R.id.txtPassword);
        databaseHelper = new DatabaseHelper(this);
    }

    public void enterClick(View view) {
        Intent intent = new Intent(this, AllNewsActivityAdministrator.class);
        Cursor res = databaseHelper.getData(txtLogin.getText().toString().trim(), txtPassword.getText().toString().trim());
        if (res.getCount() == 0) {
            Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
            return;
        }
        while (res.moveToNext()) {
            if (res.getString(7).equals("Администратор")) {
                intent.putExtra("Id", res.getInt(0));
                startActivity(intent);
            } else {
                startActivity(new Intent(this, AllNewsActivity.class));
            }
        }
    }

    public void registrationClick(View view) {
        startActivity(new Intent(this, RegistrationActivity.class));

    }
    public void btnTouchClick(View view) {
        biometricPrompt.authenticate(promptInfo);
    }
}