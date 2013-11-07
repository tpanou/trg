package org.teiath.web.vm.user.values;

import org.apache.log4j.Logger;
import org.teiath.data.domain.trg.ProductCategory;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.values.EditProductCategoryService;
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
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Messagebox;

@SuppressWarnings("UnusedDeclaration")
public class EditProductCategoryVM
		extends BaseVM {

	static Logger log = Logger.getLogger(EditProductCategoryVM.class.getName());

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
	private EditProductCategoryService editProductCategoryService;

	private ProductCategory productCategory;

	@AfterCompose
	@NotifyChange("productCategory")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		productCategory = new ProductCategory();
		try {
			productCategory = editProductCategoryService
					.getProductCategoryById((Integer) ZKSession.getAttribute("productCategoryId"));
			switch (productCategory.getParentCategoryId()) {
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
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("value.list")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			ZKSession.sendRedirect(PageURL.VALUES_LIST);
		}
	}

	@Command
	public void onSave() {
		try {
			productCategory.setParentCategoryId(parentCategoryCombo.getSelectedItem().getIndex() + 1);
			editProductCategoryService.saveProductCategory(productCategory);
			Messagebox.show(Labels.getLabel("value.message.success"), Labels.getLabel("common.messages.save_title"),
					Messagebox.OK, Messagebox.INFORMATION, new EventListener<Event>() {
				public void onEvent(Event evt) {
					ZKSession.sendRedirect(PageURL.VALUES_LIST);
				}
			});
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("value.list")),
					Labels.getLabel("common.messages.edit_title"), Messagebox.OK, Messagebox.ERROR);
			ZKSession.sendRedirect(PageURL.VALUES_LIST);
		}
	}

	@Command
	public void onCancel() {
		ZKSession.sendRedirect(PageURL.VALUES_LIST);
	}

	public ProductCategory getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(ProductCategory productCategory) {
		this.productCategory = productCategory;
	}
}

