package com.example.soen345_winter2026.events;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.soen345_winter2026.R;
import com.example.soen345_winter2026.databinding.AdminItemEventBinding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminEventAdapter extends RecyclerView.Adapter<AdminEventAdapter.EventViewHolder> {

    private List<Event> events;
    private final OnEditClickListener onEditClick;
    private final OnCancelClickListener onCancelClick;

    // Interfaces for button actions
    public interface OnEditClickListener {
        void onEdit(Event event);
    }

    public interface OnCancelClickListener {
        void onCancel(Event event);
    }

    private final Map<String, Integer> categoryBadgeColors = new HashMap<String, Integer>() {{
        put("Concert", R.drawable.cr19370800bf6339a);
        put("Movie", R.drawable.cr19370800b2196f3);
        put("Sports", R.drawable.cr19370800b00c950);
        put("Travel", R.drawable.cr19370800bf6339a);
    }};

    public AdminEventAdapter(List<Event> events,
                             OnEditClickListener onEditClick,
                             OnCancelClickListener onCancelClick) {
        this.events = events;
        this.onEditClick = onEditClick;
        this.onCancelClick = onCancelClick;
    }

    class EventViewHolder extends RecyclerView.ViewHolder {
        private final AdminItemEventBinding binding;

        public EventViewHolder(AdminItemEventBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Event event) {
            binding.tvTitle.setText(event.getTitle());
            binding.tvCategory.setText(event.getCategory());
            binding.tvDate.setText(event.getDate());
            binding.tvLocation.setText(event.getLocation());

            // Seats logic
            if (event.isSoldOut()) {
                binding.tvSeats.setText("Sold Out");
                binding.tvSeats.setTextColor(Color.parseColor("#E53935"));
            } else {
                binding.tvSeats.setText("Available: " + event.getAvailableSeats() + " seats");
                binding.tvSeats.setTextColor(Color.parseColor("#4CAF50"));
            }

            // Badge color
            int badgeRes = categoryBadgeColors.containsKey(event.getCategory())
                    ? categoryBadgeColors.get(event.getCategory())
                    : R.drawable.cr19370800bf6339a;
            binding.llCategoryBadge.setBackgroundResource(badgeRes);

            // Image
            if (event.getImageUrl() != null && !event.getImageUrl().isEmpty()) {
                Glide.with(binding.getRoot().getContext())
                        .load(event.getImageUrl())
                        .centerCrop()
                        .into(binding.ivEventImage);
            }

            // ✅ Edit button
            binding.btnEdit.setOnClickListener(v -> {
                if (onEditClick != null) {
                    onEditClick.onEdit(event);
                }
            });

            // ❌ Cancel button
            binding.btnCancel.setOnClickListener(v -> {
                if (onCancelClick != null) {
                    onCancelClick.onCancel(event);
                }
            });
        }
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdminItemEventBinding binding = AdminItemEventBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new EventViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        holder.bind(events.get(position));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void updateEvents(List<Event> newEvents) {
        this.events = newEvents;
        notifyDataSetChanged();
    }
}