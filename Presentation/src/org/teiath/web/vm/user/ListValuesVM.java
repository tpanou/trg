package org.teiath.web.vm.user;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.data.domain.trg.Currency;
import org.teiath.data.domain.trg.ProductCategory;
import org.teiath.data.domain.trg.ProductStatus;
import org.teiath.data.domain.trg.TransactionType;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.SearchCriteria;
import org.teiath.service.exceptions.DeleteViolationException;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.values.ListValuesService;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;

@SuppressWarnings("UnusedDeclaration")
public class ListValuesVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ListValuesVM.class.getName());

	@Wire("#paging")
	private Paging paging;

	@WireVariable
	private ListValuesService listValuesService;

	private SearchCriteria searchCriteria;
	private User user;
	private ListModelList<ProductCategory> productCategories;
	private ProductCategory selectedProductCategory;
	private ListModelList<ProductStatus> productStatuses;
	private ProductStatus selectedProductStatus;
	private ListModelList<TransactionType> transactionTypes;
	private TransactionType selectedTransactionType;
	private ListModelList<Currency> currencies;
	private Currency selectedCurrency;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
	}

	@Command
	public void onCreateActionCategories() {
		ZKSession.sendRedirect(PageURL.EVENT_CATEGORY_CREATE);
	}

	@Command
	public void onCreateAccommodationAttributes() {
		ZKSession.sendRedirect(PageURL.ACCOMMODATION_ATTRIBUTE_CREATE);
	}

	@Command
	public void onCreateAccommodationTypes() {
		ZKSession.sendRedirect(PageURL.ACCOMMODATION_TYPE_CREATE);
	}

	@Command
	public void onCreateProductCategories() {
		ZKSession.sendRedirect(PageURL.PRODUCT_CATEGORY_CREATE);
	}

	@Command
	public void onCreateCurrencies() {
		ZKSession.sendRedirect(PageURL.CURRENCY_CREATE);
	}

	@Command
	public void onCreateProductStatuses() {
		ZKSession.sendRedirect(PageURL.PRODUCT_STATUS_CREATE);
	}

	@Command
	public void onCreateTransactionTypes() {
		ZKSession.sendRedirect(PageURL.TRANSACTION_TYPE__CREATE);
	}

	@Command
	public void onEditProductCategories() {
		if (selectedProductCategory != null) {
			ZKSession.setAttribute("productCategoryId", selectedProductCategory.getId());
			ZKSession.sendRedirect(PageURL.PRODUCT_CATEGORY_EDIT);
		}
	}

	@Command
	public void onEditProductStatuses() {
		if (selectedProductStatus != null) {
			ZKSession.setAttribute("productStatusId", selectedProductStatus.getId());
			ZKSession.sendRedirect(PageURL.PRODUCT_STATUS_EDIT);
		}
	}

	@Command
	public void onEditTransactionTypes() {
		if (selectedTransactionType != null) {
			ZKSession.setAttribute("transactionTypeId", selectedTransactionType.getId());
			ZKSession.sendRedirect(PageURL.TRANSACTION_TYPE_EDIT);
		}
	}

	@Command
	public void onEditCurrencies() {
		if (selectedCurrency != null) {
			ZKSession.setAttribute("currencyId", selectedCurrency.getId());
			ZKSession.sendRedirect(PageURL.CURRENCY_EDIT);
		}
	}

	@Command
	public void onDeleteProductCategories() {
		if (selectedProductCategory != null) {
			Messagebox.show(Labels.getLabel("value.message.deleteQuestion"),
					Labels.getLabel("common.messages.delete_title"), Messagebox.YES | Messagebox.NO,
					Messagebox.QUESTION, new EventListener<Event>() {
				public void onEvent(org.zkoss.zk.ui.event.Event evt) {
					switch ((Integer) evt.getData()) {
						case Messagebox.YES:
							try {
								listValuesService.deleteProductCategory(selectedProductCategory);
								Messagebox.show(Labels.getLabel("value.message.deleteConfirmation"),
										Labels.getLabel("common.messages.confirm"), Messagebox.OK,
										Messagebox.INFORMATION, new EventListener<org.zkoss.zk.ui.event.Event>() {
									public void onEvent(org.zkoss.zk.ui.event.Event evt) {
										ZKSession.sendRedirect(PageURL.VALUES_LIST);
									}
								});
							} catch (DeleteViolationException e) {
								Messagebox.show(MessageBuilder
										.buildErrorMessage(e.getMessage(), Labels.getLabel("product.categories")),
										Labels.getLabel("common.messages.delete_title"), Messagebox.OK,
										Messagebox.ERROR);
							} catch (ServiceException e) {
								log.error(e.getMessage());
								Messagebox.show(MessageBuilder
										.buildErrorMessage(e.getMessage(), Labels.getLabel("product.categories")),
										Labels.getLabel("common.messages.delete_title"), Messagebox.OK,
										Messagebox.ERROR);
							}
							break;
						case Messagebox.NO:
							break;
					}
				}
			});
		}
	}

	@Command
	public void onDeleteProductStatuses() {
		if (selectedProductStatus != null) {
			Messagebox.show(Labels.getLabel("value.message.deleteQuestion"),
					Labels.getLabel("common.messages.delete_title"), Messagebox.YES | Messagebox.NO,
					Messagebox.QUESTION, new EventListener<Event>() {
				public void onEvent(org.zkoss.zk.ui.event.Event evt) {
					switch ((Integer) evt.getData()) {
						case Messagebox.YES:
							try {
								listValuesService.deleteProductStatus(selectedProductStatus);
								Messagebox.show(Labels.getLabel("value.message.deleteConfirmation"),
										Labels.getLabel("common.messages.confirm"), Messagebox.OK,
										Messagebox.INFORMATION, new EventListener<org.zkoss.zk.ui.event.Event>() {
									public void onEvent(org.zkoss.zk.ui.event.Event evt) {
										ZKSession.sendRedirect(PageURL.VALUES_LIST);
									}
								});
							} catch (DeleteViolationException e) {
								Messagebox.show(MessageBuilder
										.buildErrorMessage(e.getMessage(), Labels.getLabel("product.statuses")),
										Labels.getLabel("common.messages.delete_title"), Messagebox.OK,
										Messagebox.ERROR);
							} catch (ServiceException e) {
								log.error(e.getMessage());
								Messagebox.show(MessageBuilder
										.buildErrorMessage(e.getMessage(), Labels.getLabel("product.statuses")),
										Labels.getLabel("common.messages.delete_title"), Messagebox.OK,
										Messagebox.ERROR);
							}
							break;
						case Messagebox.NO:
							break;
					}
				}
			});
		}
	}

	@Command
	public void onDeleteTransactionTypes() {
		if (selectedTransactionType != null) {
			Messagebox.show(Labels.getLabel("value.message.deleteQuestion"),
					Labels.getLabel("common.messages.delete_title"), Messagebox.YES | Messagebox.NO,
					Messagebox.QUESTION, new EventListener<Event>() {
				public void onEvent(org.zkoss.zk.ui.event.Event evt) {
					switch ((Integer) evt.getData()) {
						case Messagebox.YES:
							try {
								listValuesService.deleteTransactionType(selectedTransactionType);
								Messagebox.show(Labels.getLabel("value.message.deleteConfirmation"),
										Labels.getLabel("common.messages.confirm"), Messagebox.OK,
										Messagebox.INFORMATION, new EventListener<org.zkoss.zk.ui.event.Event>() {
									public void onEvent(org.zkoss.zk.ui.event.Event evt) {
										ZKSession.sendRedirect(PageURL.VALUES_LIST);
									}
								});
							} catch (DeleteViolationException e) {
								log.error(e.getMessage());
								Messagebox.show(MessageBuilder
										.buildErrorMessage(e.getMessage(), Labels.getLabel("transaction.types")),
										Labels.getLabel("common.messages.delete_title"), Messagebox.OK,
										Messagebox.ERROR);
							} catch (ServiceException e) {
								log.error(e.getMessage());
								Messagebox.show(MessageBuilder
										.buildErrorMessage(e.getMessage(), Labels.getLabel("transaction.types")),
										Labels.getLabel("common.messages.delete_title"), Messagebox.OK,
										Messagebox.ERROR);
							}
							break;
						case Messagebox.NO:
							break;
					}
				}
			});
		}
	}

	@Command
	public void onDeleteCurrencies() {
		if (selectedCurrency != null) {
			Messagebox.show(Labels.getLabel("value.message.deleteQuestion"),
					Labels.getLabel("common.messages.delete_title"), Messagebox.YES | Messagebox.NO,
					Messagebox.QUESTION, new EventListener<Event>() {
				public void onEvent(org.zkoss.zk.ui.event.Event evt) {
					switch ((Integer) evt.getData()) {
						case Messagebox.YES:
							try {
								listValuesService.deleteCurrency(selectedCurrency);
								Messagebox.show(Labels.getLabel("value.message.deleteConfirmation"),
										Labels.getLabel("common.messages.confirm"), Messagebox.OK,
										Messagebox.INFORMATION, new EventListener<org.zkoss.zk.ui.event.Event>() {
									public void onEvent(org.zkoss.zk.ui.event.Event evt) {
										ZKSession.sendRedirect(PageURL.VALUES_LIST);
									}
								});
							} catch (DeleteViolationException e) {
								log.error(e.getMessage());
								Messagebox.show(MessageBuilder
										.buildErrorMessage(e.getMessage(), Labels.getLabel("transaction.types")),
										Labels.getLabel("common.messages.delete_title"), Messagebox.OK,
										Messagebox.ERROR);
							} catch (ServiceException e) {
								log.error(e.getMessage());
								Messagebox.show(MessageBuilder
										.buildErrorMessage(e.getMessage(), Labels.getLabel("transaction.types")),
										Labels.getLabel("common.messages.delete_title"), Messagebox.OK,
										Messagebox.ERROR);
							}
							break;
						case Messagebox.NO:
							break;
					}
				}
			});
		}
	}

	@Command
	@NotifyChange("selectedProductCategory")
	public void onPaging() {
		if (productCategories != null) {
			searchCriteria.setPageNumber(paging.getActivePage());
			try {
				SearchResults<ProductCategory> results = listValuesService
						.searchProductCategoriesByCriteria(searchCriteria);
				selectedProductCategory = null;
				productCategories.clear();
				productCategories.addAll(results.getData());
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(searchCriteria.getPageNumber());
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("action.theme")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}
	}

	@Command
	@NotifyChange
	public void onSort(BindContext ctx) {
		Event event = ctx.getTriggerEvent();
		Listheader listheader = (Listheader) event.getTarget();

		searchCriteria.setOrderField(listheader.getId());
		searchCriteria.setOrderDirection(listheader.getSortDirection());
		searchCriteria.setPageNumber(0);
		selectedProductCategory = null;
		productCategories.clear();

		try {
			SearchResults<ProductCategory> results = listValuesService
					.searchProductCategoriesByCriteria(searchCriteria);
			selectedProductCategory = null;
			productCategories.clear();
			productCategories.addAll(results.getData());
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(searchCriteria.getPageNumber());
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("action.theme")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	public ListModelList<ProductCategory> getProductCategories() {
		if (productCategories == null) {
			productCategories = new ListModelList<>();
			//Initial search criteria
			searchCriteria = new SearchCriteria();
			searchCriteria.setPageSize(paging.getPageSize());
			searchCriteria.setPageNumber(0);
			searchCriteria.setOrderField("parentCategoryId");
			searchCriteria.setOrderDirection("ascending");
			try {
				SearchResults<ProductCategory> results = listValuesService
						.searchProductCategoriesByCriteria(searchCriteria);
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(searchCriteria.getPageNumber());
				productCategories.addAll(results.getData());
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("action.theme")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}

		return productCategories;
	}

	public ListModelList<ProductStatus> getProductStatuses() {
		if (productStatuses == null) {
			productStatuses = new ListModelList<>();
			try {
				productStatuses.addAll(listValuesService.getProductStatuses());
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("action.theme")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}

		return productStatuses;
	}

	public ListModelList<Currency> getCurrencies() {
		if (currencies == null) {
			currencies = new ListModelList<>();
			try {
				currencies.addAll(listValuesService.getCurrencies());
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("action.theme")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}

		return currencies;
	}

	public ListModelList<TransactionType> getTransactionTypes() {
		if (transactionTypes == null) {
			transactionTypes = new ListModelList<>();
			try {
				transactionTypes.addAll(listValuesService.getTransactionTypes());
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("action.theme")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}

		return transactionTypes;
	}

	public User getLoggedUser() {
		return loggedUser;
	}

	public void setLoggedUser(User loggedUser) {
		this.loggedUser = loggedUser;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public ProductCategory getSelectedProductCategory() {
		return selectedProductCategory;
	}

	public void setSelectedProductCategory(ProductCategory selectedProductCategory) {
		this.selectedProductCategory = selectedProductCategory;
	}

	public ProductStatus getSelectedProductStatus() {
		return selectedProductStatus;
	}

	public void setSelectedProductStatus(ProductStatus selectedProductStatus) {
		this.selectedProductStatus = selectedProductStatus;
	}

	public Currency getSelectedCurrency() {
		return selectedCurrency;
	}

	public void setSelectedCurrency(Currency selectedCurrency) {
		this.selectedCurrency = selectedCurrency;
	}

	public TransactionType getSelectedTransactionType() {
		return selectedTransactionType;
	}

	public void setSelectedTransactionType(TransactionType selectedTransactionType) {
		this.selectedTransactionType = selectedTransactionType;
	}

	public SearchCriteria getSearchCriteria() {
		return searchCriteria;
	}

	public void setSearchCriteria(SearchCriteria searchCriteria) {
		this.searchCriteria = searchCriteria;
	}
}
