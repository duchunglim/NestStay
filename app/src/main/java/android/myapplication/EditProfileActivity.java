package android.myapplication;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import android.Manifest;


import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private EditText edtName, edtPhone, edtAddress, edtBirthday;
    private TextView tvName, tvEmail, tvJoinDate;
    private CircleImageView profileImage;
    private LocationManager locationManager;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;

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
        tvJoinDate = findViewById(R.id.tvJoinDate);
        profileImage = findViewById(R.id.profile_image);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        setupGenderSpinner();


        // Lấy thông tin người dùng từ Firebase Realtime Database để cập nhật name
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String name = dataSnapshot.child("name").getValue(String.class);
                        String phone = dataSnapshot.child("phone").getValue(String.class);
                        String email = dataSnapshot.child("email").getValue(String.class);
                        String birthday = dataSnapshot.child("birthday").getValue(String.class);
                        String address = dataSnapshot.child("address").getValue(String.class);
                        String joinDate = dataSnapshot.child("createdAt").getValue(String.class);
                        String gender = dataSnapshot.child("gender").getValue(String.class);
                        String profileImageUrl = dataSnapshot.child("profileImage").getValue(String.class);

                        if (joinDate != null) {
                            tvJoinDate.setText(joinDate); // Hiển thị ngày tham gia
                        }

                        // Hiển thị thông tin người dùng
                        edtName.setText(name);
                        edtPhone.setText(phone);
                        edtBirthday.setText(birthday);
                        edtAddress.setText(address);
                        tvEmail.setText(email);
                        tvName.setText(name);

                        // Thiết lập giới tính trong Spinner
                        if (gender != null) {
                            ArrayAdapter<String> genderAdapter = (ArrayAdapter<String>) spnGender.getAdapter();
                            int position = genderAdapter.getPosition(gender);
                            spnGender.setSelection(position);
                        }

                        // Hiển thị ảnh đại diện
                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            Glide.with(EditProfileActivity.this)
                                    .load(profileImageUrl)
                                    .placeholder(R.drawable.meme) // Ảnh mặc định
                                    .into(profileImage);
                        }
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

        // Thiết lập sự kiện khi nhấn vào ảnh đại diện để chọn ảnh từ thư viện
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                getAddressFromLocation(location);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            try {
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocation != null) {
                    getAddressFromLocation(lastKnownLocation);
                } else {
                    locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
                }
            } catch (SecurityException e) {
                e.printStackTrace();
                Toast.makeText(this, "Không thể truy cập vị trí: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getAddressFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                StringBuilder sb = new StringBuilder();

                String featureName = address.getFeatureName();
                String thoroughfare = address.getThoroughfare();
                if (featureName != null && thoroughfare != null && !featureName.equals(thoroughfare)) {
                    sb.append(featureName).append(" ");
                }
                if (thoroughfare != null) {
                    sb.append(thoroughfare).append(", ");
                }

                String subAdminArea = address.getSubAdminArea();
                if (subAdminArea != null) {
                    sb.append(subAdminArea).append(", ");
                }

                String adminArea = address.getAdminArea();
                if (adminArea != null) {
                    sb.append(adminArea).append(", ");
                }

                String countryName = address.getCountryName();
                if (countryName != null) {
                    sb.append(countryName);
                }

                String fullAddress = sb.toString().trim();
                if (fullAddress.endsWith(",")) {
                    fullAddress = fullAddress.substring(0, fullAddress.length() - 1);
                }

                edtAddress.setText(fullAddress);
            } else {
                Toast.makeText(this, "Không tìm thấy địa chỉ", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi lấy địa chỉ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Quyền truy cập vị trí bị từ chối", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            // Cập nhật ảnh đại diện trên ImageView
            profileImage.setImageURI(imageUri);
            // Gọi phương thức để lưu ảnh lên Firebase
            uploadImageToFirebase(imageUri);
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                    .child("profile_images").child(user.getUid() + ".jpg");

            storageReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                                    .child("users").child(user.getUid());
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("profileImage", uri.toString());
                            userRef.updateChildren(updates)
                                    .addOnSuccessListener(aVoid ->
                                            Toast.makeText(EditProfileActivity.this, "Cập nhật ảnh đại diện thành công.", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e ->
                                            Toast.makeText(EditProfileActivity.this, "Cập nhật ảnh đại diện thất bại.", Toast.LENGTH_SHORT).show());
                        });
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(EditProfileActivity.this, "Tải ảnh lên Firebase thất bại", Toast.LENGTH_SHORT).show());
        }
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