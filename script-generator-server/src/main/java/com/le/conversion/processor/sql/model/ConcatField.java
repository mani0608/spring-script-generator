package com.le.conversion.processor.sql.model;

import org.apache.commons.lang3.StringUtils;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Builder
@ToString
@Accessors(fluent = true)
public class ConcatField {

	@Getter
	@Setter
	private String fieldName;

	@Getter
	@Setter
	private String concatValue;

	@Getter
	@Setter
	private Boolean isNewLineRequired;

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 31 * (StringUtils.isNotBlank(fieldName) ? fieldName.hashCode() : 0)
				+ (StringUtils.isNotBlank(concatValue) ? concatValue.hashCode() : 0)
				+ ((isNewLineRequired != null) ? isNewLineRequired.hashCode() : 0);
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		
		if (obj instanceof ConcatField) {
			ConcatField cf = (ConcatField) obj;
			return (StringUtils.stripToEmpty(fieldName).equals(StringUtils.stripToEmpty(cf.fieldName()))
					&& StringUtils.stripToEmpty(concatValue).equals(StringUtils.stripToEmpty(cf.concatValue()))
					&& isNewLineRequired == cf.isNewLineRequired());
		}else {
			return false;
		}
	}

}
