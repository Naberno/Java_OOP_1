package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {
    final private String BOT_TOKEN = "***";
    final private String BOT_NAME = "groobee";
    static Storage storage;

    Bot() {
        storage = new Storage();
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                //Извлекаем из объекта сообщение пользователя
                Message inMess = update.getMessage();
                //Достаем из inMess id чата пользователя
                String chatId = inMess.getChatId().toString();
                //Получаем текст сообщения пользователя, отправляем в написанный нами обработчик
                String response = parseMessage(inMess.getText());
                //Создаем объект класса SendMessage - наш будущий ответ пользователю
                SendMessage outMess = new SendMessage();

                //Добавляем в наше сообщение id чата а также наш ответ
                outMess.setChatId(chatId);
                outMess.setText(response);
                outMess.setReplyMarkup(createKeyboard());

                //Отправка в чат
                execute(outMess);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static String parseMessage(String textMsg) {
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
        else
             response = textMsg;

        return response;
    }

    public ReplyKeyboardMarkup createKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Создание ряда клавиш
        KeyboardRow row1 = new KeyboardRow();
        row1.add("Детектив");
        row1.add("Романтика");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Фэнтези");
        row2.add("Научная фантастика");
        keyboard.add(row1);
        keyboard.add(row2);
        // Установка клавиатуры
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }
}
