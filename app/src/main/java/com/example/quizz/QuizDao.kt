package com.exemple.quizz // /!\ Remplace par ton package

import androidx.room.*

@Dao
interface QuizDao {

    // --- R (Read) : Pour afficher les thèmes, le jeu et l'historique ---
    @Query("SELECT * FROM themes")
    suspend fun getAllThemes(): List<Theme>

    @Transaction
    @Query("SELECT * FROM questions WHERE themeId = :idTheme")
    suspend fun getQuestionsByTheme(idTheme: Int): List<QuestionComplete>

    @Query("SELECT * FROM scores ORDER BY id_score DESC")
    suspend fun getAllScores(): List<Score>

    // --- C (Create) : Enregistrer le score à la fin ---
    @Insert
    suspend fun insertScore(score: Score)

    // --- D (Delete) : Supprimer un score de l'historique ---
    @Delete
    suspend fun deleteScore(score: Score)

    // --- Utilitaires pour insérer ton faux jeu de données au début ---
    @Insert suspend fun insertTheme(theme: Theme): Long
    @Insert suspend fun insertQuestion(question: Question): Long
    @Insert suspend fun insertReponses(reponses: List<Reponse>)
}