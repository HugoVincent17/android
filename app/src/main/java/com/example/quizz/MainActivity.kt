package com.example.quizz // Laisse ton package d'origine ici

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.quizz.R
import com.exemple.quizz.AppDatabase
import com.exemple.quizz.Question
import com.exemple.quizz.QuestionComplete
import com.exemple.quizz.QuizDao
import com.exemple.quizz.Reponse
import com.exemple.quizz.Score
import com.exemple.quizz.Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    // Variables globales du jeu
    private lateinit var db: AppDatabase
    private lateinit var quizDao: QuizDao
    private var listeQuestionsPiochees = listOf<QuestionComplete>()
    private var indexQuestionCourante = 0
    private var score = 0

    // Déclaration des éléments graphiques (Vues)
    private lateinit var layoutThemes: LinearLayout
    private lateinit var containerBoutonsThemes: LinearLayout
    private lateinit var layoutJeu: LinearLayout
    private lateinit var layoutScore: LinearLayout

    private lateinit var txtProgression: TextView
    private lateinit var txtQuestion: TextView
    private lateinit var boutonsReponses: List<Button>
    private lateinit var txtScoreFinal: TextView
    private lateinit var btnRetourMenu: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Liaison du code avec le design XML
        layoutThemes = findViewById(R.id.layoutThemes)
        containerBoutonsThemes = findViewById(R.id.containerBoutonsThemes)
        layoutJeu = findViewById(R.id.layoutJeu)
        layoutScore = findViewById(R.id.layoutScore)

        txtProgression = findViewById(R.id.txtProgression)
        txtQuestion = findViewById(R.id.txtQuestion)
        txtScoreFinal = findViewById(R.id.txtScoreFinal)
        btnRetourMenu = findViewById(R.id.btnRetourMenu)

        boutonsReponses = listOf(
            findViewById(R.id.btnReponse1),
            findViewById(R.id.btnReponse2),
            findViewById(R.id.btnReponse3),
            findViewById(R.id.btnReponse4)
        )

        // 2. Initialisation SQLite Room
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "quiz-sqlite.db").build()
        quizDao = db.quizDao()

        // 3. Lancement de l'application en tâche de fond
        CoroutineScope(Dispatchers.IO).launch {
            // Remplir la base si c'est le premier lancement (méthode vue ensemble juste avant)
            remplirBaseDeDonneesSiVide()

            // Charger les thèmes depuis la BDD et les afficher
            val listeThemes = quizDao.getAllThemes()

            withContext(Dispatchers.Main) {
                afficherListeThemes(listeThemes)
            }
        }

        // Bouton pour rejouer à la fin
        btnRetourMenu.setOnClickListener {
            layoutScore.visibility = View.GONE
            layoutThemes.visibility = View.VISIBLE
        }
    }

    // Affiche les thèmes sous forme de boutons colorés
    private fun afficherListeThemes(themes: List<Theme>) {
        containerBoutonsThemes.removeAllViews()

        // Liste de couleurs pastel à alterner
        val couleurs = listOf("#FFD1DC", "#C1E1C1", "#FFFACD", "#D6CADD", "#B0E0E6")

        for ((index, theme) in themes.withIndex()) {
            val btn = Button(this).apply {
                text = theme.nom_theme
                textSize = 18f
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 0, 0, 16) }

                // Attribution d'une couleur de fond différente à chaque bouton
                setBackgroundColor(Color.parseColor(couleurs[index % couleurs.size]))
                setTextColor(Color.BLACK) // Pour que ce soit bien lisible sur le pastel

                setOnClickListener {
                    lancerQuizPourTheme(theme.id_theme)
                }
            }
            containerBoutonsThemes.addView(btn)
        }
    }

    // Pioche 10 questions et lance l'affichage du jeu
    private fun lancerQuizPourTheme(idTheme: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            // Récupère toutes les questions de ce thème
            val toutesLesQuestions = quizDao.getQuestionsByTheme(idTheme)

            // LA MAGIE KOTLIN : .shuffled() mélange les questions, .take(10) prend les 10 premières
            listeQuestionsPiochees = toutesLesQuestions.shuffled().take(10)

            indexQuestionCourante = 0
            score = 0

            withContext(Dispatchers.Main) {
                layoutThemes.visibility = View.GONE
                layoutJeu.visibility = View.VISIBLE
                afficherQuestion()
            }
        }
    }

    // Met à jour l'écran avec la question courante et ses 4 réponses
    private fun afficherQuestion() {
        if (indexQuestionCourante >= listeQuestionsPiochees.size) {
            terminerPartie()
            return
        }

        txtProgression.text = "Question ${indexQuestionCourante + 1} / ${listeQuestionsPiochees.size}"

        val questionComplete = listeQuestionsPiochees[indexQuestionCourante]
        txtQuestion.text = questionComplete.question.texte

        // On prend les réponses liées à la question
        val reponses = questionComplete.reponses

        // On associe chaque réponse à un de nos 4 boutons
        for (i in boutonsReponses.indices) {
            if (i < reponses.size) {
                val reponse = reponses[i]
                boutonsReponses[i].visibility = View.VISIBLE
                boutonsReponses[i].text = reponse.texte

                // Au clic, on vérifie si c'est la bonne réponse
                boutonsReponses[i].setOnClickListener {
                    if (reponse.correcte) {
                        score++ // On gère le score directement dans le code !
                    }
                    // On passe à la question suivante
                    indexQuestionCourante++
                    afficherQuestion()
                }
            } else {
                // Au cas où une question aurait moins de 4 réponses (sécurité)
                boutonsReponses[i].visibility = View.GONE
            }
        }
    }

    // Enregistre le score en SQLite et bascule sur l'écran final
    private fun terminerPartie() {
        layoutJeu.visibility = View.GONE
        layoutScore.visibility = View.VISIBLE
        txtScoreFinal.text = "Votre score : $score / ${listeQuestionsPiochees.size}"

        // Action 'CREATE' du CRUD : Sauvegarde du score en base de données
        CoroutineScope(Dispatchers.IO).launch {
            val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            val enregistrementScore = Score(
                date_partie = dateStr,
                points_obtenus = score,
                total_questions = listeQuestionsPiochees.size
            )
            quizDao.insertScore(enregistrementScore)
        }
    }

    // Ton faux jeu de données de test automatique (à laisser pour amorcer la BDD)
    private suspend fun remplirBaseDeDonneesSiVide() {
        if (quizDao.getAllThemes().isNotEmpty()) return

        val idHistoire = quizDao.insertTheme(Theme(nom_theme = "Histoire")).toInt()
        val idTech = quizDao.insertTheme(Theme(nom_theme = "Technologie")).toInt()

        // Histoire Q1
        val q1 = quizDao.insertQuestion(
            Question(
                themeId = idHistoire,
                texte = "En quelle année a eu lieu la Révolution Française ?"
            )
        )
        quizDao.insertReponses(listOf(
            Reponse(questionId = q1.toInt(), texte = "1789", correcte = true),
            Reponse(questionId = q1.toInt(), texte = "1492", correcte = false),
            Reponse(questionId = q1.toInt(), texte = "1914", correcte = false),
            Reponse(questionId = q1.toInt(), texte = "1515", correcte = false)
        ))

        // Ajoute d'autres fausses questions ici (au moins 10 par thèmes) si tu veux tester la pioche complète !
        val q2 = quizDao.insertQuestion(Question(themeId = idTech, texte = "Quel langage est officiel pour Android moderne ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = q2.toInt(), texte = "Kotlin", correcte = true),
            Reponse(questionId = q2.toInt(), texte = "Java", correcte = false),
            Reponse(questionId = q2.toInt(), texte = "Python", correcte = false),
            Reponse(questionId = q2.toInt(), texte = "Swift", correcte = false)
        ))
    }
}