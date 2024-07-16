package android.myapplication;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {

    private Context context;
    private List<Reservation> reservationList;

    public ReservationAdapter(Context context, List<Reservation> reservationList) {
        this.context = context;
        this.reservationList = reservationList;
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reservation, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        Reservation reservation = reservationList.get(position);
        holder.bind(reservation);
    }

    @Override
    public int getItemCount() {
        return reservationList.size();
    }

    public class ReservationViewHolder extends RecyclerView.ViewHolder {

        private TextView tvReservationInfo;

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReservationInfo = itemView.findViewById(R.id.tvReservationInfo);
        }

        public void bind(Reservation reservation) {
            // Display reservation details with ID in red color
            String idHtml = "<font color='#FFAF20'>" + "<b>" + reservation.getTime() + "</b>" + " " + "<b>" + reservation.getDate() + "</b>" + "</font><br/>";
            String reservationDetails =
                    idHtml +"<b>Email: </b>" + reservation.getEmail() + "<br/>" +
                            "<b>Tên: </b>" + reservation.getName() + "<br/>" +
                            " <b>Số điện thoại: </b>" + reservation.getPhone() + "<br/>" +
                            "<b>Địa chỉ: </b>" + reservation.getAddress() + "<br/>" +
                            "<b>Số lượng người: </b>" + reservation.getNumberOfPeople() + "<br/>" +
                            "<b>Ghi chú: </b>" + (reservation.getNotes() != null && !reservation.getNotes().isEmpty() ? reservation.getNotes() : "Không có ghi chú");

            tvReservationInfo.setText(Html.fromHtml(reservationDetails, Html.FROM_HTML_MODE_COMPACT));
        }



    }
}
