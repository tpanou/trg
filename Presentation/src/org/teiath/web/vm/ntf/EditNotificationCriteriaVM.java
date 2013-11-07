package org.teiath.web.vm.ntf;

import org.apache.log4j.Logger;
import org.teiath.data.domain.trg.ProductCategory;
import org.teiath.data.domain.trg.ProductNotificationCriteria;
import org.teiath.data.domain.trg.ProductStatus;
import org.teiath.data.domain.trg.TransactionType;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.trg.EditProductNotificationCriteriaService;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;

@SuppressWarnings("UnusedDeclaration")
public class EditNotificationCriteriaVM
		extends BaseVM {

	static Logger log = Logger.getLogger(EditNotificationCriteriaVM.class.getName());

	@Wire("#parentCategoryCombo")
	private Combobox parentCategoryCombo;
	@Wire("#appliances")
	private Comboitem appliances;
	@Wire("#electronics")
	private Comboitem electronics;
	@Wire("#furniture")
	private Comboitem furniture;
	@Wire("#books")
	private Comboitem books;
	@Wire("#various")
	private Comboitem various;

	@WireVariable
	private EditProductNotificationCriteriaService editProductNotificationCriteriaService;

	private ProductNotificationCriteria productNotificationCriteria;

	private ListModelList<ProductCategory> categories;
	private ListModelList<TransactionType> transactionTypes;
	private ListModelList<ProductStatus> productStatuses;
	private ProductCategory selectedCategory;
	private TransactionType selectedTransactionType;
	private ProductStatus selectedProductStatus;

	@AfterCompose
	@NotifyChange("productNotificationCriteria")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		productNotificationCriteria = new ProductNotificationCriteria();
		categories = new ListModelList<>();
		try {
			productNotificationCriteria = editProductNotificationCriteriaService
					.getProductNotificationCriteriaById((Integer) ZKSession.getAttribute("notificationCriteriaId"));
			selectedCategory = productNotificationCriteria.getProductCategory();
			getProductStatuses();
			getTransactionTypes();

			if (productNotificationCriteria.getProductCategory() != null) {
				categories.addAll(editProductNotificationCriteriaService.getProductCategoriesByParentCategoryId(
						productNotificationCriteria.getProductCategory().getParentCategoryId()));


				switch (productNotificationCriteria.getProductCategory().getParentCategoryId()) {
					case 1:
						parentCategoryCombo.setSelectedItem(appliances);
						break;
					case 2:
						parentCategoryCombo.setSelectedItem(electronics);
						break;
					case 3:
						parentCategoryCombo.setSelectedItem(furniture);
						break;
					case 4:
						parentCategoryCombo.setSelectedItem(books);
						break;
					case 5:
						parentCategoryCombo.setSelectedItem(various);
						break;
				}
			}
			selectedTransactionType = productNotificationCriteria.getTransactionType();
			selectedProductStatus = productNotificationCriteria.getProductStatus();
		} catch (ServiceException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("notifications.criteria")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			log.error(e.getMessage());
			ZKSession.sendRedirect(PageURL.NOTIFICATION_CRITERIA_LIST);
		}
	}

	@Command
	public void onSave() {

		if (selectedCategory != null) {
			productNotificationCriteria.setProductCategory(selectedCategory.getId() != - 1 ? selectedCategory : null);
		}
		if (selectedTransactionType != null) {
			productNotificationCriteria
					.setTransactionType(selectedTransactionType.getId() != - 1 ? selectedTransactionType : null);
		}
		if (selectedProductStatus != null) {
			productNotificationCriteria
					.setProductStatus(selectedProductStatus.getId() != - 1 ? selectedProductStatus : null);
		}
		try {
			editProductNotificationCriteriaService.saveProductNotificationCriteria(productNotificationCriteria);
			Messagebox.show(Labels.getLabel("notifications.message.success"),
					Labels.getLabel("common.messages.save_title"), Messagebox.OK, Messagebox.INFORMATION,
					new EventListener<Event>() {
						public void onEvent(Event evt) {
							ZKSession.sendRedirect(PageURL.NOTIFICATION_CRITERIA_LIST);
						}
					});
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("notifications")),
					Labels.getLabel("common.messages.save_title"), Messagebox.OK, Messagebox.ERROR);
			ZKSession.sendRedirect(PageURL.NOTIFICATION_CRITERIA_LIST);
		}
	}

	@Command
	public void onCancel() {
		ZKSession.sendRedirect(PageURL.NOTIFICATION_CRITERIA_LIST);
	}

	@Command
	@NotifyChange("categories")
	public void onSelectParentCategory() {
		Integer parentCategoryId = parentCategoryCombo.getSelectedItem().getIndex() + 1;
		getCategoriesByParentCategoryId(parentCategoryId);
	}

	public ListModelList<ProductCategory> getCategoriesByParentCategoryId(Integer parentCategoryId) {
		categories = new ListModelList<>();
		try {
			categories.addAll(editProductNotificationCriteriaService
					.getProductCategoriesByParentCategoryId(parentCategoryId));
		} catch (ServiceException e) {
			Messagebox
					.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listing.productCategory")),
							Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			log.error(e.getMessage());
		}

		return categories;
	}

	public ListModelList<ProductCategory> getCategories() {
		return categories;
	}

	public void setCategories(ListModelList<ProductCategory> categories) {
		this.categories = categories;
	}

	public ListModelList<TransactionType> getTransactionTypes() {
		if (transactionTypes == null) {
			transactionTypes = new ListModelList<>();
			try {
				transactionTypes.addAll(editProductNotificationCriteriaService.getTransactionTypes());
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder
						.buildErrorMessage(e.getMessage(), Labels.getLabel("listing.transactionType")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
				log.error(e.getMessage());
			}
		}

		return transactionTypes;
	}

	public ListModelList<ProductStatus> getProductStatuses() {
		if (productStatuses == null) {
			productStatuses = new ListModelList<>();
			try {
				productStatuses.addAll(editProductNotificationCriteriaService.getProductStatuses());
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder
						.buildErrorMessage(e.getMessage(), Labels.getLabel("listing.productCategory")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}

		return productStatuses;
	}

	public ProductNotificationCriteria getProductNotificationCriteria() {
		return productNotificationCriteria;
	}

	public void setProductNotificationCriteria(ProductNotificationCriteria productNotificationCriteria) {
		this.productNotificationCriteria = productNotificationCriteria;
	}

	public ProductCategory getSelectedCategory() {
		return selectedCategory;
	}

	public void setSelectedCategory(ProductCategory selectedCategory) {
		this.selectedCategory = selectedCategory;
	}

	public TransactionType getSelectedTransactionType() {
		return selectedTransactionType;
	}

	public void setSelectedTransactionType(TransactionType selectedTransactionType) {
		this.selectedTransactionType = selectedTransactionType;
	}

	public ProductStatus getSelectedProductStatus() {
		return selectedProductStatus;
	}

	public void setSelectedProductStatus(ProductStatus selectedProductStatus) {
		this.selectedProductStatus = selectedProductStatus;
	}
}
