package android.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private FirebaseAuth mAuth;

    private TextInputEditText emailEditText, passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Khởi tạo các phần tử UI
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        // Thiết lập sự kiện click cho nút đăng nhập
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Kiểm tra thông tin đầu vào
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(LoginActivity.this, "Vui lòng nhập email.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Vui lòng nhập mật khẩu.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra email tồn tại trong Firebase Auth
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    // Đăng nhập thành công
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công.", Toast.LENGTH_SHORT).show();
                    // Thực hiện các hành động tiếp theo sau khi đăng nhập thành công
                    // Chuyển sang màn hình mới (ví dụ: MainActivity)
                    Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi
                    Log.e("LoginActivity", "Đăng nhập thất bại", e); // Log lỗi để xem chi tiết trong Logcat

                    // Kiểm tra xem lỗi có phải là FirebaseException và có chứa mã lỗi "INVALID_LOGIN_CREDENTIALS" không
                    if (e instanceof FirebaseException && e.getMessage().contains("INVALID_LOGIN_CREDENTIALS")) {
                        // Lỗi đăng nhập không hợp lệ (sai mật khẩu hoặc email không tồn tại)
                        Toast.makeText(LoginActivity.this, "Sai mật khẩu hoặc Email không tồn tại.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Xử lý các trường hợp khác
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    // Sự kiện click cho nút đăng ký
    public void onRegisterClick(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }
}
