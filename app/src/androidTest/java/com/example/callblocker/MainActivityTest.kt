package com.example.callblocker

import androidx.appcompat.widget.AppCompatEditText
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.callblocker.utils.RecyclerViewMatcher
import com.example.callblocker.utils.RecyclerViewMatcher.Companion.actionOnItemViewAtPosition
import com.example.callblocker.utils.RecyclerViewMatcher.Companion.actionOnItemViewWithText
import com.example.callblocker.view.BlackListAdapter
import com.example.callblocker.view.MainActivity
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    var activityScenarioRule =
        ActivityScenarioRule(
            MainActivity::class.java
        )

    @Test
    fun testUi() {
        onView(withId(R.id.fab_add))
            .check(matches(isDisplayed()))

        onView(withId(R.id.rv_blacklist))
            .check(matches(hasChildCount(0)))
    }

    @Test
    fun testListOperations() {
        onView(withId(R.id.fab_add))
            .perform(click())
        onView(withText("Number"))
            .check(matches(isDisplayed()))
        onView(withText("Contacts"))
            .check(matches(isDisplayed()))
        pressBack()
        onView(withId(R.id.rv_blacklist))
            .check(matches(hasChildCount(0)))


        onView(withId(R.id.fab_add))
            .perform(click())
        onView(withText("Number"))
            .perform(click())
        onView(withText("Enter number"))
            .check(matches(isDisplayed()))
        pressBack()
        onView(withId(R.id.rv_blacklist))
            .check(matches(hasChildCount(0)))

        onView(withId(R.id.fab_add))
            .perform(click())
        onView(withText("Number"))
            .perform(click())
        onView(withClassName(Matchers.equalTo(AppCompatEditText::class.java.name)))
            .perform(ViewActions.typeText("1234567890"))
        onView(withText("Cancel"))
            .perform(click())
        onView(withId(R.id.rv_blacklist))
            .check(matches(hasChildCount(0)))

        onView(withId(R.id.fab_add))
            .perform(click())
        onView(withText("Number"))
            .perform(click())
        onView(withClassName(Matchers.equalTo(AppCompatEditText::class.java.name)))
            .perform(ViewActions.typeText("12345"))
        onView(withText("Block"))
            .perform(click())
        onView(withText(R.string.msg_invalid_number))
            .check(matches(isDisplayed()))


        //Insert
        insertNumber("1234567890")
        onView(withId(R.id.rv_blacklist))
            .check(matches(hasChildCount(1)))
        onView(RecyclerViewMatcher.withRecyclerView(R.id.rv_blacklist)?.atPosition(0))
            .check(matches(hasDescendant(withText("1234567890"))))

        //Delete
        deleteNumber("1234567890")
        onView(withId(R.id.rv_blacklist))
            .check(matches(hasChildCount(0)))


        insertNumber("9000000000")
        insertNumber("8000000000")
        insertNumber("7000000000")
        onView(withId(R.id.rv_blacklist))
            .check(matches(hasChildCount(3)))
        onView(withId(R.id.rv_blacklist))
            .check(ViewAssertions.matches(hasDescendant(withText("9000000000"))))
        onView(withId(R.id.rv_blacklist))
            .check(ViewAssertions.matches(hasDescendant(withText("8000000000"))))
        onView(withId(R.id.rv_blacklist))
            .check(ViewAssertions.matches(hasDescendant(withText("7000000000"))))

        deleteNumber("8000000000")
        onView(withId(R.id.rv_blacklist))
            .check(matches(hasChildCount(2)))
        onView(withId(R.id.rv_blacklist))
            .check(ViewAssertions.matches(hasDescendant(withText("9000000000"))))
        onView(withId(R.id.rv_blacklist))
            .check(ViewAssertions.matches(hasDescendant(withText("7000000000"))))
        onView(withId(R.id.rv_blacklist))

        deleteNumber("7000000000")
        onView(withId(R.id.rv_blacklist))
            .check(matches(hasChildCount(1)))
        onView(withId(R.id.rv_blacklist))
            .check(ViewAssertions.matches(hasDescendant(withText("9000000000"))))
        onView(withId(R.id.rv_blacklist))


        insertNumber("7000000000")
        onView(withId(R.id.rv_blacklist))
            .check(matches(hasChildCount(2)))
        onView(withId(R.id.rv_blacklist))
            .check(ViewAssertions.matches(hasDescendant(withText("9000000000"))))
        onView(withId(R.id.rv_blacklist))
            .check(ViewAssertions.matches(hasDescendant(withText("7000000000"))))
        onView(withId(R.id.rv_blacklist))

        deleteNumber("9000000000")
        deleteNumber("7000000000")
        onView(withId(R.id.rv_blacklist))
            .check(matches(hasChildCount(0)))
    }

    private fun deleteNumberAt(pos: Int) {
        onView(withId(R.id.rv_blacklist))
            .perform(
                actionOnItemViewAtPosition<BlackListAdapter.ViewHolder>(
                    pos,
                    R.id.btn_delete,
                    click()
                )
            )
    }

    private fun deleteNumber(number: String) {
        onView(withId(R.id.rv_blacklist))
            .perform(
                actionOnItemViewWithText<BlackListAdapter.ViewHolder>(
                    number,
                    R.id.btn_delete,
                    click()
                )
            )
    }

    private fun insertNumber(number: String) {
        onView(withId(R.id.fab_add))
            .perform(ViewActions.click())
        onView(withText("Number"))
            .perform(ViewActions.click())
        onView(withClassName(Matchers.equalTo(AppCompatEditText::class.java.name)))
            .perform(ViewActions.typeText(number))
        onView(withText("Block"))
            .perform(ViewActions.click())
    }
}