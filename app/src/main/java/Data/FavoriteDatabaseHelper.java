package Data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class FavoriteDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "favorite.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_FAVORITES = "favorites";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_FOLDER_NAME = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_IMAGE_VIDEO_LIST_ID = "image_video_list_id";
    private static final String COLUMN_IMAGE_VIDEO_ID = "image_video_id";
    private static final String COLUMN_IMAGE_ID = "image_id";

    private static final String TABLE_FOLDERS = "folders";
    private static final String COLUMN_FOLDER_ID = "folder_id";
    private static final String COLUMN_FOLDER_DISPLAY_NAME = "folder_display_name";


    private static final String CREATE_TABLE_FAVORITES = "CREATE TABLE " + TABLE_FAVORITES + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_FOLDER_NAME + " TEXT, " +
            COLUMN_DESCRIPTION + " TEXT, " +
            COLUMN_IMAGE_ID + " INTEGER, " +
            COLUMN_IMAGE_VIDEO_ID + " INTEGER, " +
            COLUMN_IMAGE_VIDEO_LIST_ID + " INTEGER);";

    private static final String CREATE_TABLE_FOLDERS = "CREATE TABLE " + TABLE_FOLDERS + " (" +
            COLUMN_FOLDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_FOLDER_DISPLAY_NAME + " TEXT);";

    public FavoriteDatabaseHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_FAVORITES);
        db.execSQL(CREATE_TABLE_FOLDERS);
        ContentValues values = new ContentValues();
        values.put("folder_display_name","默认收藏夹");
        db.insert("folders",null,values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOLDERS);
        onCreate(db);
    }

    public void addFavorite(FavoriteData favorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        String insertQuery = "INSERT INTO " + TABLE_FAVORITES + " (" +
                COLUMN_FOLDER_NAME + ", " +
                COLUMN_DESCRIPTION + ", " +
                COLUMN_IMAGE_ID + ", " +
                COLUMN_IMAGE_VIDEO_ID + ", " +
                COLUMN_IMAGE_VIDEO_LIST_ID + ") VALUES ('" +
                favorite.getFolderName().replace("'", "''") + "', '" +
                favorite.getDescription().replace("'", "''") + "', " +
                favorite.getImageResourceId() + ", " +
                favorite.getVideoImageResourceId() + ", " +
                favorite.getVideoListImageResourceId() + ");";
        db.execSQL(insertQuery);
        db.close();
    }

    public List<FavoriteData> getALLFavorites() {
        List<FavoriteData> favorites = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FAVORITES,null,null,null,null,null, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndex(COLUMN_FOLDER_NAME));
                String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
                int imageId = cursor.getInt(cursor.getColumnIndex(COLUMN_IMAGE_ID));
                int imageVideoId = cursor.getInt(cursor.getColumnIndex(COLUMN_IMAGE_VIDEO_ID));
                int imageVideoListId = cursor.getInt(cursor.getColumnIndex(COLUMN_IMAGE_VIDEO_LIST_ID));
                favorites.add(new FavoriteData(description,imageId,imageVideoId,imageVideoListId,title,id));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return favorites;
    }

    public List<String> getAllFolderNames() {
        List<String> folderNames = new ArrayList<>();
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            Cursor cursor = db.query(TABLE_FOLDERS, new String[]{COLUMN_FOLDER_DISPLAY_NAME}, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    String folderName = cursor.getString(cursor.getColumnIndex(COLUMN_FOLDER_DISPLAY_NAME));
                    folderNames.add(folderName);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return folderNames;
    }

    public void deleteFavorite(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = " DELETE FROM " + TABLE_FAVORITES + " WHERE " +
                COLUMN_ID + " = " + id;
        db.execSQL(deleteQuery);
        db.close();
    }

    public void addFolder(String folderName) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_FOLDER_DISPLAY_NAME, folderName);
            db.insert(TABLE_FOLDERS, null, values);
        }
    }

    public void deleteFolder(String folderName) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            String whereClause = COLUMN_FOLDER_DISPLAY_NAME + " =?";
            String[] whereArgs = {folderName};
            db.delete(TABLE_FOLDERS, whereClause, whereArgs);
            whereClause = COLUMN_FOLDER_NAME + " =?";
            db.delete(TABLE_FAVORITES, whereClause, whereArgs);
        }
    }

    public boolean isFavoriteExists(Data data) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_FAVORITES
                + " WHERE description = ?"
                + " AND "+ COLUMN_IMAGE_ID +" = ?"
                + " AND "+ COLUMN_IMAGE_VIDEO_ID +" = ?"
                + " AND "+ COLUMN_IMAGE_VIDEO_LIST_ID +" = ?";
        Cursor cursor = db.rawQuery(query, new String[]{
                data.getDescription(),
                String.valueOf(data.getImageResourceId()),
                String.valueOf(data.getVideoImageResourceId()),
                String.valueOf(data.getVideoListImageResourceId())
        });
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public void deleteFavorite(Data data) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(
                TABLE_FAVORITES,
                COLUMN_DESCRIPTION + " = ? AND " + COLUMN_IMAGE_ID +" = ? AND " + COLUMN_IMAGE_VIDEO_ID +" = ? AND " + COLUMN_IMAGE_VIDEO_LIST_ID + " = ?",
                new String[]{
                        data.getDescription(),
                        String.valueOf(data.getImageResourceId()),
                        String.valueOf(data.getVideoImageResourceId()),
                        String.valueOf(data.getVideoListImageResourceId())
                }
        );
    }

}
