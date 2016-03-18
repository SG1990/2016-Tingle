package dk.akorach.android.tingle.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import dk.akorach.android.tingle.database.ThingDbSchema.ThingTable;

/**
 * Created by SG on 11.03.2016.
 */
public class ThingBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "thingBase.db";

    public ThingBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + ThingTable.NAME + "(" +
                        " _id integer primary key autoincrement, " +
                        ThingTable.Cols.UUID + ", " +
                        ThingTable.Cols.WHAT + ", " +
                        ThingTable.Cols.WHERE +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
