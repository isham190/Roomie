package com.ish.roomie.details;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ish.roomie.R;
import com.ish.roomie.service.RetrofitClient;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Recyclerview adapter for details image pager.
 */
public class DetailsRecyclerAdapter extends RecyclerView.Adapter<DetailsRecyclerAdapter.DetailsViewHolder> {

    private final String TAG = getClass().getSimpleName();
    private final List<String> imageList;

    public DetailsRecyclerAdapter(List<String> imagesList) {
        this.imageList = imagesList;
    }


    @Override
    public void onBindViewHolder(final DetailsViewHolder holder, final int position) {
        Picasso.with(holder.imageView.getContext()).load(RetrofitClient.BASE_URL+imageList.get(position)).placeholder(R.drawable.holder).into(holder.imageView);
    }

    @Override
    public DetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.details_list_item, parent, false);
        return new DetailsViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return imageList != null ? imageList.size() : 0;
    }

    class DetailsViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        DetailsViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.details_list_item_imageview);
        }
    }
}