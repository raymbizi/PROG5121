public class Login {

    // registration data
    private String registeredUsername;
    private String registeredPassword;
    private String registeredCellPhone;
    private String firstName;
    private String lastName;

    // Current input
    private String username;
    private String password;
    private String cellPhoneNumber;

    // Constructor
    public Login() {}

    // Data Setters
    public void setUsername(String username)         { this.username = username; }
    public void setPassword(String password)         { this.password = password; }
    public void setCellPhoneNumber(String cellPhone) { this.cellPhoneNumber = cellPhone; }
    public void setFirstName(String firstName)       { this.firstName = firstName; }
    public void setLastName(String lastName)         { this.lastName = lastName; }

    // Getters
    public String getUsername()   { return username; }
    public String getPassword()   { return password; }
    public String getFirstName()  { return firstName; }
    public String getLastName()   { return lastName; }

    // Validation
    //Username must contain an underscore and be 5 characters or fewer.

    public boolean checkUserName() {
        if (username == null || username.length() > 5) return false;
        for (char c : username.toCharArray()) {
            if (c == '_') return true;
        }
        return false;
    }

    //Password Requirements
    public boolean checkPasswordComplexity() {
        if (password == null || password.length() < 8) return false;

        boolean hasUpper   = false;
        boolean hasDigit   = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (c >= 'A' && c <= 'Z')                         hasUpper   = true;
            else if (c >= '0' && c <= '9')                    hasDigit   = true;
            else if (!Character.isLetterOrDigit(c))           hasSpecial = true;
        }
        return hasUpper && hasDigit && hasSpecial;
    }

    //South African cell number must: start with "+27" be followed by 9 digits (total length = 12: +27 + 9 digits)

    public boolean checkCellPhoneNumber() {
        if (cellPhoneNumber == null) return false;
        if (cellPhoneNumber.length() != 12) return false;

        // First three characters must be '+', '2', '7'
        if (cellPhoneNumber.charAt(0) != '+') return false;
        if (cellPhoneNumber.charAt(1) != '2') return false;
        if (cellPhoneNumber.charAt(2) != '7') return false;

        // Remaining 9 characters must all be digits
        for (int i = 3; i < cellPhoneNumber.length(); i++) {
            char c = cellPhoneNumber.charAt(i);
            if (c < '0' || c > '9') return false;
        }
        return true;
    }
    public String registerUser() {
        if (!checkUserName()) {
            return "Username is not correctly formatted; "
                    + "please ensure that your username contains an underscore "
                    + "and is no more than five characters in length.";
        }
        if (!checkPasswordComplexity()) {
            return "Password is not correctly formatted; "
                    + "please ensure that the password contains at least "
                    + "eight characters, a capital letter, a number, "
                    + "and a special character.";
        }
        if (!checkCellPhoneNumber()) {
            return "Cell number is incorrectly formatted or does not "
                    + "contain an international code; "
                    + "please correct the number and try again.";
        }

        registeredUsername  = username;
        registeredPassword  = password;
        registeredCellPhone = cellPhoneNumber;

        return "Registration successful.";
    }

    //login section 
    public boolean loginUser() {
        return registeredUsername != null
                && registeredPassword != null
                && registeredUsername.equals(username)
                && registeredPassword.equals(password);
    }

    public String returnLoginStatus() {
        if (loginUser()) {
            return "Welcome " + firstName + ", " + lastName
                    + " it is great to see you again.";
        }
        return "Username or password incorrect, please try again.";
    }
}