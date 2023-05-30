import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/library")
public class LibraryController {
    private List<Book> books = new ArrayList<>();
    private List<Order> orders = new ArrayList<>();
    private List<Basket> baskets = new ArrayList<>();

    @GetMapping("/books")
    public Flux<Book> getAllBooks() {
        return Flux.fromIterable(books);
    }

    @PostMapping("/basket/add")
    public Mono<Void> addToBasket(@RequestBody Book book) {
        Basket basket = getOrCreateBasket();
        basket.getBooks().add(book);
        return Mono.empty();
    }

    @DeleteMapping("/basket/remove")
    public Mono<Void> removeFromBasket(@RequestParam("isbn") String isbn) {
        baskets.forEach(basket -> basket.getBooks().removeIf(book -> book.getIsbn().equals(isbn)));
        return Mono.empty();
    }

    @GetMapping("/basket")
    public Mono<Basket> getBasket() {
        return Mono.justOrEmpty(getOrCreateBasket());
    }

    @PostMapping("/order")
    public Mono<Void> placeOrder() {
        Basket basket = getOrCreateBasket();
        Order order = new Order(new ArrayList<>(basket.getBooks()));
        orders.add(order);
        baskets.remove(basket);
        return Mono.empty();
    }

    @PostMapping("/books/{orderId}/return")
    public Mono<Void> returnBook(@PathVariable("orderId") int orderId, @RequestParam("isbn") String isbn) {
        Optional<Order> optionalOrder = orders.stream().filter(order -> order.getId() == orderId).findFirst();
        optionalOrder.ifPresent(order -> order.getBooks().removeIf(book -> book.getIsbn().equals(isbn)));
        return Mono.empty();
    }

    @DeleteMapping("/order/{orderId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Mono<Void> returnOrder(@PathVariable("orderId") int orderId) {
        orders.removeIf(order -> order.getId() == orderId);
        return Mono.empty();
    }

    private Basket getOrCreateBasket() {
        if (baskets.isEmpty()) {
            Basket basket = new Basket();
            baskets.add(basket);
            return basket;
        } else {
            return baskets.get(0);
        }
    }
}
