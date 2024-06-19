package android.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private Button buttonGetStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        buttonGetStarted = findViewById(R.id.buttonGetStarted);

        // Set onClickListener for the button
        buttonGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Thực hiện hành động khi người dùng nhấn vào nút
                // Ví dụ: chuyển sang màn hình mới
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);

                // Kết thúc MainActivity để ngăn không cho quay lại
                finish();
            }
        });
    }
}

