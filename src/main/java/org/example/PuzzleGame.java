package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Интерфейс для игры в загадки.
 */
interface PuzzleGameInterface {

    /**
     * Начинает новую головоломку для указанного чата.
     *
     * @param chatId Идентификатор чата, в котором начинается головоломка.
     * @return Сообщение с вопросом текущей головоломки.
     */
    String startPuzzle(long chatId);

    /**
     * Проверяет ответ пользователя на текущую головоломку.
     *
     * @param chatId     Идентификатор чата, откуда получен ответ пользователя.
     * @param userAnswer Ответ пользователя на головоломку.
     * @return Сообщение с результатом проверки ответа пользователя.
     */
    String checkAnswer(long chatId, String userAnswer);

    /**
     * Возвращает подсказку для текущей головоломки.
     *
     * @return Подсказка для текущей головоломки.
     */
    String getHint();

    /**
     * Выбирает следующую головоломку для указанного чата.
     *
     * @param chatId Идентификатор чата, для которого выбирается следующая головоломка.
     * @return Сообщение с вопросом следующей головоломки.
     */
    String getNextPuzzle(long chatId);

    /**
     * Возвращает статистику ответов пользователя для указанного чата.
     *
     * @param chatId Идентификатор чата, для которого возвращается статистика.
     * @return Статистика ответов пользователя в виде текстового сообщения.
     */
    String getStatistics(long chatId);

    /**
     * Перезапускает игровую сессию.
     *
     * @param chatId Идентификатор чата, для которого возвращается ответ на загадку.
     * @return Сообщение о начале новой игры.
     */
    String restart(long chatId);

    /**
     * Возвращает ответ на текущую загадку, записывает её как нерешённую и переходит к следующей загадке.
     *
     * @param chatId Идентификатор чата, для которого возвращается ответ на загадку.
     * @return Ответ на текущую загадку и следующая загадка.
     */
    String getAnswerAndNextPuzzle(long chatId);
}

/**
 * Класс представляет собой объект головоломки.
 */
class Puzzle {
    private String question;
    private String answer;
    private String hint;

    /**
     * Конструктор класса Puzzle. Инициализирует загадку, ответ и подсказку.
     *
     * @param question Вопрос головоломки.
     * @param answer   Правильный ответ на головоломку.
     * @param hint     Подсказка для головоломки.
     */
    public Puzzle(String question, String answer, String hint) {
        this.question = question;
        this.answer = answer;
        this.hint = hint;
    }

    /**
     * Метод возвращает вопрос головоломки.
     *
     * @return Вопрос головоломки.
     */
    public String getQuestion() {
        return question;
    }

    /**
     * Метод возвращает правильный ответ на головоломку.
     *
     * @return Правильный ответ на головоломку.
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * Метод возвращает подсказку для головоломки.
     *
     * @return Подсказка для головоломки.
     */
    public String getHint() {
        return hint;
    }
}

/**
 * Класс реализующий игру в загадки.
 */
public class PuzzleGame implements PuzzleGameInterface {
    private Puzzle currentPuzzle;
    private Map<String, Puzzle> puzzles;
    private Map<Long, Integer> correctAnswers;
    private Map<Long, Integer> userAttempts;
    private Map<Long, List<String>> unsolvedPuzzles;

    /**
     * Конструктор класса PuzzleGame. Инициализирует объекты головоломок и статистики ответов пользователей.
     */
    public PuzzleGame() {
        puzzles = new HashMap<>();
        puzzles.put("Часто висит головой вниз, к небу стремится всегда, но полететь не может", new Puzzle("Часто висит головой вниз, к небу стремится всегда, но полететь не может", "Капля", "Это падает с неба во время дождя"));
        puzzles.put("Имеет корни, но не растет. Не видит, но слышит", new Puzzle("Имеет корни, но не растет. Не видит, но слышит", "Дерево", "Это большое растение в парке"));
        puzzles.put("Без рук, без ног, а всегда идут", new Puzzle("Без рук, без ног, а всегда идут", "Часы", "Показывает время"));
        puzzles.put("Без окон, дверей и крыши, но внутри есть золото", new Puzzle("Без окон, дверей и крыши, но внутри есть золото", "Арахис", "Это еда и часто используется для приготовления масла"));
        puzzles.put("Чем больше берешь, тем меньше остается", new Puzzle("Чем больше берешь, тем меньше остается", "Время", "Это уходит, когда вы его не замечаете"));
        puzzles.put("Что можно увидеть с закрытыми глазами?", new Puzzle("Что можно увидеть с закрытыми глазами?", "Сон", "Это происходит, когда вы спите"));
        puzzles.put("Белый, пушистый, летает без крыльев", new Puzzle("Белый, пушистый, летает без крыльев", "Снег", "Это падает с неба зимой и покрывает землю"));
        puzzles.put("Имеет ключ, но не открывает замок", new Puzzle("Имеет ключ, но не открывает замок", " Карта", " Это помогает вам найти путь"));
        puzzles.put("Может быть легким как перышко, но сам не поднимется в воздух", new Puzzle("Может быть легким как перышко, но сам не поднимется в воздух", " Ветер", "Это движется вокруг нас, но невидимо"));
        puzzles.put("Имеет ушко, но не слышит", new Puzzle("Имеет ушко, но не слышит", "Игла", "Используется для шитья"));
        puzzles.put("Бежит и не может уйти вперед", new Puzzle("Бежит и не может уйти вперед", "Река", "Это течет от гор к океанам"));
        puzzles.put("Висит в воздухе и греет нас своим светом", new Puzzle("Висит в воздухе и греет нас своим светом", "Солнце", "Это небесное тело светит днем"));
        puzzles.put("Имеет зубы, но не кусает", new Puzzle("Имеет зубы, но не кусает", "Гребешок", "Это находится у морских животных"));
        puzzles.put("Может быть горячим или холодным, но никогда не теплым", new Puzzle("Может быть горячим или холодным, но никогда не теплым", "Огонь", "Это используется для приготовления пищи и обогрева"));
        puzzles.put("Стоит на кончике ног, но не упадет", new Puzzle("Стоит на кончике ног, но не упадет", " Тень", "Это образуется, когда что-то загораживает свет"));
        puzzles.put("Серое, большое, и все внутри", new Puzzle("Серое, большое, и все внутри", "Облако", "Это плавает в небе и приносит дождь"));
        puzzles.put("Маленький как бутылка, светится внутри, но не является источником света", new Puzzle("Маленький как бутылка, светится внутри, но не является источником света", "Лампочка", "Это используется для освещения комнаты"));
        puzzles.put("Может стоять в одной точке, но всегда стремится вверх", new Puzzle("Может стоять в одной точке, но всегда стремится вверх", "Дым", "Это образуется, когда что-то горит"));
        puzzles.put("Что можно сломать, даже если ни разу не касался?", new Puzzle("Что можно сломать, даже если ни разу не касался?", "Обещание", "Это слово, которое вы должны держать"));
        puzzles.put("Быстрый как стрела, он летит без перьев", new Puzzle("Быстрый как стрела, он летит без перьев", "Свет", "Это движется со скоростью 299 792 458 метров в секунду"));


        userAttempts = new HashMap<>();
        correctAnswers = new HashMap<>();
        currentPuzzle = null;
        unsolvedPuzzles = new HashMap<>();
    }


    /**
     * Метод начинает новую головоломку для указанного чата.
     *
     * @param chatId Идентификатор чата, в котором начинается головоломка.
     * @return Сообщение с вопросом текущей головоломки.
     */
    public String startPuzzle(long chatId) {
        if (puzzles.isEmpty()) {
            return "Все загадки решены!";
        }

        userAttempts.put(chatId, 0);
        String randomPuzzleKey = getRandomPuzzle();
        currentPuzzle = puzzles.get(randomPuzzleKey);
        return "Добро пожаловать в игру в загадки! Начнем.\nЗагадка: " + currentPuzzle.getQuestion();
    }


    /**
     * Метод проверяет ответ пользователя на текущую головоломку.
     *
     * @param chatId     Идентификатор чата, откуда получен ответ пользователя.
     * @param userAnswer Ответ пользователя на головоломку.
     * @return Сообщение с результатом проверки ответа пользователя.
     */
    public String checkAnswer(long chatId, String userAnswer) {
        if (currentPuzzle == null) {
            return "Нет текущей загадки.";
        }

        if (!puzzles.isEmpty()) {
            if (userAnswer.equalsIgnoreCase(currentPuzzle.getAnswer())) {
                userAttempts.put(chatId, 0);
                correctAnswers.put(chatId, correctAnswers.getOrDefault(chatId, 0) + 1);
                puzzles.remove(currentPuzzle.getQuestion());
                if (puzzles.isEmpty()) {
                    return "Поздравляю, вы решили все загадки! Пожалуйста, нажмите /stoppuzzle для выхода из игры, или /statistic для получения статистики в текущей игре, или /restart для перезапуска игры";
                } else {
                    currentPuzzle = puzzles.get(getRandomPuzzle());
                    return "Верно! Следующая загадка: " + currentPuzzle.getQuestion();
                }
            } else {
                return "Неверно! Попробуйте еще раз.";
            }
        } else {
            return "Поздравляю, вы решили все загадки! Пожалуйста, нажмите /stoppuzzle для выхода из игры, или /statistic для получения статистики в текущей игре, или /restart для перезапуска игры";
        }
    }


    /**
     * Метод возвращает подсказку для текущей головоломки.
     *
     * @return Подсказка для текущей головоломки.
     */
    public String getHint() {
        if (currentPuzzle == null) {
            return "Нет текущей загадки.";
        }
        return "Подсказка: " + currentPuzzle.getHint();
    }


    /**
     * Метод выбирает случайную головоломку из доступных.
     *
     * @return Ключ выбранной случайной головоломки.
     */
    private String getRandomPuzzle() {
        List<String> puzzleList = new ArrayList<>(puzzles.keySet());
        Random random = new Random(); //посмотреть позже
        return puzzleList.get(random.nextInt(puzzleList.size()));
    }


    /**
     * Метод выбирает следующую головоломку для указанного чата.
     *
     * @param chatId Идентификатор чата, для которого выбирается следующая головоломка.
     * @return Сообщение с вопросом следующей головоломки.
     */
    public String getNextPuzzle(long chatId) {
        puzzles.remove(currentPuzzle.getQuestion());
        userAttempts.put(chatId, 0);

        if (puzzles.isEmpty()) {
            return "Все загадки решены! Пожалуйста, нажмите /stoppuzzle для выхода из игры, или /statistic для получения статистики в текущей игре, или /restart для перезапуска игры";
        } else {
            currentPuzzle = puzzles.get(getRandomPuzzle());
            return "Следующая загадка: " + currentPuzzle.getQuestion();
        }
    }


    /**
     * Метод возвращает статистику ответов пользователя для указанного чата.
     *
     * @param chatId Идентификатор чата, для которого возвращается статистика.
     * @return Статистика ответов пользователя в виде текстового сообщения.
     */
    public String getStatistics(long chatId) {
        int correct = correctAnswers.getOrDefault(chatId, 0);
        int total = 4;
        double percentage = (correct * 100.0) / total;
        return "Правильных ответов: " + correct + "\nНеправильных ответов: " +
                (4-correct) + "\nПроцент правильных ответов: " + percentage + "%";
    }

    /*
    /**
     * Метод возвращает список нерешенных головоломок для указанного чата.
     *
     * @param chatId Идентификатор чата, для которого возвращаются нерешенные головоломки.
     * @return Список нерешенных головоломок в виде текстового сообщения.
     */
    /*
    public String getUnsolvedPuzzles(long chatId) {
        List<String> unsolved = unsolvedPuzzles.getOrDefault(chatId, new ArrayList<>());

        if (unsolved.isEmpty()) {
            return "У вас нет нерешенных загадок.";
        }

        return "Нерешенные загадки:\n" + unsolved.stream().collect(Collectors.joining("\n"));
    }
*/


    /**
     * Метод перезапускает игровую сессию
     *
     * @param chatId Идентификатор чата, для которого возвращается ответ на загадку.
     * @return сообщение о начале новой игры
     */
    public String restart(long chatId) {
        // Очистите все данные игры и статистику для данного чата
        userAttempts.remove(chatId);
        correctAnswers.remove(chatId);
        puzzles.clear();
        unsolvedPuzzles.remove(chatId);

        // Восстановите исходные загадки
        puzzles.put("Часто висит головой вниз, к небу стремится всегда, но полететь не может", new Puzzle("Часто висит головой вниз, к небу стремится всегда, но полететь не может", "Капля", "Это падает с неба во время дождя"));
        puzzles.put("Имеет корни, но не растет. Не видит, но слышит", new Puzzle("Имеет корни, но не растет. Не видит, но слышит", "Дерево", "Это большое растение в парке"));
        puzzles.put("Без рук, без ног, а всегда идут", new Puzzle("Без рук, без ног, а всегда идут", "Часы", "Показывает время"));
        puzzles.put("Без окон, дверей и крыши, но внутри есть золото", new Puzzle("Без окон, дверей и крыши, но внутри есть золото", "Арахис", "Это еда и часто используется для приготовления масла"));
        puzzles.put("Чем больше берешь, тем меньше остается", new Puzzle("Чем больше берешь, тем меньше остается", "Время", "Это уходит, когда вы его не замечаете"));
        puzzles.put("Что можно увидеть с закрытыми глазами?", new Puzzle("Что можно увидеть с закрытыми глазами?", "Сон", "Это происходит, когда вы спите"));
        puzzles.put("Белый, пушистый, летает без крыльев", new Puzzle("Белый, пушистый, летает без крыльев", "Снег", "Это падает с неба зимой и покрывает землю"));
        puzzles.put("Имеет ключ, но не открывает замок", new Puzzle("Имеет ключ, но не открывает замок", " Карта", " Это помогает вам найти путь"));
        puzzles.put("Может быть легким как перышко, но сам не поднимется в воздух", new Puzzle("Может быть легким как перышко, но сам не поднимется в воздух", " Ветер", "Это движется вокруг нас, но невидимо"));
        puzzles.put("Имеет ушко, но не слышит", new Puzzle("Имеет ушко, но не слышит", "Игла", "Используется для шитья"));
        puzzles.put("Бежит и не может уйти вперед", new Puzzle("Бежит и не может уйти вперед", "Река", "Это течет от гор к океанам"));
        puzzles.put("Висит в воздухе и греет нас своим светом", new Puzzle("Висит в воздухе и греет нас своим светом", "Солнце", "Это небесное тело светит днем"));
        puzzles.put("Имеет зубы, но не кусает", new Puzzle("Имеет зубы, но не кусает", "Гребешок", "Это находится у морских животных"));
        puzzles.put("Может быть горячим или холодным, но никогда не теплым", new Puzzle("Может быть горячим или холодным, но никогда не теплым", "Огонь", "Это используется для приготовления пищи и обогрева"));
        puzzles.put("Стоит на кончике ног, но не упадет", new Puzzle("Стоит на кончике ног, но не упадет", " Тень", "Это образуется, когда что-то загораживает свет"));
        puzzles.put("Серое, большое, и все внутри", new Puzzle("Серое, большое, и все внутри", "Облако", "Это плавает в небе и приносит дождь"));
        puzzles.put("Маленький как бутылка, светится внутри, но не является источником света", new Puzzle("Маленький как бутылка, светится внутри, но не является источником света", "Лампочка", "Это используется для освещения комнаты"));
        puzzles.put("Может стоять в одной точке, но всегда стремится вверх", new Puzzle("Может стоять в одной точке, но всегда стремится вверх", "Дым", "Это образуется, когда что-то горит"));
        puzzles.put("Что можно сломать, даже если ни разу не касался?", new Puzzle("Что можно сломать, даже если ни разу не касался?", "Обещание", "Это слово, которое вы должны держать"));
        puzzles.put("Быстрый как стрела, он летит без перьев", new Puzzle("Быстрый как стрела, он летит без перьев", "Свет", "Это движется со скоростью 299 792 458 метров в секунду"));

        currentPuzzle = null;

        return "Игра в загадки начата заново.\n" + startPuzzle(chatId);
    }


    /**
     * Метод возвращает ответ на текущую загадку, записывает её как нерешённую и переходит к следующей загадке.
     *
     * @param chatId Идентификатор чата, для которого возвращается ответ на загадку.
     * @return Ответ на текущую загадку и следующая загадка.
     */
    public String getAnswerAndNextPuzzle(long chatId) {
        if (currentPuzzle == null) {
            return "Нет текущей загадки.";
        }

        String answer = "Ответ на загадку '" + currentPuzzle.getQuestion() + "' : " + currentPuzzle.getAnswer();

        // Уберите текущую загадку из списка доступных загадок
        puzzles.remove(currentPuzzle.getQuestion());
        userAttempts.put(chatId, 0);

        if (puzzles.isEmpty()) {
            return answer + "\nПоздравляю, вы решили все загадки! Пожалуйста, нажмите /stoppuzzle";
        } else {
            currentPuzzle = puzzles.get(getRandomPuzzle());
            return answer + "\nСледующая загадка: " + currentPuzzle.getQuestion();
        }
    }

}






