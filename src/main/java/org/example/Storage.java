package org.example;
import java.sql.*;
import java.util.ArrayList;

/**
 * Интерфейс для работы с книгами.
 * Позволяет управлять списком прочитанных книг и осуществлять поиск по различным критериям.
 */
interface BookStorage {
    /**
     * Получает список прочитанных книг для указанного чата.
     *
     * @param chatId уникальный идентификатор чата пользователя
     * @return список прочитанных книг в формате строки
     */
    ArrayList<String> getReadBooks(long chatId);

    /**
     * Добавляет книгу в список прочитанных книг.
     *
     * @param title  название книги
     * @param author автор книги
     * @param year   год прочтения
     * @param chatId уникальный идентификатор чата пользователя
     */
    void addReadBook(String title, String author, int year, long chatId);

    /**
     * Удаляет все прочитанные книги для указанного чата.
     *
     * @param chatId уникальный идентификатор чата пользователя
     */
    void clearReadBooks(long chatId);

    /**
     * Получает список прочитанных книг определенного автора для указанного чата.
     *
     * @param author автор книги
     * @param chatId уникальный идентификатор чата пользователя
     * @return список прочитанных книг указанного автора в формате строки
     */
    ArrayList<String> getBooksByAuthor(String author, long chatId);

    /**
     * Получает список прочитанных книг за определенный год для указанного чата.
     *
     * @param year   год прочтения
     * @param chatId уникальный идентификатор чата пользователя
     * @return список прочитанных книг за указанный год в формате строки
     */
    ArrayList<String> getBooksByYear(int year, long chatId);

    /**
     * Изменяет существующую книгу новой книгой в списке прочитанных книг.
     *
     * @param oldTitle  старое название книги
     * @param oldAuthor старый автор книги
     * @param oldYear   старый год прочтения
     * @param newTitle  новое название книги
     * @param newAuthor новый автор книги
     * @param newYear   новый год прочтения
     * @param chatId    уникальный идентификатор чата пользователя
     */
    void editReadBook(String oldTitle, String oldAuthor, int oldYear,
                         String newTitle, String newAuthor, int newYear, long chatId);

    /**
     * Проверяет существование указанной книги в списке прочитанных книг.
     *
     * @param title  название книги
     * @param author автор книги
     * @param year   год прочтения
     * @param chatId уникальный идентификатор чата пользователя
     * @return true, если книга существует в списке прочитанных книг, в противном случае - false
     */
    boolean bookExists(String title, String author, int year, long chatId);
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
class Storage implements BookStorage, QuoteStorage {
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
     * Метод для добавления книги в список прочитанных книг по формату: название /n автор /n год
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
     * Метод для замены книги в списке прочитанных книг по формату: старое_название /n старый_автор /n старый_год новое_название /n новый_автор /n новый_год
     */
    public void editReadBook(String oldTitle, String oldAuthor, int oldYear, String newTitle, String newAuthor, int newYear, long chatId) {
        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:read_books.db");

            // Создаем запрос на обновление книги в базе данных с новыми данными
            String sql = "UPDATE read_books SET title = ?, author = ?, year = ? WHERE title = ? AND author = ? AND year = ? AND chat_id = ?";
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
     * Метод для полной очистки списка прочитанных книг
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


    /*
     * Метод для удаления книги из списка прочитанных книг по формату: название /n автор /n год
     */
  /*  public void removeReadBook(String title, String author, int year, long chatId) {
        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:read_books.db");

            // Создаем запрос на удаление книги из базы данных с указанием названия, автора, года прочтения и chatId
            String sql = "DELETE FROM read_books WHERE title = ? AND author = ? AND year = ? AND chat_id = ?";
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
    } */

    /**
     * Метод для проверки существования книги в списке прочитанных книг
     */
    public boolean bookExists(String title, String author, int year, long chatId) {
        Connection connection = null;
        boolean exists = false;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:read_books.db");

            // Создаем запрос на поиск книги в базе данных с указанным названием, автором и годом прочтения
            String sql = "SELECT * FROM read_books WHERE title = ? AND author = ? AND year = ? AND chat_id = ?";
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
     * Метод для обновления списка прочитанных книг
     */
    public void updateReadBooks(long chatId, ArrayList<String> readBooks) {
        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:read_books.db");

            // Получаем текущий список книг из базы данных
            ArrayList<String> currentBooks = getReadBooks(chatId);

            // Удаляем книги, которые были удалены из списка
            for (String book : currentBooks) {
                if (!readBooks.contains(book)) {
                    // Удаляем книгу из базы данных
                    String deleteSql = "DELETE FROM read_books WHERE chat_id = ? AND title = ?";
                    PreparedStatement deleteStatement = connection.prepareStatement(deleteSql);
                    deleteStatement.setLong(1, chatId);
                    deleteStatement.setString(2, book);
                    deleteStatement.executeUpdate();
                    deleteStatement.close();
                }
            }

            // Обновляем книги в базе данных
            PreparedStatement selectStatement = null;
            for (String book : readBooks) {
                // Проверяем, существует ли книга в базе данных
                String selectSql = "SELECT * FROM read_books WHERE chat_id = ? AND title = ?";
                selectStatement = connection.prepareStatement(selectSql);
                selectStatement.setLong(1, chatId);
                selectStatement.setString(2, book);
                ResultSet resultSet = selectStatement.executeQuery();

                // Если книги нет в базе данных, добавляем ее
                if (!resultSet.next()) {
                    String insertSql = "INSERT INTO read_books (title, chat_id) VALUES (?, ?)";
                    PreparedStatement insertStatement = connection.prepareStatement(insertSql);
                    insertStatement.setString(1, book);
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
     * Метод для получения списка прочитанных книг в полном формате (название, автор, год)
     */
    public ArrayList<String> getAllValues(long chatId) {
        Connection connection = null;
        ArrayList<String> allValues = new ArrayList<>();
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:read_books.db");

            String sql = "SELECT title, author, year FROM read_books WHERE chat_id = ?";
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



}
