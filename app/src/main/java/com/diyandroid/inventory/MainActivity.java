package com.diyandroid.inventory;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.diyandroid.inventory.data.InventoryContract.InventoryEntry;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Database helper that will provide us access to the database
     */
    ProductCursorAdapter mCursorAdapter;

    //Identifier for the pet data loader
    private static final int PET_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEditProduct.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the pet data
        ListView petListView = (ListView) findViewById(R.id.listView);

        mCursorAdapter = new ProductCursorAdapter(this, null);
        petListView.setAdapter(mCursorAdapter);

        // Setup the item click listener
        petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, ProductDetails.class);
                Uri currentPetUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);

                intent.setData(currentPetUri);
                startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(PET_LOADER, null, this);
    }

    private void insertDummyProduct() {
        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME, "Amazon");
        values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NO, "9082187");

        for (int i = 1; i < 4; i++) {
            values.put(InventoryEntry.COLUMN_PRODUCT_NAME, "Apple " + i);
            values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, Math.floor(Math.random() * 100) + 1);
            values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, Math.floor(Math.random() * 100) + 10000);
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
            Toast.makeText(this, "Uri: " + newUri, Toast.LENGTH_SHORT).show();
        }

    }

    private void deleteAllProducts() {
        int rowsDeleted = getContentResolver().delete(InventoryEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from product database");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dot_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_dummy_entry:
                insertDummyProduct();
                return true;

            case R.id.delete_all:
                deleteAllProducts();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                InventoryEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link PetCursorAdapter} with this new cursor containing updated pet data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
        mCursorAdapter.notifyDataSetChanged();
    }
}
