package org.devocative.thallo.cdc.test.model;

import org.devocative.thallo.cdc.annotation.CdcTarget;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@CdcTarget
public class VPerson {
	@Id
	private Long id;

	private String name;

	// ------------------------------

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
