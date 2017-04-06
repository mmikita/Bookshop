package pl.ksiegarnia.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import pl.ksiegarnia.model.Book;
import pl.ksiegarnia.model.Cart;
import pl.ksiegarnia.model.CartItem;
import pl.ksiegarnia.service.CartService;
import pl.ksiegarnia.service.ProductUserService;

@Controller
@RequestMapping(value = "rest/cart")

public class CartRestController {
	@Autowired
	private CartService cartService;
	@Autowired
	private ProductUserService productService;

	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody Cart create(@RequestBody Cart cart) {
		return cartService.create(cart);
	}

	@RequestMapping(value = "/{cartId}", method = RequestMethod.GET)
	public @ResponseBody Cart read(@PathVariable(value = "cartId") String cartId, HttpServletRequest request) {

		return cartService.read(cartId);
	}

	@RequestMapping(value = "/{cartId}", method = RequestMethod.PUT)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void update(@PathVariable(value = "cartId") String cartId, @RequestBody Cart cart) {
		cartService.update(cartId, cart);
	}

	@RequestMapping(value = "/{cartId}", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable(value = "cartId") String cartId) {
		cartService.delete(cartId);
	}

	@RequestMapping(value = "/add/{productId}", method = RequestMethod.PUT)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void addItem(@PathVariable String productId, HttpServletRequest request) {
		String sessionId = request.getSession(true).getId();
		Cart cart = cartService.read(sessionId);
		if (cart == null) {
			cart = cartService.create(new Cart(sessionId));
		}
		Book book = productService.getBookbyId(Integer.valueOf(productId));
		if (book == null) {
			throw new IllegalArgumentException(new Exception(productId));
		}
		cart.addCartItem(new CartItem(book));
		cartService.update(sessionId, cart);
	}

	@RequestMapping(value = "/remove/{productId}", method = RequestMethod.PUT)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void removeItem(@PathVariable String productId, HttpServletRequest request) {
		String sessionId = request.getSession(true).getId();
		Cart cart = cartService.read(sessionId);
		if (cart == null) {
			cart = cartService.create(new Cart(sessionId));
		}
		Book book = productService.getBookbyId(Integer.valueOf(productId));
		if (book == null) {
			throw new IllegalArgumentException(new Exception(productId));
		}
		cart.removeCartItem(new CartItem(book));
		cartService.update(sessionId, cart);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Niepoprawne żądanie, sprawdź przesyłane dane.")
	public void handleClientErrors(Exception ex) {
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Wewnętrznybłąd serwera.")

	public void handleServerErrors(Exception ex) {
	}

}
