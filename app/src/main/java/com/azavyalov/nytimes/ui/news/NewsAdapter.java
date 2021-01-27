package com.azavyalov.nytimes.ui.news;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.azavyalov.nytimes.R;
import com.azavyalov.nytimes.data.NewsItem;
import com.azavyalov.nytimes.util.Util;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private List<NewsItem> items;

    private final RequestManager imageLoader;
    @NonNull
    private final LayoutInflater inflater;
    @Nullable
    private final OnItemClickListener clickListener;

    public NewsAdapter(@NonNull Context context, @NonNull OnItemClickListener clickListener) {

        this.inflater = LayoutInflater.from(context);
        this.clickListener = clickListener;

        RequestOptions imageOption = new RequestOptions()
                .placeholder(R.drawable.image_placeholder)
                .fallback(R.drawable.image_placeholder)
                .centerCrop();
        this.imageLoader = Glide.with(context).applyDefaultRequestOptions(imageOption);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.news_item, parent, false), clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public void replaceItems(@NonNull List<NewsItem> newsItems) {
        this.items = newsItems;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(@NonNull NewsItem newsItem);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final TextView categoryView;
        private final TextView titleView;
        private final TextView previewView;
        private final TextView dateView;

        public ViewHolder(@NonNull final View itemView, final OnItemClickListener listener) {
            super(itemView);
            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(items.get(position));
                }
            });
            imageView = itemView.findViewById(R.id.news_item_image);
            categoryView = itemView.findViewById(R.id.news_item_category);
            titleView = itemView.findViewById(R.id.news_item_title);
            previewView = itemView.findViewById(R.id.news_item_preview);
            dateView = itemView.findViewById(R.id.news_item_date);
        }

        public void bind(NewsItem newsItem) {
            imageLoader
                    .load(newsItem.getImageUrl())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageView);
            categoryView.setText(newsItem.getCategory());
            titleView.setText(newsItem.getTitle());
            previewView.setText(newsItem.getPreviewText());
            dateView.setText(Util.formatDateString(newsItem.getPublishDate()));
        }
    }
}
