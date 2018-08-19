package com.diyandroid.inventory;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.diyandroid.inventory.data.InventoryContract.InventoryEntry;

public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView productName = (TextView) view.findViewById(R.id.productName);
        TextView productPrice = (TextView) view.findViewById(R.id.productPrice);
        TextView productQuantity = (TextView) view.findViewById(R.id.productQuantity);

        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);

        if (TextUtils.isEmpty(cursor.getString(nameColumnIndex))) {
            productName.setText(R.string.unknown);
        } else {
            productName.setText(cursor.getString(nameColumnIndex));
        }

        productPrice.setText(cursor.getString(priceColumnIndex));
        productQuantity.setText(cursor.getString(quantityColumnIndex));

    }
}
