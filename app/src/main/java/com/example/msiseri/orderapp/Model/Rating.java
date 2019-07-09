package com.example.msiseri.orderapp.Model;

/**
 * Created by MSI SERI on 04-Jan-18.
 */

public class Rating {
    private String userPhone;
    private String foodId;
    private String ratingValue;
    private String comment;

    public Rating() {
    }

    public Rating(String userPhone, String foodId, String ratingValue, String comment) {
        this.userPhone = userPhone;
        this.foodId = foodId;
        this.ratingValue = ratingValue;
        this.comment = comment;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(String ratingValue) {
        this.ratingValue = ratingValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
