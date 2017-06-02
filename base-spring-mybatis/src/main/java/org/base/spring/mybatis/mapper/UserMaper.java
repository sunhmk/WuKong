package org.base.spring.mybatis.mapper;

import java.util.List;

import org.base.spring.mybatis.pojo.Order;
import org.base.spring.mybatis.pojo.User;

public interface UserMaper { 
	public User getUserById(int userId);
	public List<Order> getUserOrders(int userId);
}
