package com.example.taskmasterapp;

import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.Espresso.onView;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class taskMasterTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Test
    public void test_The_Main_Activity(){
        onView(withId(R.id.myTasks)).check(matches(withText("My Tasks")));
    }


    @Test
    public void AddTaskTest() {

        onView(withId(R.id.addTaskButton)).perform(click());
        onView(withId(R.id.addATask)).check(matches(withText("Add Task")));

        onView(withId(R.id.newTaskName)).perform(typeText("espresso test1"), closeSoftKeyboard());
        onView(withId(R.id.newTaskBody)).perform(typeText("testing add task page"), closeSoftKeyboard());
        onView(withId(R.id.newTaskSubmit)).perform(click());

        Espresso.pressBack();
    }

    @Test
    public void RecyclerViewAfterAddingTask() {
        onView(withId(R.id.tasks_recycleView)).perform(actionOnItemAtPosition(0, click()));
        onView(withId(R.id.singleTaskTitle)).check(matches(withText("espresso test1")));
        onView(withId(R.id.task_body)).check(matches(withText("testing add task page")));
    }


    @Test
    public void SettingTestAddUsername() {

        onView(withId(R.id.settingBtn)).perform(click());

        onView(withId(R.id.usernameInput)).perform(typeText("James Gosling"), closeSoftKeyboard());
        onView(withId(R.id.usernameSaveButton)).perform(click());
//        Espresso.pressBack();

        onView(withId(R.id.textViewUserName)).check(matches(withText("James Gosling's Tasks")));
    }



}
