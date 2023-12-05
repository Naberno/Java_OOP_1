package org.example;

import java.util.ArrayList;
import java.io.IOException;

/**
 * Интерфейс для обработки сообщений пользователя
 */
interface MessageProcessor {

    /**
     * Обрабатывает текстовое сообщение пользователя и возвращает ответ.
     *
     * @param textMsg Текстовое сообщение пользователя.
     * @param chatId  Идентификатор чата пользователя.
     * @return Ответ на сообщение пользователя.
     */
    String parseMessage (String textMsg, long chatId);
}


/**
 * Класс для обработки сообщений пользователя
 */
public class MessageHandling implements MessageProcessor {

    private Storage storage;
    private PuzzleGame puzzleGame;
    private boolean puzzleMode;
    private boolean awaitingRating;
    private boolean awaitingAuthor;
    private boolean awaitingAuthorForGamesByAuthor;
    private boolean awaitingYearForGamesByYear;
    private boolean awaitingGameNumberForRemoval;
    private String lastAddedGameTitle;
    private String lastAddedGameAuthor;
    private int lastAddedGameYear;
    private long lastAddedGameChatId;

    private boolean awaitingTitle;
    private boolean awaitingYear;


    public boolean isAwaitingRating() {
        return awaitingRating;
    }


    /**
     * Конструктор класса MessageHandling. Инициализирует объекты Storage и PuzzleGame,
     * а также устанавливает начальное значение режима головоломки как false.
     */
    public MessageHandling() {
        storage = new Storage();
        puzzleGame = new PuzzleGame();
        puzzleMode = false;
        awaitingRating = false;
        awaitingAuthor = false;
        awaitingTitle = false;
        awaitingYear = false;
        awaitingAuthorForGamesByAuthor = false;
        awaitingYearForGamesByYear = false;
        awaitingGameNumberForRemoval = false;
    }


    /**
     * Метод для обработки входящего текстового сообщения от пользователя.
     *
     * @param textMsg Входящий текстовый запрос от пользователя.
     * @param chatId  Идентификатор чата пользователя.
     * @return Ответ на запрос пользователя в виде строки.
     */

    public String parseMessage(String textMsg, long chatId) {
        String response;

        if (awaitingYearForGamesByYear){
            response = handleYearForGamesByYear(textMsg, chatId);
        }else if (awaitingAuthorForGamesByAuthor) {
            response = handleAuthorForGamesByAuthor(textMsg, chatId);
        }else if (awaitingTitle) {
            response = handleAddTitle(textMsg, chatId);
        } else if (awaitingAuthor) {
            response = handleAddAuthor(textMsg, chatId);
        } else if (awaitingYear) {
            response = handleAddYear(textMsg, chatId);
        } else if (awaitingRating) {
            response = handleRating(textMsg);
        } else if (awaitingGameNumberForRemoval) {
            response = handleRemoveGame(textMsg, chatId);
        } else if (textMsg.startsWith("/addgame")) {
            response = handleAddTitle(textMsg, chatId);
        } else if (puzzleMode) {
            response = handlePuzzleMode(textMsg, chatId);
        } else {
            response = handleDefaultMode(textMsg, chatId);
        }

        return response;
    }


    /**
     * Обработчик сообщений в режиме головоломки.
     *
     * @param textMsg Входящий текстовый запрос от пользователя.
     * @param chatId  Идентификатор чата пользователя.
     * @return Ответ на запрос пользователя в режиме головоломки.
     */
    private String handlePuzzleMode(String textMsg, long chatId) {
        String response;
        if ((textMsg.equalsIgnoreCase("дай подсказку"))||(textMsg.equals("/gethint"))) {
            response = puzzleGame.getHint();
        } else if ((textMsg.equalsIgnoreCase("следующая загадка"))||(textMsg.equals("/anotheriddle"))) {
            response = puzzleGame.getNextPuzzle(chatId);
        } else if (textMsg.equals("/restart")) {
            response = puzzleGame.restart(chatId);
        } else if ((textMsg.equalsIgnoreCase("какой ответ"))||(textMsg.equals("/getanswer"))) {
            response = puzzleGame.getAnswerAndNextPuzzle(chatId);
        } else if (textMsg.equals("/stoppuzzle")) {
            response = "Режим головоломки завершен.\n" + puzzleGame.getStatistics(chatId);

            puzzleMode = false; // Выход из режима головоломки
        }else if (textMsg.startsWith("/checkanswer")) {
            // Извлеките ответ пользователя из сообщения
            String userAnswer = textMsg.substring("/checkanswer".length()).trim();
            // Передайте ответ пользователя в PuzzleGame для проверки
            response = puzzleGame.checkAnswer(chatId, userAnswer);
        } else {
            response = puzzleGame.checkAnswer(chatId, textMsg);
        }
        return response;
    }


    /**
     * Обрабатывает команду добавления названия новой игры.
     *
     * @param textMsg текст сообщения с командой и данными о новой игре
     * @param chatId id беседы, в которой пришло сообщение
     * @return стринговый ответ для пользователя
     */
    public String handleAddTitle(String textMsg, long chatId) {
        String response;

        if (!awaitingTitle) {
            response = "Введите название игры";
            awaitingTitle = true;
        } else {
            if (textMsg.contains("\n") || (textMsg.contains("  "))){
                response = "Неверный формат. Введите название игры";
            }
            else {
                awaitingTitle = false;
                lastAddedGameTitle = textMsg.trim();
                response = "Введите издателя игры.";
                awaitingAuthor = true;
            }
        }

        return response;
    }


    /**
     * Обрабатывает команду добавления издателя к новой игре.
     *
     * @param textMsg текст сообщения с командой и данными о новой игре
     * @param chatId id беседы, в которой пришло сообщение
     * @return стринговый ответ для пользователя
     */
    public String handleAddAuthor(String textMsg, long chatId) {
        String response;
        if (!awaitingAuthor) {
            response = "Введите издателя игры";
            awaitingAuthor = true;
        } else {
            if (textMsg.contains("\n") || (textMsg.contains("  "))){
                response = "Неверный формат. Введите издателя игры";
            }
            else {
                awaitingAuthor = false;
                lastAddedGameAuthor = textMsg.trim();
                lastAddedGameChatId = chatId;
                response = "Введите год выхода игры.";
                awaitingYear = true;
            }
        }

        return response;
    }


    /**
     * Обрабатывает команду добавления года к новой игре.
     *
     * @param textMsg текст сообщения с командой и данными о новой игре
     * @param chatId id беседы, в которой пришло сообщение
     * @return стринговый ответ для пользователя
     */
    public String handleAddYear(String textMsg, long chatId) {
        String title = lastAddedGameTitle;
        String author = lastAddedGameAuthor;
        // Проверка формата
        if (!textMsg.matches("\\d{4}") || textMsg.matches(".*[a-zA-Z].*")) {
            return "Некорректный формат года. Пожалуйста, введите четыре цифры без букв.";
        }
        int year = Integer.parseInt(textMsg.trim());
        if (storage.gameExists(title, author, year, chatId)) {
            awaitingTitle = true;
            awaitingAuthor = false;
            awaitingYear = false;
            awaitingRating = false;
            return "Игра с таким названием, автором и годом уже существует. Пожалуйста, введите название заново:";
        } else {
            lastAddedGameYear = year;
            lastAddedGameChatId = chatId;
            awaitingYear = false;
            awaitingRating = true;
            return "Игра '" + title + "' издателя " + author + " (" + year + ") успешно добавлена!\nОцените игру от 1 до 5:";
        }
    }


    /**
     * Обрабатывает ввод рейтинга для недавно добавленной игры.
     *
     * @param textMsg текст сообщения с введённым рейтингом
     * @return стринговый ответ для пользователя
     */
    public String handleRating(String textMsg) {
        String response;

        try {
            int rating = Integer.parseInt(textMsg.trim());

            // Добавьте дополнительную проверку на допустимые значения рейтинга
            if (rating >= 1 && rating <= 5) {
                // Обновьте базу данных с рейтингом
                storage.addPlayedGame(lastAddedGameTitle, lastAddedGameAuthor, lastAddedGameYear, rating, lastAddedGameChatId);
                awaitingRating = false;
                response = "Отзыв " + rating + "⭐ оставлен.";
            } else {
                response = "Пожалуйста, введите оценку от 1 до 5.";
            }
        } catch (NumberFormatException e) {
            response = "Некорректный формат оценки. Пожалуйста, введите числовое значение от 1 до 5.";
        }

        return response;
    }


    public String handleAuthorForGamesByAuthor(String textMsg, long chatId) {
        String response;

        // Проверяем, что ожидается ввод имени автора
        if (awaitingAuthorForGamesByAuthor) {
            // Проверяем, что введенное имя автора не содержит цифр, двух пробелов и символа перевода строки
            if (!textMsg.contains("  ") && !textMsg.contains("\n")) {
                String author = textMsg.trim();
                ArrayList<String> gamesByAuthor = storage.getGamesByAuthor(author, chatId);
                if (!gamesByAuthor.isEmpty()) {
                    response = "Игры издателя '" + author + "':\n" + String.join("\n", gamesByAuthor);
                } else {
                    response = "Нет пройденных игр этого издателя.";
                }
            } else {
                response = "Некорректный формат. Пожалуйста, введите корректно издателя игры.";
            }

            // Сбрасываем флаг ожидания имени автора
            awaitingAuthorForGamesByAuthor = false;
        } else {
            response = "Неверное состояние для ввода издателя.";
        }

        return response;
    }


    public String handleYearForGamesByYear(String textMsg, long chatId) {
        String response;

        // Проверяем, что ожидается ввод года
        if (awaitingYearForGamesByYear) {
            try {
                int year = Integer.parseInt(textMsg.trim());

                // Проверяем, что введенный год не содержит букв и не более 4 цифр
                if (textMsg.matches("\\d{1,4}")) {
                    ArrayList<String> gamesByYear = storage.getGamesByYear(year, chatId);
                    awaitingYearForGamesByYear = false;
                    if (!gamesByYear.isEmpty()) {
                        response = "Игры " + year + " года:\n" + String.join("\n", gamesByYear);
                    } else {
                        response = "Нет пройденных игр в этого года.";
                    }
                } else {
                    response = "Некорректный формат года. Пожалуйста, введите год в виде числа не более 4 цифр.";
                }
            } catch (NumberFormatException e) {
                response = "Некорректный формат года. Пожалуйста, введите год в виде числа не более 4 цифр.";
            }
        } else {
            response = "Неверное состояние для ввода года.";
        }

        return response;
    }


    // Метод обработки сообщения для удаления игры
    public String handleRemoveGame(String textMsg, long chatId) {
        String response;

        if (awaitingGameNumberForRemoval) {
            try {
                int gameNumber = Integer.parseInt(textMsg.trim());
                ArrayList<String> playedGames = storage.getPlayedGames(chatId);

                if (gameNumber >= 1 && gameNumber <= playedGames.size()) {
                    String removedGame = playedGames.remove(gameNumber - 1);
                    storage.updatePlayedGames(chatId, playedGames);
                    awaitingGameNumberForRemoval = false;
                    response = "Игра '" + removedGame + "' успешно удалена из списка пройденных!";
                } else {
                    response = "Указанный номер игры не существует.";
                }
            } catch (NumberFormatException e) {
                response = "Некорректный формат номера игры. Пожалуйста, введите число.";
            }

        } else {
            // Запросим номер игры для удаления
            response = "Введите номер игры, которую вы хотите удалить из списка пройденных:";
            awaitingGameNumberForRemoval = true;
        }

        return response;
    }




    /**
     * Обработчик сообщений в режиме по умолчанию.
     *
     * @param textMsg Входящий текстовый запрос от пользователя.
     * @param chatId  Идентификатор чата пользователя.
     * @return Ответ на запрос пользователя в режиме по умолчанию.
     */
    private String handleDefaultMode(String textMsg, long chatId) {
        String response;
        // Сравниваем текст пользователя с командами, на основе этого формируем ответ
        if (textMsg.equals("/start") || textMsg.equals("Привет")) {
            response = "Привет, я игровой бот. Жми /help, чтобы узнать что я могу.";
        } else if (textMsg.equals("/help") || textMsg.equals("Помощь")){
            response = """
                    Привет, я умею:
                    /addgame - Добавить игру, которую ты уже прошел
                    /getplayed - Список пройденных игр
                    /getbyrating - Рейтинг игр всех пользователей
                    /getbyauthor - Получить список игр по конкретному автору
                    /getbyyear - Получить список игр по конкретному году
                    /removegame - Удалить игру из списка
                    /editgame - Изменяет выбранную игру из списка на написанную
                    /playpuzzle - Быстрый квиз по разным темам для развлечения
                    """
            ;
        }else if (textMsg.equals("/get") || textMsg.equals("Просвети")) {
            response = storage.getRandQuote();

        } else if (textMsg.startsWith("/editgame")) {
            if (textMsg.length() > 10) {
                // Получаем уникальный номер игры и новые данные игры, введенные пользователем
                String[] parts = textMsg.substring(10).split("\n");
                if (parts.length == 4) {
                    int gameNumber;
                    String newTitle;
                    String newAuthor;
                    int newYear;
                    try {
                        // Получаем уникальный номер игры
                        gameNumber = Integer.parseInt(parts[0].trim());
                        // Получаем новые данные игры
                        newTitle = parts[1].trim();
                        newAuthor = parts[2].trim();
                        newYear = Integer.parseInt(parts[3].trim());

                        // Проверяем существование игры с указанным уникальным номером в списке пройденных игр
                        ArrayList<String> playedGames = storage.getAllValues(chatId);
                        if (gameNumber >= 1 && gameNumber <= playedGames.size()) {
                            // Получаем старые данные игры
                            String[] oldGameParts = playedGames.get(gameNumber - 1).split("\n");
                            String oldTitle = oldGameParts[0];
                            String oldAuthor = oldGameParts[1];
                            int oldYear = Integer.parseInt(oldGameParts[2]);

                            // Заменяем книгу в базе данных
                            storage.editPlayedGame(oldTitle, oldAuthor, oldYear, newTitle, newAuthor, newYear, chatId);
                            response = "Игра '" + oldTitle + "' успешно заменена на игру '" + newTitle + "' от издателя " + newAuthor + " (" + newYear + ") в списке пройденных!";
                        } else {
                            response = "Указанный уникальный номер игры не существует в списке пройденных игр.";
                        }
                    } catch (NumberFormatException e) {
                        response = "Некорректный формат уникального номера игры или года выхода.";
                    }
                } else {
                    response = "Некорректный формат ввода. Используйте:\n/editgame Номер_в_списке\nНовое_название\nНовый_издатель\nНовый_год";
                }
            } else {
                response = "Чтоыбы редактировать игру, используйте:\n/editgame Номер_в_списке\nНовое_название\nНовый_издатель\nНовый_год";
            }


        } else if (textMsg.equals("/clearplayed")) {
            // Очищаем список пройденных игр
            storage.clearPlayedGames(chatId);
            response = "Список пройденных игр очищен!";


        } else if (textMsg.equals("/getplayed")) {
            // Получаем список пройденных игр с уникальными номерами
            ArrayList<String> playedGames = storage.getPlayedGames(chatId);
            if (playedGames.isEmpty()) {
                response = "Список пройденных игр пуст.";
            } else {
                StringBuilder responseBuilder = new StringBuilder("Пройденные игры:\n");
                for (int i = 0; i < playedGames.size(); i++) {
                    responseBuilder.append(i + 1).append(". ").append(playedGames.get(i)).append("\n");
                }
                response = responseBuilder.toString();
            }


        } else if (textMsg.startsWith("/getbyauthor")) {
            String authorRequest = "Введите имя автора:";
            response = authorRequest;
            awaitingAuthorForGamesByAuthor = true; // Флаг ожидания имени автора для команды /getbyauthor


        } else if (textMsg.startsWith("/getbyyear")) {
            String yearRequest = "Введите год (не более 4 цифр):";
            response = yearRequest;
            awaitingYearForGamesByYear = true; // Флаг ожидания года для команды /getbyyear


        } else if (textMsg.startsWith("/getbyrating")) {
            // Обработка команды /getbyrating
            ArrayList<String> gamesByRating = storage.getGamesByAverageRating(chatId);

            if (!gamesByRating.isEmpty()) {
                response = "Список игр по среднему рейтингу:\n";
                for (String game : gamesByRating) {
                    response += game + "\n";
                }
            } else {
                response = "Нет данных о среднем рейтинге игр.";
            }


        } else if (textMsg.startsWith("/removegame")) {
            String removeRequest = "Введите номер игры, которую нужно удалить:";
            response = removeRequest;
            awaitingGameNumberForRemoval = true; // Флаг ожидания года для команды /getbyyear


        } else if (textMsg.equals("/playpuzzle")) {
            // Вход в режим головоломки
            puzzleMode = true;
            response = puzzleGame.startPuzzle(chatId);
        }

        else {
            response = textMsg;
        }
        return response;
    }


}


