package android.myapplication;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private EditText edtName, edtPhone, edtAddress, edtBirthday;
    private TextView tvName, tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        Spinner spnGender = findViewById(R.id.spnGender);
        edtBirthday = findViewById(R.id.edtBirthday);
        edtAddress = findViewById(R.id.edtAddress);
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        Button updateButton = findViewById(R.id.updateButton);
        ImageView ivBack = findViewById(R.id.ivBack);

        setupGenderSpinner();


        // Lấy thông tin người dùng từ Firebase Realtime Database để cập nhật name
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String name = dataSnapshot.child("name").getValue(String.class);
                        String phone = dataSnapshot.child("phone").getValue(String.class);
                        String email = dataSnapshot.child("email").getValue(String.class);
                        String birthday = dataSnapshot.child("birthday").getValue(String.class);
                        String address = dataSnapshot.child("address").getValue(String.class);

                        // Hiển thị thông tin người dùng
                        edtName.setText(name);
                        edtPhone.setText(phone);
                        edtBirthday.setText(birthday);
                        edtAddress.setText(address);
                        tvEmail.setText(email);
                        tvName.setText(name);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Xử lý lỗi đọc dữ liệu
                }
            });
        }

        // Lắng nghe sự kiện thay đổi của edtName để cập nhật tvName
        edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No need to implement
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No need to implement
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Cập nhật tvName khi edtName thay đổi
                tvName.setText(s.toString().trim());
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }

    public void showDatePickerDialog(View v) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    // Kiểm tra nếu ngày chọn lớn hơn hoặc bằng 1/1/2018 thì hiển thị thông báo
                    Calendar maxDateCalendar = Calendar.getInstance();
                    maxDateCalendar.set(2018, 0, 1); // Thiết lập ngày 1/1/2018
                    long maxDateInMillis = maxDateCalendar.getTimeInMillis();

                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(selectedYear, selectedMonth, selectedDayOfMonth);
                    long selectedDateInMillis = selectedCalendar.getTimeInMillis();

                    if (selectedDateInMillis >= maxDateInMillis) {
                        Toast.makeText(EditProfileActivity.this, "Vui lòng chọn ngày sinh trước năm 2018", Toast.LENGTH_SHORT).show();
                    } else {
                        // Xử lý khi người dùng chọn ngày hợp lệ
                        String selectedDate = selectedDayOfMonth + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        edtBirthday.setText(selectedDate); // Đặt giá trị vào TextInputEditText
                    }
                }, year, month, day);

        // Thiết lập ngày tối đa là 31/12/2017
        calendar.set(2017, 11, 31); // Thiết lập ngày 31/12/2017
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

        // Thiết lập ngày tối thiểu là ngày sinh trước năm 2018
        datePickerDialog.getDatePicker().setMinDate(0);

        datePickerDialog.show();
    }




    private void setupGenderSpinner() {
        String[] genderOptions = getResources().getStringArray(R.array.gender_options);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, genderOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spnGender = findViewById(R.id.spnGender); // Lấy đối tượng Spinner từ layout
        spnGender.setAdapter(adapter); // Thiết lập Adapter cho Spinner
    }


    private void updateProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            String newName = edtName.getText().toString().trim();
            String newPhone = edtPhone.getText().toString().trim();
            String newBirthday = edtBirthday.getText().toString().trim();

            // Lấy giá trị giới tính từ Spinner
            Spinner spnGender = findViewById(R.id.spnGender);
            String newGender = spnGender.getSelectedItem().toString().trim();

            String newAddress = edtAddress.getText().toString().trim();

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

            // Tạo một map để lưu các giá trị cần cập nhật
            Map<String, Object> updates = new HashMap<>();
            updates.put("name", newName);
            updates.put("phone", newPhone);
            updates.put("birthday", newBirthday);
            updates.put("gender", newGender);
            updates.put("address", newAddress);

            // Cập nhật dữ liệu lên Firebase
            userRef.updateChildren(updates)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EditProfileActivity.this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditProfileActivity.this, "Cập nhật thông tin thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}