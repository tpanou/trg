package org.teiath.web;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.validator.AbstractValidator;
import org.zkoss.util.resource.Labels;

import java.util.Date;

public class DateValidator
		extends AbstractValidator {

	public DateValidator() {

	}

	@Override
	public void validate(ValidationContext ctx) {
		Date dateFrom = (Date) ctx.getProperties("dateFrom")[0].getValue();
		Date dateTo = (Date) ctx.getProperties("dateTo")[0].getValue();
		Date timeFrom = (Date) ctx.getProperties("timeFrom")[0].getValue();
		Date timeTo = (Date) ctx.getProperties("timeTo")[0].getValue();

		if ((dateFrom == null)) {
			addInvalidMessage(ctx, "fx_dateFrom", Labels.getLabel("validation.common.emptyDates"));
		}

		if ((dateTo == null)) {
			addInvalidMessage(ctx, "fx_dateTo", Labels.getLabel("validation.common.emptyDates"));
		} else if ((dateFrom != null) && (dateTo.before(dateFrom))) {
			addInvalidMessage(ctx, "fx_dateTo", Labels.getLabel("validation.common.dateTo"));
		}

		if ((timeFrom == null)) {
			addInvalidMessage(ctx, "fx_timeFrom", Labels.getLabel("validation.common.emptyDates"));
		}

		if ((timeTo == null)) {
			addInvalidMessage(ctx, "fx_timeTo", Labels.getLabel("validation.common.emptyDates"));
		} else if ((timeFrom != null) && (timeTo.before(timeFrom))) {
			addInvalidMessage(ctx, "fx_timeTo", Labels.getLabel("validation.common.timeTo"));
		}
	}
}
