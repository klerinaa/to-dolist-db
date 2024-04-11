package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ToDoListManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/todo_db";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            createTablesIfNotExist(connection);

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("1. Create ToDo List\n2. View ToDo Lists\n3. Exit");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        createToDoList(connection);
                        break;
                    case 2:
                        viewToDoLists(connection);
                        break;
                    case 3:
                        System.out.println("Exiting application...");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTablesIfNotExist(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            // Create ToDoList table
            String createToDoListTable = "CREATE TABLE IF NOT EXISTS todolist (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "name VARCHAR(255) NOT NULL)";
            statement.executeUpdate(createToDoListTable);

            // Create Task table
            String createTaskTable = "CREATE TABLE IF NOT EXISTS task (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "todolist_id INT," +
                    "title VARCHAR(255) NOT NULL," +
                    "description TEXT," +
                    "due_date VARCHAR(20)," +
                    "priority VARCHAR(20)," +
                    "completed BOOLEAN," +
                    "FOREIGN KEY (todolist_id) REFERENCES todolist(id))";
            statement.executeUpdate(createTaskTable);
        }
    }

    private static void createToDoList(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter ToDo List Name: ");
        String listName = scanner.nextLine();

        String insertListQuery = "INSERT INTO todolist (name) VALUES (?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertListQuery, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, listName);
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("ToDo List created successfully.");

                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int listId = generatedKeys.getInt(1);
                        manageTasks(connection, listId);
                    }
                }
            } else {
                System.out.println("Error creating ToDo List.");
            }
        }
    }

    private static void manageTasks(Connection connection, int listId) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. Add Task\n2. View Tasks\n3. Back to Main Menu");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    addTask(connection, listId);
                    break;
                case 2:
                    viewTasks(connection, listId);
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void addTask(Connection connection, int listId) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Task Title: ");
        String title = scanner.nextLine();
        System.out.print("Enter Task Description: ");
        String description = scanner.nextLine();
        System.out.print("Enter Due Date (optional): ");
        String dueDate = scanner.nextLine();
        System.out.print("Enter Priority (high/medium/low): ");
        String priority = scanner.nextLine();

        String insertTaskQuery = "INSERT INTO task (todolist_id, title, description, due_date, priority, completed) " +
                "VALUES (?, ?, ?, ?, ?, false)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertTaskQuery)) {
            preparedStatement.setInt(1, listId);
            preparedStatement.setString(2, title);
            preparedStatement.setString(3, description);
            preparedStatement.setString(4, dueDate);
            preparedStatement.setString(5, priority);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Task added successfully.");
            } else {
                System.out.println("Error adding task.");
            }
        }
    }

    private static void viewTasks(Connection connection, int listId) throws SQLException {
        String selectTasksQuery = "SELECT * FROM task WHERE todolist_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectTasksQuery)) {
            preparedStatement.setInt(1, listId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<Task> tasks = new ArrayList<>();
                while (resultSet.next()) {
                    Task task = new Task();
                    task.setId(resultSet.getInt("id"));
                    task.setTitle(resultSet.getString("title"));
                    task.setDescription(resultSet.getString("description"));
                    task.setDueDate(resultSet.getString("due_date"));
                    task.setPriority(resultSet.getString("priority"));
                    task.setCompleted(resultSet.getBoolean("completed"));
                    tasks.add(task);
                }

                if (tasks.isEmpty()) {
                    System.out.println("No tasks found for this list.");
                } else {
                    for (Task task : tasks) {
                        System.out.println(task);
                    }
                }
            }
        }
    }

    private static void viewToDoLists(Connection connection) throws SQLException {
        String selectListsQuery = "SELECT * FROM todolist";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectListsQuery)) {

            List<ToDoList> toDoLists = new ArrayList<>();
            while (resultSet.next()) {
                ToDoList toDoList = new ToDoList();
                toDoList.setId(resultSet.getInt("id"));
                toDoList.setName(resultSet.getString("name"));
                toDoLists.add(toDoList);
            }

            if (toDoLists.isEmpty()) {
                System.out.println("No ToDo Lists found.");
            } else {
                for (ToDoList toDoList : toDoLists) {
                    System.out.println(toDoList);
                }

                System.out.print("Enter ToDo List ID to manage tasks (or 0 to go back): ");
                Scanner scanner = new Scanner(System.in);
                int listId = scanner.nextInt();

                if (listId != 0) {
                    manageTasks(connection, listId);
                }
            }
        }
    }
}