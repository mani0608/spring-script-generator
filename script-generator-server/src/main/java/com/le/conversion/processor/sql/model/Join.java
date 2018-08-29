package com.le.conversion.processor.sql.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@ToString
public class Join {

	@Getter
	@Setter
	private String tableName;

	@Getter
	@Setter
	private String tableAlias;

	@Getter
	@Setter
	private List<Condition> joinConditions;

	public void initializeList() {
		this.joinConditions = new ArrayList<>();
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 31 * tableName.hashCode() + joinConditions.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub

		if (obj instanceof Join) {

			Join jn = (Join) obj;

			Boolean listCondition = (this.joinConditions.stream().filter(jn.getJoinConditions()::contains).count() > 0);

			return (listCondition && this.tableName.equals(jn.getTableName())
					&& this.tableAlias.equals(jn.getTableAlias()));

		} else {
			return false;
		}
	}

}
