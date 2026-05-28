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

    private lateinit var btnQuestionSuivante: Button

    private lateinit var layoutScore: LinearLayout

    private lateinit var layoutHistorique : LinearLayout

    private lateinit var containerHistorique: LinearLayout

    private lateinit var btnRetourMenuDepuisHist: Button

    private lateinit var btnVoirHistorique: Button

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
        btnQuestionSuivante = findViewById(R.id.btnQuestionSuivante)
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

        layoutHistorique = findViewById(R.id.layoutHistorique)
        containerHistorique = findViewById(R.id.containerHistorique)
        btnRetourMenuDepuisHist = findViewById(R.id.btnRetourMenuDepuisHist)
        btnVoirHistorique = findViewById(R.id.btnVoirHistorique) // Pense à l'ajouter dans ton XML de menu !

// Clic pour ouvrir l'historique depuis le menu
        btnVoirHistorique.setOnClickListener {
            layoutThemes.visibility = View.GONE
            layoutHistorique.visibility = View.VISIBLE
            afficherHistorique()
        }

// Clic pour quitter l'historique
        btnRetourMenuDepuisHist.setOnClickListener {
            layoutHistorique.visibility = View.GONE
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

        // On cache le bouton "Suivant" au début de la question
        btnQuestionSuivante.visibility = View.GONE

        txtProgression.text = "Question ${indexQuestionCourante + 1} / ${listeQuestionsPiochees.size}"

        val questionComplete = listeQuestionsPiochees[indexQuestionCourante]
        txtQuestion.text = questionComplete.question.texte
        val reponses = questionComplete.reponses

        // 1. Configuration initiale des 4 boutons de réponses
        for (i in boutonsReponses.indices) {
            if (i < reponses.size) {
                val reponse = reponses[i]
                val bouton = boutonsReponses[i]

                bouton.visibility = View.VISIBLE
                bouton.text = reponse.texte
                bouton.isEnabled = true
                bouton.setBackgroundColor(Color.parseColor("#A2C4C9")) // Gris neutre par défaut
                bouton.setTextColor(Color.BLACK)

                // 2. Clic sur une des réponses
                bouton.setOnClickListener {
                    // Bloque immédiatement toutes les réponses
                    boutonsReponses.forEach { it.isEnabled = false }

                    // Si la réponse cliquée est correcte
                    if (reponse.correcte) {
                        score++
                        // On colore UNIQUEMENT le bouton cliqué en vert
                        bouton.setBackgroundColor(Color.parseColor("#C1E1C1")) // Vert Pastel
                    } else {
                        // Si la réponse cliquée est fausse : on la colore en rouge
                        bouton.setBackgroundColor(Color.parseColor("#FFC0CB")) // Rouge/Rose Pastel

                        // ET on cherche la bonne réponse dans la liste pour l'allumer en vert
                        for (j in boutonsReponses.indices) {
                            if (j < reponses.size && reponses[j].correcte) {
                                boutonsReponses[j].setBackgroundColor(Color.parseColor("#C1E1C1")) // Vert Pastel
                            }
                        }
                    }

                    // 4. On fait apparaître le bouton "Question suivante" en dessous
                    btnQuestionSuivante.visibility = View.VISIBLE

                    btnQuestionSuivante.setOnClickListener {
                        indexQuestionCourante++
                        afficherQuestion() // Passe à la question d'après
                    }
                }
            } else {
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

            // On récupère le nom du thème joué (via la première question piochée)
            val idThemeJoue = listeQuestionsPiochees.firstOrNull()?.question?.themeId ?: 0
            val nomThemeJoue = quizDao.getAllThemes().find { it.id_theme == idThemeJoue }?.nom_theme ?: "Inconnu"

            val enregistrementScore = Score(
                date_partie = dateStr,
                points_obtenus = score,
                total_questions = listeQuestionsPiochees.size,
                theme_joue = nomThemeJoue // <-- On ajoute le nom du thème ici !
            )
            quizDao.insertScore(enregistrementScore)
        }
    }

    // Ton faux jeu de données de test automatique (à laisser pour amorcer la BDD)
    // Ton jeu de données complet avec 5 thèmes et 10 questions par thème
    private suspend fun remplirBaseDeDonneesSiVide() {
        if (quizDao.getAllThemes().isNotEmpty()) return

        // --- 1. THÈME : CINÉMA ---
        val idCinema = quizDao.insertTheme(Theme(nom_theme = "Cinéma")).toInt()

        val c1 = quizDao.insertQuestion(Question(themeId = idCinema, texte = "Quel film détient le record du plus grand nombre d'Oscars (11) ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = c1.toInt(), texte = "Titanic", correcte = true),
            Reponse(questionId = c1.toInt(), texte = "Avatar", correcte = false),
            Reponse(questionId = c1.toInt(), texte = "Avengers EndGame", correcte = false),
            Reponse(questionId = c1.toInt(), texte = "Le Parrain 2", correcte = false)
        ))
        val c2 = quizDao.insertQuestion(Question(themeId = idCinema, texte = "Qui a réalisé le film Orange Mécanique ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = c2.toInt(), texte = "Christopher Nolan", correcte = false),
            Reponse(questionId = c2.toInt(), texte = "Steven Spielberg", correcte = false),
            Reponse(questionId = c2.toInt(), texte = "Stanley Kubrick", correcte = true),
            Reponse(questionId = c2.toInt(), texte = "Martin Scorsese", correcte = false)
        ))
        val c3 = quizDao.insertQuestion(Question(themeId = idCinema, texte = "En quelle année est sorti le premier film Star Wars au cinéma ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = c3.toInt(), texte = "1977", correcte = true),
            Reponse(questionId = c3.toInt(), texte = "1980", correcte = false),
            Reponse(questionId = c3.toInt(), texte = "1983", correcte = false),
            Reponse(questionId = c3.toInt(), texte = "1975", correcte = false)
        ))
        val c4 = quizDao.insertQuestion(Question(themeId = idCinema, texte = "Quel acteur incarne Stiles dans la série Teen Wolf ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = c4.toInt(), texte = "Tyler Posey", correcte = false),
            Reponse(questionId = c4.toInt(), texte = "Dylan O'Brien", correcte = true),
            Reponse(questionId = c4.toInt(), texte = "Tyler Hoechlin", correcte = false),
            Reponse(questionId = c4.toInt(), texte = "Tom Welling", correcte = false)
        ))
        val c5 = quizDao.insertQuestion(Question(themeId = idCinema, texte = "Dans quel saga trouve-t-on le personnage de Dobby ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = c5.toInt(), texte = "Le Seigneur des Anneaux", correcte = false),
            Reponse(questionId = c5.toInt(), texte = "Hunger Games", correcte = false),
            Reponse(questionId = c5.toInt(), texte = "Twilight", correcte = false),
            Reponse(questionId = c5.toInt(), texte = "Harry Potter", correcte = true)
        ))
        val c6 = quizDao.insertQuestion(Question(themeId = idCinema, texte = "Quelle est la couleur de la pilule que prend Neo dans Matrix pour découvrir la vérité ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = c6.toInt(), texte = "Verte", correcte = false),
            Reponse(questionId = c6.toInt(), texte = "Rouge", correcte = true),
            Reponse(questionId = c6.toInt(), texte = "Bleue", correcte = false),
            Reponse(questionId = c6.toInt(), texte = "Jaune", correcte = false)
        ))
        val c7 = quizDao.insertQuestion(Question(themeId = idCinema, texte = "Dans quel film Brad Pitt et Morgan Freeman enquêtent sur des meurtres liés aux 7 péchés capitaux ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = c7.toInt(), texte = "Prisoners", correcte = false),
            Reponse(questionId = c7.toInt(), texte = "Memories of Murder", correcte = false),
            Reponse(questionId = c7.toInt(), texte = "Se7en", correcte = true),
            Reponse(questionId = c7.toInt(), texte = "Zodiac", correcte = false)
        ))
        val c8 = quizDao.insertQuestion(Question(themeId = idCinema, texte = "Qui joue le personnage de Roman Roy dans la série Succession ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = c8.toInt(), texte = "Kieran Culkin", correcte = true),
            Reponse(questionId = c8.toInt(), texte = "Jeremy Strong", correcte = false),
            Reponse(questionId = c8.toInt(), texte = "Brian Cox", correcte = false),
            Reponse(questionId = c8.toInt(), texte = "Alexander Skarsgård", correcte = false)
        ))
        val c9 = quizDao.insertQuestion(Question(themeId = idCinema, texte = "Quel acteur joue le rôle principal dans 'Forrest Gump' ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = c9.toInt(), texte = "Tom Hanks", correcte = true),
            Reponse(questionId = c9.toInt(), texte = "Leonardo DiCaprio", correcte = false),
            Reponse(questionId = c9.toInt(), texte = "Matt Damon", correcte = false),
            Reponse(questionId = c9.toInt(), texte = "Robert De Niro", correcte = false)
        ))
        val c10 = quizDao.insertQuestion(Question(themeId = idCinema, texte = "En quelle année est sorti le film Good Will Hunting"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = c10.toInt(), texte = "1994", correcte = false),
            Reponse(questionId = c10.toInt(), texte = "1995", correcte = false),
            Reponse(questionId = c10.toInt(), texte = "1996", correcte = false),
            Reponse(questionId = c10.toInt(), texte = "1997", correcte = true)
        ))

        // --- 2. THÈME : FOOTBALL ---
        val idFoot = quizDao.insertTheme(Theme(nom_theme = "Football")).toInt()

        val f1 = quizDao.insertQuestion(Question(themeId = idFoot, texte = "Quel pays a remporté la Coupe du Monde de football en 2022 ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = f1.toInt(), texte = "Argentine", correcte = true),
            Reponse(questionId = f1.toInt(), texte = "France", correcte = false),
            Reponse(questionId = f1.toInt(), texte = "Maroc", correcte = false),
            Reponse(questionId = f1.toInt(), texte = "Brésil", correcte = false)
        ))
        val f2 = quizDao.insertQuestion(Question(themeId = idFoot, texte = "Quel pays a remporté le plus de coupe du monde (5) ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = f2.toInt(), texte = "Argentine", correcte = false),
            Reponse(questionId = f2.toInt(), texte = "Italie", correcte = false),
            Reponse(questionId = f2.toInt(), texte = "Allemagne", correcte = false),
            Reponse(questionId = f2.toInt(), texte = "Brésil", correcte = true)
        ))
        val f3 = quizDao.insertQuestion(Question(themeId = idFoot, texte = "Quel club a remporté le plus de Ligue des Champions (15) ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = f3.toInt(), texte = "AC Milan", correcte = false),
            Reponse(questionId = f3.toInt(), texte = "FC Barcelone", correcte = false),
            Reponse(questionId = f3.toInt(), texte = "Real Madrid", correcte = true),
            Reponse(questionId = f3.toInt(), texte = "Liverpool", correcte = false)
        ))
        val f4 = quizDao.insertQuestion(Question(themeId = idFoot, texte = "Quel club français a atteint les demi-finales de la Conference League cette année ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = f4.toInt(), texte = "RC Strasbourg", correcte = true),
            Reponse(questionId = f4.toInt(), texte = "Olympique de Marseille", correcte = false),
            Reponse(questionId = f4.toInt(), texte = "RC Lens", correcte = false),
            Reponse(questionId = f4.toInt(), texte = "Stade Rennais", correcte = false)
        ))
        val f5 = quizDao.insertQuestion(Question(themeId = idFoot, texte = "Où s'est déroulée la Coupe du Monde en 1998, qui a été remportée par la France ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = f5.toInt(), texte = "Italie", correcte = true),
            Reponse(questionId = f5.toInt(), texte = "France", correcte = false),
            Reponse(questionId = f5.toInt(), texte = "Allemagne", correcte = false),
            Reponse(questionId = f5.toInt(), texte = "Brésil", correcte = false)
        ))
        val f6 = quizDao.insertQuestion(Question(themeId = idFoot, texte = "Quel joueur français a remporté 3 ballons d'or d'affilée ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = f6.toInt(), texte = "Zinedine Zidane", correcte = false),
            Reponse(questionId = f6.toInt(), texte = "Thierry Henry", correcte = false),
            Reponse(questionId = f6.toInt(), texte = "Franck Ribéry", correcte = false) ,
            Reponse(questionId = f6.toInt(), texte = "Michel Platini", correcte = true)
        ))
        val f7 = quizDao.insertQuestion(Question(themeId = idFoot, texte = "Dans quel club a signé Rayan Cherki cet été ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = f7.toInt(), texte = "Real Madrid", correcte = false),
            Reponse(questionId = f7.toInt(), texte = "Manchester City", correcte = true),
            Reponse(questionId = f7.toInt(), texte = "Manchester United", correcte = false),
            Reponse(questionId = f7.toInt(), texte = "PSG", correcte = false)
        ))
        val f8 = quizDao.insertQuestion(Question(themeId = idFoot, texte = "Dans quel club anglais Cristiano Ronaldo s'est-il révélé au niveau mondial ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = f8.toInt(), texte = "Manchester United", correcte = true),
            Reponse(questionId = f8.toInt(), texte = "Chelsea", correcte = false),
            Reponse(questionId = f8.toInt(), texte = "Liverpool", correcte = false),
            Reponse(questionId = f8.toInt(), texte = "Arsenal", correcte = false)
        ))
        val f9 = quizDao.insertQuestion(Question(themeId = idFoot, texte = "Quelle équipe n'a jamais remporté la Ligue des Champions ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = f9.toInt(), texte = "Nottingham Forrest", correcte = false),
            Reponse(questionId = f9.toInt(), texte = "Dortmund", correcte = false),
            Reponse(questionId = f9.toInt(), texte = "Atletico Madrid", correcte = true),
            Reponse(questionId = f9.toInt(), texte = "Aston Villa", correcte = false)
        ))
        val f10 = quizDao.insertQuestion(Question(themeId = idFoot, texte = "Qui a remporté la Coupe du Monde en tant qu'entraîneur et en tant que joueur ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = f10.toInt(), texte = "Didier Deschamps", correcte = true),
            Reponse(questionId = f10.toInt(), texte = "Zinedine Zidane", correcte = false),
            Reponse(questionId = f10.toInt(), texte = "Laurent Blanc", correcte = false),
            Reponse(questionId = f10.toInt(), texte = "Thierry Henry", correcte = false)
        ))

        // --- 3. THÈME : CULTURE GÉNÉRALE ---
        val idCulture = quizDao.insertTheme(Theme(nom_theme = "Culture Générale")).toInt()

        val cu1 = quizDao.insertQuestion(Question(themeId = idCulture, texte = "Qui a peint la célèbre Joconde ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = cu1.toInt(), texte = "Léonard de Vinci", correcte = true),
            Reponse(questionId = cu1.toInt(), texte = "Pablo Picasso", correcte = false),
            Reponse(questionId = cu1.toInt(), texte = "Vincent van Gogh", correcte = false),
            Reponse(questionId = cu1.toInt(), texte = "Claude Monet", correcte = false)
        ))
        val cu2 = quizDao.insertQuestion(Question(themeId = idCulture, texte = "Quelle est la capitale de l'Australie ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = cu2.toInt(), texte = "Canberra", correcte = true),
            Reponse(questionId = cu2.toInt(), texte = "Sydney", correcte = false),
            Reponse(questionId = cu2.toInt(), texte = "Melbourne", correcte = false),
            Reponse(questionId = cu2.toInt(), texte = "Brisbane", correcte = false)
        ))
        val cu3 = quizDao.insertQuestion(Question(themeId = idCulture, texte = "Combien de planètes compte notre système solaire depuis 2006 ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = cu3.toInt(), texte = "8", correcte = true),
            Reponse(questionId = cu3.toInt(), texte = "9", correcte = false),
            Reponse(questionId = cu3.toInt(), texte = "7", correcte = false),
            Reponse(questionId = cu3.toInt(), texte = "10", correcte = false)
        ))
        val cu4 = quizDao.insertQuestion(Question(themeId = idCulture, texte = "Quel gaz compose principalement l'air que nous respirons (environ 78%) ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = cu4.toInt(), texte = "Azote", correcte = true),
            Reponse(questionId = cu4.toInt(), texte = "Oxygène", correcte = false),
            Reponse(questionId = cu4.toInt(), texte = "Dioxyde de carbone", correcte = false),
            Reponse(questionId = cu4.toInt(), texte = "Hydrogène", correcte = false)
        ))
        val cu5 = quizDao.insertQuestion(Question(themeId = idCulture, texte = "Quel est l'océan le plus vaste de la Terre ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = cu5.toInt(), texte = "L'océan Pacifique", correcte = true),
            Reponse(questionId = cu5.toInt(), texte = "L'océan Atlantique", correcte = false),
            Reponse(questionId = cu5.toInt(), texte = "L'océan Indien", correcte = false),
            Reponse(questionId = cu5.toInt(), texte = "L'océan Arctique", correcte = false)
        ))
        val cu6 = quizDao.insertQuestion(Question(themeId = idCulture, texte = "Quel écrivain a rédigé le célèbre roman 'Les Misérables' ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = cu6.toInt(), texte = "Victor Hugo", correcte = true),
            Reponse(questionId = cu6.toInt(), texte = "Émile Zola", correcte = false),
            Reponse(questionId = cu6.toInt(), texte = "Albert Camus", correcte = false),
            Reponse(questionId = cu6.toInt(), texte = "Gustave Flaubert", correcte = false)
        ))
        val cu7 = quizDao.insertQuestion(Question(themeId = idCulture, texte = "En quelle année l'Homme a-t-il marché sur la Lune pour la première fois ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = cu7.toInt(), texte = "1969", correcte = true),
            Reponse(questionId = cu7.toInt(), texte = "1965", correcte = false),
            Reponse(questionId = cu7.toInt(), texte = "1972", correcte = false),
            Reponse(questionId = cu7.toInt(), texte = "1961", correcte = false)
        ))
        val cu8 = quizDao.insertQuestion(Question(themeId = idCulture, texte = "Quel monument parisien a été construit pour l'Exposition Universelle de 1889 ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = cu8.toInt(), texte = "La Tour Eiffel", correcte = true),
            Reponse(questionId = cu8.toInt(), texte = "L'Arc de Triomphe", correcte = false),
            Reponse(questionId = cu8.toInt(), texte = "Le Sacré-Cœur", correcte = false),
            Reponse(questionId = cu8.toInt(), texte = "Le Louvre", correcte = false)
        ))
        val cu9 = quizDao.insertQuestion(Question(themeId = idCulture, texte = "Quel est l'animal terrestre le plus rapide du monde ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = cu9.toInt(), texte = "Le guépard", correcte = true),
            Reponse(questionId = cu9.toInt(), texte = "Le lion", correcte = false),
            Reponse(questionId = cu9.toInt(), texte = "L'autruche", correcte = false),
            Reponse(questionId = cu9.toInt(), texte = "L'antilope", correcte = false)
        ))
        val cu10 = quizDao.insertQuestion(Question(themeId = idCulture, texte = "Combien d'os possède un être humain adulte en moyenne ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = cu10.toInt(), texte = "206", correcte = true),
            Reponse(questionId = cu10.toInt(), texte = "150", correcte = false),
            Reponse(questionId = cu10.toInt(), texte = "300", correcte = false),
            Reponse(questionId = cu10.toInt(), texte = "256", correcte = false)
        ))

        // --- 4. THÈME : GÉOGRAPHIE ---
        val idGeo = quizDao.insertTheme(Theme(nom_theme = "Géographie")).toInt()

        val g1 = quizDao.insertQuestion(Question(themeId = idGeo, texte = "Quel est le plus long fleuve du monde ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = g1.toInt(), texte = "Le Nil", correcte = true),
            Reponse(questionId = g1.toInt(), texte = "L'Amazone", correcte = false),
            Reponse(questionId = g1.toInt(), texte = "Le Mississippi", correcte = false),
            Reponse(questionId = g1.toInt(), texte = "Le Yangzi Jiang", correcte = false)
        ))
        val g2 = quizDao.insertQuestion(Question(themeId = idGeo, texte = "Dans quel pays se trouve le célèbre volcan Mont Fuji ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = g2.toInt(), texte = "Chine", correcte = true),
            Reponse(questionId = g2.toInt(), texte = "Japon", correcte = false),
            Reponse(questionId = g2.toInt(), texte = "Indonésie", correcte = false),
            Reponse(questionId = g2.toInt(), texte = "Philippines", correcte = false)
        ))
        val g3 = quizDao.insertQuestion(Question(themeId = idGeo, texte = "Quel est le pays le plus vaste du monde en superficie ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = g3.toInt(), texte = "Russie", correcte = true),
            Reponse(questionId = g3.toInt(), texte = "Canada", correcte = false),
            Reponse(questionId = g3.toInt(), texte = "États-Unis", correcte = false),
            Reponse(questionId = g3.toInt(), texte = "Chine", correcte = false)
        ))
        val g4 = quizDao.insertQuestion(Question(themeId = idGeo, texte = "Quelle chaîne de montagnes abrite le Mont Everest ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = g4.toInt(), texte = "L'Himalaya", correcte = true),
            Reponse(questionId = g4.toInt(), texte = "Les Andes", correcte = false),
            Reponse(questionId = g4.toInt(), texte = "Les Alpes", correcte = false),
            Reponse(questionId = g4.toInt(), texte = "Les Rocheuses", correcte = false)
        ))
        val g5 = quizDao.insertQuestion(Question(themeId = idGeo, texte = "Quelle est la capitale du Canada ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = g5.toInt(), texte = "Ottawa", correcte = true),
            Reponse(questionId = g5.toInt(), texte = "Toronto", correcte = false),
            Reponse(questionId = g5.toInt(), texte = "Montréal", correcte = false),
            Reponse(questionId = g5.toInt(), texte = "Vancouver", correcte = false)
        ))
        val g6 = quizDao.insertQuestion(Question(themeId = idGeo, texte = "Quel détroit sépare l'Espagne du Maroc ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = g6.toInt(), texte = "Détroit de Gibraltar", correcte = true),
            Reponse(questionId = g6.toInt(), texte = "Détroit de Magellan", correcte = false),
            Reponse(questionId = g6.toInt(), texte = "Détroit de Béring", correcte = false),
            Reponse(questionId = g6.toInt(), texte = "Détroit du Bosphore", correcte = false)
        ))
        val g7 = quizDao.insertQuestion(Question(themeId = idGeo, texte = "Sur quel continent se trouve le désert du Sahara ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = g7.toInt(), texte = "Afrique", correcte = true),
            Reponse(questionId = g7.toInt(), texte = "Asie", correcte = false),
            Reponse(questionId = g7.toInt(), texte = "Australie", correcte = false),
            Reponse(questionId = g7.toInt(), texte = "Amérique du Sud", correcte = false)
        ))
        val g8 = quizDao.insertQuestion(Question(themeId = idGeo, texte = "Combien d'États composent les États-Unis d'Amérique ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = g8.toInt(), texte = "50", correcte = true),
            Reponse(questionId = g8.toInt(), texte = "52", correcte = false),
            Reponse(questionId = g8.toInt(), texte = "48", correcte = false),
            Reponse(questionId = g8.toInt(), texte = "51", correcte = false)
        ))
        val g9 = quizDao.insertQuestion(Question(themeId = idGeo, texte = "Quelle est la capitale de l'Italie ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = g9.toInt(), texte = "Rome", correcte = true),
            Reponse(questionId = g9.toInt(), texte = "Milan", correcte = false),
            Reponse(questionId = g9.toInt(), texte = "Venise", correcte = false),
            Reponse(questionId = g9.toInt(), texte = "Florence", correcte = false)
        ))
        val g10 = quizDao.insertQuestion(Question(themeId = idGeo, texte = "Quelle mer borde le sud de la France ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = g10.toInt(), texte = "La mer Méditerranée", correcte = true),
            Reponse(questionId = g10.toInt(), texte = "La mer Noire", correcte = false),
            Reponse(questionId = g10.toInt(), texte = "La mer Rouge", correcte = false),
            Reponse(questionId = g10.toInt(), texte = "La mer Morte", correcte = false)
        ))

        // --- 5. THÈME : INFORMATIQUE ---
        val idInfo = quizDao.insertTheme(Theme(nom_theme = "Informatique")).toInt()

        val i1 = quizDao.insertQuestion(Question(themeId = idInfo, texte = "Que signifie le sigle HTML ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = i1.toInt(), texte = "HyperText Markup Language", correcte = true),
            Reponse(questionId = i1.toInt(), texte = "HighText Machine Language", correcte = false),
            Reponse(questionId = i1.toInt(), texte = "Hyperlink Text Multi Language", correcte = false),
            Reponse(questionId = i1.toInt(), texte = "Home Tool Markup Language", correcte = false)
        ))
        val i2 = quizDao.insertQuestion(Question(themeId = idInfo, texte = "Quel langage est officiellement recommandé par Google pour Android aujourd'hui ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = i2.toInt(), texte = "Kotlin", correcte = true),
            Reponse(questionId = i2.toInt(), texte = "Java", correcte = false),
            Reponse(questionId = i2.toInt(), texte = "Python", correcte = false),
            Reponse(questionId = i2.toInt(), texte = "C++", correcte = false)
        ))
        val i3 = quizDao.insertQuestion(Question(themeId = idInfo, texte = "Quel composant d'un PC est considéré comme son 'cerveau' ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = i3.toInt(), texte = "Le processeur (CPU)", correcte = true),
            Reponse(questionId = i3.toInt(), texte = "La carte graphique (GPU)", correcte = false),
            Reponse(questionId = i3.toInt(), texte = "La mémoire vive (RAM)", correcte = false),
            Reponse(questionId = i3.toInt(), texte = "Le disque dur (SSD)", correcte = false)
        ))
        val i4 = quizDao.insertQuestion(Question(themeId = idInfo, texte = "Dans le système binaire, de quels chiffres se compose le code ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = i4.toInt(), texte = "0 et 1", correcte = true),
            Reponse(questionId = i4.toInt(), texte = "1 et 2", correcte = false),
            Reponse(questionId = i4.toInt(), texte = "0 et 9", correcte = false),
            Reponse(questionId = i4.toInt(), texte = "-1 et 1", correcte = false)
        ))
        val i5 = quizDao.insertQuestion(Question(themeId = idInfo, texte = "Que signifie l'extension '.ip' d'une adresse internet ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = i5.toInt(), texte = "Internet Protocol", correcte = true),
            Reponse(questionId = i5.toInt(), texte = "Internal Process", correcte = false),
            Reponse(questionId = i5.toInt(), texte = "Instant Connection", correcte = false),
            Reponse(questionId = i5.toInt(), texte = "Internet Provider", correcte = false)
        ))
        val i6 = quizDao.insertQuestion(Question(themeId = idInfo, texte = "Qui est le principal cofondateur de la société Microsoft ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = i6.toInt(), texte = "Bill Gates", correcte = true),
            Reponse(questionId = i6.toInt(), texte = "Steve Jobs", correcte = false),
            Reponse(questionId = i6.toInt(), texte = "Mark Zuckerberg", correcte = false),
            Reponse(questionId = i6.toInt(), texte = "Jeff Bezos", correcte = false)
        ))
        val i7 = quizDao.insertQuestion(Question(themeId = idInfo, texte = "Quelle mémoire s'efface instantanément lorsque le PC s'éteint ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = i7.toInt(), texte = "La mémoire RAM", correcte = true),
            Reponse(questionId = i7.toInt(), texte = "Le disque SSD", correcte = false),
            Reponse(questionId = i7.toInt(), texte = "La clé USB", correcte = false),
            Reponse(questionId = i7.toInt(), texte = "La mémoire ROM", correcte = false)
        ))
        val i8 = quizDao.insertQuestion(Question(themeId = idInfo, texte = "Quel animal sert de logo officiel au système d'exploitation Linux ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = i8.toInt(), texte = "Un manchot (Tux)", correcte = true),
            Reponse(questionId = i8.toInt(), texte = "Un renard", correcte = false),
            Reponse(questionId = i8.toInt(), texte = "Un dauphin", correcte = false),
            Reponse(questionId = i8.toInt(), texte = "Un gnou", correcte = false)
        ))
        val i9 = quizDao.insertQuestion(Question(themeId = idInfo, texte = "Quel protocole sécurisé utilise un petit cadenas vert dans la barre d'adresse (URL) ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = i9.toInt(), texte = "HTTPS", correcte = true),
            Reponse(questionId = i9.toInt(), texte = "HTTP", correcte = false),
            Reponse(questionId = i9.toInt(), texte = "FTP", correcte = false),
            Reponse(questionId = i9.toInt(), texte = "SMTP", correcte = false)
        ))
        val i10 = quizDao.insertQuestion(Question(themeId = idInfo, texte = "Combien d'octets y a-t-il exactement dans un Kilooctet (Ko) ?"))
        quizDao.insertReponses(listOf(
            Reponse(questionId = i10.toInt(), texte = "1024", correcte = true),
            Reponse(questionId = i10.toInt(), texte = "1000", correcte = false),
            Reponse(questionId = i10.toInt(), texte = "512", correcte = false),
            Reponse(questionId = i10.toInt(), texte = "2048", correcte = false)
        ))
    }

    private fun afficherHistorique() {
        CoroutineScope(Dispatchers.IO).launch {
            // 1. Récupération des scores dans la BDD SQLite
            val listeScores = quizDao.getAllScores()

            withContext(Dispatchers.Main) {
                containerHistorique.removeAllViews()

                if (listeScores.isEmpty()) {
                    val txtVide = TextView(this@MainActivity).apply {
                        text = "Aucune partie enregistrée pour le moment."
                        textSize = 16f
                        gravity = android.view.Gravity.CENTER
                    }
                    containerHistorique.addView(txtVide)
                    return@withContext
                }


                val mappingCouleurs = mapOf(
                    "Cinéma" to "#FFD1DC",
                    "Football" to "#C1E1C1",
                    "Culture Générale" to "#FFFACD",
                    "Géographie" to "#D6CADD",
                    "Informatique" to "#B0E0E6"
                )

                // 2. Création dynamique d'une ligne pour chaque score sur 2 lignes avec couleur
                for (scoreEnregistre in listeScores) {
                    val txtScore = TextView(this@MainActivity).apply {

                        // Ligne 1 : Le Thème
                        // Ligne 2 (\n) : La date et le score obtenu
                        text = "🏷️ Thème : ${scoreEnregistre.theme_joue}\n📅 ${scoreEnregistre.date_partie}  |  🏆 Score : ${scoreEnregistre.points_obtenus} / ${scoreEnregistre.total_questions}"

                        textSize = 16f
                        setPadding(16, 16, 16, 16) // Un peu d'espace pour respirer

                        // On récupère la couleur associée au thème, ou du gris par défaut si inconnu
                        val codeCouleur = mappingCouleurs[scoreEnregistre.theme_joue] ?: "#CCCCCC"

                        // On applique la couleur sur le fond du texte (comme un petit badge)
                        setBackgroundColor(Color.parseColor(codeCouleur))
                        setTextColor(Color.BLACK) // Texte écrit en noir pour rester très lisible

                        // On ajoute une petite marge sous chaque score pour les séparer proprement
                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply { setMargins(0, 0, 0, 16) }
                        layoutParams = params
                    }
                    containerHistorique.addView(txtScore)
                }
            }
        }
    }
}