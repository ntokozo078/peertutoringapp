package com.niquewrld.tutorspace.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.niquewrld.tutorspace.R;
import com.niquewrld.tutorspace.models.Tutorial;

import java.util.List;

public class TutorialAdapter extends RecyclerView.Adapter<TutorialAdapter.TutorialViewHolder> {

    private List<Tutorial> tutorials;

    public TutorialAdapter(List<Tutorial> tutorials) {
        this.tutorials = tutorials;
    }

    @NonNull
    @Override
    public TutorialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tutorial, parent, false);
        return new TutorialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TutorialViewHolder holder, int position) {
        Tutorial tutorial = tutorials.get(position);
        holder.title.setText(tutorial.getTitle());
        holder.date.setText(tutorial.getDate());
        holder.time.setText(tutorial.getTime());
    }

    @Override
    public int getItemCount() {
        return tutorials.size();
    }

    static class TutorialViewHolder extends RecyclerView.ViewHolder {
        TextView title, date, time;

        public TutorialViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tutorial_title);
            date = itemView.findViewById(R.id.tutorial_date);
            time = itemView.findViewById(R.id.tutorial_time);
        }
    }
}