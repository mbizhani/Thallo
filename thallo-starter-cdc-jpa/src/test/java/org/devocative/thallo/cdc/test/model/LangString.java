package org.devocative.thallo.cdc.test.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.devocative.thallo.cdc.annotation.CdcSource;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.io.Serializable;

@CdcSource
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LangString implements Comparable<LangString>, Serializable {
	private static final long serialVersionUID = -1159507817595820888L;

	private String ar;

	private String en;

	private String fa;

	// ------------------------------

	public LangString() {
	}

	public LangString(String en) {
		this.en = en;
	}

	// ------------------------------

	public String getAr() {
		return ar;
	}

	public void setAr(String ar) {
		this.ar = ar;
	}

	public String getEn() {
		return en;
	}

	public void setEn(String en) {
		this.en = en;
	}

	public String getFa() {
		return fa;
	}

	public void setFa(String fa) {
		this.fa = fa;
	}

	// ---------------

	@Override
	public String toString() {
		return String.format("{en: %s, fa: %s}", en, fa);
	}

	@Override
	public int compareTo(LangString o) {
		return toString().compareTo(o.toString());
	}

	// ------------------------------

	@Converter(autoApply = true)
	public static class LangStringConverter implements AttributeConverter<LangString, String> {
		@Override
		public String convertToDatabaseColumn(LangString localeString) {
			try {
				return localeString != null ? new ObjectMapper().writeValueAsString(localeString) : null;
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public LangString convertToEntityAttribute(String text) {
			try {
				if (text != null) {
					return new ObjectMapper().readValue(text, LangString.class);
				}

				return null;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
