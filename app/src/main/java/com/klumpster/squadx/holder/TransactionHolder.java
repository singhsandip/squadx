package com.klumpster.squadx.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.klumpster.squadx.R;

/**
 * Created by BOX on 06-01-2018.
 */

public class TransactionHolder extends RecyclerView.ViewHolder {
    public LinearLayout llTransaction;
    public TextView tvCoin,tvType,tvTotalValue,tvDate;

    public TransactionHolder(View itemView) {
        super(itemView);

        llTransaction = (LinearLayout) itemView.findViewById(R.id.transaction_row_ll);

        tvCoin = (TextView) itemView.findViewById(R.id.transaction_row_coin_tv);
        tvType = (TextView) itemView.findViewById(R.id.transaction_row_type_tv);
        tvTotalValue = (TextView) itemView.findViewById(R.id.transaction_row_total_value_tv);
        tvDate = (TextView) itemView.findViewById(R.id.transaction_row_date_tv);
    }
}
