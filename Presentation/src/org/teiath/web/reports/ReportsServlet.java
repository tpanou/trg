package org.teiath.web.reports;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.teiath.data.domain.trg.ProductCategory;
import org.teiath.data.domain.trg.TransactionType;
import org.teiath.service.trg.ListingsExcelService;
import org.teiath.web.reports.common.ExcelToolkit;
import org.teiath.web.reports.common.Report;
import org.teiath.web.reports.common.ReportType;
import org.teiath.web.reports.excel.CreatedListingsReport;
import org.teiath.web.reports.excel.ListingsByProductCategoryReport;
import org.teiath.web.reports.excel.ListingsByTransactionTypeReport;
import org.teiath.web.reports.excel.TransactionsReport;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

//import org.apache.commons.beanutils.PropertyUtils;

public class ReportsServlet
		extends HttpServlet {

	Map<String, Object> _parameters = null;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Map attributes = (HashMap) request.getSession().getAttribute("_window-attributes_");
		Map session = (HashMap) attributes.get(request.getParameter("zsessid"));
		Report report = (Report) session.get("REPORT");

		ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		DataSource dataSource = (DataSource) context.getBean("dataSource");
		Connection conn = null;

		try {
			Locale locale = new Locale("el", "GR");

			conn = DataSourceUtils.doGetConnection(dataSource);

			String reportFileName = getServletContext()
					.getRealPath(report.getReportLocation() + report.getRootReportFile());

			response.setCharacterEncoding("utf-8");
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + report.getOutputFileName() + "\"");
			ServletOutputStream servletOutputStream = response.getOutputStream();

			switch (report.getReportType()) {
				case ReportType.PDF:
					_parameters = report.getParameters();
					_parameters.put(JRParameter.REPORT_LOCALE, locale);
					_parameters.put("IMAGE_DIR", getServletContext().getRealPath(report.getImagesLocation()));
					_parameters.put("DATE_FROM", report.getDateFrom());
					_parameters.put("DATE_TO", report.getDateTo());
					_parameters.put("NUMBER_FROM", report.getNumberFrom());
					_parameters.put("NUMBER_TO", report.getNumberTo());
					_parameters.put("PRODUCT_CATEGORY_ID", report.getParameters().get("PRODUCT_CATEGORY_ID"));
					_parameters.put("TRANSACTION_TYPE_ID", report.getParameters().get("TRANSACTION_TYPE_ID"));
					_parameters.put("USER_ID", report.getUserId());

					JRPdfExporter pdfexporter = new JRPdfExporter();
					pdfexporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");

					JasperPrint jasperPrint;

					if (report.getListings() != null) {
						JRBeanCollectionDataSource jrbc = new JRBeanCollectionDataSource(report.getListings());
						jasperPrint = JasperFillManager.fillReport(reportFileName, _parameters, jrbc);
					} else if (report.getListingInterests() != null) {
						JRBeanCollectionDataSource jrbc = new JRBeanCollectionDataSource(report.getListingInterests());
						jasperPrint = JasperFillManager.fillReport(reportFileName, _parameters, jrbc);
					} else if (report.getTransactions() != null) {
						JRBeanCollectionDataSource jrbc = new JRBeanCollectionDataSource(report.getTransactions());
						jasperPrint = JasperFillManager.fillReport(reportFileName, _parameters, jrbc);
					} else if (report.getNotifications() != null) {
						JRBeanCollectionDataSource jrbc = new JRBeanCollectionDataSource(report.getNotifications());
						jasperPrint = JasperFillManager.fillReport(reportFileName, _parameters, jrbc);
					} else {
						jasperPrint = JasperFillManager.fillReport(reportFileName, _parameters, conn);
					}

					pdfexporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
					pdfexporter.setParameter(JRExporterParameter.OUTPUT_STREAM, servletOutputStream);
					pdfexporter.exportReport();

					break;

				case ReportType.EXCEL:

					HSSFWorkbook wb = null;
					switch (report.getExcelReport()) {
						case ExcelToolkit.CREATED_LISTINGS:
							wb = new CreatedListingsReport()
									.exportList((ListingsExcelService) context.getBean("listingsExcelService"),
											report.getDateFrom(), report.getDateTo());
							break;
						case ExcelToolkit.LISTINGS_BY_TRANSACTION_TYPE:
							wb = new ListingsByTransactionTypeReport()
									.exportList((ListingsExcelService) context.getBean("listingsExcelService"),
											(TransactionType) report.getParameters().get("TRANSACTION_TYPE"));
							break;
						case ExcelToolkit.LISTINGS_BY_PRODUCT_CATEGORY:
							wb = new ListingsByProductCategoryReport()
									.exportList((ListingsExcelService) context.getBean("listingsExcelService"),
											(ProductCategory) report.getParameters().get("PRODUCT_CATEGORY"));
							break;
						case ExcelToolkit.TRANSACTIONS:
							wb = new TransactionsReport()
									.exportList((ListingsExcelService) context.getBean("listingsExcelService"),
											report.getDateFrom(), report.getDateTo());
							break;
					}

					wb.write(servletOutputStream);
					break;
			}

			if (! conn.isClosed()) {
				servletOutputStream.flush();
				servletOutputStream.close();
			} else {
				response.setCharacterEncoding("utf-8");
				response.setContentType("text/html;charset=utf-8");
				response.getOutputStream().print("Database connection is not valid !!!");
			}
		} catch (Exception e) {
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			e.printStackTrace(printWriter);
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			response.getOutputStream().print(stringWriter.toString());
			System.out.println(stringWriter.toString());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
