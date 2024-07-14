package android.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        TextView tvSuccess = findViewById(R.id.tvSuccess);
        TextView tvHome = findViewById(R.id.tvHome);

        Intent intent = getIntent();
        String message = intent.getStringExtra("message");

        tvSuccess.setText(message);
        tvHome.setOnClickListener(v -> {
            Intent homeIntent = new Intent(this, MainActivity.class);
            startActivity(homeIntent);
            finish();
        });
    }
}