package com.kvest.testproject.provider;

import android.net.Uri;
import android.provider.BaseColumns;
import com.kvest.testproject.utils.Constants;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 03.05.14
 * Time: 21:36
 * To change this template use File | Settings | File Templates.
 */
public class CatalogProviderContract {
    public static final String AUTHORITY = "com.kvest.testproject.provider.CatalogProviderContract";

    public static final String CATALOGS_PATH = "catalogs";
    public static final String ITEMS_PATH = "items";

    public static final Uri CATALOGS_URI = Uri.parse("content://" + AUTHORITY + "/" + CATALOGS_PATH);
    public static final Uri ITEMS_URI = Uri.parse("content://" + AUTHORITY + "/" + ITEMS_PATH);

    public interface Tables {
        public interface CatalogItems extends BaseColumns {
            public static final String TABLE_NAME = "catalog_items";

            public static final String TITLE_COLUMN = "title";
            public static final String ITEM_TYPE_COLUMN = "item_type";
            public static final String PARENT_CATALOG_COLUMN = "parent_catalog";

            public static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TITLE_COLUMN + " TEXT, " +
                    ITEM_TYPE_COLUMN + " INTEGER, " +
                    PARENT_CATALOG_COLUMN + " INTEGER DEFAULT " + Constants.ROOT_CATALOG_ID + ");";

            public static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
        }

        public interface Items extends BaseColumns {
            public static final String TABLE_NAME = "items";

            public static final String DESCRIPTION_COLUMN = "description";

            public static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY, " +
                    DESCRIPTION_COLUMN + " TEXT, " +
                    "FOREIGN KEY(" + _ID + ") REFERENCES " + CatalogItems.TABLE_NAME + "(" + CatalogItems._ID + ") ON UPDATE CASCADE ON DELETE CASCADE);";

            public static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
        }
//        CREATE TABLE catalog_items (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, item_type integer, parent_catalog integer DEFAULT -1);
//        CREATE TABLE items (_id INTEGER PRIMARY KEY, description TEXT, FOREIGN KEY(_id) REFERENCES catalog_items(_id) ON UPDATE CASCADE ON DELETE CASCADE);
    }
}
