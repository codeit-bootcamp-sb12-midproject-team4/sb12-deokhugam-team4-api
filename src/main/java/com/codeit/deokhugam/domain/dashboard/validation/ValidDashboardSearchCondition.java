package com.codeit.deokhugam.domain.dashboard.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = DashboardSearchConditionValidator.class)
public @interface ValidDashboardSearchCondition {
	String message() default "ALL_TIME이 아닌 경우 batchDate는 필수입니다.";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}