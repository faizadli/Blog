package com.example.blog;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private SQLiteDatabase database;
    private static final String DATABASE_NAME = "UserDB";
    private static final int DATABASE_VERSION = 3;

    // Users table
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_EMAIL = "email";

    // Blog posts table
    private static final String TABLE_POSTS = "posts";
    private static final String COLUMN_POST_ID = "post_id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_CREATED_AT = "created_at";
    private static final String COLUMN_USER_ID = "user_id";

    // Blog image table
    private static final String TABLE_POST_IMAGES = "post_images";
    private static final String COLUMN_IMAGE_ID = "image_id";
    private static final String COLUMN_POST_ID_FK = "post_id";
    private static final String COLUMN_IMAGE_PATH = "image_path";

    // Saved posts table
    private static final String TABLE_SAVED_POSTS = "saved_posts";
    private static final String COLUMN_SAVED_POST_ID = "saved_post_id";
    private static final String COLUMN_USER_ID_FK = "user_id";
    private static final String COLUMN_POST_ID_FK_SAVED = "post_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void open() {
        database = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT UNIQUE,"
                + COLUMN_EMAIL + " TEXT UNIQUE,"
                + COLUMN_PASSWORD + " TEXT"
                + ")";
        db.execSQL(CREATE_USERS_TABLE);

        // Create blog posts table
        String CREATE_POSTS_TABLE = "CREATE TABLE " + TABLE_POSTS + "("
                + COLUMN_POST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TITLE + " TEXT NOT NULL,"
                + COLUMN_CONTENT + " TEXT NOT NULL,"
                + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + COLUMN_USER_ID + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")"
                + ")";
        db.execSQL(CREATE_POSTS_TABLE);

        // Create post images table
        String CREATE_POST_IMAGES_TABLE = "CREATE TABLE " + TABLE_POST_IMAGES + "("
                + COLUMN_IMAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_POST_ID_FK + " INTEGER UNIQUE,"
                + COLUMN_IMAGE_PATH + " TEXT NOT NULL,"
                + "FOREIGN KEY(" + COLUMN_POST_ID_FK + ") REFERENCES " + TABLE_POSTS + "(" + COLUMN_POST_ID + ")"
                + ")";
        db.execSQL(CREATE_POST_IMAGES_TABLE);

        // Create saved posts table
        String CREATE_SAVED_POSTS_TABLE = "CREATE TABLE " + TABLE_SAVED_POSTS + "("
                + COLUMN_SAVED_POST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_ID_FK + " INTEGER,"
                + COLUMN_POST_ID_FK_SAVED + " INTEGER,"
                + "UNIQUE(" + COLUMN_USER_ID_FK + ", " + COLUMN_POST_ID_FK_SAVED + "),"
                + "FOREIGN KEY(" + COLUMN_USER_ID_FK + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "),"
                + "FOREIGN KEY(" + COLUMN_POST_ID_FK_SAVED + ") REFERENCES " + TABLE_POSTS + "(" + COLUMN_POST_ID + ")"
                + ")";
        db.execSQL(CREATE_SAVED_POSTS_TABLE);

        Log.d("DatabaseHelper", "All tables created successfully");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DatabaseHelper", "Upgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POST_IMAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVED_POSTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // User methods
    public boolean addUser(String username, String email, String password) {
        try {
            // Check if username exists
            String[] columns = { COLUMN_ID };
            String selection = COLUMN_USERNAME + "=? OR " + COLUMN_EMAIL + "=?";
            String[] selectionArgs = { username, email };

            Cursor cursor = database.query(TABLE_USERS, columns, selection, selectionArgs,
                    null, null, null);

            if (cursor.getCount() > 0) {
                cursor.close();
                return false;
            }
            cursor.close();

            ContentValues values = new ContentValues();
            values.put(COLUMN_USERNAME, username);
            values.put(COLUMN_EMAIL, email);
            values.put(COLUMN_PASSWORD, password);

            long result = database.insert(TABLE_USERS, null, values);
            return result != -1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isEmailExists(String email) {
        try {
            String[] columns = { COLUMN_ID };
            String selection = COLUMN_EMAIL + "=?";
            String[] selectionArgs = { email };

            Cursor cursor = database.query(TABLE_USERS, columns, selection, selectionArgs,
                    null, null, null);

            int count = cursor.getCount();
            cursor.close();
            return count > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkUser(String username, String password) {
        try {
            String[] columns = {COLUMN_ID};
            String selection = COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?";
            String[] selectionArgs = {username, password};

            Cursor cursor = database.query(TABLE_USERS, columns, selection, selectionArgs,
                    null, null, null);

            int count = cursor.getCount();
            cursor.close();
            return count > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @SuppressLint("Range")
    public String getUserEmail(String username) {
        try {
            String[] columns = {COLUMN_EMAIL};
            String selection = COLUMN_USERNAME + "=?";
            String[] selectionArgs = {username};

            Cursor cursor = database.query(TABLE_USERS, columns, selection, selectionArgs,
                    null, null, null);

            String email = "";
            if (cursor.moveToFirst()) {
                email = cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL));
            }
            cursor.close();
            return email;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // Blog post methods
    public long createPost(String title, String content, long userId) {
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_TITLE, title);
            values.put(COLUMN_CONTENT, content);
            values.put(COLUMN_USER_ID, userId);

            return database.insert(TABLE_POSTS, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean addPostImage(long postId, String imagePath) {
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_POST_ID_FK, postId);
            values.put(COLUMN_IMAGE_PATH, imagePath);

            long result = database.insert(TABLE_POST_IMAGES, null, values);
            return result != -1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @SuppressLint("Range")
    public String getPostImage(long postId) {
        try {
            String[] columns = {COLUMN_IMAGE_PATH};
            String selection = COLUMN_POST_ID_FK + "=?";
            String[] selectionArgs = {String.valueOf(postId)};

            Cursor cursor = database.query(TABLE_POST_IMAGES, columns, selection, selectionArgs,
                    null, null, null);

            String imagePath = null;
            if (cursor.moveToFirst()) {
                imagePath = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_PATH));
            }
            cursor.close();
            return imagePath;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Save Methods
    public boolean saveBlogPost(long userId, long postId) {
        try {
            Log.d("DatabaseHelper", "Attempting to save post. UserId: " + userId + ", PostId: " + postId);

            if (isBlogPostSaved(userId, postId)) {
                Log.d("DatabaseHelper", "Post already saved");
                return true;
            }

            ContentValues values = new ContentValues();
            values.put(COLUMN_USER_ID_FK, userId);
            values.put(COLUMN_POST_ID_FK_SAVED, postId);

            long result = database.insert(TABLE_SAVED_POSTS, null, values);
            Log.d("DatabaseHelper", "Save result: " + result);

            return result != -1;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error saving post: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean unsaveBlogPost(long userId, long postId) {
        try {
            Log.d("DatabaseHelper", "Attempting to unsave post. UserId: " + userId + ", PostId: " + postId);

            int result = database.delete(TABLE_SAVED_POSTS,
                    COLUMN_USER_ID_FK + "=? AND " + COLUMN_POST_ID_FK_SAVED + "=?",
                    new String[]{String.valueOf(userId), String.valueOf(postId)});

            Log.d("DatabaseHelper", "Unsave result: " + result);
            return result > 0;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error unsaving post: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @SuppressLint("Range")
    public boolean isBlogPostSaved(long userId, long postId) {
        Cursor cursor = null;
        try {
            String[] columns = {COLUMN_SAVED_POST_ID};
            String selection = COLUMN_USER_ID_FK + "=? AND " + COLUMN_POST_ID_FK_SAVED + "=?";
            String[] selectionArgs = {String.valueOf(userId), String.valueOf(postId)};

            cursor = database.query(TABLE_SAVED_POSTS, columns, selection, selectionArgs,
                    null, null, null);

            return cursor != null && cursor.getCount() > 0;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error checking if post is saved: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @SuppressLint("Range")
    public List<BlogPost> getSavedBlogPosts(long userId) {
        List<BlogPost> savedPosts = new ArrayList<>();
        Cursor cursor = null;
        try {
            String query = "SELECT p.* FROM " + TABLE_POSTS + " p " +
                    "INNER JOIN " + TABLE_SAVED_POSTS + " s " +
                    "ON p." + COLUMN_POST_ID + " = s." + COLUMN_POST_ID_FK_SAVED + " " +
                    "WHERE s." + COLUMN_USER_ID_FK + " = ?";

            cursor = database.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    BlogPost post = new BlogPost();
                    post.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_POST_ID)));
                    post.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                    post.setContent(cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT)));
                    post.setCreatedAt(cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_AT)));
                    long postUserId = cursor.getLong(cursor.getColumnIndex(COLUMN_USER_ID));
                    post.setUserId(postUserId);
                    post.setAuthorName(getUsernameById(postUserId));
                    post.setImagePath(getPostImage(post.getId()));

                    savedPosts.add(post);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting saved posts: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return savedPosts;
    }

    public boolean updatePost(long postId, String title, String content) {
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_TITLE, title);
            values.put(COLUMN_CONTENT, content);

            int result = database.update(TABLE_POSTS, values,
                    COLUMN_POST_ID + "=?", new String[]{String.valueOf(postId)});
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePostImage(long postId, String imagePath) {
        try {
            database.beginTransaction();

            database.delete(TABLE_POST_IMAGES,
                    COLUMN_POST_ID_FK + "=?", new String[]{String.valueOf(postId)});

            if (imagePath != null) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_POST_ID_FK, postId);
                values.put(COLUMN_IMAGE_PATH, imagePath);
                database.insert(TABLE_POST_IMAGES, null, values);
            }

            database.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            database.endTransaction();
        }
    }

    public boolean deletePost(long postId) {
        try {
            database.beginTransaction();

            database.delete(TABLE_SAVED_POSTS,
                    COLUMN_POST_ID_FK_SAVED + "=?",
                    new String[]{String.valueOf(postId)});

            database.delete(TABLE_POST_IMAGES,
                    COLUMN_POST_ID_FK + "=?",
                    new String[]{String.valueOf(postId)});

            int result = database.delete(TABLE_POSTS,
                    COLUMN_POST_ID + "=?",
                    new String[]{String.valueOf(postId)});

            database.setTransactionSuccessful();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            database.endTransaction();
        }
    }

    @SuppressLint("Range")
    public long getUserId(String username) {
        try {
            String[] columns = {COLUMN_ID};
            String selection = COLUMN_USERNAME + "=?";
            String[] selectionArgs = {username};

            Cursor cursor = database.query(TABLE_USERS, columns, selection, selectionArgs,
                    null, null, null);

            long userId = -1;
            if (cursor.moveToFirst()) {
                userId = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
            }
            cursor.close();
            return userId;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @SuppressLint("Range")
    public String getUsernameById(long userId) {
        try {
            String[] columns = {COLUMN_USERNAME};
            String selection = COLUMN_ID + "=?";
            String[] selectionArgs = {String.valueOf(userId)};

            Cursor cursor = database.query(TABLE_USERS, columns, selection, selectionArgs,
                    null, null, null);

            String username = "";
            if (cursor.moveToFirst()) {
                username = cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME));
            }
            cursor.close();
            return username;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @SuppressLint("Range")
    public List<BlogPost> getAllBlogPosts() {
        List<BlogPost> blogPosts = new ArrayList<>();
        try {
            String[] columns = {
                    COLUMN_POST_ID,
                    COLUMN_TITLE,
                    COLUMN_CONTENT,
                    COLUMN_CREATED_AT,
                    COLUMN_USER_ID
            };

            Cursor cursor = database.query(TABLE_POSTS, columns, null, null,
                    null, null, COLUMN_CREATED_AT + " DESC");

            if (cursor.moveToFirst()) {
                do {
                    BlogPost post = new BlogPost();
                    post.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_POST_ID)));
                    post.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                    post.setContent(cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT)));
                    post.setCreatedAt(cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_AT)));
                    long userId = cursor.getLong(cursor.getColumnIndex(COLUMN_USER_ID));
                    post.setUserId(userId);
                    post.setAuthorName(getUsernameById(userId));
                    post.setImagePath(getPostImage(post.getId()));

                    blogPosts.add(post);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return blogPosts;
    }

    @SuppressLint("Range")
    public List<BlogPost> getUserBlogPosts(long userId) {
        List<BlogPost> blogPosts = new ArrayList<>();
        try {
            String[] columns = {
                    COLUMN_POST_ID,
                    COLUMN_TITLE,
                    COLUMN_CONTENT,
                    COLUMN_CREATED_AT,
                    COLUMN_USER_ID
            };
            String selection = COLUMN_USER_ID + "=?";
            String[] selectionArgs = {String.valueOf(userId)};

            Cursor cursor = database.query(TABLE_POSTS, columns, selection, selectionArgs,
                    null, null, COLUMN_CREATED_AT + " DESC");

            if (cursor.moveToFirst()) {
                do {
                    BlogPost post = new BlogPost();
                    post.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_POST_ID)));
                    post.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                    post.setContent(cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT)));
                    post.setCreatedAt(cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_AT)));
                    post.setUserId(userId);
                    post.setAuthorName(getUsernameById(userId));
                    post.setImagePath(getPostImage(post.getId()));

                    blogPosts.add(post);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return blogPosts;
    }
}