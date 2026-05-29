package com.example.quizz

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.quizz.ui.MainActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    /**
     * TEST 1 : Vérifie l'état initial de l'application au démarrage.
     */
    @Test
    fun test01_EtatInitialDeLApplication() {
        onView(withId(R.id.layoutThemes)).check(matches(isDisplayed()))
        onView(withId(R.id.layoutJeu)).check(matches(not(isDisplayed())))
        onView(withId(R.id.layoutScore)).check(matches(not(isDisplayed())))
        onView(withId(R.id.layoutHistorique)).check(matches(not(isDisplayed())))
    }

    /**
     * TEST 2 : Test complet de la navigation vers l'historique et du retour au menu.
     */
    @Test
    fun test02_NavigationHistorique() {
        onView(withId(R.id.btnVoirHistorique)).perform(click())
        onView(withId(R.id.layoutHistorique)).check(matches(isDisplayed()))
        onView(withId(R.id.layoutThemes)).check(matches(not(isDisplayed())))

        onView(withId(R.id.btnRetourMenuDepuisHist)).perform(click())
        onView(withId(R.id.layoutThemes)).check(matches(isDisplayed()))
        onView(withId(R.id.layoutHistorique)).check(matches(not(isDisplayed())))
    }

    /**
     * TEST 3 : Test complet d'un scénario de jeu (Version Finale Blindée)
     */
    @Test
    fun test03_ScenarioDeJeuComplet() {
        // Attente renforcée pour laisser à la base Room le temps de se déployer
        Thread.sleep(3000)

        // on applique 'nthChildOf'
        // avec l'index 0 pour sélectionner uniquement le TOUT PREMIER bouton généré.
        onView(
            nthChildOf(
                allOf(
                    isDescendantOfA(withId(R.id.containerBoutonsThemes)),
                    isAssignableFrom(Button::class.java)
                ),
                0
            )
        ).perform(click())

        // VÉRIFICATION : L'écran de jeu s'affiche bien
        onView(withId(R.id.layoutThemes)).check(matches(not(isDisplayed())))
        onView(withId(R.id.layoutJeu)).check(matches(isDisplayed()))

        //  VÉRIFICATION DE L'ÉCRAN DE JEU
        onView(withId(R.id.txtProgression)).check(matches(withText("Question 1 / 10")))
        onView(withId(R.id.btnQuestionSuivante)).check(matches(not(isDisplayed())))

        //  SELECTION D'UNE RÉPONSE (Question 1)
        onView(withId(R.id.btnReponse1)).perform(click())
        onView(withId(R.id.btnReponse1)).check(matches(not(isEnabled())))
        onView(withId(R.id.btnQuestionSuivante)).check(matches(isDisplayed()))

        //  AVANCEMENT AUTOMATIQUE DES QUESTIONS 2 À 10
        for (i in 2..10) {
            onView(withId(R.id.btnQuestionSuivante)).perform(click())
            onView(withId(R.id.btnReponse1)).perform(click())
        }

        // Clic pour valider la dernière question et basculer sur l'écran des scores
        onView(withId(R.id.btnQuestionSuivante)).perform(click())

        //  VÉRIFICATION DE L'ÉCRAN DE SCORES
        onView(withId(R.id.layoutJeu)).check(matches(not(isDisplayed())))
        onView(withId(R.id.layoutScore)).check(matches(isDisplayed()))
        onView(withId(R.id.txtScoreFinal)).check(matches(withText(org.hamcrest.CoreMatchers.containsString("Votre score :"))))

        //  RETOUR AU MENU
        onView(withId(R.id.btnRetourMenu)).perform(click())
        onView(withId(R.id.layoutThemes)).check(matches(isDisplayed()))
        onView(withId(R.id.layoutScore)).check(matches(not(isDisplayed())))
    }

    /**
     * FONCTION UTILITAIRE (Custom Matcher) : Permet de choisir un enfant précis parmi une liste
     * d'éléments correspondants, évitant ainsi l'erreur AmbiguousViewMatcherException.
     */
    private fun nthChildOf(parentMatcher: Matcher<View>, childPosition: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("avec l'enfant à la position $childPosition depuis le parent sélectionné")
                parentMatcher.describeTo(description)
            }

            override fun matchesSafely(view: View): Boolean {
                if (view.parent !is ViewGroup) return false
                val parent = view.parent as ViewGroup

                // On vérifie d'abord si le parent correspond globalement à nos critères
                if (!parentMatcher.matches(parent)) {
                    // Si ce n'est pas le parent direct, on vérifie si la vue elle-même correspond
                    // au critère et si elle est bien à la position voulue dans son conteneur direct
                    return parentMatcher.matches(view) && parent.getChildAt(childPosition) == view
                }
                return parent.getChildAt(childPosition) == view
            }
        }
    }
}