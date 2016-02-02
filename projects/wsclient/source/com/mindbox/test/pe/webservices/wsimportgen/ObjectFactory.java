
package com.mindbox.test.pe.webservices.wsimportgen;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.mindbox.test.pe.webservices.wsimportgen package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ExportDataWithCredentials_QNAME = new QName("http://webservices.server.pe.mindbox.com/", "exportDataWithCredentials");
    private final static QName _Ping_QNAME = new QName("http://webservices.server.pe.mindbox.com/", "ping");
    private final static QName _DeployWithCredentials_QNAME = new QName("http://webservices.server.pe.mindbox.com/", "deployWithCredentials");
    private final static QName _DeployResponse_QNAME = new QName("http://webservices.server.pe.mindbox.com/", "deployResponse");
    private final static QName _ImportEntitiesWithCredentials_QNAME = new QName("http://webservices.server.pe.mindbox.com/", "importEntitiesWithCredentials");
    private final static QName _PingResponse_QNAME = new QName("http://webservices.server.pe.mindbox.com/", "pingResponse");
    private final static QName _ImportEntitiesWithCredentialsResponse_QNAME = new QName("http://webservices.server.pe.mindbox.com/", "importEntitiesWithCredentialsResponse");
    private final static QName _ExportDataWithCredentialsResponse_QNAME = new QName("http://webservices.server.pe.mindbox.com/", "exportDataWithCredentialsResponse");
    private final static QName _Deploy_QNAME = new QName("http://webservices.server.pe.mindbox.com/", "deploy");
    private final static QName _ImportEntities_QNAME = new QName("http://webservices.server.pe.mindbox.com/", "importEntities");
    private final static QName _ImportEntitiesResponse_QNAME = new QName("http://webservices.server.pe.mindbox.com/", "importEntitiesResponse");
    private final static QName _ExportDataResponse_QNAME = new QName("http://webservices.server.pe.mindbox.com/", "exportDataResponse");
    private final static QName _ExportData_QNAME = new QName("http://webservices.server.pe.mindbox.com/", "exportData");
    private final static QName _DeployWithCredentialsResponse_QNAME = new QName("http://webservices.server.pe.mindbox.com/", "deployWithCredentialsResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.mindbox.test.pe.webservices.wsimportgen
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PingResponse }
     * 
     */
    public PingResponse createPingResponse() {
        return new PingResponse();
    }

    /**
     * Create an instance of {@link ImportEntities }
     * 
     */
    public ImportEntities createImportEntities() {
        return new ImportEntities();
    }

    /**
     * Create an instance of {@link PowerEditorInterfaceReturnStructure }
     * 
     */
    public PowerEditorInterfaceReturnStructure createPowerEditorInterfaceReturnStructure() {
        return new PowerEditorInterfaceReturnStructure();
    }

    /**
     * Create an instance of {@link DeployResponse }
     * 
     */
    public DeployResponse createDeployResponse() {
        return new DeployResponse();
    }

    /**
     * Create an instance of {@link ExportData }
     * 
     */
    public ExportData createExportData() {
        return new ExportData();
    }

    /**
     * Create an instance of {@link ImportEntitiesWithCredentialsResponse }
     * 
     */
    public ImportEntitiesWithCredentialsResponse createImportEntitiesWithCredentialsResponse() {
        return new ImportEntitiesWithCredentialsResponse();
    }

    /**
     * Create an instance of {@link DeployWithCredentialsResponse }
     * 
     */
    public DeployWithCredentialsResponse createDeployWithCredentialsResponse() {
        return new DeployWithCredentialsResponse();
    }

    /**
     * Create an instance of {@link ExportDataResponse }
     * 
     */
    public ExportDataResponse createExportDataResponse() {
        return new ExportDataResponse();
    }

    /**
     * Create an instance of {@link Deploy }
     * 
     */
    public Deploy createDeploy() {
        return new Deploy();
    }

    /**
     * Create an instance of {@link ExportDataWithCredentials }
     * 
     */
    public ExportDataWithCredentials createExportDataWithCredentials() {
        return new ExportDataWithCredentials();
    }

    /**
     * Create an instance of {@link ImportEntitiesResponse }
     * 
     */
    public ImportEntitiesResponse createImportEntitiesResponse() {
        return new ImportEntitiesResponse();
    }

    /**
     * Create an instance of {@link Ping }
     * 
     */
    public Ping createPing() {
        return new Ping();
    }

    /**
     * Create an instance of {@link DeployWithCredentials }
     * 
     */
    public DeployWithCredentials createDeployWithCredentials() {
        return new DeployWithCredentials();
    }

    /**
     * Create an instance of {@link ImportEntitiesWithCredentials }
     * 
     */
    public ImportEntitiesWithCredentials createImportEntitiesWithCredentials() {
        return new ImportEntitiesWithCredentials();
    }

    /**
     * Create an instance of {@link ExportDataWithCredentialsResponse }
     * 
     */
    public ExportDataWithCredentialsResponse createExportDataWithCredentialsResponse() {
        return new ExportDataWithCredentialsResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExportDataWithCredentials }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservices.server.pe.mindbox.com/", name = "exportDataWithCredentials")
    public JAXBElement<ExportDataWithCredentials> createExportDataWithCredentials(ExportDataWithCredentials value) {
        return new JAXBElement<ExportDataWithCredentials>(_ExportDataWithCredentials_QNAME, ExportDataWithCredentials.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Ping }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservices.server.pe.mindbox.com/", name = "ping")
    public JAXBElement<Ping> createPing(Ping value) {
        return new JAXBElement<Ping>(_Ping_QNAME, Ping.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeployWithCredentials }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservices.server.pe.mindbox.com/", name = "deployWithCredentials")
    public JAXBElement<DeployWithCredentials> createDeployWithCredentials(DeployWithCredentials value) {
        return new JAXBElement<DeployWithCredentials>(_DeployWithCredentials_QNAME, DeployWithCredentials.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeployResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservices.server.pe.mindbox.com/", name = "deployResponse")
    public JAXBElement<DeployResponse> createDeployResponse(DeployResponse value) {
        return new JAXBElement<DeployResponse>(_DeployResponse_QNAME, DeployResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ImportEntitiesWithCredentials }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservices.server.pe.mindbox.com/", name = "importEntitiesWithCredentials")
    public JAXBElement<ImportEntitiesWithCredentials> createImportEntitiesWithCredentials(ImportEntitiesWithCredentials value) {
        return new JAXBElement<ImportEntitiesWithCredentials>(_ImportEntitiesWithCredentials_QNAME, ImportEntitiesWithCredentials.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PingResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservices.server.pe.mindbox.com/", name = "pingResponse")
    public JAXBElement<PingResponse> createPingResponse(PingResponse value) {
        return new JAXBElement<PingResponse>(_PingResponse_QNAME, PingResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ImportEntitiesWithCredentialsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservices.server.pe.mindbox.com/", name = "importEntitiesWithCredentialsResponse")
    public JAXBElement<ImportEntitiesWithCredentialsResponse> createImportEntitiesWithCredentialsResponse(ImportEntitiesWithCredentialsResponse value) {
        return new JAXBElement<ImportEntitiesWithCredentialsResponse>(_ImportEntitiesWithCredentialsResponse_QNAME, ImportEntitiesWithCredentialsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExportDataWithCredentialsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservices.server.pe.mindbox.com/", name = "exportDataWithCredentialsResponse")
    public JAXBElement<ExportDataWithCredentialsResponse> createExportDataWithCredentialsResponse(ExportDataWithCredentialsResponse value) {
        return new JAXBElement<ExportDataWithCredentialsResponse>(_ExportDataWithCredentialsResponse_QNAME, ExportDataWithCredentialsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Deploy }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservices.server.pe.mindbox.com/", name = "deploy")
    public JAXBElement<Deploy> createDeploy(Deploy value) {
        return new JAXBElement<Deploy>(_Deploy_QNAME, Deploy.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ImportEntities }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservices.server.pe.mindbox.com/", name = "importEntities")
    public JAXBElement<ImportEntities> createImportEntities(ImportEntities value) {
        return new JAXBElement<ImportEntities>(_ImportEntities_QNAME, ImportEntities.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ImportEntitiesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservices.server.pe.mindbox.com/", name = "importEntitiesResponse")
    public JAXBElement<ImportEntitiesResponse> createImportEntitiesResponse(ImportEntitiesResponse value) {
        return new JAXBElement<ImportEntitiesResponse>(_ImportEntitiesResponse_QNAME, ImportEntitiesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExportDataResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservices.server.pe.mindbox.com/", name = "exportDataResponse")
    public JAXBElement<ExportDataResponse> createExportDataResponse(ExportDataResponse value) {
        return new JAXBElement<ExportDataResponse>(_ExportDataResponse_QNAME, ExportDataResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExportData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservices.server.pe.mindbox.com/", name = "exportData")
    public JAXBElement<ExportData> createExportData(ExportData value) {
        return new JAXBElement<ExportData>(_ExportData_QNAME, ExportData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeployWithCredentialsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservices.server.pe.mindbox.com/", name = "deployWithCredentialsResponse")
    public JAXBElement<DeployWithCredentialsResponse> createDeployWithCredentialsResponse(DeployWithCredentialsResponse value) {
        return new JAXBElement<DeployWithCredentialsResponse>(_DeployWithCredentialsResponse_QNAME, DeployWithCredentialsResponse.class, null, value);
    }

}
