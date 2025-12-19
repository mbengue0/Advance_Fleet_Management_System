=================================================
FLEET MANAGEMENT SYSTEM - SETUP INSTRUCTIONS
=================================================

1. DATABASE SETUP
   - Ensure MySQL Server is running on localhost:3306.
   - Create a database named 'fleet_db'.
   - Update 'src/main/java/com/fleetapp/util/DatabaseConnection.java' with your MySQL username and password.

2. FIRST RUN
   - Run 'com.fleetapp.Launcher'.
   - The application will automatically create all necessary tables.
   - A Default Super Admin account will be created automatically.

3. LOGIN CREDENTIALS
   - Username: admin
   - Password: 1234
   - Role: SUPER_ADMIN (Full Access)

=================================================