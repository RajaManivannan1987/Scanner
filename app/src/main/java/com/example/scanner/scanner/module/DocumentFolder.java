package com.example.scanner.scanner.module;

/**
 * Created by IM021 on 11/24/2015.
 */
public class DocumentFolder {
    private Long id;
    private String name;
    private String docType;
    private String docDate;
    private Long hasFolder;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getHasFolder() {
        return hasFolder;
    }

    public void setHasFolder(Long hasFolder) {
        this.hasFolder = hasFolder;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getDocDate() {
        return docDate;
    }

    public void setDocDate(String docDate) {
        this.docDate = docDate;
    }
}
