
package com.mindbox.test.pe.webservices.wsimportgen;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for exportDataWithCredentials complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="exportDataWithCredentials">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="arg0" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="arg1" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="arg2" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="arg3" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="arg4" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="arg5" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="arg6" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="arg7" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="arg8" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="arg9" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="arg10" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="arg11" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="arg12" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="arg13" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="arg14" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="arg15" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="arg16" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="arg17" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="arg18" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="arg19" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="arg20" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "exportDataWithCredentials", propOrder = {
    "arg0",
    "arg1",
    "arg2",
    "arg3",
    "arg4",
    "arg5",
    "arg6",
    "arg7",
    "arg8",
    "arg9",
    "arg10",
    "arg11",
    "arg12",
    "arg13",
    "arg14",
    "arg15",
    "arg16",
    "arg17",
    "arg18",
    "arg19",
    "arg20"
})
public class ExportDataWithCredentials {

    protected boolean arg0;
    protected boolean arg1;
    protected boolean arg2;
    protected boolean arg3;
    protected boolean arg4;
    protected boolean arg5;
    protected boolean arg6;
    protected boolean arg7;
    protected boolean arg8;
    protected boolean arg9;
    protected boolean arg10;
    protected String arg11;
    @XmlElement(nillable = true)
    protected List<String> arg12;
    @XmlElement(nillable = true)
    protected List<Integer> arg13;
    @XmlElement(nillable = true)
    protected List<Integer> arg14;
    protected boolean arg15;
    protected int arg16;
    protected String arg17;
    protected String arg18;
    protected String arg19;
    protected String arg20;

    /**
     * Gets the value of the arg0 property.
     * 
     */
    public boolean isArg0() {
        return arg0;
    }

    /**
     * Sets the value of the arg0 property.
     * 
     */
    public void setArg0(boolean value) {
        this.arg0 = value;
    }

    /**
     * Gets the value of the arg1 property.
     * 
     */
    public boolean isArg1() {
        return arg1;
    }

    /**
     * Sets the value of the arg1 property.
     * 
     */
    public void setArg1(boolean value) {
        this.arg1 = value;
    }

    /**
     * Gets the value of the arg2 property.
     * 
     */
    public boolean isArg2() {
        return arg2;
    }

    /**
     * Sets the value of the arg2 property.
     * 
     */
    public void setArg2(boolean value) {
        this.arg2 = value;
    }

    /**
     * Gets the value of the arg3 property.
     * 
     */
    public boolean isArg3() {
        return arg3;
    }

    /**
     * Sets the value of the arg3 property.
     * 
     */
    public void setArg3(boolean value) {
        this.arg3 = value;
    }

    /**
     * Gets the value of the arg4 property.
     * 
     */
    public boolean isArg4() {
        return arg4;
    }

    /**
     * Sets the value of the arg4 property.
     * 
     */
    public void setArg4(boolean value) {
        this.arg4 = value;
    }

    /**
     * Gets the value of the arg5 property.
     * 
     */
    public boolean isArg5() {
        return arg5;
    }

    /**
     * Sets the value of the arg5 property.
     * 
     */
    public void setArg5(boolean value) {
        this.arg5 = value;
    }

    /**
     * Gets the value of the arg6 property.
     * 
     */
    public boolean isArg6() {
        return arg6;
    }

    /**
     * Sets the value of the arg6 property.
     * 
     */
    public void setArg6(boolean value) {
        this.arg6 = value;
    }

    /**
     * Gets the value of the arg7 property.
     * 
     */
    public boolean isArg7() {
        return arg7;
    }

    /**
     * Sets the value of the arg7 property.
     * 
     */
    public void setArg7(boolean value) {
        this.arg7 = value;
    }

    /**
     * Gets the value of the arg8 property.
     * 
     */
    public boolean isArg8() {
        return arg8;
    }

    /**
     * Sets the value of the arg8 property.
     * 
     */
    public void setArg8(boolean value) {
        this.arg8 = value;
    }

    /**
     * Gets the value of the arg9 property.
     * 
     */
    public boolean isArg9() {
        return arg9;
    }

    /**
     * Sets the value of the arg9 property.
     * 
     */
    public void setArg9(boolean value) {
        this.arg9 = value;
    }

    /**
     * Gets the value of the arg10 property.
     * 
     */
    public boolean isArg10() {
        return arg10;
    }

    /**
     * Sets the value of the arg10 property.
     * 
     */
    public void setArg10(boolean value) {
        this.arg10 = value;
    }

    /**
     * Gets the value of the arg11 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArg11() {
        return arg11;
    }

    /**
     * Sets the value of the arg11 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArg11(String value) {
        this.arg11 = value;
    }

    /**
     * Gets the value of the arg12 property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the arg12 property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getArg12().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getArg12() {
        if (arg12 == null) {
            arg12 = new ArrayList<String>();
        }
        return this.arg12;
    }

    /**
     * Gets the value of the arg13 property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the arg13 property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getArg13().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getArg13() {
        if (arg13 == null) {
            arg13 = new ArrayList<Integer>();
        }
        return this.arg13;
    }

    /**
     * Gets the value of the arg14 property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the arg14 property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getArg14().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getArg14() {
        if (arg14 == null) {
            arg14 = new ArrayList<Integer>();
        }
        return this.arg14;
    }

    /**
     * Gets the value of the arg15 property.
     * 
     */
    public boolean isArg15() {
        return arg15;
    }

    /**
     * Sets the value of the arg15 property.
     * 
     */
    public void setArg15(boolean value) {
        this.arg15 = value;
    }

    /**
     * Gets the value of the arg16 property.
     * 
     */
    public int getArg16() {
        return arg16;
    }

    /**
     * Sets the value of the arg16 property.
     * 
     */
    public void setArg16(int value) {
        this.arg16 = value;
    }

    /**
     * Gets the value of the arg17 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArg17() {
        return arg17;
    }

    /**
     * Sets the value of the arg17 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArg17(String value) {
        this.arg17 = value;
    }

    /**
     * Gets the value of the arg18 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArg18() {
        return arg18;
    }

    /**
     * Sets the value of the arg18 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArg18(String value) {
        this.arg18 = value;
    }

    /**
     * Gets the value of the arg19 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArg19() {
        return arg19;
    }

    /**
     * Sets the value of the arg19 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArg19(String value) {
        this.arg19 = value;
    }

    /**
     * Gets the value of the arg20 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArg20() {
        return arg20;
    }

    /**
     * Sets the value of the arg20 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArg20(String value) {
        this.arg20 = value;
    }

}
