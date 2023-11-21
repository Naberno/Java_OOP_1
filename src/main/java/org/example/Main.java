package org.example;

import org.apache.logging.log4j.*;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    /**
     Статическая переменная для логгера, созданного через LogManager.
     Используется для логгирования событий в классе Main.
     Передается как параметр в методы логгирования для идентификации источника логирования.
     */
    private static final Logger logger = LogManager.getLogger(Main.class);

    /**
     * Главный метод, который запускает бота.
     * Создает объект TelegramBotsApi, который используется для регистрации бота.
     * Регистрирует новый экземпляр класса TelegramBot с помощью метода registerBot().
     * Если при регистрации происходит ошибка, то выводит ее в консоль.
     */
    public static void main(String[] args)
    {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new TelegramBot());
        } catch (TelegramApiException e) {
            logger.error("Error registering bot", e);
        }
    }
}