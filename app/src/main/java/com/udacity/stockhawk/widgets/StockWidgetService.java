package com.udacity.stockhawk.widgets;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import static com.udacity.stockhawk.R.id.change;
import static com.udacity.stockhawk.data.Contract.Quote.uri;

public class StockWidgetService extends RemoteViewsService {
    private DecimalFormat dollarFormatWithPlus;
    private DecimalFormat percentageFormat;
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;
            @Override
            public void onCreate() {
            }
            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(
                        uri,
                        new String[]{
                                Contract.Quote._ID,
                                Contract.Quote.COLUMN_SYMBOL,
                                Contract.Quote.COLUMN_PRICE,
                                Contract.Quote.COLUMN_ABSOLUTE_CHANGE,
                                Contract.Quote.COLUMN_PERCENTAGE_CHANGE,
                                Contract.Quote.COLUMN_HISTORY
                        },
                        null,
                        null,
                        null);
                Binder.restoreCallingIdentity(identityToken);
                Log.d("datas", data.toString());
                Log.d("datasss", Contract.Quote.COLUMN_SYMBOL);
            }
            @Override
            public void onDestroy() {
            }
            @Override
            public int getCount() {
                Log.d("count", String.valueOf(data.getCount()));
                return data == null ? 0 : data.getCount();
            }
            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_collection_item);
                dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
                dollarFormatWithPlus.setPositivePrefix("+$");
                percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
                percentageFormat.setMaximumFractionDigits(2);
                percentageFormat.setMinimumFractionDigits(2);
                percentageFormat.setPositivePrefix("+");
                float rawAbsoluteChange = data.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
                float percentageChange = data.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);
                // Bind data to the views
                String symbol = data.getString(Contract.Quote.POSITION_SYMBOL);
                views.setTextViewText(R.id.stock_symbol, data.getString(Contract.Quote.POSITION_SYMBOL));
                views.setTextViewText(R.id.price_view, data.getString(Contract.Quote.POSITION_PRICE));
                if (rawAbsoluteChange > 0) {
                    Log.d(symbol, String.valueOf(rawAbsoluteChange));
                    views.setInt(change, getResources().getString(R.string.set_background), R.drawable.percent_change_pill_green);
                } else {
                    Log.d(symbol, String.valueOf(percentageChange));
                    views.setInt(change, getResources().getString(R.string.set_background), R.drawable.percent_change_pill_red);
                }
                String percentage = percentageFormat.format(percentageChange / 100);
                views.setTextViewText(change, String.valueOf(percentage));
                final Intent fillInIntent = new Intent();
                fillInIntent.putExtra(getResources().getString(R.string.symbol), data.getString(data.getColumnIndex(Contract.Quote.COLUMN_SYMBOL)));
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                return views;
            }
            @Override
            public RemoteViews getLoadingView() {
                return null; // use the default loading view
            }
            @Override
            public int getViewTypeCount() {
                return 1;
            }
            @Override
            public long getItemId(int position) {
                // Get the row ID for the view at the specified position
                if (data != null && data.moveToPosition(position)) {
                    final int QUOTES_ID_COL = 0;
                    return data.getLong(QUOTES_ID_COL);
                }
                return position;
            }
            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}