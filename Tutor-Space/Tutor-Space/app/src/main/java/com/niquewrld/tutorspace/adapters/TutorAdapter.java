package com.niquewrld.tutorspace.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.niquewrld.tutorspace.R;
import com.niquewrld.tutorspace.RequestActivity;
import com.niquewrld.tutorspace.models.ProfileManager;
import com.niquewrld.tutorspace.models.TutorProfile;

import java.util.List;

public class TutorAdapter extends RecyclerView.Adapter<TutorAdapter.TutorViewHolder> {
    private List<TutorProfile> tutorList;
    private Context context;

    public TutorAdapter(Context context , List<TutorProfile> tutorList) {
        this.context = context;
        this.tutorList = tutorList;
    }

    public void updateTutors(List<TutorProfile> newTutors) {
        this.tutorList = newTutors;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TutorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tutor, parent, false);
        return new TutorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TutorViewHolder holder, int position) {
        TutorProfile tutor = tutorList.get(position);
        holder.tutorName.setText(tutor.getName());
        holder.tutorSubject.setText(tutor.getSubject());
        holder.tutorRating.setText(String.format("Rating: %.1f", tutor.getRating()));
        holder.tutorLocation.setText(tutor.getLocation());
        holder.tutorPrice.setText(String.format("Price: R%s", tutor.getPrice()));

        // Load profile picture using Glide
        Glide.with(holder.tutorImage.getContext())
                .load(tutor.getProfilePic())
                .placeholder(R.drawable.ic_placeholder) // Replace with a placeholder image
                .into(holder.tutorImage);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RequestActivity.class);
            ProfileManager.setTutorProfile(tutor);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return tutorList.size();
    }

    static class TutorViewHolder extends RecyclerView.ViewHolder {
        ImageView tutorImage;
        TextView tutorName, tutorSubject, tutorRating, tutorLocation, tutorPrice;

        public TutorViewHolder(@NonNull View itemView) {
            super(itemView);
            tutorImage = itemView.findViewById(R.id.tutorImage);
            tutorName = itemView.findViewById(R.id.tutorName);
            tutorSubject = itemView.findViewById(R.id.tutorSubject);
            tutorRating = itemView.findViewById(R.id.tutorRating);
            tutorLocation = itemView.findViewById(R.id.tutorLocation);
            tutorPrice = itemView.findViewById(R.id.tutorPrice);
        }
    }
}