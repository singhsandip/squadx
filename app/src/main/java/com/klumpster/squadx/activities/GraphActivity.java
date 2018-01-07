package com.klumpster.squadx.activities;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.klumpster.squadx.extensions.MyMarkerView;
import com.klumpster.squadx.R;
import com.klumpster.squadx.interfaces.ApiService;
import com.klumpster.squadx.model.ChartResponse;
import com.klumpster.squadx.model.GetRetrofit;
import com.klumpster.squadx.model.Value;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GraphActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView imgVBack;
    private ProgressBar progressBar;
    private LineChart lineChart;
    private String TAG  = GraphActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        viewInIt();

        setUpChart();
    }

    private void viewInIt() {
        imgVBack = (ImageView) findViewById(R.id.graph__back_img_v);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        lineChart = (LineChart) findViewById(R.id.graph_line_chart);

        imgVBack.setOnClickListener(this);
    }

    private void setUpChart() {
        lineChart.setDrawGridBackground(false);

        // enable scaling and dragging
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);

        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
        mv.setChartView(lineChart); // For bounds control
        lineChart.setMarker(mv); // Set the marker to the chart

        XAxis xAxis = lineChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        lineChart.getAxisRight().setEnabled(false);

        // add data
        getDataForChart();
    }

    private void getDataForChart() {
        progressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.blockchain.info/")
                .addConverterFactory(GsonConverterFactory.create()).build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<ChartResponse> responseCall = apiService.getChartData();

        responseCall.enqueue(new Callback<ChartResponse>() {
            @Override
            public void onResponse(Call<ChartResponse> call, Response<ChartResponse> response) {
                progressBar.setVisibility(View.GONE);

                response.body();

                lineChart.setVisibility(View.VISIBLE);

                if (response.code() == 200){
                    ArrayList<Entry> values = new ArrayList<Entry>();

                    ChartResponse chartResponse = response.body();

                    List<Value> valueList = chartResponse.getValues();

                    for (int i=0; i<valueList.size(); i++){
//                        values.add(new Entry(40+i,80+i,ContextCompat.getDrawable(GraphActivity.this,R.drawable.star)));

                        values.add(new Entry(valueList.get(i).getX(),(float) valueList.get(i).getY()));
                    }

                    LineDataSet lineDataSet = new LineDataSet(values,chartResponse.getDescription());

                    lineDataSet.setDrawIcons(false);

                    // set the line to be drawn like this "- - - - - -"
                    lineDataSet.enableDashedLine(10f, 5f, 0f);
                    lineDataSet.enableDashedHighlightLine(10f, 5f, 0f);
                    lineDataSet.setColor(Color.BLACK);
                    lineDataSet.setCircleColor(Color.BLACK);
                    lineDataSet.setLineWidth(1f);
                    lineDataSet.setCircleRadius(3f);
                    lineDataSet.setDrawCircleHole(false);
                    lineDataSet.setValueTextSize(9f);
                    lineDataSet.setDrawFilled(true);
                    lineDataSet.setFormLineWidth(1f);
                    lineDataSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
                    lineDataSet.setFormSize(15.f);

                    lineDataSet.setFillColor(Color.BLACK);

                    ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                    dataSets.add(lineDataSet); // add the datasets

                    // create a data object with the datasets
                    LineData data = new LineData(dataSets);

                    // set data
                    lineChart.setData(data);

                    lineChart.animateX(2500);

                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(GraphActivity.this, "Failed to get data ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChartResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(GraphActivity.this, "Something went wrong please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.graph__back_img_v:
                onBackPressed();
                break;
        }
    }
}
