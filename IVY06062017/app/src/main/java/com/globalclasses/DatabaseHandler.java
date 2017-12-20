package com.globalclasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "contactsManager";

    // Contacts table name
    private static final String TABLE_CONTACTS = "contacts";
    private static final String LOGIN_USER_INFO = "contacts";

    // Contacts Table Columns names
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_ID = "id_contact";
    private static final String KEY_NAME = "user_name";
    private static final String KEY_PH_NO = "phone_number";
    private static final String KEY_E_MAIL = "email";
    private static final String KEY_IS_AlREADY_ADDED = "is_already_added";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "(" + KEY_ID + " TEXT," + KEY_NAME + " TEXT,"
                + KEY_PH_NO + " TEXT," + KEY_E_MAIL + " TEXT," + KEY_IS_AlREADY_ADDED + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new contact
    public void addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, contact.getIdContact());
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PH_NO, contact.getPhoneNumber());
        values.put(KEY_E_MAIL, contact.getE_mail()); // Contact Name
        values.put(KEY_IS_AlREADY_ADDED, contact.getIs_already_added()); // Contact Phone
        // Inserting Row
        db.insert(TABLE_CONTACTS, null, values);
        db.close(); // Closing database connection
    }

    // Adding new user login info
    void addContactofLoginUser(Contact contactUser) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, contactUser.getUserId());
        values.put(KEY_ID, contactUser.getIdContact());
        values.put(KEY_IS_AlREADY_ADDED, contactUser.getIs_already_added()); // Contact Phone
        // Inserting Row
        db.insert(TABLE_CONTACTS, null, values);
        db.close(); // Closing database connection
    }


    // Getting single contact
    Contact getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS, new String[]{KEY_ID, KEY_NAME, KEY_PH_NO}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Contact contact = new Contact(cursor.getString(0),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        // return contact
        return contact;
    }

    // Getting All Contacts
    public List<Contact> getAllContacts() {
        List<Contact> contactList = new ArrayList<Contact>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

//        String sortOrder = ContactsContract.Data.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
//        Uri contactsUri = ContactsContract.Data.CONTENT_URI;
//        Cursor cursor = activity.getContentResolver().query(contactsUri, null, null, null, sortOrder);


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setIdContact(cursor.getString(0));
                contact.setName(cursor.getString(1));
                contact.setE_mail(cursor.getString(2));
                contact.setPhoneNumber(cursor.getString(3));
                contact.setIs_already_added(cursor.getString(4));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }


    // getting contact by name or number

    // Getting All Contacts by searchtext
    public List<Contact> getContactbyNameOrPhoneNumber(String searchtext) {
        List<Contact> contactList = new ArrayList<Contact>();

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = " select * from " + TABLE_CONTACTS + " where " + KEY_ID + " like '"
                + "%" + searchtext + "%"
                + "' or " + KEY_NAME + " like '"
                + "%" + searchtext + "%"
                + "%' or " + KEY_PH_NO + " like '%"
                + "%" + searchtext + "%"
                + "%' or " + KEY_E_MAIL + " like '%"
                + "%" + searchtext + "%"
                + "%' or " + KEY_IS_AlREADY_ADDED + " like '%"
                + "%" + searchtext + "%"
                + "%' ORDER BY " + KEY_NAME + " DESC ";


        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setIdContact(cursor.getString(0));
                contact.setName(cursor.getString(1));
                contact.setE_mail(cursor.getString(2));
                contact.setPhoneNumber(cursor.getString(3));
                contact.setIs_already_added(cursor.getString(4));
                // Adding contact to list
                contactList.add(contact);
                GlobalMethod.write("====getsearchlist" + contactList);
            } while (cursor.moveToNext());
        }
        // return contact list
        return contactList;
    }

    // Updating single contact
    public int updateContact(Contact contact, String is_alreaady_value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IS_AlREADY_ADDED, is_alreaady_value);
        // updating row
        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?", new String[]{contact.getIdContact()});
    }

    // Updating single contact
    public int updateContact(Contact contact, String id_contact, String is_alreaady_value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IS_AlREADY_ADDED, is_alreaady_value);
        values.put(KEY_ID, id_contact);
        // updating row
        return db.update(TABLE_CONTACTS, values, KEY_PH_NO + " = ?", new String[]{contact.getPhoneNumber()});
    }

    // Updateing contact basis of id_contact

    public int updateContactId_Contact(Contact contact, String id_contact, String user_name, String user_e_mail, String phone_number,
                                       String is_already_added) {
        GlobalMethod.write("====isalready_added" + is_already_added);
        if (TextUtils.isEmpty(phone_number.trim()) && phone_number.trim().length() < 8) {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_CONTACTS, KEY_ID + " = ?", new String[]{contact.getIdContact()});
            db.close();
            return -1;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IS_AlREADY_ADDED, is_already_added);
        values.put(KEY_E_MAIL, user_e_mail);
        values.put(KEY_NAME, user_name);
        values.put(KEY_PH_NO, phone_number);
        // updating row
        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?", new String[]{contact.getIdContact()});
    }

    // Deleting single contact
    public void deleteContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + " = ?", new String[]{contact.getIdContact()});
        db.close();
    }


    // Getting contacts Count
    public String getContactsCount() {
        String countQuery = "SELECT  count(*) FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        if (cursor.moveToFirst()) {
            do {
//                Contact contact = new Contact();

                return cursor.getString(0);

            } while (cursor.moveToNext());

        } else {
            return "0";
        }

        // return count

    }

    public void clearDataBase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_CONTACTS);
        db.close();
    }
}
