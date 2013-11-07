package org.teiath.web.vm.trg;

import org.apache.log4j.Logger;
import org.teiath.data.domain.image.ApplicationImage;
import org.teiath.data.domain.image.ListingMainImage;
import org.teiath.data.domain.trg.*;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.trg.EditListingService;
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
public class EditListingVM
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
	@Wire("#typesCombo")
	private Combobox typesCombo;
	@Wire("#exchangeRow")
	private Row exchangeRow;
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
	private EditListingService editListingService;

	private Listing listing;
	private ListModelList<ProductCategory> categories;
	private ListModelList<TransactionType> transactionTypes;
	private ListModelList<ProductStatus> productStatuses;
	private ListModelList<ApplicationImage> images;
	private ListModelList<AImage> uploadedImages;
	private ListModelList<Currency> currencies;
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

		uploadedImages = new ListModelList<>();
		currencies = new ListModelList<>();
		listing = new Listing();
		categories = new ListModelList<>();
		tradeableGoods = new ListModelList<>();
		try {
			listing = editListingService.getListingById((Integer) ZKSession.getAttribute("listingId"));
			categories.addAll(editListingService
					.getProductCategoriesByParentCategoryId(listing.getProductCategory().getParentCategoryId()));
			selectedCategory = listing.getProductCategory();

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

			currencies.addAll(editListingService.getCurrencies());
		} catch (ServiceException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listing")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			log.error(e.getMessage());
			ZKSession.sendRedirect(PageURL.LISTING_LIST);
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		listing.setUser(loggedUser);

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
				tradeableGoods.addAll(editListingService.getListingTradeableGoods(listing));
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
	public void deleteMainImage() {
		photoVBox.removeChild(photoVBox.getLastChild());
		Image imageComponent = new Image();
		imageComponent.setWidth("300px");
		imageComponent.setHeight("300px");
		imageComponent.setSrc("/img/noImage.jpg");
		photoVBox.appendChild(imageComponent);
		mainImage = null;
	}

	@Command
	public void onView() {
		ZKSession.setAttribute("aImage", uploadedImages.get(photosListbox.getSelectedIndex()));
		Window window = (Window) Executions.createComponents("/zul/trg/image_view.zul", null, null);
		window.doModal();
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
	public void onSave() {

		if (selectedCategory != null) {
			listing.setProductCategory(selectedCategory);
			listing.setActive(true);
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

				editListingService.deleteListingPhotos(listing);
				editListingService.deleteListingTradeableGoods(listing);
				editListingService.saveListing(listing, uploadedImages, tradeableGoods);
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
			categories.addAll(editListingService.getProductCategoriesByParentCategoryId(parentCategoryId));
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
				transactionTypes.addAll(editListingService.getTransactionTypes());
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
				productStatuses.addAll(editListingService.getProductStatuses());
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder
						.buildErrorMessage(e.getMessage(), Labels.getLabel("listing.productCategory")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}

		return productStatuses;
	}

	public ListModelList<ApplicationImage> getImages() {
		if (images == null) {
			images = new ListModelList<>();
			try {
				images.addAll(editListingService.getImages(listing));
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder
						.buildErrorMessage(e.getMessage(), Labels.getLabel("listing.productPhotos")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
				log.error(e.getMessage());
			}
		}
		return images;
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

	public ProductCategory getSelectedCategory() {
		return selectedCategory;
	}

	public void setSelectedCategory(ProductCategory selectedCategory) {
		this.selectedCategory = selectedCategory;
	}
}
