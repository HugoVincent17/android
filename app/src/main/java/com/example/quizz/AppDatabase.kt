package com.exemple.quizz // /!\ Remplace par ton package

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Theme::class, Question::class, Reponse::class, Score::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun quizDao(): QuizDao
}