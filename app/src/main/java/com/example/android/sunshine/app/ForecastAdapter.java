package com.example.android.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;
    private static final int VIEW_TYPE_COUNT = 2;

    // Flag to determine if we want to use a separate view for "today".
    private boolean useTodayLayoutFlag = true;

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /*
        Remember that these views are reused as needed.
     */

    // Method returns a new list item layout, but has no data in it
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        // Choose the layout type
        // Determine layoutId from viewType
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;

        if (viewType == VIEW_TYPE_TODAY) {
            layoutId = R.layout.list_item_forecast_today;
        } else if (viewType == VIEW_TYPE_FUTURE_DAY) {
            layoutId = R.layout.list_item_forecast;
        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */

    // Method takes an existing list item layout and update it with the data from Cursor
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Choose the layout type
        // Determine layoutId from viewType
        int viewType = getItemViewType(cursor.getPosition());

        if (viewType == VIEW_TYPE_TODAY) {
            // Use weather art image
            viewHolder.iconView.setImageResource(
                    Utility.getArtResourceForWeatherCondition(
                            cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID
                            )
                    )
            );
        } else if (viewType == VIEW_TYPE_FUTURE_DAY) {
            // Use weather icon image
            viewHolder.iconView.setImageResource(
                    Utility.getIconResourceForWeatherCondition(
                            cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID)
                    )
            );
        }

        // Read date from cursor
        long dateInMilliseconds = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        // Find TextView and set formatted date on it
        viewHolder.dateView.setText(Utility.getFriendlyDayString(context, dateInMilliseconds));

        // Read weather forecast from cursor
        String forecastDescription = cursor.getString(ForecastFragment.COL_WEATHER_DESC);

        // Find TextView and set weather forecast on it in line with current locale
        String language = Locale.getDefault().getLanguage();
        if (language.equals("uk") || language.equals("ru")) {
            viewHolder.descriptionView.setText(
                    Utility.getLocaleForecastDescription(
                            context,
                            cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID)
                    ));
        } else {
            viewHolder.descriptionView.setText(forecastDescription);

        }

        // Accessibility: add a content description to the icon field
        viewHolder.iconView.setContentDescription(forecastDescription);

        // Read high temperature from cursor
        double high = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        // Find TextView and set high temperature on it
        viewHolder.highTempView.setText(Utility.formatTemperature(context, high));

        // Read low temperature from cursor
        double low = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        // Find TextView and set low temperature on it
        viewHolder.lowTempView.setText(Utility.formatTemperature(context, low));

    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        useTodayLayoutFlag = useTodayLayout;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && useTodayLayoutFlag) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }
}