package com.example.smarttour;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DBName = "SmartTour.db";

    public DBHelper(Context context) {
        super(context, DBName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        db.execSQL("CREATE TABLE users(" +
                "username TEXT PRIMARY KEY, " +
                "password TEXT)");

        // Create places table
        db.execSQL("CREATE TABLE Places (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "location TEXT, " +
                "type TEXT, " +
                "name TEXT, " +
                "address TEXT)");

        // Create feedback table
        db.execSQL("CREATE TABLE feedback (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT, " +
                "rating INTEGER, " +
                "review TEXT, " +
                "FOREIGN KEY(username) REFERENCES users(username))");

        // Insert initial data
        insertInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS Places");
        db.execSQL("DROP TABLE IF EXISTS feedback");
        onCreate(db);
    }

    // Hash password method
    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // Register user
    public boolean registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", hashPassword(password));

        // Check if username already exists
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ?", new String[]{username});
        boolean userExists = cursor.getCount() > 0;
        cursor.close();

        if (userExists) {
            return false;  // Username already exists
        }

        long result = db.insert("users", null, contentValues);
        return result != -1;
    }

    // Check username and password for login
    public boolean checkUsernamePassword(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ? AND password = ?",
                new String[]{username, hashPassword(password)});
        boolean isLoggedIn = cursor.getCount() > 0;
        cursor.close();
        return isLoggedIn;
    }

    public boolean insertFeedback(String username, int rating, String review) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("rating", rating);
        contentValues.put("review", review);

        long result = db.insert("feedback", null, contentValues);
        return result != -1; // Returns true if insert was successful
    }

    // Fetch places by type and location
    public List<String> getPlacesByType(String location, String type) {
        List<String> places = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM Places WHERE location = ? AND type = ?", new String[]{location, type});
        while (cursor.moveToNext()) {
            places.add(cursor.getString(0));
        }
        cursor.close();
        return places;
    }

    // Get place address
    public String getPlaceAddress(String location, String type, String placeName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT address FROM Places WHERE location = ? AND type = ? AND name = ?",
                new String[]{location, type, placeName});
        String address = null;
        if (cursor.moveToFirst()) {
            address = cursor.getString(0);
        }
        cursor.close();
        return address;
    }

    // Get all unique locations
    public List<String> getAllLocations() {
        List<String> locations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT location FROM Places", null);
        while (cursor.moveToNext()) {
            locations.add(cursor.getString(0));
        }
        cursor.close();
        return locations;
    }

    // Insert initial data into the database
    private void insertInitialData(SQLiteDatabase db) {
        // Sample data with complete addresses
        String[] locations = {"Coimbatore", "Chennai"};
        String[] hotels = {
                "Radisson Blu",
                "Gokulam Park",
                "Le Meridien",
                "Banyan Hotel",
                "Grand Residence",
                "Le Royal Meridien"
        };
        String[] attractions = {
                "Kovai Kutralam Water Falls",
                "Ukkadam Lake",
                "Valparai",
                "Marina Beach",
                "Mahabalipuram",
                "Vandaloor Zoo"
        };
        String[] jobPlaces = {
                "Tidel Park",
                "India Land Tech Park",
                "KGiSL IT Park",
                "Chennai One IT SEZ",
                "International Tech Park",
                "Olympia Tech Park"
        };
        String[] addresses = {
                "Avinashi Rd, Peelamedu, Coimbatore, Tamil Nadu 641004", // Radisson Blu
                "1st Street, Gokulam Park, Coimbatore, Tamil Nadu 641018", // Gokulam Park
                "Le Meridien Rd, Coimbatore, Tamil Nadu 641014", // Le Meridien
                "Nehru Rd, Near Arumbakkam, Chennai, Tamil Nadu 600106", // Banyan Hotel
                "47, Grand Residence Rd, Velachery, Chennai, Tamil Nadu 600042", // Grand Residence
                "1, GST Rd, St Thomas Mount, Chennai, Tamil Nadu 600016", // Le Royal Meridien
                "Kovai Kutralam, Coimbatore, Tamil Nadu 642106", // Kovai Kutralam
                "Ukkadam, Coimbatore, Tamil Nadu 641001", // Ukkadam Lake
                "Valparai, Tamil Nadu 642127", // Valparai
                "Marina Beach, Chennai, Tamil Nadu 600001", // Marina Beach
                "Mahabalipuram, Tamil Nadu 603104", // Mahabalipuram
                "Vandaloor, Tamil Nadu 600048", // Vandaloor Zoo
                "Tidel Park, Coimbatore, Tamil Nadu 641014", // Tidel Park
                "India Land Tech Park, Coimbatore, Tamil Nadu 641014", // India Land Tech Park
                "KGiSL IT Park, Coimbatore, Tamil Nadu 641014", // KGiSL IT Park
                "Chennai One IT SEZ, Chennai, Tamil Nadu 600100", // Chennai One IT SEZ
                "International Tech Park, Chennai, Tamil Nadu 600100", // International Tech Park
                "Olympia Tech Park, Chennai, Tamil Nadu 600100" // Olympia Tech Park
        };

        // Insert hotels
        for (int i = 0; i < hotels.length; i++) {
            String location = i < 3 ? locations[0] : locations[1];
            db.execSQL("INSERT INTO Places (location, type, name, address) VALUES (?, ?, ?, ?)",
                    new Object[]{location, "Hotel", hotels[i], addresses[i]});
        }

        // Insert attractions
        for (int i = 0; i < attractions.length; i++) {
            String location = i < 3 ? locations[0] : locations[1];
            db.execSQL("INSERT INTO Places (location, type, name, address) VALUES (?, ?, ?, ?)",
                    new Object[]{location, "Attraction", attractions[i], addresses[i + hotels.length]});
        }

        // Insert job places
        for (int i = 0; i < jobPlaces.length; i++) {
            String location = i < 3 ? locations[0] : locations[1];
            db.execSQL("INSERT INTO Places (location, type, name, address) VALUES (?, ?, ?, ?)",
                    new Object[]{location, "Job Place", jobPlaces[i], addresses[i + hotels.length + attractions.length]});
        }
    }
}
