package dio.challenge.strategy;

import org.springframework.stereotype.Component;

@Component
public class MarketDiscountStrategy implements DiscountStrategy {
    @Override
    public double applyDiscount(double originalPrice) {
        // Aplica o desconto de 10% para a feira
        return originalPrice * 0.1;
    }
}
