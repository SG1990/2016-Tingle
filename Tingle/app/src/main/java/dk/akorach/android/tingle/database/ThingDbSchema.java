package dk.akorach.android.tingle.database;

/**
 * Created by SG on 11.03.2016.
 */
public class ThingDbSchema {
    public static final class ThingTable {
        public static final String NAME = "things";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String WHAT = "what";
            public static final String BARCODE = "barcode";
            public static final String WHERE = "whereabouts";
            public static final String FILENAME = "filename";
        }
    }
}
