package com.example.quizz

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class QuizDaoTest {

    private lateinit var db: com.example.quizz.data.AppDatabase
    private lateinit var quizDao: com.example.quizz.data.QuizDao

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, com.example.quizz.data.AppDatabase::class.java).build()
        quizDao = db.quizDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun testQuizDao_FonctionnalitesCompletes() = runBlocking {

        // TEST : Insertion et Lecture des Thèmes
        val theme = com.exemple.quizz.Theme(id_theme = 1, nom_theme = "Géographie", nb_parties_jouees = 0)
        quizDao.insertTheme(theme)

        val listeThemes = quizDao.getAllThemes()
        assertEquals(1, listeThemes.size)
        assertEquals("Géographie", listeThemes[0].nom_theme)

        //  TEST : Update - Incrémentation du compteur
        quizDao.incrementerCompteurTheme(1)
        val themeMisAJour = quizDao.getAllThemes()[0]
        assertEquals(1, themeMisAJour.nb_parties_jouees)

        //  TEST : Structure QuestionComplete (Jointure)
        val question = com.exemple.quizz.Question(id_question = 5, themeId = 1, texte = "Quelle est la capitale de l'Italie ?")
        quizDao.insertQuestion(question)

        val rep1 = com.exemple.quizz.Reponse(id_reponse = 10, questionId = 5, texte = "Rome", correcte = true)
        val rep2 = com.exemple.quizz.Reponse(id_reponse = 11, questionId = 5, texte = "Milan", correcte = false)
        quizDao.insertReponses(listOf(rep1, rep2))

        val questionsCompletes = quizDao.getQuestionsByTheme(1)
        assertEquals(1, questionsCompletes.size)
        assertEquals("Quelle est la capitale de l'Italie ?", questionsCompletes[0].question.texte)
        assertEquals(2, questionsCompletes[0].reponses.size)

        //TEST : Gestion des Scores (Create & Delete)
        val score1 = com.exemple.quizz.Score(id_score = 1, date_partie = "30/05/2026", points_obtenus = 7, total_questions = 10, theme_joue = "Géographie")
        quizDao.insertScore(score1)

        var listeScores = quizDao.getAllScores()
        assertEquals(1, listeScores.size)

        quizDao.deleteScore(score1)
        listeScores = quizDao.getAllScores()
        assertTrue(listeScores.isEmpty())

        // TEST : Limite des 20 derniers scores maximum
        // On insère volontairement 25 scores à la suite
        for (i in 1..25) {
            val scoreBoucle = com.exemple.quizz.Score(
                id_score = i,
                date_partie = "30/05/2026",
                points_obtenus = 5,
                total_questions = 10,
                theme_joue = "Géographie"
            )
            quizDao.insertScore(scoreBoucle)
        }

        // On appelle ta fonction de nettoyage personnalisée
        quizDao.conserverUniquementLes20DerniersScores()

        // On vérifie qu'il ne reste bien que 20 lignes en base de données
        val scoresApresNettoyage = quizDao.getAllScores()
        assertEquals(20, scoresApresNettoyage.size)

        // Comme c'est trié par id_score DESC, le premier doit être l'ID 25 (le plus récent)
        assertEquals(25, scoresApresNettoyage[0].id_score)
    }
}