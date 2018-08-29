package com.le.conversion.common;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;

import org.apache.commons.lang3.StringUtils;

public enum Constants {

	COMMA(","), PERIOD("."), SPACE(" "), EMPTY(""), PLUS("+"), EQ_SIGN("="), NEQ_SIGN("<>"), 
	PERCENT("%"), SEMI_COLON(";"), UNDERSCORE("_"), HYPHEN("-"), COLON(":"), SBRACKET("["), EBRACKET("]"), 
	OPEN_PARAN("("), CLOSE_PARAN(")"), DEST_VAL_PH("_VALUE"), NEWLINE_PH("_NL"), AND_PH(" _AND_ "), 
	ELSE_PH(" _ELSE_ "), IN_PH(" _IN_ "), NOT_IN_PH(" _NOT_IN_ "), CASE_WHEN_PH("_CASE_WHEN_ "), REL_CASE_WHEN_PH("_REL_CASE_WHEN_ "), 
	EQ_PH(" _EQ_ "), NEQ_PH(" _NEQ_ "), NULL_PH(" _IS_NULL_ "), NOT_NULL_PH(" _IS_NOT_NULL_ "), REL_COND_PFX("_REL_"),
	SRC_VAL_PH("_SVALUE"), LIKE_PH(" _LIKE_ "), NOT_LIKE_PH(" _NOT_LIKE_ "), KEY_PH("_KEY:"), SRC_KEY_PH("_SK:"), 
	REL_COND_PH("_COND:"), DBL_QUOTE("\""), CONCAT_PH("||"), SINGLE_QUOTE("'"), CT_IN("IN"), CT_NOT_IN("NOT IN"), CT_LIKE("LIKE"), 
	CT_NOT_LIKE("NOT LIKE"), CT_EQ("EQ"), CT_NOT_EQ("NOT EQ"), CT_NULL("NULL"), CT_NOT_NULL("NOT NULL"), 
	SRC_REl_ALIAS_PFX("SRC"), TGT_REl_ALIAS_PFX("TGT"), NL_CHAR_PH("@newlinechar");

	public static final EnumSet<Constants> PLHDRS = EnumSet.of(AND_PH, ELSE_PH, IN_PH, NOT_IN_PH, EQ_PH, NEQ_PH,
			NULL_PH, NOT_NULL_PH, LIKE_PH, NOT_LIKE_PH);
	public static final EnumSet<Constants> MULTIVALUES = EnumSet.of(IN_PH, NOT_IN_PH);
	public static final EnumSet<Constants> LIKES = EnumSet.of(LIKE_PH, NOT_LIKE_PH);

	public static final EnumMap<Constants, Constants> PHCT = new EnumMap<>(Constants.class);

	private String constant;

	Constants(String constant) {
		this.constant = constant;
	}

	public String value() {
		return this.constant;
	}

	public static Constants getEnum(String enumValue) {

		return Arrays.asList(Constants.values()).stream().filter(cnst -> StringUtils.equals(cnst.value(), enumValue))
				.findFirst().get();

	}

	public static String getCondType(String condType) {

		if (PHCT.size() == 0) {
			initializeMapper();
		}

		return PHCT.get(getEnum(condType)).value();

	}

	private static void initializeMapper() {

		PHCT.put(IN_PH, CT_IN);
		PHCT.put(NOT_IN_PH, CT_NOT_IN);
		PHCT.put(LIKE_PH, CT_LIKE);
		PHCT.put(NOT_LIKE_PH, CT_NOT_LIKE);
		PHCT.put(EQ_PH, CT_EQ);
		PHCT.put(NEQ_PH, CT_NOT_EQ);
		PHCT.put(NULL_PH, CT_NULL);
		PHCT.put(NOT_NULL_PH, CT_NOT_NULL);

	}

	public static String getElseCondition(String conditionType) {

		switch (Constants.getEnum(conditionType)) {

		case IN_PH:
			return NOT_IN_PH.value();
		case NOT_IN_PH:
			return IN_PH.value();
		case LIKE_PH:
			return NOT_LIKE_PH.value();
		case NOT_LIKE_PH:
			return LIKE_PH.value();
		case EQ_PH:
			return NEQ_PH.value();
		case NEQ_PH:
			return EQ_PH.value();
		case NULL_PH:
			return NOT_NULL_PH.value();
		case NOT_NULL_PH:
			return NULL_PH.value();
		default:
			return null;

		}

	}

}
