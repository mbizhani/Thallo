package org.devocative.thallo.itest.domain;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import lombok.Getter;
import lombok.Setter;
import org.devocative.thallo.itest.IService;

import java.util.Objects;

@Getter
@Setter
public abstract class AbstractService {
	@XStreamAsAttribute
	private String name;

	// ------------------------------

	public abstract IService<? extends AbstractService> create();

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AbstractService)) return false;
		AbstractService that = (AbstractService) o;
		return Objects.equals(getName(), that.getName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getName());
	}
}
