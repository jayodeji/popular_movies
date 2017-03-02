package com.jayodeji.android.popularmovies.dbcontract;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by joshuaadeyemi on 2/26/17.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movie.db";

    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MovieContract.MovieEntry.getTableCreationSql());
        db.execSQL(MovieContract.TrailerEntry.getTableCreationSql());
        db.execSQL(MovieContract.ReviewEntry.getTableCreationSql());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        final String SQL_DROP_TABLES = MovieContract.MovieEntry.getTableDeletionSql() +
                MovieContract.TrailerEntry.getTableDeletionSql() +
                MovieContract.ReviewEntry.getTableDeletionSql();

        db.execSQL(SQL_DROP_TABLES);
        onCreate(db);
    }
}
