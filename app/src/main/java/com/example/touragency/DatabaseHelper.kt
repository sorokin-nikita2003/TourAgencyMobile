package com.example.touragency

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // Создание таблицы для хранения данных пользователя
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS your_table_name (id INTEGER PRIMARY KEY, name TEXT, age INTEGER)")
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT NOT NULL,
                $COLUMN_PASSWORD TEXT NOT NULL,
                $COLUMN_ROLES TEXT NOT NULL
            )
        """
        db.execSQL(createTableQuery)
    }

    // Обновление таблицы, если версия базы данных изменится
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Метод для добавления или обновления данных о пользователе
    fun saveUser(username: String, password: String, roles: String) {
        val db = writableDatabase
        val insertQuery = """
            INSERT OR REPLACE INTO $TABLE_NAME ($COLUMN_USERNAME, $COLUMN_PASSWORD, $COLUMN_ROLES)
            VALUES ('$username', '$password', '$roles')
        """
        db.execSQL(insertQuery)
        db.close()
    }

    // Метод для получения текущего пользователя
    @SuppressLint("Range")
    fun getCurrentUser(): User? {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME LIMIT 1"
        val cursor = db.rawQuery(query, null)

        return if (cursor.moveToFirst()) {
            val username = cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME))
            val password = cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD))
            val roles = cursor.getString(cursor.getColumnIndex(COLUMN_ROLES))
            cursor.close()
            User(username, password, roles)
        } else {
            cursor.close()
            null
        }
    }

    // Метод для удаления данных о пользователе
    fun deleteUser() {
        val db = writableDatabase
        db.execSQL("DELETE FROM $TABLE_NAME")
        db.close()
    }

    companion object {
        private const val DATABASE_NAME = "user_database.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "current_user"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_ROLES = "roles"
    }
}

// Класс для представления данных о пользователе
data class User(val username: String, val password: String, val roles: String)