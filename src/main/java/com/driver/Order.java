package com.driver;

public class Order {

    private String id;
    private int deliveryTime;

    public Order(String id, String deliveryTime) {

        // The deliveryTime has to converted from string to int and then stored in the attribute
        //deliveryTime  = HH*60 + MM
        this.id=id;
        int hours = Integer.parseInt(deliveryTime.split(":")[0]);
        int min = Integer.parseInt(deliveryTime.split(":")[1]);
        this.deliveryTime = hours*60+min;
    }

    public String getId() {
        return id;
    }

    public int getDeliveryTime() {return deliveryTime;}
}
