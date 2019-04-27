package org.devocative.thallo.cdc.test.model;

import org.devocative.thallo.cdc.CdcTargetHandler;
import org.devocative.thallo.cdc.annotation.CdcTarget;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@CdcTarget(handler = VBook.CdcHandler.class)
public class VBook {
	@Id
	private Long id;

	@Column(nullable = false)
	private String name;

//	@Column(nullable = false)
//	private String publisher;

	private Price price;

	private Integer version;

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

	public Price getPrice() {
		return price;
	}

	public void setPrice(Price price) {
		this.price = price;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	// ------------------------------

	public static class CdcHandler implements CdcTargetHandler<VBook> {
		@Override
		public VBook beforePersist(VBook target) {
			if (target.getPrice() != null) {
				target.setName("t - " + target.getName());
				return target;
			}

			return null;
		}
	}
}
