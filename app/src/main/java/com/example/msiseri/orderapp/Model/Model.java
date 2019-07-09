package com.example.msiseri.orderapp.Model;

/**
 * Created by MSI SERI on 27-Feb-18.
 */

public class Model {
    private  String id, name, image;

    public Model() {
    }

    public Model(String id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
