package com.advengers.mabo.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.advengers.mabo.Model.ChatMessage;
import com.advengers.mabo.Model.User;
import com.advengers.mabo.Utils.LogUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.advengers.mabo.Interfaces.Keys.EMAIL;
import static com.advengers.mabo.Interfaces.Keys.ID;
import static com.advengers.mabo.Interfaces.Keys.IMAGENAME;
import static com.advengers.mabo.Interfaces.Keys.PHONE;
import static com.advengers.mabo.Interfaces.Keys.PROFILE_IMAGENAME;
import static com.advengers.mabo.Interfaces.Keys.ROOMID;
import static com.advengers.mabo.Interfaces.Keys.USERNAME;

public class MyDBHandler extends SQLiteOpenHelper {
    //information of database
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MaboDB";
    public static final String TABLE_NAME = "Friends";
    public static final String TABLE_MESSAGE_NAME = "Message";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "username";
    public static final String CALLER = "caller";
    public static final String RECEIVER = "receiver";
    public static final String MESSAGE = "message";
    public static final String DATE = "date";
    public static final String TYPE = "type";

    String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ID +
            " TEXT," + COLUMN_NAME + " TEXT,"+EMAIL+" TEXT,"+PROFILE_IMAGENAME+" TEXT,"+PHONE+ " TEXT,"+ROOMID+" TEXT)";
    String CREATE_MESSAGE_TABLE = "CREATE TABLE " + TABLE_MESSAGE_NAME + "(" + COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ CALLER +
            " TEXT," +USERNAME+" TEXT,"+ RECEIVER + " TEXT,"+MESSAGE+" TEXT,"+TYPE+" TEXT, "+ROOMID+" TEXT, "+DATE+" TEXT)";
    Context context;
    //initialize the database
    public MyDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            LogUtils.e(CREATE_TABLE);
            sqLiteDatabase.execSQL(CREATE_TABLE);
            sqLiteDatabase.execSQL(CREATE_MESSAGE_TABLE);
        }catch (Exception e)
        {
            LogUtils.e("Exceptiom "+e.getMessage());
        }

    }

    /*  @Override
        public void onCreate(SQLiteDatabase db) {
            LogUtils.e("I am table calling");
            LogUtils.e(CREATE_TABLE);
            db.execSQL(CREATE_TABLE);
        }*/
    public void insertFriends(User user) {
        // get writable database as we want to write data

        if(!checkfriendexist(user.getId())) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            // `id` and `timestamp` will be inserted automatically.
            // no need to add them
            values.put(ID, user.getId());
            values.put(USERNAME, user.getUsername());
            values.put(PHONE, user.getPhone());
            values.put(EMAIL, user.getEmail());
            values.put(PROFILE_IMAGENAME, user.getprofile_imagename());
            values.put(ROOMID,user.getRoomid());
            LogUtils.e("RoomId "+ user.getRoomid());
            // insert row
            long id = db.insert(TABLE_NAME, null, values);
            db.close();
        }
        // close db connection


        // return newly inserted row id
      //  return id;
    }
    public void deleteFriends(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?",
                new String[]{id});
        db.close();
    }
    public void deleteallFriends() {
        String selectQuery = "DELETE FROM " + TABLE_NAME;
        this.getWritableDatabase().execSQL(selectQuery);
    }
    public void Createfriends()
    {
        this.getWritableDatabase().execSQL(CREATE_TABLE);
    }
    public void Createmessages()
    {
        this.getWritableDatabase().execSQL(CREATE_MESSAGE_TABLE);
    }
    public boolean checkfriendexist(String id)
    {

            // Select All Query
            String selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = " + id;

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    User note = new User();
                    note.setId(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
                    note.setEmail(cursor.getString(cursor.getColumnIndex(EMAIL)));
                    note.setprofile_imagename(cursor.getString(cursor.getColumnIndex(PROFILE_IMAGENAME)));
                    note.setPhone(cursor.getString(cursor.getColumnIndex(PHONE)));
                    note.setUsername(cursor.getString(cursor.getColumnIndex(USERNAME)));
                    note.setRoomid(cursor.getString(cursor.getColumnIndex(ROOMID)));
                    LogUtils.e(note.getId() + " " + note.getUsername() + " " + note.getEmail() + " " + note.getPhone() + " " + note.getprofile_imagename());
                    if (id.equals(note.getId()))
                        return true;
                    else
                        return false;
                } while (cursor.moveToNext());
            }

            // close db connection
            db.close();

        return false;
    }
    public boolean isTableExists(String tableName) {

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'";
        try (Cursor cursor = db.rawQuery(query, null)) {
            if(cursor!=null) {
                if(cursor.getCount()>0) {
                    return true;
                }
            }
            return false;
        }
    }
    public ArrayList<User> getFriendsList() {

        ArrayList<User> notes = new ArrayList<>();
        if(isTableExists(TABLE_NAME)) {
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                User note = new User();
                note.setId(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
                note.setEmail(cursor.getString(cursor.getColumnIndex(EMAIL)));
                note.setprofile_imagename(cursor.getString(cursor.getColumnIndex(PROFILE_IMAGENAME)));
                note.setPhone(cursor.getString(cursor.getColumnIndex(PHONE)));
                note.setUsername(cursor.getString(cursor.getColumnIndex(USERNAME)));
                note.setRoomid(cursor.getString(cursor.getColumnIndex(ROOMID)));
                LogUtils.e(note.getId()+" "+note.getUsername()+" "+note.getEmail()+" "+note.getPhone()+" "+note.getprofile_imagename());
                notes.add(note);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();
        }else{
            Createfriends();
        }
        // return notes list
        return notes;
    }

    public void dropfriends()
    {
        this.getWritableDatabase().execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {}

    public void insertMessages(String name,String from,String to,String message,String type,String roomid) {
        // get writable database as we want to write data

        if(isTableExists(TABLE_MESSAGE_NAME)) {
            SQLiteDatabase db = this.getWritableDatabase();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            ContentValues values = new ContentValues();
            // `id` and `timestamp` will be inserted automatically.
            // no need to add them
            values.put(CALLER,from);
            values.put(USERNAME,name);
            values.put(RECEIVER,to);
            values.put(MESSAGE,message);
            values.put(DATE,dateFormat.format(date));
            values.put(TYPE,type);
            values.put(ROOMID,roomid);
            // insert row
            long id = db.insert(TABLE_MESSAGE_NAME, null, values);
            db.close();
        }
        else
        {
            Createmessages();
        }
        // close db connection


        // return newly inserted row id
        //  return id;
    }
    public ArrayList<ChatMessage> getMessageList(String roomid) {

        ArrayList<ChatMessage> notes = new ArrayList<>();
        if(isTableExists(TABLE_MESSAGE_NAME)) {
            // Select All Query
            String selectQuery = "SELECT  * FROM " + TABLE_MESSAGE_NAME + " WHERE "+ROOMID + " = '" + roomid+"'" ;

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    ChatMessage note = new ChatMessage();
                    note.setId(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
                    note.setCaller(cursor.getString(cursor.getColumnIndex(CALLER)));
                    note.setReceiver(cursor.getString(cursor.getColumnIndex(RECEIVER)));
                    note.setMessage(cursor.getString(cursor.getColumnIndex(MESSAGE)));
                    note.setUsername(cursor.getString(cursor.getColumnIndex(USERNAME)));
                    note.setType(cursor.getString(cursor.getColumnIndex(TYPE)));
                    note.setDate(cursor.getString(cursor.getColumnIndex(DATE)));
                    note.setRoomid(cursor.getString(cursor.getColumnIndex(ROOMID)));
                    LogUtils.e(note.getId()+" "+note.getUsername()+" "+note.getCaller()+" "+note.getReceiver()+" "+note.getMessage()+" "+note.getType()+" "+note.getDate()+" "+note.getRoomid());
                    notes.add(note);
                } while (cursor.moveToNext());
            }

            // close db connection
            db.close();
        }else{
            Createfriends();
        }
        // return notes list
        return notes;
    }
}

