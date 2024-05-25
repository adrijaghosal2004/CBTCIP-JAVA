import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.sql.*;


public class OnlineExamination implements ActionListener
{
    private static final String url = "jdbc:mysql://localhost:3306/onlineExam";
    private static final String username = "root";
    private static final String password = "Adrija123@";

    JFrame frame;
    JLabel label;
    JRadioButton[] radioButton = new JRadioButton[5];
    JButton btnNext,btnUpdateProfileandPassword,btnLogout;
    ButtonGroup bg;
    int count = 0, current = 0;
    Timer timer;
    JLabel timerLabel;
    int timeLeftInSeconds = 300;
    JFrame loginFrame;
    JTextField usernameField;
    JPasswordField passwordField;
    JButton loginButton;

    public String[][] questions =
    {
            {"Q1: Which of the following is the default package in Java?", "java.lang", "java.util", "java.net", "java.awt"},
            {"Q2: Which keyword is used for inheritance in Java?", "extend", "inherits", "inherits", "extends"},
            {"Q3: Which of the following is not a Java keyword?", "sync", "static", "transient", "volatile"},
            {"Q4: What is the use of the println() method?", "Prints the string inside quotes and moves the cursor to the beginning of the next line.", "Prints the string inside quotes without moving the cursor to the next line.", "Prints the string inside quotes and keeps the cursor at the end of the printed line.", "Prints the string inside quotes and moves the cursor to the beginning of the next line and flushes the stream."},
            {"Q5: Which method must be implemented by all threads?", "run()", "start()", "stop()", "yield()"},
            {"Q6: What is the output of the following code snippet? \nint x = 10; \nSystem.out.println(x++ + ++x);", "22", "23", "21", "24"},
            {"Q7: What does the 'final' keyword do in Java?", "It makes a variable, method, or class immutable or unchangeable.", "It specifies a method's signature." , "It initializes a variable.", "It enables method overloading."},
            {"Q8: Which of the following is a marker interface in Java?", "Serializable", "Runnable", "Cloneable", "Iterable"},
            {"Q9: Which of the following exception is thrown by read() method of InputStream class?", "IOException", "FileNotFoundException", "EOFException", "StreamException"},
            {"Q10: What is the output of the following code snippet? \nint x = 5; \nSystem.out.println(x >> 1);", "2", "5", "10", "1"}
    };

    public String[] correctAnswers = {"java.lang", "extends", "sync", "Prints the string inside quotes and moves the cursor to the beginning of the next line.","run()", "24", "It makes a variable, method, or class immutable or unchangeable.", "Serializable", "IOException", "2"};

    public OnlineExamination()
    {
        showLoginWindow();
    }

    void showMainExamWindow()
    {
        frame = new JFrame("Online Examination");
        frame.setLayout(null);

        label = new JLabel();
        label.setFont(new Font("Calibri", Font.BOLD, 20));
        frame.add(label);

        bg = new ButtonGroup();
        for (int i = 0; i < 5; i++)
        {
            radioButton[i] = new JRadioButton();
            frame.add(radioButton[i]);
            radioButton[i].setFont(new Font("Arial", Font.PLAIN, 16));
            bg.add(radioButton[i]);
        }

        btnNext = new JButton("Next");
        btnNext.addActionListener(this);
        frame.add(btnNext);


        setComponents();

        label.setBounds(30, 40, 800, 20);
        radioButton[0].setBounds(50, 80, 800, 20);
        radioButton[1].setBounds(50, 110, 800, 20);
        radioButton[2].setBounds(50, 140, 800, 20);
        radioButton[3].setBounds(50, 170, 800, 20);
        btnNext.setBounds(100, 240, 100, 30);


        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 350);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        timer = new Timer(1000, this);
        timer.start();

        // Initialize timerLabel
        timerLabel = new JLabel();
        timerLabel.setFont(new Font("Calibri", Font.BOLD, 16));
        timerLabel.setBounds(700, 10, 180, 20);
        frame.add(timerLabel);
        updateTimerLabel();
    }

    void showLoginWindow()
    {
        loginFrame = new JFrame("Login");
        loginFrame.setLayout(new GridLayout(3, 2));

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        Font labelFont = new Font("Arial", Font.PLAIN, 16);
        usernameLabel.setFont(labelFont);
        passwordLabel.setFont(labelFont);
        loginButton = new JButton("Login");

        loginButton.addActionListener(this);

        loginFrame.add(usernameLabel);
        loginFrame.add(usernameField);
        loginFrame.add(passwordLabel);
        loginFrame.add(passwordField);
        loginFrame.add(loginButton);

        JButton updateProfileButton = new JButton("Update Profile and Password");
        updateProfileButton.addActionListener(this);
        loginFrame.add(updateProfileButton);
        btnUpdateProfileandPassword = updateProfileButton;

        loginFrame.setSize(500, 200);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setVisible(true);
    }


    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == loginButton)
        {
            try
            {
                Class.forName("com.mysql.cj.jdbc.Driver");
            }
            catch (ClassNotFoundException ex)
            {
                System.out.println(ex.getMessage());
            }
            try
            {
                Connection connection = DriverManager.getConnection(url, OnlineExamination.username, OnlineExamination.password);
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (usernameExist(connection, username))
                {
                    if (checkPassword(connection, username, Integer.parseInt(password)))
                    {
                        loginFrame.dispose();
                        showMainExamWindow();
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(loginFrame, "Invalid password", "Login Failed", JOptionPane.ERROR_MESSAGE);
                        passwordField.setText("");
                    }
                }
                else
                {
                    String sql = "INSERT INTO students (username, password) VALUES (?, ?)";
                    try (PreparedStatement statement = connection.prepareStatement(sql))
                    {
                        statement.setString(1, username);
                        statement.setString(2, password);
                        int affectedRows = statement.executeUpdate();
                        if (affectedRows > 0) {
                            passwordField.setText("");
                            usernameField.setText("");
                            JOptionPane.showMessageDialog(loginFrame, "Account Created", "Register", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                    catch (SQLException ex)
                    {
                        System.out.println(ex.getMessage());
                    }
                }
            }
            catch (SQLException ex)
            {
                System.out.println(ex.getMessage());
            }
        }
        else if (e.getSource() == btnNext)
        {
            if (check())
                count = count + 1;
            current++;
            if (current == 10) {
                btnNext.setEnabled(false);
                JOptionPane.showMessageDialog(frame, "Your Score: " + count);

                int choice = JOptionPane.showOptionDialog(frame, "What would you like to do?", "Options",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                        new String[]{"Start Exam Again", "Logout"}, null);

                if (choice == JOptionPane.YES_OPTION)
                {
                    current = 0;
                    count = 0;
                    setComponents();
                    btnNext.setEnabled(true);
                    timer.restart();
                }
                else
                {
                    frame.dispose();
                    showLoginWindow();
                }
            }
            else
            {
                setComponents();
            }
        }
        else if (e.getSource() == btnUpdateProfileandPassword)
        {
            try
            {
                updateProfile();
            }
            catch (SQLException ex)
            {
                throw new RuntimeException(ex);
            }
        }
        else if (e.getSource() == btnLogout)
        {
            frame.dispose();
            showLoginWindow();
        }
        else if (e.getSource() == timer)
        {
            timeLeftInSeconds--;
            updateTimerLabel();
            if (timeLeftInSeconds <= 0)
            {
                timer.stop();
                btnNext.setEnabled(false);
                JOptionPane.showMessageDialog(frame, "Time's up! Your Score: " + count);
                frame.dispose();
                showLoginWindow();
            }
        }
    }


    void setComponents()
    {
        radioButton[4].setSelected(true);
        label.setText(questions[current][0]);
        for (int i = 0; i < 4; i++)
        {
            radioButton[i].setText(questions[current][i + 1]);
        }
    }
    void updateTimerLabel()
    {
        int minutes = timeLeftInSeconds / 60;
        int seconds = timeLeftInSeconds % 60;
        timerLabel.setText(String.format("Time Left: %02d:%02d", minutes, seconds));
    }

    boolean check()
    {
        int selectedOption = -1;
        for (int i = 0; i < radioButton.length; i++)
        {
            if (radioButton[i].isSelected())
            {
                selectedOption = i;
                break;
            }
        }
        return selectedOption != -1 && radioButton[selectedOption].getText().equals(correctAnswers[current]);
    }

    void updateProfile() throws SQLException
    {
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(url, OnlineExamination.username, OnlineExamination.password);
        } catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        String oldUsername = JOptionPane.showInputDialog(frame, "Enter old username:");
        String oldPassword = JOptionPane.showInputDialog(frame, "Enter old password:");

        if (checkPassword(connection, oldUsername, Integer.parseInt(oldPassword)))
        {
            Object[] options = {"Change Username", "Change Password", "Change Both"};
            int choice = JOptionPane.showOptionDialog(frame, "What would you like to change?", "Update Profile",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

            if (choice == 0 || choice == 2)
            {
                String newUsername = JOptionPane.showInputDialog(frame, "Enter new username:");
                if (newUsername != null && !newUsername.isEmpty())
                {
                    if (!usernameExist(connection, newUsername))
                    {
                        String sql =
                                "UPDATE students SET username = '" + newUsername + "' WHERE username = '" + oldUsername + "'";
                        try(Statement statement =connection.createStatement())
                        {
                            int affect= statement.executeUpdate(sql);
                            if(affect>0)
                            {
                                System.out.println("Username Updated Succesfully");
                                oldUsername=newUsername;
                            }
                        }
                        catch (SQLException e)
                        {
                            System.out.println(e.getMessage());
                        }
                        JOptionPane.showMessageDialog(frame, "Username updated successfully to: " + newUsername, "Update Profile", JOptionPane.INFORMATION_MESSAGE);
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(frame, "Username already exists", "Update Profile", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

            if (choice == 1 || choice == 2)
            {
                String newPasswordInput = JOptionPane.showInputDialog(frame, "Enter new password :");
                int newPassword = Integer.parseInt(newPasswordInput);
                if (newPassword >= 0)
                {
                    String sql =
                            "UPDATE students SET password = " + newPassword + " WHERE username = '" + oldUsername + "'";
                    try (Statement statement = connection.createStatement())
                    {
                        int affectedRows = statement.executeUpdate(sql);
                        if (affectedRows > 0)
                        {
                            System.out.println("Password Updated Successfully");
                            JOptionPane.showMessageDialog(frame, "Password updated successfully.", "Update Profile", JOptionPane.INFORMATION_MESSAGE);
                        }
                        else
                        {
                            System.out.println("No rows were updated.");
                        }
                    }
                    catch (SQLException e)
                    {
                        System.out.println(e.getMessage());
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(frame, "Invalid password. Please enter a non-negative integer.", "Update Profile", JOptionPane.ERROR_MESSAGE);
                }

            }
        }
        else
        {
            JOptionPane.showMessageDialog(frame, "Incorrect old username or password", "Update Profile", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static boolean usernameExist(Connection connection, String username)
    {
        try
        {
            String sql = "SELECT * FROM students WHERE username = '" + username + "'";
            try (Statement statement = connection.createStatement();
                 ResultSet rs = statement.executeQuery(sql))
            {
                return rs.next();
            }
        }
        catch (SQLException e)
        {
            System.out.println("Error checking if username exists: " + e.getMessage());
            return false;
        }
    }


    private static boolean checkPassword(Connection connection, String username, int password)
    {
        try
        {
            String sql = "SELECT * FROM students WHERE username = '" + username + "' AND password = '" + password + "'";
            try(Statement statement =connection.createStatement();
                ResultSet rs=statement.executeQuery(sql))
            {
                return rs.next();

            }
        }
        catch (SQLException e)
        {
            System.out.println("Invalid Password! Please Try Again");
            return false;
        }
    }

    public static void main(String [] args)
    {
        new OnlineExamination();
    }
}
