
package com.mindbox.test.pe.webservices.wsimportgen;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for deployWithCredentials complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="deployWithCredentials">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="arg0" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="arg1" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="arg2" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="arg3" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="arg4" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="arg5" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="arg6" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="arg7" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="arg8" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="arg9" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="arg10" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="arg11" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="arg12" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="arg13" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="arg14" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="arg15" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="arg16" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="arg17" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="arg18" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "deployWithCredentials", propOrder = {
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
    "arg18"
})
public class DeployWithCredentials {

    protected String arg0;
    @XmlElement(nillable = true)
    protected List<String> arg1;
    protected boolean arg2;
    @XmlElement(nillable = true)
    protected List<Integer> arg3;
    protected boolean arg4;
    @XmlElement(nillable = true)
    protected List<Integer> arg5;
    protected boolean arg6;
    protected int arg7;
    protected String arg8;
    protected boolean arg9;
    protected boolean arg10;
    protected boolean arg11;
    protected boolean arg12;
    protected boolean arg13;
    protected boolean arg14;
    protected String arg15;
    protected boolean arg16;
    protected String arg17;
    protected String arg18;

    /**
     * Gets the value of the arg0 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArg0() {
        return arg0;
    }

    /**
     * Sets the value of the arg0 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArg0(String value) {
        this.arg0 = value;
    }

    /**
     * Gets the value of the arg1 property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the arg1 property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getArg1().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getArg1() {
        if (arg1 == null) {
            arg1 = new ArrayList<String>();
        }
        return this.arg1;
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
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the arg3 property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getArg3().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getArg3() {
        if (arg3 == null) {
            arg3 = new ArrayList<Integer>();
        }
        return this.arg3;
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
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the arg5 property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getArg5().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getArg5() {
        if (arg5 == null) {
            arg5 = new ArrayList<Integer>();
        }
        return this.arg5;
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
    public int getArg7() {
        return arg7;
    }

    /**
     * Sets the value of the arg7 property.
     * 
     */
    public void setArg7(int value) {
        this.arg7 = value;
    }

    /**
     * Gets the value of the arg8 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArg8() {
        return arg8;
    }

    /**
     * Sets the value of the arg8 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArg8(String value) {
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
     */
    public boolean isArg11() {
        return arg11;
    }

    /**
     * Sets the value of the arg11 property.
     * 
     */
    public void setArg11(boolean value) {
        this.arg11 = value;
    }

    /**
     * Gets the value of the arg12 property.
     * 
     */
    public boolean isArg12() {
        return arg12;
    }

    /**
     * Sets the value of the arg12 property.
     * 
     */
    public void setArg12(boolean value) {
        this.arg12 = value;
    }

    /**
     * Gets the value of the arg13 property.
     * 
     */
    public boolean isArg13() {
        return arg13;
    }

    /**
     * Sets the value of the arg13 property.
     * 
     */
    public void setArg13(boolean value) {
        this.arg13 = value;
    }

    /**
     * Gets the value of the arg14 property.
     * 
     */
    public boolean isArg14() {
        return arg14;
    }

    /**
     * Sets the value of the arg14 property.
     * 
     */
    public void setArg14(boolean value) {
        this.arg14 = value;
    }

    /**
     * Gets the value of the arg15 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArg15() {
        return arg15;
    }

    /**
     * Sets the value of the arg15 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArg15(String value) {
        this.arg15 = value;
    }

    /**
     * Gets the value of the arg16 property.
     * 
     */
    public boolean isArg16() {
        return arg16;
    }

    /**
     * Sets the value of the arg16 property.
     * 
     */
    public void setArg16(boolean value) {
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

}
