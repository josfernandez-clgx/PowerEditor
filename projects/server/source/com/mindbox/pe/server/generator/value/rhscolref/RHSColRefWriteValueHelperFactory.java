package com.mindbox.pe.server.generator.value.rhscolref;

import java.util.Date;

import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;
import com.mindbox.pe.model.table.DynamicStringValue;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.model.table.IRange;

public final class RHSColRefWriteValueHelperFactory {

	private static RHSColRefWriteValueHelperFactory instance;

	public static RHSColRefWriteValueHelperFactory getInstance() {
		if (instance == null) {
			instance = new RHSColRefWriteValueHelperFactory();
		}
		return instance;
	}

	private DynamicStringValueHelper dynamicStringValueHelper = new DynamicStringValueHelper();
	private IRangeValueHelper iRangeValueHelper = new IRangeValueHelper();
	private CategoryOrEntityValueValueHelper categoryOrEntityValueValueHelper = new CategoryOrEntityValueValueHelper();
	private CategoryOrEntityValuesValueHelper categoryOrEntityValuesValueHelper = new CategoryOrEntityValuesValueHelper();
	private EnumValueHelper enumValueHelper = new EnumValueHelper();
	private EnumValuesHelper enumValuesHelper = new EnumValuesHelper();
	private IntegerValueHelper integerValueHelper = new IntegerValueHelper();
	private FloatValueHelper floatValueHelper = new FloatValueHelper();
	private DoubleValueHelper doubleValueHelper = new DoubleValueHelper();
	private DateValueHelper dateValueHelper = new DateValueHelper();
	private DefaultValueHelper defaultValueHelper = new DefaultValueHelper();

	public RHSColRefWriteValueHelper<?> getRHSColRefWriteValueHelper(Object obj) {
		if (obj instanceof DynamicStringValue) {
			return dynamicStringValueHelper;
		}
		else if (obj instanceof IRange) {
			return iRangeValueHelper;
		}
		else if (obj instanceof CategoryOrEntityValue) {
			return categoryOrEntityValueValueHelper;
		}
		else if (obj instanceof CategoryOrEntityValues) {
			return categoryOrEntityValuesValueHelper;
		}
		else if (EnumValues.class.isInstance(obj)) {
			return enumValuesHelper;
		}
		else if (obj instanceof EnumValue) {
			return enumValueHelper;
		}
		else if (obj instanceof Integer) {
			return integerValueHelper;
		}
		else if (obj instanceof Float) {
			return floatValueHelper;
		}
		else if (obj instanceof Double) {
			return doubleValueHelper;
		}
		else if (obj instanceof Date) {
			return dateValueHelper;
		}
		else {
			return defaultValueHelper;
		}
	}

	private RHSColRefWriteValueHelperFactory() {

	}
}
