package com.niquewrld.tutorspace.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.niquewrld.tutorspace.R;
import com.niquewrld.tutorspace.models.Transaction;

import java.util.List;
import java.util.Objects;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {

    private List<Transaction> transactions;

    public TransactionsAdapter(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.amountTextView.setText(String.format("Amount: R%s", transaction.getAmount()));
        holder.statusTextView.setText(String.format("Status: %s", transaction.getStatus()));
        holder.refTextView.setText(String.format("Ref: %s", transaction.getRef()));

        if(Objects.equals(transaction.getType(), "Incoming")){
            holder.tutorNameTextView.setText(String.format("Student: %s", transaction.getStudentName()));
        }
        else{
            holder.tutorNameTextView.setText(String.format("Tutor: %s", transaction.getTutorName()));
        }

        if(transaction.isDeposited()){
            holder.statusIndicator.setBackgroundResource(R.color.green);
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView amountTextView;
        public TextView statusTextView;
        public TextView tutorNameTextView;
        public TextView refTextView;
        public View statusIndicator;

        public ViewHolder(View itemView) {
            super(itemView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            tutorNameTextView = itemView.findViewById(R.id.tutorNameTextView);
            refTextView = itemView.findViewById(R.id.refTextView);
            statusIndicator = itemView.findViewById(R.id.statusIndicator);
        }
    }
}