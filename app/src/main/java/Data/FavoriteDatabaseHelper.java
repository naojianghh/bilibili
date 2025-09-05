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
    private static final int DATABASE_VERSION = 4; // 版本号+1（从3→4）
    private static final String TABLE_FAVORITES = "favorites";
    private static final String COLUMN_ID = "id"; // 数据库自增主键
    private static final String COLUMN_ORIGINAL_ID = "original_id"; // 新增：存储Data的原始id
    private static final String COLUMN_FOLDER_NAME = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_IMAGE_VIDEO_LIST_ID = "image_video_list_id";
    private static final String COLUMN_IMAGE_VIDEO_ID = "image_video_id";
    private static final String COLUMN_IMAGE_ID = "image_id";

    private static final String TABLE_FOLDERS = "folders";
    private static final String COLUMN_FOLDER_ID = "folder_id";
    private static final String COLUMN_FOLDER_DISPLAY_NAME = "folder_display_name";
    private static final String COLUMN_FOLDER_COVER = "folder_cover";


    // 修改：添加COLUMN_ORIGINAL_ID字段
    private static final String CREATE_TABLE_FAVORITES = "CREATE TABLE " + TABLE_FAVORITES + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_ORIGINAL_ID + " INTEGER, " + // 存储原始Data的id
            COLUMN_FOLDER_NAME + " TEXT, " +
            COLUMN_DESCRIPTION + " TEXT, " +
            COLUMN_IMAGE_ID + " INTEGER, " +
            COLUMN_IMAGE_VIDEO_ID + " INTEGER, " +
            COLUMN_IMAGE_VIDEO_LIST_ID + " INTEGER);";

    private static final String CREATE_TABLE_FOLDERS = "CREATE TABLE " + TABLE_FOLDERS + " (" +
            COLUMN_FOLDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_FOLDER_DISPLAY_NAME + " TEXT, " +
            COLUMN_FOLDER_COVER + " TEXT);";

    public FavoriteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_FAVORITES);
        db.execSQL(CREATE_TABLE_FOLDERS);
        ContentValues values = new ContentValues();
        values.put(COLUMN_FOLDER_DISPLAY_NAME, "默认收藏夹");
        values.put(COLUMN_FOLDER_COVER, (String) null);
        db.insert(TABLE_FOLDERS, null, values);
    }

    // 升级数据库：新增original_id字段
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_FOLDERS +
                    " ADD COLUMN " + COLUMN_FOLDER_COVER + " TEXT DEFAULT NULL;");
        }
        // 新增：添加original_id字段（兼容旧数据）
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE " + TABLE_FAVORITES +
                    " ADD COLUMN " + COLUMN_ORIGINAL_ID + " INTEGER DEFAULT -1;");
        }
    }

    // 修改：插入数据时包含original_id
    public void addFavorite(FavoriteData favorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        String insertQuery = "INSERT INTO " + TABLE_FAVORITES + " (" +
                COLUMN_ORIGINAL_ID + ", " + // 新增字段
                COLUMN_FOLDER_NAME + ", " +
                COLUMN_DESCRIPTION + ", " +
                COLUMN_IMAGE_ID + ", " +
                COLUMN_IMAGE_VIDEO_ID + ", " +
                COLUMN_IMAGE_VIDEO_LIST_ID + ") VALUES (" +
                favorite.getOriginalId() + ", '" + // 传入原始id
                favorite.getFolderName().replace("'", "''") + "', '" +
                favorite.getDescription().replace("'", "''") + "', " +
                favorite.getImageResourceId() + ", " +
                favorite.getVideoImageResourceId() + ", " +
                favorite.getVideoListImageResourceId() + ");";
        db.execSQL(insertQuery);
        db.close();
    }

    // 修改：查询时读取original_id
    public List<FavoriteData> getALLFavorites() {
        List<FavoriteData> favorites = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FAVORITES, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID)); // 数据库自增id
                int originalId = cursor.getInt(cursor.getColumnIndex(COLUMN_ORIGINAL_ID)); // 原始id
                String title = cursor.getString(cursor.getColumnIndex(COLUMN_FOLDER_NAME));
                String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
                int imageId = cursor.getInt(cursor.getColumnIndex(COLUMN_IMAGE_ID));
                int imageVideoId = cursor.getInt(cursor.getColumnIndex(COLUMN_IMAGE_VIDEO_ID));
                int imageVideoListId = cursor.getInt(cursor.getColumnIndex(COLUMN_IMAGE_VIDEO_LIST_ID));
                // 传入originalId
                favorites.add(new FavoriteData(description, imageId, imageVideoId, imageVideoListId, title, id, originalId));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return favorites;
    }

    // 其余方法不变...
    public List<Folder> getAllFolders() {
        List<Folder> folders = new ArrayList<>();
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            Cursor cursor = db.query(
                    TABLE_FOLDERS,
                    new String[]{COLUMN_FOLDER_DISPLAY_NAME, COLUMN_FOLDER_COVER},
                    null, null, null, null, null
            );
            if (cursor.moveToFirst()) {
                do {
                    String folderName = cursor.getString(cursor.getColumnIndex(COLUMN_FOLDER_DISPLAY_NAME));
                    String folderCover = cursor.getString(cursor.getColumnIndex(COLUMN_FOLDER_COVER));
                    folders.add(new Folder(folderName, folderCover));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return folders;
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

    public void setFolderCover(String folderName, String coverPathOrUri) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_FOLDER_COVER, coverPathOrUri);
            String whereClause = COLUMN_FOLDER_DISPLAY_NAME + " = ?";
            String[] whereArgs = {folderName};
            db.update(TABLE_FOLDERS, values, whereClause, whereArgs);
        }
    }

    @SuppressLint("Range")
    public String getFolderCover(String folderName) {
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            Cursor cursor = db.query(
                    TABLE_FOLDERS,
                    new String[]{COLUMN_FOLDER_COVER},
                    COLUMN_FOLDER_DISPLAY_NAME + " = ?",
                    new String[]{folderName},
                    null, null, null
            );
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex(COLUMN_FOLDER_COVER));
            }
            cursor.close();
        }
        return null;
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
            values.put(COLUMN_FOLDER_COVER, (String) null);
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
                + " WHERE " + COLUMN_ORIGINAL_ID + " = ?"; // 优化：通过original_id判断更高效
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(data.getId())});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public void deleteFavorite(Data data) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(
                TABLE_FAVORITES,
                COLUMN_ORIGINAL_ID + " = ?", // 通过original_id删除
                new String[]{String.valueOf(data.getId())}
        );
    }

    public static class Folder {
        private String folderName;
        private String folderCover;

        public Folder(String folderName, String folderCover) {
            this.folderName = folderName;
            this.folderCover = folderCover;
        }

        public String getFolderName() {
            return folderName;
        }

        public void setFolderName(String folderName) {
            this.folderName = folderName;
        }

        public String getFolderCover() {
            return folderCover;
        }

        public void setFolderCover(String folderCover) {
            this.folderCover = folderCover;
        }
    }
}