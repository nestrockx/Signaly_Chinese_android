package com.wegielek.signalychinese.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase

@Database(
    entities = [Dictionary::class, Radicals::class, History::class, FlashCards::class, Sentences::class],
    version = 1
)
abstract class AppDictionaryDatabase : RoomDatabase() {
    abstract fun dictionaryDao(): DictionaryDao
    abstract fun radicalDao(): RadicalsDao
    abstract fun historyDao(): HistoryDao
    abstract fun flashCardsDao(): FlashCardsDao
    abstract fun sentencesDao(): SentencesDao

    companion object {
        private val LOCK = Any()
        private const val DATABASE_NAME = "dictionary.db"
        private var mInstance: AppDictionaryDatabase? = null
        fun getInstance(context: Context): AppDictionaryDatabase? {
            if (mInstance == null) {
                synchronized(LOCK) {
                    mInstance = databaseBuilder(
                        context.applicationContext,
                        AppDictionaryDatabase::class.java,
                        DATABASE_NAME
                    ).createFromAsset("dictionarylp.db").build()
                }
            }
            return mInstance
        }
    }
}