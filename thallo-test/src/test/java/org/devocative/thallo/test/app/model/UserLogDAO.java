package org.devocative.thallo.test.app.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserLogDAO extends JpaRepository<UserLog, Long>, JpaSpecificationExecutor<UserLog> {
}
