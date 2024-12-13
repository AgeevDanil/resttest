import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pojos.FoodPojo;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class RestTest {
    @Test
    @DisplayName("Тестирование получение изначального списка продуктов")
    void test(){
        Specifications.installSpecification(Specifications.requestSpecification("http://localhost:8080/api"), Specifications.responseSpecification(200));
        List<FoodPojo> foodList = given()
                .when()
                .get("/food")
                .then()
                .log()
                .all()
                .extract()
                .jsonPath().getList("" ,FoodPojo.class);
        Assertions.assertEquals(4 , foodList.size());
        for (FoodPojo food : foodList) {
            System.out.println("Продукт: " + food);

            // Пример проверки значений
            Assertions.assertNotNull(food.getName(), "Имя продукта не должно быть null");
            Assertions.assertNotNull(food.getType(), "Тип продукта не должен быть null");
            Assertions.assertNotNull(food.getExotic(), "Значение экзотичности не должно быть null");

            switch (food.getName()) {
                case "Апельсин":
                    Assertions.assertEquals("FRUIT", food.getType(), "Тип продукта не совпадает для Апельсин");
                    Assertions.assertTrue(food.getExotic(), "Значение экзотичности не совпадает для Апельсин");
                    break;
                case "Капуста":
                    Assertions.assertEquals("VEGETABLE", food.getType(), "Тип продукта не совпадает для Капуста");
                    Assertions.assertFalse(food.getExotic(), "Значение экзотичности не совпадает для Капуста");
                    break;
                case "Помидор":
                    Assertions.assertEquals("VEGETABLE", food.getType(), "Тип продукта не совпадает для Помидор");
                    Assertions.assertFalse(food.getExotic(), "Значение экзотичности не совпадает для Помидор");
                    break;
                case "Яблоко":
                    Assertions.assertEquals("FRUIT", food.getType(), "Тип продукта не совпадает для Яблоко");
                    Assertions.assertFalse(food.getExotic(), "Значение экзотичности не совпадает для Яблоко");
                    break;
                default:
                    Assertions.fail("Неизвестный продукт: " + food.getName());
            }
        }
    }

    @Test
    @DisplayName("Тестирование добавления нового продукта")
    void test2(){
        Specifications.installSpecification(Specifications.requestSpecification("http://localhost:8080/api"), Specifications.responseSpecification(200));
        String sessionID = given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "  \"name\": \"Кабачок\",\n" +
                        "  \"type\": \"VEGETABLE\",\n" +
                        "  \"exotic\": false\n" +
                        "}")
                .basePath("/food")
                .when()
                .post()
                .then()
                .log()
                .all()
                .extract().
                cookie("JSESSIONID");
        System.out.println(sessionID);
        List<FoodPojo> foodlost = given()
                .cookie("JSESSIONID", sessionID)
                .when()
                .get("/food")
                .then()
                .log()
                .all()
                .extract()
                .jsonPath().getList("" ,FoodPojo.class);
        Assertions.assertEquals("Кабачок", foodlost.get(4).getName());
        Assertions.assertEquals("VEGETABLE", foodlost.get(4).getType());
        Assertions.assertEquals(false, foodlost.get(4).getExotic());
        Assertions.assertEquals(5 , foodlost.size());
    }

    @Test
    @DisplayName("Тестирование возвращения списка в прежний вид")
    void test3(){
        Specifications.installSpecification(Specifications.requestSpecification("http://localhost:8080/api"), Specifications.responseSpecification(200));
        String sessionID = given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "  \"name\": \"Кабачок\",\n" +
                        "  \"type\": \"VEGETABLE\",\n" +
                        "  \"exotic\": false\n" +
                        "}")
                .basePath("/food")
                .when()
                .post()
                .then()
                .log()
                .all()
                .extract().
                cookie("JSESSIONID");
        given()
                .cookie("JSESSIONID", sessionID)
                .basePath("/data/reset")
                .when()
                .post()
                .then()
                .log()
                .all();
        System.out.println(sessionID);
        List<FoodPojo> foodlost = given()
                .cookie("JSESSIONID", sessionID)
                .when()
                .get("/food")
                .then()
                .log()
                .all()
                .extract()
                .jsonPath().getList("" ,FoodPojo.class);
        Assertions.assertEquals(4 , foodlost.size());

    }


}
