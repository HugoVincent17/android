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
class QuestionCompleteTest {


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
    fun testQuestionComplete_StructureEtJointureAutomatique() = runBlocking {
        //  insertion du Thème (Package com.exemple.quizz avec un 'e')
        val themeTest = com.exemple.quizz.Theme(
            id_theme = 1,
            nom_theme = "Cinéma",
            nb_parties_jouees = 0
        )
        quizDao.insertTheme(themeTest)

        //  Insertion de la Question (Package com.exemple.quizz avec un 'e')
        val questionTest = com.exemple.quizz.Question(
            id_question = 42,
            themeId = 1,
            texte = "Qui a réalisé le film Inception ?"
        )
        quizDao.insertQuestion(questionTest)

        // Insertion des Réponses (Package com.exemple.quizz avec un 'e')
        val rep1 = com.exemple.quizz.Reponse(id_reponse = 1, questionId = 42, texte = "Christopher Nolan", correcte = true)
        val rep2 = com.exemple.quizz.Reponse(id_reponse = 2, questionId = 42, texte = "Steven Spielberg", correcte = false)
        val rep3 = com.exemple.quizz.Reponse(id_reponse = 3, questionId = 42, texte = "Quentin Tarantino", correcte = false)

        quizDao.insertReponses(listOf(rep1, rep2, rep3))

        //  ACTION : On récupère l'objet QuestionComplete via ton DAO
        val resultat: List<com.example.quizz.data.QuestionComplete> = quizDao.getQuestionsByTheme(1)

        //  ASSERTIONS : On valide à 100% l'objet QuestionComplete
        assertNotNull("La liste renvoyée par le DAO ne doit pas être nulle", resultat)
        assertTrue("La liste doit contenir la question insérée", resultat.isNotEmpty())

        val oIdQuestionComplete = resultat[0]

        // Validation du @Embedded (La Question)
        assertEquals(42, oIdQuestionComplete.question.id_question)
        assertEquals("Qui a réalisé le film Inception ?", oIdQuestionComplete.question.texte)

        // Validation du @Relation (La liste de réponses associée)
        val reponsesAssociees = oIdQuestionComplete.reponses
        assertNotNull("Room doit initialiser la liste des réponses", reponsesAssociees)
        assertEquals("Room doit lier automatiquement les 3 réponses", 3, reponsesAssociees.size)

        // Validation des valeurs à l'intérieur
        assertEquals("Christopher Nolan", reponsesAssociees[0].texte)
        assertTrue(reponsesAssociees[0].correcte)
    }
}