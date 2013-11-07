package org.teiath.web.vm.trg;

import org.apache.log4j.Logger;
import org.teiath.data.domain.image.ListingMainImage;
import org.teiath.data.domain.trg.*;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.trg.CreateListingService;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.annotation.*;
import org.zkoss.image.AImage;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;

import java.math.BigDecimal;

@SuppressWarnings("UnusedDeclaration")
public class CreateListingVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ListListingsVM.class.getName());

	@Wire("#createListingWin")
	private Window win;
	@Wire("#uploadButton")
	private Button uploadButton;
	@Wire("#btnDelete")
	private Button deleteButton;
	@Wire("#btnView")
	private Button viewButton;
	@Wire("#photosListbox")
	private Listbox photosListbox;
	@Wire("#priceRow")
	private Row priceRow;
	@Wire("#exchangeRow")
	private Row exchangeRow;
	@Wire("#photoVBox")
	private Vbox photoVBox;
	@Wire("#tradeableGoodNameBox")
	private Textbox tradeableGoodNameBox;
	@Wire("#categoriesHbox")
	private Hbox categoriesHbox;
	@Wire("#parentCategoryCombo")
	private Combobox parentCategoryCombo;
	@Wire("#childCategoryCombo")
	private Combobox childCategoryCombo;
	@Wire("#photoTab")
	private Tab photoTab;
	@Wire("#photoLayout")
	private Vlayout photoLayout;

	@WireVariable
	private CreateListingService createListingService;

	private Listing listing;
	private ListModelList<ProductCategory> categories;
	private ListModelList<TransactionType> transactionTypes;
	private ListModelList<ProductStatus> productStatuses;
	private ListModelList<Currency> currencies;
	private ListModelList<AImage> uploadedImages;
	private TransactionType selectedTransactionType;
	private Currency defaultCurrency;
	private AImage mainImage;
	private ListModelList<TradeableGood> tradeableGoods;
	private TradeableGood selectedGood;
	private ProductCategory selectedCategory;

	@AfterCompose
	@NotifyChange("listing")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		if ((Executions.getCurrent().getUserAgent().contains("Android")) || (Executions.getCurrent().getUserAgent()
				.contains("iPhone")) || (Executions.getCurrent().getUserAgent().contains("iPad"))) {

			photoLayout.setVisible(false);
			photoTab.setVisible(false);
		}

		Image imageComponent = new Image();
		imageComponent.setWidth("300px");
		imageComponent.setHeight("300px");
		imageComponent.setSrc("/img/noImage.jpg");
		photoVBox.appendChild(imageComponent);

		uploadedImages = new ListModelList<>();
		currencies = new ListModelList<>();
		tradeableGoods = new ListModelList<>();
		try {
			currencies.addAll(createListingService.getCurrencies());
			listing = new Listing();
			listing.setUser(loggedUser);
			listing.setEnabled(true);
			listing.setPrice(BigDecimal.ZERO);
			defaultCurrency = createListingService.getDefaultCurrency();
			if (defaultCurrency != null) {
				listing.setCurrency(createListingService.getDefaultCurrency());
			}
		} catch (ServiceException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listing.price")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	public void onImageUpload(
			@ContextParam(ContextType.BIND_CONTEXT)
			BindContext ctx) {
		UploadEvent upEvent = null;
		Object objUploadEvent = ctx.getTriggerEvent();
		if (objUploadEvent != null && (objUploadEvent instanceof UploadEvent)) {
			upEvent = (UploadEvent) objUploadEvent;
		}
		if ((upEvent != null) && (uploadedImages.getSize() < 4)) {
			Media media = upEvent.getMedia();
			uploadedImages.add((AImage) media);
			Listitem listitem = new Listitem();
			Listcell listcell = new Listcell();
			Image image = new Image();
			image.setContent((AImage) media);
			image.setHeight("64px");
			image.setWidth("64px");
			listcell.appendChild(image);
			listitem.appendChild(listcell);
			photosListbox.appendChild(listitem);
		}
		photosListbox.setVisible(true);
	}

	@Command
	public void uploadMainImage(
			@ContextParam(ContextType.BIND_CONTEXT)
			BindContext ctx) {
		UploadEvent upEvent = null;
		Object objUploadEvent = ctx.getTriggerEvent();
		if (objUploadEvent != null && (objUploadEvent instanceof UploadEvent)) {
			upEvent = (UploadEvent) objUploadEvent;
		}
		Media media = upEvent.getMedia();
		mainImage = (AImage) media;
		photoVBox.removeChild(photoVBox.getLastChild());
		Image imageComponent = new Image();
		imageComponent.setWidth("300px");
		imageComponent.setHeight("300px");
		imageComponent.setContent((AImage) media);
		photoVBox.appendChild(imageComponent);
	}

	@Command
	public void onSelect() {
		deleteButton.setDisabled(false);
		viewButton.setDisabled(false);
	}

	@Command
	public void onDelete() {
		uploadedImages.remove(photosListbox.getSelectedIndex());
		photosListbox.removeItemAt(photosListbox.getSelectedIndex());
		deleteButton.setDisabled(true);
	}

	@Command
	@NotifyChange("tradeableGoods")
	public void addTradeableGood() {
		if ((tradeableGoodNameBox.getValue() != null) && (tradeableGoodNameBox.getValue() != "")) {
			TradeableGood tradeableGood = new TradeableGood();
			tradeableGood.setName(tradeableGoodNameBox.getValue());
			tradeableGoods.add(tradeableGood);
			tradeableGoodNameBox.setValue(null);
		}
	}

	@Command
	public void removeTradeableGood() {
		tradeableGoods.remove(selectedGood);
	}

	@Command
	public void onView() {
		ZKSession.setAttribute("aImage", uploadedImages.get(photosListbox.getSelectedIndex()));
		Window window = (Window) Executions.createComponents("/zul/trg/image_view.zul", null, null);
		window.doModal();
	}

	@Command
	public void deleteMainImage() {
		photoVBox.removeChild(photoVBox.getLastChild());
		Image imageComponent = new Image();
		imageComponent.setWidth("300px");
		imageComponent.setHeight("300px");
		imageComponent.setSrc("/img/noImage.jpg");
		photoVBox.appendChild(imageComponent);
	}

	@Command
	public void onSave() {
		if (selectedCategory != null) {
			if (selectedCategory.getId() != -1) {
				listing.setProductCategory(selectedCategory);
				Transaction transaction = new Transaction();
				transaction.setCompleted(false);
				transaction.setSeller(loggedUser);
				transaction.setListing(listing);
				listing.setTransaction(transaction);
				listing.setActive(true);
				listing.setTransactionType(selectedTransactionType);
				if ((listing.getTransactionType().getId() == 1) || (listing.getTransactionType().getId() == 2)) {
					listing.setPrice(BigDecimal.ZERO);
				}
				ListingMainImage listingMainImage = new ListingMainImage();
				if (mainImage != null) {
					listingMainImage.setImageBytes(mainImage.getByteData());
					listing.setListingMainImage(listingMainImage);
				} else {
					listing.setListingMainImage(null);
				}
				try {
					createListingService.saveListing(listing, uploadedImages, tradeableGoods);

					Messagebox.show(Labels.getLabel("listing.message.success"), Labels.getLabel("common.messages.save_title"),
							Messagebox.OK, Messagebox.INFORMATION, new EventListener<Event>() {
						public void onEvent(Event evt) {
							ZKSession.sendRedirect(PageURL.LISTING_SELLER_LIST);
						}
					});
				} catch (ServiceException e) {
					Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listing")),
							Labels.getLabel("common.messages.save_title"), Messagebox.OK, Messagebox.ERROR);
					log.error(e.getMessage());
					ZKSession.sendRedirect(PageURL.LISTING_SELLER_LIST);
				}
			}
			else  {
				Messagebox.show(MessageBuilder.buildErrorMessage("Παρακαλώ συμπληρώστε τα πεδία κατηγορίας προϊόντος", Labels.getLabel("listing")),
						Labels.getLabel("common.messages.save_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}
		else {
			Messagebox.show(MessageBuilder.buildErrorMessage("Παρακαλώ συμπληρώστε τα πεδία κατηγορίας προϊόντος", Labels.getLabel("listing")),
					Labels.getLabel("common.messages.save_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	public void onCancel() {
		ZKSession.sendRedirect(PageURL.LISTING_SELLER_LIST);
	}

	@Command
	public void togglePrice() {
		if ((selectedTransactionType.getId() == 1) || (selectedTransactionType.getId() == 2)) {
			priceRow.setVisible(false);
		} else {
			priceRow.setVisible(true);
		}

		if ((selectedTransactionType.getId() == 2)) {
			exchangeRow.setVisible(true);
		} else {
			exchangeRow.setVisible(false);
			tradeableGoods.clear();
		}
	}

	@Command
	@NotifyChange("categories")
	public void onSelectParentCategory() {
		categoriesHbox.setVisible(true);
		Integer parentCategoryId = parentCategoryCombo.getSelectedItem().getIndex() + 1;
		getCategoriesByParentCategoryId(parentCategoryId);
		childCategoryCombo.setConstraint("no empty: Το πεδίο θα πρέπει να συμπληρωθεί υποχρεωτικά");
		childCategoryCombo.setSelectedItem(null);
	}

	public ListModelList<ProductCategory> getCategoriesByParentCategoryId(Integer parentCategoryId) {

		categories = new ListModelList<>();
		categories = new ListModelList<>();
		try {
			categories.addAll(createListingService.getProductCategoriesByParentCategoryId(parentCategoryId));
		} catch (ServiceException e) {
			Messagebox
					.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listing.productCategory")),
							Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			log.error(e.getMessage());
		}

		return categories;
	}

	public ListModelList<TransactionType> getTransactionTypes() {
		if (transactionTypes == null) {
			transactionTypes = new ListModelList<>();
			try {
				transactionTypes.addAll(createListingService.getTransactionTypes());
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
				productStatuses.addAll(createListingService.getProductStatuses());
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder
						.buildErrorMessage(e.getMessage(), Labels.getLabel("listing.productCategory")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}

		return productStatuses;
	}

	public ListModelList<Currency> getCurrencies() {
		return currencies;
	}

	public void setCurrencies(ListModelList<Currency> currencies) {
		this.currencies = currencies;
	}

	public Listing getListing() {
		return listing;
	}

	public void setListing(Listing listing) {
		this.listing = listing;
	}

	public ListModelList<AImage> getUploadedImages() {
		return uploadedImages;
	}

	public void setUploadedImages(ListModelList<AImage> uploadedImages) {
		this.uploadedImages = uploadedImages;
	}

	public TransactionType getSelectedTransactionType() {
		return selectedTransactionType;
	}

	public void setSelectedTransactionType(TransactionType selectedTransactionType) {
		this.selectedTransactionType = selectedTransactionType;
	}

	public Currency getDefaultCurrency() {
		return defaultCurrency;
	}

	public void setDefaultCurrency(Currency defaultCurrency) {
		this.defaultCurrency = defaultCurrency;
	}

	public AImage getMainImage() {
		return mainImage;
	}

	public void setMainImage(AImage mainImage) {
		this.mainImage = mainImage;
	}

	public ListModelList<TradeableGood> getTradeableGoods() {
		return tradeableGoods;
	}

	public void setTradeableGoods(ListModelList<TradeableGood> tradeableGoods) {
		this.tradeableGoods = tradeableGoods;
	}

	public TradeableGood getSelectedGood() {
		return selectedGood;
	}

	public void setSelectedGood(TradeableGood selectedGood) {
		this.selectedGood = selectedGood;
	}

	public ListModelList<ProductCategory> getCategories() {
		return categories;
	}

	public void setCategories(ListModelList<ProductCategory> categories) {
		this.categories = categories;
	}

	public ProductCategory getSelectedCategory() {
		return selectedCategory;
	}

	public void setSelectedCategory(ProductCategory selectedCategory) {
		this.selectedCategory = selectedCategory;
	}
}
