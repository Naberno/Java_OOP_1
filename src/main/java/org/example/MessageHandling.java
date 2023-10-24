package org.example;

import java.util.ArrayList;

/**
 * Класс для обработки сообщений пользователя
 */
public class MessageHandling {
    Storage storage;
    public MessageHandling() {
        storage = new Storage();
    }

    /**
     * Метод для обработки сообщений пользователя
     */
    public String parseMessage(String textMsg, long chatId) {
        String response;
        // Сравниваем текст пользователя с командами, на основе этого формируем ответ
        if (textMsg.equals("/start") || textMsg.equals("/help")) {
            response = "Приветствую, это литературный бот. Жми /get, чтобы получить случайную цитату. Жми /genre, чтобы перейти в раздел жанров книг.";
        }
        else if (textMsg.equals("/get") || textMsg.equals("Просвети")) {
            response = storage.getRandQuote();
        }
        else if (textMsg.equals("/genre")) {
            response = "Здравствуйте, добро пожаловать в бот рекомендации книг! Выберите жанр:";
        }
        else if (textMsg.equals("Научная фантастика")) {
            response = "Прочитайте 'Автостопом по галактике', 'Время жить и время умирать' или 'Война миров'";
        }
        else if (textMsg.equals("Фэнтези")) {
            response = "Прочитайте 'Хоббит', 'Игра престолов' или 'Гарри Поттер'";
        }
        else if (textMsg.equals("Романтика")) {
            response = "Прочитайте 'Великий Гетсби', 'Триумфальная арка' или 'Поющие в терновнике'";
        }
        else if (textMsg.equals("Детектив")) {
            response = "Прочитайте 'Убийство в восточном экспрессе', 'Снеговик' или 'Собака Баскервилей'";

        }
        else if (textMsg.startsWith("/addbook")) {
            // Получаем название книги, автора и год прочтения, введенные пользователем
            String[] parts = textMsg.substring(9).split("\n");
            if (parts.length == 3) {
                String title = parts[0].trim();
                String author = parts[1].trim();
                int year;
                try {
                    year = Integer.parseInt(parts[2].trim());
                    // Добавляем книгу в базу данных
                    storage.addReadBook(title, author, year, chatId);
                    response = "Книга '" + title + "' от автора " + author + " (год: " + year + ") успешно добавлена в список прочитанных!";
                }
                catch (NumberFormatException e) {
                    response = "Некорректный формат года прочтения.";
                }
            }
            else {
                response = "Некорректный формат ввода. Используйте /addbook Название книги\nАвтор\nГод прочтения";
            }

        }

        else if (textMsg.equals("/clearread")) {
        // Очищаем список прочитанных книг
        storage.clearReadBooks(chatId);
        response = "Список прочитанных книг очищен!";

        }

        else if (textMsg.equals("/getread")) {
            // Получаем список прочитанных книг
            ArrayList<String> readBooks = storage.getReadBooks(chatId);
            response = "Прочитанные книги:\n" + String.join("\n", readBooks);

        }

        else if (textMsg.startsWith("/getbyauthor")) {
                String author = textMsg.substring(13); // Получаем имя автора из сообщения
                ArrayList<String> booksByAuthor = storage.getBooksByAuthor(author, chatId);
                if (!booksByAuthor.isEmpty()) {
                    response = "Книги автора " + author + ":\n" + String.join("\n", booksByAuthor);
                }
                else {
                    response = "Нет прочитанных книг этого автора.";
                }
        }

        else if (textMsg.startsWith("/getbyyear")) {
            int year = Integer.parseInt(textMsg.substring(11)); // Получаем год из сообщения
            ArrayList<String> getBooksByYear = storage.getBooksByYear(year, chatId);
            if (!getBooksByYear.isEmpty()) {
                response = "Книги" + " " + year + " " + "года" + ":\n" + String.join("\n", getBooksByYear);
            }
            else {
                response = "Нет прочитанных книг этого автора.";
            }
        }

        else {
            response = textMsg;
        }

        return response;
    }
}
