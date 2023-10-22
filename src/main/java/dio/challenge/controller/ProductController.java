package dio.challenge.controller;

import dio.challenge.model.Product;
import dio.challenge.service.ProductService;
import dio.challenge.strategy.DiscountStrategy;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.annotations.OpenAPI30;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/products", produces = {"application/json"})
@Tag(name = "products-api-rest")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private DiscountStrategy blackFridayDiscountStrategy; // Injeção da estratégia para Black Friday
    @Autowired
    private DiscountStrategy marketDiscountStrategy; // Injeção da estratégia para a Feira

    @Operation(summary = "Solicita todos elementos do banco", method = "GET")
    @GetMapping
    public List<Product> getAllProducts() {
        System.out.println("GET - Retornando todos elementos");
        return productService.getAllProducts();
    }

    @Operation(summary = "Solicita um elemento específico por ID", method = "GET")
    @GetMapping("/{id}")
    public Optional<Product> getProduct(@PathVariable Long id) {
        System.out.println("GET by ID - Retornando elemento por ID");
        return productService.getProductById(id);
    }

    @Operation(summary = "Insere um elemento no banco", method = "POST")
    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        System.out.println("POST - Inserindo o produto");

        verifyDiscount(product);

        return productService.createProduct(product);
    }

    @Operation(summary = "Atualiza um elemento específico por ID", method = "PUT")
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
        System.out.println("PUT - Atualizando o produto");
        Optional<Product> existingProduct = productService.getProductById(id);

        // Verifica se o produto existe, se não existir é retornado uma resposta HTTP com o status "404 Not Found"
        if (existingProduct.isPresent()) {
            Product product = existingProduct.get();
            product.setName(updatedProduct.getName());
            product.setPrice(updatedProduct.getPrice());

            verifyDiscount(product);

            productService.updateProduct(product);
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete um elemento específico por ID", method = "DELETE")
    @DeleteMapping("/{id}")
    public void deleteProduct (@PathVariable Long id){
        System.out.println("DELETE - Deletando o produto");
        productService.deleteProduct(id);
    }

    // Método para verificar se há um desconto a ser aplicado
    private void verifyDiscount(Product product) {
        LocalDate currentDate = LocalDate.now();
        if (currentDate.getMonthValue() == 11 && currentDate.getDayOfMonth() == 24) {
            // É 24 de novembro, aplique o desconto de Black Friday
            double originalPrice = product.getPrice();
            double discountedPrice = blackFridayDiscountStrategy.applyDiscount(originalPrice);
            product.setPrice(originalPrice - discountedPrice);
        }
        if (currentDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
            // É sábado, aplique o desconto de feira
            double originalPrice = product.getPrice();
            double discountedPrice = marketDiscountStrategy.applyDiscount(originalPrice);
            product.setPrice(originalPrice - discountedPrice);
        }
    }
}
