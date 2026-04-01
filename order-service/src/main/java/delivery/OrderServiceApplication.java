//разобраться с зависимостями в gradle, привести к единому виду
//проверить корректно ли в OrderResponse выводится id или лучше его убрать
//в OrderResponse убрать отображение null полей

package delivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

}

