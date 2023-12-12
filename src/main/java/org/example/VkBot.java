package org.example;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.queries.messages.MessagesGetLongPollHistoryQuery;

import java.util.List;

/**
 * Класс для реализации VK-бота
 */
public class VkBot {

    private MessageHandling messageHandling;
    private static final long GROUP_ID = 223017873;
    private static final String ACCESS_TOKEN = System.getenv("vkbotToken");
    private VkApiClient vk;
    private GroupActor actor;


    /**
     * Конструктор класса VkBot, инициализирующий объекты VkApiClient, GroupActor и MessageHandling.
     */
    public VkBot() {
        vk = new VkApiClient(new HttpTransportClient());
        actor = new GroupActor((int) GROUP_ID, ACCESS_TOKEN);
        messageHandling = new MessageHandling();
    }


    /**
     * Метод startBot запускает VK бота.
     * Получает данные для Long Poll сервера, затем в бесконечном цикле получает и обрабатывает сообщения.
     * Обработанный ответ отправляется обратно в чат.
     * В случае возникновения исключений, выводит их в консоль.
     */
    public void startBot() {
        try {
            int ts = vk.messages().getLongPollServer(actor).execute().getTs();

            while (true) {
                MessagesGetLongPollHistoryQuery historyQuery = vk.messages()
                        .getLongPollHistory(actor)
                        .ts(ts);

                List<Message> messages = historyQuery.execute().getMessages().getItems();

                for (Message message : messages) {
                    // Извлекаем из объекта сообщение пользователя
                    String userMessage = message.getText();
                    // Достаем из inMess id чата пользователя
                    long chatId = message.getPeerId();

                    // Выводим сообщение пользователя в консоль
                    System.out.println("VK User Message: " + userMessage);

                    // Получаем текст сообщения пользователя, отправляем в написанный нами обработчик
                    String response = messageHandling.parseMessage(userMessage, chatId);

                    // Выводим ответ бота в консоль
                    System.out.println("VK Bot Response: " + response);

                    // Отправка в чат
                    sendVkMessage(chatId, response);
                }

                ts = vk.messages().getLongPollServer(actor).execute().getTs();

                Thread.sleep(10);
            }
        } catch (ApiException | ClientException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Метод sendVkMessage отправляет сообщение в VK.
     * Использует метод messages().send() VK API, указывая получателя, текст сообщения и уникальный random_id.
     * Обрабатывает возможные исключения и выводит их в консоль.
     *
     * @param chatId Идентификатор чата или пользователя в VK.
     * @param text   Текст сообщения для отправки.
     */
    private void sendVkMessage(long chatId, String text) {
        try {
            vk.messages().send(actor)
                    .peerId((int) chatId)
                    .message(text)
                    .randomId((int) System.currentTimeMillis())  // Используйте уникальное значение для random_id
                    .execute();
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
        }
    }


    /**
     * Главный метод main запускает VK бота.
     * Создает экземпляр VkBot и вызывает его метод startBot().
     *
     * @param args Аргументы командной строки (не используются).
     */
    public static void main(String[] args) {
        VkBot vkBot = new VkBot();
        vkBot.startBot();
    }
}
