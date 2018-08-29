package com.le.conversion.loader.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.le.conversion.common.Columns;
import com.le.conversion.common.Constants;
import com.le.conversion.common.exceptions.ExceptionWrapper;
import com.le.conversion.common.util.CommonUtil;
import com.le.conversion.model.DestinationColumns;
import com.le.conversion.model.DestinationTable;
import com.le.conversion.model.DestinationTableMapping;
import com.le.conversion.model.MappingProperties;
import com.le.conversion.model.Mappings;
import com.le.conversion.model.SourceTable;
import com.le.conversion.model.SourceTableMapping;

public class LoaderUtil {

	private static XSSFWorkbook workbook;
	private static XSSFSheet mappingSheet;
	private static String mappingFilePath;
	private static DataFormatter dataFormatter;
	private static Mappings mappingData;
	private static Map<String, Integer> hdrs;
	private static List<SourceTable> sourceTablesImported;

	public static Mappings readFile(String filePath) throws InvalidFormatException, IOException {

		mappingFilePath = filePath;
		
		File mappingFile = new File(mappingFilePath);

		init(mappingFile);

		load();

		return mappingData;

	}
	
	public static Mappings readFile(File mappingFile) throws InvalidFormatException, IOException {

		init(mappingFile);

		load();

		return mappingData;

	}

	private static void init(File mappingFile) throws InvalidFormatException, IOException {

		workbook = new XSSFWorkbook(mappingFile);
		mappingSheet = workbook.getSheet("Mapping");
		hdrs = new ConcurrentHashMap<>();
		sourceTablesImported = new ArrayList<>();
		mappingData = Mappings.builder().build();

		if (mappingSheet == null)
			mappingSheet = workbook.getSheetAt(0);

		dataFormatter = new DataFormatter();

		// Identify column headers
		Row row = mappingSheet.getRow(0);

		row.forEach(cell -> {
			if (StringUtils.isNotBlank(cell.getStringCellValue())) {
				hdrs.put(getFormattedHeader(cell), cell.getColumnIndex());
			}
		});

		// Read only distinct source tables/Tables to be imported
		readSourceTables();

	}

	private static String getFormattedHeader(Cell cell) {

		String cellValue = dataFormatter.formatCellValue(cell);

		cellValue = StringUtils.remove(cellValue, StringUtils.SPACE);

		return StringUtils.strip(cellValue);

	}

	private static void load() {

		sourceTablesImported.stream().forEach(table -> {

			SourceTableMapping stMapping = SourceTableMapping.builder().sourceTable(table.getName()).build();

			table.getIndexes().forEach(index -> {

				XSSFRow sRow = getRow(index);

				if (isPrimayKeyRow(sRow)) {

					stMapping.setPrimaryKeyCol(extractKey(getCellValue(sRow, Columns.SOURCE_COLUMN.col())));

				} else {

					List<DestinationTableMapping> dtMappings = new ArrayList<>();

					table.getSortedDestTableIndexes().forEach(dt -> {

						DestinationTableMapping dtMapping = DestinationTableMapping.builder()
								.destinationTable(dt.getName()).build();

						List<MappingProperties> mappingProps = new ArrayList<>();

						dt.getIndexes().stream().forEach(dtIndex -> {

							XSSFRow dRow = getRow(dtIndex);

							if (isNonRelMultiDestCol(dRow)) {

								getDestColumnList(dRow).stream().forEach(destCol -> {

									mappingProps.add(MappingProperties.builder()
											.sourceColumn(
													getFormattedColumn(getCellValue(dRow, Columns.SOURCE_COLUMN.col())))
											.destColumns(createDestinationColumn(destCol))
											.destValue(getCellValue(dRow, Columns.DEST_VALUE.col()))
											.joinTable(getCellValue(dRow, Columns.JOIN_TABLE_COLUMN.col()))
											.conditions(getCellValue(dRow, Columns.CONDITIONS.col()))
											.isProcessed(false).build());

								});

							} else {

								mappingProps.add(MappingProperties.builder()
										.sourceColumn(
												getFormattedColumn(getCellValue(dRow, Columns.SOURCE_COLUMN.col())))
										.destColumns(
												createDestinationColumn(getCellValue(dRow, Columns.DEST_COLUMN.col())))
										.destValue(getCellValue(dRow, Columns.DEST_VALUE.col()))
										.joinTable(getCellValue(dRow, Columns.JOIN_TABLE_COLUMN.col()))
										.conditions(getCellValue(dRow, Columns.CONDITIONS.col()))
										.isProcessed(false).build());
							}

						});

						dtMapping.setMappingProps(mappingProps);

						dtMappings.add(dtMapping);

					});

					stMapping.setDestTableMappings(dtMappings);
				}

			});

			mappingData.addMapping(stMapping);

		});

	}

	private static XSSFRow getRow(Integer index) {
		return mappingSheet.getRow(index);
	}

	private static List<String> getDestTableList(Row row) {

		String destTables = getCellValue(row, Columns.DEST_TABLE.col());

		return CommonUtil.getValueAsList(destTables, Constants.COMMA.value());
	}

	private static List<String> getDestColumnList(Row row) {

		String destColumns = getCellValue(row, Columns.DEST_COLUMN.col());

		return CommonUtil.getValueAsList(destColumns, Constants.COMMA.value());
	}

	private static Boolean isNonRelMultiDestCol(Row row) {

		String destColumns = getCellValue(row, Columns.DEST_COLUMN.col());

		if (CommonUtil.hasMultiple(destColumns)) {
			if (!destColumns.contains(Columns.SRC_TBL_PFX.col())
					&& !destColumns.contains(Columns.DEST_TBL_PFX.col())) {
				return true;
			}
		}

		return false;

	}

	private static String getCellValue(Row row, String colName) {

		Cell cell = row.getCell(hdrs.get(colName));
		return StringUtils.strip(dataFormatter.formatCellValue(cell));

	}

	/**
	 * Read source tables and its corresponding destination table indexes
	 */
	private static void readSourceTables() {

		mappingSheet.forEach(ExceptionWrapper.handleConsumer(row -> {

			if (row.getRowNum() > 0) {

				String stName = StringUtils.strip(dataFormatter.formatCellValue(row.getCell(0)));

				SourceTable sTable;

				if (!CommonUtil.contains(sourceTablesImported, "name", stName)) {

					sTable = SourceTable.builder().name(stName).build();

					sTable.addIndex(row.getRowNum());

					sTable.setDestTableIndexes(new ArrayList<>());

					sourceTablesImported.add(sTable);

				} else {

					sTable = sourceTablesImported.stream().filter(st -> StringUtils.equals(st.getName(), stName))
							.findFirst().get();

					sTable.getIndexes().add(row.getRowNum());

				}

				if (!isPrimayKeyRow(row)) {

					List<String> splitTables = getDestTableList(row);

					splitTables.stream().forEach(ExceptionWrapper.handleConsumer(dtName -> {

						List<DestinationTable> destTableMappings = sTable.getDestTableIndexes();

						if (destTableMappings != null
								&& !CommonUtil.contains(destTableMappings, "name", StringUtils.strip(dtName))) {

							DestinationTable dTable = DestinationTable.builder().name(StringUtils.strip(dtName))
									.build();

							dTable.addIndex(row.getRowNum());

							sTable.addDestTableIndex(dTable);

						} else {

							DestinationTable dTable = sTable.getDestTableIndexes().stream()
									.filter(dt -> StringUtils.equalsIgnoreCase(dt.getName(), dtName)).findFirst().get();

							dTable.getIndexes().add(row.getRowNum());

						}
					}));
				}
			}
		}));

	}

	private static String extractKey(String cellValue) {

		String key = StringUtils.remove(cellValue, Constants.KEY_PH.value());

		return getFormattedColumn(StringUtils.strip(key));

	}

	private static Boolean isPrimayKeyRow(Row row) {

		String cellValue = getCellValue(row, Columns.SOURCE_COLUMN.col());

		return (StringUtils.contains(cellValue, Constants.KEY_PH.value()));
	}

	private static String getFormattedColumn(String cellValue) {

		StringBuilder builder = new StringBuilder(cellValue);

		if (!cellValue.startsWith(Constants.SBRACKET.value())) {
			builder.insert(0, Constants.SBRACKET.value());
		}

		if (!cellValue.endsWith(Constants.EBRACKET.value())) {
			builder.append(Constants.EBRACKET.value());
		}

		return builder.toString();
	}

	private static DestinationColumns<String> createDestinationColumn(String columns) {

		DestinationColumns<String> dColumns = new DestinationColumns<>();

		List<String> splitColumns = Arrays.asList(columns.split(Constants.COMMA.value()));

		splitColumns.forEach(c -> {
			if (!c.endsWith(Constants.UNDERSCORE.value())) {
				c = c + Constants.UNDERSCORE.value();
			}

			dColumns.getColumns().add(c);
		});

		return dColumns;
	}

}
