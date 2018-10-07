package com.khlafawi.neos.provider;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class ArticleContract {

    static final String CONTENT_AUTHORITY = "com.khlafawi.neos.provider";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    static final String PATH_ARTICLE = "article";

    public static final class ArticleEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ARTICLE).build();
        static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_URI + "/" + PATH_ARTICLE;
        static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_ARTICLE;

        static final String TABLE_NAME = "favorites";
        static final String COLUMN_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_URL_TO_IMAGE = "urlToImage";
        public static final String COLUMN_PUBLISHED_DATE_AT = "publishedAt";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_DESCRIPTION = "description";

        static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
