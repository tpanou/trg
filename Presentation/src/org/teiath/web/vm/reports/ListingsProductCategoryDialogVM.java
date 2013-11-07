package org.teiath.web.vm.reports;

import org.apache.log4j.Logger;
import org.teiath.data.domain.trg.ProductCategory;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.trg.ListingsProductCategoryDialogService;
import org.teiath.web.reports.common.ExcelToolkit;
import org.teiath.web.reports.common.Report;
import org.teiath.web.reports.common.ReportToolkit;
import org.teiath.web.reports.common.ReportType;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;

@SuppressWarnings("UnusedDeclaration")
public class ListingsProductCategoryDialogVM {

	static Logger log = Logger.getLogger(ListingsProductCategoryDialogVM.class.getName());

	@WireVariable
	private ListingsProductCategoryDialogService listingsProductCategoryDialogService;

	private ListModelList<ProductCategory> categories;
	private ProductCategory selectedCategory;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
	}

	@Command
	public void onPrintPDF() {
		if (selectedCategory != null) {
			Report report = ReportToolkit
					.requestListingsProductCategoryReport(selectedCategory.getId(), ReportType.PDF);
			ZKSession.setAttribute("REPORT", report);
			ZKSession.sendPureRedirect(
					"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(), "_self");
		} else {
			Messagebox
					.show("Μη έγκυρη κατηγορία προϊόντος", Labels.getLabel("common.messages.read_title"), Messagebox.OK,
							Messagebox.ERROR);
		}
	}

	@Command
	public void onPrintXLS() {
		if (selectedCategory != null) {
			Report report = ReportToolkit.requestListingsProductCategoryReportXLS(selectedCategory, ReportType.EXCEL);
			report.setExcelReport(ExcelToolkit.LISTINGS_BY_PRODUCT_CATEGORY);
			ZKSession.setAttribute("REPORT", report);
			ZKSession.sendPureRedirect(
					"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(), "_self");
		} else {
			Messagebox.show("Μη έγκυρη ημερομηνία", Labels.getLabel("common.messages.read_title"), Messagebox.OK,
					Messagebox.ERROR);
		}
	}

	@Command
	public void onCancel() {
		//		ZKSession.sendRedirect(PageURL.ROUTE_LIST);
	}

	public ListModelList<ProductCategory> getCategories() {
		if (categories == null) {
			categories = new ListModelList<>();
			selectedCategory = new ProductCategory();
			selectedCategory.setId(- 1);
			selectedCategory.setName("");
			categories.add(selectedCategory);
			try {
				categories.addAll(listingsProductCategoryDialogService.getProductCategories());
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox.show(MessageBuilder
						.buildErrorMessage(e.getMessage(), Labels.getLabel("listing.productCategory")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}

		return categories;
	}

	public ProductCategory getSelectedCategory() {
		return selectedCategory;
	}

	public void setSelectedCategory(ProductCategory selectedCategory) {
		this.selectedCategory = selectedCategory;
	}
}
