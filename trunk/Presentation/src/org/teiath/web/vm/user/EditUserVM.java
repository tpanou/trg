package org.teiath.web.vm.user;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.data.domain.image.ApplicationImage;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.user.EditUserService;
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
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;

import java.io.IOException;

@SuppressWarnings("UnusedDeclaration")
public class EditUserVM
		extends BaseVM {

	static Logger log = Logger.getLogger(EditUserVM.class.getName());

	@Wire("#emailRow")
	private Row emailRow;
	@Wire("#userPhoto")
	private Image userPhoto;
	@Wire("#genderCombo")
	private Combobox genderCombo;

	@WireVariable
	private EditUserService editUserService;

	private User user;
	private UserEditValidator userValidator;

	@AfterCompose
	@NotifyChange("user")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		user = new User();
		userValidator = new UserEditValidator();
		try {
			user = editUserService.getUserById(loggedUser.getId());
			if (user.getUserType() == User.USER_TYPE_EXTERNAL) {
				emailRow.setVisible(true);
			}
			if (user.getApplicationImage() == null) {
				userPhoto.setSrc("/img/default-avatar.png");
			} else {
				AImage aImage = new AImage("", user.getApplicationImage().getImageBytes());
				userPhoto.setContent(aImage);
			}

			if (user.getGender() != null) {
				if (user.getGender() == User.GENDER_MALE) {
					genderCombo.setValue(Labels.getLabel("user.male"));
				} else {
					genderCombo.setValue(Labels.getLabel("user.female"));
				}
			}
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("user.profile")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			ZKSession.sendRedirect(PageURL.USER_PROFILE);
		} catch (IOException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("user.profile")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			ZKSession.sendRedirect(PageURL.USER_PROFILE);
		}
	}

	@Command
	public void onSave() {
		if (genderCombo.getValue().equals(Labels.getLabel("user.male"))) {
			user.setGender(User.GENDER_MALE);
		} else {
			user.setGender(User.GENDER_FEMALE);
		}
		try {
			//user.setApplicationImage(uploadedImage);
			editUserService.saveUser(user);
			Messagebox.show(Labels.getLabel("user.message.edit.success"), Labels.getLabel("common.messages.save_title"),
					Messagebox.OK, Messagebox.INFORMATION, new EventListener<Event>() {
				public void onEvent(Event evt) {
					ZKSession.sendRedirect(PageURL.USER_PROFILE);
				}
			});
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("user.profile")),
					Labels.getLabel("common.messages.edit_title"), Messagebox.OK, Messagebox.ERROR);
			ZKSession.sendRedirect(PageURL.USER_PROFILE);
		}
	}

	@Command
	public void onCancel() {
		ZKSession.sendRedirect(PageURL.USER_PROFILE);
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
		Media media = upEvent.getMedia();
		if (user.getApplicationImage() == null) {
			ApplicationImage applicationImage = new ApplicationImage();
			user.setApplicationImage(applicationImage);
		}
		user.getApplicationImage().setImageBytes(media.getByteData());
		userPhoto.setContent((AImage) media);
	}

	@Command
	public void onDeleteImage() {
		userPhoto.setSrc("/img/default-avatar.png");
		user.getApplicationImage().setImageBytes(null);
		user.setApplicationImage(null);
	}

	public UserEditValidator getUserValidator() {
		return userValidator;
	}

	public void setUserValidator(UserEditValidator userValidator) {
		this.userValidator = userValidator;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
