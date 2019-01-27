package com.kela.common.response.error;

import com.kela.common.response.Response;

public class ErrorMessage extends Response{
	String errorMessage;

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
