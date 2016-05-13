package com.mindbox.pe.model;

import java.util.Date;

import net.sf.oval.constraint.NotNull;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.ConfigUtil;

/**
 * Date Synonym.
 * To create a named instance, use the constructor {@link #DateSynonym(int, String, String, Date)}.
 * To create an unnamed instance, use the static factory method, {@link #createUnnamedInstance(Date)}.
 * @author Geneho
 * @since PowerEditor 4.2.0
 */
public final class DateSynonym extends AbstractIDNameDescriptionObject implements Auditable {

	private static final long serialVersionUID = 20041208170000L;
    
   /**
     * Default constructor. Only invoked by Digester during import. All
     * date synonyms should be named in the future.
     */
    public DateSynonym() {
        super(UNASSIGNED_ID, "", null);
        isNamed = true;
    }   
    
		
	/**
	 * Creates a new unnamed date synonym.
	 * @param date
	 * @return newly created unnamed date synonym
	 */
	public static DateSynonym createUnnamedInstance(Date date) {
		String postfix = String.valueOf(System.currentTimeMillis());
		return new DateSynonym(-1, postfix, "Unnamed-"+postfix, date, false);
	}
	
	/**
	 * Creates an unnamed date synonym. Do not use this from client.
	 * This is for creating alreay saved date synonym at cache loading time.
	 * @param id must be greater than 0
	 * @param name name
	 * @param desc description
	 * @param date date
	 * @return newly created unnamed date synonym
	 * @throws IllegalArgumentException if <code>id</code> &lt; 1
	 */
	public static DateSynonym createUnnamedInstance(int id, String name, String desc, Date date) {
		if (id < 1) throw new IllegalArgumentException("id must be greater than 0");
		return new DateSynonym(id,name,desc,date,false);
	}
	
	@NotNull
	private Date theDate = null;
	private final boolean isNamed;
	
	// Indicates if this date synonym is not in use; i.e., filtered out by KB Filter configuration
	private boolean notInUse = false;

	/**
	 * Create a new named date synonym.
	 * Identical to <code>DateSynonym(id,name,desc,false)</code>.
	 * @param id
	 * @param name
	 * @param desc
	 * @param date cannot be null
	 * @throws NullPointerException if <code>date</code> is <code>null</code>
	 */
	public DateSynonym(int id, String name, String desc, Date date) {
		this(id, name, desc, date, true);
	}
	
	private DateSynonym(int id, String name, String desc, Date date, boolean isNamed) {
		super(id, name, desc);
		checkDate(date);
		this.theDate = date;
		this.isNamed = isNamed;
	}
	

	/**
	 * 
	 * @param ds
	 * @return <code>true</code> if this is after ds or if ds is <code>null</code>; <code>false</code>, otherwise
	 */
	public boolean after(DateSynonym ds) {
		if (ds == null) return true;
		return theDate.after(ds.getDate());
	}
	
	/**
	 * 
	 * @param ds
	 * @return <code>true</code> if ds is not <code>null</code> and this is before ds; <code>false</code>, otherwise
	 */
	public boolean before(DateSynonym ds) {
		if (ds == null) return false;
		return theDate.before(ds.getDate());
	}
	
	/**
	 * Tests if this is not after the specified date synonym
	 * @param ds date synonym to test against
	 * @return <code>true</code> if <code>ds</code> is not <code>null</code> and this is not after <code>ds</code>; <code>false</code>, otherwise
	 * @since 5.1.0
	 */
	public boolean notAfter(DateSynonym ds) {
		if (ds == null) return false;
		return !theDate.after(ds.getDate());
	}
	
	/**
	 * Tests if this is not before the specified date synonym
	 * @param ds date synonym to test against
	 * @return <code>true</code> if is not before <code>ds</code> or <code>ds</code> is <code>null</code>; <code>false</code>, otherwise
	 * @since 5.1.0
	 */
	public boolean notBefore(DateSynonym ds) {
		if (ds == null) return true;
		return !theDate.before(ds.getDate());
	}
	
	/**
	 * Tests if this is a named date synonym.
	 * @return <code>true</code> if this is a named date synonym; <code>false</code>, otherwise
	 */
	public boolean isNamed() {
		return isNamed;
	}
	
	/**
	 * Tests equality.
	 * This just compares the id. That is, this returns <code>true</code>, if and only if
	 * <code>obj</code> is an instance of this and <code>this.getID() == obj.getID()</code>.

	 */
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof DateSynonym) {
			return this.getID() == ((DateSynonym) obj).getID();
		}
		else {
			return false;
		}
	}

	public int hashCode() {
		return this.getID();
	}
	
	public boolean isSameDate(DateSynonym synonym) {
		if (synonym == null) return false;
		if (synonym == this || synonym.getID() == getID()) return true;
		return UtilBase.isSame(this.theDate, synonym.theDate);
	}
	
	public Auditable deepCopy() {
		DateSynonym copy = new DateSynonym(getID(), getName(), getDescription(), theDate);
		return copy;
	}
	
	public String getAuditDescription() {
		return "date '"+ getName() + "'";
	}
	
	public void copyFrom(DateSynonym source) {
		setName(source.getName());
		setDescription(source.getDescription());
		this.theDate = source.theDate;
	}

	/**
	 * Gets the date of this. Never <code>null</code>
	 * @return date of this; never <code>null</code>
	 */
	public Date getDate() {
		return theDate;
	}

	/**
	 * 
	 * @param date
	 * @throws NullPointerException if <code>date</code> is <code>null</code>
	 */
	public void setDate(Date date) {
		checkDate(date);
		this.theDate = date;
	}

    /**
     * Added for digest support.
     */
    public void setDateString(String dateStr) {
        this.theDate = ConfigUtil.toDate(dateStr);
    }

    public String toString() {
        return ((theDate == null) ? "" : getName());
	}
	
	private void checkDate(Date date) {
		if (date == null) throw new NullPointerException("date cannot be null");
	}

	public boolean isNotInUse() {
		return notInUse;
	}

	public void setNotInUse(boolean notInUse) {
		this.notInUse = notInUse;
	}
	
}
