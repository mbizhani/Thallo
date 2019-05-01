package org.devocative.thallo.cdc.test.repo;

import org.devocative.thallo.cdc.test.model.VPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VPersonRepository extends JpaRepository<VPerson, Long> {
}
