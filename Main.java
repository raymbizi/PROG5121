public class Main {

    public static void main(String[] args) throws Exception {
        Login login = new Login();

        //Place of Registration
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

        // ── LOGIN ─────────────────────────────────────────────
        System.out.println("\n===== LOGIN =====");

        System.out.print("Enter username: ");
        login.setUsername(readLine());

        System.out.print("Enter password: ");
        login.setPassword(readLine());

        System.out.println("\n>> " + login.returnLoginStatus());
    }

    // ═══════════════════════════════════════════════════════════
    //  HELPER — reads one line from System.in without imports
    // ═══════════════════════════════════════════════════════════

    private static String readLine() throws Exception {
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = System.in.read()) != -1) {
            if (c == '\n') break;           // Unix newline
            if (c == '\r') {                // Windows carriage-return
                int next = System.in.read();
                if (next != '\n') {         // lone \r — put the byte back
                    sb.append((char) next);
                }
                break;
            }
            sb.append((char) c);
        }
        return sb.toString();
    }
}