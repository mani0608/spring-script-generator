package com.le.conversion.processor.sql.model;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.le.conversion.common.util.CommonUtil;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@ToString
public class Condition {

	@Getter
	@Setter
	private String field;

	@Getter
	@Setter
	private List<String> inCondition;

	@Getter
	@Setter
	private List<String> notInCondition;

	@Getter
	@Setter
	private String likeCondition;

	@Getter
	@Setter
	private String notLikeCondition;

	@Getter
	@Setter
	private String eqCondition;

	@Getter
	@Setter
	private String neqCondition;

	@Getter
	@Setter
	private Boolean isNullCondition;

	@Getter
	@Setter
	private Boolean isNotNullCondition;

	@Getter
	@Setter
	private Boolean isFieldRef;

	@Getter
	@Setter
	private String activeCondition;

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 31 * field.hashCode() + (CommonUtil.isNotEmpty(inCondition) ? inCondition.hashCode() : 0)
				+ (CommonUtil.isNotEmpty(notInCondition) ? notInCondition.hashCode() : 0)
				+ (StringUtils.isNotBlank(likeCondition) ? likeCondition.hashCode() : 0)
				+ (StringUtils.isNotBlank(notLikeCondition) ? notLikeCondition.hashCode() : 0)
				+ (StringUtils.isNotBlank(eqCondition) ? eqCondition.hashCode() : 0)
				+ (StringUtils.isNotBlank(neqCondition) ? neqCondition.hashCode() : 0)
				+ ((isNullCondition != null) ? isNullCondition.hashCode() : 0)
				+ ((isNotNullCondition != null) ? isNotNullCondition.hashCode() : 0)
				+ ((isFieldRef != null) ? isFieldRef.hashCode() : 0)
				+ (StringUtils.isNotBlank(activeCondition) ? activeCondition.hashCode() : 0);
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (obj instanceof Condition) {

			Condition cond = (Condition) obj;

			Boolean listCondition = (CommonUtil.isEmpty(this.inCondition) && CommonUtil.isEmpty(cond.getInCondition()));

			if (!listCondition) {
				if (CommonUtil.isNotEmpty(this.inCondition) && CommonUtil.isNotEmpty(cond.getInCondition())) {
					listCondition = (this.inCondition.stream().filter(cond.getInCondition()::contains).count() > 0);
				} else {
					listCondition = false;
				}
			}
			
			listCondition = listCondition && (CommonUtil.isEmpty(this.notInCondition) && CommonUtil.isEmpty(cond.getNotInCondition()));
			
			if (!listCondition) {
				if (CommonUtil.isNotEmpty(this.notInCondition) && CommonUtil.isNotEmpty(cond.getNotInCondition())) {
					listCondition = (this.notInCondition.stream().filter(cond.getNotInCondition()::contains).count() > 0);
				} else {
					listCondition = false;
				}
			}

			return (listCondition && this.field.equals(cond.getField())
					&& StringUtils.stripToEmpty(this.likeCondition)
							.equals(StringUtils.stripToEmpty(cond.getLikeCondition()))
					&& StringUtils.stripToEmpty(this.notLikeCondition)
							.equals(StringUtils.stripToEmpty(cond.getNotLikeCondition()))
					&& StringUtils.stripToEmpty(this.eqCondition)
							.equals(StringUtils.stripToEmpty(cond.getEqCondition()))
					&& StringUtils.stripToEmpty(this.neqCondition)
							.equals(StringUtils.stripToEmpty(cond.getNeqCondition()))
					&& this.isNullCondition == cond.getIsNullCondition()
					&& this.isNotNullCondition == cond.getIsNotNullCondition()
					&& this.isFieldRef == cond.getIsFieldRef() && StringUtils.stripToEmpty(this.activeCondition)
							.equals(StringUtils.stripToEmpty(cond.getActiveCondition())));

		} else {
			return false;
		}
	}

}
