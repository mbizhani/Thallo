package org.devocative.thallo.cdc;

public interface CdcTargetHandler<T> {
	default T beforePersist(T target) {
		return target;
	}

	default T beforeUpdate(T target) {
		return target;
	}

	default boolean beforeDelete(T target) {
		return true;
	}
}
