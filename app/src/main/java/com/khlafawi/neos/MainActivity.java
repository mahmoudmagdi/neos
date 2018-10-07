package com.khlafawi.neos;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.khlafawi.neos.adapters.DaySection;
import com.khlafawi.neos.adapters.FavoriteAdapter;
import com.khlafawi.neos.model.ArticleClass;
import com.khlafawi.neos.provider.FavoriteDB;
import com.khlafawi.neos.tools.HttpHandler;
import com.khlafawi.neos.tools.Strings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {

    private ImageView logo;

    private LinearLayout favorite_linear_layout;
    private RecyclerView favorite_recycler_view;
    private RecyclerView today_recycler_view;

    private FavoriteAdapter favoriteAdapter;


    private SectionedRecyclerViewAdapter sectionAdapter;
    private FavoriteDB favDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initActions();
        getFavorites();
        getTodayNews();
    }

    private void initView() {
        this.logo = findViewById(R.id.logo);
        this.favorite_linear_layout = findViewById(R.id.favorite_linear_layout);
        this.favorite_recycler_view = findViewById(R.id.favorite_recycler_view);
        this.today_recycler_view = findViewById(R.id.today_recycler_view);

        favDB = new FavoriteDB(MainActivity.this);
    }

    private void initActions() {
        this.logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void getFavorites() {
        LinearLayoutManager favoriteLinearLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        favoriteAdapter = new FavoriteAdapter(MainActivity.this);
        FavoriteDB favDB = new FavoriteDB(MainActivity.this);
        favoriteAdapter.setNewsList(favDB.refreshValuesFromContentProvider());
        if (favoriteAdapter.getItemCount() > 0) {
            favorite_linear_layout.setVisibility(VISIBLE);
            favorite_recycler_view.setLayoutManager(favoriteLinearLayoutManager);
            favorite_recycler_view.setAdapter(favoriteAdapter);
        }
    }

    private void getTodayNews() {
        new GetTodayNews().execute();
    }

    private class GetTodayNews extends AsyncTask<Void, Void, Void> {

        private String url = Strings.news_path;
        private String TAG = MainActivity.class.getSimpleName();
        private List<ArticleClass> newsList;
        private List<Date> dates;


        @Override
        protected Void doInBackground(Void... params) {
            HttpHandler sh = new HttpHandler();

            sectionAdapter = new SectionedRecyclerViewAdapter();
            dates = new ArrayList<>();
            newsList = new ArrayList<>();

            String jsonStr = sh.makeServiceCall(url);
            JSONObject json = null;
            try {
                json = new JSONObject(jsonStr);
                JSONArray jArray = json.getJSONArray("articles");

                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json_data = jArray.getJSONObject(i);


                    String pattern = "yyyy-MM-dd";
                    DateFormat df = new SimpleDateFormat(pattern, Locale.getDefault());


                    ArticleClass news = new ArticleClass();

                    if (!json_data.getString("author").equals("") || json_data.getString("author") != null)
                        news.setAuthor(json_data.getString("author"));

                    if (!json_data.getString("content").equals("") || json_data.getString("content") != null)
                        news.setContent(json_data.getString("content"));

                    if (!json_data.getString("description").equals("") || json_data.getString("description") != null)
                        news.setDescription(json_data.getString("description"));

                    if (!json_data.getString("publishedAt").equals("") || json_data.getString("publishedAt") != null) {
                        if (!dates.contains(df.parse(json_data.getString("publishedAt"))))
                            dates.add(df.parse(json_data.getString("publishedAt")));
                        news.setPublishedAt(df.parse(json_data.getString("publishedAt")));
                    }

                    if (!json_data.getString("title").equals("") || json_data.getString("title") != null)
                        news.setTitle(json_data.getString("title"));

                    if (!json_data.getString("url").equals("") || json_data.getString("url") != null)
                        news.setUrl(json_data.getString("url"));

                    if (!json_data.getString("urlToImage").equals("") || json_data.getString("urlToImage") != null)
                        news.setUrlToImage(json_data.getString("urlToImage"));

                    newsList.add(news);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            for (Date d : dates) {
                List<ArticleClass> news = getNewsWithDates(d);

                if (news.size() > 0) {
                    sectionAdapter.addSection(new DaySection(String.valueOf(d), news, MainActivity.this, favDB));
                }
            }

            today_recycler_view.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            today_recycler_view.setAdapter(sectionAdapter);
        }

        private List<ArticleClass> getNewsWithDates(Date date) {
            List<ArticleClass> news = new ArrayList<>();

            for (ArticleClass articleClass : newsList) {
                if (articleClass.getPublishedAt().equals(date)) {
                    news.add(articleClass);
                }
            }
            return news;
        }
    }
}
