package com.khlafawi.neos.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ArticleProvider extends ContentProvider {

    private static final int ARTICLE = 100;
    private static final int ARTICLE_TITLE = 101;

    private static final UriMatcher UriMatcher = buildUriMatcher();
    private FavoriteDB OpenHelper;

    @Override
    public boolean onCreate() {
        OpenHelper = new FavoriteDB(getContext());
        return true;
    }

    public static UriMatcher buildUriMatcher() {
        String content = ArticleContract.CONTENT_AUTHORITY;
        UriMatcher matcher = new UriMatcher(android.content.UriMatcher.NO_MATCH);
        matcher.addURI(content, ArticleContract.PATH_ARTICLE, ARTICLE);
        matcher.addURI(content, ArticleContract.PATH_ARTICLE + "/#", ARTICLE_TITLE);
        return matcher;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = OpenHelper.getWritableDatabase();
        long _id;
        Cursor retCursor;
        switch (UriMatcher.match(uri)) {
            case ARTICLE:
                retCursor = db.query(
                        ArticleContract.ArticleEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case ARTICLE_TITLE:
                _id = ContentUris.parseId(uri);
                retCursor = db.query(
                        ArticleContract.ArticleEntry.TABLE_NAME,
                        projection,
                        ArticleContract.ArticleEntry._ID + " = ?",
                        new String[]{String.valueOf(_id)},
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (UriMatcher.match(uri)) {
            case ARTICLE:
                return ArticleContract.ArticleEntry.CONTENT_TYPE;
            case ARTICLE_TITLE:
                return ArticleContract.ArticleEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = OpenHelper.getWritableDatabase();
        long _id;
        Uri returnUri;

        switch (UriMatcher.match(uri)) {
            case ARTICLE:
                _id = db.insert(ArticleContract.ArticleEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = ArticleContract.ArticleEntry.buildMovieUri(_id);
                } else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = OpenHelper.getWritableDatabase();
        int rows;

        switch (UriMatcher.match(uri)) {
            case ARTICLE:
                rows = db.delete(ArticleContract.ArticleEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (selection == null || rows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = OpenHelper.getWritableDatabase();
        int rows;

        switch (UriMatcher.match(uri)) {
            case ARTICLE:
                rows = db.update(ArticleContract.ArticleEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rows;
    }
}
