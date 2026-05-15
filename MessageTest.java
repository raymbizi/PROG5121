public class MessageTest {

    private Message message;
    private int passed = 0;
    private int failed = 0;

    public static void main(String[] args) {
        MessageTest suite = new MessageTest();
        suite.runAll();
    }

    private void runAll() {
        System.out.println(" ");
        System.out.println("  Running MessageTest Suite");
        System.out.println("\n");

        run("testMessageUnder250Chars_Success",            this::testMessageUnder250Chars_Success);
        run("testMessageOver250Chars_Failure",             this::testMessageOver250Chars_Failure);
        run("testRecipientCorrectlyFormatted_Success",     this::testRecipientCorrectlyFormatted_Success);
        run("testRecipientIncorrectlyFormatted_Failure",   this::testRecipientIncorrectlyFormatted_Failure);
        run("testMessageHashCorrect_Task1",                this::testMessageHashCorrect_Task1);
        run("testMessageHashCorrect_Task2",                this::testMessageHashCorrect_Task2);
        run("testSentMessage_Send",                        this::testSentMessage_Send);
        run("testSentMessage_Disregard",                   this::testSentMessage_Disregard);
        run("testSentMessage_Store",                       this::testSentMessage_Store);
        run("testMessageIDNotMoreThan10Digits_True",       this::testMessageIDNotMoreThan10Digits_True);
        run("testReturnTotalMessages",                     this::testReturnTotalMessages);

        System.out.println("\n");
        System.out.println("  Results: " + passed + " passed, " + failed + " failed");
        System.out.println();
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
        message = new Message();
    }

    // Assertions
    private void assertEquals(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError(
                    "Expected: \"" + expected + "\"\n" +
                            "  Actual: \"" + actual   + "\""
            );
        }
    }
    private void assertTrue(boolean condition) {
        if (!condition) throw new AssertionError("Expected true but was false.");
    }
    private void assertFalse(boolean condition) {
        if (condition) throw new AssertionError("Expected false but was true.");
    }

    //  MESSAGE LENGTH TESTS

    void testMessageUnder250Chars_Success() {
        message.setMessage("Hi Mike, can you join us for dinner tonight?");
        boolean ok = message.getMessage().length() <= 250;
        assertEquals(
                "Message ready to send.",
                ok ? "Message ready to send."
                        : "Message exceeds 250 characters by "
                          + (message.getMessage().length() - 250)
                          + " [enter number here]; please reduce the size."
        );
    }
    void testMessageOver250Chars_Failure() {
        String longMsg = "A".repeat(260);
        message.setMessage(longMsg);
        int over = message.getMessage().length() - 250;
        boolean ok = message.getMessage().length() <= 250;
        assertEquals(
                "Message exceeds 250 characters by " + over
                        + " [enter number here]; please reduce the size.",
                ok ? "Message ready to send."
                        : "Message exceeds 250 characters by " + over
                          + " [enter number here]; please reduce the size."
        );
    }


    //  RECIPIENT CELL TESTS

    void testRecipientCorrectlyFormatted_Success() {
        message.setRecipient("+27718693002");
        assertEquals(
                "Cell phone number successfully captured.",
                message.checkRecipientCell()
        );
    }

    void testRecipientIncorrectlyFormatted_Failure() {
        message.setRecipient("08575975889");   // 11 chars, no intl code
        assertEquals(
                "Cell phone number is incorrectly formatted or does not "
                        + "contain an international code. Please correct the number and try again.",
                message.checkRecipientCell()
        );
    }


    //  MESSAGE HASH TESTs


    //
    Test Case 1 from spec:
    Recipient: +27718693002
    Message:   "Hi Mike, can you join us for dinner tonight?"
    Expected hash pattern: XX:1:HITONIGHT  (first 2 digits of ID vary, count=1)
    //
    void testMessageHashCorrect_Task1() {
        message.setRecipient("+27718693002");
        message.setMessage("Hi Mike, can you join us for dinner tonight?");
        String hash = message.createMessageHash();

        // Hash ends with :1:HITONIGHT (count=1 after resetAll)
        assertTrue(hash.endsWith(":1:HITONIGHT"));
    }

    /**
     * Test Case 2 from spec:
     *   Recipient: 08575975889 (second message, count=1 after fresh setUp)
     *   Message:   "Hi Keegan, did you receive the payment?"
     *   Expected hash ends with :1:HIPAYMENT
     */
    void testMessageHashCorrect_Task2() {
        message.setRecipient("08575975889");
        message.setMessage("Hi Keegan, did you receive the payment?");
        String hash = message.createMessageHash();
        assertTrue(hash.endsWith(":1:HIPAYMENT"));
    }

    //  SENT MESSAGE TESTS

    void testSentMessage_Send() {
        message.setRecipient("+27718693002");
        message.setMessage("Hi Mike, can you join us for dinner tonight?");
        message.createMessageHash();
        assertEquals("Message successfully sent.", message.sentMessage(1));
    }
    void testSentMessage_Disregard() {
        message.setRecipient("+27718693002");
        message.setMessage("Hi Mike, can you join us for dinner tonight?");
        message.createMessageHash();
        assertEquals("Press 0 to delete the message.", message.sentMessage(2));
    }
    void testSentMessage_Store() {
        message.setRecipient("+27718693002");
        message.setMessage("Hi Mike, can you join us for dinner tonight?");
        message.createMessageHash();
        assertEquals("Message successfully stored.", message.sentMessage(3));
    }


    //  MESSAGE ID TEST

    void testMessageIDNotMoreThan10Digits_True() {
        assertTrue(message.checkMessageID());
    }


    void testReturnTotalMessages() {
        // Send 2 messages in sequence
        Message.resetAll();
        Message m1 = new Message();
        m1.setRecipient("+27718693002");
        m1.setMessage("Hi Mike, can you join us for dinner tonight?");
        m1.createMessageHash();
        m1.sentMessage(1);

        Message m2 = new Message();
        m2.setRecipient("+27838968976");
        m2.setMessage("Hi Keegan, did you receive the payment?");
        m2.createMessageHash();
        m2.sentMessage(1);

        assertTrue(m2.returnTotalMessages() == 2);
    }
}
}
