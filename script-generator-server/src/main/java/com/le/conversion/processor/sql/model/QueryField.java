package com.le.conversion.processor.sql.model;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.le.conversion.common.util.CommonUtil;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Builder
@ToString
@Accessors(fluent=true)
public class QueryField {

	@Getter @Setter
	private String importFieldName;
	
	@Getter @Setter
	private String sourceFieldName;
	
	@Getter @Setter
	private String sourceAliasName;
	
	@Getter @Setter
	private String pkPrefix;
	
	@Getter @Setter
	private Object concatValue;
	
	@Getter @Setter
	private List<ConcatField> concatCandidates;
	
	@Getter @Setter
	private List<Condition> caseWhenCandidates;
	
	@Getter @Setter
	private Boolean isNewLineRequired;
	

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 31 * ((StringUtils.isNotBlank(importFieldName) ? importFieldName.hashCode() : 0)
					+ (StringUtils.isNotBlank(sourceFieldName) ? sourceFieldName.hashCode() : 0)
					+ (StringUtils.isNotBlank(sourceAliasName) ? sourceAliasName.hashCode() : 0)
					+ (StringUtils.isNotBlank(pkPrefix) ? pkPrefix.hashCode() : 0)
					+ ((concatValue != null) ? concatValue.hashCode() : 0)
					+ (CommonUtil.isNotEmpty(concatCandidates) ? concatCandidates.hashCode() : 0)
					+ (CommonUtil.isNotEmpty(caseWhenCandidates) ? caseWhenCandidates.hashCode() : 0)
					+ ((isNewLineRequired != null) ? isNewLineRequired.hashCode() : 0));
					
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (obj instanceof QueryField) {
			
			QueryField qf = (QueryField) obj;
			
			Boolean listCondition = (this.concatCandidates.stream().filter(qf.concatCandidates()::contains).count() > 0);
			
			listCondition = listCondition && (this.caseWhenCandidates.stream().filter(qf.caseWhenCandidates()::contains).count() > 0);
			
			return listCondition && (StringUtils.stripToEmpty(importFieldName).equals(StringUtils.stripToEmpty(qf.importFieldName()))
					&& (StringUtils.stripToEmpty(sourceFieldName).equals(StringUtils.stripToEmpty(qf.sourceFieldName())))
					&& (StringUtils.stripToEmpty(sourceAliasName).equals(StringUtils.stripToEmpty(sourceAliasName())))
					&& (StringUtils.stripToEmpty(pkPrefix).equals(StringUtils.stripToEmpty(pkPrefix())))
					&& (concatValue == qf.concatValue())
					&& (isNewLineRequired == qf.isNewLineRequired()));
			
		}else {
			return false;
		}
	}
	
}
