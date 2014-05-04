package com.kvest.testproject.provider;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import com.kvest.testproject.provider.CatalogProviderContract;
import com.kvest.testproject.utils.Constants;

import java.lang.Long;
import java.lang.String;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 04.05.14
 * Time: 14:51
 * To change this template use File | Settings | File Templates.
 */
public class CatalogProviderTest extends ProviderTestCase2<CatalogProvider> {
    public CatalogProviderTest() {
        super(CatalogProvider.class, CatalogProviderContract.AUTHORITY);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        //test provider is clean
        String[] catalogsProjection = {CatalogProviderContract.Tables.CatalogItems._ID};
        Cursor cursor = getMockContentResolver().query(CatalogProviderContract.CATALOGS_URI, catalogsProjection,
                null, null, null);
        try {
            assertEquals(0, cursor.getCount());
        } finally {
            cursor.close();
        }
        String[] itemsProjection = {CatalogProviderContract.Tables.Items._ID};
        cursor = getMockContentResolver().query(CatalogProviderContract.ITEMS_URI, itemsProjection,
                null, null, null);
        try {
            assertEquals(0, cursor.getCount());
        } finally {
            cursor.close();
        }
    }

    public void testCatalogsInsert() {
        ContentValues cv = new ContentValues(2);
        cv.put(CatalogProviderContract.Tables.CatalogItems.TITLE_COLUMN, "catalog1");
        cv.put(CatalogProviderContract.Tables.CatalogItems.ITEM_TYPE_COLUMN, Constants.CatalogType.CATALOG);
        getMockContentResolver().insert(CatalogProviderContract.CATALOGS_URI, cv);
        String[] catalogsProjection = {CatalogProviderContract.Tables.CatalogItems._ID};
        Cursor cursor = getMockContentResolver().query(CatalogProviderContract.CATALOGS_URI, catalogsProjection,
                null, null, null);
        try {
            assertEquals(1, cursor.getCount());
        } finally {
            cursor.close();
        }

        cv.clear();
        cv.put(CatalogProviderContract.Tables.CatalogItems.TITLE_COLUMN, "catalog2");
        cv.put(CatalogProviderContract.Tables.CatalogItems.ITEM_TYPE_COLUMN, Constants.CatalogType.CATALOG);
        getMockContentResolver().insert(CatalogProviderContract.CATALOGS_URI, cv);
        cursor = getMockContentResolver().query(CatalogProviderContract.CATALOGS_URI, catalogsProjection,
                null, null, null);
        try {
            assertEquals(2, cursor.getCount());
        } finally {
            cursor.close();
        }
    }

    public void testCatalogsQuery() {
        //insert three items
        int rowsCount = 3;
        for (int i = 0; i < rowsCount; ++i) {
            ContentValues cv = new ContentValues(2);
            cv.put(CatalogProviderContract.Tables.CatalogItems.TITLE_COLUMN, "catalog" + i);
            cv.put(CatalogProviderContract.Tables.CatalogItems.ITEM_TYPE_COLUMN, Constants.CatalogType.CATALOG);
            getMockContentResolver().insert(CatalogProviderContract.CATALOGS_URI, cv);
        }

        //check count
        String[] catalogsProjection = {CatalogProviderContract.Tables.CatalogItems._ID};
        Cursor cursor = getMockContentResolver().query(CatalogProviderContract.CATALOGS_URI, catalogsProjection,
                null, null, null);
        try {
            assertEquals(rowsCount, cursor.getCount());
        } finally {
            cursor.close();
        }
    }

    public void testCatalogsQueryOneItem() {
        String targetName = "target";
        int targetType = Constants.CatalogType.CATALOG;
        //insert items
        ContentValues cv = new ContentValues(2);
        cv.put(CatalogProviderContract.Tables.CatalogItems.TITLE_COLUMN, "catalog1");
        cv.put(CatalogProviderContract.Tables.CatalogItems.ITEM_TYPE_COLUMN, Constants.CatalogType.CATALOG);
        getMockContentResolver().insert(CatalogProviderContract.CATALOGS_URI, cv);
        cv.clear();
        cv.put(CatalogProviderContract.Tables.CatalogItems.TITLE_COLUMN, targetName);
        cv.put(CatalogProviderContract.Tables.CatalogItems.ITEM_TYPE_COLUMN, targetType);
        Uri targetUri = getMockContentResolver().insert(CatalogProviderContract.CATALOGS_URI, cv);
        cv.clear();
        cv.put(CatalogProviderContract.Tables.CatalogItems.TITLE_COLUMN, "catalog2");
        cv.put(CatalogProviderContract.Tables.CatalogItems.ITEM_TYPE_COLUMN, Constants.CatalogType.CATALOG);
        getMockContentResolver().insert(CatalogProviderContract.CATALOGS_URI, cv);

        String[] catalogsProjection = {CatalogProviderContract.Tables.CatalogItems._ID,
                                       CatalogProviderContract.Tables.CatalogItems.TITLE_COLUMN,
                                       CatalogProviderContract.Tables.CatalogItems.ITEM_TYPE_COLUMN,
                                       CatalogProviderContract.Tables.CatalogItems.PARENT_CATALOG_COLUMN};
        Cursor cursor = getMockContentResolver().query(targetUri, catalogsProjection, null, null, null);
        try {
            assertEquals(1, cursor.getCount());

            //check content
            cursor.moveToFirst();
            assertFalse(cursor.isAfterLast());
            assertEquals(targetName, cursor.getString(cursor.getColumnIndex(CatalogProviderContract.Tables.CatalogItems.TITLE_COLUMN)));
            assertEquals(targetType, cursor.getInt(cursor.getColumnIndex(CatalogProviderContract.Tables.CatalogItems.ITEM_TYPE_COLUMN)));
            assertEquals(Constants.ROOT_CATALOG_ID, cursor.getInt(cursor.getColumnIndex(CatalogProviderContract.Tables.CatalogItems.PARENT_CATALOG_COLUMN)));
        } finally {
            cursor.close();
        }
    }

    public void testCatalogsDelete() {
        //insert 10 items
        int rowsCount = 10;
        for (int i = 0; i < rowsCount; ++i) {
            ContentValues cv = new ContentValues(2);
            cv.put(CatalogProviderContract.Tables.CatalogItems.TITLE_COLUMN, "catalog" + i);
            cv.put(CatalogProviderContract.Tables.CatalogItems.ITEM_TYPE_COLUMN, Constants.CatalogType.CATALOG);
            getMockContentResolver().insert(CatalogProviderContract.CATALOGS_URI, cv);
        }

        //check count
        String[] catalogsProjection = {CatalogProviderContract.Tables.CatalogItems._ID};
        Cursor cursor = getMockContentResolver().query(CatalogProviderContract.CATALOGS_URI, catalogsProjection,
                null, null, null);
        try {
            assertEquals(rowsCount, cursor.getCount());
        } finally {
            cursor.close();
        }

        //delete all rows
        assertEquals(rowsCount, getMockContentResolver().delete(CatalogProviderContract.CATALOGS_URI, null, null));

        //check tabe is clean
        cursor = getMockContentResolver().query(CatalogProviderContract.CATALOGS_URI, catalogsProjection,
                null, null, null);
        try {
            assertEquals(0, cursor.getCount());
        } finally {
            cursor.close();
        }
    }

    public void testCatalogsDeleteOneItem() {
        //insert items
        ContentValues cv = new ContentValues(2);
        cv.put(CatalogProviderContract.Tables.CatalogItems.TITLE_COLUMN, "catalog1");
        cv.put(CatalogProviderContract.Tables.CatalogItems.ITEM_TYPE_COLUMN, Constants.CatalogType.CATALOG);
        getMockContentResolver().insert(CatalogProviderContract.CATALOGS_URI, cv);
        cv.clear();
        cv.put(CatalogProviderContract.Tables.CatalogItems.TITLE_COLUMN, "catalog2");
        cv.put(CatalogProviderContract.Tables.CatalogItems.ITEM_TYPE_COLUMN, Constants.CatalogType.CATALOG);
        Uri targetUri = getMockContentResolver().insert(CatalogProviderContract.CATALOGS_URI, cv);
        cv.clear();
        cv.put(CatalogProviderContract.Tables.CatalogItems.TITLE_COLUMN, "catalog2");
        cv.put(CatalogProviderContract.Tables.CatalogItems.ITEM_TYPE_COLUMN, Constants.CatalogType.CATALOG);
        getMockContentResolver().insert(CatalogProviderContract.CATALOGS_URI, cv);

        //check count
        String[] catalogsProjection = {CatalogProviderContract.Tables.CatalogItems._ID};
        Cursor cursor = getMockContentResolver().query(CatalogProviderContract.CATALOGS_URI, catalogsProjection, null, null, null);
        try {
            assertEquals(3, cursor.getCount());
        } finally {
            cursor.close();
        }

        //delete one item
        assertEquals(1, getMockContentResolver().delete(targetUri, null, null));

        //check count
        cursor = getMockContentResolver().query(CatalogProviderContract.CATALOGS_URI, catalogsProjection,
                null, null, null);
        try {
            assertEquals(2, cursor.getCount());
        } finally {
            cursor.close();
        }
    }

    public void testCatalogsUpdate() {
        //insert 10 items
        int rowsCount = 10;
        for (int i = 0; i < rowsCount; ++i) {
            ContentValues cv = new ContentValues(2);
            cv.put(CatalogProviderContract.Tables.CatalogItems.TITLE_COLUMN, "catalog" + i);
            cv.put(CatalogProviderContract.Tables.CatalogItems.ITEM_TYPE_COLUMN, Constants.CatalogType.CATALOG);
            getMockContentResolver().insert(CatalogProviderContract.CATALOGS_URI, cv);
        }

        //update all records
        String targetName = "target";
        int targetType = Constants.CatalogType.ITEM;
        ContentValues cv = new ContentValues(2);
        cv.put(CatalogProviderContract.Tables.CatalogItems.TITLE_COLUMN, targetName);
        cv.put(CatalogProviderContract.Tables.CatalogItems.ITEM_TYPE_COLUMN, targetType);
        assertEquals(rowsCount, getMockContentResolver().update(CatalogProviderContract.CATALOGS_URI, cv, null, null));

        String[] catalogsProjection = {CatalogProviderContract.Tables.CatalogItems._ID,
                CatalogProviderContract.Tables.CatalogItems.TITLE_COLUMN,
                CatalogProviderContract.Tables.CatalogItems.ITEM_TYPE_COLUMN,
                CatalogProviderContract.Tables.CatalogItems.PARENT_CATALOG_COLUMN};
        Cursor cursor = getMockContentResolver().query(CatalogProviderContract.CATALOGS_URI, catalogsProjection, null, null, null);
        try {
            assertEquals(rowsCount, cursor.getCount());

            //check content
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                assertEquals(targetName, cursor.getString(cursor.getColumnIndex(CatalogProviderContract.Tables.CatalogItems.TITLE_COLUMN)));
                assertEquals(targetType, cursor.getInt(cursor.getColumnIndex(CatalogProviderContract.Tables.CatalogItems.ITEM_TYPE_COLUMN)));
                assertEquals(Constants.ROOT_CATALOG_ID, cursor.getInt(cursor.getColumnIndex(CatalogProviderContract.Tables.CatalogItems.PARENT_CATALOG_COLUMN)));

                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
    }

    public void testCatalogsUpdateOneItem() {
        //insert items
        ContentValues cv = new ContentValues(2);
        cv.put(CatalogProviderContract.Tables.CatalogItems.TITLE_COLUMN, "catalog1");
        cv.put(CatalogProviderContract.Tables.CatalogItems.ITEM_TYPE_COLUMN, Constants.CatalogType.CATALOG);
        getMockContentResolver().insert(CatalogProviderContract.CATALOGS_URI, cv);
        cv.clear();
        cv.put(CatalogProviderContract.Tables.CatalogItems.TITLE_COLUMN, "catalog2");
        cv.put(CatalogProviderContract.Tables.CatalogItems.ITEM_TYPE_COLUMN, Constants.CatalogType.CATALOG);
        Uri targetUri = getMockContentResolver().insert(CatalogProviderContract.CATALOGS_URI, cv);
        cv.clear();
        cv.put(CatalogProviderContract.Tables.CatalogItems.TITLE_COLUMN, "catalog2");
        cv.put(CatalogProviderContract.Tables.CatalogItems.ITEM_TYPE_COLUMN, Constants.CatalogType.CATALOG);
        getMockContentResolver().insert(CatalogProviderContract.CATALOGS_URI, cv);

        //update target item
        String targetName = "target";
        int targetType = Constants.CatalogType.ITEM;
        cv = new ContentValues(2);
        cv.put(CatalogProviderContract.Tables.CatalogItems.TITLE_COLUMN, targetName);
        cv.put(CatalogProviderContract.Tables.CatalogItems.ITEM_TYPE_COLUMN, targetType);
        assertEquals(1, getMockContentResolver().update(targetUri, cv, null, null));

        //check content after update
        long targetId = Long.parseLong(targetUri.getLastPathSegment());
        String[] catalogsProjection = {CatalogProviderContract.Tables.CatalogItems._ID,
                CatalogProviderContract.Tables.CatalogItems.TITLE_COLUMN,
                CatalogProviderContract.Tables.CatalogItems.ITEM_TYPE_COLUMN,
                CatalogProviderContract.Tables.CatalogItems.PARENT_CATALOG_COLUMN};
        Cursor cursor = getMockContentResolver().query(CatalogProviderContract.CATALOGS_URI, catalogsProjection, null, null, null);
        try {
            //check content
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                if (cursor.getLong(cursor.getColumnIndex(CatalogProviderContract.Tables.CatalogItems._ID)) == targetId) {
                    assertEquals(targetName, cursor.getString(cursor.getColumnIndex(CatalogProviderContract.Tables.CatalogItems.TITLE_COLUMN)));
                    assertEquals(targetType, cursor.getInt(cursor.getColumnIndex(CatalogProviderContract.Tables.CatalogItems.ITEM_TYPE_COLUMN)));
                    assertEquals(Constants.ROOT_CATALOG_ID, cursor.getInt(cursor.getColumnIndex(CatalogProviderContract.Tables.CatalogItems.PARENT_CATALOG_COLUMN)));
                } else {
                    assertFalse(targetName.equals(cursor.getString(cursor.getColumnIndex(CatalogProviderContract.Tables.CatalogItems.TITLE_COLUMN))));
                    assertFalse(targetType == cursor.getInt(cursor.getColumnIndex(CatalogProviderContract.Tables.CatalogItems.ITEM_TYPE_COLUMN)));
                    assertEquals(Constants.ROOT_CATALOG_ID, cursor.getInt(cursor.getColumnIndex(CatalogProviderContract.Tables.CatalogItems.PARENT_CATALOG_COLUMN)));
                }

                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
    }
}
