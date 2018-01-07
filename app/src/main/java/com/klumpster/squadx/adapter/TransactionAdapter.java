package com.klumpster.squadx.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.klumpster.squadx.R;
import com.klumpster.squadx.holder.TransactionHolder;
import com.klumpster.squadx.listeners.TransactionRowListener;
import com.klumpster.squadx.model.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BOX on 06-01-2018.
 */

public class TransactionAdapter extends RecyclerView.Adapter<TransactionHolder> {
    private Context context;
    private LayoutInflater layoutInflater;
    private List<Transaction> transactionList;

    public TransactionAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);

        transactionList = new ArrayList<>();
    }

    @Override
    public TransactionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TransactionHolder(layoutInflater.inflate(R.layout.transection_row_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(TransactionHolder holder, int position) {
        Transaction transaction = transactionList.get(position);

        holder.tvType.setText(transaction.getType());
        holder.tvTotalValue.setText(transaction.getTotalValue());
        holder.tvDate.setText(transaction.getTradeDate());

        holder.llTransaction.setOnClickListener(new TransactionRowListener(context,position + 1,transaction.getBitPrice(),transaction.getQuantity(),transaction.getTotalValue()));
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public void addList(List<Transaction> transactionList){
        this.transactionList = transactionList;
        notifyDataSetChanged();
    }
}
