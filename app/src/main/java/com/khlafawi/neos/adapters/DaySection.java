package com.khlafawi.neos.adapters;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.khlafawi.neos.R;
import com.khlafawi.neos.model.ArticleClass;
import com.khlafawi.neos.provider.ArticleContract;
import com.khlafawi.neos.provider.FavoriteDB;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

public class DaySection extends StatelessSection {

    private String title;
    private List<ArticleClass> list;
    private Context context;
    private FavoriteDB favDB;

    public DaySection(String title, List<ArticleClass> list, Context context, FavoriteDB favDB) {
        // call constructor with layout resources for this Section header and items
        super(SectionParameters.builder()
                .itemResourceId(R.layout.item_today)
                .headerResourceId(R.layout.item_group)
                .build());

        this.title = title;
        this.list = list;
        this.context = context;
        this.favDB = favDB;
    }

    @Override
    public int getContentItemsTotal() {
        return list.size(); // number of items of this section
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        // return a custom instance of ViewHolder for the items of this section
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder1, int position) {
        final ItemViewHolder holder = (ItemViewHolder) holder1;

        final ArticleClass article = list.get(position);
        if (article.getUrlToImage() != null || article.getAuthor().equals("null")) {
            Picasso.with(context)
                    .load(article.getUrlToImage())
                    .placeholder(context.getResources().getDrawable(R.drawable.placeholder))
                    .into(holder.news_img);
        }

        if (article.getAuthor() != null || !article.getAuthor().equals("null")) {
            holder.news_author.setText(article.getAuthor());
        } else {
            holder.author.setVisibility(View.GONE);
        }

        if (article.getTitle() != null) {
            holder.news_headline.setText(article.getTitle());
        }

        if (article.getPublishedAt() != null) {
            holder.news_date.setText(String.valueOf(article.getPublishedAt()).substring(0, 10));
        } else {
            holder.date.setVisibility(View.GONE);
        }

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Added to Fav.", Toast.LENGTH_LONG).show();

                if (favDB.CheckIsDataAlreadyInDBorNot(ArticleContract.ArticleEntry.COLUMN_TITLE, article.getTitle())) {
                    ContentResolver cr = context.getContentResolver();
                    cr.delete(ArticleContract.ArticleEntry.CONTENT_URI, ArticleContract.ArticleEntry.COLUMN_TITLE + " = ?", new String[]{article.getTitle()});
                    holder.like.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_fav_unchecked));
                    favDB.refreshValuesFromContentProvider();
                } else {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(ArticleContract.ArticleEntry.COLUMN_TITLE, article.getTitle());
                    contentValues.put(ArticleContract.ArticleEntry.COLUMN_AUTHOR, article.getAuthor());
                    contentValues.put(ArticleContract.ArticleEntry.COLUMN_URL, article.getUrl());
                    contentValues.put(ArticleContract.ArticleEntry.COLUMN_URL_TO_IMAGE, article.getUrlToImage());
                    contentValues.put(ArticleContract.ArticleEntry.COLUMN_PUBLISHED_DATE_AT, String.valueOf(article.getPublishedAt()));
                    contentValues.put(ArticleContract.ArticleEntry.COLUMN_CONTENT, article.getContent());
                    contentValues.put(ArticleContract.ArticleEntry.COLUMN_DESCRIPTION, article.getDescription());

                    context.getContentResolver().insert(ArticleContract.ArticleEntry.CONTENT_URI, contentValues);

                    holder.like.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_fav_checked));
                }
            }
        });

        holder.body.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl()));
                context.startActivity(browserIntent);
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

        // bind your header view here
        headerHolder.title.setText(title.substring(0, 10));
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;

        HeaderViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout body;
        LinearLayout author;
        LinearLayout date;
        TextView news_headline;
        TextView news_date;
        TextView news_author;
        ImageView news_img;
        ImageView like;

        ItemViewHolder(View itemView) {
            super(itemView);
            this.body = itemView.findViewById(R.id.body);
            this.author = itemView.findViewById(R.id.author);
            this.date = itemView.findViewById(R.id.date);
            this.news_headline = itemView.findViewById(R.id.news_headline);
            this.news_date = itemView.findViewById(R.id.news_date);
            this.news_author = itemView.findViewById(R.id.news_author);
            this.news_img = itemView.findViewById(R.id.news_img);
            this.like = itemView.findViewById(R.id.like);
        }
    }
}
