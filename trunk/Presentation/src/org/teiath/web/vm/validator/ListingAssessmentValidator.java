package org.teiath.web.vm.validator;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.validator.AbstractValidator;

public class ListingAssessmentValidator
		extends AbstractValidator {

	@Override
	public void validate(ValidationContext ctx) {
		if ((ctx.getProperties("rating")[0].getValue() == null) || (ctx.getProperties("rating")[0].getValue()
				.equals(0))) {
			addInvalidMessage(ctx, "fx_rating", "Η βαθμολόγηση είναι υποχρεωτική");
		}
	}
}

