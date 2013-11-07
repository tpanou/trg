package org.teiath.web.reports.excel;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Font;
import org.teiath.data.domain.trg.Listing;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.trg.ListingsExcelService;
import org.teiath.web.reports.common.ExcelToolkit;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

public class CreatedListingsReport {

	public HSSFWorkbook exportList(ListingsExcelService listingsExcelService, Date dateFrom, Date dateTo) {
		HSSFWorkbook wb = new HSSFWorkbook();
		int rowDataIndex = 1;
		int rowIndex = 1;

		HSSFSheet sheet = wb.createSheet("Λίστα Αγγελιών");

		HSSFRow row = sheet.createRow((short) 0);
		row.setHeightInPoints((float) 25);

		HSSFFont font = wb.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		font.setFontHeightInPoints((short) 9);

		ExcelToolkit excelToolkit = new ExcelToolkit();

		excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 0, "A/A", HSSFCellStyle.ALIGN_CENTER,
				HSSFCellStyle.VERTICAL_CENTER, false, true, HSSFColor.GREY_25_PERCENT.index);
		excelToolkit
				.createCell(ExcelToolkit.GENERIC, wb, row, font, 1, "Ημερομηνία Αγγελίας", HSSFCellStyle.ALIGN_CENTER,
						HSSFCellStyle.VERTICAL_CENTER, false, true, HSSFColor.GREY_25_PERCENT.index);
		excelToolkit
				.createCell(ExcelToolkit.GENERIC, wb, row, font, 2, "Κωδικός", HSSFCellStyle.ALIGN_CENTER,
						HSSFCellStyle.VERTICAL_CENTER, false, true, HSSFColor.GREY_25_PERCENT.index);
		excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 3, "Τιμή", HSSFCellStyle.ALIGN_CENTER,
				HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.GREY_25_PERCENT.index);
		excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 4, "Προϊόν", HSSFCellStyle.ALIGN_CENTER,
				HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.GREY_25_PERCENT.index);
		excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 5, "Κατηγορία", HSSFCellStyle.ALIGN_CENTER,
				HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.GREY_25_PERCENT.index);
		excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 6, "Κατάσταση", HSSFCellStyle.ALIGN_CENTER,
				HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.GREY_25_PERCENT.index);
		excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 7, "Είδος Συναλλαγής", HSSFCellStyle.ALIGN_CENTER,
				HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.GREY_25_PERCENT.index);
		excelToolkit
				.createCell(ExcelToolkit.GENERIC, wb, row, font, 8, "Ιδιοκτήτης Αγγελίας", HSSFCellStyle.ALIGN_CENTER,
						HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.GREY_25_PERCENT.index);

		sheet.setColumnWidth(0, 2000);
		sheet.setColumnWidth(1, 5000);
		sheet.setColumnWidth(2, 10000);
		sheet.setColumnWidth(3, 15000);
		sheet.setColumnWidth(4, 5000);
		sheet.setColumnWidth(5, 5000);
		sheet.setColumnWidth(6, 5000);
		sheet.setColumnWidth(7, 5000);

		font = wb.createFont();
		font.setFontHeightInPoints((short) 9);
		font.setBoldweight(Font.BOLDWEIGHT_NORMAL);

		try {
			Collection<Listing> listings = listingsExcelService.getCreatedListings(dateFrom, dateTo);

			for (Listing listing : listings) {
				row = sheet.createRow((short) rowDataIndex++);
				excelToolkit
						.createCell(ExcelToolkit.GENERIC, wb, row, font, 0, rowIndex++ + "", HSSFCellStyle.ALIGN_CENTER,
								HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.WHITE.index);
				excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 1,
						new SimpleDateFormat("dd/MM/yyyy").format(listing.getListingCreationDate()),
						HSSFCellStyle.ALIGN_CENTER, HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.WHITE.index);
				excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 2, listing.getCode(),
						HSSFCellStyle.ALIGN_LEFT, HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.WHITE.index);
				excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 3, listing.getPrice() != null? listing.getPrice().toString(): "",
						HSSFCellStyle.ALIGN_LEFT, HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.WHITE.index);
				excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 4, listing.getProductName(),
						HSSFCellStyle.ALIGN_LEFT, HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.WHITE.index);
				excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 5, listing.getProductCategory().getName(),
						HSSFCellStyle.ALIGN_CENTER, HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.WHITE.index);
				excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 6,
						listing.getProductStatus().getName().toString(), HSSFCellStyle.ALIGN_LEFT,
						HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.WHITE.index);
				excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 7, listing.getTransactionType().getName(),
						HSSFCellStyle.ALIGN_CENTER, HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.WHITE.index);
				excelToolkit.createCell(ExcelToolkit.GENERIC, wb, row, font, 8, listing.getUser().getFullName(),
						HSSFCellStyle.ALIGN_LEFT, HSSFCellStyle.VERTICAL_CENTER, true, true, HSSFColor.WHITE.index);
			}
		} catch (ServiceException e) {
			e.printStackTrace();
		}

		return wb;
	}
}
