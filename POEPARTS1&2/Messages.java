import java.util.ArrayList;
import java.util.Random;

public class Messages {

        private int    messageID;
        private int    numMessagesSent;
        private String recipient;
        private String message;
        private String messageHash;

        private static int          totalMessagesSent = 0;
        private static ArrayList<String> sentMessages = new ArrayList<>();

        // Constructor
        public Message() {
            this.messageID      = generateMessageID();
            this.numMessagesSent = ++totalMessagesSent;
        }

        // Helpers
        private int generateMessageID() {
            // 10-digit random number
            Random rand = new Random();
            long min = 1_000_000_000L;
            long max = 9_999_999_999L;
            return (int)((long)(rand.nextDouble() * (max - min)) + min);
        }

        // Setters
        public void setRecipient(String recipient) { this.recipient = recipient; }
        public void setMessage(String message)     { this.message   = message;   }

        // ── Getters
        public int    getMessageID()       { return messageID;       }
        public int    getNumMessagesSent() { return numMessagesSent; }
        public String getRecipient()       { return recipient;       }
        public String getMessage()         { return message;         }
        public String getMessageHash()     { return messageHash;     }

        // number Validation
        /**
         * Message ID must be no more than 10 characters (digits).
         */
        public boolean checkMessageID() {
            return String.valueOf(messageID).length() <= 10;
        }
        /**
         * Recipient cell must be no more than 10 characters long and start with a code.
         * Reuses Login.checkCellPhoneNumber() logic: starts with +27, total length 12.
         * The spec says "no more than ten characters" for the raw digits after the +,
         * so we validate length <= 12 and starts with a recognised code (+27 or 0).
         */
        public String checkRecipientCell() {
            if (recipient == null) {
                return "Cell phone number is incorrectly formatted or does not "
                        + "contain an international code. Please correct the number and try again.";
            }
            // Accept +27XXXXXXXXX (12 chars) or 0XXXXXXXXX (10 chars)
            boolean valid = false;
            if (recipient.length() == 12 && recipient.startsWith("+27")) {
                valid = true;
                for (int i = 3; i < recipient.length(); i++) {
                    if (!Character.isDigit(recipient.charAt(i))) { valid = false; break; }
                }
            } else if (recipient.length() == 10 && recipient.startsWith("0")) {
                valid = true;
                for (int i = 1; i < recipient.length(); i++) {
                    if (!Character.isDigit(recipient.charAt(i))) { valid = false; break; }
                }
            }

            if (valid) return "Cell phone number successfully captured.";
            return "Cell phone number is incorrectly formatted or does not "
                    + "contain an international code. Please correct the number and try again.";
        }

        /**
         * Creates hash: first two digits of messageID + ":" + numMessagesSent + ":" +
         * first word + last word of message — all in CAPS.
         * e.g. 00:0:HITHANKS
         */
        public String createMessageHash() {
            if (message == null || message.isEmpty()) return "";

            String idStr   = String.valueOf(messageID);
            String twoDigits = idStr.length() >= 2
                    ? idStr.substring(0, 2)
                    : idStr;

            String[] words = message.trim().split("\\s+");
            String firstWord = words[0];
            String lastWord  = words[words.length - 1];

            // Strip trailing punctuation from last word
            lastWord = lastWord.replaceAll("[^A-Za-z0-9]", "");

            messageHash = (twoDigits + ":" + numMessagesSent + ":" + firstWord + lastWord).toUpperCase();
            return messageHash;
        }

        /**
         * Lets user choose to Send, Disregard, or Store the message.
         * choice: 1 = Send, 2 = Disregard, 3 = Store
         */
        public String sentMessage(int choice) {
            switch (choice) {
                case 1:
                    sentMessages.add("Message ID: " + messageID
                            + " | Hash: " + messageHash
                            + " | To: " + recipient
                            + " | Message: " + message);
                    return "Message successfully sent.";
                case 2:
                    return "Press 0 to delete the message.";
                case 3:
                    storeMessage();
                    return "Message successfully stored.";
                default:
                    return "Invalid option.";
            }
        }

        /**
         * Returns all messages sent during this session.
         */
        public String printMessages() {
            if (sentMessages.isEmpty()) return "No messages sent yet.";
            StringBuilder sb = new StringBuilder();
            for (String m : sentMessages) {
                sb.append(m).append("\n");
            }
            return sb.toString().trim();
        }

        /**
         * Returns total number of messages sent.
         */
        public int returnTotalMessages() {
            return sentMessages.size();
        }

        /**
         * Stores message as a JSON entry in messages.json.
         * Research: uses basic file I/O to append JSON objects.
         */
        public void storeMessage() {
            String json = "{"
                    + "\"messageID\":" + messageID + ","
                    + "\"numMessagesSent\":" + numMessagesSent + ","
                    + "\"recipient\":\"" + recipient + "\","
                    + "\"message\":\"" + message + "\","
                    + "\"messageHash\":\"" + messageHash + "\""
                    + "}";
            try {
                java.nio.file.Path path = java.nio.file.Paths.get("messages.json");
                java.util.List<String> lines;
                if (java.nio.file.Files.exists(path)) {
                    lines = java.nio.file.Files.readAllLines(path);
                } else {
                    lines = new java.util.ArrayList<>();
                    lines.add("[]");
                }

                // Simple append inside JSON array
                String content = String.join("", lines).trim();
                if (content.equals("[]")) {
                    content = "[" + json + "]";
                } else {
                    content = content.substring(0, content.length() - 1) + "," + json + "]";
                }
                java.nio.file.Files.write(path, content.getBytes());
            } catch (Exception e) {
                System.out.println("Could not store message: " + e.getMessage());
            }
        }

        // ── Static reset (for testing) ────────────────────────────
        public static void resetAll() {
            totalMessagesSent = 0;
            sentMessages.clear();
        }
    }
}
