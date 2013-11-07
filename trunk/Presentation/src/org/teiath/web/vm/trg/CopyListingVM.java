package org.teiath.web.vm.trg;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.teiath.data.domain.image.ApplicationImage;
import org.teiath.data.domain.image.ListingMainImage;
import org.teiath.data.domain.trg.*;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.trg.CopyListingService;
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

import java.io.IOException;
import java.math.BigDecimal;

@SuppressWarnings("UnusedDeclaration")
public class CopyListingVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ListListingsVM.class.getName());

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
	@Wire("#typesCombo")
	private Combobox typesCombo;
	@Wire("#photoVBox")
	private Vbox photoVBox;
	@Wire("#tradeableGoodNameBox")
	private Textbox tradeableGoodNameBox;
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
	@Wire("#childCategoryCombo")
	private Combobox childCategoryCombo;
	@Wire("#photoTab")
	private Tab photoTab;
	@Wire("#photoLayout")
	private Vlayout photoLayout;

	@WireVariable
	private CopyListingService copyListingService;

	private Listing listing;
	private Listing newListing;
	private ListModelList<ProductCategory> categories;
	private ListModelList<TransactionType> transactionTypes;
	private ListModelList<ProductStatus> productStatuses;
	private ListModelList<AImage> uploadedImages;
	private TransactionType selectedTransactionType;
	private ListModelList<Currency> currencies;
	private AImage mainImage;
	private ListModelList<ApplicationImage> images;
	private ListModelList<TradeableGood> tradeableGoods;
	private TradeableGood selectedGood;
	private ProductCategory selectedCategory;

	@AfterCompose
	@NotifyChange("newListing")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		if ((Executions.getCurrent().getUserAgent().contains("Android")) || (Executions.getCurrent().getUserAgent()
				.contains("iPhone")) || (Executions.getCurrent().getUserAgent().contains("iPad"))) {

			photoLayout.setVisible(false);
			photoTab.setVisible(false);
		}

		uploadedImages = new ListModelList<>();
		currencies = new ListModelList<>();
		try {
			currencies.addAll(copyListingService.getCurrencies());
		} catch (ServiceException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listing.price")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}

		listing = new Listing();
		newListing = new Listing();
		categories = new ListModelList<>();

		try {

			listing = copyListingService.getListingById((Integer) ZKSession.getAttribute("listingId"));

			categories.addAll(copyListingService
					.getProductCategoriesByParentCategoryId(listing.getProductCategory().getParentCategoryId()));

			switch (listing.getProductCategory().getParentCategoryId()) {
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

			selectedCategory = listing.getProductCategory();
			newListing.setTransactionType(listing.getTransactionType());
			newListing.setProductCategory(listing.getProductCategory());
			newListing.setProductName(listing.getProductName());
			newListing.setProductBrand(listing.getProductBrand());
			newListing.setProductDescription(listing.getProductDescription());
			newListing.setProductStatus(listing.getProductStatus());
			newListing.setPurchaseDate(listing.getPurchaseDate());
			newListing.setSendHome(listing.isSendHome());
			newListing.setEnabled(listing.isEnabled());
			newListing.setComments(listing.getComments());
			newListing.setPrice(listing.getPrice());
			newListing.setCurrency(listing.getCurrency());
			newListing.setCode(RandomStringUtils.randomAlphanumeric(15).toUpperCase());
			Transaction transaction = new Transaction();
			transaction.setCompleted(false);
			transaction.setSeller(loggedUser);
			transaction.setListing(newListing);
			newListing.setTransaction(transaction);
			if (listing.getListingMainImage() != null) {
				Image imageComponent = new Image();
				imageComponent.setWidth("300px");
				imageComponent.setHeight("300px");
				imageComponent.setContent(listing.getListingMainImage().getImage());
				photoVBox.appendChild(imageComponent);
				mainImage = listing.getListingMainImage().getImage();
			} else {
				Image imageComponent = new Image();
				imageComponent.setWidth("300px");
				imageComponent.setHeight("300px");
				imageComponent.setSrc("/img/noImage.jpg");
				photoVBox.appendChild(imageComponent);
			}
		} catch (ServiceException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listing")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			log.error(e.getMessage());
			ZKSession.sendRedirect(PageURL.LISTING_LIST);
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		newListing.setUser(loggedUser);

		if (listing.getTransactionType().getId() == 1) {
			priceRow.setVisible(false);
		}

		images = this.getImages();
		if (images != null) {
			for (ApplicationImage applicationImage : images) {
				try {
					AImage aImage = new AImage("", applicationImage.getImageBytes());
					uploadedImages.add(aImage);
					Listitem listitem = new Listitem();
					Listcell listcell = new Listcell();
					Image image = new Image();
					image.setHeight("64px");
					image.setWidth("64px");
					image.setContent(aImage);

					listcell.appendChild(image);
					listitem.appendChild(listcell);
					photosListbox.appendChild(listitem);
				} catch (IOException e) {
					log.error(e.getMessage());
					Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listing")),
							Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
					ZKSession.sendRedirect(PageURL.LISTING_LIST);
				}
			}
			photosListbox.setVisible(true);
		}

		if (listing.getTransactionType().getId() == TransactionType.TYPE_EXCHANGE) {
			exchangeRow.setVisible(true);
			tradeableGoods = new ListModelList<>();
			try {
				tradeableGoods.addAll(copyListingService.getListingTradeableGoods(listing));
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listing")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
				ZKSession.sendRedirect(PageURL.LISTING_LIST);
			}
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
	public void deleteMainImage() {
		photoVBox.removeChild(photoVBox.getLastChild());
		Image imageComponent = new Image();
		imageComponent.setWidth("300px");
		imageComponent.setHeight("300px");
		imageComponent.setSrc("/img/noImage.jpg");
		photoVBox.appendChild(imageComponent);
		listing.setListingMainImage(null);
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
	public void onSave() {

		if (selectedCategory != null) {
			newListing.setActive(true);
			newListing.setProductCategory(selectedCategory);
			if ((newListing.getTransactionType().getName().startsWith("Δωρ") || newListing.getTransactionType().getName()
					.startsWith("ΔΩΡ")) || newListing.getTransactionType().getName()
					.startsWith("ΑΝΤ") || newListing.getTransactionType().getName()
					.startsWith("Αντ") )  {
				newListing.setPrice(BigDecimal.ZERO);
			}
			ListingMainImage listingMainImage = new ListingMainImage();
			if (listing.getListingMainImage() != null) {
				listingMainImage.setImageBytes(mainImage.getByteData());
				newListing.setListingMainImage(listingMainImage);
			} else {
				newListing.setListingMainImage(null);
			}

			try {
				copyListingService.deleteListingPhotos(newListing);
				copyListingService.deleteListingTradeableGoods(newListing);
				copyListingService.saveListing(newListing, uploadedImages, tradeableGoods);
				Messagebox.show(Labels.getLabel("listing.message.success"), Labels.getLabel("common.messages.save_title"),
						Messagebox.OK, Messagebox.INFORMATION, new EventListener<Event>() {
					public void onEvent(Event evt) {
						ZKSession.sendRedirect(PageURL.LISTING_SELLER_LIST);
					}
				});
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listing")),
						Labels.getLabel("common.messages.save_title"), Messagebox.OK, Messagebox.ERROR);
				ZKSession.sendRedirect(PageURL.LISTING_SELLER_LIST);
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
		if ((typesCombo.getSelectedItem().getLabel().equals("Δωρεά")) || (typesCombo.getSelectedItem().getLabel().equals("Ανταλλαγή"))) {
			priceRow.setVisible(false);
		} else {
			priceRow.setVisible(true);
		}

		if (typesCombo.getSelectedItem().getLabel().equals("Ανταλλαγή")) {
			tradeableGoods = new ListModelList<>();
			exchangeRow.setVisible(true);
		} else {
			exchangeRow.setVisible(false);
		}
	}

	@Command
	@NotifyChange("categories")
	public void onSelectParentCategory() {
		Integer parentCategoryId = parentCategoryCombo.getSelectedItem().getIndex() + 1;
		getCategoriesByParentCategoryId(parentCategoryId);
		childCategoryCombo.setSelectedItem(null);
		selectedCategory = null;
	}

	public ListModelList<ProductCategory> getCategoriesByParentCategoryId(Integer parentCategoryId) {
		categories = new ListModelList<>();
		try {
			categories.addAll(copyListingService.getProductCategoriesByParentCategoryId(parentCategoryId));
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
				transactionTypes.addAll(copyListingService.getTransactionTypes());
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
				productStatuses.addAll(copyListingService.getProductStatuses());
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder
						.buildErrorMessage(e.getMessage(), Labels.getLabel("listing.productCategory")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}

		return productStatuses;
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

	public Listing getNewListing() {
		return newListing;
	}

	public void setNewListing(Listing newListing) {
		this.newListing = newListing;
	}

	public ListModelList<Currency> getCurrencies() {
		return currencies;
	}

	public void setCurrencies(ListModelList<Currency> currencies) {
		this.currencies = currencies;
	}

	public AImage getMainImage() {
		return mainImage;
	}

	public void setMainImage(AImage mainImage) {
		this.mainImage = mainImage;
	}

	public ListModelList<ApplicationImage> getImages() {
		if (images == null) {
			images = new ListModelList<>();
			try {
				images.addAll(copyListingService.getImages(listing));
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder
						.buildErrorMessage(e.getMessage(), Labels.getLabel("listing.productPhotos")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
				log.error(e.getMessage());
			}
		}
		return images;
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
