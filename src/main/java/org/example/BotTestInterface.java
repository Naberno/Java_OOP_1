package org.example;
import org.junit.Test;


/**
 * Интерфейс для тестирования функционала бота.
 */

public interface BotTestInterface {
    /**
     * Подготавливает необходимые ресурсы перед выполнением тестов.
     */
    void setUp();

    /**
     * Проверка функционала команды жанра.
     */
    @Test
    void GenreCommandTest();

    /**
     * Проверка ответа на произвольное сообщение.
     */
    @Test
    void AnyMessageTest();

    /**
     * Проверка команды для полной очистки списка прочитанных книг.
     */
    @Test
    void testClearReadBooksCommand();

    /**
     * Проверка добавления книги в базу данных при корректном вводе
     */
    @Test
    void testAddBookCommandWithValidInput();

    /**
     * Проверка, что книга не добавляется, если она уже существует в базе данных
     */
    @Test
    void testAddBookCommandWithExistingBook();

    /**
     * Проверка случая, когда год вводится в неверном формате
     */
    @Test
    void testAddBookCommandWithInvalidYear();

    /**
     * Проверка случая, когда ввод неполный
     */
    @Test
    void testAddBookCommandWithIncompleteInput();

    /**
     * Проверка команды /getread для вывода полного списка прочитанных книг при пустом списке
     */
    @Test
    void testGetReadBooksCommandWithEmptyList();

    /**
     * Проверка команды /getread для вывода полного списка прочитанных книг при заполненном списке
     */
    @Test
    void testGetReadBooksCommandWithNonEmptyList();

    /**
     * Проверка команды /getbyauthor для получения списка прочитанных книг указанного автора для случая, когда автор указан верно
     */
    @Test
    void testGetBooksByAuthorCommandWithExistingBooks();

    /**
     * Проверка команды /getbyauthor для получения списка прочитанных книг указанного автора для случая, когда авор указан неверно
     */
    @Test
    void testGetBooksByAuthorCommandWithNoBooks();

    /**
     * Проверка команды /getbyyear для получения списка прочитанных книг в указанном году для случая, когда год указан неверно
     */
    @Test
    void testGetBooksByYearCommandWithNoBooks();

    /**
     * Проверка команды /getbyyear для получения списка прочитанных книг в указанном году для случая, когда год указан верно
     */
    @Test
    void testGetBooksByYearCommandWithExistingBooks();

    /**
     * Проверка команды /removebook для удаления указанной книги из списка прочитанных книг для случая, когда номер книги в списке указан верно
     */
    @Test
    void testRemoveBookCommandWithValidBookNumber();

    /**
     * Проверка команды /removebook для удаления указанной книги из списка прочитанных книг для случая, когда номер книги в списке указан неверно
     */
    @Test
    void testRemoveBookCommandWithInvalidBookNumber();

    /**
     * Проверка команды /removebook для удаления указанной книги из списка прочитанных книг для случая, когда указано не число
     */
    @Test
    void testRemoveBookCommandWithInvalidFormat();

    /**
     * Проверка команды /editbook для случая, когда выполняется успешное редактирование книги с правильными данными
     */
    @Test
    void testEditBookCommandWithValidData();

    /**
     * Проверка команды /editbook для случая, когда указанный номер книги недопустим (например, больше размера списка)
     */
    @Test
    void testEditBookCommandWithInvalidBookNumber();

    /**
     * Проверка команды /editbook для случая, когда данные книги введены в неверном формате.
     */
    @Test
    void testEditBookCommandWithInvalidDataFormat();

    /**
     * Проверяет команду получения цитаты.
     */
    @Test
    void CitationCommandTest();

    /**
     * Проверяет команду начала игры в загадки.
     */
    @Test
    void playPuzzleCommandTest();

    /**
     * Проверяет побочную команду очистки списка загадок, необходимую для последующих тестов.
     */
    @Test
    void ClearPuzzleCommandTest();

    /**
     * Проверяет, что произвольная загадка, вызываемая в сообщении командой начала игры в загадки
     * содержится в списке всех загадок
     */
    @Test
    void testPlayPuzzleContainsRandomPuzzle();

    /**
     * Проверяет команду начала игры в загадки для случая, когда загадок нет.
     */
    @Test
    void testStartPuzzleGameWhenPuzzlesEmpty();

    /**
     * Проверяет команду получения подсказки в игре в загадки.
     */
    @Test
    void getHintCommandTest();

    /**
     * Тестирует команду /gethint
     * Тестирует случай, когда в puzzles есть загадки.
     */
    @Test
    void testGetHintContainsRandomHint();

    /**
     * Метод для извлечения подсказки из строки response
     */
     String extractHintFromResponse(String response);

    /**
     * Тестирует команду /gethint
     *  Проверяет случай, когда загадок нет.
     */
    @Test
    void testGetHintWithoutCurrentPuzzle();

    /**
     * Проверяет команду получения следующей загадки в игре.
     */
    @Test
    void anotherRiddleCommandTest();

    /**
     * Проверяет, что произвольная загадка, вызываемая в сообщении командой следующая загадка в игре в загадки
     * содержится в списке всех загадок
     */
    @Test
    void testAnotheRiddleContainsRandomPuzzle();

    /**
     * Проверяет команду начала игры в загадки для случая, когда текущей загадки нет.
     */
    @Test
    void testanotheriddleCurrentPuzzleEmpty();

    /**
     * Проверяет команду начала игры в загадки для случая, когда загадок нет.
     */
    @Test
    void testanotheriddleWhenPuzzlesEmpty();

    /**
     * Проверяет команду перезапуска игры в загадки.
     */
    @Test
    void restartCommandTest();

    /**
     * Проверяет, что произвольная загадка, вызываемая в сообщении командой перезапуска игры в загадки
     * содержится в списке всех загадок
     */
    @Test
    void testRestartContainsRandomPuzzle();

    /**
     * Проверяет команду получения ответа на текущую загадку в игре.
     */
    @Test
    void getAnswerCommandTest();

    /**
     * Проверяет команду получения ответа на текущую загадку в игре с учетом любой произвольной загадки
     */
    @Test
    void testGetAnswerAndNextPuzzleContainsRandomAnswer();

    /**
     *   Метод для извлечения ответа из строки response
     */
   String extractAnswerFromResponse(String response);

    /**
     * Тестирует команду /getanswer
     *  Проверяет случай, когда загадок нет.
     */
    @Test
    void testGetAnswerWithoutCurrentPuzzle();

    /**
     * Проверяет команду завершения режима головоломки.
     */
    @Test
    void stopPuzzleCommandTest();

    /**
     * Тестирует команду /gethint
     *  Проверяет случай, когда одна конкретная загадка
     */
    @Test
    void testGetHintWithSetPuzzle();

    /**
     * Тестирует команду /playpuzzle
     *  Проверяет случай, когда одна конкретная загадка
     */
    @Test
    void testPlayPuzzleWithSetPuzzle();

    /**
     * Тестирует команду /getanswer
     *  Проверяет случай, когда одна конкретная загадка
     */
    @Test
    void testGetAnswerWithSetPuzzle();

    /**
     * Тестирует команду /anotheriddle
     *  Проверяет случай, когда одна конкретная загадка
     */
    @Test
    void testNextPuzzleWithSetPuzzle();

    /**
     * Проверяет побочную команду очистки текущей загадки, необходимую для последующих тестов.
     */
    @Test
    void СlearCurrentPuzzleCommandTest();

    /**
     * Проверяет побочную команду устанавки текущей загадки и списка загадок
     */
    @Test
    void SetPuzzlePuzzleCommandTest();
}

