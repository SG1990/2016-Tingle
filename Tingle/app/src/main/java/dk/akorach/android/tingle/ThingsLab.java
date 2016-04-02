package dk.akorach.android.tingle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dk.akorach.android.tingle.database.ThingBaseHelper;
import dk.akorach.android.tingle.database.ThingCursorWrapper;
import dk.akorach.android.tingle.database.ThingDbSchema;
import dk.akorach.android.tingle.database.ThingDbSchema.ThingTable;

/**
 * Created by SG on 11.02.2016.
 */
public class ThingsLab {
    private static ThingsLab sThingsLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;


    public static ThingsLab getInstance(Context context) {
        if(sThingsLab == null)
            sThingsLab = new ThingsLab(context);

        return sThingsLab;
    }

    private ThingsLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new ThingBaseHelper(mContext)
                .getWritableDatabase();
    }

    public List<Thing> getThings() {
        List<Thing> things = new ArrayList<>();

        ThingCursorWrapper cursor = queryCrimes(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                things.add(cursor.getThing());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return things;
    }

    public Thing getThing(UUID id) {
        ThingCursorWrapper cursor = queryCrimes(
                ThingTable.Cols.UUID + "= ?",
                new String[] { id.toString() }
        );

        try {
            if(cursor.getCount() == 0) return null;

            cursor.moveToFirst();
            return cursor.getThing();
        } finally {
            cursor.close();
        }
    }

    public Thing getLastThing() {
        ThingCursorWrapper cursor = queryCrimes(null, null);
        try {
            cursor.moveToLast();
            return cursor.getThing();
        } finally {
            cursor.close();
        }
    }

    public void addThing(Thing thing) {
        ContentValues values = getContentValues(thing);

        mDatabase.insert(ThingTable.NAME, null, values);
    }

    public void deleteThing(Thing t) {
        String uuidString = t.getId().toString();

        mDatabase.delete(ThingTable.NAME,
                ThingTable.Cols.UUID + "= ?",
                new String[]{uuidString});
    }

    public void updateThing(Thing t) {
        String uuidString = t.getId().toString();
        ContentValues values = getContentValues(t);

        mDatabase.update(ThingTable.NAME, values,
                ThingTable.Cols.UUID + "= ?",
                new String[] {uuidString});

    }

    private static ContentValues getContentValues (Thing thing) {
        ContentValues values = new ContentValues();
        values.put(ThingTable.Cols.UUID, thing.getId().toString());
        values.put(ThingTable.Cols.WHAT, thing.getWhat());
        values.put(ThingTable.Cols.BARCODE, thing.getBarcode());
        values.put(ThingTable.Cols.WHERE, thing.getWhere());

        return values;
    }

    private ThingCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                ThingTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new ThingCursorWrapper(cursor);
    }

}
