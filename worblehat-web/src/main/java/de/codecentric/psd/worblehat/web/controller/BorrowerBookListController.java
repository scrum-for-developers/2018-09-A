package de.codecentric.psd.worblehat.web.controller;

import de.codecentric.psd.worblehat.domain.Book;
import de.codecentric.psd.worblehat.domain.BookAlreadyBorrowedException;
import de.codecentric.psd.worblehat.domain.BookService;
import de.codecentric.psd.worblehat.domain.Borrowing;
import de.codecentric.psd.worblehat.web.formdata.BookBorrowFormData;
import de.codecentric.psd.worblehat.web.formdata.BorrowerBookListFormData;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.SetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Controller for BorrowingBook
 */
@RequestMapping("/borrowerBookList")
@Controller
public class BorrowerBookListController {

	private BookService bookService;
	private ModelMap model;

	@Autowired
	public BorrowerBookListController(BookService bookService) {
		this.bookService= bookService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public void setupForm(final ModelMap model) {
		this.model = model;
		model.put("borrowerFormData", new BorrowerBookListFormData());
		model.put("borrowerBookResultList", new ArrayList<Borrowing>());
	}

	@Transactional
	@RequestMapping(method = RequestMethod.POST)
	public String processSubmit(@ModelAttribute("borrowFormData") @Valid BorrowerBookListFormData borrowerBookListFormData,
			BindingResult result) {
		if (result.hasErrors()) {
			return "borrowerBookList";
		}
		List<Borrowing> borrows = bookService.findBooksByBorrower(borrowerBookListFormData.getEmail());
		if(borrows.isEmpty()) {
			result.rejectValue("email", "noBookExists");
			return "borrowerBookList";
		}
		this.model.put("borrowerBookResultList", borrows);
		return "borrowerBookResultList";
	}

	@ExceptionHandler(Exception.class)
	public String handleErrors(Exception ex, HttpServletRequest request) {
		return "home";
	}
}
