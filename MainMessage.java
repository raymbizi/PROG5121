public class Main {

    public static void main(String[] args) throws Exception {

        // ── REGISTRATION ──────────────────────────────────────
        System.out.println("REGISTRATION");

        Login login = new Login();
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
        System.out.println("\n>> " + regResult + "\n");

        if (!regResult.equals("Registration successful.")) {
            System.out.println("Registration failed. Exiting.");
            return;
        }

        // ── LOGIN ─────────────────────────────────────────────
        System.out.println("===== LOGIN =====");
        System.out.print("Enter username: ");
        login.setUsername(readLine());
        System.out.print("Enter password: ");
        login.setPassword(readLine());
        String loginStatus = login.returnLoginStatus();
        System.out.println("\n>> " + loginStatus + "\n");

        if (!login.loginUser()) {
            System.out.println("Login failed. Exiting.");
            return;
        }

        // ── QUICKCHAT MENU ────────────────────────────────────
        System.out.println("Welcome to QuickChat.");
        System.out.print("\nHow many messages do you want to send? ");
        int numMessages = Integer.parseInt(readLine().trim());
        boolean running = true;
        while (running) {
            System.out.println("\nMenu:");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.print("Choose an option: ");
            String choice = readLine().trim();

            switch (choice) {
                case "1":
                    sendMessages(numMessages);
                    break;
                case "2":
                    System.out.println("Coming Soon.");
                    break;
                case "3":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option, please try again.");
            }
        }

        // Total messages sent
        System.out.println("\nTotal messages sent: " + getTotalSent());
    }

    // ── Send N messages ───────────────────────────────────────
    private static int lastTotal = 0;

    private static void sendMessages(int count) throws Exception {
        for (int i = 0; i < count; i++) {
            System.out.println("\n--- Message " + (i + 1) + " of " + count + " ---");
            Message msg = new Message();

            // Recipient
            String recipient;
            while (true) {
                System.out.print("Enter recipient cell number: ");
                recipient = readLine();
                msg.setRecipient(recipient);
                String check = msg.checkRecipientCell();
                System.out.println(">> " + check);
                if (check.equals("Cell phone number successfully captured.")) break;
            }
            // Message text
            String text;
            while (true) {
                System.out.print("Enter message (max 250 chars): ");
                text = readLine();
                if (text.length() <= 250) {
                    System.out.println(">> Message ready to send.");
                    break;
                } else {
                    int over = text.length() - 250;
                    System.out.println(">> Message exceeds 250 characters by " + over
                            + "; please reduce the size.");
                }
            }
            msg.setMessage(text);
            // Generate hash
            String hash = msg.createMessageHash();
            // Show message ID
            System.out.println(">> Message ID generated: " + msg.getMessageID());
            // Send options
            System.out.println("\nWhat would you like to do?");
            System.out.println("1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message to send later");
            System.out.print("Choose: ");
            int sendChoice = Integer.parseInt(readLine().trim());
            String result = msg.sentMessage(sendChoice);
            System.out.println(">> " + result);
            // Display full details after send
            System.out.println("\n--- Message Details ---");
            System.out.println("Message ID   : " + msg.getMessageID());
            System.out.println("Message Hash : " + hash);
            System.out.println("Recipient    : " + msg.getRecipient());
            System.out.println("Message      : " + msg.getMessage());
            lastTotal = msg.returnTotalMessages();
        }
    }

    private static int getTotalSent() { return lastTotal; }

    // ── readLine helper ───────────────────────────────────────
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
}
