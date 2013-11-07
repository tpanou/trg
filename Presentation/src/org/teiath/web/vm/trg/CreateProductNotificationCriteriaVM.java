package org.teiath.web.vm.trg;

import org.apache.log4j.Logger;
import org.teiath.data.domain.trg.ProductCategory;
import org.teiath.data.domain.trg.ProductNotificationCriteria;
import org.teiath.data.domain.trg.ProductStatus;
import org.teiath.data.domain.trg.TransactionType;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.trg.CreateProductNotificationCriteriaService;
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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;

@SuppressWarnings("UnusedDeclaration")
public class CreateProductNotificationCriteriaVM
		extends BaseVM {

	static Logger log = Logger.getLogger(CreateProductNotificationCriteriaVM.class.getName());

	@Wire("#categoriesHbox")
	private Hbox categoriesHbox;
	@Wire("#parentCategoryCombo")
	private Combobox parentCategoryCombo;

	@WireVariable
	private CreateProductNotificationCriteriaService createProductNotificationCriteriaService;

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
		productNotificationCriteria.setUser(loggedUser);
	}

	@Command
	public void onSave() {

		if (selectedCategory != null) {
			productNotificationCriteria.setProductCategory(selectedCategory.getId() != - 1 ? selectedCategory : null);
		}
		productNotificationCriteria
				.setTransactionType(selectedTransactionType.getId() != - 1 ? selectedTransactionType : null);
		productNotificationCriteria
				.setProductStatus(selectedProductStatus.getId() != - 1 ? selectedProductStatus : null);
		try {
			createProductNotificationCriteriaService.saveProductNotificationCriteria(productNotificationCriteria);
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
		categoriesHbox.setVisible(true);
		Integer parentCategoryId = parentCategoryCombo.getSelectedItem().getIndex() + 1;
		getCategoriesByParentCategoryId(parentCategoryId);
	}

	public ListModelList<ProductCategory> getCategoriesByParentCategoryId(Integer parentCategoryId) {
		categories = new ListModelList<>();
		try {
			categories.addAll(createProductNotificationCriteriaService
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
			selectedTransactionType = new TransactionType();
			selectedTransactionType.setId(- 1);
			selectedTransactionType.setName("");
			selectedTransactionType.setCode("-1");
			transactionTypes.add(selectedTransactionType);
			try {
				transactionTypes.addAll(createProductNotificationCriteriaService.getTransactionTypes());
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
			selectedProductStatus = new ProductStatus();
			selectedProductStatus.setId(- 1);
			selectedProductStatus.setName("");
			productStatuses.add(selectedProductStatus);
			try {
				productStatuses.addAll(createProductNotificationCriteriaService.getProductStatuses());
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder
						.buildErrorMessage(e.getMessage(), Labels.getLabel("listing.productStatus")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
				log.error(e.getMessage());
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
