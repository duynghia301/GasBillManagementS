package com.example.gasbillmanagements.database;


import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.gasbillmanagements.model.Customer;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "gas_manage.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_CUSTOMER = "customer";
    public static final String ID = "ID";
    public static final String NAME = "NAME";
    public static final String YYYYMM = "YYYYMM";
    public static final String ADDRESS = "ADDRESS";
    public static final String USED_NUM_GAS = "USED_NUM_GAS";
    public static final String GAS_LEVEL_TYPE_ID = "GAS_LEVEL_TYPE_ID";

    private static final String TABLE_GAS_LEVEL_TYPE = "gas_level_type";
    private static final String _ID = "ID";
    private static final String GAS_LEVEL_TYPE_NAME = "GAS_LEVEL_TYPE_NAME";
    private static final String UNIT_PRICE = "UNIT_PRICE";
    private static final String MAX_NUM_GAS = "MAX_NUM_GAS";
    private static final String RATE_PRICE_FOR_OVER = "RATE_PRICE_FOR_OVER";

    private Context context;
    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CUSTOMER + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME + " TEXT, " + YYYYMM + " TEXT, " + ADDRESS + " TEXT, " + USED_NUM_GAS + " INTEGER, " + GAS_LEVEL_TYPE_ID + " INTEGER)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_GAS_LEVEL_TYPE + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + GAS_LEVEL_TYPE_NAME + " TEXT, " + UNIT_PRICE + " INTEGER, " + RATE_PRICE_FOR_OVER + " INTEGER, " + MAX_NUM_GAS + " INTEGER  )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAS_LEVEL_TYPE);
        onCreate(db);
    }

    public boolean insertCustomer(String name, String yyyyMm, String address, int usedNumGas, int gasLevelTypeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
        ContentValues values = new ContentValues();
        values.put(NAME, name);
        values.put(YYYYMM, yyyyMm);
        values.put(ADDRESS, address);
        values.put(USED_NUM_GAS, usedNumGas);
        values.put(GAS_LEVEL_TYPE_ID, gasLevelTypeId);
        long result =db.insert(TABLE_CUSTOMER, null, values);
        return result !=-1;

        } catch (Exception e) {
            Toast.makeText(context, "Lỗi khi thêm loại gas: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("DatabaseHelper", "Error inserting gas level type: ", e);
            return false;
        } finally {
            db.close(); // Đóng cơ sở dữ liệu ở đây là phù hợp
        }

    }
    public void insertGasLevelType(String gasLevelTypeName, int unitPrice, int maxNumGas, float ratePriceForOver) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GAS_LEVEL_TYPE_NAME, gasLevelTypeName);
        values.put(UNIT_PRICE, unitPrice);
        values.put(MAX_NUM_GAS, maxNumGas);
        values.put(RATE_PRICE_FOR_OVER, ratePriceForOver);
        db.insert(TABLE_GAS_LEVEL_TYPE, null, values);
        db.close();
    }


    public Cursor searchCustomers(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = "NAME LIKE ? OR ADDRESS LIKE ?";
        String[] selectionArgs = new String[]{"%" + query + "%", "%" + query + "%"};
        // Sử dụng DISTINCT để loại bỏ bản ghi trùng lặp
        String sql = "SELECT DISTINCT ID AS _id, NAME, ADDRESS FROM " + TABLE_CUSTOMER + " WHERE " + selection;
        return db.rawQuery(sql, selectionArgs);
    }


    public Cursor getAllCustomers() {
        SQLiteDatabase db = this.getReadableDatabase();
        // Đổi tên cột ID thành _id để tương thích với SimpleCursorAdapter
        return db.rawQuery("SELECT ID AS _id, NAME, ADDRESS FROM " + TABLE_CUSTOMER, null);
    }
    public Cursor getAllCustomersA() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CUSTOMER, null);
    }
    public Cursor searchCustomersA(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CUSTOMER + " WHERE NAME LIKE ?", new String[]{"%" + query + "%"}); // Kiểm tra tên bảng
    }

    public Cursor getCustomerById(int _id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = "ID = ?";
        String[] selectionArgs = {String.valueOf(_id)}; // Đã sửa
        String sql = "SELECT DISTINCT ID AS _id, NAME, ADDRESS FROM " + TABLE_CUSTOMER + " WHERE " + selection;
        return db.rawQuery(sql, selectionArgs);
    }

    public Cursor searchCustomersByNameAndAddress(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = "NAME LIKE ? OR ADDRESS LIKE ?";
        String[] selectionArgs = new String[]{"%" + query + "%", "%" + query + "%"};
        String sql = "SELECT DISTINCT ID AS _id, NAME, ADDRESS FROM " + TABLE_CUSTOMER + " WHERE " + selection;
        return db.rawQuery(sql, selectionArgs);
    }


    public ArrayList<Customer> fetchCustomers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CUSTOMER, null);

        ArrayList<Customer> arrayList = new ArrayList<>();
        while (cursor.moveToNext()){
            Customer customer = new Customer();
            Customer.ID = cursor.getInt(0);
            Customer.NAME = cursor.getString(1);
            Customer.YYYYMM = cursor.getString(2);
            Customer.ADDRESS = cursor.getString(3);
            Customer.USED_NUM_GAS = cursor.getInt(4);
            Customer.GAS_LEVEL_TYPE_ID = cursor.getInt(5);
            arrayList.add(customer); // Thêm đối tượng customer vào danh sách

        }
        cursor.close();
        return arrayList;
    }


    public Cursor searchCustomersByName(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = "NAME LIKE ?";
        String[] selectionArgs = new String[]{"%" + query + "%"};
        String sql = "SELECT DISTINCT ID AS _id, NAME, ADDRESS FROM " + TABLE_CUSTOMER + " WHERE " + selection;
        return db.rawQuery(sql, selectionArgs);
    }

    public Cursor searchCustomersByAddress(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = "ADDRESS LIKE ?";
        String[] selectionArgs = new String[]{"%" + query + "%"};
        String sql = "SELECT DISTINCT ID AS _id, NAME, ADDRESS FROM " + TABLE_CUSTOMER + " WHERE " + selection;
        return db.rawQuery(sql, selectionArgs);
    }

    public boolean deleteCustomer(int customerId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_CUSTOMER, ID + "=?", new String[]{String.valueOf(customerId)});
        return rowsAffected > 0; // Trả về true nếu xóa thành công
    }





    //Gas
    // Phương thức tăng giá gas


    // Phương thức lấy giá gas hiện tại
    public double getCurrentGasPrice(int gasLevelId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + UNIT_PRICE + " FROM " + TABLE_GAS_LEVEL_TYPE + " WHERE " + _ID + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(gasLevelId)});
        double price = 0;

        if (cursor != null && cursor.moveToFirst()) {
            price = cursor.getDouble(cursor.getColumnIndexOrThrow(UNIT_PRICE));
            cursor.close();
        }
        return price;
    }



    public Cursor getAllGasLevels() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT ID, GAS_LEVEL_TYPE_NAME FROM gas_level_type", null);
    }


    public boolean updateGasPrice(int gasLevelId, double increaseAmount) {
        SQLiteDatabase db = this.getWritableDatabase();
        double currentPrice = getCurrentGasPrice(gasLevelId);
        double newPrice = currentPrice + increaseAmount;

        ContentValues contentValues = new ContentValues();
        contentValues.put(UNIT_PRICE, newPrice);
        int rowsAffected = db.update(TABLE_GAS_LEVEL_TYPE, contentValues, _ID + "=?", new String[]{String.valueOf(gasLevelId)});
        return rowsAffected > 0;
    }



}







