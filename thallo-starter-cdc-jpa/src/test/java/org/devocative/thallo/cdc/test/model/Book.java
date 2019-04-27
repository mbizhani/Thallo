package org.devocative.thallo.cdc.test.model;

import org.devocative.thallo.cdc.annotation.CdcSource;
import org.devocative.thallo.cdc.annotation.CdcSourceData;

import javax.persistence.*;
import java.util.List;

@Entity
@CdcSource
public class Book extends Auditable {
	@Id
	private Long id;

	private String name;

	private String publisher;

	private Price price;

	private EBookSize size;

	@ManyToOne
	@JoinColumn(name = "f_owner")
	private Person owner;

	@CdcSourceData(false)
	@ManyToMany
	@JoinTable(name = "mt_books_author")
	private List<Person> authors;

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

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public Price getPrice() {
		return price;
	}

	public void setPrice(Price price) {
		this.price = price;
	}

	public EBookSize getSize() {
		return size;
	}

	public void setSize(EBookSize size) {
		this.size = size;
	}

	public Person getOwner() {
		return owner;
	}

	public void setOwner(Person owner) {
		this.owner = owner;
	}

	public List<Person> getAuthors() {
		return authors;
	}

	public void setAuthors(List<Person> authors) {
		this.authors = authors;
	}
}
