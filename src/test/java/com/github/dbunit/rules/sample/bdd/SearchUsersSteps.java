package com.github.dbunit.rules.sample.bdd;

import com.github.dbunit.rules.api.dataset.DataSet;
import com.github.dbunit.rules.cdi.api.DBUnitInterceptor;
import com.github.dbunit.rules.sample.User;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by pestano on 20/06/16.
 */
@DBUnitInterceptor
public class SearchUsersSteps {

    @Inject
    EntityManager entityManager;

    List<User> usersFound;

    @Given("^We have two users that have tweets in our database$")
    @DataSet("usersWithTweet.json")
    public void We_have_two_users_in_our_database() throws Throwable {
    }

    @When("^I search them by tweet content \"([^\"]*)\"$")
    public void I_search_them_by_tweet_content_value(String tweetContent) throws Throwable {
        Session session = entityManager.unwrap(Session.class);
        usersFound = session.createCriteria(User.class).
        createAlias("tweets","tweets", JoinType.LEFT_OUTER_JOIN).
        add(Restrictions.ilike("tweets.content",tweetContent, MatchMode.ANYWHERE)).list();
    }

    @Then("^I should find (\\d+) users$")
    public void I_should_find_number_users(int numberOfUsersFound) throws Throwable {
        assertThat(usersFound).
                isNotNull().
                hasSize(numberOfUsersFound).
                contains(new User(1L));//examples contains user with id=1
    }


}
