package org.devocative.thallo.cdc.test.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Price {

	private BigDecimal amount;

	private String currency;

	// ------------------------------

	public Price() {
	}

	public Price(BigDecimal amount, String currency) {
		this.amount = amount;
		this.currency = currency;
	}

	// ------------------------------

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
}
