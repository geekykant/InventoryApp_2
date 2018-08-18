package com.diyandroid.inventory.data;

import android.provider.BaseColumns;

public final class InventoryContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private InventoryContract() {
    }

    public static final class InventoryEntry implements BaseColumns {

        public final static String TABLE_NAME = "inventory";

        //Unique ID number for the product (only for use in the database table).
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_NAME = "Product_Name";
        public final static String COLUMN_PRODUCT_PRICE = "Price";
        public final static String COLUMN_PRODUCT_QUANTITY = "Quantity";
        public final static String COLUMN_PRODUCT_SUPPLIER_NAME = "Supplier_Name";
        public final static String COLUMN_PRODUCT_SUPPLIER_PHONE_NO = "Supplier_Phone_Number";
    }

}
