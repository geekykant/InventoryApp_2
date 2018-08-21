package com.diyandroid.inventory;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.diyandroid.inventory.data.InventoryContract.InventoryEntry;

public class ProductDetails extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    private Uri mCurrentProductUri;
    private static final int EXISTING_PRODUCT_LOADER = 0;
    private int quantity;
    private String supplierContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        ((Button) findViewById(R.id.increaseQuantity)).setOnClickListener(this);
        ((Button) findViewById(R.id.decreaseQuantity)).setOnClickListener(this);
        ((Button) findViewById(R.id.purchase)).setOnClickListener(this);
        ((Button) findViewById(R.id.deleteProduct)).setOnClickListener(this);

        mCurrentProductUri = getIntent().getData();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabEdit);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDetails.this, AddEditProduct.class);
                intent.setData(mCurrentProductUri);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NO};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentProductUri,         // Query the content URI for the current product
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of product attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NO);

            quantity = Integer.parseInt(cursor.getString(quantityColumnIndex));
            supplierContact = cursor.getString(supplierPhoneColumnIndex);

            // Update the views on the screen with the values from the database
            ((TextView) findViewById(R.id.seeProductName)).setText(getString(R.string.product_name) + cursor.getString(nameColumnIndex));
            ((TextView) findViewById(R.id.seeProductPrice)).setText(getString(R.string.product_price) + cursor.getString(priceColumnIndex));
            ((TextView) findViewById(R.id.seeProductQuantity)).setText(getString(R.string.product_quantity) + cursor.getString(quantityColumnIndex));
            ((TextView) findViewById(R.id.seeProductSupplierName)).setText(getString(R.string.supplier_name) + cursor.getString(supplierNameColumnIndex));
            ((TextView) findViewById(R.id.seeProductPhoneNo)).setText(getString(R.string.supplier_phone_no) + cursor.getString(supplierPhoneColumnIndex));

            if(TextUtils.isEmpty(supplierContact)){
                ((Button)findViewById(R.id.purchase)).setVisibility(View.GONE);
                ((TextView)findViewById(R.id.purchaseText)).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onClick(View view) {

        ContentValues values = new ContentValues();
        int newQuantity;

        switch (view.getId()) {
            case R.id.decreaseQuantity:
                newQuantity = quantity - 1;

                if (newQuantity < 0) {
                    Toast.makeText(view.getContext(), "Empty stock", Toast.LENGTH_SHORT).show();
                } else {
                    values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);

                    int rowsAffected = view.getContext().getContentResolver().update(mCurrentProductUri, values, null, null);

                    // Show a toast message depending on whether or not the update was successful.
                    if (rowsAffected == 0) {
                        // If no rows were affected, then there was an error with the update.
                        Toast.makeText(view.getContext(), "updating failed!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.increaseQuantity:
                newQuantity = quantity + 1;
                values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);

                int rowsAffected = view.getContext().getContentResolver().update(mCurrentProductUri, values, null, null);

                // Show a toast message depending on whether or not the update was successful.
                if (rowsAffected == 0) {
                    // If no rows were affected, then there was an error with the update.
                    Toast.makeText(view.getContext(), "updating failed!", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.purchase:
                if (!TextUtils.isEmpty(supplierContact)) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + supplierContact));
                    startActivity(intent);
                }
                break;
            case R.id.deleteProduct:
                int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

                // Show a toast message depending on whether or not the delete was successful.
                if (rowsDeleted == 0) {
                    // If no rows were deleted, then there was an error with the delete.
                    Toast.makeText(this, "Deletion failed!", Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the delete was successful and we can display a toast.
                    Toast.makeText(this, "Deletion Successful!", Toast.LENGTH_SHORT).show();
                }
                finish();
                break;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
