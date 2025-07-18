package com.niquewrld.tutorspace.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.niquewrld.tutorspace.R;

import java.util.*;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagViewHolder> {
    public interface OnSelectionChangedListener {
        void onSelectionChanged(int selectedCount);
    }

    private List<String> tags;
    private List<String> filteredTags;
    private Set<String> globalSelectedTags;
    private int maxSelectable;
    private OnSelectionChangedListener listener;

    public TagAdapter(List<String> tags, Set<String> globalSelectedTags, int maxSelectable, OnSelectionChangedListener listener) {
        this.tags = tags;
        this.filteredTags = new ArrayList<>(tags);
        this.globalSelectedTags = globalSelectedTags;
        this.maxSelectable = maxSelectable;
        this.listener = listener;
    }

    public void filter(String query) {
        filteredTags.clear();
        if (query == null || query.trim().isEmpty()) {
            filteredTags.addAll(tags);
        } else {
            for (String tag : tags) {
                if (tag.toLowerCase().contains(query.toLowerCase())) {
                    filteredTags.add(tag);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tag_card, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        String tag = filteredTags.get(position);
        holder.tagName.setText(tag);

        holder.checkTag.setOnCheckedChangeListener(null);
        holder.checkTag.setChecked(globalSelectedTags.contains(tag));

        holder.itemView.setOnClickListener(v -> onTagClick(holder, tag));
        holder.checkTag.setOnClickListener(v -> onTagClick(holder, tag));
    }

    private void onTagClick(TagViewHolder holder, String tag) {
        if (globalSelectedTags.contains(tag)) {
            globalSelectedTags.remove(tag);
        } else {
            if (globalSelectedTags.size() >= maxSelectable) {
                holder.checkTag.setChecked(false); // Uncheck if limit hit
                Toast.makeText(holder.itemView.getContext(), "You can select up to " + maxSelectable + " modules.", Toast.LENGTH_SHORT).show();
                return;
            } else {
                globalSelectedTags.add(tag);
            }
        }
        notifyDataSetChanged();
        if (listener != null) {
            listener.onSelectionChanged(globalSelectedTags.size());
        }
    }

    @Override
    public int getItemCount() {
        return filteredTags.size();
    }

    public static class TagViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkTag;
        TextView tagName;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            checkTag = itemView.findViewById(R.id.checkTag);
            tagName = itemView.findViewById(R.id.tagName);
        }
    }
}