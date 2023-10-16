package org.example;
import org.junit.Assert;
import org.junit.Test;
public class BotTest {
    @Test
    public void parseTest() {
        Bot bot = new Bot();

        /**
         Проверка для команды /start
         */
        String response = bot.parseMessage("/start");
        Assert.assertEquals("Приветствую, это литературный бот. Жми /get, чтобы получить случайную цитату. Жми /genre, чтобы перейти в раздел жанры книг", response);

        /**
         Проверка для команды /help
         */
        response = bot.parseMessage("/help");
        Assert.assertEquals("Приветствую, это литературный бот. Жми /get, чтобы получить случайную цитату. Жми /genre, чтобы перейти в раздел жанры книг", response);

        /**
         Проверка для команды /genre
         */
        response = bot.parseMessage("/genre");
        Assert.assertEquals("Здравствуйте, добро пожаловать в бот рекомендации книг! Выберите жанр:", response);

        /**
         Проверка для жанра "Научная фантастика"
         */
        response = bot.parseMessage("Научная фантастика");
        Assert.assertEquals("Прочитайте 'Автостопом по галактике', 'Время жить и время умирать' или 'Война миров'", response);

        /**
         Проверка для жанра "Фэнтези"
         */
        response = bot.parseMessage("Фэнтези");
        Assert.assertEquals("Прочитайте 'Хоббит', 'Игра престолов' или 'Гарри Поттер'", response);

        /**
         Проверка для жанра "Романтика"
         */
        response = bot.parseMessage("Романтика");
        Assert.assertEquals("Прочитайте 'Великий Гетсби', 'Триумфальная арка' или 'Поющие в терновнике'", response);

        /**
         Проверка для жанра "Детектив"
         */
        response = bot.parseMessage("Детектив");
        Assert.assertEquals("Прочитайте 'Убийство в восточном экспрессе', 'Снеговик' или 'Собака Баскервилей'", response);

        /**
         Проверка для произвольного сообщения
         */
        response = bot.parseMessage("Привет");
        Assert.assertEquals("Привет", response);
    }
}
