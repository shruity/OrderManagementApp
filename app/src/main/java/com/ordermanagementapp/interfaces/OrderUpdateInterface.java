package com.ordermanagementapp.interfaces;

public interface OrderUpdateInterface {
    void edit(String orderNo, String orderDueDate, String customerName, String customerPhone, String customerAddress,
              String orderTotal, String location);
    void delete(String orderNo);
}
