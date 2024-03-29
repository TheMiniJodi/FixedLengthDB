# File-based Database using fixed length records

This is a simple CLI file-based database. Currently, it is suited for only one CSV file, but it could be expanded into something more general. This program will start up with a recursive menu with nine options: 
  * **Create a database:** <br> <blockquote>Takes in a CSV file and parses it into two files: <em>**config** and <em>**data**. Where the config file holds the number of records along with the attributes and their associated maximum length. The data file will hold the records in it's fixed length form along with blank records inbetween each real record.</blockquote>
  * **Open a database:** <br> <blockquote>Takes in the <em>.data file that was made from the create database option and allows the file to be read and written to. If a database is already open it will prompt the user to close it. For creating a report, updating, adding, and deleting a record all require a database to be open. If a database is not open it will prompt the user to open one.</blockquote>
  * **Close a database:** <br> <blockquote>Closes the current database.  </blockquote>
  * **Display a record:** <br> <blockquote>This displays a record and all it's attributes. The program prompts the user for the record ID. If the record doesn't exist it will display a messege as such.</blockquote>
  * **Update a record:** <br> <blockquote>This will allow the user to update any field except for the ID of a record. The program prompts the user for the record ID number. If the record exist, the program will prompt the user for the field they would like to update. If the field does not exist it will display a messege as such. Once the user enters a vaild field, they will be asked to enter the new value (I assume that the user will enter the correct data types).</blockquote>
  * **Create a report:** <br> <blockquote>This option will display the first ten records of the database.</blockquote>
  * **Add a new record:** <br> <blockquote>Allows the user to add a new record to the file. The program will will prompt the user for the ID number of the new record. If the ID already exist it will display a messege as such. Once the user enters a vaild ID, the program will prompt the user to enter the values of the fields pressing enter after each field.</blockquote>
  * **Delete a record:** <br> <blockquote>Allows the user to delete a record. The program will prompt the user for an ID number. Once entered the program will insert 'missing' in place of the record and automatically reorganize the file where there is an empty record between each real record.</blockquote>
  * **Quit:** <br> <blockquote>Terminates the program.</blockquote>

# Technologies Used

- Java
- VS Code
- Git/GitHub

**To-do-list**
  - Add error handling when user enters wrong value types
  - Display record after updating or adding
  - Add recursive menu for updating, adding, deleting records
  - Fix open database to inform that there is already an open database before allowing them to enter a database name
  - Ask user if they do want to delete a record
  - Add a file to hold the deleted records
 
 # Getting Started
 
 **Pre Requirements**
  - Git CLI
  - Java


 **unix:**
 - gh repo clone TheMiniJodi/FixedLengthDB
 - javac FileData.java java FileData
 
    
 # Usage
 
 
 CLI interation:

<img width="926" alt="Screenshot 2023-02-20 at 2 25 47 PM" src="https://user-images.githubusercontent.com/18232226/220192578-4e4cbf73-b992-462f-9bbd-6f2b3bbec9c6.png">

