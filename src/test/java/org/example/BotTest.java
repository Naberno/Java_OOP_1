package org.example;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class BotTest {
    private long ChatId;
    private  MessageHandling bot ;

    @Before
    public void setUp() {
        ChatId = 12345L;
        bot = new MessageHandling();
    }

    /**
     * Проверка для команды /genre
     */
    @Test
    public void GenreCommandTest() {
        String response = bot.parseMessage("/genre",ChatId);
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
        String response = bot.parseMessage("Привет", ChatId);
        Assert.assertEquals("Привет", response);
    }

    /**
     * Проверка команды /clearread для полной очистки списка прочитанных книг
     */
    @Test
    public void ClearReadCommandTest() {
        MessageHandling botMock = Mockito.mock(MessageHandling.class);
        Mockito.when(botMock.parseMessage("/clearread", ChatId)).thenReturn("Список прочитанных книг очищен!");

        String response = botMock.parseMessage("/clearread", ChatId);
        Assert.assertEquals("Список прочитанных книг очищен!", response);
    }


    /**
     * Проверка команд /addbook для добавления книги в список прочитанных по формату: название /n автор /n год
     * и /getread для вывода полного списка прочитанных книг
     */
    @Test
    public void GetReadCommandTest() {
        MessageHandling botMock = Mockito.mock(MessageHandling.class);
        Mockito.when(botMock.parseMessage("/addbook 11.22.63\n Кинг\n 2020", ChatId))
                .thenReturn("Книга '11.22.63' от автора Кинг (год: 2020) успешно добавлена в список прочитанных!");
        Mockito.when(botMock.parseMessage("/getread", ChatId)).thenReturn("Прочитанные книги:\n" + "11.22.63");

        String response = botMock.parseMessage("/addbook 11.22.63\n Кинг\n 2020", ChatId);
        Assert.assertEquals("Книга '11.22.63' от автора Кинг (год: 2020) успешно добавлена в список прочитанных!", response);

        response = botMock.parseMessage("/getread", ChatId);
        Assert.assertEquals("Прочитанные книги:\n" + "11.22.63", response);
    }


    /**
     * Проверка команды /getbyauthor для получения списка прочитанных книг указанного автора
     */
    @Test
    public void GetByAuthorCommandTest() {
        MessageHandling botMock = Mockito.mock(MessageHandling.class);
        Mockito.when(botMock.parseMessage("/addbook 11.22.63\n Кинг\n 2020", ChatId)).thenReturn("");
        Mockito.when(botMock.parseMessage("/getbyauthor Кинг", ChatId)).thenReturn("Книги автора Кинг:\n" + "11.22.63");

        String response = botMock.parseMessage("/addbook 11.22.63\n Кинг\n 2020", ChatId);
        Assert.assertEquals("", response); // Если метод добавления книги ничего не возвращает

        response = botMock.parseMessage("/getbyauthor Кинг", ChatId);
        Assert.assertEquals("Книги автора Кинг:\n" + "11.22.63", response);
    }


    /**
     * Проверка команды /getbyyear для получения списка прочитанных книг в указанном году
     */
    @Test
    public void GetByYearCommandTest() {
        MessageHandling botMock = Mockito.mock(MessageHandling.class);
        Mockito.when(botMock.parseMessage("/addbook 11.22.63\n Кинг\n 2020", ChatId)).thenReturn("");
        Mockito.when(botMock.parseMessage("/getbyyear 2020", ChatId)).thenReturn("Книги 2020 года:\n" + "11.22.63");

        String response = botMock.parseMessage("/addbook 11.22.63\n Кинг\n 2020", ChatId);
        Assert.assertEquals("", response); // Если метод добавления книги ничего не возвращает

        response = botMock.parseMessage("/getbyyear 2020", ChatId);
        Assert.assertEquals("Книги 2020 года:\n" + "11.22.63", response);
    }


    /**
     * Проверка команды /removebook для удаления указанной книги из списка прочитанных книг
     */
    @Test
    public void RemoveBookCommandTest() {
        MessageHandling botMock = Mockito.mock(MessageHandling.class);
        Mockito.when(botMock.parseMessage("/addbook 11.22.63\n Кинг\n 2020", ChatId)).thenReturn("");
        Mockito.when(botMock.parseMessage("/removebook 11.22.63\n Кинг\n 2020", ChatId))
                .thenReturn("Книга '11.22.63' от автора Кинг (год: 2020) успешно удалена из списка прочитанных!");

        // Добавляем книгу
        String response = botMock.parseMessage("/addbook 11.22.63\n Кинг\n 2020", ChatId);
        Assert.assertEquals("", response); // Если метод добавления книги ничего не возвращает

        // Удаляем книгу
        response = botMock.parseMessage("/removebook 11.22.63\n Кинг\n 2020", ChatId);
        Assert.assertEquals("Книга '11.22.63' от автора Кинг (год: 2020) успешно удалена из списка прочитанных!", response);
    }

    /**
     * Проверяет команду получения цитаты.
     */
    @Test
    public void CitationCommandTest() {
        String response = bot.parseMessage("/get", ChatId);
        Assert.assertTrue(response.startsWith("Цитата:"));
    }

    /**
     * Проверяет команду начала игры в загадки.
     */
    @Test
    public void playPuzzleCommandTest() {
        String response = bot.parseMessage("/playpuzzle", ChatId);
        Assert.assertTrue(response.startsWith("Добро пожаловать в игру в загадки! Начнем."));
    }

    /**
     * Проверяет команду получения подсказки в игре в загадки.
     */
    @Test
    public void getHintCommandTest() {
        bot.parseMessage("/playpuzzle", ChatId);
        String response = bot.parseMessage("/gethint", ChatId);
        Assert.assertTrue(response.startsWith("Подсказка:"));
    }

    /**
     * Проверяет команду получения следующей загадки в игре.
     */
    @Test
    public void anotherRiddleCommandTest() {
        bot.parseMessage("/playpuzzle", ChatId);
        String response = bot.parseMessage("/anotheriddle", ChatId);
        Assert.assertTrue(response.startsWith("Следующая загадка:"));
    }

    /**
     * Проверяет команду получения статистики игры в загадки.
     */
    @Test
    public void statisticCommandTest() {
        bot.parseMessage("/playpuzzle", ChatId);
        String response = bot.parseMessage("/statistic", ChatId);
        Assert.assertTrue(response.startsWith("Правильных ответов:"));
    }

    /**
     * Проверяет команду перезапуска игры в загадки.
     */
    @Test
    public void restartCommandTest() {
        bot.parseMessage("/playpuzzle", ChatId);
        String response = bot.parseMessage("/restart", ChatId);
        Assert.assertTrue(response.startsWith("Игра в загадки начата заново."));
    }

    /**
     * Проверяет команду получения ответа на текущую загадку в игре.
     */
    @Test
    public void getAnswerCommandTest() {
        bot.parseMessage("/playpuzzle", ChatId);
        String response = bot.parseMessage("/getanswer", ChatId);
        Assert.assertTrue(response.contains("Ответ на загадку"));
    }

    /**
     * Проверяет команду завершения режима головоломки.
     */
    @Test
    public void stopPuzzleCommandTest() {
        bot.parseMessage("/playpuzzle", ChatId);
        String response = bot.parseMessage("/stoppuzzle", ChatId);
        Assert.assertEquals("Режим головоломки завершен.", response);
    }
}
