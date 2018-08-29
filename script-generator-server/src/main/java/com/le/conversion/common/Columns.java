package com.le.conversion.common;

import java.util.EnumSet;

public enum Columns {
	SOURCE_TABLE("SourceTable"),
	SOURCE_COLUMN("SourceColumn"),
	DEST_COLUMN("DestinationColumn"),
	DEST_TABLE("DestinationTable"),
	DEST_VALUE("DestinationValue"),
	JOIN_TABLE_COLUMN("JoinTable"),
	CONDITIONS("Conditions"),
	SELECT("SELECT"),
	PK_ALIAS("ID"),
	TRID_ALIAS("TRID"),
	INSERT_COL_ID("id"),
	INSERT_COL_TRID("transaction_id"),
	SRC_TBL_PFX("s$"),
	DEST_TBL_PFX("t$");
	
	public static final EnumSet<Columns> RELTBLPFXS = EnumSet.of(SRC_TBL_PFX, DEST_TBL_PFX);
	
	private String columnName;
	
	Columns(String columnName) {
		this.columnName = columnName;
	}
	
	public String col() {
		return this.columnName;
	}
}
