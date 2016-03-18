package dk.akorach.android.tingle;

import java.util.UUID;

public class Thing {

    private UUID mId;
    private String mWhat;
    private String mWhere;

    public Thing() {
        this(UUID.randomUUID());
    }

    public Thing(UUID id) {
        mId = id;
    }

    public Thing(String what, String where) {
        mId = UUID.randomUUID();
        mWhat = what;
        mWhere = where;
    }

    public UUID getId() {
        return mId;
    }

    public String getWhat() { return mWhat; }
    public void setWhat(String what) { mWhat = what; }

    public String getWhere() { return mWhere; }
    public void setWhere(String where) { mWhere = where; }

    public String oneLine(String pre, String post) {
        return pre + mWhat + " " + post + mWhere;
    }

    @Override
    public String toString() {
        return oneLine("Item: ", "is here: ");
    }

}
