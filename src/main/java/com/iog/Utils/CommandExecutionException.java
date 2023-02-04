package com.iog.Utils;

public class CommandExecutionException extends RuntimeException {
	/**
	 * Severity of the exception
	 */
	public final Severity severity;
	
	/**
	 * @param severity Severity of the exception
	 * @param cause    The cause of the exception with technical details
	 */
	public CommandExecutionException(String reason, Severity severity, Throwable cause) {
		super(reason, cause);
		this.severity = severity;
	}
	
	/**
	 * Severity levels for CommandExecutionException
	 */
	public enum Severity {
		/**
		 * The cause is known and expected, indicates that there is nothing wrong with the library itself.
		 */
		COMMON,
		/**
		 * The cause might not be exactly known, but is possibly caused by outside factors. For example when an outside
		 * service responds in a format that we do not expect.
		 */
		SUSPICIOUS,
		/**
		 * If the probable cause is an issue with the library or when there is no way to tell what the cause might be.
		 * This is the default level and other levels are used in cases where the thrower has more in-depth knowledge
		 * about the error.
		 */
		FAULT
	}
}
