package com.udacity.stockhawk.graph;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.udacity.stockhawk.R;

import butterknife.BindView;
import butterknife.ButterKnife;
/**
 * Created by Ajay R Warrier on 16-12-2016.
 */
public class GraphActivity extends AppCompatActivity {
    String symbol;
    String url = "http://chart.finance.yahoo.com/z?s=";
    String urlOptions = "&t=6m&q=l&l=on&z=l&p=m10,m200";
    String mainUrl;
    @BindView(R.id.graphView)
    ImageView graphView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        ButterKnife.bind(this);
        symbol = getIntent().getStringExtra("symbol");
        mainUrl = url + symbol + urlOptions;
        Picasso.with(this)
                .load(mainUrl)
                .into(graphView);
    }
}
