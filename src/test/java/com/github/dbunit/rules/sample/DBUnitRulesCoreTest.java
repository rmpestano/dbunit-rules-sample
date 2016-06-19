package com.github.dbunit.rules.sample;

import com.github.dbunit.rules.DBUnitRule;
import com.github.dbunit.rules.api.dataset.DataSet;
import com.github.dbunit.rules.api.dataset.ExpectedDataSet;
import com.github.dbunit.rules.util.EntityManagerProvider;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.github.dbunit.rules.util.EntityManagerProvider.em;
import static com.github.dbunit.rules.util.EntityManagerProvider.tx;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by rmpestano on 6/19/16.
 */
@RunWith(JUnit4.class)
public class DBUnitRulesCoreTest {

    @Rule
    public EntityManagerProvider emProvider = EntityManagerProvider.instance("rulesDB");

    @Rule
    public DBUnitRule dbUnitRule = DBUnitRule.instance(emProvider.connection());


    @Test
    @DataSet("users.yml")
    public void shouldListUsers() {
        List<User> users = em().
                createQuery("select u from User u").
                getResultList();
        assertThat(users).
                isNotNull().
                isNotEmpty().
                hasSize(2);
    }

    @Test
    @DataSet("users.yml")
    public void shouldUpdateUser() {
        User user = (User) em().
                createQuery("select u from User u  where u.id = 1").
                getSingleResult();
        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("@realpestano");
        tx().begin();
        user.setName("@rmpestano");
        em().merge(user);
        tx().commit();
        assertThat(user.getName()).isEqualTo("@rmpestano");
    }

    @Test
    @DataSet("users.yml")
    public void shouldDeleteUser() {
        User user = (User) em().
                createQuery("select u from User u  where u.id = 1").
                getSingleResult();
        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("@realpestano");
        tx().begin();
        em().remove(user);
        tx().commit();
        List<User> users = em().
                createQuery("select u from User u ").
                getResultList();
        assertThat(users).
                hasSize(1);
    }

    @Test
    @DataSet("users.yml")
    @ExpectedDataSet(value = "expectedUser.yml",ignoreCols = "id")
    public void shouldAssertDatabaseUsingExpectedDataSet() {
        User user = (User) em().
                createQuery("select u from User u  where u.id = 1").
                getSingleResult();
        assertThat(user).isNotNull();
        tx().begin();
        em().remove(user);
        tx().commit();
    }

    @Test
    @DataSet(cleanBefore = true)
    @ExpectedDataSet("expectedUsersRegex.yml")
    public void shouldAssertDatabaseUsingRegex() {
        User u = new User();
        u.setName("expected user1");
        User u2 = new User();
        u2.setName("expected user2");
        tx().begin();
        em().persist(u);
        em().persist(u2);
        tx().commit();
    }

    @Test
    @DataSet(value = "dataset-with-javascript.yml",
            cleanBefore = true,
            disableConstraints = true)
    public void shouldSeedDatabaseUsingJavaScriptInDataset() {
        Tweet tweet = (Tweet) emProvider.em().createQuery("select t from Tweet t where t.id = 1").getSingleResult();
        assertThat(tweet).isNotNull();
        assertThat(tweet.getLikes()).isEqualTo(50);
    }

    @Test
    @DataSet(value = "dataset-with-groovy.yml",
            cleanBefore = true,
            disableConstraints = true)
    public void shouldSeedDatabaseUsingGroovyInDataset() throws ParseException {
        Tweet tweet = (Tweet) emProvider.em().createQuery("select t from Tweet t where t.id = '1'").getSingleResult();
        assertThat(tweet).isNotNull();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//remove time
        Date now = sdf.parse(sdf.format(new Date()));
        assertThat(tweet.getDate()).isEqualTo(now);
    }
}
