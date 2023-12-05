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
    private String lastAddedGameTitle;
    private String lastAddedGameAuthor;
    private int lastAddedGameYear;
    private long lastAddedGameChatId;
    private boolean awaitingGameNumberForEdit;
    private int lastAddedGameNumber;
    private boolean awaitingEditTitle;
    private boolean awaitingEditAuthor;
    private boolean awaitingEditYear;
    private boolean awaitingAuthorForGamesByAuthor;
    private boolean awaitingYearForGamesByYear;
    private boolean awaitingGameNumberForRemoval;

    public boolean isAwaitingRating() {
        return awaitingRating;
    }

    /**
     * Конструктор класса MessageHandling. Инициализирует объекты Storage и PuzzleGame,
     * а также устанавливает начальное значение режима головоломки как false.
     */
    public MessageHandling() {
        awaitingGameNumberForRemoval = false;
        awaitingYearForGamesByYear = false;
        awaitingAuthorForGamesByAuthor = false;
        awaitingEditYear = false;
        awaitingEditAuthor = false;
        awaitingEditTitle = false;
        awaitingGameNumberForEdit = false;
        storage = new Storage();
        puzzleGame = new PuzzleGame();
        puzzleMode = false;
        awaitingRating = false;
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
        if (awaitingEditYear){
            response = handleEditYear(textMsg, chatId);
        }else if (awaitingEditAuthor) {
            response = handleEditAuthor(textMsg, chatId);
        }else if (awaitingEditTitle) {
            response = handleEditTitle(textMsg, chatId);
        } else if (awaitingGameNumberForEdit){
            response = handleEditNumber(textMsg, chatId);
        }else if (awaitingRating) {
            response = handleRating(textMsg);
        } else if (textMsg.startsWith("/addgame")) {
            response = handleAddGame(textMsg, chatId);
        } else if (puzzleMode) {
            response = handlePuzzleMode(textMsg, chatId);
        } else{
            response = handleDefaultMode(textMsg, chatId);
        }

        return response;
    }


    /**
     Обрабатывает действия пользователя в режиме головоломки.

     @param textMsg сообщение пользователя
     @param chatId  идентификатор чата
     @return ответ бота на действие пользователя
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
        Обрабатывает команду добавления игры в базу данных.

        @param textMsg текстовое сообщение, полученное от пользователя
        @param chatId идентификатор чата пользователя
        @return сообщение с ответом, указывающее на успешность или неудачу добавления игры
     */
    public String handleAddGame(String textMsg, long chatId) {
        String response;

        if (textMsg.length() > 9) {
            String[] parts = textMsg.substring(9).split("\n");
            if (parts.length == 3) {
                String title = parts[0].trim();
                String author = parts[1].trim();
                int year;

                try {
                    year = Integer.parseInt(parts[2].trim());

                    if (!storage.gameExists(title, author, year, chatId)) {
                        lastAddedGameTitle = title;
                        lastAddedGameAuthor = author;
                        lastAddedGameYear = year;
                        lastAddedGameChatId = chatId;
                        awaitingRating = true;

                        response = "Игра '" + title + "' издателя " + author + " (" + year + ") успешно добавлена!\nОцените игру от 1 до 5:";
                        return response;  // Возвращаем ответ, чтобы прервать дальнейшую обработку в текущем вызове
                    } else {
                        response = "Игра с указанным названием, издателем и годом выхода уже существует в базе данных.";
                    }
                } catch (NumberFormatException e) {
                    response = "Некорректный формат года выхода.";
                }
            } else {
                response = "Некорректный формат ввода. Используйте:\n/addgame Название_игры\nИздатель\nГод_выхода";
            }
        } else {
            response = "Чтобы добавить игру используйте:\n/addgame Название_игры\nИздатель\nГод_выхода";
        }

        return response;
    }


    /**
     Обрабатывает оценку пользователя по текстовому сообщению.

     @param textMsg текстовое сообщение с оценкой
     @return ответный текстовый ответ, казывающее на успешное или неудачное добавление оценки игре
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


    /**
     Обрабатывает команду изменения номера игры.

     @param textMsg текстовое сообщение с указанным пользователем номером игры
     @param chatId идентификатор чата с пользователем
     @return ответное сообщение в виде строки, уведомляющее пользователя о изменения названия игры или неккоректной форме записи запроса
     */
    public String handleEditNumber(String textMsg, long chatId) {
        String response;
        int gameNumber = Integer.parseInt(textMsg.trim());
        ArrayList<String> playedGames = storage.getPlayedGames(chatId);

        if (!awaitingGameNumberForEdit) {
            response = "Введите номер игры";
            awaitingGameNumberForEdit = true;
        } else {
            if (gameNumber >= 1 && gameNumber <= playedGames.size()) {
                lastAddedGameNumber = gameNumber;
                awaitingGameNumberForEdit = false;
                lastAddedGameTitle = textMsg.trim();
                response = "Введите новое название игры.";
                awaitingEditTitle = true;
            }
            else {
                response = "Неверный формат. Введите номер игры из списка /getplayed";
            }
        }

        return response;
    }


    /**
     * Обрабатывает команду добавления названия новой игры.
     *
     * @param textMsg текст сообщения с командой и данными о новой игре
     * @param chatId идентификатор чата с пользователем
     * @return ответное сообщение в виде строки, уведомляет пользователя о успешной или неудачном добавлении названия игры
     */
    public String handleEditTitle(String textMsg, long chatId) {
        String response;

        if (!awaitingEditTitle) {
            response = "Введите новое название игры";
            awaitingEditTitle = true;
        } else {
            if (textMsg.contains("\n") || (textMsg.contains("  "))){
                response = "Неверный формат. Введите название игры";
            }
            else {
                awaitingEditTitle = false;
                lastAddedGameTitle = textMsg.trim();
                response = "Введите нового издателя игры.";
                awaitingEditAuthor = true;
            }
        }

        return response;
    }


    /**
     * Обрабатывает команду добавления издателя к новой игре.
     *
     * @param textMsg текст сообщения с командой и данными о новой игре
     * @param chatId идентификатор чата с пользователем
     * @return ответное сообщение в виде строки, уведомляет пользователя о успешной или неудачном добавлении издателя игры
     */
    public String handleEditAuthor(String textMsg, long chatId) {
        String response;
        if (!awaitingEditAuthor) {
            response = "Введите нового издателя игры";
            awaitingEditAuthor = true;
        } else {
            if (textMsg.contains("\n") || (textMsg.contains("  "))){
                response = "Неверный формат. Введите издателя игры";
            }
            else {
                awaitingEditAuthor = false;
                lastAddedGameAuthor = textMsg.trim();
                lastAddedGameChatId = chatId;
                response = "Введите год новый выхода игры.";
                awaitingEditYear = true;
            }
        }

        return response;
    }


    /**
     * Обрабатывает команду изменения года к новой игре.
     *
     * @param textMsg текст сообщения с командой и данными о новой игре
     * @param chatId идентификатор чата с пользователем
     * @return ответное сообщение в виде строки, уведомляет пользователя о успешной или неудачном добавлении года издания игры,
     * так же проверяет игру в процессе добавления на уникальность
     */
    public String handleEditYear(String textMsg, long chatId) {
        int gameNumber;
        String newTitle;
        String newAuthor;
        int newYear;
        newTitle = lastAddedGameTitle;
        newAuthor = lastAddedGameAuthor;
        // Проверка формата
        if (!textMsg.matches("\\d{4}") || textMsg.matches(".*[a-zA-Z].*")) {
            return "Некорректный формат года. Пожалуйста, введите четыре цифры без букв.";
        }
        newYear = Integer.parseInt(textMsg.trim());
        if (storage.gameExists(newTitle, newAuthor, newYear, chatId)) {
            awaitingEditTitle = true;
            awaitingEditAuthor = false;
            awaitingEditYear = false;
            return "Игра с таким названием, автором и годом уже существует. Пожалуйста, введите название заново:";
        } else {
            gameNumber = lastAddedGameNumber;
            ArrayList<String> playedGames = storage.getAllValues(chatId);
            String[] oldGameParts = playedGames.get(gameNumber - 1).split("\n");
            String oldTitle = oldGameParts[0];
            String oldAuthor = oldGameParts[1];
            int oldYear = Integer.parseInt(oldGameParts[2]);

            // Заменяем книгу в базе данных
            storage.editPlayedGame(oldTitle, oldAuthor, oldYear, newTitle, newAuthor, newYear, chatId);

            awaitingEditYear = false;
            return "Игра '" + oldTitle + "' успешно заменена на игру '" + newTitle + "' от издателя " + newAuthor + " (" + newYear + ") в списке пройденных!";
        }
    }




    /**
     * Обработчик сообщений в режиме по умолчанию.
     *
     * @param textMsg Входящий текстовый запрос от пользователя.
     * @param chatId   идентификатор чата с пользователем
     * @return ответ на запрос пользователя командами
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
            String numberRequest = "Введите номер из списка:";
            response = numberRequest;
            awaitingGameNumberForEdit = true; // Флаг ожидания имени автора для команды /getbyauthor


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