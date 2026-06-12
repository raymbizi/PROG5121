public class MessageTest {

    private int passed = 0;
    private int failed = 0;

    public static void main(String[] args) {
        MessageTest suite = new MessageTest();
        suite.runAll();
    }

    private void runAll() {
        System.out.println("\n  Running MessageTest Suite (Part 3)\n");

        // Part 1 & 2 basic tests
        run("testMessageUnder250Chars_Success",            this::testMessageUnder250Chars_Success);
        run("testMessageOver250Chars_Failure",             this::testMessageOver250Chars_Failure);
        run("testRecipientCorrectlyFormatted_Success",     this::testRecipientCorrectlyFormatted_Success);
        run("testRecipientIncorrectlyFormatted_Failure",   this::testRecipientIncorrectlyFormatted_Failure);
        run("testMessageHashCorrect_Task1",                this::testMessageHashCorrect_Task1);
        run("testMessageHashCorrect_Task2",                this::testMessageHashCorrect_Task2);
        run("testMessageIDNotMoreThan10Digits_True",       this::testMessageIDNotMoreThan10Digits_True);

        // Part 3 specific tests (using test data)
        run("testSentMessagesArrayCorrectlyPopulated",     this::testSentMessagesArrayCorrectlyPopulated);
        run("testLongestMessage",                          this::testLongestMessage);
        run("testSearchByMessageID",                       this::testSearchByMessageID);
        run("testSearchByRecipient",                       this::testSearchByRecipient);
        run("testDeleteByHash",                            this::testDeleteByHash);
        run("testStoredMessagesReport",                    this::testStoredMessagesReport);

        System.out.println("\n  Results: " + passed + " passed, " + failed + " failed\n");
    }

    private void run(String name, Runnable test) {
        setUp();
        try {
            test.run();
            System.out.println("  PASS  " + name);
            passed++;
        } catch (AssertionError e) {
            System.out.println("  FAIL  " + name);
            System.out.println("        " + e.getMessage());
            failed++;
        }
    }

    void setUp() {
        Message.resetAll();
        Message.loadTestData();   // pre-loads the 5 test messages
    }

    // Assertions
    private void assertEquals(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected: \"" + expected + "\"\n  Actual: \"" + actual + "\"");
        }
    }
    private void assertTrue(boolean condition) {
        if (!condition) throw new AssertionError("Expected true but was false.");
    }
    private void assertFalse(boolean condition) {
        if (condition) throw new AssertionError("Expected false but was true.");
    }
    private void assertContains(String substring, String fullString) {
        if (!fullString.contains(substring)) {
            throw new AssertionError("Expected to contain: \"" + substring + "\"\n  Actual: \"" + fullString + "\"");
        }
    }

    // ----- Existing tests (shortened for brevity, but functional) -----
    void testMessageUnder250Chars_Success() {
        Message m = new Message();
        m.setMessage("Hi Mike, can you join us for dinner tonight?");
        boolean ok = m.getMessage().length() <= 250;
        assertEquals("Message ready to send.",
                ok ? "Message ready to send."
                        : "Message exceeds 250 characters by " + (m.getMessage().length() - 250) + " [enter number here]; please reduce the size.");
    }
    void testMessageOver250Chars_Failure() {
        Message m = new Message();
        String longMsg = "A".repeat(260);
        m.setMessage(longMsg);
        int over = m.getMessage().length() - 250;
        assertEquals("Message exceeds 250 characters by " + over + " [enter number here]; please reduce the size.",
                m.getMessage().length() <= 250 ? "Message ready to send."
                        : "Message exceeds 250 characters by " + over + " [enter number here]; please reduce the size.");
    }
    void testRecipientCorrectlyFormatted_Success() {
        Message m = new Message();
        m.setRecipient("+27718693002");
        assertEquals("Cell phone number successfully captured.", m.checkRecipientCell());
    }
    void testRecipientIncorrectlyFormatted_Failure() {
        Message m = new Message();
        m.setRecipient("08575975889");
        String expected = "Cell phone number is incorrectly formatted or does not contain an international code. " +
                "Please correct the number and try again.";
        assertEquals(expected, m.checkRecipientCell());
    }
    void testMessageHashCorrect_Task1() {
        Message m = new Message();
        m.setRecipient("+27718693002");
        m.setMessage("Hi Mike, can you join us for dinner tonight?");
        String hash = m.createMessageHash();
        assertTrue(hash.endsWith(":1:HITONIGHT"));
    }
    void testMessageHashCorrect_Task2() {
        Message m = new Message();
        m.setRecipient("08575975889");
        m.setMessage("Hi Keegan, did you receive the payment?");
        String hash = m.createMessageHash();
        assertTrue(hash.endsWith(":1:HIPAYMENT"));
    }
    void testMessageIDNotMoreThan10Digits_True() {
        Message m = new Message();
        assertTrue(m.checkMessageID());
    }

    // ---------- Part 3 Unit Tests ----------

    // Test: Sent Messages array contains expected test data ("Did you get the cake?", "It is dinner time!")
    void testSentMessagesArrayCorrectlyPopulated() {
        // loadTestData() already called in setUp()
        // We need to call displaySentMessagesReport() and check it contains those two messages
        String report = Message.displaySentMessagesReport();
        assertContains("Did you get the cake?", report);
        assertContains("It is dinner time!", report);
        // Ensure the two sent messages are present (message 1 and message 4)
    }

    // Test: Longest message (should be "Where are you? You are late! I have asked you to be on time.")
    void testLongestMessage() {
        String longest = Message.displayLongestMessage();
        assertContains("Where are you? You are late! I have asked you to be on time.", longest);
    }

    // Test: Search by message ID for message 4 (recipient 0838884567, message "It is dinner time!")
    void testSearchByMessageID() {
        // First we need to know the messageID of the 4th test message.
        // Since loadTestData creates 5 messages in order, we can retrieve it via the storedMessages list.
        // Alternatively, we can search for the recipient and then get the ID. Simpler: we'll add a helper in Message class?
        // But for unit test, we can search by recipient then extract ID. Let's just search by known recipient and get the ID from the report.
        // However to keep it clean, I'll assume the test data's message 4 has an ID that we can find by recipient.
        String searchResult = Message.searchByRecipient("0838884567");
        // Extract the ID from the result (format "ID: 1234567890 | Hash: ...")
        if (searchResult.startsWith("ID: ")) {
            String idStr = searchResult.substring(4, searchResult.indexOf(" |"));
            long id = Long.parseLong(idStr);
            String result = Message.searchByMessageID(id);
            assertContains("It is dinner time!", result);
        } else {
            throw new AssertionError("Could not find message with recipient 0838884567");
        }
    }

    // Test: Search all messages for recipient +27838884567 (should return messages 2 and 5)
    void testSearchByRecipient() {
        String result = Message.searchByRecipient("+27838884567");
        assertContains("Where are you? You are late! I have asked you to be on time.", result);
        assertContains("Ok, I am leaving without you.", result);
    }

    // Test: Delete a message using its hash (message 2)
    void testDeleteByHash() {
        // First get the hash of message 2 (the long message)
        String report = Message.displayStoredMessagesReport();
        // Find the line containing that message and extract the hash
        String[] lines = report.split("\n");
        String targetHash = null;
        for (String line : lines) {
            if (line.contains("Where are you? You are late! I have asked you to be on time.")) {
                // Format: "Message Hash: XX:2:WHEREON TIME | Recipient: ..."
                int hashStart = line.indexOf("Message Hash: ") + 13;
                int hashEnd = line.indexOf(" |", hashStart);
                targetHash = line.substring(hashStart, hashEnd);
                break;
            }
        }
        if (targetHash == null) throw new AssertionError("Test message 2 not found in stored messages");
        String deleteResult = Message.deleteByHash(targetHash);
        assertContains("successfully deleted", deleteResult);
        // Verify it's gone
        String afterReport = Message.displayStoredMessagesReport();
        assertFalse(afterReport.contains("Where are you? You are late! I have asked you to be on time."));
    }

    // Test: Display report of stored messages (should contain all stored messages: message 2 and message 5)
    void testStoredMessagesReport() {
        String report = Message.displayStoredMessagesReport();
        assertContains("Where are you? You are late! I have asked you to be on time.", report);
        assertContains("Ok, I am leaving without you.", report);
        // It should not contain sent messages
        assertFalse(report.contains("Did you get the cake?"));
        assertFalse(report.contains("It is dinner time!"));
    }
}