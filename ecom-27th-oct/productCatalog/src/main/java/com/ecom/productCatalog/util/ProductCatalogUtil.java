package com.ecom.productCatalog.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.ecom.productCatalog.exceptions.ErrorCode;
import com.ecom.productCatalog.exceptions.ProductCatalogException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProductCatalogUtil {

	private Properties properties = null;

	public String readProperty(String key) throws ProductCatalogException {
		try {
			if (properties == null) {
				properties = new Properties();
				InputStream inputStream = this.getClass().getClassLoader()
						.getResourceAsStream(ProductCatalogConstant.CONFIG_FILE);
				properties.load(inputStream);
				inputStream.close();
			}
			return (String) properties.get(key);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ProductCatalogException(ErrorCode.UNABLE_TO_START_SERVICE);
		}

	}

	public static <T> String responseString(T allProducts) throws ProductCatalogException {
		ObjectMapper mapper = new ObjectMapper();
		String writeValueAsString;
		try {
			mapper.writeValueAsString(allProducts);
			writeValueAsString = mapper.writeValueAsString(allProducts);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new ProductCatalogException(ErrorCode.BUSINESS_VALIDATION_ERROR);
		}
		return writeValueAsString;
	}

}
