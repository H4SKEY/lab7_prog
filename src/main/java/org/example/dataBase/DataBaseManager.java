package org.example.dataBase;

import org.example.data.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DataBaseManager {
    private final Connection connection;

    public DataBaseManager(Connection connection) {
        this.connection = connection;
    }

    public boolean userExists(String login) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM USERS WHERE LOGIN = ?");
            statement.setString(1, login);
            ResultSet res = statement.executeQuery();
            boolean isExist = res.next();
            res.close();
            statement.close();
            return isExist;
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к бд: " + e.getMessage());
            return false;
        }
    }

    public boolean checkPassword(String login, String password) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM USERS WHERE LOGIN = ? AND PASSWORD = ?");
            statement.setString(1, login);
            statement.setString(2, password);
            ResultSet res = statement.executeQuery();
            boolean correct = res.next();
            res.close();
            statement.close();
            return correct;
        } catch (SQLException e) {
            System.err.println("Ошибка при проверке пароля: " + e.getMessage());
            return false;
        }
    }

    public boolean isAuthorized(User user) {
        return userExists(user.getLogin()) && checkPassword(user.getLogin(), user.getPassword());
    }

    public boolean registerUser(User user) {
        if (userExists(user.getLogin())) return false;
        return insertUser(user);
    }

    public int getUserId(String login) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM USERS WHERE LOGIN = ?");
        statement.setString(1, login);
        ResultSet res = statement.executeQuery();
        if (res.next()) {
            int id = res.getInt("id");
            res.close();
            statement.close();
            return id;
        }
        throw new SQLException();
    }

    public boolean insertUser(User user) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO USERS(LOGIN, PASSWORD) " +
                    "VALUES(?, ?)");

            statement.setString(1, user.getLogin());
            statement.setString(2, user.getPassword());

            int insertedRows = statement.executeUpdate();
            statement.close();

            return insertedRows > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к бд: " + e.getMessage());
            return false;
        }
    }

    public int insertPerson(Person person, User user) {
        try {
            if (!isAuthorized(user)) {
                return -1;
            }
            int userId = getUserId(user.getLogin());

            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO PERSONS (PASSPORT_ID, EYE_COLOR, HAIR_COLOR, USER_ID) " +
                            "VALUES (?, ?::color, ?::color, ?) RETURNING ID", Statement.RETURN_GENERATED_KEYS);

            String passportId = person.getPassportID();
            if (passportId != null) {
                statement.setString(1, person.getPassportID());
            } else {
                statement.setNull(1, Types.VARCHAR);
            }

            Color eyeColor = person.getEyeColor();
            if (eyeColor != null) {
                statement.setString(2, person.getEyeColor().name());
            } else {
                statement.setNull(2, Types.VARCHAR);
            }

            Color hairColor = person.getHairColor();
            if (hairColor != null) {
                statement.setString(3, person.getHairColor().name());
            } else {
                statement.setNull(3, Types.VARCHAR);
            }

            statement.setInt(4, userId);

            statement.executeUpdate();
            ResultSet res = statement.getGeneratedKeys();
            if (res.next()) {
                int id = res.getInt(1);
                res.close();
                statement.close();
                return id;
            }
            res.close();
            statement.close();
            return -1;
        } catch (SQLException e) {
            System.err.println("Ошибка добавления человека: " + e.getMessage());
            return -1;
        }
    }


    public int insertCoordinates(Coordinates coordinates, User user) {
        try {
            if (!isAuthorized(user)) {
                return -1;
            }
            int userId = getUserId(user.getLogin());

            PreparedStatement statement = connection.prepareStatement("INSERT INTO COORDINATES" +
                    "(X, Y, USER_ID) VALUES(?, ?, ?)", Statement.RETURN_GENERATED_KEYS
            );

            statement.setLong(1, coordinates.getX());
            statement.setLong(2, coordinates.getY());
            statement.setInt(3, userId);

            statement.executeUpdate();
            ResultSet res = statement.getGeneratedKeys();
            if (res.next()) {
                int id = res.getInt(1);
                res.close();
                statement.close();
                return id;
            }
            res.close();
            statement.close();
            return -1;
        } catch (SQLException e) {
            System.err.println("Ошибка добавления координат: " + e.getMessage());
            return -1;
        }
    }

    public int insertTicket(Ticket ticket, User user) {
        try {
            if (!isAuthorized(user)) {
                return -1;
            }
            int userId = getUserId(user.getLogin());

            int personId = insertPerson(ticket.getPerson(), user);
            if (personId == -1) return -1;

            int coordinatesId = insertCoordinates(ticket.getCoordinates(), user);
            if (coordinatesId == -1) return -1;

            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO TICKETS (NAME, COORDINATES_ID, CREATION_DATE, PRICE, DISCOUNT, TYPE, " +
                            "PERSON_ID, USER_ID) VALUES (?, ?, ?, ?, ?, ?::ticket_type, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, ticket.getName());
            statement.setInt(2, coordinatesId);
            statement.setObject(3, ticket.getCreationDate());

            Integer price = ticket.getPrice();
            if (price != null) {
                statement.setInt(4, price);
            } else {
                statement.setNull(4, Types.INTEGER);
            }

            Integer discount = ticket.getDiscount();
            if (discount != null) {
                statement.setInt(5, discount);
            } else {
                statement.setNull(5, Types.INTEGER);
            }

            TicketType type = ticket.getType();
            if (type != null) {
                statement.setString(6, type.name());
            } else {
                statement.setNull(6, Types.VARCHAR);
            }

            statement.setInt(7, personId);
            statement.setInt(8, userId);

            statement.executeUpdate();
            ResultSet res = statement.getGeneratedKeys();
            if (res.next()) {
                int id = res.getInt(1);
                res.close();
                statement.close();
                return id;
            }
            res.close();
            statement.close();
            return -1;
        } catch (SQLException e) {
            System.err.println("Ошибка добавления билета: " + e.getMessage());
            return -1;
        }
    }


    public List<Ticket> loadCollection() {
        try {
            List<Ticket> tickets = new ArrayList<>();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM TICKETS");
            ResultSet res = statement.executeQuery();

            while (res.next()) {
                int id = res.getInt("ID");
                String name = res.getString("NAME");
                LocalDateTime creationDate = res.getObject("CREATION_DATE", LocalDateTime.class);
                Integer price = res.getInt("PRICE");
                Integer discount = res.getInt("DISCOUNT");

                TicketType type = null;
                String typeString = res.getString("TYPE");
                if (typeString != null && !typeString.isEmpty()) {
                    type = TicketType.valueOf(typeString);
                }

                int personID = res.getInt("PERSON_ID");
                Person person = getPersonById(personID);
                int coordinatesID = res.getInt("COORDINATES_ID");
                Coordinates coordinates = getCoordinatesById(coordinatesID);
                Ticket ticket = new Ticket(id, name, coordinates, creationDate, price, discount, type, person);
                tickets.add(ticket);
            }

            statement.close();
            return tickets;
        } catch (SQLException e) {
            System.err.println("Ошибка загрузки коллекции: " + e.getMessage());
            return null;
        }
    }


    public Person getPersonById(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM PERSONS WHERE ID = ?");
            statement.setInt(1, id);
            ResultSet res = statement.executeQuery();


            if (res.next()) {
                String passportID = res.getString("PASSPORT_ID");

                Color eyeColor = null;
                String eyeColorString = res.getString("EYE_COLOR");
                if (eyeColorString != null && !eyeColorString.isEmpty()) {
                    eyeColor = Color.valueOf(eyeColorString);
                }

                Color hairColor = null;
                String hairColorString = res.getString("HAIR_COLOR");
                if (hairColorString != null && !hairColorString.isEmpty()) {
                    hairColor = Color.valueOf(hairColorString);
                }

                res.close();
                statement.close();
                return new Person(passportID, eyeColor, hairColor);
            }

            return null;
        } catch (SQLException e) {
            System.err.println("Ошибка получения человека по ID: " + e.getMessage());
            return null;
        }
    }

    public Coordinates getCoordinatesById(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM COORDINATES WHERE ID = ?");
            statement.setInt(1, id);
            ResultSet res = statement.executeQuery();


            if (res.next()) {
                long x = res.getLong("X");
                long y = res.getLong("Y");
                res.close();
                statement.close();
                return new Coordinates(x, y);
            }

            return null;
        } catch (SQLException e) {
            System.err.println("Ошибка получения координат по ID: " + e.getMessage());
            return null;
        }
    }

    public Ticket getTicketById(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM TICKETS WHERE ID = ?");
            statement.setInt(1, id);
            ResultSet res = statement.executeQuery();

            if (res.next()) {
                String name = res.getString("name");
                int coordinatesId = res.getInt("coordinates_id");
                Coordinates coordinates = getCoordinatesById(coordinatesId);
                LocalDateTime creationDate = res.getObject("creation_date", LocalDateTime.class);
                int price = res.getInt("price");
                int discount = res.getInt("discount");
                String typeString = res.getString("ticket_type");
                TicketType type = TicketType.valueOf(typeString);
                int personId = res.getInt("person_id");
                Person person = getPersonById(personId);
                res.close();
                statement.close();
                return new Ticket(id, name, coordinates, creationDate, price, discount, type, person);
            }

            return null;
        } catch (SQLException e) {
            System.err.println("Ошибка получения билета по ID: " + e.getMessage());
            return null;
        }
    }

    public String getUserLoginById(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM USERS WHERE ID = ?");
            statement.setInt(1, id);
            ResultSet res = statement.executeQuery();

            if (res.next()) {
                String login = res.getString("login");
                res.close();
                statement.close();
                return login;
            }

            return null;
        } catch (SQLException e) {
            System.err.println("Ошибка получения пользователя по ID: " + e.getMessage());
            return null;
        }
    }

    public String getUserLoginByTicketId(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM TICKETS WHERE ID = ?");
            statement.setInt(1, id);
            ResultSet res = statement.executeQuery();

            if (res.next()) {
                int userId = res.getInt("user_id");
                String login = getUserLoginById(userId);
                res.close();
                statement.close();
                return login;
            }

            return null;
        } catch (SQLException e) {
            System.err.println("Ошибка получения пользователя по ID билета: " + e.getMessage());
            return null;
        }
    }

    public int getCoordinatesIdByTicketId(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM TICKETS WHERE ID = ?");
            statement.setInt(1, id);
            ResultSet res = statement.executeQuery();

            if (res.next()) {
                int coordinatesId = res.getInt("coordinates_id");
                res.close();
                statement.close();
                return coordinatesId;
            }

            return -1;
        } catch (SQLException e) {
            System.err.println("Ошибка получения ID координат по ID билета: " + e.getMessage());
            return -1;
        }
    }

    public int getPersonIdByTicketId(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM TICKETS WHERE ID = ?");
            statement.setInt(1, id);
            ResultSet res = statement.executeQuery();

            if (res.next()) {
                int personId = res.getInt("person_id");
                res.close();
                statement.close();
                return personId;
            }

            return -1;
        } catch (SQLException e) {
            System.err.println("Ошибка получения ID человека по ID билета: " + e.getMessage());
            return -1;
        }
    }

    public boolean updateCoordinates(int id, Coordinates newCoordinates, User user) {
        try {
            if (!isAuthorized(user)) {
                return false;
            }

            if (!getUserLoginByTicketId(id).equals(user.getLogin())) {
                return false;
            }

            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE COORDINATES SET X = ?, Y = ? WHERE ID = ?");

            statement.setLong(1, newCoordinates.getX());
            statement.setLong(2, newCoordinates.getY());
            statement.setInt(3, id);

            int updated = statement.executeUpdate();
            statement.close();
            return updated > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении координат: " + e.getMessage());
            return false;
        }
    }

    public boolean updatePerson(int id, Person newPerson, User user) {
        try {
            if (!isAuthorized(user)) {
                return false;
            }

            if (!getUserLoginByTicketId(id).equals(user.getLogin())) {
                return false;
            }

            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE PERSONS SET PASSPORT_ID = ?, EYE_COLOR = ?, HAIR_COLOR = ? WHERE ID = ?");

            statement.setString(1, newPerson.getPassportID());
            statement.setString(2, newPerson.getEyeColor().name());
            statement.setString(3, newPerson.getHairColor().name());
            statement.setInt(4, id);

            int updated = statement.executeUpdate();
            statement.close();
            return updated > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении человека: " + e.getMessage());
            return false;
        }
    }

    public boolean updateTicket(int id, Ticket newTicket, User user) {
        try {
            if (!isAuthorized(user)) {
                return false;
            }

            if (!getUserLoginByTicketId(id).equals(user.getLogin())) {
                return false;
            }

            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE TICKETS SET NAME = ?, COORDINATES_ID = ?, CREATION_DATE = ?, PRICE = ?, " +
                            "DISCOUNT = ?, TYPE = ?, PERSON_ID = ? WHERE ID = ?");

            int coordinatesId = getCoordinatesIdByTicketId(id);
            if (coordinatesId == -1) {
                return false;
            }
            if (!updateCoordinates(coordinatesId, newTicket.getCoordinates(), user)) {
                return false;
            }

            int personId = getPersonIdByTicketId(id);
            if (personId == -1) {
                return false;
            }
            if (!updatePerson(personId, newTicket.getPerson(), user)) {
                return false;
            }

            statement.setString(1, newTicket.getName());
            statement.setInt(2, coordinatesId);
            statement.setObject(3, newTicket.getCreationDate());
            statement.setInt(4, newTicket.getPrice());
            statement.setInt(5, newTicket.getDiscount());
            statement.setString(6, newTicket.getType().name());
            statement.setInt(7, personId);
            statement.setInt(8, id);

            int updated = statement.executeUpdate();
            statement.close();
            return updated > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении билета: " + e.getMessage());
            return false;
        }
    }

    public boolean removeTicketById(int id, User user) {
        try {
            if (!isAuthorized(user)) {
                return false;
            }
            int userId = getUserId(user.getLogin());

            PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM TICKETS WHERE ID = ? AND USER_ID = ?");

            statement.setInt(1, id);
            statement.setInt(2, userId);

            int deleted = statement.executeUpdate();
            statement.close();
            return deleted > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении билета: " + e.getMessage());
            return false;
        }
    }

    public boolean clearUserTickets(User user) {
        try {
            if (!isAuthorized(user)) {
                return false;
            }
            int userId = getUserId(user.getLogin());

            PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM TICKETS WHERE USER_ID = ?");
            statement.setInt(1, userId);

            int deleted = statement.executeUpdate();
            statement.close();
            return deleted > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при очистке билетов пользователя: " + e.getMessage());
            return false;
        }
    }

    public boolean isTicketOwnedByUser(int ticketId, User user) {
        try {
            if (!isAuthorized(user)) {
                return false;
            }
            int userId = getUserId(user.getLogin());

            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM TICKETS WHERE ID = ? AND USER_ID = ?");
            statement.setInt(1, ticketId);
            statement.setInt(2, userId);

            ResultSet rs = statement.executeQuery();
            boolean result = rs.next();
            rs.close();
            statement.close();
            return result;
        } catch (SQLException e) {
            System.err.println("Ошибка проверки владения билетом: " + e.getMessage());
            return false;
        }
    }
}
