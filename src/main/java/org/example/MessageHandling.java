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
    private Gpt3Chat chatGpt;
    private boolean puzzleMode;
    private boolean chatMode;
    private boolean awaitingRating;
    private String lastAddedGameTitle;
    private String lastAddedGameAuthor;
    private int lastAddedGameYear;
    private long lastAddedGameChatId;


    /**
     * Конструктор класса MessageHandling. Инициализирует объекты Storage и PuzzleGame,
     * а также устанавливает начальное значение режима головоломки как false.
     */
    public MessageHandling() {
        storage = new Storage();
        puzzleGame = new PuzzleGame();
        chatGpt = new Gpt3Chat();
        puzzleMode = false;
        chatMode = false;
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

        if (awaitingRating) {
            response = handleRating(textMsg);
        } else if (textMsg.startsWith("/addgame")) {
            response = handleAddGame(textMsg, chatId);
        } else if (puzzleMode) {
            response = handlePuzzleMode(textMsg, chatId);
        } else if (chatMode) {
            response = handleChatMode(textMsg, chatId);
        } else{
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


    /**Подлкючает ChatGpt API
     *
     * @param textMsg Входящий текстовый запрос от пользователя.
     * @param chatId  Идентификатор чата пользователя.
     * @return Ответ на запрос пользователя в режиме ассистента
     */
    private String handleChatMode(String textMsg, long chatId) {
        String response = "";
        if (textMsg.equals("/stopchat")) {
            response = "Режим чата завершен";
            chatMode = false; // Выход из режима чата
        } else {
            try {
                response = chatGpt.generateResponse(textMsg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response;
    }


    /**
     * Обрабатывает команду добавления новой игры.
     *
     * @param textMsg текст сообщения с командой и данными о новой игре
     * @param chatId id беседы, в которой пришло сообщение
     * @return стринговый ответ для пользователя
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
                    /chat - Включать чат-бота для помощи в разных вопросах
                    /addgame - Добавить игру, которую ты уже прошел
                    /getplayed - Список пройденных игр
                    /removegame - Удалить игру из списка
                    /editgame - Изменяет выбранную игру из списка на написанную
                    /getbyauthor - Получить список игр по конкретному автору
                    /getbyyear - Получить список игр по конкретному году
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
            if (textMsg.length() > 12) {
                String author = textMsg.substring(13); // Получаем имя автора из сообщения
                ArrayList<String> gamesByAuthor = storage.getGamesByAuthor(author, chatId);
                if (!gamesByAuthor.isEmpty()) {
                    response = "Игры издателя " + author + ":\n" + String.join("\n", gamesByAuthor);
                } else {
                    response = "Нет пройденных игр этого издателя.";
                }
            } else {
                response = "Введите параметр после /getbyauthor";
            }



        } else if (textMsg.startsWith("/getbyyear")) {
            if (textMsg.length() > 10) {
                int year = Integer.parseInt(textMsg.substring(11)); // Получаем год из сообщения
                ArrayList<String> getGamesByYear = storage.getGamesByYear(year, chatId);
                if (!getGamesByYear.isEmpty()) {
                    response = "Игры" + " " + year + " " + "года" + ":\n" + String.join("\n", getGamesByYear);
                } else {
                    response = "Нет пройденных игр в этого года.";
                }
            } else {
                response = "Введите параметр после /getbyyear";
            }


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
            if (textMsg.length() > 11) {
                String message = textMsg.substring(12);
                try {
                    int gameNumber = Integer.parseInt(message);
                    ArrayList<String> playedGames = storage.getPlayedGames(chatId);
                    if (gameNumber >= 1 && gameNumber <= playedGames.size()) {
                        String removedGame = playedGames.remove(gameNumber - 1); // Удаляем книгу и получаем ее данные
                        // Здесь можно использовать removedGame для получения информации об удаленной книге
                        storage.updatePlayedGames(chatId, playedGames); // Обновляем список без удаленной игры
                        response = "Игра " + removedGame + " успешно удалена из списка пройденных!";
                    } else {
                        response = "Указанный номер игры не существует.";
                    }
                } catch (NumberFormatException e) {
                    response = "Некорректный формат номера игры";
                }
            } else {response = "Чтобы удалить игру введите\n/removegame Номер_в_списке";}

        } else if (textMsg.equals("/playpuzzle")) {
            // Вход в режим головоломки
            puzzleMode = true;
            response = puzzleGame.startPuzzle(chatId);

            //Вход в режим чата с ассистентом
        } else if (textMsg.equals("/chat")){
            chatMode = true;
            response = "Режим чата включен";
        }

        else {
            response = textMsg;
        }
        return response;
    }


}


