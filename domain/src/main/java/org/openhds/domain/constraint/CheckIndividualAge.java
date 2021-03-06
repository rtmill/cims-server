package org.openhds.domain.constraint;

import java.util.Calendar;
import javax.validation.ConstraintValidatorContext;

import org.openhds.domain.model.Individual;

public abstract class CheckIndividualAge extends AppContextAware {
	
	protected int requiredAge;
	protected boolean allowNull;
	
	/**
	 * Validate that an individual is older then some required age
	 */
	public boolean isValid(Individual arg0, ConstraintValidatorContext arg1) {
		
		if (allowNull && arg0 == null) 
			return true;
		if (allowNull)
			return true;
	
		Calendar minAge = Calendar.getInstance();
		minAge.add(Calendar.YEAR, -(requiredAge));

		return arg0.getDob().compareTo(minAge) < 0;

	}
}
