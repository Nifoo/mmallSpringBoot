package com.mmall.service;

import com.mmall.common.Cnst;
import com.mmall.common.ServerResponse;
import com.mmall.model.Cart;
import com.mmall.model.CartMapper;
import com.mmall.model.Product;
import com.mmall.model.ProductMapper;
import com.mmall.model.vo.CartItemVo;
import com.mmall.model.vo.CartVo;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    @Value("${nginx.server.img.prefix}")
    private String nginxImgPrefix;

    @Override
    public ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count) {
        if (productId == null || count == null) {
            return ServerResponse.failWithMsg("productId or itemCount is empty!");
        }
        Cart cartItemDB = cartMapper.selectByUserIdProductId(userId, productId);

        //Update cart table in DB. After this update, cart table may exists some product quantity larger than stock or smaller than 0.
        //must do the getCartVo() to re-updated this out-of-boundary situation.
        if (cartItemDB == null) {
            // thie product is a new item for current user's cart.
            Cart cartItem = new Cart();
            // new item added into cart always set checked.
            cartItem.setChecked(Cnst.Cart.CHECKED);
            cartItem.setUserId(userId);
            cartItem.setProductId(productId);
            cartItem.setQuantity(count);
            cartMapper.insert(cartItem);
        } else {
            cartItemDB.setQuantity(cartItemDB.getQuantity() + count);
            cartMapper.updateByPrimaryKey(cartItemDB);
        }
        //When generate cartVo, Cart Table would be updated again (check if quantity beyond stock or below 0, then set to the boundary)
        CartVo cartVo = getCartVo(userId);
        return ServerResponse.succWithMsgData("edit item in cart success", cartVo);
    }

    @Override
    public ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count) {
        if (productId == null || count == null) {
            return ServerResponse.failWithMsg("productId or itemCount is empty!");
        }
        Cart cartItemDB = cartMapper.selectByUserIdProductId(userId, productId);

        int curCnt = 0;
        if (cartItemDB != null) {
            curCnt = cartItemDB.getQuantity();
        }
        return add(userId, productId, count - curCnt);
    }

    @Override
    public ServerResponse<CartVo> delete(Integer userId, Integer productId) {
        if (productId == null) {
            return ServerResponse.failWithMsg("productId is empty!");
        }
        Cart cartItemDB = cartMapper.selectByUserIdProductId(userId, productId);

        int curCnt = 0;
        if (cartItemDB != null) {
            curCnt = cartItemDB.getQuantity();
        }
        return add(userId, productId, -curCnt);
    }

    //Core Function. Search cartItems (List<Cart> cartList) in cart table according to userId, convert them into List<CartItemVo> then CartVo.
    //@Override
    private CartVo getCartVo(Integer userId) {
        //Find ALL the (userId, productId, count, ..) in Cart Table.
        List<Cart> cartList = cartMapper.selectByUserId(userId);
        //List<Cart> -> List<CartItemVo>
        List<CartItemVo> cartItemVoList = new ArrayList<>();
        BigDecimal totalPrice = new BigDecimal(0);
        boolean allChecked = true;
        for (Cart cartItem : cartList) {
            CartItemVo cartItemVo = convertCartToCartItemVo(cartItem);
            if (cartItemVo.getQuantity() > 0) {
                cartItemVoList.add(cartItemVo);
                //added into totalPrice only when checked; set "allChecked" false only when unchecked.
                if (cartItem.getChecked() == Cnst.Cart.CHECKED) {
                    totalPrice.add(cartItemVo.getProductTotalPrice());
                } else {
                    allChecked = false;
                }
            }
        }
        //List<CartItemVo> -> CartVo
        CartVo cartVo = new CartVo();
        cartVo.setUserId(userId);
        cartVo.setCartItemVoList(cartItemVoList);
        cartVo.setCartTotalPrice(totalPrice);
        cartVo.setImgHost(nginxImgPrefix);
        cartVo.setAllChecked(allChecked);

        return cartVo;
    }

    //This function would update Cart Table according to the actual possible product quantity under the restriction of staock.
    private CartItemVo convertCartToCartItemVo(Cart cartItem) {
        CartItemVo cartItemVo = new CartItemVo();
        Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());

        cartItemVo.setCartId(cartItem.getId());
        cartItemVo.setProductChecked(cartItem.getChecked());
        cartItemVo.setUserId(cartItem.getUserId());

        if (product != null) {
            cartItemVo.setProductId(cartItem.getProductId());
            cartItemVo.setProductMainImage(product.getMainImage());
            cartItemVo.setProductName(product.getName());
            cartItemVo.setProductPrice(product.getPrice());
            cartItemVo.setProductStatus(product.getStatus());
            cartItemVo.setProductStock(product.getStock());
            cartItemVo.setProductSubtitle(product.getSubtitle());

            int productCnt = 0;

            if (cartItem.getQuantity() > product.getStock()) {
                //Out of stock.
                productCnt = product.getStock();
                cartItemVo.setQuantityLimited(true);
                //Update cart table
                Cart tmpCart = new Cart();
                tmpCart.setId(cartItem.getId());
                tmpCart.setQuantity(productCnt);
                cartMapper.updateByPrimaryKeySelective(tmpCart);

            } else {
                productCnt = cartItem.getQuantity();
                cartItemVo.setQuantityLimited(false);
            }

            if (productCnt <= 0) {
                productCnt = 0;
                //remove this item from Cart Table
                cartMapper.deleteByPrimaryKey(cartItem.getId());
            }

            cartItemVo.setQuantity(productCnt);

            //Here productTotalPrice calculated without considering checked or not.
            cartItemVo.setProductTotalPrice(
                    product.getPrice().multiply(BigDecimal.valueOf(productCnt)));
        }

        return cartItemVo;
    }

    @Override
    public ServerResponse<CartVo> changeCheckState(Integer userId, List<Integer> productIds) {
        for (Integer productId : productIds) {
            Cart cartItemDB = cartMapper.selectByUserIdProductId(userId, productId);
            if (cartItemDB != null) {
                cartItemDB.setChecked(Cnst.changeCheckState(cartItemDB.getChecked()));
                cartMapper.updateByPrimaryKey(cartItemDB);
            }
        }
        CartVo cartVo = getCartVo(userId);
        return ServerResponse.succWithMsgData("changeCheckState in cart success", cartVo);
    }

    @Override
    public ServerResponse<CartVo> setAllCheckState(Integer userId, int checkState) {
        cartMapper.updateAllCheckByUserId(userId, checkState);
        CartVo cartVo = getCartVo(userId);
        return ServerResponse.succWithMsgData("changeAllCheckState in cart success", cartVo);
    }
}
