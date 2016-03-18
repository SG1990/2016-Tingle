package dk.akorach.android.tingle.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.UUID;

import dk.akorach.android.tingle.Thing;
import dk.akorach.android.tingle.database.ThingDbSchema.ThingTable;

/**
 * Created by SG on 11.03.2016.
 */
public class ThingCursorWrapper extends CursorWrapper {
    public ThingCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Thing getThing() {
        String uuidString = getString(getColumnIndex(ThingTable.Cols.UUID));
        String what = getString(getColumnIndex(ThingTable.Cols.WHAT));
        String where = getString(getColumnIndex(ThingTable.Cols.WHERE));

        Thing thing = new Thing(UUID.fromString(uuidString));
        thing.setWhat(what);
        thing.setWhere(where);

        return thing;
    }
}
