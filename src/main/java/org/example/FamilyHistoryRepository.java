package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

class FamilyMember {

    private String name;
    private int age;
    private int familyMemberId;

    public FamilyMember(int familyMemberId, String name, int age) {
        this.familyMemberId = familyMemberId;
        this.name = name;
        this.age = age;
    }

    public int getFamilyId() {
        return familyMemberId;
    }

    public void setId(int familyMemberId) {
        this.familyMemberId = familyMemberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "FamilyMember {" +
                "familyMemberId=" + getFamilyId() +
                ", name='" + getName() + '\'' +
                ", age=" + getAge() +
                '}';
    }
}

public class FamilyHistoryRepository {

    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private final Connection connection;

    public FamilyHistoryRepository(Connection connection) {
        this.connection = connection;
    }

    public void createFamilyTable(Connection connection) {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS family_members (familyMemberId INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, age INTEGER);";
        try (PreparedStatement statement = connection.prepareStatement(createTableSQL)) {
            statement.execute();
        } catch (SQLException e) {
            logger.severe("Unable to create table");
            logger.log(Level.SEVERE, e.getMessage(), (Throwable) e);
        }
    }

    public void createFamilyMember(FamilyMember familyMember) {
        String insertDataSQL = "INSERT INTO family_members (name, age) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertDataSQL)) {
            preparedStatement.setString(1, familyMember.getName());
            preparedStatement.setInt(2, familyMember.getAge());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.severe(" unable to create " + familyMember);
            logger.log(Level.SEVERE, e.getMessage(), (Throwable) e);
        }
    }

    public List<FamilyMember> readFamilyMembers() throws SQLException {
        List<FamilyMember> familyMembers = new ArrayList<>();
        String selectDataSQL = "SELECT * FROM family_members";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectDataSQL);
                ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int familyMemberId = resultSet.getInt("familyMemberId");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                familyMembers.add(new FamilyMember(familyMemberId, name, age));
            }
        } catch (SQLException e) {
            logger.severe("Unable to read database");
            logger.log(Level.SEVERE, e.getMessage(), (Throwable) e);
        }
        return familyMembers;
    }

    public FamilyMember getFamilyMemberByName(String name) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM family_members WHERE name = ?")) {
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int familyMemberId = resultSet.getInt("familyMemberId");
                String retrievedUsername = resultSet.getString("name");
                int age = resultSet.getInt("age");
                return new FamilyMember(familyMemberId, retrievedUsername, age);
            }
        } catch (SQLException e) {
            logger.severe(name + " Not found in database");
            logger.log(Level.SEVERE, e.getMessage(), (Throwable) e);
        }

        return null;
    }

    public void updateName(int memberId, String newName) {
        String updateNameSQL = "UPDATE family_members SET name = ? WHERE familyMemberId = ?";
        try (PreparedStatement updateNameStatement = connection.prepareStatement(updateNameSQL)) {
            updateNameStatement.setString(1, newName);
            updateNameStatement.setInt(2, memberId);
            updateNameStatement.executeUpdate();
        } catch (SQLException e) {
            logger.severe(memberId + " Id Not found in database ");
            logger.log(Level.SEVERE, e.getMessage(), (Throwable) e);
        }
    }

    public void updateAge(int memberId, int newAge) {
        String updateAgeSQL = "UPDATE family_members SET age = ? WHERE familyMemberId = ?";
        try (PreparedStatement updateAgeStatement = connection.prepareStatement(updateAgeSQL)) {
            updateAgeStatement.setInt(1, newAge);
            updateAgeStatement.setInt(2, memberId);
            updateAgeStatement.executeUpdate();
        } catch (SQLException e) {
            logger.severe(memberId + " Id Not found in database ");
            logger.log(Level.SEVERE, e.getMessage(), (Throwable) e);
        }
    }

    public void deleteFamilyMember(int memberId) {
        String deleteDataSQL = "DELETE FROM family_members WHERE familyMemberId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteDataSQL)) {
            preparedStatement.setInt(1, memberId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.severe(memberId + " Id Not found in database ");
            logger.log(Level.SEVERE, e.getMessage(), (Throwable) e);
        }
    }
}
