package com.driver;

import java.sql.SQLOutput;
import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {

    Map<String, Order> orders = new HashMap<String,Order>();
    Map<String, DeliveryPartner> deliveryPartners = new HashMap<String,DeliveryPartner>();
    Map<String, String> orderPartnerPairs = new HashMap<String,String>();
    Map<String, Set<String>> partnerOrderLists = new HashMap<String,Set<String>>();

    public void addOrder(Order order){
        orders.put(order.getId(),order);
    }

    public void addPartner(DeliveryPartner dp) {
        deliveryPartners.put(dp.getId(),dp);
    }

    public void addOrderPartner(String orderId, String partnerId) {
        orderPartnerPairs.put(orderId,partnerId);

        if(!partnerOrderLists.containsKey(partnerId)) partnerOrderLists.put(partnerId,new HashSet<String>());
        partnerOrderLists.get(partnerId).add(orderId);
        int n = partnerOrderLists.get(partnerId).size();
        System.out.println(n);
        if(!deliveryPartners.containsKey(partnerId)) deliveryPartners.put(partnerId, new DeliveryPartner(partnerId));
        deliveryPartners.get(partnerId).setNumberOfOrders(n);
        System.out.println(deliveryPartners.get(partnerId).getNumberOfOrders());
    }

    public Order getOrderById(String orderId) {
        return orders.getOrDefault(orderId,null);
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        System.out.println(deliveryPartners.getOrDefault(partnerId,null));
        return deliveryPartners.getOrDefault(partnerId,null);
    }

    public Integer getOrderCountByPartnerId(String partnerId) {
        if(deliveryPartners.getOrDefault(partnerId,null)==null) return 0;
        return deliveryPartners.getOrDefault(partnerId,null).getNumberOfOrders();
    }

    public List<String> getOrdersByPartnerId(String partnerId) {
        Set<String> orders = partnerOrderLists.getOrDefault(partnerId,null);
        List<String> ans = new ArrayList<String>();
        if(orders!=null) ans.addAll(orders);
        return ans;
    }

    public List<String> getAllOrders() {
        Set<String> orders = this.orders.keySet();
        List<String> ans = new ArrayList<String>();
        if(orders!=null && orders.size()>0) ans.addAll(orders);
        return ans;
    }

    public Integer getCountOfUnassignedOrders() {
        return orders.size()-orderPartnerPairs.size();
    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(int hours, int mins, String partnerId) {
        Set<String> partnerOrders = partnerOrderLists.getOrDefault(partnerId,null);
        int ans = 0;
        if(partnerOrders!=null){
            for(String orderId: partnerOrders){
                Order order = orders.getOrDefault(orderId,null);
                if(order==null) continue;
                int time = order.getDeliveryTime();
                int h = time/60;
                int m = time%60;
                if(h>hours) ans++;
                else if(h==hours && m>mins) ans++;
            }
        }
        return ans;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) {
        Set<String> partnerOrders = partnerOrderLists.getOrDefault(partnerId,null);
        int max = 0;
        if(partnerOrders!=null){
            for(String orderId: partnerOrders){
                Order order = orders.getOrDefault(orderId,null);
                if(order==null) continue;
                int time = order.getDeliveryTime();
                max = Math.max(max,time);
            }
        }
        String ans = "";
        if(max/60<10) ans+="0"+(max/60);
        else ans+=(max/60);
        ans+=":";
        if(max%60<10) ans+="0"+(max%60);
        else ans+=(max%60);
        return ans;
    }

    public void deletePartnerById(String partnerId) {
        if(!deliveryPartners.containsKey(partnerId)) return;
        else deliveryPartners.remove(partnerId);
        if(!partnerOrderLists.containsKey(partnerId)) return;
        else{
            for(String orderId: partnerOrderLists.get(partnerId)){
                if(orderPartnerPairs.containsKey(orderId)) orderPartnerPairs.remove(orderId);
            }
            partnerOrderLists.remove(partnerId);
        }
        return;
    }

    public void deleteOrderById(String orderId) {
        if(orders.containsKey(orderId)) orders.remove(orderId);
        if(orderPartnerPairs.containsKey(orderId)){
            String deliveryPartnerId = orderPartnerPairs.get(orderId);
            if(partnerOrderLists.containsKey(deliveryPartnerId) && partnerOrderLists.get(deliveryPartnerId).contains(orderId)) partnerOrderLists.get(deliveryPartnerId).remove(orderId);
            orderPartnerPairs.remove(orderId);
        }
    }
}
