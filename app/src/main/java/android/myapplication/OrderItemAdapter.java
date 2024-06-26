package android.myapplication;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {

    private List<String> orderItemList;

    public OrderItemAdapter(List<String> orderItemList) {
        this.orderItemList = orderItemList;
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        String orderItem = orderItemList.get(position);
        holder.orderItemText.setText(orderItem);

        holder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ItemDescriptionActivity.class);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }

    public static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        TextView orderItemText, orderItemPrice;
        ImageView orderItemImage;
        ImageButton addButton;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            orderItemText = itemView.findViewById(R.id.itemName);
            orderItemPrice = itemView.findViewById(R.id.itemPrice);
            orderItemImage = itemView.findViewById(R.id.itemImageView);
            addButton = itemView.findViewById(R.id.plus_icon);
        }
    }
}