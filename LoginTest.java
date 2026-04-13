public class LoginTest {

    private Login login;
    private int passed = 0;
    private int failed = 0;

    public static void main(String[] args) {
        LoginTest suite = new LoginTest();
        suite.runAll();
    }

    private void runAll() {
        System.out.println(" ");
        System.out.println("  Running LoginTest Suite");
        System.out.println("\n");

        run("testUsernameCorrectlyFormatted_LoginMessage",    this::testUsernameCorrectlyFormatted_LoginMessage);
        run("testUsernameIncorrectlyFormatted_Message",       this::testUsernameIncorrectlyFormatted_Message);
        run("testPasswordMeetsComplexity_Message",            this::testPasswordMeetsComplexity_Message);
        run("testPasswordDoesNotMeetComplexity_Message",      this::testPasswordDoesNotMeetComplexity_Message);
        run("testCellPhoneCorrectlyFormatted_Message",        this::testCellPhoneCorrectlyFormatted_Message);
        run("testCellPhoneIncorrectlyFormatted_Message",      this::testCellPhoneIncorrectlyFormatted_Message);
        run("testLoginSuccessful_ReturnsTrue",                this::testLoginSuccessful_ReturnsTrue);
        run("testLoginFailed_ReturnsFalse",                   this::testLoginFailed_ReturnsFalse);
        run("testUsernameCorrectlyFormatted_ReturnsTrue",     this::testUsernameCorrectlyFormatted_ReturnsTrue);
        run("testUsernameIncorrectlyFormatted_ReturnsFalse",  this::testUsernameIncorrectlyFormatted_ReturnsFalse);
        run("testPasswordMeetsComplexity_ReturnsTrue",        this::testPasswordMeetsComplexity_ReturnsTrue);
        run("testPasswordDoesNotMeetComplexity_ReturnsFalse", this::testPasswordDoesNotMeetComplexity_ReturnsFalse);
        run("testCellPhoneCorrectlyFormatted_ReturnsTrue",    this::testCellPhoneCorrectlyFormatted_ReturnsTrue);
        run("testCellPhoneIncorrectlyFormatted_ReturnsFalse", this::testCellPhoneIncorrectlyFormatted_ReturnsFalse);

        System.out.println("\n");
        System.out.println("  Results: " + passed + " passed, " + failed + " failed");
        System.out.println("");
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
        login = new Login();
        login.setFirstName("Kyle");
        login.setLastName("Smith");
    }


//Assertion//
    private void assertEquals(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError(
                    "Expected: \"" + expected + "\"\n" +
                            "  Actual: \"" + actual + "\""
            );
        }
    }

    private void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected true but was false.");
        }
    }

    private void assertFalse(boolean condition) {
        if (condition) {
            throw new AssertionError("Expected false but was true.");
        }
    }

    // ═══════════════════════════════════════════════════════════
    //  assertEquals TESTS
    // ═══════════════════════════════════════════════════════════

    void testUsernameCorrectlyFormatted_LoginMessage() {
        login.setUsername("kyl_1");
        login.setPassword("Ch&sec@ke991");
        login.setCellPhoneNumber("+27838968976");
        login.registerUser();

        login.setUsername("kyl_1");
        login.setPassword("Ch&sec@ke991");

        assertEquals(
                "Welcome Kyle, Smith it is great to see you again.",
                login.returnLoginStatus()
        );
    }

    void testUsernameIncorrectlyFormatted_Message() {
        login.setUsername("kyle!!!!!!!");
        login.setPassword("Ch&sec@ke991");
        login.setCellPhoneNumber("+27838968976");

        assertEquals(
                "Username is not correctly formatted; "
                        + "please ensure that your username contains an underscore "
                        + "and is no more than five characters in length.",
                login.registerUser()
        );
    }

    void testPasswordMeetsComplexity_Message() {
        login.setUsername("kyl_1");
        login.setPassword("Ch&sec@ke991");

        assertEquals(
                "Password successfully captured.",
                login.checkPasswordComplexity()
                        ? "Password successfully captured."
                        : "Password is not correctly formatted; please ensure "
                          + "that the password contains at least eight characters, "
                          + "a capital letter, a number, and a special character."
        );
    }

    void testPasswordDoesNotMeetComplexity_Message() {
        login.setUsername("kyl_1");
        login.setPassword("password");

        assertEquals(
                "Password is not correctly formatted; please ensure "
                        + "that the password contains at least eight characters, "
                        + "a capital letter, a number, and a special character.",
                login.checkPasswordComplexity()
                        ? "Password successfully captured."
                        : "Password is not correctly formatted; please ensure "
                          + "that the password contains at least eight characters, "
                          + "a capital letter, a number, and a special character."
        );
    }

    void testCellPhoneCorrectlyFormatted_Message() {
        login.setCellPhoneNumber("+27838968976");

        assertEquals(
                "Cell number successfully captured.",
                login.checkCellPhoneNumber()
                        ? "Cell number successfully captured."
                        : "Cell number is incorrectly formatted or does not "
                          + "contain an international code; "
                          + "please correct the number and try again."
        );
    }

    void testCellPhoneIncorrectlyFormatted_Message() {
        login.setCellPhoneNumber("08966553");

        assertEquals(
                "Cell number is incorrectly formatted or does not "
                        + "contain an international code; "
                        + "please correct the number and try again.",
                login.checkCellPhoneNumber()
                        ? "Cell number successfully captured."
                        : "Cell number is incorrectly formatted or does not "
                          + "contain an international code; "
                          + "please correct the number and try again."
        );
    }

    // ═══════════════════════════════════════════════════════════
    //  assertTrue / assertFalse TESTS
    // ═══════════════════════════════════════════════════════════

    // ── Login ─────────────────────────────────────────────────
    void testLoginSuccessful_ReturnsTrue() {
        login.setUsername("kyl_1");
        login.setPassword("Ch&sec@ke991");
        login.setCellPhoneNumber("+27838968976");
        login.registerUser();

        login.setUsername("kyl_1");
        login.setPassword("Ch&sec@ke991");
        assertTrue(login.loginUser());
    }

    void testLoginFailed_ReturnsFalse() {
        login.setUsername("kyl_1");
        login.setPassword("Ch&sec@ke991");
        login.setCellPhoneNumber("+27838968976");
        login.registerUser();

        login.setUsername("kyl_1");
        login.setPassword("wrongPass1!");
        assertFalse(login.loginUser());
    }

    // Username tests
    void testUsernameCorrectlyFormatted_ReturnsTrue() {
        login.setUsername("kyl_1");
        assertTrue(login.checkUserName());
    }

    void testUsernameIncorrectlyFormatted_ReturnsFalse() {
        login.setUsername("kyle!!!!!!!");
        assertFalse(login.checkUserName());
    }

    // Password validation
    void testPasswordMeetsComplexity_ReturnsTrue() {
        login.setPassword("Ch&sec@ke991");
        assertTrue(login.checkPasswordComplexity());
    }

    void testPasswordDoesNotMeetComplexity_ReturnsFalse() {
        login.setPassword("password");
        assertFalse(login.checkPasswordComplexity());
    }
    //Cell Phone Validation
    void testCellPhoneCorrectlyFormatted_ReturnsTrue() {
        login.setCellPhoneNumber("+27838968976");
        assertTrue(login.checkCellPhoneNumber());
    }
    void testCellPhoneIncorrectlyFormatted_ReturnsFalse() {
        login.setCellPhoneNumber("08966553");
        assertFalse(login.checkCellPhoneNumber());
    }
}