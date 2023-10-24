package org.example;

import org.junit.Assert;
import org.junit.Test;

/**
 * Класс для тестирования Telegram-бота.
 * Этот класс содержит методы для тестирования функциональности бота.
 */
public class BotTest {

    /**
     * Проверка для команды /genre
     */
    @Test
    public void GenreCommandTest() {
        long ChatId = 12345L;
        MessageHandling bot = new MessageHandling();
        String response = bot.parseMessage("/genre", ChatId);
        Assert.assertEquals("Здравствуйте, добро пожаловать в бот рекомендации книг! Выберите жанр:", response);

        /*
         * Проверка для жанра "Научная фантастика"
         */
        response = bot.parseMessage("Научная фантастика", ChatId);
        Assert.assertEquals("Прочитайте 'Автостопом по галактике', 'Время жить и время умирать' или 'Война миров'", response);

        /*
         * Проверка для жанра "Фэнтези"
         */
        response = bot.parseMessage("Фэнтези", ChatId);
        Assert.assertEquals("Прочитайте 'Хоббит', 'Игра престолов' или 'Гарри Поттер'", response);

        /*
         * Проверка для жанра "Романтика"
         */
        response = bot.parseMessage("Романтика", ChatId);
        Assert.assertEquals("Прочитайте 'Великий Гетсби', 'Триумфальная арка' или 'Поющие в терновнике'", response);

        /*
         * Проверка для жанра "Детектив"
         */
        response = bot.parseMessage("Детектив", ChatId);
        Assert.assertEquals("Прочитайте 'Убийство в восточном экспрессе', 'Снеговик' или 'Собака Баскервилей'", response);
    }

    /**
     * Проверка ответа для произвольного сообщения
     */
    @Test
    public void AnyMessageTest() {
        long ChatId = 12345L;
        MessageHandling bot = new MessageHandling();
        String response = bot.parseMessage("Привет", ChatId);
        Assert.assertEquals("Привет", response);
    }

    /**
     * Проверка команды /clearread - полного удаления списка прочитанных книг
     */
    @Test
    public void ClearReadCommandTest() {
        long ChatId = 12345L;
        MessageHandling bot = new MessageHandling();
        bot.parseMessage("/addbook 11.22.63\n Кинг\n 2020", ChatId);
        String response = bot.parseMessage("/clearread", ChatId);
        Assert.assertEquals("Список прочитанных книг очищен!", response);
    }

    /**
     * Проверка команды (/addbook название\n автор\n год) - добавления книги в список прочитанных
     * Проверка команды /getread - вывода общего списка прочитанных книг
     * В конце бот чистит список книг, чтобы проверка работала корректно каждый раз
     */
    @Test
    public void GetReadCommandTest() {
        long ChatId = 123459L;
        MessageHandling bot = new MessageHandling();
        String response = bot.parseMessage("/addbook 11.22.63\n Кинг\n 2020", ChatId);
        Assert.assertEquals("Книга '11.22.63' от автора Кинг (год: 2020) успешно добавлена в список прочитанных!", response);
        response = bot.parseMessage("/getread", ChatId);
        Assert.assertEquals("Прочитанные книги:\n" + "11.22.63", response);
        bot.parseMessage("/clearread", ChatId);
    }

    /**
     * Проверка команды /getbyauthor - вывода списка прочитанных книг по одному автору
     * В конце бот чистит список книг, чтобы проверка работала корректно каждый раз
     */
    @Test
    public void GetByAuthorCommandTest() {
        long ChatId = 1234565L;
        MessageHandling bot = new MessageHandling();
        bot.parseMessage("/addbook 11.22.63\n Кинг\n 2020", ChatId);
        String response = bot.parseMessage("/getbyauthor Кинг", ChatId);
        Assert.assertEquals("Книги автора Кинг:\n" + "11.22.63", response);
        bot.parseMessage("/clearread", ChatId);
    }

    /**
     * Проверка команды /getbyyear - вывода списка прочитанных книг по году
     * В конце бот чистит список книг, чтобы проверка работала корректно каждый раз
     */
    @Test
    public void GetByYearCommandTest() {
        long ChatId = 12345678L;
        MessageHandling bot = new MessageHandling();
        bot.parseMessage("/addbook 11.22.63\n Кинг\n 2020", ChatId);
        String response = bot.parseMessage("/getbyyear 2020", ChatId);
        Assert.assertEquals("Книги 2020 года:\n" + "11.22.63", response);
        bot.parseMessage("/clearread", ChatId);
    }

    /**
     * Проверка команды /get - получения рандомной цитаты
     */
    @Test
    public void GetRandQuoteCommandTest() {
        long ChatId = 12345678L;
        MessageHandling bot = new MessageHandling();
        String response = bot.parseMessage("/get", ChatId);
        Assert.assertEquals("Начинать всегда стоит с того, что сеет сомнения. \n\nБорис Стругацкий.", response);
    }
}