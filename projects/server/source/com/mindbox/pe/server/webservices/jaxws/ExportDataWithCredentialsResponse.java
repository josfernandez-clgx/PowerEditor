
package com.mindbox.pe.server.webservices.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "exportDataWithCredentialsResponse", namespace = "http://webservices.server.pe.mindbox.com/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "exportDataWithCredentialsResponse", namespace = "http://webservices.server.pe.mindbox.com/")
public class ExportDataWithCredentialsResponse {

    @XmlElement(name = "return", namespace = "")
    private com.mindbox.pe.server.webservices.PowerEditorInterfaceReturnStructure _return;

    /**
     * 
     * @return
     *     returns PowerEditorInterfaceReturnStructure
     */
    public com.mindbox.pe.server.webservices.PowerEditorInterfaceReturnStructure getReturn() {
        return this._return;
    }

    /**
     * 
     * @param _return
     *     the value for the _return property
     */
    public void setReturn(com.mindbox.pe.server.webservices.PowerEditorInterfaceReturnStructure _return) {
        this._return = _return;
    }

}
