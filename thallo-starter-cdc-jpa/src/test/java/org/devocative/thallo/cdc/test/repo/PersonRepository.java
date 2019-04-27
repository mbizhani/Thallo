package org.devocative.thallo.cdc.test.repo;

import org.devocative.thallo.cdc.test.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
}
