package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для реализации Телеграмм-бота
 */
public class TelegramBot extends TelegramLongPollingBot {

    final private String BOT_TOKEN = System.getenv("tgbotToken");
    final private String BOT_NAME = "groobee";

    Storage storage;
    MessageHandling messageHandling;
    public TelegramBot() {
        storage = new Storage();
        messageHandling = new MessageHandling();
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }


    /**
     * Получение и Отправка сообщения в чат пользователю
     */
    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                //Извлекаем из объекта сообщение пользователя
                Message inMess = update.getMessage();
                //Достаем из inMess id чата пользователя
                String chatId = inMess.getChatId().toString();
                //Получаем текст сообщения пользователя, отправляем в написанный нами обработчик
                String response = messageHandling.parseMessage(inMess.getText());
                //Создаем объект класса SendMessage - наш будущий ответ пользователю
                SendMessage outMess = new SendMessage();
                //Добавляем в наше сообщение id чата, а также наш ответ
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


    /**
    * Метод для создания клавиатуры в боте
     */
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