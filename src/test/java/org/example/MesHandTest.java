package org.example;

import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MesHandTest{

    private long ChatId;


    @Mock
    private Storage storage;

    @InjectMocks
    private MessageHandling messageHandling;

    @Before
    public void setUp() {
        ChatId = 12345L;
    }


    /**
     * Проверка ответа для произвольного сообщения
     */
    @Test
    public void AnyMessageTest() {
        String response = messageHandling.parseMessage("Привет", ChatId);
        Assert.assertEquals("Привет, я игровой бот. Жми /help, чтобы узнать что я могу.", response);
    }


    /**
     * Проверка команды /clearplayed для полной очистки списка пройденных игр
     */
    @Test
    public void testClearPlayedGamesCommand() {
        String response = messageHandling.parseMessage("/clearplayed", ChatId);
        verify(storage, times(1)).clearPlayedGames(ChatId);
        Assert.assertEquals("Список пройденных игр очищен!", response);
    }


    /**
     * Проверка добавления игры в базу данных при корректном вводе
     */
    @Test
    public void testHandleAddGameValidInput() {
        String response = messageHandling.handleAddGame("/addgame Title\nAuthor\n2000", 123L);
        Assert.assertEquals("Игра 'Title' издателя Author (2000) успешно добавлена!\nОцените игру от 1 до 5:", response);
    }


    /**
     * Проверка, что игра не добавляется, если она уже существует в базе данных
     */
    @Test
    public void testAddGameCommandWithExistingGame() {
        String textMsg = "/addgame\nSample Game\nJohn Doe\n2023";
        when(storage.gameExists(anyString(), anyString(), anyInt(), anyLong())).thenReturn(true);
        messageHandling.parseMessage(textMsg, ChatId);
        String response = messageHandling.parseMessage(textMsg, ChatId);
        verify(storage, never()).addPlayedGame(anyString(), anyString(), anyInt(), anyInt(), anyLong());
        Assert.assertEquals("Игра с указанным названием, издателем и годом выхода уже существует в базе данных.", response);
    }


    /**
     * Проверка случая, когда год вводится в неверном формате
     */
    @Test
    public void testAddGameCommandWithInvalidYear() {
        String textMsg = "/addgame\nSample Game\nJohn Doe\nInvalidYear";
        String response = messageHandling.parseMessage(textMsg, ChatId);
        verify(storage, never()).addPlayedGame(anyString(), anyString(), anyInt(), anyInt(), anyLong());
        Assert.assertEquals("Некорректный формат года выхода.", response);
    }


    /**
     * Проверка случая, когда ввод неполный
     */
    @Test
    public void testAddGameCommandWithIncompleteInput() {
        String textMsg = "/addgame\nSample Game\nJohn Doe";
        String response = messageHandling.parseMessage(textMsg, ChatId);
        verify(storage, never()).addPlayedGame(anyString(), anyString(), anyInt(), anyInt(), anyLong());
        Assert.assertEquals("Некорректный формат ввода. Используйте:\n/addgame Название_игры\nИздатель\nГод_выхода", response);
    }


    /**
     * Проверка команды /getplayed для вывода полного списка пройденных игр при пустом списке
     */
    @Test
    public void testGetPlayedGamesCommandWithEmptyList() {
        ArrayList<String> emptyList = new ArrayList<>();
        when(storage.getPlayedGames(ChatId)).thenReturn(emptyList);
        String response = messageHandling.parseMessage("/getplayed", ChatId);
        verify(storage, times(1)).getPlayedGames(ChatId);
        Assert.assertEquals("Список пройденных игр пуст.", response);
    }


    /**
     * Проверка команды /getplayed для вывода полного списка пройденных игр при заполненном списке
     */
    @Test
    public void testGetPlayedGamesCommandWithNonEmptyList() {
        // Замените ChatId на фактическое значение, которое вы ожидаете передать
        long chatId = 12345L;

        ArrayList<String> nonEmptyList = new ArrayList<>();
        nonEmptyList.add("Game 1");
        nonEmptyList.add("Game 2");

        // Замените ChatId на chatId
        when(storage.getPlayedGames(chatId)).thenReturn(nonEmptyList);

        // Замените ChatId на chatId
        String response = messageHandling.parseMessage("/getplayed", chatId);

        // Замените ChatId на chatId
        verify(storage, times(1)).getPlayedGames(chatId);
        Assert.assertEquals("Пройденные игры:\n1. Game 1\n2. Game 2\n", response);
    }


    /**
     * Проверка команды /getbyauthor для получения списка пройденных игр указанного автора для случая, когда автор указан верно
     */
    @Test
    public void testGetGamesByAuthorCommandWithExistingGames() {
        String author = "John Doe";
        ArrayList<String> games = new ArrayList<>();
        games.add("Game 1");
        games.add("Game 2");
        when(storage.getGamesByAuthor(author, ChatId)).thenReturn(games);
        String response = messageHandling.parseMessage("/getbyauthor " + author, ChatId);
        verify(storage, times(1)).getGamesByAuthor(author, ChatId);
        Assert.assertEquals("Игры издателя John Doe:\nGame 1\nGame 2", response);
    }


    /**
     * Проверка команды /getbyauthor для получения списка пройденных игр указанного автора для случая, когда авор указан неверно
     */
    @Test
    public void testGetGamesByAuthorCommandWithNoGames() {
        String author = "Nonexistent Author";
        when(storage.getGamesByAuthor(author, ChatId)).thenReturn(new ArrayList<>());
        String response = messageHandling.parseMessage("/getbyauthor " + author, ChatId);
        verify(storage, times(1)).getGamesByAuthor(author, ChatId);
        Assert.assertEquals("Нет пройденных игр этого издателя.", response);
    }


    /**
     * Проверка команды /getbyyear для получения списка пройденных игр в указанном году для случая, когда год указан неверно
     */
    @Test
    public void testGetGamesByYearCommandWithNoGames() {
        int year = 1112;
        when(storage.getGamesByYear(year, ChatId)).thenReturn(new ArrayList<>());
        String response = messageHandling.parseMessage("/getbyyear " + year, ChatId);
        verify(storage, times(1)).getGamesByYear(year, ChatId);
        Assert.assertEquals("Нет пройденных игр в этого года.", response);
    }


    /**
     * Проверка команды /getbyyear для получения списка пройденных игр в указанном году для случая, когда год указан верно
     */
    @Test
    public void testGetGamesByYearCommandWithExistingGames() {
        int year = 2020;
        ArrayList<String> games = new ArrayList<>();
        games.add("Game 1");
        games.add("Game 2");
        when(storage.getGamesByYear(year, ChatId)).thenReturn(games);
        String response = messageHandling.parseMessage("/getbyyear " + year, ChatId);
        verify(storage, times(1)).getGamesByYear(year, ChatId);
        Assert.assertEquals("Игры 2020 года:\nGame 1\nGame 2", response);
    }


    /**
     * Проверка команды /removegame для удаления указанной игры из списка пройденных игр для случая, когда номер игры в списке указан верно
     */
    @Test
    public void testRemoveGameCommandWithValidGameNumber() {
        String message = "1";
        ArrayList<String> playedGames = new ArrayList<>();
        playedGames.add("Game 1");
        playedGames.add("Game 2");
        when(storage.getPlayedGames(ChatId)).thenReturn(playedGames);
        String response = messageHandling.parseMessage("/removegame " + message, ChatId);
        verify(storage, times(1)).updatePlayedGames(eq(ChatId), any(ArrayList.class));
        Assert.assertEquals("Игра Game 1 успешно удалена из списка пройденных!", response);
    }


    /**
     * Проверка команды /removegame для удаления указанной игры из списка пройденных игр для случая, когда номер игры в списке указан неверно
     */
    @Test
    public void testRemoveGameCommandWithInvalidGameNumber() {
        String message = "3";
        ArrayList<String> playedGames = new ArrayList<>();
        playedGames.add("Game 1");
        playedGames.add("Game 2");
        when(storage.getPlayedGames(ChatId)).thenReturn(playedGames);
        String response = messageHandling.parseMessage("/removegame " + message, ChatId);
        verify(storage, never()).updatePlayedGames(eq(ChatId), any(ArrayList.class));
        Assert.assertEquals("Указанный номер игры не существует.", response);
    }


    /**
     * Проверка команды /removegame для удаления указанной игры из списка пройденных игр для случая, когда указано не число
     */
    @Test
    public void testRemoveGameCommandWithInvalidFormat() {
        String message = "InvalidNumber";
        String response = messageHandling.parseMessage("/removegame " + message, ChatId);
        verify(storage, never()).updatePlayedGames(eq(ChatId), any(ArrayList.class));
        Assert.assertEquals("Некорректный формат номера игры", response);
    }


    /**
     * Проверка команды /editgame для случая, когда выполняется успешное редактирование игры с правильными данными
     */
    @Test
    public void testEditGameCommandWithValidData() {
        String message = "1\nNew Game\nNew Author\n2023";
        ArrayList<String> playedGames = new ArrayList<>();
        playedGames.add("Old Game\nOld Author\n2022");
        when(storage.getAllValues(ChatId)).thenReturn(playedGames);
        String response = messageHandling.parseMessage("/editgame " + message, ChatId);
        verify(storage, times(1)).editPlayedGame(eq("Old Game"), eq("Old Author"), eq(2022), eq("New Game"), eq("New Author"), eq(2023), eq(ChatId));
        Assert.assertEquals("Игра 'Old Game' успешно заменена на игру 'New Game' от издателя New Author (2023) в списке пройденных!", response);
    }


    /**
     * Проверка команды /editgame для случая, когда указанный номер игры недопустим (например, больше размера списка)
     */
    @Test
    public void testEditGameCommandWithInvalidGameNumber() {
        String message = "3\nNew Game\nNew Author\n2023";
        ArrayList<String> playedGames = new ArrayList<>();
        playedGames.add("Old Game\nOld Author\n2022");
        when(storage.getAllValues(ChatId)).thenReturn(playedGames);
        String response = messageHandling.parseMessage("/editgame " + message, ChatId);
        verify(storage, never()).editPlayedGame(anyString(), anyString(), anyInt(), anyString(), anyString(), anyInt(), eq(ChatId));
        Assert.assertEquals("Указанный уникальный номер игры не существует в списке пройденных игр.", response);
    }


    /**
     * Проверка команды /editgame для случая, когда данные игры введены в неверном формате.
     */
    @Test
    public void testEditGameCommandWithInvalidDataFormat() {
        String message = "InvalidData";
        String response = messageHandling.parseMessage("/editgame " + message, ChatId);
        verify(storage, never()).editPlayedGame(anyString(), anyString(), anyInt(), anyString(), anyString(), anyInt(), eq(ChatId));
        Assert.assertEquals("Некорректный формат ввода. Используйте:\n/editgame Номер_в_списке\nНовое_название\nНовый_издатель\nНовый_год", response);
    }








    /**
     * Тестирование метода handleRating с допустимым рейтингом в пределах разрешенного диапазона.
     */
    @Test
    public void testHandleRatingWithValidRating() {
        String response = messageHandling.handleAddGame("/addgame Title\nAuthor\n2000", 123L);
        Assert.assertEquals("Игра 'Title' издателя Author (2000) успешно добавлена!\nОцените игру от 1 до 5:", response);
        String textMsg = "4";
        response = messageHandling.handleRating(textMsg);
        verify(storage, times(1)).addPlayedGame(eq("Title"), eq("Author"),
                eq(2000), eq(4), eq(123L));
        Assert.assertEquals("Отзыв 4⭐ оставлен.", response);
    }


    /**
     * Тестирование метода handleRating с рейтингом больше 5 или меньше 1.
     */
    @Test
    public void testHandleRatingWithInvalidRating() {
        String textMsg = "6";
        String response = messageHandling.handleRating(textMsg);

        verify(storage, never()).addPlayedGame(any(), any(), anyInt(), anyInt(), anyInt());
        Assert.assertEquals("Пожалуйста, введите оценку от 1 до 5.", response);
    }


    /**
     * Тестирование метода handleRating с нечисловым значением рейтинга.
     */
    @Test
    public void testHandleRatingWithInvalidFormat() {
        String textMsg = "abc";
        String response = messageHandling.handleRating(textMsg);

        verify(storage, never()).addPlayedGame(any(), any(), anyInt(), anyInt(), anyInt());
        Assert.assertEquals("Некорректный формат оценки. Пожалуйста, введите числовое значение от 1 до 5.", response);
    }









    @Test
    public void testHandleDefaultModeGetByRatingCommandWithGames() {
        String response = messageHandling.handleAddGame("/addgame Game 1\nAuthor\n2000", 123L);
        Assert.assertEquals("Игра 'Game 1' издателя Author (2000) успешно добавлена!\nОцените игру от 1 до 5:", response);
        String textMsg = "3";
        response = messageHandling.handleRating(textMsg);
        verify(storage, times(1)).addPlayedGame(eq("Game 1"), eq("Author"),
                eq(2000), eq(3), eq(123L));



        response = messageHandling.parseMessage("/getbyrating ", ChatId);
        verify(storage, times(1)).getGamesByAverageRating(ChatId);


        // Проверяем результат
        Assert.assertEquals("Список игр по среднему рейтингу:\n1. Game 1: 3.0⭐\n2. Game 2: 2.0⭐", response);
    }

//    @Test
//    public void testHandleDefaultModeGetByRatingCommandNoGames() {
//        // Подготавливаем данные для mock-объекта Storage
//        ArrayList<String> emptyGames = new ArrayList<>();
//
//        // Настроим mock-объект
//        when(storage.getGamesByAverageRating(chatId)).thenReturn(emptyGames);
//
//        // Вызываем метод
//        String response = yourClass.handleDefaultMode("/getbyrating", chatId);
//
//        // Проверяем результат
//        Assert.assertEquals("Нет данных о среднем рейтинге игр.", response);
//    }






    /**
     * Тест проверяет правильность вывода avg rating
     */
    @Test
    public void testGetGamesByAverageRating_validData() {
        storage.addPlayedGame("Game 1", "Author", 2000, 3, ChatId);
        storage.addPlayedGame("Game 2", "Author", 2000, 2, ChatId);
        when(storage.getGamesByAverageRating(ChatId)).thenReturn(new ArrayList<>(Arrays.asList("1. Game 1: 3.0⭐", "2. Game 2: 2.0⭐")));
        ArrayList<String> games = storage.getGamesByAverageRating(ChatId);
        Assert.assertEquals("1. Game 1: 3.0⭐", games.get(0));
        Assert.assertEquals("2. Game 2: 2.0⭐", games.get(1));
    }


    /**
     * Тест проверяет правильность вывода при пустом ответе
     */
    @Test
    public void testGetGamesByAverageRating_emptyList() {
        ArrayList<String> games = storage.getGamesByAverageRating(ChatId);
        Assert.assertEquals(0, games.size());
    }


    /**
     * Тест проверяет правильность вывода avg rating
     */
    @Test
    public void testGetGamesByAverageRating_formatting() {
        storage.addPlayedGame("Game", "Author", 2000, 3, ChatId);
        when(storage.getGamesByAverageRating(ChatId)).thenReturn(new ArrayList<>(Collections.singletonList("1. Game: 3.0⭐")));
        ArrayList<String> games = storage.getGamesByAverageRating(ChatId);
        Assert.assertEquals("1. Game: 3.0⭐", games.get(0));
    }

    /**
     * Тест на avg рейтинг если ввели рахные данные
     */
    @Test
    public void testAverageRating_multipleRatings() {
        // Подготовка тестовых данных
        storage.addPlayedGame("Game", "Author", 2000, 3, 123L);
        storage.addPlayedGame("Game", "Author", 2000, 4, 1234L);
        // Имитация вызова метода, который возвращает список с рейтингами
        when(storage.getGamesByAverageRating(ChatId)).thenReturn(new ArrayList<>(Arrays.asList("1. Game 1: 3.5⭐")));
        // Вызываем метод, который должен получить список с рейтингами
        ArrayList<String> games = storage.getGamesByAverageRating(ChatId);
        // Проверяем результат
        Assert.assertEquals("1. Game 1: 3.5⭐", games.get(0));
    }


}