package com.ecom.productCatalog.response;

import java.util.List;

import com.ecom.productCatalog.model.Book;

public class ProductCatalogResponse {

	private List<Book> books;

	public List<Book> getBooks() {
		return books;
	}

	public void setBooks(List<Book> books) {
		this.books = books;
	}

}
