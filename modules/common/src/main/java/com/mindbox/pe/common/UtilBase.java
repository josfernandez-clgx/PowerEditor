package com.mindbox.pe.common;

import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.swing.JLabel;

import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.table.IRange;

/**
 * Provides utility methods for Server and Applet.
 *
 * @since PowerEditor 1.0
 */
public class UtilBase {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("M/d/yy h:mm a");

    public static final String REGEX_VALID_SYMBOL = "[!@$\\^*_\\-+={}\\[:<>\\.?/0-9a-zA-Z]++";

    private static final Pattern VALID_SYMBOL_PATTERN = Pattern.compile(REGEX_VALID_SYMBOL);

    public static boolean asBoolean(final Boolean booleanFlag, final boolean defaultValue) {
        if (booleanFlag == null) {
            return defaultValue;
        }
        else {
            return booleanFlag.booleanValue();
        }
    }

    private static final List<Integer> asList(int[] intArray) {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < intArray.length; i++) {
            list.add(intArray[i]);
        }
        return list;
    }

    public static <T> String nameEqualsValue(final String name, final T value) {
        if (null == value) {
            return name + "=null";
        } else {
            StringBuilder builder = new StringBuilder();
            nameEqualsValue(builder, name, value);
            return builder.toString();
        }
    }

    public static <T> void nameEqualsValue(StringBuilder builder, final String name, final T value) {
        builder.append(name);
        builder.append('=');
        if (null == value) {
            builder.append("null");
        } else {
            final boolean isString = (String.class == value.getClass());
            if (isString) {
                builder.append('"');
            }
            builder.append(value);
            if (isString) {
                builder.append('"');
            }
        }
    }

    public static <T> String nameEqualsValueArray(final String name, final T[] value) {
        if (null == value) {
            return name + "=null";
        } else {
            StringBuilder builder = new StringBuilder();
            nameEqualsValueArray(builder, name, value);
            return builder.toString();
        }
    }

    public static <T> void nameEqualsValueArray(StringBuilder builder, final String name, final T[] values) {
        if (null == values) {
            builder.append(name);
            builder.append('=');
            builder.append("null");
        } else if (0 == values.length) {
            builder.append(name);
            builder.append("[]=empty");
        } else {
            nameEqualsValue(builder, name + "[0]", values[0]);
            for (int i = 1; i < values.length; i++) {
                nameEqualsValue(builder, ',' + name + '[' + Integer.toString(i) + ']', values[i]);
            }
        }
    }

    /**
     * Constructs privilege display name
     * @param privilege privilege
     * @return String
     */
    public static String constructEditGuidelinePrivilege_DisplayName(String privilege) {
        return PrivilegeConstants.EDIT_PRIV_DISPLAY_NAME_PREFIX + privilege + PrivilegeConstants.VIEW_AND_EDIT_USAGE_GUIDELINE_PRIV_DISPLAY_NAME_SUFFIX;
    }

    /**
     * Constructs privilege name
     * @param privilege privilege
     * @return String
     */
    public static String constructEditGuidelinePrivilege_Name(String privilege) {
        return PrivilegeConstants.EDIT_PRIV_NAME_PREFIX + privilege;
    }

    /**
     * Constructs privilege display name
     * @param privilege privilege
     * @return String
     */
    public static String constructEditTemplatesPrivilege_DisplayName(String privilege) {
        return PrivilegeConstants.EDIT_PRIV_DISPLAY_NAME_PREFIX + privilege + PrivilegeConstants.VIEW_AND_EDIT_USAGE_TEMPLATE_PRIV_DISPLAY_NAME_SUFFIX;
    }

    /**
     * Constructs privilege name
     * @param privilege privilege
     * @return String
     */
    public static String constructEditTemplatesPrivilege_Name(String privilege) {
        return PrivilegeConstants.EDIT_PRIV_NAME_PREFIX + privilege + PrivilegeConstants.VIEW_AND_EDIT_USAGE_TEMPLATE_PRIV_NAME_SUFFIX;
    }

    /**
     * Constructs privilege display name
     * @param privilege privilege
     * @return String
     */
    public static String constructViewGuidelinePrivilege_DisplayName(String privilege) {
        return PrivilegeConstants.VIEW_PRIV_DISPLAY_NAME_PREFIX + privilege + PrivilegeConstants.VIEW_AND_EDIT_USAGE_GUIDELINE_PRIV_DISPLAY_NAME_SUFFIX;
    }

    /**
     * A UsageTypes configured with like this...
     * &lt;sageType name="Global-Qualify" displayName="Global Qualify" privilege="Qualification"/&gt;
     * will have 4 privileges
     * "EditQualification", "EditQualificationTemplates", "ViewQualification", "ViewQualificationTemplates".
     * Display name will be
     * "Edit Qualification Guidelines", "Edit Qualification Templates", "View Qualification Guidelines", "View Qualification Templates".
     * All 8 constructXXXX methods accomplish the above mentioned
     *
     * @param privilege privilege
     * @return String
     */
    public static String constructViewGuidelinePrivilege_Name(String privilege) {
        return PrivilegeConstants.VIEW_PRIV_NAME_PREFIX + privilege;
    }

    /**
     * Constructs privilege display name
     * @param privilege privilege
     * @return String
     */
    public static String constructViewTemplatesPrivilege_DisplayName(String privilege) {
        return PrivilegeConstants.VIEW_PRIV_DISPLAY_NAME_PREFIX + privilege + PrivilegeConstants.VIEW_AND_EDIT_USAGE_TEMPLATE_PRIV_DISPLAY_NAME_SUFFIX;
    }

    /**
     * Constructs privilege name
     * @param privilege privilege
     * @return String
     */
    public static String constructViewTemplatesPrivilege_Name(String privilege) {
        return PrivilegeConstants.VIEW_PRIV_NAME_PREFIX + privilege + PrivilegeConstants.VIEW_AND_EDIT_USAGE_TEMPLATE_PRIV_NAME_SUFFIX;
    }

    /**
     * @param subcontents subcontents
     * @param container container
     * @return true if every ID in subcontents is a member of container
     */
    public static boolean contains(int[] subcontents, int[] container) {
        for (int i = 0; i < subcontents.length; i++) {
            if (!isMember(subcontents[i], container)) {
                return false;
            }
        }
        return true;
    }

    public static String convertCellValueToString(Object value) {
        return (value == null ? "" : (value instanceof Date ? Constants.THREADLOCAL_FORMAT_DATE_TIME_SEC.get().format((Date) value) : value.toString()));
    }

    public static final int[] copy(int[] source) {
        if (source == null) return null;
        int[] copy = new int[source.length];
        for (int i = 0; i < copy.length; i++) {
            copy[i] = source[i];
        }
        return copy;
    }

    /**
     * Tests if the two specified int arrays are identical.
     * The arrays are identical if they have the same number of elements and,
     * all elements in one are contained in the other.
     * @param ids1 int array one
     * @param ids2 int array two
     * @return <code>true</code> if <code>ids1</code> is identical to <code>ids2</code>;
     *         <code>false</code>, otherwise
     */
    public static boolean equals(int[] ids1, int[] ids2) {
        if ((ids1 == null || ids1.length == 0) && (ids2 == null || ids2.length == 0)) {
            return true;
        }
        else if (ids1 != null && ids2 != null && ids1.length == ids2.length) {
            List<Integer> list1 = asList(ids1);
            List<Integer> list2 = asList(ids2);
            return list1.containsAll(list2);
        }
        else {
            return false;
        }
    }

    /**
     * Returns a string representation of the specified date.
     * @param date the date
     * @return the string representation of <code>date</code>
     */
    public static final String format(Date date) {
        if (date != null) {
            synchronized (DATE_FORMAT) {
                return DATE_FORMAT.format(date);
            }
        }
        else {
            return null;
        }
    }

    // GKIM, 2007-05-15: simplified and moved from Constants, which should not have any methods
    public static String getRequiredPermission(TemplateUsageType usageType, boolean viewOnly) {
        return usageType.getPrivilege() == null ? null : (viewOnly ? "View" : "Edit") + usageType.getPrivilege();
    }

    /**
     * Compares a char array to input String and returns true if a member of
     * char array is present in the string
     * @param chr chr
     * @param str str
     * @return boolean
     */
    public static final boolean hasIllegalCharacters(char[] chr, String str) {
        for (int i = 0; i < chr.length; i++) {
            if (str.indexOf(chr[i]) > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tests if <code>ids1</code> is contained in <code>ids</code>.
     * Contained means all elements of <code>ids1</code> is an element of <code>ids</code>.
     * This retuns true if ids1 is null, or false if ids2 is null
     * @param ids1 int array to test membership for
     * @param ids2 int array to test membership of <code>ids1</code>
     * @return <code>true</code> if <code>ids1</code> is contained in <code>ids</code>
     */
    public static boolean isContainedIn(int[] ids1, int[] ids2) {
        if (ids1 == null) return true;
        if (ids2 == null) return false;
        if (ids1.length > ids2.length) return false;
        for (int i = 0; i < ids1.length; i++) {
            if (!isMember(ids1[i], ids2)) {
                return false;
            }
        }
        return true;
    }

    public static final boolean isEmpty(Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }

    /**
     * Tests if the specified int array is null or empty.
     * @param intArray intArray
     * @return <code>true</code> if <code>intArray</code> is null or empty; <code>false</code>, otherwise
     * @since PowerEditor 4.3.6
     */
    public static final boolean isEmpty(int[] intArray) {
        return (intArray == null || intArray.length == 0);
    }

    /**
     * Tests if the specified string is null or empty.
     * @param str string to test
     * @return <code>true</code> if <code>str</code> is null or of zero length; <code>false</code>, otherwise
     * @since PowerEditor 4.2.0
     */
    public static final boolean isEmpty(String str) {
        return (str == null || str.length() == 0);
    }

    /**
     * Tests if the specified string is null or empty after trim().
     * @param str string to test
     * @return <code>true</code> if <code>str</code> is null or of zero length after trim; <code>false</code>, otherwise
     * @since PowerEditor 4.5.0
     */
    public static final boolean isEmptyAfterTrim(String str) {
        return (str == null || str.trim().length() == 0);
    }

    public static final boolean isEmptyCellValue(Object obj) {
        return obj == null || isEmpty(obj.toString()) || ((obj instanceof IRange) && ((IRange) obj).isEmpty());
    }

    public static final boolean isMember(int value, int[] ia) {
        for (int i = 0; i < ia.length; i++) {
            if (ia[i] == value) {
                return true;
            }
        }
        return false;
    }

    public static final boolean isMember(Object obj, Object[] array) {
        if (obj == null) throw new NullPointerException("obj cannot be null");
        for (int i = 0; i < array.length; i++) {
            if (obj.equals(array[i])) return true;
        }
        return false;
    }

    /**
     * Tests if the specified value is a member of the specified string array.
     * Equivalent to <code>isMember(value, set, false)</code>.
     * @param value can be <code>null</code>
     * @param set set
     * @return true if member; false, otherwise
     * @throws NullPointerException if <code>value</code> is null, or <code>set</code> is <code>null</code>
     */
    public static final boolean isMember(String value, String[] set) {
        return isMember(value, set, false);
    }

    public static final boolean isMember(String value, String[] set, boolean ignoreCase) {
        if (value == null) throw new NullPointerException("value cannot be null");
        for (int i = 0; i < set.length; i++) {
            if ((ignoreCase && value.equalsIgnoreCase(set[i]) || value.equals(set[i]))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tests the equality of two objects.
     * This allows any of the specified objects to be null.
     * @param obj object one
     * @param obj1 object two
     * @return <code>true</code> if obj is identical to obj1
     */
    public static final boolean isSame(Object obj, Object obj1) {
        if (obj == obj1) return true;
        if (obj != null && obj1 == null) return false;
        if (obj == null && obj1 != null) return false;
        if (obj == null && obj1 == null)
            return true;
        else {
            return obj.equals(obj1);
        }
    }

    /**
     * Tests if the specified two collections contains the same objects.
     * This assumes each list contains no more than one unique objects.
     *
     * @param <T> any type
     * @param list1 list1
     * @param list2 list2
     * @return <code>true</code> if <code>list1</code> and <code>list2</code> contains the same elements
     */
    public static final <T> boolean isSameCollection(Collection<T> list1, Collection<T> list2) {
        if (list1 == list2) return true;
        if (list1 == null || list2 == null) return false;
        if (list1.size() != list2.size()) return false;
        for (T item : list1) {
            if (!list2.contains(item)) return false;
        }
        return true;
    }

    /**
     * Tests the equality of two objects as grid cell value.
     * This is identical to {@link #isSame(Object, Object)} except this considers an empty string the same as <code>null</code>.
     * @param obj1 object one
     * @param obj2 object two
     * @return <code>true</code> if obj1 is identical to obj2 as grid cell value
     */
    public static final boolean isSameGridCellValue(Object obj1, Object obj2) {
        Object o1 = isEmptyCellValue(obj1) ? null : obj1;
        Object o2 = isEmptyCellValue(obj2) ? null : obj2;
        return isSame(o1, o2);
    }

    public static boolean isTrue(final Boolean booleanFlag) {
        return asBoolean(booleanFlag, false);
    }

    public static boolean isValidSymbol(String s) {
        if (s == null || s.length() == 0) {
            return false;
        }
        return VALID_SYMBOL_PATTERN.matcher(s).matches();
    }

    public static final boolean nullSafeEquals(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }

    /**
     * Converts the specified string to a list of strings.
     * Delimter is a comma or a space.
     * @param s the string to convert
     * @return a list of strings
     */
    public static List<String> parseList(String s) {
        List<String> list = new ArrayList<String>();
        for (StringTokenizer tokens = new StringTokenizer(s, "[, ]", false); tokens.hasMoreTokens();) {
            list.add(tokens.nextToken());
        }
        return list;
    }

    /**
     * Removes all blanks from the specified string
     * @param s str to remove blanks
     * @return s with no blanks
     * @throws NullPointerException if s is null
     */
    public static final String removeBlanks(String s) {
        String[] strs = s.split("\\s");
        StringBuilder buff = new StringBuilder();
        for (int i = 0; i < strs.length; i++) {
            buff.append(strs[i]);
        }
        return buff.toString();
    }

    /**
     * Gets a new map that has key-value reversed from the source map.
     * That is, if source has a mapping of key-&gt;value, the returned map
     * will have a mapping of value-&gt;key.
     * @param <T1> key type
     * @param <T2> value type
     * @param source source
     * @return reversed map of <code>source</code>
     */
    public static <T1, T2> Map<T2, T1> reverseMapping(Map<T1, T2> source) {
        Map<T2, T1> newMap = new HashMap<T2, T1>();
        for (Map.Entry<T1, T2> entry : source.entrySet()) {
            newMap.put(entry.getValue(), entry.getKey());
        }
        return newMap;
    }

    public static String strip(String source, String toBeStripped) {
        return source == null ? null : isEmpty(toBeStripped) ? source : toString(source.split(toBeStripped), null);
    }

    public static int[] toIntArray(List<? extends Number> intList) {
        int[] ids = new int[intList.size()];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = intList.get(i).intValue();
        }
        return ids;
    }


    /**
     * Converts the specified string into an int array.
     * Values in the string shall be separasted by a comma.
     * @param str the string to convert
     * @return the converted int array; never <code>null</code>
     * @see #toString(int[])
     */
    public static int[] toIntArray(String str) {
        if (str == null || str.length() == 0) {
            return new int[0];
        }
        else {
            StringTokenizer st = new StringTokenizer(str, ",");
            List<String> list = new ArrayList<String>();
            while (st.hasMoreTokens()) {
                list.add(st.nextToken());
            }
            int[] array = new int[list.size()];
            for (int i = 0; i < array.length; i++) {
                array[i] = Integer.parseInt(list.get(i));
            }
            return array;
        }
    }

    public static List<Integer> toIntegerList(int[] ids) {
        List<Integer> list = new LinkedList<Integer>();
        for (int i = 0; i < ids.length; i++) {
            list.add(ids[i]);
        }
        return list;
    }

    public static int[] toIntSequence(int limit) {
        return toIntSequence(1, limit);
    }

    public static int[] toIntSequence(int start, int limit) {
        int[] ids = new int[limit];
        for (int i = 0; i < limit; i++) {
            ids[i] = start + i;
        }
        return ids;
    }

    public static String toString(Collection<?> objects) {
        return toString(objects, ",");
    }

    public static String toString(Collection<?> objects, String separator) {
        if (objects == null || objects.isEmpty()) {
            return "";
        }

        StringBuilder buff = new StringBuilder();
        for (Iterator<?> iter = objects.iterator(); iter.hasNext();) {
            buff.append(iter.next());
            if (iter.hasNext() && separator != null) {
                buff.append(separator);
            }
        }
        return buff.toString();
    }

    /**
     * Generate a string with values in the specified array.
     * The values are comma separated.
     * @param intArray the int array
     * @return the string
     * @see #toIntArray(String)
     */
    public static String toString(int[] intArray) {
        if (intArray == null || intArray.length == 0) {
            return "";
        }
        StringBuilder buff = new StringBuilder();
        buff.append(intArray[0]);
        for (int i = 1; i < intArray.length; i++) {
            buff.append(",");
            buff.append(intArray[i]);
        }
        return buff.toString();

    }

    public static String toString(Object[] objs) {
        return toString(objs, ",");
    }

    public static String toString(Object[] objs, String separator) {
        if (objs == null) {
            return "";
        }

        StringBuilder buff = new StringBuilder();
        for (int i = 0; i < objs.length; i++) {
            if (i > 0 && separator != null) {
                buff.append(separator);
            }
            buff.append(String.valueOf(objs[i]));
        }
        return buff.toString();
    }

    /**
     * Trims the specified string.
     * This returns an empty string if <code>s</code> is null.
     * @param s the string to trim
     * @return trimmed string; never null
     */
    public static final String trim(String s) {
        if (s != null)
            return s.trim();
        else
            return new String("");
    }
    
    public static final String removeTrailing(String s, final char c) {
        if (null != s) {
            int l = s.length();
            if (0 != l) {
                if (c == s.charAt(l - 1)) {
                    s = s.substring(0, l - 1);
                }
            }
        }
        return s;
    }
    
    public static final String removeTrailing(String s, final String t) {
        if ((null != s) && (null != t)) {
            int sl = s.length();
            int tl = t.length();
            if (sl >= tl) {
                int i = s.lastIndexOf(t);
                if (sl == (i + tl))
                    s = s.substring(0, sl - tl);
            }
        }
        return s;
    }

    public static final String removeAllTrailing(String s, final char c) {
        if (null != s) {
            if (0 != s.length()) {
                StringBuffer b = new StringBuffer(s);
                b.reverse();
                while ((0 != b.length()) && (c == b.charAt(0))) {
                    b.deleteCharAt(0);
                }
                b.reverse();
                s = b.toString();
            }
        }
        return s;
    }

    public static final String removeAllTrailing(String s, String t) {
        if ((null != s) && (null != t)) {
            if (s.length() >= t.length()) {
                StringBuffer tb = new StringBuffer(t);
                tb.reverse();
                String tr = tb.toString();
                int tl = tr.length();
                
                StringBuffer sb = new StringBuffer(s);
                sb.reverse();
                while (0 == sb.indexOf(tr)) {
                    sb.delete(0, tl);
                }
                sb.reverse();
                s = sb.toString();
            }
        }
        return s;
    }    

    public static Font resizeFont(Font font, double factor) {
        String name = font.getName();
        int size = font.getSize();
        int style = font.getStyle();
        int newSize = (int) (factor * size);
        Font newFont = new Font(name, style, newSize);
        return newFont;
    }
    
    public static void resizeLabel(JLabel label, double factor) {
        Font oldFont = label.getFont();
        Font newFont = resizeFont(oldFont, factor);
        label.setFont(newFont);
    }

    public static String addHtmlCenterTags(String input) {
        return String.format("<html><center>%s</center></html>", input);
    }

    /**
     * Default constructor.
     *
     */
    protected UtilBase() {
    }

}
