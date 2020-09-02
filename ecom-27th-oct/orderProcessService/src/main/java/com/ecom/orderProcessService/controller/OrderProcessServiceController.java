package com.ecom.orderProcessService.controller;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ecom.orderProcessService.exceptions.ErrorCode;
import com.ecom.orderProcessService.exceptions.OrderProcessServiceException;
import com.ecom.orderProcessService.model.AccountInfo;
import com.ecom.orderProcessService.request.OrderConfirmRequest;
import com.ecom.orderProcessService.request.OrderRequest;
import com.ecom.orderProcessService.request.UserInfo;
import com.ecom.orderProcessService.response.OrderPlaceResponse;
import com.ecom.orderProcessService.service.OrderProcessService;
import com.ecom.orderProcessService.util.OrderProcessServiceConstant;
import com.ecom.orderProcessService.util.OrderProcessServiceUtil;

@Path("/")
public class OrderProcessServiceController {

	private static Logger logger = LoggerFactory.getLogger(OrderProcessServiceController.class);
	private OrderProcessService orderProcessService = new OrderProcessService();
	private OrderProcessServiceUtil util = OrderProcessServiceUtil.getUtil();

	@POST
	@Path("/SignUp")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createAccount(String requestBody) {
		try {
			AccountInfo accountInfo = (AccountInfo) OrderProcessServiceUtil.extractJsonObject(requestBody,
					AccountInfo.class);
			OrderProcessServiceUtil.validateRequest(accountInfo);
			if (orderProcessService.isNewAccount(accountInfo)) {
				return Response.status(Status.OK).type(MediaType.APPLICATION_JSON)
						.entity(OrderProcessServiceUtil.responseString(accountInfo)).build();
			}
			return Response.status(Status.CONFLICT).type(MediaType.APPLICATION_JSON)
					.entity(util.readProperty(OrderProcessServiceConstant.USER_EXISTS)).build();

		} catch (OrderProcessServiceException orderProcessServiceException) {
			logger.error("Error Code: " + orderProcessServiceException.getErrorCode().getDescription());
			orderProcessServiceException.printStackTrace();
			if (orderProcessServiceException.getErrorCode().getDescription()
					.equals(ErrorCode.VALIDATION_ERROR.getDescription())) {
				return Response.status(Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON)
						.entity(OrderProcessServiceConstant.BAD_REQUEST).build();
			}
			return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON)
					.entity(OrderProcessServiceConstant.INTERNAL_ERROR).build();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception message ", e.getMessage(), e.getCause());
			return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON)
					.entity(OrderProcessServiceConstant.INTERNAL_ERROR).build();

		}

	}

	@POST
	@Path("/CheckAccount")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response checkAccount(String requestBody) {
		try {
			UserInfo userInfo = (UserInfo) OrderProcessServiceUtil.extractJsonObject(requestBody, UserInfo.class);
			List<AccountInfo> userAccountInfo = orderProcessService.checkIfAccountExists(userInfo);
			if (userAccountInfo.size() > 0) {
				String responseString = OrderProcessServiceUtil.responseString(userAccountInfo.get(0));
				return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(responseString).build();
			}

			return Response.status(Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON)
					.entity(util.readProperty(OrderProcessServiceConstant.USER_NOT_FOUND)).build();

		} catch (OrderProcessServiceException orderProcessServiceException) {
			logger.error("Error Code: " + orderProcessServiceException.getErrorCode().getDescription());
			orderProcessServiceException.printStackTrace();
			if (orderProcessServiceException.getErrorCode().getDescription()
					.equals(ErrorCode.VALIDATION_ERROR.getDescription())) {
				return Response.status(Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON)
						.entity(OrderProcessServiceConstant.BAD_REQUEST).build();
			}
			return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON)
					.entity(OrderProcessServiceConstant.INTERNAL_ERROR).build();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception trace " + e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON)
					.entity(OrderProcessServiceConstant.INTERNAL_ERROR).build();

		}

	}

	@POST
	@Path("/createOrder")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createOrder(String requestBody) {
		try {
			OrderRequest orderRequest = (OrderRequest) OrderProcessServiceUtil.extractJsonObject(requestBody,
					OrderRequest.class);
			OrderProcessServiceUtil.validateOrderRequest(orderRequest);
			OrderPlaceResponse orderPlaceResponse = orderProcessService.authorizeOrder(orderRequest);
			return Response.status(Status.OK).type(MediaType.APPLICATION_JSON)
					.entity(OrderProcessServiceUtil.responseString(orderPlaceResponse)).build();

		} catch (OrderProcessServiceException orderProcessServiceException) {
			logger.error("Error Code: " + orderProcessServiceException.getErrorCode().getDescription());
			orderProcessServiceException.printStackTrace();
			if (orderProcessServiceException.getErrorCode().getDescription()
					.equals(ErrorCode.VALIDATION_ERROR.getDescription())) {
				return Response.status(Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON)
						.entity(OrderProcessServiceConstant.BAD_REQUEST).build();
			}
			if (orderProcessServiceException.getErrorCode().getDescription()
					.equals(ErrorCode.ORDER_AUTH_ERROR.getDescription())) {
				return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON)
						.entity(OrderProcessServiceConstant.ORDER_DENIED).build();
			}
			return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON)
					.entity(OrderProcessServiceConstant.INTERNAL_ERROR).build();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception message ", e.getMessage(), e.getCause());
			return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON)
					.entity(OrderProcessServiceConstant.INTERNAL_ERROR).build();

		}

	}

	@POST
	@Path("/confirmOrder")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response confirmOrder(String requestBody) {
		try {
			OrderConfirmRequest orderRequest = (OrderConfirmRequest) OrderProcessServiceUtil
					.extractJsonObject(requestBody, OrderConfirmRequest.class);
			int checkTransactionsCount = orderProcessService.checkTransactionsCount();
			if (checkTransactionsCount % 5 == 0) {
				return Response.status(Status.FORBIDDEN).type(MediaType.APPLICATION_JSON)
						.entity(OrderProcessServiceUtil
								.responseString(OrderProcessServiceConstant.ORDER_DENIED))
						.build();
			}
			orderProcessService.placeOrder(orderRequest);
			return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(OrderProcessServiceUtil
					.responseString(util.readProperty(OrderProcessServiceConstant.ORDER_CONFIRMED))).build();

		} catch (OrderProcessServiceException orderProcessServiceException) {
			logger.error("Error Code: " + orderProcessServiceException.getErrorCode().getDescription());
			orderProcessServiceException.printStackTrace();
			if (orderProcessServiceException.getErrorCode().getDescription()
					.equals(ErrorCode.VALIDATION_ERROR.getDescription())) {
				return Response.status(Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON)
						.entity(OrderProcessServiceConstant.BAD_REQUEST).build();
			}
			if (orderProcessServiceException.getErrorCode().getDescription()
					.equals(ErrorCode.CONFIRM_ORDER.getDescription())) {
				return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON)
						.entity(OrderProcessServiceConstant.ORDER_DENIED).build();
			}
			return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON)
					.entity(OrderProcessServiceConstant.INTERNAL_ERROR).build();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception message ", e.getMessage(), e.getCause());
			return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON)
					.entity(OrderProcessServiceConstant.INTERNAL_ERROR).build();

		}

	}

}
