import java.io.*;

public class Main {

    public static void main(String[] args) throws Exception {
        // ---- REGISTRATION ----
        Login login = new Login();
        System.out.println("===== REGISTRATION =====");
        System.out.print("Enter first name: ");
        login.setFirstName(readLine());
        System.out.print("Enter last name: ");
        login.setLastName(readLine());
        System.out.print("Enter username (max 5 chars, must include _): ");
        login.setUsername(readLine());
        System.out.print("Enter password: ");
        login.setPassword(readLine());
        System.out.print("Enter SA cell phone number (e.g. +27831234567): ");
        login.setCellPhoneNumber(readLine());

        String regResult = login.registerUser();
        System.out.println("\n>> " + regResult);
        if (!regResult.equals("Registration successful.")) {
            System.out.println("Registration failed. Exiting.");
            return;
        }

        // ---- LOGIN ----
        System.out.println("\n===== LOGIN =====");
        System.out.print("Enter username: ");
        login.setUsername(readLine());
        System.out.print("Enter password: ");
        login.setPassword(readLine());
        String loginStatus = login.returnLoginStatus();
        System.out.println("\n>> " + loginStatus);
        if (!login.loginUser()) {
            System.out.println("Login failed. Exiting.");
            return;
        }

        // Set the current sender (logged-in user's cell number) for messages
        Message.setCurrentSender(login.getRegisteredCellPhone());

        // Load any previously stored messages from JSON into storedMessages array
        Message.loadStoredMessagesFromJSON();

        System.out.println("\nWelcome to QuickChat, " + login.getFirstName() + "!");

        // ---- MAIN MENU ----
        boolean running = true;
        while (running) {
            System.out.println("\nMenu:");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages (report)");
            System.out.println("3) Stored Messages");
            System.out.println("4) Quit");
            System.out.print("Choose an option: ");
            String choice = readLine().trim();

            switch (choice) {
                case "1":
                    sendMessages();
                    break;
                case "2":
                    System.out.println("\n" + Message.displaySentMessagesReport());
                    break;
                case "3":
                    storedMessagesMenu();
                    break;
                case "4":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option, please try again.");
            }
        }
        System.out.println("\nTotal messages sent: " + Message.getTotalMessagesSent());
    }

    private static void sendMessages() throws Exception {
        System.out.print("\nHow many messages do you want to send? ");
        int count;
        try {
            count = Integer.parseInt(readLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number, returning to menu.");
            return;
        }

        for (int i = 0; i < count; i++) {
            System.out.println("\n--- Message " + (i + 1) + " of " + count + " ---");
            Message msg = new Message();

            // Recipient validation
            while (true) {
                System.out.print("Enter recipient cell number: ");
                msg.setRecipient(readLine());
                String check = msg.checkRecipientCell();
                System.out.println(">> " + check);
                if (check.equals("Cell phone number successfully captured.")) break;
            }

            // Message text validation (max 250 chars)
            while (true) {
                System.out.print("Enter message (max 250 chars): ");
                String text = readLine();
                if (text.length() <= 250) {
                    msg.setMessage(text);
                    System.out.println(">> Message ready to send.");
                    break;
                } else {
                    int over = text.length() - 250;
                    System.out.println(">> Message exceeds 250 characters by " + over +
                            "; please reduce the size.");
                }
            }

            String hash = msg.createMessageHash();
            System.out.println(">> Message ID generated: " + msg.getMessageID());
            System.out.println(">> Message Hash: " + hash);

            System.out.println("\nWhat would you like to do?");
            System.out.println("1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message to send later");
            System.out.print("Choose: ");
            int sendChoice;
            try {
                sendChoice = Integer.parseInt(readLine().trim());
            } catch (NumberFormatException e) {
                sendChoice = -1;
            }
            String result = msg.sentMessage(sendChoice);
            System.out.println(">> " + result);
        }
    }

    private static void storedMessagesMenu() throws Exception {
        boolean back = false;
        while (!back) {
            System.out.println("\n----- Stored Messages -----");
            System.out.println("a) Display sender & recipient of all stored messages");
            System.out.println("b) Display the longest stored message");
            System.out.println("c) Search for a stored message by Message ID");
            System.out.println("d) Search stored messages by recipient");
            System.out.println("e) Delete a stored message using its hash");
            System.out.println("f) Display a full report of all stored messages");
            System.out.println("g) Back to main menu");
            System.out.print("Choose an option: ");
            String choice = readLine().trim().toLowerCase();

            switch (choice) {
                case "a":
                    System.out.println("\n" + Message.displaySenderAndRecipientOfStored());
                    break;
                case "b":
                    System.out.println("\nLongest stored message: " + Message.displayLongestMessage());
                    break;
                case "c":
                    System.out.print("Enter Message ID to search for: ");
                    try {
                        long id = Long.parseLong(readLine().trim());
                        System.out.println("\n" + Message.searchByMessageID(id));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Message ID.");
                    }
                    break;
                case "d":
                    System.out.print("Enter recipient cell number to search for: ");
                    String recipient = readLine().trim();
                    System.out.println("\n" + Message.searchByRecipient(recipient));
                    break;
                case "e":
                    System.out.print("Enter the message hash to delete: ");
                    String hash = readLine().trim();
                    System.out.println("\n" + Message.deleteByHash(hash));
                    break;
                case "f":
                    System.out.println("\n" + Message.displayStoredMessagesReport());
                    break;
                case "g":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option, please try again.");
            }
        }
    }

    private static String readLine() throws Exception {
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = System.in.read()) != -1) {
            if (c == '\n') break;
            if (c == '\r') {
                int next = System.in.read();
                if (next != '\n') sb.append((char) next);
                break;
            }
            sb.append((char) c);
        }
        return sb.toString();
    }
}