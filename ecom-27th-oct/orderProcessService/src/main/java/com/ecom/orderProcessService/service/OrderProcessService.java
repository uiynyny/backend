package com.ecom.orderProcessService.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.ecom.dbagent.dao.DaoInterface;
import com.ecom.dbagent.daoImpl.DaoImpl;
import com.ecom.orderProcessService.exceptions.ErrorCode;
import com.ecom.orderProcessService.exceptions.OrderProcessServiceException;
import com.ecom.orderProcessService.model.AccountInfo;
import com.ecom.orderProcessService.model.Book;
import com.ecom.orderProcessService.model.POItem;
import com.ecom.orderProcessService.model.PurchaseOrder;
import com.ecom.orderProcessService.request.OrderConfirmRequest;
import com.ecom.orderProcessService.request.OrderRequest;
import com.ecom.orderProcessService.request.UserInfo;
import com.ecom.orderProcessService.response.OrderPlaceResponse;
import com.ecom.orderProcessService.util.OrderProcessServiceConstant;
import com.ecom.orderProcessService.util.OrderProcessServiceUtil;

public class OrderProcessService {
	private DaoInterface dao;

	public OrderProcessService() {
		this.dao = new DaoImpl();
	}

	public boolean isNewAccount(AccountInfo accountInfo) throws OrderProcessServiceException {
		try {
			dao.openCurrentSessionwithTransaction();
			Map<String, String> paramValue = new HashMap<String, String>();
			paramValue.put(OrderProcessServiceConstant.EMAIL, accountInfo.getEmail());
			List<AccountInfo> findAll = dao.findAllWithCondition(OrderProcessServiceUtil.getUtil()
					.readProperty(OrderProcessServiceConstant.CHECK_EMAIL_EXISTS_QUERY), paramValue);
			if (findAll.size() > 0) {
				return false;
			}
			accountInfo.setAccountId(UUID.randomUUID().toString());
			dao.persist(accountInfo);
			dao.closeCurrentSessionwithTransaction();
			return true;
		} catch (Exception e) {
			dao.closeCurrentSession();
			throw new OrderProcessServiceException(ErrorCode.CREATE_ACCOUNT);
		}
	}

	public List<AccountInfo> checkIfAccountExists(UserInfo userInfo) throws OrderProcessServiceException {
		try {
			dao.openCurrentSession();
			Map<String, String> paramValue = new HashMap<String, String>();
			paramValue.put(OrderProcessServiceConstant.EMAIL, userInfo.getUsername());
			paramValue.put(OrderProcessServiceConstant.PASSWORD, userInfo.getPassword());
			List<AccountInfo> findAll = dao.findAllWithCondition(OrderProcessServiceUtil.getUtil()
					.readProperty(OrderProcessServiceConstant.CHECK_ACCOUNT_EXISTS_QUERY), paramValue);
			dao.closeCurrentSession();
			return findAll;
		} catch (Exception e) {
			dao.closeCurrentSession();
			throw new OrderProcessServiceException(ErrorCode.CHECK_ACCOUNT);
		}
	}

	public int placeOrder(OrderConfirmRequest orderRequest) throws OrderProcessServiceException {

		try {
			dao.openCurrentSessionwithTransaction();
			Map<String, String> paramValue = new HashMap<String, String>();
			paramValue.put(OrderProcessServiceConstant.PURCHASE_ID, orderRequest.getPurchase_id());
			paramValue.put(OrderProcessServiceConstant.STATUS, orderRequest.getStatus());
			int update = dao.update(
					OrderProcessServiceUtil.getUtil().readProperty(OrderProcessServiceConstant.CONFIRM_ORDER),
					paramValue);

			dao.closeCurrentSessionwithTransaction();
			return update;
		} catch (Exception e) {
			dao.closeCurrentSession();
			throw new OrderProcessServiceException(ErrorCode.CONFIRM_ORDER, e);
		}

	}

	public OrderPlaceResponse authorizeOrder(OrderRequest orderRequest) throws OrderProcessServiceException {
		PurchaseOrder purchase = new PurchaseOrder();
		AccountInfo accountInfo = new AccountInfo();
		OrderPlaceResponse orderPalceRepsonse = new OrderPlaceResponse();
		String purchaseId = null;
		try {
			dao.openCurrentSessionwithTransaction();
			accountInfo.setAccountId(orderRequest.getAccountId());
			purchase.setAccount(accountInfo);
			purchaseId = UUID.randomUUID().toString();
			purchase.setPurchase_id(purchaseId);
			purchase.setShipping_info(orderRequest.getShipping_info());
			purchase.setStatus(orderRequest.getStatus());
			purchase.setTotal_price(orderRequest.getTotal_price());
			orderPalceRepsonse.setPurchase_id(purchaseId);
			dao.persist(purchase);
			for (String bookid : orderRequest.getBookIds()) {
				Book book = new Book();
				POItem poitem = new POItem();
				book.setBookid(bookid);
				poitem.setBookid(book);
				poitem.setPurchase_id(purchase);
				poitem.setId(UUID.randomUUID().toString());
				dao.persist(poitem);
			}

			dao.closeCurrentSessionwithTransaction();
			return orderPalceRepsonse;
		} catch (Exception e) {
			dao.closeCurrentSession();
			throw new OrderProcessServiceException(ErrorCode.ORDER_AUTH_ERROR);
		}
	}

	public int checkTransactionsCount() throws OrderProcessServiceException {
		try {
			dao.openCurrentSession();
			int findCount = dao.findCount(PurchaseOrder.class);
			dao.openCurrentSession();
			return findCount;
		} catch (Exception e) {
			e.printStackTrace();
			dao.closeCurrentSession();
			throw new OrderProcessServiceException(ErrorCode.CHECK_COUNT, e);

		}
	}

}
