package com.michal.collectiontracker.datamodel;


import java.io.File;
import java.util.List;

public class Collection {

    private List<CollectionItem> collectionItems;
    private DataSource thisDatasource;
    private int numberOfItems;
    File file;

    public Collection(File collectionFile) {
        this.file = collectionFile;
        this.numberOfItems = 0;
        thisDatasource = new DataSource(collectionFile.getAbsolutePath());


    }

    public void loadCollectionFromFile(File collection) {

    }

    public File getFile() {
        return file;
    }
}

