package com.codeit.deokhugam.domain.dashboard.validation;

import com.codeit.deokhugam.domain.dashboard.dto.request.DashboardSearchCondition;
import com.codeit.deokhugam.domain.dashboard.entity.PeriodType;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DashboardSearchConditionValidator
	implements ConstraintValidator<ValidDashboardSearchCondition, DashboardSearchCondition> {

	@Override
	public boolean isValid(
		DashboardSearchCondition condition,
		ConstraintValidatorContext context
	) {
		if (condition == null || condition.period() == null) {
			return true;
		}

		boolean valid =
			condition.period() == PeriodType.ALL_TIME
				|| condition.batchDate() != null;
		if (valid) {
			return true;
		}

		context.disableDefaultConstraintViolation();

		context.buildConstraintViolationWithTemplate(
				context.getDefaultConstraintMessageTemplate())
			.addPropertyNode("batchDate")
			.addConstraintViolation();

		return false;
	}
}