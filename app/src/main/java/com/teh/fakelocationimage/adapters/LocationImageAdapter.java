package com.teh.fakelocationimage.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.teh.fakelocationimage.databinding.LocationImageContainerBinding;
import com.teh.fakelocationimage.listeners.LocationImageListener;
import com.teh.fakelocationimage.models.Location;

import java.util.List;

public class LocationImageAdapter extends RecyclerView.Adapter<LocationImageAdapter.LocationViewHolder> {

    private final List<Location> locationList;
    private final LocationImageListener locationImageListener;

    public LocationImageAdapter(List<Location> locationList, LocationImageListener locationImageListener) {
        this.locationList = locationList;
        this.locationImageListener = locationImageListener;
    }


    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LocationImageContainerBinding locationImageContainerBinding = LocationImageContainerBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new LocationViewHolder(locationImageContainerBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        holder.setData(locationList.get(position));
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public class LocationViewHolder extends RecyclerView.ViewHolder {
        private final LocationImageContainerBinding binding;
        public LocationViewHolder(LocationImageContainerBinding locationImageContainerBinding) {
            super(locationImageContainerBinding.getRoot());
            binding =locationImageContainerBinding;
        }

        public void setData(Location location) {
            Glide.with(binding.getRoot())
                    .load(location.getImageLink())
                    .into(binding.locationImage);
            binding.locationName.setText(location.getName());
            binding.locationImage.setOnClickListener(v -> locationImageListener.onClickLocationImageItem(location));
        }
    }
}
