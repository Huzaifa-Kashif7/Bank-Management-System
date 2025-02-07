package bank.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.util.Date;

public class WithDrawal extends JFrame implements ActionListener {
    String pin;
    TextField textField;

    JButton b1, b2;

    WithDrawal(String pin) {
        this.pin = pin;
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/atm2.png"));
        Image i2 = i1.getImage().getScaledInstance(1550, 830, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel l3 = new JLabel(i3);
        l3.setBounds(0, 0, 1550, 830);
        add(l3);

        JLabel label1 = new JLabel("MAXIMUM WITHDRAWAL IS RS. 25,000");
        label1.setForeground(Color.WHITE);
        label1.setFont(new Font("System", Font.BOLD, 16));
        label1.setBounds(460, 180, 700, 35);
        l3.add(label1);

        JLabel label2 = new JLabel("PLEASE ENTER YOUR AMOUNT");
        label2.setForeground(Color.WHITE);
        label2.setFont(new Font("System", Font.BOLD, 16));
        label2.setBounds(460, 220, 700, 35);
        l3.add(label2);

        textField = new TextField();
        textField.setBackground(new Color(65, 125, 128));
        textField.setForeground(Color.WHITE);
        textField.setBounds(460, 260, 320, 25);
        textField.setFont(new Font("Raleway", Font.BOLD, 22));
        l3.add(textField);

        b1 = new JButton("WITHDRAW");
        b1.setBounds(700, 362, 150, 35);
        b1.setBackground(new Color(65, 125, 128));
        b1.setForeground(Color.WHITE);
        b1.addActionListener(this);
        l3.add(b1);

        b2 = new JButton("BACK");
        b2.setBounds(700, 406, 150, 35);
        b2.setBackground(new Color(65, 125, 128));
        b2.setForeground(Color.WHITE);
        b2.addActionListener(this);
        l3.add(b2);

        setLayout(null);
        setSize(1550, 1080);
        setLocation(0, 0);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == b1) { // Withdraw button logic
            try {
                String inputAmount = textField.getText().trim();

                // Validate that the input is a non-negative number
                if (inputAmount.isEmpty() || !inputAmount.matches("\\d+")) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid non-negative amount.");
                    return;
                }

                int amount = Integer.parseInt(inputAmount);

                // Validate the amount range
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(null, "Withdrawal amount must be greater than zero.");
                    return;
                } else if (amount > 25000) {
                    JOptionPane.showMessageDialog(null, "The maximum withdrawal amount is Rs. 25,000.");
                    return;
                }

                _Con c = new _Con();
                c.connection.setAutoCommit(false); // Start transaction

                // Use FOR UPDATE to lock the rows for this PIN
                ResultSet resultSet = c.statement.executeQuery("select * from bank where pin = '" + pin + "' FOR UPDATE");
                int balance = 0;

                // Calculate the current balance
                while (resultSet.next()) {
                    if (resultSet.getString("type").equals("Deposit")) {
                        balance += Integer.parseInt(resultSet.getString("amount"));
                    } else if (resultSet.getString("type").equals("Withdrawal")) {
                        balance -= Integer.parseInt(resultSet.getString("amount"));
                    }
                }

                if (balance < amount) {
                    JOptionPane.showMessageDialog(null, "Insufficient Balance");
                    c.connection.rollback(); // Rollback transaction
                    return;
                }

                // Record the withdrawal
                Date date = new Date();
                c.statement.executeUpdate("insert into bank values('" + pin + "', '" + date + "', 'Withdrawal', '" + amount + "')");
                c.connection.commit(); // Commit transaction

                JOptionPane.showMessageDialog(null, "Rs. " + amount + " Debited Successfully");
                setVisible(false);
                new main_Class(pin);

            } catch (Exception ex) {
                ex.printStackTrace();
                try {
                    _Con c = new _Con();
                    c.connection.rollback(); // Rollback in case of exception
                } catch (Exception rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            } finally {
                try {
                    _Con c = new _Con();
                    c.connection.setAutoCommit(true); // Reset auto-commit
                } catch (Exception resetEx) {
                    resetEx.printStackTrace();
                }
            }
        } else if (e.getSource() == b2) { // Back button logic
            setVisible(false);
            new main_Class(pin);
        }
    }

    public static void main(String[] args) {
        new WithDrawal("");
    }
}