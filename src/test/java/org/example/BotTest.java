package org.example;

import org.junit.Assert;
import org.junit.Test;
public class BotTest {
    /**
     * Проверка для команды /genre
     */
    @Test
    public void GenreCommandTest() {
        MessageHandling bot = new MessageHandling();
        String response = bot.parseMessage("/genre");
        Assert.assertEquals("Здравствуйте, добро пожаловать в бот рекомендации книг! Выберите жанр:", response);

        /*
         * Проверка для жанра "Научная фантастика"
         */
        response = bot.parseMessage("Научная фантастика");
        Assert.assertEquals("Прочитайте 'Автостопом по галактике', 'Время жить и время умирать' или 'Война миров'", response);

        /*
         * Проверка для жанра "Фэнтези"
         */
        response = bot.parseMessage("Фэнтези");
        Assert.assertEquals("Прочитайте 'Хоббит', 'Игра престолов' или 'Гарри Поттер'", response);

        /*
         * Проверка для жанра "Романтика"
         */
        response = bot.parseMessage("Романтика");
        Assert.assertEquals("Прочитайте 'Великий Гетсби', 'Триумфальная арка' или 'Поющие в терновнике'", response);

        /*
         * Проверка для жанра "Детектив"
         */
        response = bot.parseMessage("Детектив");
        Assert.assertEquals("Прочитайте 'Убийство в восточном экспрессе', 'Снеговик' или 'Собака Баскервилей'", response);
    }

    /**
     * Проверка для произвольного сообщения
     */
    @Test
    public void AnyMessageTest() {
        MessageHandling bot = new MessageHandling();
        String response = bot.parseMessage("Привет");
        Assert.assertEquals("Привет", response);
    }
}