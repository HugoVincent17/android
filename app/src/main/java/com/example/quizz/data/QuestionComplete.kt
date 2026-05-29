package com.example.quizz.data

import androidx.room.Embedded
import androidx.room.Relation
import com.exemple.quizz.Question
import com.exemple.quizz.Reponse

data class QuestionComplete(
    @Embedded val question: Question,
    @Relation(
        parentColumn = "id_question",
        entityColumn = "questionId"
    )
    val reponses: List<Reponse> // Room ira chercher automatiquement les 4 réponses de cette question
)