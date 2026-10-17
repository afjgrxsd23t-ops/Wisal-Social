package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        PostEntity::class,
        CommentEntity::class,
        StoryEntity::class,
        ConversationEntity::class,
        ChatMessageEntity::class,
        NotificationEntity::class,
        UserEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class WisalDatabase : RoomDatabase() {
    abstract fun wisalDao(): WisalDao

    companion object {
        @Volatile
        private var INSTANCE: WisalDatabase? = null

        fun getDatabase(context: Context): WisalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WisalDatabase::class.java,
                    "wisal_database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
