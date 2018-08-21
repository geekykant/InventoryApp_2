package com.diyandroid.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.diyandroid.inventory.data.InventoryContract.InventoryEntry;

public class ProductCursorAdapter extends CursorAdapter {
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return view;
    }

    @Override
    public void bindView(final View view, Context context, final Cursor cursor) {

        final TextView productName = (TextView) view.findViewById(R.id.productName);
        productName.setTag(cursor.getPosition());

        TextView productPrice = (TextView) view.findViewById(R.id.productPrice);
        final TextView productQuantity = (TextView) view.findViewById(R.id.productQuantity);

        Button minus = (Button) view.findViewById(R.id.productMinus);
        Button add = (Button) view.findViewById(R.id.productAdd);

        final int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
        final int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
        final int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);

        if (TextUtils.isEmpty(cursor.getString(nameColumnIndex))) {
            productName.setText(R.string.unknown);
        } else {
            productName.setText(cursor.getString(nameColumnIndex));
        }

        productPrice.setText(cursor.getString(priceColumnIndex));
        productQuantity.setText(cursor.getString(quantityColumnIndex));

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (int) productName.getTag() + 1;
                Uri mCurrentProductUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, position);

                ContentValues values = new ContentValues();
                int newQuantity = Integer.parseInt(cursor.getString(quantityColumnIndex)) - 1;

                if (newQuantity < 0) {
                    Toast.makeText(view.getContext(), "Empty stock", Toast.LENGTH_SHORT).show();
                } else {
                    values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);

                    int rowsAffected = view.getContext().getContentResolver().update(mCurrentProductUri, values, null, null);

                    // Show a toast message depending on whether or not the update was successful.
                    if (rowsAffected == 0) {
                        // If no rows were affected, then there was an error with the update.
                        Toast.makeText(view.getContext(), "updating failed!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Otherwise, the update was successful and we can display a toast.
                        Toast.makeText(view.getContext(), "Successfully updated!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (int) productName.getTag() + 1;

                Uri mCurrentProductUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, position);

                ContentValues values = new ContentValues();
                int newQuantity = Integer.parseInt(cursor.getString(quantityColumnIndex)) + 1;

                values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);

                int rowsAffected = view.getContext().getContentResolver().update(mCurrentProductUri, values, null, null);

                // Show a toast message depending on whether or not the update was successful.
                if (rowsAffected == 0) {
                    // If no rows were affected, then there was an error with the update.
                    Toast.makeText(view.getContext(), "updating failed!", Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the update was successful and we can display a toast.
                    Toast.makeText(view.getContext(), "Successfully updated!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
