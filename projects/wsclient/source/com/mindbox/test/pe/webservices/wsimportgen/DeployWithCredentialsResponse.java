
package com.mindbox.test.pe.webservices.wsimportgen;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for deployWithCredentialsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="deployWithCredentialsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://webservices.server.pe.mindbox.com/}powerEditorInterfaceReturnStructure" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "deployWithCredentialsResponse", propOrder = {
    "_return"
})
public class DeployWithCredentialsResponse {

    @XmlElement(name = "return")
    protected PowerEditorInterfaceReturnStructure _return;

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link PowerEditorInterfaceReturnStructure }
     *     
     */
    public PowerEditorInterfaceReturnStructure getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link PowerEditorInterfaceReturnStructure }
     *     
     */
    public void setReturn(PowerEditorInterfaceReturnStructure value) {
        this._return = value;
    }

}
