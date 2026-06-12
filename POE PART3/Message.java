import java.io.*;
import java.nio.file.*;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class Message {

    private long messageID;
    private int sequenceNumber;
    private String sender;          // sender's cell number (logged-in user)
    private String recipient;
    private String message;
    private String messageHash;

    // Static arrays for Part 3 requirements
    private static ArrayList<Message> sentMessages = new ArrayList<>();
    private static ArrayList<Message> disregardedMessages = new ArrayList<>();
    private static ArrayList<Message> storedMessages = new ArrayList<>();
    private static ArrayList<String> messageHashes = new ArrayList<>();
    private static ArrayList<String> messageIDs = new ArrayList<>();

    private static int messageCounter = 0;
    private static String currentSender = "";  // set after login

    private static final String STORAGE_FILE = "messages.json";
    private static final Random RANDOM = new Random();

    public Message() {
        this.messageID = generateMessageID();
        this.sequenceNumber = ++messageCounter;
        this.sender = currentSender;
    }

    private long generateMessageID() {
        return 1_000_000_000L + (long)(RANDOM.nextDouble() * 9_000_000_000L);
    }

    // Setters
    public void setRecipient(String recipient) { this.recipient = recipient; }
    public void setMessage(String message)     { this.message = message; }

    // Getters
    public long getMessageID()   { return messageID; }
    public int getSequenceNumber() { return sequenceNumber; }
    public String getSender()    { return sender; }
    public String getRecipient() { return recipient; }
    public String getMessage()   { return message; }
    public String getMessageHash() { return messageHash; }

    public boolean checkMessageID() {
        return String.valueOf(messageID).length() <= 10;
    }

    public String checkRecipientCell() {
        if (recipient == null) {
            return "Cell phone number is incorrectly formatted or does not contain an international code. " +
                    "Please correct the number and try again.";
        }
        boolean valid = false;
        if (recipient.length() == 12 && recipient.startsWith("+27")) {
            valid = true;
            for (int i = 3; i < 12; i++) {
                if (!Character.isDigit(recipient.charAt(i))) { valid = false; break; }
            }
        } else if (recipient.length() == 10 && recipient.startsWith("0")) {
            valid = true;
            for (int i = 1; i < 10; i++) {
                if (!Character.isDigit(recipient.charAt(i))) { valid = false; break; }
            }
        }
        return valid ? "Cell phone number successfully captured."
                : "Cell phone number is incorrectly formatted or does not contain an international code. " +
                  "Please correct the number and try again.";
    }

    public String createMessageHash() {
        if (message == null || message.trim().isEmpty()) return "";
        String idStr = String.valueOf(messageID);
        String twoDigits = idStr.length() >= 2 ? idStr.substring(0, 2) : idStr;
        String[] words = message.trim().split("\\s+");
        String firstWord = words[0];
        String lastWord = words[words.length - 1].replaceAll("[^A-Za-z0-9]", "");
        messageHash = (twoDigits + ":" + sequenceNumber + ":" + firstWord + lastWord).toUpperCase();
        return messageHash;
    }

    public String sentMessage(int choice) {
        // Add to respective arrays and hash/ID lists
        messageHashes.add(messageHash);
        messageIDs.add(String.valueOf(messageID));

        switch (choice) {
            case 1: // Send
                sentMessages.add(this);
                return "Message successfully sent.";
            case 2: // Disregard
                disregardedMessages.add(this);
                return "Press 0 to delete the message.";
            case 3: // Store
                storedMessages.add(this);
                saveStoredMessagesToJSON();
                return "Message successfully stored.";
            default:
                return "Invalid option.";
        }
    }

    // ------ Static methods for Part 3 ------

    public static void setCurrentSender(String sender) {
        currentSender = sender;
    }

    // Load stored messages from JSON into storedMessages array
    public static void loadStoredMessagesFromJSON() {
        storedMessages.clear();
        Path path = Paths.get(STORAGE_FILE);
        if (!Files.exists(path)) return;
        try {
            String json = new String(Files.readAllBytes(path));
            Gson gson = new Gson();
            java.lang.reflect.Type type = new TypeToken<ArrayList<Message>>(){}.getType();
            ArrayList<Message> loaded = gson.fromJson(json, type);
            if (loaded != null) {
                storedMessages.addAll(loaded);
            }
        } catch (IOException e) {
            System.out.println("Could not load stored messages: " + e.getMessage());
        }
    }

    private static void saveStoredMessagesToJSON() {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(storedMessages);
            Files.write(Paths.get(STORAGE_FILE), json.getBytes());
        } catch (IOException e) {
            System.out.println("Could not save stored messages: " + e.getMessage());
        }
    }

    // a) Display sender & recipient of all stored messages
    public static String displaySenderAndRecipientOfStored() {
        if (storedMessages.isEmpty()) return "No stored messages.";
        StringBuilder sb = new StringBuilder();
        for (Message msg : storedMessages) {
            sb.append("Sender: ").append(msg.sender)
                    .append(" | Recipient: ").append(msg.recipient).append("\n");
        }
        return sb.toString();
    }

    // b) Longest stored message
    public static String displayLongestMessage() {
        if (storedMessages.isEmpty()) return "No stored messages.";
        Message longest = storedMessages.get(0);
        for (Message msg : storedMessages) {
            if (msg.message.length() > longest.message.length()) longest = msg;
        }
        return "ID: " + longest.messageID + " | Hash: " + longest.messageHash +
                " | Message: " + longest.message;
    }

    // c) Search by message ID
    public static String searchByMessageID(long id) {
        for (Message msg : storedMessages) {
            if (msg.messageID == id) {
                return "Recipient: " + msg.recipient + " | Message: " + msg.message;
            }
        }
        return "No message found with ID " + id;
    }

    // d) Search all stored messages for a recipient
    public static String searchByRecipient(String recipient) {
        StringBuilder result = new StringBuilder();
        for (Message msg : storedMessages) {
            if (msg.recipient.equals(recipient)) {
                result.append("ID: ").append(msg.messageID)
                        .append(" | Hash: ").append(msg.messageHash)
                        .append(" | Message: ").append(msg.message).append("\n");
            }
        }
        return result.length() == 0 ? "No messages found for recipient " + recipient : result.toString();
    }

    // e) Delete a stored message using hash
    public static String deleteByHash(String hash) {
        Iterator<Message> it = storedMessages.iterator();
        while (it.hasNext()) {
            Message msg = it.next();
            if (msg.messageHash.equals(hash)) {
                it.remove();
                saveStoredMessagesToJSON();
                return "Message: \"" + msg.message + "\" successfully deleted.";
            }
        }
        return "No message found with hash " + hash;
    }

    // f) Display full report of all stored messages (as required by Part 3)
    public static String displayStoredMessagesReport() {
        if (storedMessages.isEmpty()) return "No stored messages.";
        StringBuilder sb = new StringBuilder();
        for (Message msg : storedMessages) {
            sb.append("Message Hash: ").append(msg.messageHash)
                    .append(" | Recipient: ").append(msg.recipient)
                    .append(" | Message: ").append(msg.message).append("\n");
        }
        return sb.toString();
    }

    // For main menu option 2: report of sent messages
    public static String displaySentMessagesReport() {
        if (sentMessages.isEmpty()) return "No sent messages.";
        StringBuilder sb = new StringBuilder();
        for (Message msg : sentMessages) {
            sb.append("Message Hash: ").append(msg.messageHash)
                    .append(" | Recipient: ").append(msg.recipient)
                    .append(" | Message: ").append(msg.message).append("\n");
        }
        return sb.toString();
    }

    public static int getTotalMessagesSent() {
        return sentMessages.size();
    }

    // For testing – reset all static state
    public static void resetAll() {
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageHashes.clear();
        messageIDs.clear();
        messageCounter = 0;
        currentSender = "";
    }

    // Helper for unit tests to pre-load test data
    public static void loadTestData() {
        resetAll();
        currentSender = "+27831234567"; // assume test sender

        // Message 1 (Sent)
        Message m1 = new Message();
        m1.setRecipient("+278348557896");
        m1.setMessage("Did you get the cake?");
        m1.createMessageHash();
        m1.sentMessage(1);

        // Message 2 (Stored)
        Message m2 = new Message();
        m2.setRecipient("+27838884567");
        m2.setMessage("Where are you? You are late! I have asked you to be on time.");
        m2.createMessageHash();
        m2.sentMessage(3);

        // Message 3 (Disregarded)
        Message m3 = new Message();
        m3.setRecipient("+27834884567");
        m3.setMessage("Yahoooo, I am at your gate.");
        m3.createMessageHash();
        m3.sentMessage(2);

        // Message 4 (Sent) – note recipient is 0838884567 (10 digits, no +)
        Message m4 = new Message();
        m4.setRecipient("0838884567");
        m4.setMessage("It is dinner time!");
        m4.createMessageHash();
        m4.sentMessage(1);

        // Message 5 (Stored)
        Message m5 = new Message();
        m5.setRecipient("+27838884567");
        m5.setMessage("Ok, I am leaving without you.");
        m5.createMessageHash();
        m5.sentMessage(3);
    }
}