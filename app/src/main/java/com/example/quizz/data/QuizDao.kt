package com.example.quizz.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.exemple.quizz.Question
import com.exemple.quizz.Reponse
import com.exemple.quizz.Score
import com.exemple.quizz.Theme

@Dao
interface QuizDao {

    // --- R (Read) : Pour afficher les thèmes, le jeu et l'historique des scores ---
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

    // 20 scores maximum dans l'historique
    @Query("DELETE FROM scores WHERE id_score NOT IN (SELECT id_score FROM scores ORDER BY id_score DESC LIMIT 20)")
    suspend fun conserverUniquementLes20DerniersScores()

    // --- Utilitaires pour insérer le jeu de données au début ---
    @Insert
    suspend fun insertTheme(theme: Theme): Long
    @Insert
    suspend fun insertQuestion(question: Question): Long
    @Insert
    suspend fun insertReponses(reponses: List<Reponse>)

    // --- U (Update) : Mettre à jour le compteur du thème ---
    @Query("UPDATE themes SET nb_parties_jouees = nb_parties_jouees + 1 WHERE id_theme = :idTheme")
    suspend fun incrementerCompteurTheme(idTheme: Int)
}