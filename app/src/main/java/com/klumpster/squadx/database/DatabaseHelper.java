package com.klumpster.squadx.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by BOX on 06-01-2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // Database Name
    public static final String DATABASE_NAME = "MY_DATABASE";

    //table name
    public static final String TABLE_TRANSACTIONS = "TRANSACTIONS";

    // TRANSACTIONS Table Columns names
    public static final String KEY_ID = "ID";
    public static final String TRADING_PAIR = "TRADING_PAIR";
    public static final String SELECT_EXCHANGE = "SELECT_EXCHANGE";
    public static final String TRANSFER_TYPE = "TRANSFER_TYPE";
    public static final String TRADE_PRICE = "TRADE_PRICE";
    public static final String BIT_PRICE = "BIT_PRICE";
    public static final String BIT_QUANTITY = "BIT_QUANTITY";
    public static final String TOTAL_VALUE = "TOTAL_VALUE";
    public static final String TRADE_DATE = "TRADE_DATE";
    public static final String TRADE_NOTES = "TRADE_NOTES";

    //table name
    public static final String TABLE_MY_BIT_COINS = "MY_BIT_COINS";
    // MY_BIT_COINS Table Columns names
    public static final String MY_BIT_COINS = "MY_BIT_COINS";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TRANSACTIONS_TABLE = "CREATE TABLE " + TABLE_TRANSACTIONS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + TRADING_PAIR + " TEXT,"
                + SELECT_EXCHANGE + " TEXT,"
                + TRANSFER_TYPE + " TEXT,"
                + TRADE_PRICE + " TEXT,"
                + BIT_PRICE + " TEXT,"
                + BIT_QUANTITY + " TEXT,"
                + TOTAL_VALUE + " TEXT,"
                + TRADE_DATE + " TEXT,"
                + TRADE_NOTES + " TEXT" + ")";

        String CREATE_MY_BIT_COINS_TABLE = "CREATE TABLE " + TABLE_MY_BIT_COINS + "("
                +   MY_BIT_COINS + " INTEGER" + ")";

        db.execSQL(CREATE_MY_BIT_COINS_TABLE);
        db.execSQL(CREATE_TRANSACTIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MY_BIT_COINS);
        // Create tables again
        onCreate(db);
    }
}
