package com.github.dbunit.rules.sample.cdi;

import com.github.dbunit.rules.api.dataset.ExpectedDataSet;
import com.github.dbunit.rules.cdi.api.UsingDataSet;
import com.github.dbunit.rules.sample.User;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static com.github.dbunit.rules.util.EntityManagerProvider.em;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by rmpestano on 6/19/16.
 */
// tag::sample[]
@RunWith(CdiTestRunner.class) //<1>
public class DBUnitRulesCDITest {

    @Inject
    EntityManager em; //<2>



    @Test
    @UsingDataSet("users.yml") //<3>
    public void shouldListUsers() {
        List<User> users = em().
                createQuery("select u from User u").
                getResultList();
        assertThat(users).
                isNotNull().
                isNotEmpty().
                hasSize(2);
    }
// end::sample[]

    // tag::expectedCDI[]
    @Test
    @UsingDataSet(cleanBefore = true) //needed to activate interceptor (can be at class level)
    @ExpectedDataSet(value = "expectedUsers.yml",ignoreCols = "id")
    public void shouldMatchExpectedDataSet() {
        User u = new User();
        u.setName("expected user1");
        User u2 = new User();
        u2.setName("expected user2");
        em.getTransaction().begin();
        em.persist(u);
        em.persist(u2);
        em.getTransaction().commit();
    }
    // end::expectedCDI[]

}
