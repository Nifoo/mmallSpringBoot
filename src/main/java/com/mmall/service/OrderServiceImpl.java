package com.mmall.service;

import com.mmall.common.Cnst;
import com.mmall.common.Cnst.OrderStatusEnum;
import com.mmall.common.Cnst.paymentPlatform;
import com.mmall.common.Cnst.paymentType;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.controller.portal.OrderController;
import com.mmall.model.Cart;
import com.mmall.model.CartMapper;
import com.mmall.model.Order;
import com.mmall.model.OrderItem;
import com.mmall.model.OrderItemMapper;
import com.mmall.model.OrderMapper;
import com.mmall.model.PayInfo;
import com.mmall.model.PayInfoMapper;
import com.mmall.model.Product;
import com.mmall.model.ProductMapper;
import com.mmall.model.Shipping;
import com.mmall.model.ShippingMapper;
import com.mmall.model.User;
import com.mmall.model.vo.CartItemVo;
import com.mmall.model.vo.CartVo;
import com.mmall.model.vo.OrderItemVo;
import com.mmall.model.vo.OrderVo;
import com.mmall.util.DateTimeUtil;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.apache.catalina.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements IOrderService {

    private static Logger logger = LoggerFactory.getLogger(IOrderService.class);

    @Value("${nginx.server.img.prefix}")
    private String nginxImgPrefix;

    @Autowired
    private ICartService cartService;

    @Autowired
    private ShippingMapper shippingMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CartMapper cartMapper;

    @Value("${pay.apikey}")
    private String apiKey;

    @Autowired
    private PayInfoMapper payInfoMapper;

    //Create order according to the currentUser's cart, and the shippingId.
    @Override
    public ServerResponse<OrderVo> create(Integer shippingId, Integer userId) {

        CartVo cartVo = cartService.getCartVo(userId);
        if (cartVo == null) {
            return ServerResponse.failWithMsg("Cart is empty!");
        }
        Shipping shipping = shippingMapper
                .selectByUserIdShippingId(userId, shippingId);
        if (shipping == null) {
            return ServerResponse.failWithMsg("Shipping info is empty");
        }

        //Generate order
        Order order = asbOrder(cartVo, shipping, userId);
        orderMapper.insertSelective(order);

        //Generate orderItemList
        List<CartItemVo> cartItemVoList = cartVo.getCartItemVoList();
        List<OrderItem> orderItemList = asbOrderItemList(cartItemVoList, order);
        orderItemMapper.batchInsert(orderItemList);

        //Assemble an orderVo for view
        OrderVo orderVo = asbOrderVo(order, orderItemList);

        return ServerResponse.succWithMsgData("create order succeed.", orderVo);

    }

    @Override
    public ServerResponse charge(String stripeToken, String stripeTokenType, String stripeEmail,
            Integer userId, BigDecimal totalPrice, Long orderNum) {

        // Set your secret key: remember to change this to your live secret key in production
        // See your keys here: https://dashboard.stripe.com/account/apikeys
        Stripe.apiKey = apiKey;

        // Token is created using Checkout or Elements!
        // Get the payment token ID submitted by the form:
        Map<String, Object> params = new HashMap<>();

        //convert $x.xx to $xxx
        BigDecimal totalPrice100 = totalPrice.multiply(new BigDecimal(100));
        int totalPrice100Int = totalPrice100.intValue();

        params.put("amount", totalPrice100Int);
        params.put("currency", "usd");
        params.put("description", "Cart Charge");
        params.put("source", stripeToken);
        try {
            Charge charge = Charge.create(params);
            PayInfo payInfo = new PayInfo();
            logger.info("Charge succeed. userId:{}, orderNum:{}, paymentAmount:{}", userId, orderNum, totalPrice);
            payInfo.setOrderNo(orderNum);
            payInfo.setPayPlatform(paymentPlatform.STRIPE);
            payInfo.setPlatformNumber(charge.getId());
            payInfo.setUserId(userId);
            payInfo.setPlatformStatus(charge.getStatus());
            payInfo.setChargeAmout(charge.getAmount());
            payInfo.setCurrentcy(charge.getCurrency());
            payInfo.setMetaData(String.valueOf(charge.getMetadata()));
            int rowCnt = payInfoMapper.insertSelective(payInfo);

            List<OrderItem> orderItemList = getOrderItemList(orderNum, userId);
            //Reduce product quantity in stock
            reduceProductStock(orderItemList);
            //Clear Cart
            clearCart(userId, orderItemList);

            return ServerResponse.succWithMsg("Charge Succeed. reduceProductStock and clearCart Succeed.");

        } catch (StripeException e) {
            logger.error("Charge Error!", e);
            return ServerResponse.failWithMsg("Charge Failed.");
        }
    }

    private List<OrderItem> getOrderItemList(Long orderNum, Integer userId){
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNumUserId(orderNum, userId);
        return orderItemList;
    }

    private OrderVo asbOrderVo(Order order, List<OrderItem> orderItemList) {
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderId(order.getId());
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPaymentAmount(order.getPayment());
        orderVo.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));
        orderVo.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        orderVo.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
        orderVo.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));
        orderVo.setEndTime(DateTimeUtil.dateToStr(order.getEndTime()));
        orderVo.setImageHost(nginxImgPrefix);
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(Cnst.OrderStatusEnum.getValue(order.getStatus()));
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        orderVo.setShipping(shipping);
        List<OrderItemVo> orderItemVoList = asbOrderItemVo(orderItemList);
        orderVo.setOrderItemVoList(orderItemVoList);
        return orderVo;
    }

    private List<OrderItemVo> asbOrderItemVo(List<OrderItem> orderItemList) {
        List<OrderItemVo> orderItemVoList = new ArrayList<>();
        for (OrderItem orderItem : orderItemList) {
            OrderItemVo orderItemVo = new OrderItemVo();
            orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
            orderItemVo.setProductId(orderItem.getProductId());
            orderItemVo.setProductImage(orderItem.getProductImage());
            orderItemVo.setProductName(orderItem.getProductName());
            orderItemVo.setQuantity(orderItem.getQuantity());
            orderItemVo.setTotalPrice(orderItem.getTotalPrice());
            orderItemVoList.add(orderItemVo);
        }
        return orderItemVoList;
    }

    private void clearCart(Integer userId, List<OrderItem> orderItemList) {
        for (OrderItem orderItem : orderItemList) {
            Cart cartItem = cartMapper
                    .selectByUserIdProductId(userId, orderItem.getProductId());
            cartMapper.deleteByPrimaryKey(cartItem.getId());
        }
    }

    private void reduceProductStock(List<OrderItem> orderItemList) {
        for (OrderItem orderItem : orderItemList) {
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            int newQuant = product.getStock() - orderItem.getQuantity();
            product.setStock(newQuant);
            productMapper.insertSelective(product);
        }
    }

    private List<OrderItem> asbOrderItemList(List<CartItemVo> cartItemVoList, Order order) {
        List<OrderItem> orderItemList = new ArrayList<>();
        for (CartItemVo cartItemVo : cartItemVoList) {
            if (cartItemVo.getProductChecked() == Cnst.Cart.CHECKED) {
                OrderItem orderItem = new OrderItem();

                orderItem.setUserId(order.getUserId());
                orderItem.setOrderNo(order.getOrderNo());

                orderItem.setProductId(cartItemVo.getProductId());
                orderItem.setProductName(cartItemVo.getProductName());
                orderItem.setProductImage(cartItemVo.getProductMainImage());
                orderItem.setQuantity(cartItemVo.getQuantity());
                orderItem.setCurrentUnitPrice(cartItemVo.getProductPrice());
                orderItem.setTotalPrice(cartItemVo.getProductTotalPrice());

                orderItemList.add(orderItem);
            }
        }
        return orderItemList;
    }

    private Order asbOrder(CartVo cartVo, Shipping shipping, Integer userId) {
        Order order = new Order();

        SimpleDateFormat sfDate = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String strDate = sfDate.format(new Date());
        Long orderNum20 = Long.valueOf(strDate) * 1000 + userId % 1000;

        order.setOrderNo(orderNum20);
        order.setUserId(userId);
        order.setPayment(cartVo.getCartTotalPrice());
        order.setShippingId(shipping.getId());
        order.setPaymentType(paymentType.CREDIT_CARD);
        order.setPostage(0);
        order.setStatus(OrderStatusEnum.NOT_PAID.getCode());

        return order;
    }

    @Override
    public ServerResponse cancel(Long orderNum, Integer userId) {
        Order order = orderMapper.selectByOrderNumUserId(orderNum, userId);
        if (order == null) {
            return ServerResponse.failWithMsg("order doesn't exit!");
        }
        if (order.getStatus() >= OrderStatusEnum.PAID.getCode()) {
            return ServerResponse.failWithMsg(
                    "order is in state: '" + OrderStatusEnum.getValue(order.getStatus())
                            + "', can't cancel.");
        }
        order.setStatus(OrderStatusEnum.CANCELED.getCode());
        orderMapper.updateByPrimaryKey(order);
        return ServerResponse.succWithMsg("cancel order succeed.");
    }
}
