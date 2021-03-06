package org.tutske.rest.exceptions;

import org.tutske.rest.data.RestObject;
import org.tutske.rest.jwt.JsonWebToken;

import javax.servlet.http.HttpServletResponse;


public class InvalidJwtException extends ResponseException {

	{
		type = "/invalid_jwt";
		title = "The provided jwt could not be validated";
		status = HttpServletResponse.SC_FORBIDDEN;
	}

	public InvalidJwtException () {
		this ("Invalid jwt.");
	}

	public InvalidJwtException (String message) {
		super (message);
	}

	public InvalidJwtException (String message, Throwable cause) {
		super (message, cause);
	}

	public InvalidJwtException (Throwable cause) {
		super (cause);
	}

	public InvalidJwtException (RestObject data) {
		super (data);
	}

	public InvalidJwtException (String message, RestObject data) {
		super (message, data);
	}

	public InvalidJwtException (JsonWebToken token) {
		this (new RestObject () {{
			v ("token", token == null ? "null" : token.toString ());
		}});
	}

}
