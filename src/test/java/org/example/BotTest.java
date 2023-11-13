package org.example;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Mock;
import java.util.*;
import static org.mockito.Mockito.*;

public class BotTest implements BotTestInterface {
    private long ChatId;
    private  MessageHandling bot ;

    private PuzzleGame puzzleGame;

    private Map<String, Puzzle>  puzzles;
    @Mock
    private Storage storage;

    @InjectMocks
    private MessageHandling messageHandling;

    @Before
    public void setUp() {
        ChatId = 12345L;
        bot = new MessageHandling();
        MockitoAnnotations.initMocks(this);
        puzzleGame = new PuzzleGame();
        puzzles = new HashMap<>();
    }

    /**
     * Проверка для команды /genre
     */
    @Test
    public void GenreCommandTest() {
        String response = bot.parseMessage("/genre",ChatId);
        Assert.assertEquals("Здравствуйте, добро пожаловать в бот рекомендации книг! Нажмите /chat и выберите жанр", response);

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
    public void testClearReadBooksCommand() {
        String response = messageHandling.parseMessage("/clearread", ChatId);
        verify(storage, times(1)).clearReadBooks(ChatId);
        Assert.assertEquals("Список прочитанных книг очищен!", response);
    }


    /**
     * Проверка добавления книги в базу данных при корректном вводе
     */
    @Test
    public void testAddBookCommandWithValidInput() {
        String textMsg = "/addbook\nSample Book\nJohn Doe\n2023";
        when(storage.bookExists(anyString(), anyString(), anyInt(), anyLong())).thenReturn(false);
        String response = messageHandling.parseMessage(textMsg, ChatId);
        verify(storage, times(1)).addReadBook("Sample Book", "John Doe", 2023, ChatId);
        Assert.assertEquals("Книга 'Sample Book' от автора John Doe (год: 2023) успешно добавлена в список прочитанных!", response);
    }

    /**
     * Проверка, что книга не добавляется, если она уже существует в базе данных
     */
    @Test
    public void testAddBookCommandWithExistingBook() {
        String textMsg = "/addbook\nSample Book\nJohn Doe\n2023";
        when(storage.bookExists(anyString(), anyString(), anyInt(), anyLong())).thenReturn(true);
        messageHandling.parseMessage(textMsg, ChatId);
        String response = messageHandling.parseMessage(textMsg, ChatId);
        verify(storage, never()).addReadBook(anyString(), anyString(), anyInt(), anyLong());
        Assert.assertEquals("Книга с указанным названием, автором и годом прочтения уже существует в базе данных.", response);
    }


    /**
     * Проверка случая, когда год вводится в неверном формате
     */
    @Test
    public void testAddBookCommandWithInvalidYear() {
        String textMsg = "/addbook\nSample Book\nJohn Doe\nInvalidYear";
        String response = messageHandling.parseMessage(textMsg, ChatId);
        verify(storage, never()).addReadBook(anyString(), anyString(), anyInt(), anyLong());
        Assert.assertEquals("Некорректный формат года прочтения.", response);
    }


    /**
     * Проверка случая, когда ввод неполный
     */
    @Test
    public void testAddBookCommandWithIncompleteInput() {
        String textMsg = "/addbook\nSample Book\nJohn Doe";
        String response = messageHandling.parseMessage(textMsg, ChatId);
        verify(storage, never()).addReadBook(anyString(), anyString(), anyInt(), anyLong());
        Assert.assertEquals("Некорректный формат ввода. Используйте /addbook Название книги\nАвтор\nГод прочтения", response);
    }


    /**
     * Проверка команды /getread для вывода полного списка прочитанных книг при пустом списке
     */
    @Test
    public void testGetReadBooksCommandWithEmptyList() {
        ArrayList<String> emptyList = new ArrayList<>();
        when(storage.getReadBooks(ChatId)).thenReturn(emptyList);
        String response = messageHandling.parseMessage("/getread",ChatId);
        verify(storage, times(1)).getReadBooks(ChatId);
        Assert.assertEquals("Список прочитанных книг пуст.", response);
    }


    /**
     * Проверка команды /getread для вывода полного списка прочитанных книг при заполненном списке
     */
    @Test
    public void testGetReadBooksCommandWithNonEmptyList() {
        ArrayList<String> nonEmptyList = new ArrayList<>();
        nonEmptyList.add("Book 1");
        nonEmptyList.add("Book 2");
        when(storage.getReadBooks(ChatId)).thenReturn(nonEmptyList);
        String response = messageHandling.parseMessage("/getread",ChatId);
        verify(storage, times(1)).getReadBooks(ChatId);
        Assert.assertEquals("Прочитанные книги:\n1. Book 1\n2. Book 2\n", response);
    }


    /**
     * Проверка команды /getbyauthor для получения списка прочитанных книг указанного автора для случая, когда автор указан верно
     */
    @Test
    public void testGetBooksByAuthorCommandWithExistingBooks() {
        String author = "John Doe";
        ArrayList<String> books = new ArrayList<>();
        books.add("Book 1");
        books.add("Book 2");
        when(storage.getBooksByAuthor(author, ChatId)).thenReturn(books);
        String response = messageHandling.parseMessage("/getbyauthor " + author,ChatId);
        verify(storage, times(1)).getBooksByAuthor(author, ChatId);
        Assert.assertEquals("Книги автора John Doe:\nBook 1\nBook 2", response);
    }


    /**
     * Проверка команды /getbyauthor для получения списка прочитанных книг указанного автора для случая, когда авор указан неверно
     */
    @Test
    public void testGetBooksByAuthorCommandWithNoBooks() {
        String author = "Nonexistent Author";
        when(storage.getBooksByAuthor(author, ChatId)).thenReturn(new ArrayList<>());
        String response = messageHandling.parseMessage("/getbyauthor " + author,ChatId);
        verify(storage, times(1)).getBooksByAuthor(author, ChatId);
        Assert.assertEquals("Нет прочитанных книг этого автора.", response);
    }



    /**
     * Проверка команды /getbyyear для получения списка прочитанных книг в указанном году для случая, когда год указан неверно
     */
    @Test
    public void testGetBooksByYearCommandWithNoBooks() {
        int year = 1112;
        when(storage.getBooksByYear(year, ChatId)).thenReturn(new ArrayList<>());
        String response = messageHandling.parseMessage("/getbyyear " + year,ChatId);
        verify(storage, times(1)).getBooksByYear(year, ChatId);
        Assert.assertEquals("Нет прочитанных книг в этом году.", response);
    }

    /**
     * Проверка команды /getbyyear для получения списка прочитанных книг в указанном году для случая, когда год указан верно
     */
    @Test
    public void testGetBooksByYearCommandWithExistingBooks() {
        int year = 2020;
        ArrayList<String> books = new ArrayList<>();
        books.add("Book 1");
        books.add("Book 2");
        when(storage.getBooksByYear(year, ChatId)).thenReturn(books);
        String response = messageHandling.parseMessage("/getbyyear " + year,ChatId);
        verify(storage, times(1)).getBooksByYear(year, ChatId);
        Assert.assertEquals("Книги 2020 года:\nBook 1\nBook 2", response);
    }


    /**
     * Проверка команды /removebook для удаления указанной книги из списка прочитанных книг для случая, когда номер книги в списке указан верно
     */
    @Test
    public void testRemoveBookCommandWithValidBookNumber() {
        String message = "1";
        ArrayList<String> readBooks = new ArrayList<>();
        readBooks.add("Book 1");
        readBooks.add("Book 2");
        when(storage.getReadBooks(ChatId)).thenReturn(readBooks);
        String response = messageHandling.parseMessage("/removebook " + message, ChatId);
        verify(storage, times(1)).updateReadBooks(eq(ChatId), any(ArrayList.class));
        Assert.assertEquals("Книга Book 1 успешно удалена из списка прочитанных!", response);
    }


    /**
     * Проверка команды /removebook для удаления указанной книги из списка прочитанных книг для случая, когда номер книги в списке указан неверно
     */
    @Test
    public void testRemoveBookCommandWithInvalidBookNumber() {
        String message = "3";
        ArrayList<String> readBooks = new ArrayList<>();
        readBooks.add("Book 1");
        readBooks.add("Book 2");
        when(storage.getReadBooks(ChatId)).thenReturn(readBooks);
        String response = messageHandling.parseMessage("/removebook " + message, ChatId);
        verify(storage, never()).updateReadBooks(eq(ChatId), any(ArrayList.class));
        Assert.assertEquals("Указанный номер книги не существует.", response);
    }


    /**
     * Проверка команды /removebook для удаления указанной книги из списка прочитанных книг для случая, когда указано не число
     */
    @Test
    public void testRemoveBookCommandWithInvalidFormat() {
        String message = "InvalidNumber";
        String response = messageHandling.parseMessage("/removebook " + message, ChatId);
        verify(storage, never()).updateReadBooks(eq(ChatId), any(ArrayList.class));
        Assert.assertEquals("Некорректный формат номера книги", response);
    }


    /**
     * Проверка команды /editbook для случая, когда выполняется успешное редактирование книги с правильными данными
     */
    @Test
    public void testEditBookCommandWithValidData() {
        String message = "1\nNew Book\nNew Author\n2023";
        ArrayList<String> readBooks = new ArrayList<>();
        readBooks.add("Old Book\nOld Author\n2022");
        when(storage.getAllValues(ChatId)).thenReturn(readBooks);
        String response = messageHandling.parseMessage("/editbook " + message, ChatId);
        verify(storage, times(1)).editReadBook(eq("Old Book"), eq("Old Author"), eq(2022), eq("New Book"), eq("New Author"), eq(2023), eq(ChatId));
        Assert.assertEquals("Книга 'Old Book' успешно заменена на книгу 'New Book' от автора New Author (год: 2023) в списке прочитанных!", response);
    }


    /**
     * Проверка команды /editbook для случая, когда указанный номер книги недопустим (например, больше размера списка)
     */
    @Test
    public void testEditBookCommandWithInvalidBookNumber() {
        String message = "3\nNew Book\nNew Author\n2023";
        ArrayList<String> readBooks = new ArrayList<>();
        readBooks.add("Old Book\nOld Author\n2022");
        when(storage.getAllValues(ChatId)).thenReturn(readBooks);
        String response = messageHandling.parseMessage("/editbook " + message, ChatId);
        verify(storage, never()).editReadBook(anyString(), anyString(), anyInt(), anyString(), anyString(), anyInt(), eq(ChatId));
        Assert.assertEquals("Указанный уникальный номер книги не существует в списке прочитанных книг.", response);
    }


    /**
     * Проверка команды /editbook для случая, когда данные книги введены в неверном формате.
     */
    @Test
    public void testEditBookCommandWithInvalidDataFormat() {
        String message = "InvalidData";
        String response = messageHandling.parseMessage("/editbook " + message, ChatId);
        verify(storage, never()).editReadBook(anyString(), anyString(), anyInt(), anyString(), anyString(), anyInt(), eq(ChatId));
        Assert.assertEquals("Некорректный формат ввода. Используйте /editbook Уникальный_номер\n Новое_название\nНовый_автор\nНовый_год", response);
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
        String response = bot.parseMessage("/playpuzzle", ChatId); // запуск режима загадок
        Assert.assertTrue(response.startsWith("Добро пожаловать в игру в загадки! Начнем."));
    }


    /**
     * Проверяет побочную команду очистки списка загадок, необходимую для последующих тестов.
     */
    @Test
    public void ClearPuzzleCommandTest() {
        bot.parseMessage("/playpuzzle", ChatId); // запуск режима загадок
        String response = bot.parseMessage("/clearpuzzles 31415926", ChatId); //очистка списка загадок
        Assert.assertEquals("Список загадок успешно очищен", response);
    }

    /**
     * Проверяет побочную команду очистки текущей загадки, необходимую для последующих тестов.
     */
    @Test
    public void СlearCurrentPuzzleCommandTest() {
        bot.parseMessage("/playpuzzle", ChatId); // запуск режима загадок
        String response = bot.parseMessage("/clearcurrentpuzzle 31415926", ChatId); //очистка списка загадок
        Assert.assertEquals("Текущая загадка успешно удалена", response);
    }

    /**
     * Проверяет побочную команду устанавки текущей загадки и списка загадок
     */
    @Test
    public void SetPuzzlePuzzleCommandTest() {
        bot.parseMessage("/playpuzzle", ChatId); // запуск режима загадок
        String response = bot.parseMessage("/setpuzzle 31415926", ChatId); //очистка списка загадок
        Assert.assertEquals("Новая загадка успешно установлена", response);
    }

    /**
     * Проверяет, что произвольная загадка, вызываемая в сообщении командой начала игры в загадки
     * содержится в списке всех загадок
     */
    @Test
        public void testPlayPuzzleContainsRandomPuzzle() {
            puzzles = puzzleGame.getPuzzles();
            String response = bot.parseMessage("/playpuzzle",ChatId);
            String puzzleQuestion = response.replace("Добро пожаловать в игру в загадки! Начнем.\nЗагадка: ", "");
            Assert.assertTrue(puzzles.containsKey(puzzleQuestion));
        }


    /**
     * Проверяет команду начала игры в загадки для случая, когда загадок нет.
     */
    @Test
    public void testStartPuzzleGameWhenPuzzlesEmpty() {
        bot.parseMessage("/playpuzzle",ChatId); // запуск режима загадок
        bot.parseMessage("/clearpuzzles 31415926",ChatId); //очистка списка загадок
        bot.parseMessage("/stoppuzzle",ChatId); // выход из режима загадок
        String response = bot.parseMessage("/playpuzzle",ChatId); // запуск режима с пустым списком загадок
        Assert.assertEquals("Все загадки решены!", response);
    }

    /**
     * Тестирует команду /playpuzzle
     *  Проверяет случай, когда одна конкретная загадка
     */
    @Test
    public void testPlayPuzzleWithSetPuzzle() {
        bot.parseMessage("/playpuzzle",ChatId); // запуск режима загадок
        bot.parseMessage("/setpuzzle 31415926",ChatId); //замена текущей загадки
        bot.parseMessage("/stoppuzzle",ChatId); // выход из режима загадок
        String response = bot.parseMessage("/playpuzzle",ChatId); // запуск режима с пустым списком загадок
        Assert.assertEquals("Добро пожаловать в игру в загадки! Начнем.\nЗагадка: Часто висит головой вниз, к небу стремится всегда, но полететь не может", response);
    }


    /**
     * Проверяет команду получения подсказки в игре в загадки.
     */
    @Test
    public void getHintCommandTest() {
        bot.parseMessage("/playpuzzle", ChatId); // запуск режима загадок
        String response = bot.parseMessage("/gethint", ChatId); //получение подсказки к текущей загадке
        Assert.assertTrue(response.startsWith("Подсказка:"));
    }

    /**
     * Тестирует команду /gethint
     * Тестирует случай, когда в puzzles есть загадки.
     */
    @Test
    public void testGetHintContainsRandomHint() {
        bot.parseMessage("/playpuzzle", ChatId); // Предварительно начнем загадку
        String response = bot.parseMessage("/gethint", ChatId);
        puzzles = puzzleGame.getPuzzles();
        // Извлекаем подсказку из response
        String hintFromResponse = extractHintFromResponse(response).trim();
        // Проверяем, что подсказка содержится среди значений загадок
        Assert.assertTrue(puzzles.values().stream().anyMatch(puzzle -> puzzle.getHint().equalsIgnoreCase(hintFromResponse)));
    }

    /**
     * Метод для извлечения подсказки из строки response
     */
    public String extractHintFromResponse(String response) {
        // Логика извлечения подсказки из строки response
        // Например, если подсказка начинается после "Подсказка: "
        int startIndex = response.indexOf("Подсказка: ") + "Подсказка: ".length();
        return response.substring(startIndex);
    }

    /**
     * Тестирует команду /gethint
     *  Проверяет случай, когда загадок нет.
     */
    @Test
    public void testGetHintWithoutCurrentPuzzle() {
        bot.parseMessage("/playpuzzle",ChatId); // запуск режима загадок
        bot.parseMessage("/clearcurrentpuzzle 31415926",ChatId); //очистка текущей загадки
        String response = bot.parseMessage("/gethint", ChatId);
        Assert.assertEquals("Нет текущей загадки.", response);
    }

    /**
     * Тестирует команду /gethint
     *  Проверяет случай, когда одна конкретная загадка
     */
    @Test
    public void testGetHintWithSetPuzzle() {
        bot.parseMessage("/playpuzzle",ChatId); // запуск режима загадок
        bot.parseMessage("/setpuzzle 31415926",ChatId); //очистка текущей загадки
        String response = bot.parseMessage("/gethint", ChatId);
        Assert.assertEquals("Подсказка: Это падает с неба во время дождя", response);
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
     * Проверяет, что произвольная загадка, вызываемая в сообщении командой следующая загадка в игре в загадки
     * содержится в списке всех загадок
     */
    @Test
    public void testAnotheRiddleContainsRandomPuzzle() {
        puzzles = puzzleGame.getPuzzles();
        bot.parseMessage("/playpuzzle", ChatId);
        String response = bot.parseMessage("/anotheriddle",ChatId);
        String puzzleQuestion = response.replace("Следующая загадка: ", "");
        Assert.assertTrue(puzzles.containsKey(puzzleQuestion));
    }


    /**
     * Проверяет команду следующей загадки в загадки для случая, когда текущей загадки нет.
     */
    @Test
    public void testanotheriddleCurrentPuzzleEmpty() {
        bot.parseMessage("/playpuzzle",ChatId); // запуск режима загадок
        bot.parseMessage("/clearcurrentpuzzle 31415926",ChatId); //очистка списка загадок
        String response = bot.parseMessage("/anotheriddle",ChatId); // запуск режима с пустым списком загадок
        Assert.assertEquals("Нет текущей загадки!", response);
    }


    /**
     * Проверяет команду следующей загадки в загадки для случая, когда загадок нет.
     */
    @Test
    public void testanotheriddleWhenPuzzlesEmpty() {
        bot.parseMessage("/playpuzzle",ChatId); // запуск режима загадок
        bot.parseMessage("/clearpuzzles 31415926",ChatId); //очистка списка загадок
        String response = bot.parseMessage("/anotheriddle",ChatId); // запуск режима с пустым списком загадок
        Assert.assertEquals("Все загадки решены! Поздравляю, вы решили все загадки! Пожалуйста, нажмите /stoppuzzle, чтобы завершить игру и посмотреть статистику, либо /restart, чтобы начать заново", response);
    }

    /**
     * Тестирует команду /anotheriddle
     *  Проверяет случай, когда одна конкретная загадка
     */
    @Test
    public void testNextPuzzleWithSetPuzzle() {
        bot.parseMessage("/playpuzzle",ChatId); // запуск режима загадок
        bot.parseMessage("/setpuzzle 31415926",ChatId); //очистка текущей загадки
        String response = bot.parseMessage("/anotheriddle", ChatId);
        Assert.assertEquals("Все загадки решены! Поздравляю, вы решили все загадки! Пожалуйста, нажмите /stoppuzzle, чтобы завершить игру и посмотреть статистику, либо /restart, чтобы начать заново", response);
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
     * Проверяет, что произвольная загадка, вызываемая в сообщении командой перезапуска игры в загадки
     * содержится в списке всех загадок
     */
    @Test
    public void testRestartContainsRandomPuzzle() {
        puzzles = puzzleGame.getPuzzles();
        bot.parseMessage("/playpuzzle", ChatId);
        String response = bot.parseMessage("/restart", ChatId);
        String puzzleQuestion = response.replace("Игра в загадки начата заново.\n"+"Добро пожаловать в игру в загадки! Начнем.\nЗагадка: ", "");
        Assert.assertTrue(puzzles.containsKey(puzzleQuestion));
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
     * Проверяет команду получения ответа на текущую загадку в игре с учетом любой произвольной загадки
     */
    @Test
    public void testGetAnswerAndNextPuzzleContainsRandomAnswer() {
        // Предварительно начнем загадку
        messageHandling.parseMessage("/playpuzzle", ChatId);
        // Вызов метода getAnswerAndNextPuzzle
        String response = messageHandling.parseMessage("/getanswer", ChatId);
        // Извлекаем ответ из response
        String answerFromResponse = extractAnswerFromResponse(response).trim();
        // Проверяем, что ответ содержится среди значений загадок
        Assert.assertTrue(puzzleGame.getPuzzles().values().stream().anyMatch(puzzle -> puzzle.getAnswer().equalsIgnoreCase(answerFromResponse)));
        // проверка, что извлеченный ответ answerFromResponse присутствует в коллекции значений всех загадок (puzzleGame.getPuzzles().values()). Мы используем anyMatch, чтобы проверить, что хотя бы для одной загадки в коллекции getPuzzles() её ответ совпадает с извлеченным ответом.
    }

    /**
     *   Метод для извлечения ответа из строки response
      */
    public String extractAnswerFromResponse(String response) {
        // Логика извлечения ответа из строки response
        // Например, если ответ начинается после "Ответ на загадку: "
        int startIndex = response.indexOf("Ответ на загадку: ") + "Ответ на загадку: ".length();
        int endIndex = response.indexOf(".", startIndex);
        return response.substring(startIndex, endIndex);
    }


    /**
     * Тестирует команду /getanswer
     *  Проверяет случай, когда загадок нет.
     */
    @Test
    public void testGetAnswerWithoutCurrentPuzzle() {
        messageHandling.parseMessage("/playpuzzle",ChatId); // запуск режима загадок
        messageHandling.parseMessage("/clearcurrentpuzzle 31415926",ChatId); //очистка текущей загадки
        String response = messageHandling.parseMessage("/getanswer", ChatId);
        Assert.assertEquals("Нет текущей загадки.", response);
    }


    /**
     * Тестирует команду /getanswer
     *  Проверяет случай, когда одна конкретная загадка
     */
    @Test
    public void testGetAnswerWithSetPuzzle() {
        bot.parseMessage("/playpuzzle",ChatId); // запуск режима загадок
        bot.parseMessage("/setpuzzle 31415926",ChatId); //очистка текущей загадки
        String response = bot.parseMessage("/getanswer", ChatId);
        Assert.assertEquals("Ответ на загадку: Капля.\nВсе загадки решены! Поздравляю, вы решили все загадки! Пожалуйста, нажмите /stoppuzzle, чтобы завершить игру и посмотреть статистику, либо /restart, чтобы начать заново", response);
    }

    /**
     * Проверяет команду завершения режима головоломки.
     */
    @Test
    public void stopPuzzleCommandTest() {
        bot.parseMessage("/playpuzzle", ChatId);
        String response = bot.parseMessage("/stoppuzzle", ChatId);
        Assert.assertEquals("Режим головоломки завершен.\nПравильных ответов: 0\n" + "Неправильных ответов: 20\n" + "Процент правильных ответов: 0.0%", response);}
}
