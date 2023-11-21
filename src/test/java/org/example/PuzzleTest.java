package org.example;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PuzzleTest {

    private PuzzleGame game;

    private MessageHandling bot;

    private long ChatId;

    @Before
    public void setUp() {
        // Заполняем словарь головоломками
        game = new PuzzleGame();
        bot = new MessageHandling();
        ChatId = 12345L;
    }


    /**
     * Проверяет включение режима в загадки.
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
        Assert.assertEquals("Режим головоломки завершен.\nПравильных ответов: 0\n" + "Неправильных ответов: 20\n" + "Процент правильных ответов: 0.0%", response);
    }


    /**
     *  Тест проверяет правильный ответ на головоломку
     */
    @Test
    public void testCorrectAnswer() {
        bot.parseMessage("/playpuzzle", ChatId);
        // Устанавливаем текущую головоломку
        game.currentPuzzle = new Puzzle("вопрос", "правильный ответ", "");

        String response = game.checkAnswer(ChatId, "правильный ответ");

            Assert.assertTrue(response.startsWith("Верно! Следующая загадка: "));
    }


    /**
     *  Тест проверяет неправильный ответ на головоломку
     */
    @Test
    public void testIncorrectAnswer() {
        bot.parseMessage("/playpuzzle", ChatId);
        game.currentPuzzle = new Puzzle("вопрос", "правильный ответ", "");
        String response = game.checkAnswer(ChatId, "неправильный ответ");
        Assert.assertEquals("Неверно! Попробуйте еще раз.", response);
    }


    /**
     *  Тест проверяет ответ без текущей головоломки
     */
    @Test
    public void testNoCurrentPuzzle() {
        bot.parseMessage("/playpuzzle", ChatId);
        game.currentPuzzle = null;
        String response = game.checkAnswer(ChatId, "");
        Assert.assertEquals("Нет текущей загадки.", response);
    }


}