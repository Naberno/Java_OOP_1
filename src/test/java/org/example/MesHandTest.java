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
        Assert.assertEquals("Привет", response);
    }


    /**
     * Проверка команды /clearplayed для полной очистки списка пройденных игр
     */
    @Test
    public void testClearPlayedGamesCommand() {
        String response = messageHandling.parseMessage("/clearplayed", ChatId);
        verify(storage, times(1)).clearPlayedGames(ChatId);
        Assert.assertEquals("Список пройденных игр очищен!", response);
        response = messageHandling.parseMessage("/getplayed", ChatId);
        Assert.assertEquals("Список пройденных игр пуст.", response);
    }



    /**
     * Проверка, что игра не добавляется, если она уже существует в базе данных
     */
    @Test
    public void testAddGameCommandWithExistingGame() {
        String textMsg = messageHandling.parseMessage("/addgame", 123L);
        Assert.assertEquals("Введите название игры:", textMsg);
        textMsg = messageHandling.handleAddTitle("Title", 123L);
        Assert.assertEquals("Введите издателя игры:", textMsg);
        textMsg = messageHandling.handleAddAuthor("Author", 123L);
        Assert.assertEquals("Введите год выхода игры:", textMsg);
        textMsg = messageHandling.handleAddYear("2000", 123L);
        Assert.assertEquals("Игра 'Title' издателя Author (2000) успешно добавлена!\nОцените игру от 1 до 5:", textMsg);
        messageHandling.parseMessage(textMsg, ChatId);
        String response = messageHandling.parseMessage(textMsg, ChatId);
        verify(storage, never()).addPlayedGame(anyString(), anyString(), anyInt(), anyInt(), anyLong());
        Assert.assertEquals("Некорректный формат оценки. Пожалуйста, введите числовое значение от 1 до 5.", response);
    }


    /**
     * Проверка случая, когда вводится не в том формате
     */
    @Test
    public void testAddGameCommandWithInvalidYear() {
        String textMsg = "/addgame папвпав";
        String response = messageHandling.parseMessage(textMsg, ChatId);
        verify(storage, never()).addPlayedGame(anyString(), anyString(), anyInt(), anyInt(), anyLong());
        Assert.assertEquals("Введите название игры:", response);
    }



    /**
     * Проверка команды /getplayed для вывода полного списка пройденных игр при пустом списке
     */
    @Test
    public void testGetPlayedGamesCommandWithEmptyList() {
        List<String> emptyList = new ArrayList<>();
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
        List<String> nonEmptyList = new ArrayList<>();
        nonEmptyList.add("Game 1");
        nonEmptyList.add("Game 2");
        when(storage.getPlayedGames(ChatId)).thenReturn(nonEmptyList);
        String response = messageHandling.parseMessage("/getplayed", ChatId);

        verify(storage, times(1)).getPlayedGames(ChatId);
        Assert.assertEquals("Пройденные игры:\n1. Game 1\n2. Game 2\n", response);
    }


    /**
     * Проверка команды /getbyauthor для получения списка пройденных игр указанного автора для случая, когда автор указан верно
     */
    @Test
    public void testGetGamesByAuthorCommandWithExistingGames() {
        String author = "John Doe";
        List<String> games = new ArrayList<>();
        games.add("Game 1");
        games.add("Game 2");
        when(storage.getGamesByAuthor(author, ChatId)).thenReturn(games);
        String response = messageHandling.parseMessage("/getbyauthor", ChatId);
        Assert.assertEquals("Введите имя автора:", response);
        response = messageHandling.parseMessage(author, ChatId);
        verify(storage, times(1)).getGamesByAuthor(author, ChatId);
        Assert.assertEquals("Игры издателя 'John Doe':\nGame 1\nGame 2", response);
    }


    /**
     * Проверка команды /getbyauthor для получения списка пройденных игр указанного автора для случая, когда авор указан неверно
     */
    @Test
    public void testGetGamesByAuthorCommandWithNoGames() {
        String author = "Nonexistent Author";
        when(storage.getGamesByAuthor(author, ChatId)).thenReturn(new ArrayList<>());
        String response = messageHandling.parseMessage("/getbyauthor " + author, ChatId);
        Assert.assertEquals("Введите имя автора:", response);
        response = messageHandling.parseMessage(author, ChatId);
        verify(storage, times(1)).getGamesByAuthor(author, ChatId);
        Assert.assertEquals("Нет пройденных игр этого издателя.", response);
    }


    /**
     * Проверка команды /getbyyear для получения списка пройденных игр в неправильном указанном году для случая, когда год указан неверно
     */
    @Test
    public void testGetGamesByYearCommandWithNoGames() {
        int year = 1222;
        when(storage.getGamesByYear(year, ChatId)).thenReturn(new ArrayList<>());

        String response = messageHandling.parseMessage("/getbyyear", ChatId);
        Assert.assertEquals("Введите год (не более 4 цифр):", response);
        response = messageHandling.parseMessage(String.valueOf(year), ChatId);

        verify(storage).getGamesByYear(year, ChatId);

        Assert.assertEquals("Нет пройденных игр этого года.", response);

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
        messageHandling.parseMessage("/getbyyear", ChatId);
        String response = messageHandling.parseMessage(String.valueOf(year), ChatId);
        verify(storage, times(1)).getGamesByYear(year, ChatId);
        Assert.assertEquals("Игры 2020 года:\nGame 1\nGame 2", response);
    }


    /**
     * Проверка команды /removegame для удаления указанной игры из списка пройденных игр для случая, когда номер игры в списке указан верно
     */
    @Test
    public void testRemoveGameCommandWithValidGameNumber() {
        List<String> playedGames = new ArrayList<>();
        playedGames.add("Game 1");
        playedGames.add("Game 2");
        when(storage.getPlayedGames(ChatId)).thenReturn(playedGames);
        String response = messageHandling.parseMessage("/removegame", ChatId);
        Assert.assertEquals("Введите номер игры, которую нужно удалить:", response);
        response = messageHandling.parseMessage("1", ChatId);
        verify(storage, times(1)).updatePlayedGames(eq(ChatId), any(ArrayList.class));
        Assert.assertEquals("Игра 'Game 1' успешно удалена из списка пройденных!", response);

        response = messageHandling.parseMessage("/getplayed", ChatId);
        Assert.assertEquals("Пройденные игры:\n1. Game 2\n", response);
    }


    /**
     * Проверка команды /removegame для удаления указанной игры из списка пройденных игр для случая, когда номер игры в списке указан неверно
     */
    @Test
    public void testRemoveGameCommandWithInvalidGameNumber() {
        List<String> playedGames = new ArrayList<>();
        playedGames.add("Game 1");
        playedGames.add("Game 2");
        when(storage.getPlayedGames(ChatId)).thenReturn(playedGames);
        String response = messageHandling.parseMessage("/removegame", ChatId);
        Assert.assertEquals("Введите номер игры, которую нужно удалить:", response);
        response = messageHandling.parseMessage("3", ChatId);
        verify(storage, never()).updatePlayedGames(eq(ChatId), any(ArrayList.class));
        Assert.assertEquals("Указанный номер игры не существует.", response);
    }


    /**
     * Проверка команды /removegame для удаления указанной игры из списка пройденных игр для случая, когда указано не число
     */
    @Test
    public void testRemoveGameCommandWithInvalidFormat() {
        String response = messageHandling.parseMessage("/removegame", ChatId);
        Assert.assertEquals("Введите номер игры, которую нужно удалить:", response);
        response = messageHandling.parseMessage("abc", ChatId);
        verify(storage, never()).updatePlayedGames(eq(ChatId), any(ArrayList.class));
        Assert.assertEquals("Некорректный формат номера игры. Пожалуйста, введите число.", response);
    }


    /**
     * Проверка команды /editgame для случая, когда выполняется успешное редактирование игры с правильными данными
     */
    @Test
    public void testEditGameCommandWithValidData() {
        List<String> playedGames = new ArrayList<>();
        playedGames.add("Old Game\nOld Author\n2022");
        when(storage.getPlayedGames(ChatId)).thenReturn(playedGames);
        when(storage.getAllValues(ChatId)).thenReturn(playedGames);
        messageHandling.parseMessage("/editgame", ChatId);
        messageHandling.parseMessage("1", ChatId);
        messageHandling.parseMessage("New Game", ChatId);
        messageHandling.parseMessage("New Author", ChatId);
        String response = messageHandling.parseMessage("2023", ChatId);
        verify(storage, times(1)).editPlayedGame(eq("Old Game"), eq("Old Author"),
                eq(2022), eq("New Game"), eq("New Author"), eq(2023), eq(ChatId));
        Assert.assertEquals("Игра 'Old Game' успешно заменена на игру 'New Game' от издателя New Author (2023) в списке пройденных!", response);
    }


    /**
     * Проверка команды /editgame для случая, когда указанный номер игры недопустим (например, больше размера списка)
     */
    @Test
    public void testEditGameCommandWithInvalidGameNumberTooBig() {
        int number = 2;
        List<String> playedGames = new ArrayList<>();
        playedGames.add("Game 1");

        String response = messageHandling.parseMessage("/editgame", ChatId);
        Assert.assertEquals("Введите номер из списка:", response);
        response = messageHandling.handleEditNumber(String.valueOf(number), ChatId);
        verify(storage, never()).editPlayedGame(
                anyString(), anyString(), anyInt(), anyString(), anyString(), anyInt(), eq(ChatId));
        Assert.assertEquals("Неверный формат. Введите номер игры из списка /getplayed", response);
    }


    /**
     * Проверка команды /editgame для случая, когда данные игры введены в неверном формате.
     */
    @Test
    public void testEditGameCommandWithInvalidDataFormat() {
        String invalidMessage = "1 abc Author 2023";
        String response = messageHandling.parseMessage("/editgame " + invalidMessage, ChatId);
        verify(storage, never()).editPlayedGame(
                anyString(), anyString(), anyInt(), anyString(), anyString(), anyInt(), eq(ChatId));
        Assert.assertEquals("Введите номер из списка:", response);
    }


    /**
     * Тестирование /addgame с допустимым рейтингом в пределах разрешенного диапазона.
     */
    @Test
    public void testHandleRatingWithValidRating() {
        String response = messageHandling.parseMessage("/addgame", 123L);
        Assert.assertEquals("Введите название игры:", response);
        response = messageHandling.handleAddTitle("Title", 123L);
        Assert.assertEquals("Введите издателя игры:", response);
        response = messageHandling.handleAddAuthor("Author", 123L);
        Assert.assertEquals("Введите год выхода игры:", response);
        response = messageHandling.handleAddYear("2000", 123L);
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


    /**
     * Проверка команды /getbyrating для получения списка пройденных игр по рейтингу
     */
    @Test
    public void testHandleDefaultModeGetByRatingCommandWithGames() {
        // Подготовка тестовых данных
        storage.addPlayedGame("Game 1", "Author", 2000, 3, ChatId);
        storage.addPlayedGame("Game 2", "Author", 2001, 4, ChatId);
        List<String> games = new ArrayList<>();
        games.add("1. Game 1: 3.0⭐");
        games.add("2. Game 2: 4.0⭐");

        when(storage.getGamesByAverageRating(ChatId)).thenReturn(games);
        String response = messageHandling.parseMessage("/getbyrating", ChatId);
        verify(storage).getGamesByAverageRating(ChatId);
        Assert.assertEquals("Список игр по среднему рейтингу:\n1. Game 1: 3.0⭐\n2. Game 2: 4.0⭐\n", response);

    }


    /**
     * Проверка ответа при отсутствии данных
     */
    @Test
    public void testHandleDefaultModeGetByRatingCommandNoGames() {
        when(storage.getGamesByAverageRating(ChatId)).thenReturn(new ArrayList<>());
        String response = messageHandling.parseMessage("/getbyrating", ChatId);
        Assert.assertEquals("Нет данных о среднем рейтинге игр.", response);
    }


    /**
     * Тест проверяет правильность вывода avg rating
     */
    @Test
    public void testGetGamesByAverageRating_validData() {
        storage.addPlayedGame("Game 1", "Author", 2000, 3, ChatId);
        storage.addPlayedGame("Game 2", "Author", 2000, 2, ChatId);
        when(storage.getGamesByAverageRating(ChatId)).thenReturn(new ArrayList<>(Arrays.asList("1. Game 1: 3.0⭐", "2. Game 2: 2.0⭐")));
        List<String> games = storage.getGamesByAverageRating(ChatId);
        Assert.assertEquals("1. Game 1: 3.0⭐", games.get(0));
        Assert.assertEquals("2. Game 2: 2.0⭐", games.get(1));
    }



    /**
     * Проверка вычисления среднего рейтинга для игры
     * с несколькими оценками
     */
    @Test
    public void testAverageRating_multipleRatings() {
        storage.addPlayedGame("Game", "Author", 2000, 3, ChatId);
        storage.addPlayedGame("Game", "Author", 2000, 4, ChatId);
        double actualAvg = (3 + 4) / 2.0;
        when(storage.getGamesByAverageRating(ChatId))
                .thenReturn(Collections.singletonList("1. Game: " + actualAvg + "⭐"));
        List<String> games = storage.getGamesByAverageRating(ChatId);
        Assert.assertEquals("1. Game: " + actualAvg + "⭐", games.get(0));
    }


}