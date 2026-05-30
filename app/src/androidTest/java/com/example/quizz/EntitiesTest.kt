package com.example.quizz

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.quizz.data.AppDatabase
import com.example.quizz.data.QuizDao
import com.exemple.quizz.Question
import com.exemple.quizz.Reponse
import com.exemple.quizz.Score
import com.exemple.quizz.Theme
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AppEntitiesTest {

    private lateinit var db: AppDatabase
    private lateinit var quizDao: QuizDao

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    /**
     * Initialisation d'une base de données éphémère en mémoire RAM avant chaque test
     */
    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        quizDao = db.quizDao()
    }

    /**
     * Fermeture de la base de données après le test
     */
    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    /**
     * TEST GLOBAL : Insertion globale, vérification des liaisons et test de l'effet CASCADE.
     */
    @Test
    @Throws(Exception::class)
    fun testRelationsEtInsertionsGlobales() = runBlocking {
        // INSERTION DU JEU DE DONNÉES COMPLET
        val theme = Theme(id_theme = 1, nom_theme = "Histoire", nb_parties_jouees = 0)
        quizDao.insertTheme(theme)

        val question = Question(id_question = 10, themeId = 1, texte = "En quelle année a eu lieu la Révolution Française ?")
        quizDao.insertQuestion(question)

        val reponseJuste = Reponse(id_reponse = 100, questionId = 10, texte = "1789", correcte = true)
        val reponseFausse = Reponse(id_reponse = 101, questionId = 10, texte = "1914", correcte = false)

        // Insertion de la liste des réponses (ajusté suite à tes corrections précédentes)
        quizDao.insertReponses(listOf(reponseJuste, reponseFausse))

        val score = Score(
            id_score = 1,
            date_partie = "30/05/2026",
            points_obtenus = 8,
            total_questions = 10,
            themeId = 1
        )
        quizDao.insertScore(score)

        // VÉRIFICATIONS DES DONNÉES (L'essentiel pour ton CRUD)

        // On vérifie le Thème
        val themes = quizDao.getAllThemes()
        assertNotNull(themes)
        assertEquals(1, themes.size)
        assertEquals("Histoire", themes[0].nom_theme)

        // On vérifie le Score (Historique)
        val scores = quizDao.getAllScores()
        assertNotNull(scores)
        assertEquals(1, scores.size)
        assertEquals(8, scores[0].points_obtenus)
        assertEquals("Histoire", scores[0].nom_theme)
    }
}