package org.devocative.thallo.cdc.test.model;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.util.Date;

@MappedSuperclass
public abstract class Auditable {
	@CreatedDate
	@Column(name = "d_created_date", updatable = false)
	private Date createdDate;

	@CreatedBy
	@Column(name = "c_created_by", updatable = false)
	private String createdBy;

	@LastModifiedDate
	@Column(name = "d_last_modified_date")
	private Date lastModifiedDate;

	@LastModifiedBy
	@Column(name = "c_last_modified_by")
	private String lastModifiedBy;

	@Version
	@Column(name = "n_version", nullable = false)
	private Integer version;

	// ------------------------------

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
}
