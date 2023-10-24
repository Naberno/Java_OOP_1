package org.example;

import java.sql.*;
import java.util.ArrayList;

/**
 * Класс, реализующий хранилище данных
 */
 class Storage {
    private ArrayList<String> quoteList;

     /**
     * Хранилище для цитат
      */
    Storage()
    {
        quoteList = new ArrayList<>();
        quoteList.add("Начинать всегда стоит с того, что сеет сомнения. \n\nБорис Стругацкий.");
        quoteList.add("80% успеха - это появиться в нужном месте в нужное время.\n\nВуди Аллен");
        quoteList.add("Мы должны признать очевидное: понимают лишь те,кто хочет понять.\n\nБернар Вербер");
    }

     /**
      * Метод для получения рандомной цитаты из quoteList
      */
    String getRandQuote()
    {
        //получаем случайное значение в интервале от 0 до самого большого индекса
        int randValue = (int)(Math.random() * quoteList.size());
        //Из коллекции получаем цитату со случайным индексом и возвращаем ее
        return quoteList.get(randValue);
    }

    /**
     * Метод для получения списка прочитанных книг
     */
    public ArrayList<String> getReadBooks(long chatId) {
        ArrayList<String> books = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:read_books.db");
            String sql = "SELECT title FROM read_books WHERE chat_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setLong(1, chatId);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                books.add(resultSet.getString("title"));
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
        return books;
    }

    /**
     * Метод для добавления книги в список прочитанных книг по формату: название, автор, год
     */
    public void addReadBook(String title, String author, int year, long chatId) {
        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:read_books.db");

            // Создаем запрос на добавление книги в базу данных с указанием названия, автора и года прочтения
            String sql = "INSERT INTO read_books (title, author, year, chat_id) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, title);
            statement.setString(2, author);
            statement.setInt(3, year);
            statement.setLong(4, chatId);
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
     * Метод для понлой очистки списка прочитанных книг
     */
    public void clearReadBooks(long chatId) {
        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:read_books.db");

            // Создаем запрос на удаление записей из таблицы read_books по chat_id
            String deleteSql = "DELETE FROM read_books WHERE chat_id = ?";
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
     * Метод для получения книг одного автора из списка прочитанных книг
     */
    public ArrayList<String> getBooksByAuthor(String author, long chatId) {
        ArrayList<String> books = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:read_books.db");
            String sql = "SELECT title FROM read_books WHERE author = ? AND chat_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, author);
            statement.setLong(2, chatId);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                books.add(resultSet.getString("title"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return books;
    }

    /**
     * Метод для получения книг по конкретному году из списка прочитанных книг
     */
    public ArrayList<String> getBooksByYear(int year, long chatId) {
        ArrayList<String> books = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:read_books.db");
            String sql = "SELECT title FROM read_books WHERE year = ? AND chat_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, year);
            statement.setLong(2, chatId);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                books.add(resultSet.getString("title"));
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
        return books;
    }

}
