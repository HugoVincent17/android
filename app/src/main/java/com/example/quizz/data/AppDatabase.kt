package com.example.quizz.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.exemple.quizz.Question
import com.exemple.quizz.Reponse
import com.exemple.quizz.Score
import com.exemple.quizz.Theme

@Database(entities = [Theme::class, Question::class, Reponse::class, Score::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun quizDao(): QuizDao
}