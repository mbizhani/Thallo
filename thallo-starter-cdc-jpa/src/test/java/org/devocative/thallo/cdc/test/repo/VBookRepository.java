package org.devocative.thallo.cdc.test.repo;

import org.devocative.thallo.cdc.test.model.VBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VBookRepository extends JpaRepository<VBook, Long> {
}
