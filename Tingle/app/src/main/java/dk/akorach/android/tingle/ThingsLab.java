package dk.akorach.android.tingle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dk.akorach.android.tingle.database.ThingBaseHelper;
import dk.akorach.android.tingle.database.ThingCursorWrapper;
import dk.akorach.android.tingle.database.ThingDbSchema;
import dk.akorach.android.tingle.database.ThingDbSchema.ThingTable;

/**
 * Created by akor on 11.02.2016.
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

    public List<Thing> findThingsByName(String like){
        if(like == null || like.isEmpty()) return null;

        List<Thing> matches = new ArrayList<>();
        like = "%" + like + "%";

        ThingCursorWrapper cursor = queryCrimesOrdered(
                ThingTable.Cols.WHAT + " like ?",
                new String[] { like }
        );

        try {
            if(cursor.getCount() == 0) return null;

            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                matches.add(cursor.getThing());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return matches;
    }

    public void addThing(Thing thing) {
        ContentValues values = getContentValues(thing);

        mDatabase.insert(ThingTable.NAME, null, values);
    }

    public void deleteThing(Thing t) {
        String uuidString = t.getId().toString();

        File file = getPhotoFile(t);
        if(file != null && file.exists())
            file.delete();

        mDatabase.delete(ThingTable.NAME,
                ThingTable.Cols.UUID + "= ?",
                new String[]{uuidString});
    }

    public void updateThing(Thing t) {
        String uuidString = t.getId().toString();
        ContentValues values = getContentValues(t);

        mDatabase.update(ThingTable.NAME, values,
                ThingTable.Cols.UUID + "= ?",
                new String[]{uuidString});

    }

    public File getPhotoFile(Thing thing) {
        if(thing.getFilename() == null || thing.getFilename().isEmpty()){
            return null;
        }

        File externalFilesDir = mContext
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if(externalFilesDir == null) {
            return null;
        }

        return new File(externalFilesDir, thing.getFilename());
    }

    public String getNewFileName() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        java.util.Date date = new java.util.Date();
        String datetime = dateFormat.format(date);
        String filename = "THING_" + datetime;

        return filename;
    }

    private static ContentValues getContentValues (Thing thing) {
        ContentValues values = new ContentValues();
        values.put(ThingTable.Cols.UUID, thing.getId().toString());
        values.put(ThingTable.Cols.WHAT, thing.getWhat());
        values.put(ThingTable.Cols.BARCODE, thing.getBarcode());
        values.put(ThingTable.Cols.WHERE, thing.getWhere());
        values.put(ThingTable.Cols.FILENAME, thing.getFilename());

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

    private ThingCursorWrapper queryCrimesOrdered(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                ThingTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                ThingTable.Cols.WHAT + " ASC"
        );

        return new ThingCursorWrapper(cursor);
    }
}
