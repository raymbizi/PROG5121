# PROG5121
This repository will my Programming 1A project files. 

Messaging App
Java console app with login, message validation, JSON storage, and full message management.
Features
- User registration (username ≤5 chars with `_`, strong password, SA cell +27...)
- Login with validation messages
- Send/disregard/store messages (250 char limit, recipient validation)
- Auto-generated 10-digit Message ID and hash (format: `XX:count:FIRSTLAST` uppercase)
- Persistent JSON storage using Gson
- Stored messages menu:
  - Show sender/recipient
  - Longest message
  - Search by ID or recipient
  - Delete by hash
  - Full stored messages report
- Unit tests for all features

Tech Stack
- Java 8+
- Gson 2.10.1

Setup
1. Download `gson-2.10.1.jar` from [here](https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar)
2. Place the JAR in the `src/` folder alongside the `.java` files
3. Open terminal in `src/`

Compile
javac -cp ".;gson-2.10.1.jar" *.java

# Run
java -cp ".;gson-2.10.1.jar" Main
