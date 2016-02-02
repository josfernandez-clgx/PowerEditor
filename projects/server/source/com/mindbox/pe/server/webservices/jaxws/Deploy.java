
package com.mindbox.pe.server.webservices.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "deploy", namespace = "http://webservices.server.pe.mindbox.com/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "deploy", namespace = "http://webservices.server.pe.mindbox.com/", propOrder = {
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
    "arg16"
})
public class Deploy {

    @XmlElement(name = "arg0", namespace = "")
    private String arg0;
    @XmlElement(name = "arg1", namespace = "", nillable = true)
    private String[] arg1;
    @XmlElement(name = "arg2", namespace = "")
    private boolean arg2;
    @XmlElement(name = "arg3", namespace = "", nillable = true)
    private int[] arg3;
    @XmlElement(name = "arg4", namespace = "")
    private boolean arg4;
    @XmlElement(name = "arg5", namespace = "", nillable = true)
    private int[] arg5;
    @XmlElement(name = "arg6", namespace = "")
    private boolean arg6;
    @XmlElement(name = "arg7", namespace = "")
    private int arg7;
    @XmlElement(name = "arg8", namespace = "")
    private String arg8;
    @XmlElement(name = "arg9", namespace = "")
    private boolean arg9;
    @XmlElement(name = "arg10", namespace = "")
    private boolean arg10;
    @XmlElement(name = "arg11", namespace = "")
    private boolean arg11;
    @XmlElement(name = "arg12", namespace = "")
    private boolean arg12;
    @XmlElement(name = "arg13", namespace = "")
    private boolean arg13;
    @XmlElement(name = "arg14", namespace = "")
    private boolean arg14;
    @XmlElement(name = "arg15", namespace = "")
    private String arg15;
    @XmlElement(name = "arg16", namespace = "")
    private boolean arg16;

    /**
     * 
     * @return
     *     returns String
     */
    public String getArg0() {
        return this.arg0;
    }

    /**
     * 
     * @param arg0
     *     the value for the arg0 property
     */
    public void setArg0(String arg0) {
        this.arg0 = arg0;
    }

    /**
     * 
     * @return
     *     returns String[]
     */
    public String[] getArg1() {
        return this.arg1;
    }

    /**
     * 
     * @param arg1
     *     the value for the arg1 property
     */
    public void setArg1(String[] arg1) {
        this.arg1 = arg1;
    }

    /**
     * 
     * @return
     *     returns boolean
     */
    public boolean isArg2() {
        return this.arg2;
    }

    /**
     * 
     * @param arg2
     *     the value for the arg2 property
     */
    public void setArg2(boolean arg2) {
        this.arg2 = arg2;
    }

    /**
     * 
     * @return
     *     returns int[]
     */
    public int[] getArg3() {
        return this.arg3;
    }

    /**
     * 
     * @param arg3
     *     the value for the arg3 property
     */
    public void setArg3(int[] arg3) {
        this.arg3 = arg3;
    }

    /**
     * 
     * @return
     *     returns boolean
     */
    public boolean isArg4() {
        return this.arg4;
    }

    /**
     * 
     * @param arg4
     *     the value for the arg4 property
     */
    public void setArg4(boolean arg4) {
        this.arg4 = arg4;
    }

    /**
     * 
     * @return
     *     returns int[]
     */
    public int[] getArg5() {
        return this.arg5;
    }

    /**
     * 
     * @param arg5
     *     the value for the arg5 property
     */
    public void setArg5(int[] arg5) {
        this.arg5 = arg5;
    }

    /**
     * 
     * @return
     *     returns boolean
     */
    public boolean isArg6() {
        return this.arg6;
    }

    /**
     * 
     * @param arg6
     *     the value for the arg6 property
     */
    public void setArg6(boolean arg6) {
        this.arg6 = arg6;
    }

    /**
     * 
     * @return
     *     returns int
     */
    public int getArg7() {
        return this.arg7;
    }

    /**
     * 
     * @param arg7
     *     the value for the arg7 property
     */
    public void setArg7(int arg7) {
        this.arg7 = arg7;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getArg8() {
        return this.arg8;
    }

    /**
     * 
     * @param arg8
     *     the value for the arg8 property
     */
    public void setArg8(String arg8) {
        this.arg8 = arg8;
    }

    /**
     * 
     * @return
     *     returns boolean
     */
    public boolean isArg9() {
        return this.arg9;
    }

    /**
     * 
     * @param arg9
     *     the value for the arg9 property
     */
    public void setArg9(boolean arg9) {
        this.arg9 = arg9;
    }

    /**
     * 
     * @return
     *     returns boolean
     */
    public boolean isArg10() {
        return this.arg10;
    }

    /**
     * 
     * @param arg10
     *     the value for the arg10 property
     */
    public void setArg10(boolean arg10) {
        this.arg10 = arg10;
    }

    /**
     * 
     * @return
     *     returns boolean
     */
    public boolean isArg11() {
        return this.arg11;
    }

    /**
     * 
     * @param arg11
     *     the value for the arg11 property
     */
    public void setArg11(boolean arg11) {
        this.arg11 = arg11;
    }

    /**
     * 
     * @return
     *     returns boolean
     */
    public boolean isArg12() {
        return this.arg12;
    }

    /**
     * 
     * @param arg12
     *     the value for the arg12 property
     */
    public void setArg12(boolean arg12) {
        this.arg12 = arg12;
    }

    /**
     * 
     * @return
     *     returns boolean
     */
    public boolean isArg13() {
        return this.arg13;
    }

    /**
     * 
     * @param arg13
     *     the value for the arg13 property
     */
    public void setArg13(boolean arg13) {
        this.arg13 = arg13;
    }

    /**
     * 
     * @return
     *     returns boolean
     */
    public boolean isArg14() {
        return this.arg14;
    }

    /**
     * 
     * @param arg14
     *     the value for the arg14 property
     */
    public void setArg14(boolean arg14) {
        this.arg14 = arg14;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getArg15() {
        return this.arg15;
    }

    /**
     * 
     * @param arg15
     *     the value for the arg15 property
     */
    public void setArg15(String arg15) {
        this.arg15 = arg15;
    }

    /**
     * 
     * @return
     *     returns boolean
     */
    public boolean isArg16() {
        return this.arg16;
    }

    /**
     * 
     * @param arg16
     *     the value for the arg16 property
     */
    public void setArg16(boolean arg16) {
        this.arg16 = arg16;
    }

}
