package dio.challenge.strategy;

import org.springframework.stereotype.Component;

@Component
public class BlackFridayDiscountStrategy implements DiscountStrategy {
    @Override
    public double applyDiscount(double originalPrice) {
        // Aplica o desconto de 50% para a Black Friday
        return originalPrice * 0.5;
    }
}