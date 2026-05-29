package com.example.quizz.data

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.exemple.quizz.Question
import com.exemple.quizz.Theme
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {

    private lateinit var db: AppDatabase
    private lateinit var quizDao: QuizDao

    // Cette règle force Room à exécuter toutes les opérations instantanément sur le même thread pour le test
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    /**
     * S'EXÉCUTE AVANT CHAQUE TEST : On crée une base de données temporaire en mémoire RAM
     */
    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // "inMemoryDatabaseBuilder" crée une BDD éphémère qui s'efface dès que le test est fini
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        quizDao = db.quizDao()
    }

    /**
     * S'EXÉCUTE APRÈS CHAQUE TEST : On ferme la base de données pour libérer la mémoire
     */
    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    /**
     * TEST : Insertion d'un thème et d'une question, puis vérification de la lecture (CRUD)
     * On utilise "runBlocking" car les fonctions du DAO sont des fonctions "suspend" (Coroutines)
     */
    @Test
    @Throws(Exception::class)
    fun insertAndReadQuizData() = runBlocking {
        //  ACTION : On insère un thème de test
        val themeTest = Theme(id_theme = 1, nom_theme = "Cinéma", nb_parties_jouees = 0)
        quizDao.insertTheme(themeTest)

        //  ACTION : On insère une question liée à ce thème (clé étrangère themeId = 1)
        val questionTest = Question(id_question = 1, themeId = 1, texte = "Quel film détient le record d'Oscars ?")
        quizDao.insertQuestion(questionTest)

        //  LECTURE : On récupère les thèmes depuis la BDD
        val themesEnBDD = quizDao.getAllThemes()

        //  VÉRIFICATION (Assertion) : On vérifie que la BDD n'est pas vide et contient bien notre thème
        assertNotNull(themesEnBDD)
        assertEquals(1, themesEnBDD.size)
        assertEquals("Cinéma", themesEnBDD[0].nom_theme)

        //  LECTURE DE LA JOINTURE : On récupère les questions liées au thème 1
        val questionsDuTheme = quizDao.getQuestionsByTheme(1)

        //  VÉRIFICATION : Est-ce que la question lue correspond à celle insérée ?
        assertEquals(1, questionsDuTheme.size)
        assertEquals("Quel film détient le record d'Oscars ?", questionsDuTheme[0].question.texte)
    }
}