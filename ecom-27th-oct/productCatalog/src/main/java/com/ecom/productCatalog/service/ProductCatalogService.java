package com.ecom.productCatalog.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ecom.dbagent.dao.DaoInterface;
import com.ecom.dbagent.daoImpl.DaoImpl;
import com.ecom.productCatalog.exceptions.ErrorCode;
import com.ecom.productCatalog.exceptions.ProductCatalogException;
import com.ecom.productCatalog.model.Book;
import com.ecom.productCatalog.response.ProductCatalogResponse;
import com.ecom.productCatalog.util.ProductCatalogConstant;
import com.ecom.productCatalog.util.ProductCatalogUtil;

public class ProductCatalogService {
	private DaoInterface dao;
	private ProductCatalogUtil productUtil = new ProductCatalogUtil();

	public ProductCatalogService() {
		this.dao = new DaoImpl();
	}

	public List<String> getAllCategories() throws ProductCatalogException {
		try {
			dao.openCurrentSession();
			List<String> categories = dao.findAll(productUtil.readProperty(ProductCatalogConstant.ALL_CATEGORY_QUERY));
			dao.closeCurrentSession();
			return categories;
		} catch (Exception e) {
			throw new ProductCatalogException(ErrorCode.QUERY_PROCESSING_ERROR);
		}

	}

	public ProductCatalogResponse getAllProducts(String category) throws ProductCatalogException {
		try {
			dao.openCurrentSession();
			Map paramValues = new HashMap<String, String>();
			paramValues.put(ProductCatalogConstant.CATEGORY, category);
			List<Book> categories = dao.findAllWithCondition(
					productUtil.readProperty(ProductCatalogConstant.CATEGORY_PRODUCT_INFO_QUERY), paramValues);
			ProductCatalogResponse productCatalogResponse = new ProductCatalogResponse();
			productCatalogResponse.setBooks(categories);
			dao.closeCurrentSession();
			return productCatalogResponse;
		} catch (Exception e) {
			throw new ProductCatalogException(ErrorCode.QUERY_PROCESSING_ERROR);
		}

	}

	public ProductCatalogResponse getProductInfo(String bookId) throws ProductCatalogException {

		try {
			dao.openCurrentSession();
			Map paramValues = new HashMap<String, String>();
			paramValues.put(ProductCatalogConstant.BOOK_ID, bookId);
			List<Book> categories = dao.findAllWithCondition(
					productUtil.readProperty(ProductCatalogConstant.PRODUCT_INFO_QUERY), paramValues);
			ProductCatalogResponse productCatalogResponse = new ProductCatalogResponse();
			productCatalogResponse.setBooks(categories);
			dao.closeCurrentSession();
			return productCatalogResponse;
		} catch (Exception e) {
			throw new ProductCatalogException(ErrorCode.QUERY_PROCESSING_ERROR);
		}

	}

}
