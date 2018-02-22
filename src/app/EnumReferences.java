package app;

import java.util.regex.Pattern;

public interface EnumReferences {

	public static final String REGEX_TOKENS = "(\\d+\\.\\d+|\\d+|\\D)";
	public static final Pattern PATTERN_TOKENS = Pattern.compile(REGEX_TOKENS, Pattern.MULTILINE);
	public static final String OPERATEURS = "+-*/^s";
	public static final String[] PRIORITE = { "(){}[]", "+-", "/*", "^s" };
	public static final String L_BRACKETS = "[{(";
	public static final String R_BRACKETS = "]})";

}
