package com.shoppa.shoppa.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.shoppa.shoppa.R;
import com.shoppa.shoppa.db.entity.CartDetail;
import com.shoppa.shoppa.db.entity.Item;

import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context mContext;
    private List<Item> productList;
    private List<CartDetail> cartDetailList;
    private OnItemChangedListener onItemChangedListener;

    public CartAdapter(Context mContext,
                       List<Item> productList,
                       List<CartDetail> cartDetailList,
                       OnItemChangedListener onItemChangedListener) {

        this.mContext = mContext;
        this.productList = productList;
        this.cartDetailList = cartDetailList;
        this.onItemChangedListener = onItemChangedListener;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_view_cart, viewGroup, false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CartViewHolder cartViewHolder, int i) {

        final int position = i;
        final Item item = productList.get(i);
        final CartDetail cartDetail = cartDetailList.get(i);
        double price = item.getPrice() * cartDetail.getQuantity();

        cartViewHolder.name.setText(item.getName());
        cartViewHolder.price.setText(String.format(Locale.getDefault(), "RM %.2f", price));
        cartViewHolder.quantity.setText(String.format("%s", cartDetail.getQuantity()));

        byte[] decodedString = Base64.decode(item.getImg(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory
                .decodeByteArray(decodedString, 0, decodedString.length);
        cartViewHolder.img.setImageBitmap(decodedByte);
        cartViewHolder.img.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.DialogTheme)
                        .setTitle(item.getName() + "'s Description")
                        .setMessage(item.getDescription())
                        .setPositiveButton("Ok", null);
                builder.show();
            }
        });

        cartViewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                View dialogView = View.inflate(mContext, R.layout.dialog_product_edit, null);
                final Spinner spinner
                        = (Spinner) dialogView.findViewById(R.id.spinner_edit_product_quantity);

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.DialogTheme)
                        .setTitle("Edit Quantity")
                        .setView(dialogView)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                int quantity = Integer.parseInt(spinner.getSelectedItem().toString());
                                cartDetail.setQuantity(quantity);
                                cartDetailList.set(position, cartDetail);

                                notifyDataSetChanged();
                                onItemChangedListener.onItemChanged();
                            }
                        })
                        .setNegativeButton("Cancel", null);
                builder.show();
            }
        });

        cartViewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.DialogTheme)
                        .setTitle("Confirmation")
                        .setMessage("Remove " + item.getName() + "?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                productList.remove(position);
                                cartDetailList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, getItemCount());

                                onItemChangedListener.onItemChanged();
                            }
                        })
                        .setNegativeButton("Cancel", null);
                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartDetailList.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {

        ImageView img;
        TextView name, price, quantity;
        Button btnEdit;
        ImageButton btnRemove;

        CartViewHolder(View view) {

            super(view);
            img = (ImageView) view.findViewById(R.id.img_product);
            name = (TextView) view.findViewById(R.id.tv_product_name);
            price = (TextView) view.findViewById(R.id.tv_product_price);
            quantity = (TextView) view.findViewById(R.id.tv_product_qty);
            btnEdit = (Button) view.findViewById(R.id.btn_product_edit);
            btnRemove = (ImageButton) view.findViewById(R.id.btn_product_delete);
        }
    }

    public interface OnItemChangedListener {
        void onItemChanged();
    }
}
