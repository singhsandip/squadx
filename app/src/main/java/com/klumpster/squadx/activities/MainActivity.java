package com.klumpster.squadx.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.klumpster.squadx.Constants;
import com.klumpster.squadx.R;
import com.klumpster.squadx.adapter.TransactionAdapter;
import com.klumpster.squadx.database.DatabaseHelper;
import com.klumpster.squadx.helper.CheckConnectionHelper;
import com.klumpster.squadx.interfaces.ApiService;
import com.klumpster.squadx.model.GetRetrofit;
import com.klumpster.squadx.model.PriceResponse;
import com.klumpster.squadx.model.Transaction;
import com.klumpster.squadx.singleton.DatabaseHelperSingleton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView tvNoTransaction;
    private ImageView imgVGrowth,imgVAddTransaction,imgVDoller;
    private TextView tvPortfolioValue,tvChangeInHour;
    private RecyclerView rvTransactions;
    private List<Transaction> transactionList;
    private DatabaseHelper databaseHelper;
    private TransactionAdapter transactionAdapter;
    private LinearLayout llTransactionTittles;
    private LinearLayoutManager linearLayoutManager;
    private SQLiteDatabase sqliteDatabase;
    private int myBitCoins;
    private float priceUsd;
    private boolean isPortfolioValueVisible = true;
    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        transactionAdapter = new TransactionAdapter(this);

        databaseHelper = DatabaseHelperSingleton.getDatabaseHelperInstance(getApplicationContext());


        myBitCoins = getMyBitCoins();

        viewsInIt();

//        getBitCoinPrice();
    }

    private void getBitCoinPrice() {
        ApiService apiService = GetRetrofit.getRetrofitInstance().create(ApiService.class);
        Call<List<PriceResponse>> responseCall = apiService.getBitCoinPrice();

        responseCall.enqueue(new Callback<List<PriceResponse>>() {
            @Override
            public void onResponse(Call<List<PriceResponse>> call, Response<List<PriceResponse>> response) {
                if (response.code() == 200){
                    priceUsd = Float.parseFloat(response.body().get(0).getPriceUsd());

                    int myBitCoins  = getMyBitCoins();

                    if (myBitCoins >= 0){
                       if (isPortfolioValueVisible){
                           tvPortfolioValue.setText(String.valueOf(myBitCoins * priceUsd));
                       }
                        calculateProfitOrLoos();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<PriceResponse>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed to get price check internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateProfitOrLoos() {
        Log.d(TAG, "calculateProfitOrLoos: ");
        float boughtBits = 0,sellingBits = 0,boughtBitMean,sellBitMean,boughtBitsPrice = 0,seilingBitsPrice = 0;

        for (int i=0; i<transactionList.size(); i++){
            Transaction transaction = transactionList.get(i);
            if (transaction.getType().equalsIgnoreCase(Constants.BUY)){
                boughtBits = Float.parseFloat(boughtBits + transaction.getQuantity());
                boughtBitsPrice = boughtBitsPrice + Float.parseFloat(transaction.getBitPrice()) * Integer.parseInt(transaction.getQuantity());
            }else {
                sellingBits = Float.parseFloat(sellingBits + transaction.getQuantity());
                seilingBitsPrice = seilingBitsPrice + Float.parseFloat(transaction.getBitPrice()) * Integer.parseInt(transaction.getQuantity());
            }
        }

        boughtBitMean = boughtBitsPrice / boughtBits;
        sellBitMean = seilingBitsPrice / sellingBits;


        if (seilingBitsPrice == 0){
            tvChangeInHour.setText("0.0%");
        }else {
            if (sellBitMean < boughtBitMean){
                tvChangeInHour.setText("-"+String.valueOf(((boughtBitMean - sellBitMean) / sellBitMean) * 100).substring(0,4)+"%");
            }else if (sellBitMean > boughtBitMean){
                tvChangeInHour.setText("+"+String.valueOf(((sellBitMean - boughtBitMean) / sellBitMean) * 100).substring(0,4)+"%");
            }else {
                tvChangeInHour.setText("0.0%");
            }
        }

        //update profit after each two seconds
        getBitCoinPrice();
    }

    private void viewsInIt() {
        tvNoTransaction = (TextView) findViewById(R.id.main_no_transaction_tv);
        llTransactionTittles = (LinearLayout) findViewById(R.id.main_transaction_tittle_ll);

        imgVDoller = (ImageView) findViewById(R.id.main_doller_img_v);
        imgVGrowth = (ImageView) findViewById(R.id.main_growth_img_v);
        imgVAddTransaction = (ImageView) findViewById(R.id.main_add_img_v);

        tvPortfolioValue = (TextView) findViewById(R.id.main_amount_tv);
        tvChangeInHour = (TextView) findViewById(R.id.main_percent_growth_tv);

        rvTransactions = (RecyclerView) findViewById(R.id.main_transections_rv);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);

        rvTransactions.setLayoutManager(linearLayoutManager);
        rvTransactions.setAdapter(transactionAdapter);


        transactionList = getTransactionList();

        if (transactionList.isEmpty()){
            llTransactionTittles.setVisibility(View.GONE);
            tvNoTransaction.setVisibility(View.VISIBLE);
        }else {
            tvNoTransaction.setVisibility(View.GONE);
            llTransactionTittles.setVisibility(View.VISIBLE);
            transactionAdapter.addList(transactionList);
        }

        tvPortfolioValue.setOnClickListener(this);
        imgVGrowth.setOnClickListener(this);
        imgVAddTransaction.setOnClickListener(this);
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
        sqliteDatabase.close();

        return myBitCoins;
    }

    private List<Transaction> getTransactionList() {
        transactionList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + DatabaseHelper.TABLE_TRANSACTIONS;

        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Transaction transaction = new Transaction();
                transaction.setTradingPair(cursor.getString(1));
                transaction.setExchange(cursor.getString(2));
                transaction.setType(cursor.getString(3));
                transaction.setTradePrice(cursor.getString(4));
                transaction.setBitPrice(cursor.getString(5));
                transaction.setQuantity(cursor.getString(6));
                transaction.setTotalValue(cursor.getString(7));
                transaction.setTradeDate(cursor.getString(8));
                transaction.setNotes(cursor.getString(9));

                transactionList.add(transaction);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return transactionList;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBitCoinPrice();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_amount_tv:
                if (isPortfolioValueVisible){
                    imgVDoller.setVisibility(View.GONE);
                    isPortfolioValueVisible = false;
                    tvPortfolioValue.setText("BTC/USD "+priceUsd);
                   }else {
                        imgVDoller.setVisibility(View.VISIBLE);
                        isPortfolioValueVisible = true;
                        tvPortfolioValue.setText(String.valueOf(myBitCoins * priceUsd));
                }
                break;

            case R.id.main_growth_img_v:
                if (CheckConnectionHelper.isConnectedToInternet(this)){
                    startActivity(new Intent(this,GraphActivity.class));
                }else {
                    Toast.makeText(this, "Please connect to internet", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.main_add_img_v:
                startActivity(new Intent(this,AddTransectionActivity.class));
                break;
        }
    }
}
