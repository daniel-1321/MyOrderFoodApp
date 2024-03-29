package com.example.msiseri.orderapp.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.msiseri.orderapp.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MSI SERI on 09-Dec-17.
 */

public class Database extends SQLiteAssetHelper {
    private static final String DB_Name = "EatItDB.db";
    private static final int DB_VER= 1;
    public Database(Context context) {
        super(context, DB_Name, null, DB_VER);
    }

    public boolean checkFoodExist(String foodId, String userPhone){
        boolean flag = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQL = String.format("SELECT * FROM OrderDetail WHERE UserPhone='%s' And ProductId='%s'",userPhone,foodId);
        cursor = db.rawQuery(SQL,null);
        if(cursor.getCount()>0){
            flag =  true;
        }else{
            flag =  false;
        }
        cursor.close();
        return flag;
    }

    public List<Order> getCarts(String userPhone){
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"UserPhone","ProductName","ProductId","Quantity","Price","Discount","Image"};
        String sqlTable = "OrderDetail";

        qb.setTables(sqlTable);
        Cursor c = qb.query(db,sqlSelect,"UserPhone=?",new String[]{userPhone},null,null,null);

        final List<Order> result = new ArrayList<>();
        if(c.moveToFirst()){
            do{
                result.add(new Order(
                        c.getString(c.getColumnIndex("UserPhone")),
                        c.getString(c.getColumnIndex("ProductId")),
                        c.getString(c.getColumnIndex("ProductName")),
                        c.getString(c.getColumnIndex("Quantity")),
                        c.getString(c.getColumnIndex("Price")),
                        c.getString(c.getColumnIndex("Discount")),
                        c.getString(c.getColumnIndex("Image"))));
            }while(c.moveToNext());

        }
        return result;
    }

    public void addToCart(Order order){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT OR REPLACE INTO OrderDetail(UserPhone,ProductId,ProductName,Quantity,Price,Discount,Image) VALUES ('%s','%s','%s','%s','%s','%s','%s')",
                order.getUserPhone(),
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount(),
                order.getImage());

        db.execSQL(query);
    }

    public void cleanCart(String userPhone){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail WHERE UserPhone='%s'",userPhone);
        db.execSQL(query);
    }

    public int getCountCarts(String userPhone) {
        int count = 0;
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT COUNT(*) FROM OrderDetail WHERE UserPhone='%s'",userPhone);
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                count = cursor.getInt(0);
            }while (cursor.moveToNext());
        }
        return count;
    }

    public void updateCart(Order order) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE OrderDetail SET Quantity='%s' WHERE UserPhone='%s' AND ProductId='%s'",order.getQuantity(),order.getUserPhone(),order.getProductId());
        db.execSQL(query);
    }
    public void increaseCart(String  userPhone, String foodId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE OrderDetail SET Quantity=Quantity+1 WHERE UserPhone='%s' AND ProductId='%s'",userPhone,foodId);
        db.execSQL(query);
    }


    public void addToFavorites(String foodId){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO Favorites(FoodId) VALUES ('%s');",foodId);
        db.execSQL(query);
    }

    public void deleteFromFavorites(String foodId){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM Favorites WHERE FoodId='%s'",foodId);
        db.execSQL(query);
    }

    public boolean foodFavorites(String foodId){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format(" SELECT * FROM Favorites WHERE FoodId='%s'",foodId);
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.getCount() < 1){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }


    public void deleteFromCart(String productId, String phone) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail WHERE UserPhone='%s'AND ProductId='%s'",phone,productId);
        db.execSQL(query);
    }
}
