package org.devocative.thallo.core.aspect;

import org.devocative.thallo.core.annotation.ELogMode;
import org.devocative.thallo.core.annotation.ELogPlace;
import org.devocative.thallo.core.annotation.EStackTraceLogType;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "devocative.thallo.log")
public class MethodLogConfiguration {
	private ELogMode mode;

	private Boolean logParams;

	private Boolean logResult;

	private EStackTraceLogType stacktrace;

	private ELogPlace place;

	// ------------------------------

	public ELogMode getMode() {
		return mode;
	}

	public void setMode(ELogMode mode) {
		this.mode = mode;
	}

	public Boolean getLogParams() {
		return logParams;
	}

	public void setLogParams(Boolean logParams) {
		this.logParams = logParams;
	}

	public Boolean getLogResult() {
		return logResult;
	}

	public void setLogResult(Boolean logResult) {
		this.logResult = logResult;
	}

	public EStackTraceLogType getStacktrace() {
		return stacktrace;
	}

	public void setStacktrace(EStackTraceLogType stacktrace) {
		this.stacktrace = stacktrace;
	}

	public ELogPlace getPlace() {
		return place;
	}

	public void setPlace(ELogPlace place) {
		this.place = place;
	}

}
