package e3.projet;

/**
 * Created by anupamchugh on 19/10/15.
 * Modified by Théo Van Rooij 18/05/21
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_NAME = "ALARMS";

    // Table columns
    public static final String _ID = "_id";
    public static final String HOUR = "hour";
    public static final String MINUTES = "minutes";
    public static final String ORANGES = "nbOranges";
    public static final String ENABLE = "enable";
    public static final String MONDAY = "enableMONDAY";
    public static final String TUESDAY = "enableTUESDAY";
    public static final String WEDNESDAY = "enableWEDNESDAY";
    public static final String THURSDAY = "enableTHURSDAY";
    public static final String FRIDAY = "enableFRIDAY";
    public static final String SATURDAY = "enableSATURDAY";
    public static final String SUNDAY = "enableSUNDAY";


    // Database Information
    static final String DB_NAME = "E3_PROJET.DB";

    // database version
    static final int DB_VERSION = 1;

    // Creating table query
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + HOUR + " TINYINT NOT NULL, " + MINUTES + " TINYINT NOT NULL, " + ORANGES + " TINYINT NOT NULL, "+ ENABLE +" TINYINT NOT NULL, "+  SUNDAY +" TINYINT NOT NULL,"+ MONDAY +" TINYINT NOT NULL, "+ TUESDAY +" TINYINT NOT NULL, "+ WEDNESDAY +" TINYINT NOT NULL" +
            ", "+ THURSDAY +" TINYINT NOT NULL, "+ FRIDAY +" TINYINT NOT NULL, "+ SATURDAY +" TINYINT NOT NULL);";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
