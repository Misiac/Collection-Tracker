package com.michal.collectiontracker.datamodel;

import javafx.scene.image.Image;

public class CollectionItem {
    int id;
    String name;
    Image image;
    boolean isOwned;


    public CollectionItem() {
    }

    public CollectionItem(int id, String name, Image image, boolean isOwned) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.isOwned = isOwned;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setOwned(boolean owned) {
        isOwned = owned;
    }

    public Image getImage() {
        return image;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isOwned() {
        return isOwned;
    }
}