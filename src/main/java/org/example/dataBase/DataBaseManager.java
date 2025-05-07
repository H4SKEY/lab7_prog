package org.example.dataBase;

import org.example.data.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DataBaseManager {
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "shkaf1337";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

     public boolean findUser(User user) {
        try {
            Connection connection = getConnection();
            String login = user.getLogin();
            String password = user.getPassword();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM USER WHERE LOGIN = ? AND PASSWORD = ?");
            statement.setString(1, login);
            statement.setString(2, password);
            ResultSet res = statement.executeQuery();
            int count = 0;

            while (res.next()) {
                count ++;
            }

            statement.close();

            if (count == 1) {
                return true;
            }

            return false;
        }

        catch (SQLException e) {
            System.err.println("Ошибка подключения к бд...");
            return false;
        }
     }

     public boolean insertPerson(Person person) {
         return true;
     }

     public boolean insertCoordinates(Coordinates coordinates) {
         return true;
     }

     public boolean insertTicket(Ticket ticket) {
        return true;
    }

    public boolean insertUser(User user) {
        try {
            Connection connection = getConnection();
            String login = user.getLogin();
            String password = user.getPassword();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO USERS(LOGIN, PASSWORD) VALUES(?, ?)");
            statement.setString(1, login);
            statement.setString(2, password);
            int insertedRows = statement.executeUpdate();

            statement.close();

            if (insertedRows > 0) {
                return true;
            }

            return false;
        }

        catch (SQLException e) {
            System.err.println("Ошибка подключения к бд...");
            return false;
        }
    }

    public List<Ticket> loadCollection() {
        try {
            List<Ticket> tickets = new ArrayList<>();
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM TICKETS");
            ResultSet res = statement.executeQuery();

            while (res.next()) {
                int id = res.getInt("id");
                String name = res.getString("name");
                LocalDateTime creationDate = res.getObject("creationDate", LocalDateTime.class);
                Integer price = res.getInt("price");
                Integer discount = res.getInt("discount");
                String typeString = res.getString("type");
                TicketType type = TicketType.valueOf(typeString);
                int personID = res.getInt("personID");
                Person person = getPersonById(personID);
                int coordinatesID = res.getInt("coordinatesID");
                Coordinates coordinates = getCoordinatesById(coordinatesID);
                Ticket ticket = new Ticket(id, name, coordinates, creationDate, price, discount, type, person);
                tickets.add(ticket);
            }

            statement.close();
            return tickets;
        }

        catch (SQLException e) {
            System.err.println("Ошибка подключения к бд...");
            return null;
        }
    }

    public Person getPersonById(int id) {
        try {
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM PERSONS WHERE ID = ?");
            statement.setInt(1, id);
            ResultSet res = statement.executeQuery();

            statement.close();

            if (res.next()) {
                String passportID = res.getString("passportID");
                String eyeColorString = res.getString("eyeColor");
                Color eyeColor = Color.valueOf(eyeColorString);
                String hairColorString = res.getString("hairColor");
                Color hairColor = Color.valueOf(hairColorString);
                Person person = new Person(passportID, eyeColor, hairColor);
                return person;
            }

            return null;
        }

        catch (SQLException e) {
            System.err.println("Ошибка подключения к бд...");
            return null;
        }
    }

    public Coordinates getCoordinatesById(int id) {
        try {
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM COORDINATES WHERE ID = ?");
            statement.setInt(1, id);
            ResultSet res = statement.executeQuery();

            statement.close();

            if (res.next()) {
                long x = res.getLong("x");
                long y = res.getLong("y");
                Coordinates coordinates = new Coordinates(x, y);
                return coordinates;
            }

            return null;
        }

        catch (SQLException e) {
            System.err.println("Ошибка подключения к бд...");
            return null;
        }
    }
}
