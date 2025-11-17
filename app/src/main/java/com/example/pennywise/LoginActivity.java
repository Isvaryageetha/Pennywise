package com.example.pennywise;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// Firebase imports
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.Task; // Required for the task object in addOnCompleteListener

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private ProgressBar progressBar;
    private TextView signUpTextView, skipForNowTextView;
    private ImageView moneyIcon;
    private Animation bounce;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "LoginActivity started");

        // Test if MainActivity can be loaded
        testMainActivityClass();

        initializeViews();
        setupClickListeners();
        startMoneyIconAnimation();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Log.d(TAG, "User already signed in: " + currentUser.getEmail());
            // User is already signed in, navigate to MainActivity
            safeNavigateToMainActivity();
        } else {
            Log.d(TAG, "No user signed in.");
        }
    }

    private void performLogin(String email, String password) {
        Log.d(TAG, "Starting Firebase login process for email: " + email);

        progressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    loginButton.setEnabled(true);

                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        showToast("Login successful!", "✅");
                        safeNavigateToMainActivity();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        showErrorDialog("Login Failed", "Authentication failed: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                    }
                });
    }

    private void testMainActivityClass() {
        try {
            // Test if MainActivity class exists and can be loaded
            Class<?> mainActivityClass = Class.forName("com.example.pennywise.MainActivity");
            Log.d(TAG, "✓ MainActivity class found: " + mainActivityClass.getName());

            // Test if we can create an intent
            Intent testIntent = new Intent(this, mainActivityClass);
            Log.d(TAG, "✓ MainActivity intent created successfully");

        } catch (ClassNotFoundException e) {
            Log.e(TAG, "✗ MainActivity class not found!", e);
            showErrorDialog("MainActivity not found", "The main app screen is missing. Please reinstall the app.");
        } catch (Exception e) {
            Log.e(TAG, "✗ Error testing MainActivity: " + e.getMessage(), e);
            showErrorDialog("App Error", "Cannot start main app: " + e.getMessage());
        }
    }

    private void initializeViews() {
        try {
            emailEditText = findViewById(R.id.email_edit_text);
            passwordEditText = findViewById(R.id.password_edit_text);
            loginButton = findViewById(R.id.login_button);
            progressBar = findViewById(R.id.progress_bar);
            signUpTextView = findViewById(R.id.sign_up_text_view);
            skipForNowTextView = findViewById(R.id.skip_for_now_text_view);
            moneyIcon = findViewById(R.id.login_money_icon);

            bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);

            Log.d(TAG, "All views initialized");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage(), e);
            showErrorDialog("Layout Error", "Screen layout is corrupted: " + e.getMessage());
        }
    }

    private void startMoneyIconAnimation() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                moneyIcon.startAnimation(bounce);
                moneyIcon.setAlpha(1.0f);
                Log.d(TAG, "Money icon animation started");
            }
        }, 1000);
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Login button clicked");
                attemptLogin();
            }
        });

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Sign up text clicked");
                // Implement Firebase user creation here, or navigate to a SignUpActivity
                showToast("Sign up feature coming soon!", "💡");
                // If you want to enable sign up, you would call a method like:
                // createFirebaseUser(emailEditText.getText().toString().trim(), passwordEditText.getText().toString().trim());
            }
        });

        skipForNowTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Skip for now clicked");
                safeNavigateToMainActivity();
            }
        });
    }

    private void attemptLogin() {
        emailEditText.setError(null);
        passwordEditText.setError(null);

        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        Log.d(TAG, "Attempting login for email: " + email);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            focusView = passwordEditText;
            cancel = true;
            Log.d(TAG, "Password validation failed: empty");
        } else if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            focusView = passwordEditText;
            cancel = true;
            Log.d(TAG, "Password validation failed: too short");
        }

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            focusView = emailEditText;
            cancel = true;
            Log.d(TAG, "Email validation failed: empty");
        } else if (!isEmailValid(email)) {
            emailEditText.setError("Enter a valid email address");
            focusView = emailEditText;
            cancel = true;
            Log.d(TAG, "Email validation failed: invalid format");
        }

        if (cancel) {
            focusView.requestFocus();
            Log.d(TAG, "Login validation failed");
        } else {
            // Call the Firebase login method
            performLogin(email, password);
        }
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    private void safeNavigateToMainActivity() {
        try {
            Log.d(TAG, "Attempting safe navigation to MainActivity");

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);

            // Add flags for clean start
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            Log.d(TAG, "Starting MainActivity...");
            startActivity(intent);

            Log.d(TAG, "MainActivity started successfully");
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            // Don't finish immediately - wait to see if MainActivity crashes
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Finishing LoginActivity");
                    finish();
                }
            }, 1000);

        } catch (Exception e) {
            Log.e(TAG, "✗ NAVIGATION FAILED: " + e.getMessage(), e);

            // Show detailed error dialog
            showErrorDialog(
                    "Cannot Start App",
                    "The main app screen failed to load.\n\n" +
                            "Error: " + e.getClass().getSimpleName() + "\n" +
                            "Message: " + e.getMessage() + "\n\n" +
                            "Please check if all layout files exist."
            );
        }
    }

    private void showErrorDialog(String title, String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    new android.app.AlertDialog.Builder(LoginActivity.this)
                            .setTitle(title)
                            .setMessage(message)
                            .setPositiveButton("OK", null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } catch (Exception e) {
                    // Fallback to toast if dialog fails
                    Toast.makeText(LoginActivity.this, title + ": " + message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showToast(String message, String icon) {
        Log.d(TAG, "Showing toast: " + icon + " " + message);
        Toast.makeText(this, icon + " " + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "LoginActivity resumed");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "LoginActivity paused");
    }
}