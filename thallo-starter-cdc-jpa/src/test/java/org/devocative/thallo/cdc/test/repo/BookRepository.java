package org.devocative.thallo.cdc.test.repo;

import org.devocative.thallo.cdc.test.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
}
