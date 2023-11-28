package org.example;
import java.sql.*;
import java.util.ArrayList;

/**
 * Интерфейс для работы с книгами.
 * Позволяет управлять списком пройденных игр и осуществлять поиск по различным критериям.
 */
interface GameStorage {
    /**
     * Получает список пройденных игр для указанного чата.
     *
     * @param chatId уникальный идентификатор чата пользователя
     * @return список пройденных игр в формате строки
     */
    ArrayList<String> getPlayedGames(long chatId);

    /**
     * Добавляет книгу в список пройденных игр.
     *
     * @param title  название игры
     * @param author автор игры
     * @param year   год прочтения
     * @param rating рейтинг игры
     * @param chatId уникальный идентификатор чата пользователя
     */
    void addPlayedGame(String title, String author, int year, int rating, long chatId);

    /**
     * Удаляет все пройденные игры для указанного чата.
     *
     * @param chatId уникальный идентификатор чата пользователя
     */
    void clearPlayedGames(long chatId);

    /**
     * Получает список пройденных игр определенного автора для указанного чата.
     *
     * @param author автор игры
     * @param chatId уникальный идентификатор чата пользователя
     * @return список пройденных игр указанного автора в формате строки
     */
    ArrayList<String> getGamesByAuthor(String author, long chatId);

    /**
     * Получает список пройденных игр за определенный год для указанного чата.
     *
     * @param year   год прочтения
     * @param chatId уникальный идентификатор чата пользователя
     * @return список пройденных игр за указанный год в формате строки
     */
    ArrayList<String> getGamesByYear(int year, long chatId);

    /**
     * Получает пронумерованный список всех игр с средним рейтингом.
     *
     * @param chatId уникальный идентификатор чата пользователя
     * @return пронумерованный список игр с средним рейтингом
     */
    ArrayList<String> getGamesByAverageRating(long chatId);


    /**
     * Изменяет существующую книгу новой книгой в списке пройденных игр.
     *
     * @param oldTitle  старое название игры
     * @param oldAuthor старый автор игры
     * @param oldYear   старый год прочтения
     * @param newTitle  новое название игры
     * @param newAuthor новый автор игры
     * @param newYear   новый год прочтения
     * @param chatId    уникальный идентификатор чата пользователя
     */
    void editPlayedGame(String oldTitle, String oldAuthor, int oldYear,
                         String newTitle, String newAuthor, int newYear, long chatId);

    /**
     * Проверяет существование указанной игры в списке пройденных игр.
     *
     * @param title  название игры
     * @param author автор игры
     * @param year   год прочтения
     * @param chatId уникальный идентификатор чата пользователя
     * @return true, если книга существует в списке пройденных игр, в противном случае - false
     */
    boolean gameExists(String title, String author, int year, long chatId);
}

/**
 * Интерфейс для работы с цитатами.
 * Позволяет получать случайные цитаты.
 */
interface QuoteStorage {
    /**
     * Получает случайную цитату.
     *
     * @return случайная цитата в формате строки
     */
    String getRandQuote();
}

// Реализация интерфейсов в классе Storage
class Storage implements GameStorage, QuoteStorage {
    final private ArrayList<String> quoteList;

    /**
     * Хранилище для цитат
     */
    public Storage()
    {
        quoteList = new ArrayList<>();
        quoteList.add("Цитата: Начинать всегда стоит с того, что сеет сомнения. \n\nБорис Стругацкий.");
        quoteList.add("Цитата: 80% успеха - это появиться в нужном месте в нужное время.\n\nВуди Аллен");
        quoteList.add("Цитата: Мы должны признать очевидное: понимают лишь те,кто хочет понять.\n\nБернар Вербер");
    }

    /**
     * Метод для получения произвольной цитаты из quoteList
     */
    public String getRandQuote()
    {
        //получаем случайное значение в интервале от 0 до самого большого индекса
        int randValue = (int)(Math.random() * quoteList.size());
        //Из коллекции получаем цитату со случайным индексом и возвращаем ее
        return quoteList.get(randValue);
    }

    /**
     * Метод для получения списка пройденных игр
     */
    public ArrayList<String> getPlayedGames(long chatId) {
        ArrayList<String> games = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:completed_games.db");
            String sql = "SELECT title FROM completed_games WHERE chat_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setLong(1, chatId);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                games.add(resultSet.getString("title"));
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return games;
    }

    /**
     * Метод для добавления игры в список пройденных игр по формату: название /n автор /n год
     */
    public void addPlayedGame(String title, String author, int year, int rating, long chatId) {
        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:completed_games.db");

            // Создаем запрос на добавление игры в базу данных с указанием названия, автора, года прочтения и рейтинга
            String sql = "INSERT INTO completed_games (title, author, year, chat_id, rating) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, title);
            statement.setString(2, author);
            statement.setInt(3, year);
            statement.setLong(4, chatId);
            statement.setInt(5, rating);
            statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }
        }
    }


    /**
     * Метод для замены игры в списке пройденных игр по формату:  старое_название /n старый_автор /n старый_год новое_название /n новый_автор /n новый_год
     */
    public void editPlayedGame(String oldTitle, String oldAuthor, int oldYear, String newTitle, String newAuthor, int newYear, long chatId) {
        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:completed_games.db");

            // Создаем запрос на обновление игры в базе данных с новыми данными
            String sql = "UPDATE completed_games SET title = ?, author = ?, year = ? WHERE title = ? AND author = ? AND year = ? AND chat_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, newTitle);
            statement.setString(2, newAuthor);
            statement.setInt(3, newYear);
            statement.setString(4, oldTitle);
            statement.setString(5, oldAuthor);
            statement.setInt(6, oldYear);
            statement.setLong(7, chatId);
            statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }
        }
    }


    /**
     * Метод для полной очистки списка пройденных игр
     */
    public void clearPlayedGames(long chatId) {
        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:completed_games.db");

            // Создаем запрос на удаление записей из таблицы completed_games по chat_id
            String deleteSql = "DELETE FROM completed_games WHERE chat_id = ?";
            PreparedStatement deleteStatement = connection.prepareStatement(deleteSql);
            deleteStatement.setLong(1, chatId);
            deleteStatement.executeUpdate();

            // Закрываем подготовленный запрос
            deleteStatement.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        } finally {
            // Закрываем соединение с базой данных
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }
        }
    }

    /**
     * Метод для получения игр одного автора из списка пройденных игр
     */
    public ArrayList<String> getGamesByAuthor(String author, long chatId) {
        ArrayList<String> games = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:completed_games.db");
            String sql = "SELECT title FROM completed_games WHERE author = ? AND chat_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, author);
            statement.setLong(2, chatId);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                games.add(resultSet.getString("title"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return games;
    }

    /**
     * Метод для получения игр по конкретному году из списка пройденных игр
     */
    public ArrayList<String> getGamesByYear(int year, long chatId) {
        ArrayList<String> games = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:completed_games.db");
            String sql = "SELECT title FROM completed_games WHERE year = ? AND chat_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, year);
            statement.setLong(2, chatId);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                games.add(resultSet.getString("title"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return games;
    }

    public ArrayList<String> getGamesByAverageRating(long chatId) {
        ArrayList<String> games = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:completed_games.db");
            String sql = "SELECT title, AVG(rating) AS avg_rating FROM completed_games GROUP BY title ORDER BY avg_rating DESC";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            int rank = 1;
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                double avgRating = resultSet.getDouble("avg_rating");

                // Форматирование строки для отображения среднего рейтинга с одним знаком после запятой
                String formattedAvgRating = String.format("%.1f", avgRating);

                games.add(rank + ". " + title + ": " + formattedAvgRating + "⭐");
                rank++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return games;
    }


    /**
     * Метод для проверки существования игры в списке пройденных игр
     */
    public boolean gameExists(String title, String author, int year, long chatId) {
        Connection connection = null;
        boolean exists = false;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:completed_games.db");

            // Создаем запрос на поиск игры в базе данных с указанным названием, автором и годом прочтения
            String sql = "SELECT * FROM completed_games WHERE title = ? AND author = ? AND year = ? AND chat_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, title);
            statement.setString(2, author);
            statement.setInt(3, year);
            statement.setLong(4, chatId);

            ResultSet resultSet = statement.executeQuery();
            // Если запись найдена, устанавливаем флаг exists в true
            if (resultSet.next()) {
                exists = true;
            }

            resultSet.close();
            statement.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }
        }
        return exists;
    }

    /**
     * Метод для обновления списка пройденных игр
     */
    public void updatePlayedGames(long chatId, ArrayList<String> playedGames) {
        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:completed_games.db");

            // Получаем текущий список игр из базы данных
            ArrayList<String> currentGames = getPlayedGames(chatId);

            // Удаляем игры, которые были удалены из списка
            for (String game : currentGames) {
                if (!playedGames.contains(game)) {
                    // Удаляем книгу из базы данных
                    String deleteSql = "DELETE FROM completed_games WHERE chat_id = ? AND title = ?";
                    PreparedStatement deleteStatement = connection.prepareStatement(deleteSql);
                    deleteStatement.setLong(1, chatId);
                    deleteStatement.setString(2, game);
                    deleteStatement.executeUpdate();
                    deleteStatement.close();
                }
            }

            // Обновляем игры в базе данных
            PreparedStatement selectStatement = null;
            for (String game : playedGames) {
                // Проверяем, существует ли книга в базе данных
                String selectSql = "SELECT * FROM completed_games WHERE chat_id = ? AND title = ?";
                selectStatement = connection.prepareStatement(selectSql);
                selectStatement.setLong(1, chatId);
                selectStatement.setString(2, game);
                ResultSet resultSet = selectStatement.executeQuery();

                // Если игры нет в базе данных, добавляем ее
                if (!resultSet.next()) {
                    String insertSql = "INSERT INTO completed_games (title, chat_id) VALUES (?, ?)";
                    PreparedStatement insertStatement = connection.prepareStatement(insertSql);
                    insertStatement.setString(1, game);
                    insertStatement.setLong(2, chatId);
                    insertStatement.executeUpdate();
                    insertStatement.close();
                }
            }

            selectStatement.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }
        }
    }


    /**
     * Метод для получения списка пройденных игр в полном формате (название, автор, год)
     */
    public ArrayList<String> getAllValues(long chatId) {
        Connection connection = null;
        ArrayList<String> allValues = new ArrayList<>();
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:completed_games.db");

            String sql = "SELECT title, author, year FROM completed_games WHERE chat_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, chatId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                int year = resultSet.getInt("year");
                allValues.add(title + "\n" + author + "\n" + year);
            }

            resultSet.close();
            statement.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }
        }
        return allValues;
    }


}
