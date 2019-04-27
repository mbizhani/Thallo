package org.devocative.thallo.cdc.test.model;

import org.devocative.thallo.cdc.annotation.CdcSource;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@CdcSource
public class Person {
	@Id
	private Long id;

	private String name;

	@OneToMany(mappedBy = "owner")
	private List<Book> myBooks;

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

	public List<Book> getMyBooks() {
		return myBooks;
	}

	public void setMyBooks(List<Book> myBooks) {
		this.myBooks = myBooks;
	}
}
