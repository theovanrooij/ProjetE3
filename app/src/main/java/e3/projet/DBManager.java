package e3.projet;

/**
 * Created by anupamchugh on 19/10/15.
 * Modified by Théo Van Rooij 18/05/21
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;

public class DBManager {

    private DatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public int insert(int hour, int minutes,  int nbOranges, boolean enable ,HashMap<String, Short> boolJours) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.HOUR, hour);
        contentValues.put(DatabaseHelper.MINUTES, minutes);
        contentValues.put(DatabaseHelper.ORANGES, nbOranges);
        contentValues.put(DatabaseHelper.ENABLE, enable);
        for  (String i : boolJours.keySet()) {
            contentValues.put("enable"+i, boolJours.get(i).shortValue());
        }
        return (int) database.insert(DatabaseHelper.TABLE_NAME, null, contentValues);
    }

    public Cursor fetchAll() {
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
    public Cursor fetch(String _id) {
        String[] whereArgs =  new String[] { _id };
       Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, null, "_id == ?", whereArgs, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(int _id, int hour, int minutes, int nbOranges, boolean enable ,HashMap<String, Short> boolJours) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.HOUR, hour);
        contentValues.put(DatabaseHelper.MINUTES, minutes);
        contentValues.put(DatabaseHelper.ORANGES, nbOranges);
        contentValues.put(DatabaseHelper.ENABLE, enable);
        for  (String i : boolJours.keySet()) {
            contentValues.put("enable"+i, boolJours.get(i).shortValue());
        }
        int i = database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper._ID + " = " + _id, null);
        return i;
    }

    public void delete(int _id) {
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper._ID + "=" + _id, null);
    }

}
