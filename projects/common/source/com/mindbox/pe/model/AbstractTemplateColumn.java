package com.mindbox.pe.model;

import java.text.DateFormat;

import com.mindbox.pe.common.DomainClassProvider;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.UIConfiguration;
import com.mindbox.pe.model.exceptions.InvalidDataException;
import com.mindbox.pe.model.table.BooleanDataHelper;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;
import com.mindbox.pe.model.table.DateRange;
import com.mindbox.pe.model.table.DateTimeRange;
import com.mindbox.pe.model.table.DynamicStringValue;
import com.mindbox.pe.model.table.EnumValuesDataHelper;
import com.mindbox.pe.model.table.EnumerationSourceProxy;
import com.mindbox.pe.model.table.FloatRange;
import com.mindbox.pe.model.table.IntegerRange;
import com.mindbox.pe.model.table.TimeRange;


/**
 * Guideline Template column.
 * @author Geneho
 * @since PowerEditor 1.0
 */
public abstract class AbstractTemplateColumn extends AbstractIDNameDescriptionObject {

	private static final long serialVersionUID = 2003122613004000L;


	protected int columnWidth = 100;
	protected String font;
	protected String color;
	protected TemplateUsageType usageType;
	private ColumnDataSpecDigest dataSpecDigest;
	private String title;
	/** To support attributeMap on column element for backward-compatibility. */
	private String attributeMapOldStr = null;

	protected AbstractTemplateColumn(int id, String name, String desc, int width, TemplateUsageType usageType) {
		super(id, name, desc);
		this.columnWidth = width;
		this.usageType = usageType;
		this.dataSpecDigest = new ColumnDataSpecDigest();
	}

	protected AbstractTemplateColumn(AbstractTemplateColumn source) {
		this(source.getID(), source.getName(), source.getDescription(), source.columnWidth, source.usageType);
		this.dataSpecDigest = new ColumnDataSpecDigest(source.dataSpecDigest);
	}

	public void setAttributeMapOldStr(String attributeMapOldStr) {
		this.attributeMapOldStr = attributeMapOldStr;
	}

	/**
	 * Gets the value of specified cell as a cell value object.
	 * This is used by {@link com.mindbox.pe.server.db.loaders.GridLoader} and
	 * {@link com.mindbox.pe.server.imexport.ObjectConverter} to convert string into value objects.
	 * @param strValue
	 *            string
	 * @param colIndex
	 *            zero-based index
	 * @return the cell value object
	 * @throws InvalidDataException
	 *             if the cell contains an invalid vaue
	 */
	public final Object convertToCellValue(String strValue, DomainClassProvider domainClassProvider, EnumerationSourceProxy enumerationSourceProxy) throws InvalidDataException {
		if (UtilBase.isEmpty(strValue)) return null;
		Object obj = null;
		try {
			ColumnDataSpecDigest columnDataSpecDigest = getColumnDataSpecDigest();
			if (columnDataSpecDigest == null) {
				return strValue;
			}
			else if (columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_BOOLEAN)) {
				if (!BooleanDataHelper.isValidString(strValue)) {
					throw new InvalidDataException("Column " + getTitle(), strValue, "Invalid boolean value");
				}
				// TT 1935
				obj = BooleanDataHelper.mapToBooleanValue(strValue, columnDataSpecDigest.isBlankAllowed());

			}
			else if (columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_CURRENCY_RANGE)
					|| columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_FLOAT_RANGE)) {
				obj = FloatRange.parseValue(strValue);
			}
			else if (columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_DATE)) {
				try {
					obj = UIConfiguration.FORMAT_DATE.parse(strValue);
				}
				catch (Exception ex) {
					obj = DateFormat.getInstance().parse(strValue);
				}
			}
			else if (columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_DATE_RANGE)) {
				obj = DateRange.parseValue(strValue);
			}
			else if (columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_DATE_TIME)) {
				try {
					obj = UIConfiguration.FORMAT_DATE_TIME_SEC.parse(strValue);
				}
				catch (Exception ex) {
					obj = DateFormat.getInstance().parse(strValue);
				}
			}
			else if (columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_DATE_TIME_RANGE)) {
				obj = DateTimeRange.parseValue(strValue);
			}
			else if (columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_DYNAMIC_STRING)) {
				obj = DynamicStringValue.parseValue(strValue);
			}
			else if (columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_ENTITY)) {
				if (columnDataSpecDigest.isMultiSelectAllowed()) {
					obj = CategoryOrEntityValues.parseCategoryOrEntityValues(
							strValue,
							columnDataSpecDigest.getEntityType(),
							columnDataSpecDigest.isEntityAllowed(),
							columnDataSpecDigest.isCategoryAllowed());
				}
				else {
					obj = CategoryOrEntityValue.valueOf(
							strValue,
							columnDataSpecDigest.getEntityType(),
							columnDataSpecDigest.isEntityAllowed(),
							columnDataSpecDigest.isCategoryAllowed());
				}
			}
			else if (columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_ENUM_LIST)) {
				obj = EnumValuesDataHelper.convertToEnumValue(columnDataSpecDigest, strValue, domainClassProvider, enumerationSourceProxy);
			}
			else if (columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_FLOAT)
					|| columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_CURRENCY)
					|| columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_PERCENT)) {
				obj = new Double(strValue);
			}
			else if (columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_INTEGER)) {
				obj = new Integer(strValue);
			}
			else if (columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_INTEGER_RANGE)) {
				obj = IntegerRange.parseValue(strValue);
			}
			else if (columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_PERCENT)) {

			}
			else if (columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_TIME_RANGE)) {
				obj = TimeRange.parseTimeRangeValue(strValue);
			}
			else {
				obj = strValue;
			}
		}
		catch (Exception exception) {
			exception.printStackTrace();
			throw new InvalidDataException("Cell Value", strValue, "Could not convert to an object: " + strValue + " - "
					+ exception.getMessage());
		}
		return obj;
	}

	public TemplateUsageType getUsageType() {
		return usageType;
	}

	public void setUsageType(TemplateUsageType usageType) {
		this.usageType = usageType;
	}

	/**
	 * Sets presentation details from the specified presentation digest object.
	 * @param digest
	 * @since PowerEditor 3.2.0
	 */
	public void setDataSpecDigest(ColumnDataSpecDigest digest) {
		this.dataSpecDigest = digest;
		if (attributeMapOldStr != null) {
			dataSpecDigest.setAttributeMap(attributeMapOldStr);
		}
	}

	public ColumnDataSpecDigest getColumnDataSpecDigest() {
		return dataSpecDigest;
	}

	/**
	 * Sets presentation details from the specified presentation digest object.
	 * @param digest
	 * @since PowerEditor 3.2.0
	 */
	public void setPresentation(ColumnPresentationDigest digest) {
		this.setTitle(digest.getTitle());
		this.columnWidth = digest.getColumnWidth();
		this.font = digest.getFont();
		this.color = digest.getColor();
	}

	/**
	 * @return Returns the font.
	 */
	public String getFont() {
		return font;
	}

	/**
	 * @param font The font to set.
	 */
	public void setFont(String font) {
		this.font = font;
	}

	public String getMappedAttribute() {
		return dataSpecDigest.getMappedAttribute();
	}

	public final String getMAClassName() {
		String mappedAttribute = getMappedAttribute();
		if (mappedAttribute == null) return null;
		int i = mappedAttribute.indexOf('.');
		if (i == -1)
			return null;
		else
			return mappedAttribute.substring(0, i);
	}

	public final String getMAAttributeName() {
		String mappedAttribute = getMappedAttribute();
		if (mappedAttribute == null) return null;
		int i = mappedAttribute.indexOf('.');
		if (i == -1)
			return null;
		else
			return mappedAttribute.substring(i + 1, mappedAttribute.length());
	}

	public int getColumnWidth() {
		return columnWidth;
	}

	public void setColumnWidth(int width) {
		columnWidth = width;
	}

	public final int getColumnNumber() {
		return super.getID();
	}

	public final String getTitle() {
		return title;
	}

	public final void setTitle(String s) {
		this.title = s;
	}

	public String toString() {
		return "Column[" + getColumnNumber() + ",title=" + getTitle() + ",ds = " + getColumnDataSpecDigest() + "]";
	}

	/**
	 * @return Returns the color.
	 */
	public String getColor() {
		return color;
	}

	/**
	 * @param color The color to set.
	 */
	public void setColor(String color) {
		this.color = color;
	}

	//	public abstract MessageConfiguration getMessageConfiguration();
}