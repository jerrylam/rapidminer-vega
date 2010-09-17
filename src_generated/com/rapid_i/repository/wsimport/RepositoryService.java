
package com.rapid_i.repository.wsimport;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebService(name = "RepositoryService", targetNamespace = "http://service.web.rapidrepository.com/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface RepositoryService {


    /**
     * 
     * @param parentLocation
     * @param blobName
     * @return
     *     returns com.rapid_i.repository.wsimport.EntryResponse
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "createBlob", targetNamespace = "http://service.web.rapidrepository.com/", className = "com.rapid_i.repository.wsimport.CreateBlob")
    @ResponseWrapper(localName = "createBlobResponse", targetNamespace = "http://service.web.rapidrepository.com/", className = "com.rapid_i.repository.wsimport.CreateBlobResponse")
    public EntryResponse createBlob(
        @WebParam(name = "parentLocation", targetNamespace = "")
        String parentLocation,
        @WebParam(name = "blobName", targetNamespace = "")
        String blobName);

    /**
     * 
     * @param entryLocation
     * @return
     *     returns com.rapid_i.repository.wsimport.Response
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "deleteEntry", targetNamespace = "http://service.web.rapidrepository.com/", className = "com.rapid_i.repository.wsimport.DeleteEntry")
    @ResponseWrapper(localName = "deleteEntryResponse", targetNamespace = "http://service.web.rapidrepository.com/", className = "com.rapid_i.repository.wsimport.DeleteEntryResponse")
    public Response deleteEntry(
        @WebParam(name = "entryLocation", targetNamespace = "")
        String entryLocation);

    /**
     * 
     * @param entryLocation
     * @return
     *     returns java.util.List<com.rapid_i.repository.wsimport.AccessRights>
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getAccessRights", targetNamespace = "http://service.web.rapidrepository.com/", className = "com.rapid_i.repository.wsimport.GetAccessRights")
    @ResponseWrapper(localName = "getAccessRightsResponse", targetNamespace = "http://service.web.rapidrepository.com/", className = "com.rapid_i.repository.wsimport.GetAccessRightsResponse")
    public List<AccessRights> getAccessRights(
        @WebParam(name = "entryLocation", targetNamespace = "")
        String entryLocation);

    /**
     * 
     * @return
     *     returns java.util.List<java.lang.String>
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getAllGroupNames", targetNamespace = "http://service.web.rapidrepository.com/", className = "com.rapid_i.repository.wsimport.GetAllGroupNames")
    @ResponseWrapper(localName = "getAllGroupNamesResponse", targetNamespace = "http://service.web.rapidrepository.com/", className = "com.rapid_i.repository.wsimport.GetAllGroupNamesResponse")
    public List<String> getAllGroupNames();

    /**
     * 
     * @param entryLocation
     * @return
     *     returns com.rapid_i.repository.wsimport.EntryResponse
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getEntry", targetNamespace = "http://service.web.rapidrepository.com/", className = "com.rapid_i.repository.wsimport.GetEntry")
    @ResponseWrapper(localName = "getEntryResponse", targetNamespace = "http://service.web.rapidrepository.com/", className = "com.rapid_i.repository.wsimport.GetEntryResponse")
    public EntryResponse getEntry(
        @WebParam(name = "entryLocation", targetNamespace = "")
        String entryLocation);

    /**
     * 
     * @param folderLocation
     * @return
     *     returns com.rapid_i.repository.wsimport.FolderContentsResponse
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getFolderContents", targetNamespace = "http://service.web.rapidrepository.com/", className = "com.rapid_i.repository.wsimport.GetFolderContents")
    @ResponseWrapper(localName = "getFolderContentsResponse", targetNamespace = "http://service.web.rapidrepository.com/", className = "com.rapid_i.repository.wsimport.GetFolderContentsResponse")
    public FolderContentsResponse getFolderContents(
        @WebParam(name = "folderLocation", targetNamespace = "")
        String folderLocation);

    /**
     * 
     * @param revisionNumber
     * @param entryLocation
     * @return
     *     returns com.rapid_i.repository.wsimport.ProcessContentsResponse
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getProcessContents", targetNamespace = "http://service.web.rapidrepository.com/", className = "com.rapid_i.repository.wsimport.GetProcessContents")
    @ResponseWrapper(localName = "getProcessContentsResponse", targetNamespace = "http://service.web.rapidrepository.com/", className = "com.rapid_i.repository.wsimport.GetProcessContentsResponse")
    public ProcessContentsResponse getProcessContents(
        @WebParam(name = "entryLocation", targetNamespace = "")
        String entryLocation,
        @WebParam(name = "revisionNumber", targetNamespace = "")
        int revisionNumber);

    /**
     * 
     * @param parentLocation
     * @param subfolderName
     * @return
     *     returns com.rapid_i.repository.wsimport.EntryResponse
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "makeFolder", targetNamespace = "http://service.web.rapidrepository.com/", className = "com.rapid_i.repository.wsimport.MakeFolder")
    @ResponseWrapper(localName = "makeFolderResponse", targetNamespace = "http://service.web.rapidrepository.com/", className = "com.rapid_i.repository.wsimport.MakeFolderResponse")
    public EntryResponse makeFolder(
        @WebParam(name = "parentLocation", targetNamespace = "")
        String parentLocation,
        @WebParam(name = "subfolderName", targetNamespace = "")
        String subfolderName);

    /**
     * 
     * @param location
     * @param newName
     * @return
     *     returns com.rapid_i.repository.wsimport.EntryResponse
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "rename", targetNamespace = "http://service.web.rapidrepository.com/", className = "com.rapid_i.repository.wsimport.Rename")
    @ResponseWrapper(localName = "renameResponse", targetNamespace = "http://service.web.rapidrepository.com/", className = "com.rapid_i.repository.wsimport.RenameResponse")
    public EntryResponse rename(
        @WebParam(name = "location", targetNamespace = "")
        String location,
        @WebParam(name = "newName", targetNamespace = "")
        String newName);

    /**
     * 
     * @param accessRights
     * @param entryLocation
     * @return
     *     returns com.rapid_i.repository.wsimport.Response
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "setAccessRights", targetNamespace = "http://service.web.rapidrepository.com/", className = "com.rapid_i.repository.wsimport.SetAccessRights")
    @ResponseWrapper(localName = "setAccessRightsResponse", targetNamespace = "http://service.web.rapidrepository.com/", className = "com.rapid_i.repository.wsimport.SetAccessRightsResponse")
    public Response setAccessRights(
        @WebParam(name = "entryLocation", targetNamespace = "")
        String entryLocation,
        @WebParam(name = "accessRights", targetNamespace = "")
        List<AccessRights> accessRights);

    /**
     * 
     * @param processLocation
     * @return
     *     returns com.rapid_i.repository.wsimport.Response
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "startNewRevision", targetNamespace = "http://service.web.rapidrepository.com/", className = "com.rapid_i.repository.wsimport.StartNewRevision")
    @ResponseWrapper(localName = "startNewRevisionResponse", targetNamespace = "http://service.web.rapidrepository.com/", className = "com.rapid_i.repository.wsimport.StartNewRevisionResponse")
    public Response startNewRevision(
        @WebParam(name = "processLocation", targetNamespace = "")
        String processLocation);

    /**
     * 
     * @param processXML
     * @param lastTimestamp
     * @param entryLocation
     * @return
     *     returns com.rapid_i.repository.wsimport.Response
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "storeProcess", targetNamespace = "http://service.web.rapidrepository.com/", className = "com.rapid_i.repository.wsimport.StoreProcess")
    @ResponseWrapper(localName = "storeProcessResponse", targetNamespace = "http://service.web.rapidrepository.com/", className = "com.rapid_i.repository.wsimport.StoreProcessResponse")
    public Response storeProcess(
        @WebParam(name = "entryLocation", targetNamespace = "")
        String entryLocation,
        @WebParam(name = "processXML", targetNamespace = "")
        String processXML,
        @WebParam(name = "lastTimestamp", targetNamespace = "")
        XMLGregorianCalendar lastTimestamp);

}
