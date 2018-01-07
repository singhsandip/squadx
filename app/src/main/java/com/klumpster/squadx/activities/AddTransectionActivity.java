package com.klumpster.squadx.activities;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.klumpster.squadx.Constants;
import com.klumpster.squadx.R;
import com.klumpster.squadx.database.DatabaseHelper;
import com.klumpster.squadx.interfaces.ApiService;
import com.klumpster.squadx.model.GetRetrofit;
import com.klumpster.squadx.model.PriceResponse;
import com.klumpster.squadx.singleton.DatabaseHelperSingleton;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTransectionActivity extends AppCompatActivity implements View.OnClickListener,TextWatcher,RadioGroup.OnCheckedChangeListener{

    private ProgressBar progressBar;
    private ImageView imgVCancel,imgVSelectExchange,imgVTradingDate;
    private TextView tvSave,tvTradingPair,tvSelectExchange,tvCurrentPrice,tvTotalValue,tvTradeDate;
    private LinearLayout llSelectExchange,llTradeDate;
    private RadioGroup radioGroup;
    private RadioButton rdbBuy,rdbSell,rdbWatchOnly;
    private EditText etTradePrice,etQuantity,etAddNotes;
    private InputMethodManager inputMethodManger;
    private String TAG = AddTransectionActivity.class.getSimpleName();
    private Calendar calendar;
    private int currentYear,month,day;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase sqliteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transections_activtity);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        inputMethodManger = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        currentYear = calendar.get(Calendar.YEAR);

        databaseHelper = DatabaseHelperSingleton.getDatabaseHelperInstance(getApplicationContext());

        viewInIt();

        getBitCoinPrice();
    }

    private void getBitCoinPrice() {
        progressBar.setVisibility(View.VISIBLE);

        ApiService apiService = GetRetrofit.getRetrofitInstance().create(ApiService.class);
        Call<List<PriceResponse>> responseCall = apiService.getBitCoinPrice();

        responseCall.enqueue(new Callback<List<PriceResponse>>() {
            @Override
            public void onResponse(Call<List<PriceResponse>> call, Response<List<PriceResponse>> response) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                if (response.code() == 200){
                    progressBar.setVisibility(View.GONE);
                    tvCurrentPrice.setText(response.body().get(0).getPriceUsd());
                }else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AddTransectionActivity.this, "Failed to get price ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PriceResponse>> call, Throwable t) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
               progressBar.setVisibility(View.GONE);
                Toast.makeText(AddTransectionActivity.this, "Failed to get price", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void viewInIt() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        imgVCancel = (ImageView) findViewById(R.id.add_transaction_cancel_img_v);
        imgVSelectExchange = (ImageView) findViewById(R.id.add_transaction_select_exchange_img_v);
        imgVTradingDate = (ImageView) findViewById(R.id.add_transaction_trade_date_img_v);

        tvSave = (TextView) findViewById(R.id.add_transaction_save_tv);
        tvTradingPair = (TextView) findViewById(R.id.add_transaction_trading_pair_tv);
        tvSelectExchange = (TextView) findViewById(R.id.add_transaction_select_exchange_tv);
        tvCurrentPrice = (TextView) findViewById(R.id.add_transaction_current_price_tv);
        tvTotalValue = (TextView) findViewById(R.id.add_transaction_total_value_tv);
        tvTradeDate = (TextView) findViewById(R.id.add_transaction_trade_date_tv);

        tvTradeDate.setText(calendar.getDisplayName(Calendar.MONTH,Calendar.LONG, Locale.getDefault())+" "+day+","+currentYear);

        llSelectExchange = (LinearLayout) findViewById(R.id.add_transaction_select_exchange_ll);
        llTradeDate = (LinearLayout) findViewById(R.id.add_transaction_trade_date_ll);
        
        etTradePrice = (EditText) findViewById(R.id.add_transaction_trade_price_et);
        etQuantity = (EditText) findViewById(R.id.add_transaction_quantity_et);
        etAddNotes = (EditText) findViewById(R.id.add_transaction_add_notes_et);

        radioGroup = (RadioGroup) findViewById(R.id.add_transaction_rdgrp);

        radioGroup.setOnCheckedChangeListener(this);
        imgVCancel.setOnClickListener(this);
        tvSave.setOnClickListener(this);
        etQuantity.addTextChangedListener(this);
        llSelectExchange.setOnClickListener(this);
        llTradeDate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_transaction_cancel_img_v:
                onBackPressed();
                break;

            case R.id.add_transaction_save_tv:
                if (etTradePrice.getText().toString().isEmpty()){
                      addError("Enter trade price",etTradePrice);
                  }else if (etQuantity.getText().toString().isEmpty()  || Integer.parseInt(etQuantity.getText().toString()) == 0){
                      addError("Enter quantity",etQuantity);
                  }else if (radioGroup.getCheckedRadioButtonId() == -1){
                    Toast.makeText(this, "Select Transaction Type", Toast.LENGTH_SHORT).show();
                }else if (tvCurrentPrice.getText().toString().isEmpty()){
                    Toast.makeText(this, "BTC current price is not available please restart app", Toast.LENGTH_SHORT).show();
                }
                else {
                    addBitCoins(getTransactionType());
                }
                break;

            case R.id.add_transaction_select_exchange_ll:
                onBackPressed();
                break;

            case R.id.add_transaction_trade_date_ll:
                showDatePicker();
                break;
        }
    }

    private void addBitCoins(String trnasactionType) {
        sqliteDatabase = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        int myBitCoins = getMyBitCoins();

        Log.d(TAG, "addBitCoins: "+myBitCoins);

        if (trnasactionType.equalsIgnoreCase(Constants.SELL) && myBitCoins < Integer.parseInt(etQuantity.getText().toString())){
                Toast.makeText(this, "You don't have this much amount to sell", Toast.LENGTH_SHORT).show();
        }else {
            if (trnasactionType.equalsIgnoreCase(Constants.SELL)){
                contentValues.put(DatabaseHelper.MY_BIT_COINS,myBitCoins - Integer.parseInt(etQuantity.getText().toString()));
            }else {
                contentValues.put(DatabaseHelper.MY_BIT_COINS,myBitCoins + Integer.parseInt(etQuantity.getText().toString()));
            }

            // Inserting Row
            long check = sqliteDatabase.insert(DatabaseHelper.TABLE_MY_BIT_COINS, null, contentValues);

            if (check != -1){
                addTransaction(trnasactionType, myBitCoins);
            }else {
                Toast.makeText(this, "Failed to add transaction", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private int getMyBitCoins() {
        int myBitCoins = 0;
        sqliteDatabase = databaseHelper.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + DatabaseHelper.TABLE_MY_BIT_COINS;

        Cursor cursor = sqliteDatabase.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                myBitCoins = cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return myBitCoins;
    }

    private void showDatePicker() {
        DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {;
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                calendar.set(Calendar.MONTH,monthOfYear);
                calendar.set(Calendar.YEAR,year);

                calendar.setTimeInMillis(calendar.getTimeInMillis());

                tvTradeDate.setText(calendar.getDisplayName(Calendar.MONTH,Calendar.LONG, Locale.getDefault())+" "+dayOfMonth+","+year);
            }
        }, currentYear, month, day);
        datePicker.show();
    }

    private void addError(String error, EditText editText){
        editText.requestFocus();
        editText.setError(error);
        inputMethodManger.showSoftInput(editText,InputMethodManager.SHOW_IMPLICIT);
    }

    private void addTransaction(String tranSactionType,int bitCoinValue) {
        sqliteDatabase = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.TRADING_PAIR, tvTradingPair.getText().toString());
        values.put(DatabaseHelper.SELECT_EXCHANGE, tvSelectExchange.getText().toString());
        values.put(DatabaseHelper.TRANSFER_TYPE, tranSactionType);
        values.put(DatabaseHelper.TRADE_PRICE, etTradePrice.getText().toString());
        values.put(DatabaseHelper.BIT_PRICE, tvCurrentPrice.getText().toString());
        values.put(DatabaseHelper.BIT_QUANTITY, etQuantity.getText().toString());
        values.put(DatabaseHelper.TOTAL_VALUE, tvTotalValue.getText().toString());
        values.put(DatabaseHelper.TRADE_DATE, tvTradeDate.getText().toString());
        values.put(DatabaseHelper.TRADE_NOTES, etAddNotes.getText().toString());

        // Inserting Row
        long check = sqliteDatabase.insert(DatabaseHelper.TABLE_TRANSACTIONS, null, values);
        if (check != -1){
            Toast.makeText(this, "Transaction Added", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }else {
            //remove added or subtracted transaction
            values.put(DatabaseHelper.MY_BIT_COINS,bitCoinValue);
            sqliteDatabase.insert(DatabaseHelper.MY_BIT_COINS,null,values);
            Toast.makeText(this, "failed to add transaction", Toast.LENGTH_SHORT).show();
        }
        sqliteDatabase.close();
    }

    private String getTransactionType() {
        String transactionType = null;
        switch (radioGroup.getCheckedRadioButtonId()){
            case R.id.add_transaction_buy_rdb:
                transactionType = Constants.BUY;
                break;

            case R.id.add_transaction_sell_rdb:
                transactionType = Constants.SELL;
                break;

            case R.id.add_transaction_watch_only_rdb:
                transactionType  = Constants.WATCH_ONLY;
                break;
        }
        return transactionType;
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (! etQuantity.getText().toString().isEmpty() && ! tvCurrentPrice.getText().toString().isEmpty()){
            tvTotalValue.setText(String.valueOf(Float.parseFloat(tvCurrentPrice.getText().toString()) * Integer.parseInt(etQuantity.getText().toString())));
        }else {
            tvTotalValue.setText("");
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.add_transaction_watch_only_rdb){

            etTradePrice.setFocusable(false);
            etTradePrice.setFocusableInTouchMode(false);

            etQuantity.setFocusable(false);
            etQuantity.setFocusableInTouchMode(false);

            tvSave.setClickable(false);
            llTradeDate.setClickable(false);

            inputMethodManger.hideSoftInputFromInputMethod(group.getWindowToken(),0);
        }else{
            etTradePrice.setFocusable(true);
            etTradePrice.setFocusableInTouchMode(true);

            etQuantity.setFocusable(true);
            etQuantity.setFocusableInTouchMode(true);

            tvSave.setClickable(true);
            llTradeDate.setClickable(true);
        }
    }
}
