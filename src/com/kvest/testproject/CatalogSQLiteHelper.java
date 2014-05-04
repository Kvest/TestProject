package com.kvest.testproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.kvest.testproject.provider.CatalogProviderContract;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 04.05.14
 * Time: 0:31
 * To change this template use File | Settings | File Templates.
 */
public class CatalogSQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "catalog.db";
    private static final int DATABASE_VERSION = 1;

    public CatalogSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CatalogProviderContract.Tables.CatalogItems.CREATE_TABLE_SQL);
        db.execSQL(CatalogProviderContract.Tables.Items.CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Nothing to do
    }
}
