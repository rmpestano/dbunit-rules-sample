package com.github.dbunit.rules.sample;

import com.github.dbunit.rules.DBUnitRule;
import com.github.dbunit.rules.api.dataset.DataSet;
import com.github.dbunit.rules.util.EntityManagerProvider;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static com.github.dbunit.rules.util.EntityManagerProvider.em;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by pestano on 16/07/16.
 */
public class ToManyAssociationListTest {

    @Rule
    public EntityManagerProvider emProvider = EntityManagerProvider.instance("rulesDB");

    @Rule
    public DBUnitRule dbUnitRule = DBUnitRule.instance(emProvider.connection());


    @Test
    @DataSet("userTweets.yml")
    public void shouldListUsersAndTweetsWithJPQL() {

        long count = (Long) em().createQuery("select count (distinct u.id) from User u " +
                "left join u.tweets t where t.content like '%tweet%'").
                getSingleResult();
        assertThat(count).isEqualTo(3);

        List<User> users = em().createQuery
                ("select distinct u from User u " +
                        "left join fetch u.tweets t where t.content like '%tweet%'").
                setFirstResult(0).setMaxResults(2).getResultList();
        assertThat(users).isNotNull().hasSize(2);
        assertThat(users.get(0)).hasFieldOrPropertyWithValue("name","@dbunit");
        assertThat(users.get(1)).hasFieldOrPropertyWithValue("name","@dbunit2");
        assertThat(users.get(0).getTweets()).isNotNull().hasSize(2);
        assertThat(users.get(1).getTweets()).isNotNull().hasSize(2);

    }

    @Test
    @DataSet("userTweets.yml")
    public void shouldListUsersAndTweetsWithHibernateCriteria() {

        Session session = em().unwrap(Session.class);
        Criteria criteria = session.createCriteria(User.class);

        long count = (Long)criteria.createAlias("tweets","t", JoinType.LEFT_OUTER_JOIN).
        add(Restrictions.ilike("t.content", "tweet", MatchMode.ANYWHERE)).
        setProjection(Projections.countDistinct("id")).
        uniqueResult();

        assertThat(count).isEqualTo(3);


        ProjectionList projectionList = Projections.projectionList().
                add(Projections.id().as("id")).
                add(Projections.property("name").as("name")).
                add(Projections.property("t.id").as("tweets.id")).
                add(Projections.property("t.content").as("tweets.content")).
                add(Projections.property("t.likes").as("tweets.likes"));


        List<User> users = criteria.setProjection(Projections.distinct(projectionList)).
                 //hibernate's alisToBean throws PropertyNotFoundException: Could not find setter for tweets.id on class com.github.dbunit.rules.sample.User
                 //setResultTransformer(new AliasToBeanResultTransformer(User.class)).
                setResultTransformer(new AliasToBeanNestedResultTransformer(User.class)).
                setFirstResult(0).setMaxResults(2).
                list();
        assertThat(users).isNotNull().hasSize(2);
        assertThat(users.get(0)).hasFieldOrPropertyWithValue("name","@dbunit");
        //fails cause resultTransformer resolves entity values in-memory ater the page has been returned from db
        assertThat(users.get(1)).hasFieldOrPropertyWithValue("name","@dbunit2");
        assertThat(users.get(0).getTweets()).isNotNull().hasSize(2);
        assertThat(users.get(1).getTweets()).isNotNull().hasSize(2);

    }


}
