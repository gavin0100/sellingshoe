package com.data.filtro.service;

import com.data.filtro.Util.Utility;
import com.data.filtro.model.*;
import com.data.filtro.model.payment.OrderStatus;
import com.data.filtro.model.payment.PaymentMethod;
import com.data.filtro.repository.OrderDetailRepository;
import com.data.filtro.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.management.Query;
import java.util.*;

@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderDetailRepository orderDetailRepository;

    @Autowired
    ProductService productService;
    @Autowired
    OrderShipperService donHangDaGiaoUserService;



    public Order placeOrder(User user, String phone, String email, String address, String city, int zip, PaymentMethod paymentMethod, List<CartItem> cartItemList) {
        Order order = new Order();
        order.setUser(user);
        if (user.getAddress() == null || !user.getAddress().equals(address)) {
            order.setAddress(address);
        } else {
            order.setAddress(user.getAddress());
        }
        if (user.getPhoneNumber() == null || !user.getPhoneNumber().equals(phone)) {
            order.setPhoneNumber(phone);
        } else {
            order.setPhoneNumber(user.getPhoneNumber());
        }
        if (email.isBlank() || email.isEmpty()) {
            order.setEmail(null);
        } else if (user.getEmail() == null || !user.getEmail().equals(email)) {
            order.setEmail(email);
        } else {
            order.setEmail(user.getEmail());
        }
        if (user.getCity() == null || !user.getCity().equals(city)) {
            order.setCity(city);
        } else {
            order.setCity(user.getCity());
        }
        if (user.getZip() == null || !user.getZip().equals(zip)) {
            order.setZip(zip);
        } else {
            order.setZip(user.getZip());
        }
        order.setOrderDate(new Date());
        order.setPaymentMethod(paymentMethod);
        order.setStatus(1);

        order.setStatusPayment(OrderStatus.PENDING);
        String orderCode = Utility.generateRandomString(10) + order.getUser().getAccountName();
        System.out.println("orderCode: " + orderCode);
        order.setOrder_code(orderCode);
        orderRepository.save(order);
//        System.out.println("price chuan bi nhap vo order detail: " + cartItemList.get(0).getPrice() + " " + cartItemList.get(1).getPrice() );
        List<OrderDetail> orderDetails = new ArrayList<>();
        int total = 0;
        for (CartItem cartItem : cartItemList) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(order);
            orderDetail.setProduct(cartItem.getProduct());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setPrice(cartItem.getPrice());
            orderDetail.setTotal(cartItem.getTotal());
            orderDetails.add(orderDetail);
            orderDetailRepository.save(orderDetail);
            total += orderDetail.getTotal();
        }
        order.setTotal(total);
        order.setOrderDetails(orderDetails);
        orderRepository.save(order);
        return order;
    }

    public void updateStatusOrder(OrderStatus orderStatus, Order order){
        order.setStatusPayment(orderStatus);
        orderRepository.save(order);
    }

    public List<Order> getOrderByUserId(int id) {
        return orderRepository.findOrderByUserId(id);
    }

    public Order getOrderByOrderCode(String orderCode) {
        return orderRepository.findOrderByOrderCode(orderCode);
    }

    public List<Order> findOrderByStatusOrder (int status){return orderRepository.findOrderByStatusOrder(status);};

    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    public Order getCurrentOrderByCartId(int id) {
        return orderRepository.finCurrentOrderByCartId(id);
    }

    public Page<Order> getAllPaging(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public void update(Order order) {
//        System.out.println(order.getId());
        Order newOrder = orderRepository.findById(order.getId()).get();
//        System.out.println(newOrder.getId());
//        newOrder.setEmail(order.getEmail());
        newOrder.setPhoneNumber(order.getPhoneNumber());
        newOrder.setAddress(order.getAddress());
        newOrder.setCity(order.getCity());
        newOrder.setZip(order.getZip());
//        newOrder.setPaymentMethod(order.getPaymentMethod());
        newOrder.setStatusPayment(order.getStatusPayment());
        orderRepository.save(newOrder);
    }

    public void updatess(Order order) {

//        newOrder.setPhoneNumber(order.getPhoneNumber());
//        newOrder.setAddress(order.getAddress());
//        newOrder.setCity(order.getCity());
//        newOrder.setZip(order.getZip());
        orderRepository.save(order);
    }

    @Transactional
    public void delete(int id) {
        orderRepository.cancelOrder(id, OrderStatus.CANCELED);
    }

    public Order getOrderById(int id) {
        return orderRepository.findOrderById(id);
    }


    public int checkOrderStatusById(int id) {
        return orderRepository.checkOrderStatusById(id);
    }

    public List<Order> getAllVerifiedOrders() {
        return orderRepository.findAllVerifiedOrders();
    }

    @Transactional
    public void updateCancelOrder(int id) {
        orderRepository.cancelOrder(id, OrderStatus.CANCELED);
    }

    public List<Order> filterStatusOrder(int status){
        return orderRepository.filterStatusOrder(status);
    }

    public void updateOrderStatus(int orderId) {
        Order order = getOrderById(orderId);
        if (order != null) {

            int status = order.getStatus();
            if (status == 2) {
//                try {
//                    Timer timer = new Timer();
//                    timer.schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            order.setStatus(3);
//                            orderRepository.save(order);
//                            System.out.println("Order status updated to shipped");
//                            System.out.println("Current time: " + System.currentTimeMillis());
//                            timer.schedule(new TimerTask() {
//                                @Override
//                                public void run() {
//                                    order.setStatus(4);
//                                    orderRepository.save(order);
//                                    System.out.println("Order status updated to delivered");
//                                }
//                            }, 300000L);
//                        }
//                    }, 60000L);
//
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
                try {
                    Timer timer = new Timer();
                    TimerTask shippedTask = new TimerTask() {
                        @Override
                        public void run() {
                            order.setStatus(3);
                            orderRepository.save(order);
                            System.out.println("Order status updated to shipped");
                            System.out.println("Current time: " + System.currentTimeMillis());
                            TimerTask deliveredTask = new TimerTask() {
                                @Override
                                public void run() {
                                    order.setStatus(4);
                                    orderRepository.save(order);
                                    System.out.println("Order status updated to delivered");
                                }
                            };
                            timer.schedule(deliveredTask, 300000L);
                            this.cancel();
                        }
                    };
                    timer.schedule(shippedTask, 60000L);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }


    public void updateSoldByOrderStatus(Order order) {
//        System.out.println(order.getId());
        Order updatedOrder = getOrderById(order.getId());
        if (updatedOrder == null) {
            return;
        }
        OrderStatus status = updatedOrder.getStatusPayment();
        if (status == OrderStatus.CONFIRMED) {
            updateSold(updatedOrder.getOrderDetails(), true);
        } else if (status == OrderStatus.CANCELED) {
            updateSold(updatedOrder.getOrderDetails(), false);
        }
    }

    private void updateSold(List<OrderDetail> orderDetails, boolean isIncrease) {
        for (OrderDetail detail : orderDetails) {
            Product product = detail.getProduct();
            int quantity = detail.getQuantity();
            if (isIncrease) {
                product.setSold(product.getSold() + quantity);
                product.setQuantity(product.getQuantity() - quantity);
            } else {
                product.setSold(product.getSold() - quantity);
                product.setQuantity(product.getQuantity() + quantity);
            }
            productService.save(product);
        }
    }

    public void saveOrder(Order order){
        orderRepository.save(order);
    }

    public List<OrderDetail> getOrderDetailByOrderId (int orderId){
        System.out.println("getOrderDetailByOrderId");
        return orderDetailRepository.findOrderDetailByOrderId(orderId);
    }
}
