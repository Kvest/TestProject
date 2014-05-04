package com.kvest.testproject.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import com.kvest.testproject.CatalogSQLiteHelper;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 03.05.14
 * Time: 21:36
 * To change this template use File | Settings | File Templates.
 */
public class CatalogProvider extends ContentProvider {
    private CatalogSQLiteHelper sqlStorage;

    private static final int CATALOGS_URI_INDICATOR = 1;
    private static final int CATALOG_URI_INDICATOR = 2;
    private static final int ITEMS_URI_INDICATOR = 3;
    private static final int ITEM_URI_INDICATOR = 4;

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(CatalogProviderContract.AUTHORITY, CatalogProviderContract.CATALOGS_PATH, CATALOGS_URI_INDICATOR);
        uriMatcher.addURI(CatalogProviderContract.AUTHORITY, CatalogProviderContract.CATALOGS_PATH + "/#", CATALOG_URI_INDICATOR);
        uriMatcher.addURI(CatalogProviderContract.AUTHORITY, CatalogProviderContract.ITEMS_PATH, ITEMS_URI_INDICATOR);
        uriMatcher.addURI(CatalogProviderContract.AUTHORITY, CatalogProviderContract.ITEMS_PATH + "/#", ITEM_URI_INDICATOR);
    }

    @Override
    public boolean onCreate() {
        sqlStorage = new CatalogSQLiteHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        switch (uriMatcher.match(uri)) {
            case CATALOGS_URI_INDICATOR :
                queryBuilder.setTables(CatalogProviderContract.Tables.CatalogItems.TABLE_NAME);
                break;
            case CATALOG_URI_INDICATOR :
                queryBuilder.setTables(CatalogProviderContract.Tables.CatalogItems.TABLE_NAME);
                queryBuilder.appendWhere(CatalogProviderContract.Tables.CatalogItems._ID + "=" + uri.getLastPathSegment());
                break;
            case ITEMS_URI_INDICATOR :
                queryBuilder.setTables(CatalogProviderContract.Tables.Items.TABLE_NAME);
                break;
            case ITEM_URI_INDICATOR :
                queryBuilder.setTables(CatalogProviderContract.Tables.Items.TABLE_NAME);
                queryBuilder.appendWhere(CatalogProviderContract.Tables.Items._ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown uri = " + uri);
        }

        //make query
        SQLiteDatabase db = sqlStorage.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        // Make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = sqlStorage.getWritableDatabase();
        long rowId;

        switch (uriMatcher.match(uri)) {
            case CATALOGS_URI_INDICATOR :
                //replace works as insert or update
                rowId = db.replace(CatalogProviderContract.Tables.CatalogItems.TABLE_NAME, null, values);
                if (rowId > 0)
                {
                    Uri resultUri = ContentUris.withAppendedId(uri, rowId);
                    getContext().getContentResolver().notifyChange(resultUri, null);
                    return resultUri;
                }
                break;
            case ITEMS_URI_INDICATOR :
                //replace works as insert or update
                rowId = db.replace(CatalogProviderContract.Tables.Items.TABLE_NAME, null, values);
                if (rowId > 0)
                {
                    Uri resultUri = ContentUris.withAppendedId(uri, rowId);
                    getContext().getContentResolver().notifyChange(resultUri, null);
                    return resultUri;
                }
                break;
        }

        throw new IllegalArgumentException("Faild to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted;
        boolean hasSelection = !TextUtils.isEmpty(selection);
        SQLiteDatabase db = sqlStorage.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case CATALOGS_URI_INDICATOR :
                rowsDeleted = db.delete(CatalogProviderContract.Tables.CatalogItems.TABLE_NAME, selection, selectionArgs);
                break;
            case CATALOG_URI_INDICATOR :
                rowsDeleted = db.delete(CatalogProviderContract.Tables.CatalogItems.TABLE_NAME,
                                        CatalogProviderContract.Tables.CatalogItems._ID + "=" + uri.getLastPathSegment() +
                                        (hasSelection ? (" AND " + selection) : ""), (hasSelection ? selectionArgs : null));
                break;
            case ITEMS_URI_INDICATOR :
                rowsDeleted = db.delete(CatalogProviderContract.Tables.Items.TABLE_NAME, selection, selectionArgs);
                break;
            case ITEM_URI_INDICATOR :
                rowsDeleted = db.delete(CatalogProviderContract.Tables.Items.TABLE_NAME,
                                        CatalogProviderContract.Tables.Items._ID + "=" + uri.getLastPathSegment() +
                                        (hasSelection ? (" AND " + selection) : ""), (hasSelection ? selectionArgs : null));
                break;
            default:
                throw new IllegalArgumentException("Unknown uri = " + uri);
        }

        if (rowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowsUpdated;
        boolean hasSelection = !TextUtils.isEmpty(selection);
        SQLiteDatabase db = sqlStorage.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case CATALOGS_URI_INDICATOR :
                rowsUpdated = db.update(CatalogProviderContract.Tables.CatalogItems.TABLE_NAME, values, selection, selectionArgs);
                break;
            case CATALOG_URI_INDICATOR :
                rowsUpdated = db.update(CatalogProviderContract.Tables.CatalogItems.TABLE_NAME, values,
                                        CatalogProviderContract.Tables.CatalogItems._ID + "=" + uri.getLastPathSegment() +
                                        (hasSelection ? (" AND " + selection) : ""), (hasSelection ? selectionArgs : null));
                break;
            case ITEMS_URI_INDICATOR :
                rowsUpdated = db.update(CatalogProviderContract.Tables.Items.TABLE_NAME, values, selection, selectionArgs);
                break;
            case ITEM_URI_INDICATOR :
                rowsUpdated = db.update(CatalogProviderContract.Tables.Items.TABLE_NAME, values,
                                        CatalogProviderContract.Tables.Items._ID + "=" + uri.getLastPathSegment() +
                                        (hasSelection ? (" AND " + selection) : ""), (hasSelection ? selectionArgs : null));
                break;
            default:
                throw new IllegalArgumentException("Unknown uri = " + uri);
        }

        if (rowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException("CatalogProvider doesn't implements getType() method");
    }
}
