package android.myapplication;

import android.os.Bundle;
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

public class ReservationFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private CalendarView calendarView;
    private TimePicker timePicker;
    private TextView number1, number2, number3, number4, number5, number6, number7, number8, number9, number10;
    private EditText etName, etPhone, etAddress, etEmail, etNotes;
    private Button btnConfirm;
    private int numberOfPeople = 1;

    public ReservationFragment() {
        // Required empty public constructor
    }

    public static ReservationFragment newInstance(String param1, String param2) {
        ReservationFragment fragment = new ReservationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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

        View.OnClickListener numberClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            String name = etName.getText().toString();
            String phone = etPhone.getText().toString();
            String address = etAddress.getText().toString();
            String email = etEmail.getText().toString();
            String notes = etNotes.getText().toString();

            int hour = timePicker.getCurrentHour();
            int minute = timePicker.getCurrentMinute();

            Toast.makeText(getActivity(), "Reservation confirmed for " + numberOfPeople + " people at " + hour + ":" + minute, Toast.LENGTH_LONG).show();
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
}
