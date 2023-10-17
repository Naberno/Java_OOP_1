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
    public String parseMessage(String textMsg) {
        String response;
        //Сравниваем текст пользователя с нашими командами, на основе этого формируем ответ
        if (textMsg.equals("/start") || textMsg.equals("/help"))
            response = "Приветствую, это литературный бот. Жми /get, чтобы получить случайную цитату. Жми /genre, чтобы перейти в раздел жанры книг";
        else if (textMsg.equals("/get") || textMsg.equals("Просвети"))
            response = storage.getRandQuote();
        else if (textMsg.equals("/genre"))
            response = "Здравствуйте, добро пожаловать в бот рекомендации книг! Выберите жанр:";
        else if (textMsg.equals("Научная фантастика"))
            response = "Прочитайте 'Автостопом по галактике', 'Время жить и время умирать' или 'Война миров'";
        else if (textMsg.equals("Фэнтези"))
            response = "Прочитайте 'Хоббит', 'Игра престолов' или 'Гарри Поттер'";
        else if (textMsg.equals("Романтика"))
            response = "Прочитайте 'Великий Гетсби', 'Триумфальная арка' или 'Поющие в терновнике'";
        else if (textMsg.equals("Детектив"))
            response = "Прочитайте 'Убийство в восточном экспрессе', 'Снеговик' или 'Собака Баскервилей'";
        else {
            response = textMsg;
        }
        return response;
    }
}
