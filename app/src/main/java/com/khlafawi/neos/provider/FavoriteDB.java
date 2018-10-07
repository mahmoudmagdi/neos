package com.khlafawi.neos.provider;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.khlafawi.neos.model.ArticleClass;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.khlafawi.neos.provider.ArticleContract.ArticleEntry.COLUMN_AUTHOR;
import static com.khlafawi.neos.provider.ArticleContract.ArticleEntry.COLUMN_CONTENT;
import static com.khlafawi.neos.provider.ArticleContract.ArticleEntry.COLUMN_DESCRIPTION;
import static com.khlafawi.neos.provider.ArticleContract.ArticleEntry.COLUMN_ID;
import static com.khlafawi.neos.provider.ArticleContract.ArticleEntry.COLUMN_PUBLISHED_DATE_AT;
import static com.khlafawi.neos.provider.ArticleContract.ArticleEntry.COLUMN_TITLE;
import static com.khlafawi.neos.provider.ArticleContract.ArticleEntry.COLUMN_URL;
import static com.khlafawi.neos.provider.ArticleContract.ArticleEntry.COLUMN_URL_TO_IMAGE;
import static com.khlafawi.neos.provider.ArticleContract.ArticleEntry.TABLE_NAME;

public class FavoriteDB extends SQLiteOpenHelper {


    private static final int DB_VERSION = 1;
    private static String DB_NAME = "favorites.db";
    private Context mContext;

    public FavoriteDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME
                + " (" + COLUMN_ID + " integer primary key , "
                + COLUMN_TITLE + " text , "
                + COLUMN_AUTHOR + " text , "
                + COLUMN_URL + " text , "
                + COLUMN_URL_TO_IMAGE + " text , "
                + COLUMN_PUBLISHED_DATE_AT + " text , "
                + COLUMN_CONTENT + " text , "
                + COLUMN_DESCRIPTION + " text );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<ArticleClass> refreshValuesFromContentProvider() {

        ArrayList<ArticleClass> favList = new ArrayList<>();


        CursorLoader cursorLoader = new CursorLoader(mContext, ArticleContract.ArticleEntry.CONTENT_URI, null, null, null, null);
        Cursor c = cursorLoader.loadInBackground();

        String pattern = "yyyy-MM-dd";
        DateFormat df = new SimpleDateFormat(pattern, Locale.getDefault());

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            ArticleClass news = new ArticleClass();
            news.setTitle(c.getString(c.getColumnIndex(ArticleContract.ArticleEntry.COLUMN_TITLE)));
            news.setAuthor(c.getString(c.getColumnIndex(ArticleContract.ArticleEntry.COLUMN_AUTHOR)));
            news.setUrl(c.getString(c.getColumnIndex(ArticleContract.ArticleEntry.COLUMN_URL)));
            news.setUrlToImage(c.getString(c.getColumnIndex(ArticleContract.ArticleEntry.COLUMN_URL_TO_IMAGE)));
//            try {
//                news.setPublishedAt(df.parse(c.getString(c.getColumnIndex(ArticleContract.ArticleEntry.COLUMN_PUBLISHED_DATE_AT))));
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
            news.setContent(c.getString(c.getColumnIndex(ArticleContract.ArticleEntry.COLUMN_CONTENT)));
            news.setDescription(c.getString(c.getColumnIndex(ArticleContract.ArticleEntry.COLUMN_DESCRIPTION)));
            favList.add(news);
        }
        return favList;
    }

    public boolean CheckIsDataAlreadyInDBorNot(String DatabaseField, String fieldValue) {
        SQLiteDatabase favDB = this.getWritableDatabase();
        String Query = "Select * from favorites where " + DatabaseField + " = ?";
        favDB.rawQuery(Query, new String[]{fieldValue});

        Cursor cursor = favDB.rawQuery(Query, new String[]{fieldValue});
        cursor.moveToFirst();
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
}
