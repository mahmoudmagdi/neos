package com.khlafawi.neos.adapters;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.khlafawi.neos.R;
import com.khlafawi.neos.model.ArticleClass;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private List<ArticleClass> newsList;
    private LayoutInflater inflater;
    private AppCompatActivity context;

    public FavoriteAdapter(AppCompatActivity context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_favorite, parent, false);
        final FavoriteAdapter.FavoriteViewHolder viewHolder = new FavoriteAdapter.FavoriteViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getAdapterPosition();
                ArticleClass article = newsList.get(position);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl()));
                context.startActivity(browserIntent);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        ArticleClass news = newsList.get(position);
        if (news.getUrlToImage() != null) {
            Picasso.with(context)
                    .load(news.getUrlToImage())
                    .placeholder(context.getResources().getDrawable(R.drawable.placeholder))
                    .into(holder.img);
        }

        if (news.getTitle() != null) {
            holder.title.setText(news.getTitle());
        }
    }

    @Override
    public int getItemCount() {
        return (newsList == null) ? 0 : newsList.size();
    }

    public void setNewsList(List<ArticleClass> newsList) {
        this.newsList = new ArrayList<>();
        this.newsList.addAll(newsList);
        notifyDataSetChanged();
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView title;

        FavoriteViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            title = itemView.findViewById(R.id.title);
        }
    }
}
