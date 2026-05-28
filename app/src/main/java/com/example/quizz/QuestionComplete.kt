package com.exemple.quizz // /!\ Remplace par ton package

import androidx.room.Embedded
import androidx.room.Relation

data class QuestionComplete(
    @Embedded val question: Question,
    @Relation(
        parentColumn = "id_question",
        entityColumn = "questionId"
    )
    val reponses: List<Reponse> // Room ira chercher automatiquement les 4 réponses de cette question
)