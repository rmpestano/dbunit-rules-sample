package com.github.dbunit.rules.sample;

import javax.persistence.metamodel.SingularAttribute;

import org.apache.deltaspike.data.api.Repository;
import org.apache.deltaspike.data.api.criteria.CriteriaSupport;
import org.apache.deltaspike.data.api.criteria.QuerySelection;

@Repository
public abstract class UserRepository implements CriteriaSupport<User>{

    

}
