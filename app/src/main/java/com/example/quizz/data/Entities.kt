package com.exemple.quizz

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

//  TABLE THEME
@Entity(tableName = "themes")
data class Theme(
    @PrimaryKey(autoGenerate = true) val id_theme: Int = 0,
    val nom_theme: String,
    val nb_parties_jouees: Int = 0
)

//  TABLE QUESTION (Relation "Appartenir" via clé étrangère vers le Thème)
@Entity(
    tableName = "questions",
    foreignKeys = [ForeignKey(
        entity = Theme::class,
        parentColumns = ["id_theme"],
        childColumns = ["themeId"],
        onDelete = ForeignKey.CASCADE // Si on supprime un thème, les questions partent avec
    )]
)
data class Question(
    @PrimaryKey(autoGenerate = true) val id_question: Int = 0,
    val themeId: Int, // Clé étrangère qui matérialise la relation "Appartenir"
    val texte: String
)

//  TABLE REPONSE (Relation "Proposer" via clé étrangère vers la Question)
@Entity(
    tableName = "reponses",
    foreignKeys = [ForeignKey(
        entity = Question::class,
        parentColumns = ["id_question"],
        childColumns = ["questionId"],
        onDelete = ForeignKey.CASCADE // Si on supprime une question, ses réponses partent avec
    )]
)
data class Reponse(
    @PrimaryKey(autoGenerate = true) val id_reponse: Int = 0,
    val questionId: Int, // Clé étrangère qui matérialise la relation "Proposer"
    val texte: String,
    val correcte: Boolean
)

//  TABLE SCORE (Obligatoire pour l'historique et valider le CREATE/DELETE du CRUD)
@Entity(
    tableName = "scores",
    foreignKeys = [
        ForeignKey(
            entity = Theme::class,
            parentColumns = ["id_theme"],
            childColumns = ["themeId"],
            // Si on supprime le thème, on garde le score dans l'historique
            // mais on passe son themeId à null pour ne pas casser la base
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class Score(
    @PrimaryKey(autoGenerate = true) val id_score: Int = 0,
    val date_partie: String,
    val points_obtenus: Int,
    val total_questions: Int,
    val themeId: Int? // ID lié (nullable au cas où le thème est supprimé)
)

// Petit conteneur de données  créé pour récupérer le résultat de la jointure
data class ScoreAvecTheme(
    val id_score: Int,
    val date_partie: String,
    val points_obtenus: Int,
    val total_questions: Int,
    val nom_theme: String? // Contiendra le nom du thème récupéré via le JOIN SQL
)