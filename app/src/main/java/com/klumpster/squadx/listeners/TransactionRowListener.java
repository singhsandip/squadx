package com.klumpster.squadx.listeners;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.klumpster.squadx.R;

/**
 * Created by BOX on 07-01-2018.
 */

public class TransactionRowListener implements View.OnClickListener {
    private Context context;
    private int position;
    private String bitPrice,bitQuantity,bitTotalValue;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private LayoutInflater layoutInflater;

    public TransactionRowListener(Context context, int position, String bitPrice, String bitQuantity, String bitTotalValue) {
        this.context = context;
        this.position = position;
        this.bitPrice = bitPrice;
        this.bitQuantity = bitQuantity;
        this.bitTotalValue = bitTotalValue;

        builder = new AlertDialog.Builder(context);
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public void onClick(View v) {
        View view = layoutInflater.inflate(R.layout.transaction_dialog_layout,null);

        TextView tvTransactionId = (TextView) view.findViewById(R.id.transaction_dialog_transaction_id_tv);
        TextView tvBitPrice = (TextView) view.findViewById(R.id.transaction_dialog_bit_price_tv);
        TextView tvBitQuantity = (TextView) view.findViewById(R.id.transaction_dialog_bit_quantity_tv);
        TextView tvTotalValue = (TextView) view.findViewById(R.id.transaction_dialog_total_value_tv);

        tvTransactionId.setText("Transaction ID : "+String.valueOf(position));
        tvBitPrice.setText("BTC Price : "+bitPrice);
        tvBitQuantity.setText("BTC Quantity : "+bitQuantity);
        tvTotalValue.setText("Total Value : "+bitTotalValue);

        builder.setView(view);

        alertDialog = builder.create();
        alertDialog.show();

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}
