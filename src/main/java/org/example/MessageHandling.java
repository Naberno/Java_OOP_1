package org.example;
import java.io.IOException;
import java.util.ArrayList;

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


    /**
     * Конструктор класса MessageHandling. Инициализирует объекты Storage и PuzzleGame,
     * а также устанавливает начальное значение режима головоломки как false.
     */
    public MessageHandling() {
        storage = new Storage();
        puzzleGame = new PuzzleGame();
        puzzleMode = false;
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

        if (puzzleMode) {
            response = handlePuzzleMode(textMsg, chatId);
        }else{
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
        } else if (textMsg.equals("/statistic")) {
            response = puzzleGame.getStatistics(chatId);
        } else if (textMsg.equals("/restart")) {
            response = puzzleGame.restart(chatId);
        } else if ((textMsg.equalsIgnoreCase("какой ответ"))||(textMsg.equals("/getanswer"))) {
            response = puzzleGame.getAnswerAndNextPuzzle(chatId);
            /* } else if ((textMsg.equalsIgnoreCase("покажи нерешённые загадки"))||(textMsg.equals("/showriddles"))) {
            response = puzzleGame.getUnsolvedPuzzles(chatId); */
        } else if (textMsg.equals("/stoppuzzle")) {
            response = "Режим головоломки завершен.";
            puzzleMode = false; // Выход из режима головоломки
        } else {
            response = puzzleGame.checkAnswer(chatId, textMsg);
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
        if (textMsg.equals("/start") || textMsg.equals("/help")) {
            response = "Приветствую, это литературный бот. Жми /get, чтобы получить случайную цитату. Жми /genre, чтобы перейти в раздел жанров книг.";
        } else if (textMsg.equals("/get") || textMsg.equals("Просвети")) {
            response = storage.getRandQuote();
        } else if (textMsg.equals("/genre")) {
            response = "Здравствуйте, добро пожаловать в бот рекомендации книг! Нажмите /chat и выберите жанр";
        } else if (textMsg.equals("Научная фантастика")) {
            response = "Прочитайте 'Автостопом по галактике', 'Время жить и время умирать' или 'Война миров'";
        } else if (textMsg.equals("Фэнтези")) {
            response = "Прочитайте 'Хоббит', 'Игра престолов' или 'Гарри Поттер'";
        } else if (textMsg.equals("Романтика")) {
            response = "Прочитайте 'Великий Гетсби', 'Триумфальная арка' или 'Поющие в терновнике'";
        } else if (textMsg.equals("Детектив")) {
            response = "Прочитайте 'Убийство в восточном экспрессе', 'Снеговик' или 'Собака Баскервилей'";


        } else if (textMsg.startsWith("/addbook")) {
            // Получаем название книги, автора и год прочтения, введенные пользователем
            String[] parts = textMsg.substring(9).split("\n");
            if (parts.length == 3) {
                String title = parts[0].trim();
                String author = parts[1].trim();
                int year;
                try {
                    year = Integer.parseInt(parts[2].trim());

                    // Проверяем существование книги в базе данных
                    if (!storage.bookExists(title, author, year, chatId)) {
                        // Если книги с такими данными нет, добавляем книгу в базу данных
                        storage.addReadBook(title, author, year, chatId);
                        response = "Книга '" + title + "' от автора " + author + " (год: " + year + ") успешно добавлена в список прочитанных!";
                    } else {
                        response = "Книга с указанным названием, автором и годом прочтения уже существует в базе данных.";
                    }
                } catch (NumberFormatException e) {
                    response = "Некорректный формат года прочтения.";
                }
            } else {
                response = "Некорректный формат ввода. Используйте /addbook Название книги\nАвтор\nГод прочтения";
            }


    } else if (textMsg.startsWith("/replacebook")) {
            // Получаем старые и новые данные книги, введенные пользователем
            String[] parts = textMsg.substring(13).split("\n");
            if (parts.length == 6 && parts[3].startsWith("to ")) {
                String oldTitle = parts[0].trim();
                String oldAuthor = parts[1].trim();
                int oldYear;
                // Получаем newTitle с третьего символа четвёртой строки
                String newTitle = parts[3].substring(3).trim();
                String newAuthor = parts[4].trim();
                int newYear;
                try {
                    oldYear = Integer.parseInt(parts[2].trim());
                    newYear = Integer.parseInt(parts[5].trim());

                    // Проверяем существование старой книги в базе данных
                    if (storage.bookExists(oldTitle, oldAuthor, oldYear, chatId)) {
                        // Заменяем книгу в базе данных
                        storage.replaceReadBook(oldTitle, oldAuthor, oldYear, newTitle, newAuthor, newYear, chatId);
                        response = "Книга '" + oldTitle + "' от автора " + oldAuthor + " (год: " + oldYear + ") успешно заменена на книгу '" + newTitle + "' от автора " + newAuthor + " (год: " + newYear + ") в списке прочитанных!";
                    } else {
                        response = "Книга с указанными старым названием, автором и годом прочтения не найдена в базе данных.";
                    }
                } catch (NumberFormatException e) {
                    response = "Некорректный формат года прочтения.";
                }
            } else {
                response = "Некорректный формат ввода. Используйте /replace Старое_название\nСтарый_автор\nСтарый_год\nto Новое_название\nНовый_автор\nНовый_год";
            }



        } else if (textMsg.equals("/clearread")) {
            // Очищаем список прочитанных книг
            storage.clearReadBooks(chatId);
            response = "Список прочитанных книг очищен!";


        } else if (textMsg.equals("/getread")) {
            // Получаем список прочитанных книг
            ArrayList<String> readBooks = storage.getReadBooks(chatId);

            if (readBooks.isEmpty()) {
                response = "Список прочитанных книг пуст.";
            } else {
                response = "Прочитанные книги:\n" + String.join("\n", readBooks);
            }


    } else if (textMsg.startsWith("/getbyauthor")) {
            String author = textMsg.substring(13); // Получаем имя автора из сообщения
            ArrayList<String> booksByAuthor = storage.getBooksByAuthor(author, chatId);
            if (!booksByAuthor.isEmpty()) {
                response = "Книги автора " + author + ":\n" + String.join("\n", booksByAuthor);
            } else {
                response = "Нет прочитанных книг этого автора.";
            }


        } else if (textMsg.startsWith("/getbyyear")) {
            int year = Integer.parseInt(textMsg.substring(11)); // Получаем год из сообщения
            ArrayList<String> getBooksByYear = storage.getBooksByYear(year, chatId);
            if (!getBooksByYear.isEmpty()) {
                response = "Книги" + " " + year + " " + "года" + ":\n" + String.join("\n", getBooksByYear);
            } else {
                response = "Нет прочитанных книг в этом году.";
            }


        } else if (textMsg.startsWith("/removebook")) {
            // Получаем название книги, автора и год прочтения, введенные пользователем
            String[] parts = textMsg.substring(12).split("\n");
            if (parts.length == 3) {
                String title = parts[0].trim();
                String author = parts[1].trim();
                int year;
                try {
                    year = Integer.parseInt(parts[2].trim());

                    // Проверяем существование книги в базе данных
                    if (storage.bookExists(title, author, year, chatId)) {
                        // Удаляем книгу из базы данных
                        storage.RemoveReadBook(title, author, year, chatId);
                        response = "Книга '" + title + "' от автора " + author + " (год: " + year + ") успешно удалена из списка прочитанных!";
                    } else {
                        response = "Книга с указанным названием, автором и годом прочтения не найдена в базе данных.";
                    }
                } catch (NumberFormatException e) {
                    response = "Некорректный формат года прочтения.";
                }
            } else {
                response = "Некорректный формат ввода. Используйте /removebook Название книги\nавтор\nгод прочтения";
            }


    } else if (textMsg.equals("/playpuzzle")) {
            // Вход в режим головоломки
            puzzleMode = true;
            response = puzzleGame.startPuzzle(chatId);


    }else {
        response = textMsg;
    }
        return response;
    }


}
