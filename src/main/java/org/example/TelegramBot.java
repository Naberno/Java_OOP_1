package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;




import java.util.ArrayList;
import java.util.List;


/**
 * Интерфейс для Телеграмм-бота.
 */
interface TelegramBotInterface {


    /**
     * Создание клавиатуры в боте.
     *
     * @return Объект ReplyKeyboardMarkup с настроенной клавиатурой.
     */
    InlineKeyboardMarkup createKeyboard();
}


/**
 * Класс для реализации Телеграмм-бота
 */
public class TelegramBot extends TelegramLongPollingBot implements TelegramBotInterface {

    /**
     * Токен для Telegram-бота.
     * Получено из переменной среды "tgbotToken".
     */
    final private String BOT_TOKEN = System.getenv("tgbotToken");

    /**
     * Имя Telegram-бота.
     * Эта переменная используется для хранения имени бота.
     */
    final private String BOT_NAME = "Nabo";

    /**
     * Экземпляр класса MessageHandling.
     * Эта переменная используется для хранения экземпляра обработки сообщения.
     */
    private MessageHandling messageHandling;
    /**
     * Конструктор класса TelegramBot, который инициализирует объекты Storage и MessageHandling.
     * Storage используется для управления базой данных с прочитанными книгами,
     * а MessageHandling - для обработки входящих сообщений от пользователя.
     */
    public TelegramBot() {
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
                // Извлекаем из объекта сообщение пользователя
                Message message = update.getMessage();
                String userMessage = message.getText();
                // Достаем из inMess id чата пользователя
                long chatId = message.getChatId();

                // Выводим сообщение пользователя в консоль
                System.out.println("TG User Message: " + userMessage);

                // Получаем текст сообщения пользователя, отправляем в написанный нами обработчик
                String response = messageHandling.parseMessage(userMessage, chatId);

                // Выводим ответ бота в консоль
                System.out.println("TG Bot Response: " + response);

                // Создаем объект класса SendMessage - наш будущий ответ пользователю
                SendMessage outMess = new SendMessage();
                // Добавляем в наше сообщение id чата, а также наш ответ
                outMess.setChatId(String.valueOf(chatId));
                outMess.setText(response);
                // Проверяем флаг awaitingRating
                if (messageHandling.isAwaitingRating()) {
                    // Если оценка ожидается, вызываем createKeyboard
                    outMess.setReplyMarkup(createKeyboard());
                }
                // Отправка в чат
                execute(outMess);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }



    /**
     * Метод для создания клавиатуры в боте
     */

    public InlineKeyboardMarkup createKeyboard() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        // Создаем первый ряд кнопок
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        rowInline1.add(InlineKeyboardButton.builder().text("1").callbackData("1").build());
        rowInline1.add(InlineKeyboardButton.builder().text("2").callbackData("2").build());
        rowsInline.add(rowInline1);

        // Создаем второй ряд кнопок
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        rowInline2.add(InlineKeyboardButton.builder().text("3").callbackData("3").build());
        rowInline2.add(InlineKeyboardButton.builder().text("4").callbackData("4").build());
        rowInline2.add(InlineKeyboardButton.builder().text("5").callbackData("5").build());
        rowsInline.add(rowInline2);

        // Устанавливаем клавиатуру
        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }


}