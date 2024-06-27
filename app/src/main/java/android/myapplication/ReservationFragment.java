package android.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReservationFragment extends Fragment {

    private CalendarView calendarView;
    private TimePicker timePicker;
    private TextView number1, number2, number3, number4, number5, number6, number7, number8, number9, number10;
    private EditText etName, etPhone, etAddress, etEmail, etNotes;
    private Button btnConfirm;
    private int numberOfPeople;
    private long selectedDate;

    private boolean isOperatingHoursWarningShown = false;


    public ReservationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservation, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        timePicker = view.findViewById(R.id.timePicker);
        number1 = view.findViewById(R.id.number1);
        number2 = view.findViewById(R.id.number2);
        number3 = view.findViewById(R.id.number3);
        number4 = view.findViewById(R.id.number4);
        number5 = view.findViewById(R.id.number5);
        number6 = view.findViewById(R.id.number6);
        number7 = view.findViewById(R.id.number7);
        number8 = view.findViewById(R.id.number8);
        number9 = view.findViewById(R.id.number9);
        number10 = view.findViewById(R.id.number10);
        etName = view.findViewById(R.id.etName);
        etPhone = view.findViewById(R.id.etPhone);
        etAddress = view.findViewById(R.id.etAddress);
        etEmail = view.findViewById(R.id.etEmail);
        etNotes = view.findViewById(R.id.etNotes);
        btnConfirm = view.findViewById(R.id.btnConfirm);

        calendarView.setMinDate(System.currentTimeMillis() - 1000);

        // Đặt selectedDate mặc định là ngày hiện tại
        Calendar today = Calendar.getInstance();
        selectedDate = today.getTimeInMillis();

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            Calendar selectedCal = Calendar.getInstance();
            selectedCal.set(year, month, dayOfMonth, 0, 0, 0);
            selectedCal.set(Calendar.MILLISECOND, 0);

            // Lưu lại ngày được chọn
            selectedDate = selectedCal.getTimeInMillis();


            if (selectedCal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    selectedCal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                timePicker.setCurrentHour(today.get(Calendar.HOUR_OF_DAY));
                timePicker.setCurrentMinute(today.get(Calendar.MINUTE));
            } else {
                timePicker.setCurrentHour(7);
                timePicker.setCurrentMinute(0);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String date = sdf.format(selectedCal.getTime());
            System.out.println("Ngày được chọn: " + date);
        });




        // Thiết lập TimePicker
        timePicker.setIs24HourView(true); // Đặt chế độ 24 giờ
        timePicker.setCurrentHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)); // Đặt giờ hiện tại
        timePicker.setCurrentMinute(Calendar.getInstance().get(Calendar.MINUTE)); // Đặt phút hiện tại

        timePicker.setOnTimeChangedListener((view12, hourOfDay, minute) -> {
            // Kiểm tra nếu thời gian được chọn nằm ngoài giờ hoạt động
            if (!isOperatingHours(hourOfDay, minute)) {
                timePicker.setCurrentHour(7); // Đặt lại giờ thành 7 AM
                timePicker.setCurrentMinute(0);
                Toast.makeText(getActivity(), "Cửa hàng hoạt động từ 7:00 AM đến 10:00 PM", Toast.LENGTH_SHORT).show();
            }
        });



        View.OnClickListener numberClickListener = v -> {
            resetNumberSelection();
            v.setSelected(true);
            v.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.selected_color));

            int id = v.getId();
            if (id == R.id.number1) {
                numberOfPeople = 1;
            } else if (id == R.id.number2) {
                numberOfPeople = 2;
            } else if (id == R.id.number3) {
                numberOfPeople = 3;
            } else if (id == R.id.number4) {
                numberOfPeople = 4;
            } else if (id == R.id.number5) {
                numberOfPeople = 5;
            } else if (id == R.id.number6) {
                numberOfPeople = 6;
            } else if (id == R.id.number7) {
                numberOfPeople = 7;
            } else if (id == R.id.number8) {
                numberOfPeople = 8;
            } else if (id == R.id.number9) {
                numberOfPeople = 9;
            } else if (id == R.id.number10) {
                numberOfPeople = 10;
            }
        };

        number1.setOnClickListener(numberClickListener);
        number2.setOnClickListener(numberClickListener);
        number3.setOnClickListener(numberClickListener);
        number4.setOnClickListener(numberClickListener);
        number5.setOnClickListener(numberClickListener);
        number6.setOnClickListener(numberClickListener);
        number7.setOnClickListener(numberClickListener);
        number8.setOnClickListener(numberClickListener);
        number9.setOnClickListener(numberClickListener);
        number10.setOnClickListener(numberClickListener);

        btnConfirm.setOnClickListener(v -> {
            // Lấy thông tin nhập từ người dùng
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String notes = etNotes.getText().toString().trim();

            // Kiểm tra số lượng người được chọn
            if (numberOfPeople == 0) {
                Toast.makeText(getActivity(), "Vui lòng chọn số lượng người!", Toast.LENGTH_SHORT).show();
                return;
            }
            // Kiểm tra tên
            if (name.isEmpty()) {
                Toast.makeText(getActivity(), "Vui lòng nhập tên!", Toast.LENGTH_SHORT).show();
                return;
            }
            // Kiểm tra số điện thoại
            if (phone.isEmpty()) {
                Toast.makeText(getActivity(), "Vui lòng nhập số điện thoại!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (phone.length() < 9 || phone.length() > 10) {
                Toast.makeText(getActivity(), "Số điện thoại tối thiểu 9 số!", Toast.LENGTH_SHORT).show();
                return;
            }
            // Kiểm tra địa chỉ
            if (address.isEmpty()) {
                Toast.makeText(getActivity(), "Vui lòng nhập địa chỉ!", Toast.LENGTH_SHORT).show();
                return;
            }
            // Kiểm tra email
            if (email.isEmpty()) {
                Toast.makeText(getActivity(), "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!email.endsWith("@gmail.com")) {
                Toast.makeText(getActivity(), "Vui lòng nhập email có đuôi '@gmail.com'", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lấy thời gian hiện tại
            Calendar currentTime = Calendar.getInstance();
            currentTime.add(Calendar.HOUR_OF_DAY, 1);
            currentTime.set(Calendar.SECOND, 0);
            currentTime.set(Calendar.MILLISECOND, 0);

            // Tạo đối tượng Calendar từ ngày và giờ được chọn
            Calendar selectedDateTime = Calendar.getInstance();
            selectedDateTime.setTimeInMillis(selectedDate);
            selectedDateTime.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
            selectedDateTime.set(Calendar.MINUTE, timePicker.getCurrentMinute());
            selectedDateTime.set(Calendar.SECOND, 0);
            selectedDateTime.set(Calendar.MILLISECOND, 0);

            // In ra thông tin thời gian để debug
            SimpleDateFormat sdfDebug = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            String currentTimeString = sdfDebug.format(currentTime.getTime());
            String selectedDateTimeString = sdfDebug.format(selectedDateTime.getTime());
            System.out.println("Thời gian hiện tại: " + currentTimeString);
            System.out.println("Thời gian được chọn: " + selectedDateTimeString);

            // So sánh thời gian và ngày được chọn với thời gian hiện tại
            if (selectedDateTime.before(currentTime)) {
                Toast.makeText(getActivity(), "Bạn cần đặt bàn trước 1 giờ để sắp xếp chỗ!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Xây dựng thông tin chi tiết đặt bàn
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String time = sdfTime.format(selectedDateTime.getTime());
            SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String date = sdfDate.format(selectedDateTime.getTime());

            String reservationDetails = "Tên: " + name +
                    "\nSố điện thoại: " + phone +
                    "\nĐịa chỉ: " + address +
                    "\nEmail: " + email +
                    "\nNgày: " + date +
                    "\nGiờ: " + time +
                    "\nSố lượng người: " + numberOfPeople +
                    "\nGhi chú: " + (notes.isEmpty() ? "Không có" : notes);

            // Hiển thị hộp thoại xác nhận đặt bàn
            new AlertDialog.Builder(getActivity())
                    .setTitle("Xác nhận đặt bàn")
                    .setMessage(reservationDetails)
                    .setPositiveButton("Xác nhận", (dialog, which) -> {
                        Toast.makeText(getActivity(), "Chúc mừng bạn đã đặt bàn thành công", Toast.LENGTH_LONG).show();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });


        return view;
    }

    private void resetNumberSelection() {
        number1.setSelected(false);
        number2.setSelected(false);
        number3.setSelected(false);
        number4.setSelected(false);
        number5.setSelected(false);
        number6.setSelected(false);
        number7.setSelected(false);
        number8.setSelected(false);
        number9.setSelected(false);
        number10.setSelected(false);

        number1.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.hover_background));
        number2.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.hover_background));
        number3.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.hover_background));
        number4.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.hover_background));
        number5.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.hover_background));
        number6.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.hover_background));
        number7.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.hover_background));
        number8.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.hover_background));
        number9.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.hover_background));
        number10.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.hover_background));
    }

    // Phương thức kiểm tra thời gian có nằm trong khoảng hoạt động từ 7 AM đến 10 PM hay không
    private boolean isOperatingHours(int hourOfDay, int minute) {
        return hourOfDay >= 7 && hourOfDay < 21; // Kiểm tra giờ có từ 7 đến 22 (10 PM)
    }


}
