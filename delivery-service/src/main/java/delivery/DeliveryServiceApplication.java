package delivery;
/*
1) добавить у DeliveryEntity поле Стринговое "Адрес" и поле Лонг "номер дома"
2) изменить OrderPaidEvent
3) изменить для этого все реквесты респонсы
4) добавить метод который выбирает ближайшего курьера
5) сделать так чтобы курьер реально доставлял определенное время заказ и после становился свободен
 */
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DeliveryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeliveryServiceApplication.class, args);
    }

}
