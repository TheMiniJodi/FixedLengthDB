//Name: Jodi Mitchell
//Date: 02/02/21
//Description: This is a simple file database. It's build for the current fields and associated sizes: ID:10, Region:2, State:2, Code:4, Name:90, Type:40, Visitors:11. You can create, open, close a database. You may also update, add, display a record from a provided database. There is even a choice to display the first ten records of the database.

import java.util.Scanner;
import java.io.RandomAccessFile;
import java.io.FileReader;  
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;

public class FileData {
    String record, databaseName;
    int recordSize;
    static RandomAccessFile configFile;
    static RandomAccessFile dataFile;
    boolean success, adding;
    int[] fieldSizes = { 10, 2, 2, 4, 90, 40, 11 };
    static Scanner input = new Scanner(System.in);

    // Constructor
    FileData() {
        configFile = null;
        dataFile = null;
        recordSize = 167;
        success = false;
        adding = false;
    }

    // Choice 1
    // Creating a new DataBase
    void createDB() 
    {
        System.out.println("Creating a new DataBase");
        System.out.println("Please enter the name of the csv file you would like to create a database for...\n"+ "(ex: if your file is Name.csv only enter Name)");
        String fileName = input.nextLine();
        System.out.println("You have enter in the file name: " + fileName);
        readInFile(fileName);
    }

    // Reading in the file name for the new database
    void readInFile(String fileName) 
    {
        int numRows = 0;

        try {
            BufferedReader file = new BufferedReader(new FileReader(fileName + ".csv"));
            String firstLine = file.readLine();
            String[] data = firstLine.split(",");
            String line = null;
            FileWriter dataFile = new FileWriter(new File(fileName + ".data")); // Writing the data file
            dataFile.write(writeBlank() + "\n");

            while ((line = file.readLine()) != null) 
            {

                String temp, begin, last;

                // Getting rid of entries that have commas within the field
                // This helps separate the data correctly
                if (line.contains("\"")) {
                    temp = line;
                    int holder1 = (temp.indexOf("\""));            // The first instance of "
                    int holder2 = (temp.lastIndexOf("\"") + 1);    // the last instance of "
                    temp = temp.substring(holder1, holder2);       // String "     "
                    temp = temp.replace(',', ' ');
                    begin = line.substring(0, holder1);
                    last = line.substring(holder2, line.length());
                    line = begin + temp + last;
                }

                // Separating the different fields
                String[] lineObj = line.split(",");
                for (int i = 0; i < lineObj.length; i++) 
                {
                    int diff = 0;

                    if (lineObj[i].contains(" "))
                        lineObj[i] = lineObj[i].replace(' ', '_');

                    if (lineObj[i].length() < fieldSizes[i]) // Making the fields their required sizes with spaces as
                                                             // holders
                        diff = fieldSizes[i] - lineObj[i].length();

                    for (int j = 0; j < diff; j++)
                        lineObj[i] = lineObj[i] + " ";

                    dataFile.write(lineObj[i] + " ");
                }
                dataFile.write("\n");

                dataFile.write(writeBlank() + "\n");  // Adding blank records
                numRows++;
                numRows++;

            }
            dataFile.close();

            FileWriter configFile = new FileWriter(new File(fileName + ".config")); // Writing the config file
            configFile.write(String.format(Integer.toString(numRows)));
            configFile.write(System.lineSeparator());

            for (int i = 0; i < data.length; i++)
                configFile.write(data[i] + " " + fieldSizes[i] + " ");

            configFile.close();
            System.out.println("Database for " + fileName + " has been created");
            System.out.println("\n");
            file.close();

        } catch (Exception e) {
            System.out.println("Sorry could not open file\n" + "Terminating Program...");
            System.exit(0);
        }
    }

    // Making a blank record
    String writeBlank() 
    {
        String temp = "";
        for (int i = 0; i < 83; i++) 
        {
            temp = temp + "//";
        }
        return temp;
    }

    // Adding more blank records to the database
    void reorganize() throws IOException 
    {
        dataFile.seek(0);
        String line = null;
        int pos = 1;

        // temp file to hold real values so real records don't get written over by new
        // blanks
        RandomAccessFile tempDataFile = new RandomAccessFile("temp.data", "rw");
        tempDataFile.writeBytes(writeBlank() + "\n");

        while ((line = dataFile.readLine()) != null) 
        {
            if (!line.contains("//") && !line.contains("missing")) 
            {
                tempDataFile.seek(0);
                tempDataFile.skipBytes(pos * recordSize);
                tempDataFile.writeBytes(line + "\n");
                tempDataFile.writeBytes(writeBlank() + "\n");
                pos += 2;
            } 
            else if (line.contains("missing")) 
            {
                tempDataFile.seek(0);
                tempDataFile.skipBytes(pos * recordSize);
                tempDataFile.writeBytes(writeBlank() + "\n");
                pos++;
            }
        }
        configFile.seek(0);
        String numRows = Integer.toString(pos);
        byte[] cases = numRows.getBytes();
        configFile.write(cases);
        File temp = new File("temp.data");
        File actual = new File(databaseName + ".data");
        temp.renameTo(actual); // Renaming the file to the database that is currently being used
        tempDataFile.close();
        dataFile.close();
        openAfterReOrg(); 
        // I had to close the file and reopen it so the program would run normally. 
        // If I do not close and reopen the file the program wouldn't read the new blanks that were inserted.
    }

    // Open method after reorganizing
    void openAfterReOrg() throws FileNotFoundException
    {
        dataFile = new RandomAccessFile(databaseName + ".data", "rw");
    }

    // Choice 2
    // Opening a database
    void open() 
    {
        System.out.println("Opening a database");
        System.out.println("Please enter the prefix of the database that you would like to open...\n" + "(ex: if your file is Name.data only enter Name)");
            try 
            {
                databaseName = input.nextLine();
                System.out.println("You have enter in the file name: " + databaseName);

            
                if (configFile == null && dataFile == null) 
                {
                    File data = new File(databaseName + ".data");
                    if(data.exists()) // Checking to see if the database exists
                    {
                        configFile = new RandomAccessFile(databaseName + ".config", "rw");
                        dataFile = new RandomAccessFile(databaseName + ".data", "rw");
                        System.out.println(databaseName + " database is open\n");
                    }
                    else
                    {
                        System.out.println("Something went wrong!\n" + "Terminating Program...");
                        System.exit(0);
                    }
                } 
                else
                    System.out.println("Currently, there is an opened database. You must close the current database before opening another." + "\n");
            } catch (Exception e) {
                System.out.println("Something went wrong, could not open the database");
            }
    }

    // Choice 3
    // Closing a database
    void close() 
    {
        try {
            configFile.close();
            dataFile.close();
            configFile = null;
            dataFile = null;
            System.out.println("Database is closed.\n");
        } catch (Exception e) {
            System.out.println("Something went wrong!\n");
        }
    }

    // Choice number 4
    // Display record
    String display() throws IOException 
    {
        if (dataFile != null && configFile != null) 
        {
            try 
            {
                configFile.seek(0);
                configFile.readLine();
                String field = configFile.readLine();
                String [] fieldNames = field.split(" ");

                System.out.println("Please enter the ID of the record");
                String recordPos = input.nextLine();
                System.out.println("Searching..." + "\n");

                if (getRecord(dataFile, search(dataFile, recordPos))) // Does the record exists
                {
                    System.out.println("Record found:" + "\n");
                    String[] dataInfo = record.split(" ");
                    int j = 0;

                    for (int i = 0; i < dataInfo.length; i++) 
                    {
                        dataInfo[i] = dataInfo[i].replace('_', ' ');  // Displaying them without underscores
                        System.out.println(fieldNames[j] + ": " + dataInfo[i] + " ");
                        j += 2;
                    }
                    System.out.println("\n");
                    return recordPos;
                } 
                else 
                {
                    System.out.println("Could not find Record " + recordPos);
                    System.out.println("Please double check your record ID number and try again...\n\n");
                    return "";
                }
            } catch (Exception e) {
                System.out.println("Something went wrong");
                System.exit(0);
            }
        } 
        else
            System.out.println("Whoops, please open a database first\n");
        return "";
    }


    // Binary Search
    int search(RandomAccessFile dataFile, String ID) throws IOException 
    {
        int low = 0;
        configFile.seek(0);
        int high = Integer.parseInt(configFile.readLine()); // Grabbing the number 
        int middle = 0;
        boolean found = false;
        int lookId = Integer.parseInt(ID);  // Changint the ID to a number


        while (!found && (high >= low)) 
        {
            middle = (low + high) / 2;
            getRecord(dataFile, middle);
            while(((record.contains("//")) || record.contains("missing") )) // Skipping the blank and missing records
            {
                middle++;
                getRecord(dataFile, middle);

                if(low == high && Math.abs(middle - low) <= 1)
                {
                    if(!adding)
                        return -1;
                    if(adding)
                        return middle;
                }
                else if((middle - high) <= 1 && (middle - low) <= 1)
                {
                    if(!adding)
                        return -1;
                    if(adding)
                    {
                        if(record.contains("//") || record.contains("missing"))
                            return middle - 1;
                        return middle;
                    }
                }
            }
                String MiddleId[] = record.split(" ");

                int midId = Integer.parseInt(MiddleId[0]);
                lookId = Integer.parseInt(ID); // The id that we are looking for

                if (midId == lookId)
                    found = true;
                else if (midId < lookId)
                    low = middle + 1;
                else
                    high = middle - 1;
        }
        if (found)
            return middle; // the record number
        else{
            if(adding)
                return middle;
            return -1;
        }
        
    }

    // Grabbing the record
    boolean getRecord(RandomAccessFile dataFile, int recordPos) throws IOException 
    {
        configFile.seek(0);
        int numRows = Integer.parseInt(configFile.readLine());
        success = false;

        if ((recordPos >= 0) && (recordPos < numRows))
        {
            dataFile.seek(0);
            dataFile.skipBytes((recordPos)* recordSize);
            record = dataFile.readLine().replaceAll("\\s+", " ");
            success = true;
        }
        return success;
    }

    // Choice 5
    // Update a record
    // TODO:  Need to display record after updating
    void update() throws IOException 
    {
        int recordPos;
        if (dataFile != null) 
        {
            String recordNum =  display();
            if(recordNum != null)
            {
                recordPos = search(dataFile, recordNum);

                System.out.println("Please type in the field name you would like to edit. For example: Visitors");
                String field = input.nextLine();
                System.out.println("You entered the field name: " + field + "\n");

                if (field.equals("ID") || field.equals("Id"))
                    System.out.println("Sorry, but we can't edit the ID");
                else 
                {
                    int pos = 0;
                    int pos2 = 0;
                    int maxLength = 0;
                    
                    // Switch statement for the field that the user wants to update
                    switch (field) 
                    {
                        case "Region":
                            pos = 11;   // Start position to over write in the record
                            pos2 = 1;   // Position of the field's name in array
                            maxLength = 2;  // Max length of the field
                            break;
                        case "State":
                            pos = 14;
                            pos2 = 2;
                            maxLength = 2;
                            break;
                        case "Code":
                            pos = 17;
                            pos2 = 3;
                            maxLength = 4;
                            break;
                        case "Name":
                            pos = 22;
                            pos2 = 4;
                            maxLength = 90;
                            break;
                        case "Type":
                            pos = 113;
                            pos2 = 5;
                            maxLength = 40;
                            break;
                        case "Visitors":
                            pos = 154;
                            pos2 = 6;
                            maxLength = 11;
                            break;
                        default:
                            System.out.println("Invaild Input" + "\n");
                    }
                    if (pos != 0) 
                    { 
                        dataFile.seek(0);
                        dataFile.skipBytes(((recordPos) * recordSize) + pos);

                        System.out.println("Enter in the new value for " + field);
                        String temp = input.nextLine();

                        int diff = 0;

                        if(temp.contains(","))
                            temp = temp.replace(',', ' '); // Replacing the commas with a space
                        if (temp.contains(" "))
                            temp = temp.replace(' ', '_'); // replacing the spaces with underscores

                        if (temp.length() < fieldSizes[pos2]) // Making the fields their required sizes with spaces as holders
                        {
                            diff = fieldSizes[pos2] - temp.length();

                            for (int j = 0; j < diff; j++)
                                temp = temp + " ";
                        }
                        if (temp.length() > fieldSizes[pos2]) // Making the field smaller if larger than the fieldsize
                            temp = temp.substring(0, maxLength);

                        dataFile.writeBytes(temp);
                    }
                }
            }
        } 
        else
            System.out.println("Whoops, please open a database first\n");   
    }

    // Choice 6
    // Displaying the first ten real records
    void createR() throws IOException 
    {
        int pos = 0;
        if (dataFile != null && configFile != null) 
        {
            configFile.seek(0);
            configFile.readLine();
            String field = configFile.readLine();
            String [] fieldNames = field.split(" ");

            System.out.println("This is the first ten records:" + "\n");

            for (int i = 0; pos < 10; i++) // need the first ten real records
            {
                getRecord(dataFile, i);
                while(record.contains("//") || record.contains("missing")) // skipping the missing and blank records
                {
                    getRecord(dataFile, i++);
                }

                String[] dataInfo = record.split(" ");
                System.out.print((pos + 1) + ". ");
                int k = 0;

                for (int j = 0; j < dataInfo.length; j++) 
                {
                    dataInfo[j] = dataInfo[j].replace('_', ' '); // Taking out the underscores
                    System.out.print(fieldNames[k] + ": " + dataInfo[j] + " ");
                    k +=2;
                }
                System.out.println("\n");
                pos++;
            }
        } 
        else 
            System.out.println("Whoops, please open a database first\n");
    }
  

    // Choice 7
    // Adding a record
    void add() throws IOException 
    {
        if(dataFile != null)
        {
            System.out.println("Please enter in the ID number for the new addition");
            String recordNum = input.nextLine();
            int id = Integer.parseInt(recordNum);
            int diff = 0;
            int nextRecordPos = 0; 

            if(!getRecord(dataFile, search(dataFile, recordNum))) // Checking to see if the record already exist
            {
                adding = true;
                nextRecordPos = search(dataFile, recordNum);
                String comparedNum[] = record.split(" ");
            
                if(!record.contains("//")) // Adjusting to the correct position 
                {
                    int nextRecordId = Integer.parseInt(comparedNum[0]);

                    if(id < nextRecordId)
                        nextRecordPos--;
                    if(id > nextRecordId)
                        nextRecordPos++;
                }
                    dataFile.seek(0);
                    dataFile.skipBytes((nextRecordPos) * recordSize);
                    String slot = dataFile.readLine();

                    // System.out.println(slot);

                if(slot != null && slot.contains("//"))
                {
                    System.out.println("Please enter in the Region, State, Code, Name, Type, and Visitors in that order.\n" + "After each field press enter");
                    String region = input.nextLine();
                    String state = input.nextLine();
                    String code = input.nextLine();
                    String name = input.nextLine();
                    name = name.replace(",", "");
                    name = name.replace(" ", "_");
                    String type = input.nextLine();
                    type = type.replace(" ", "_");
                    String visitors = input.nextLine();

                    String line = id + "," + region + "," + state + "," + code + "," + name + "," + type + "," + visitors;
                    String newRecord = "";
                    String[] lineObj = line.split(",");

                    

                    for(int i = 0; i < lineObj.length; i++) 
                    {
                        if (lineObj[i].length() < fieldSizes[i])  // Making the fields their required sizes with spaces as holders
                        {
                            diff = fieldSizes[i] - lineObj[i].length();
                            for (int j = 0; j < diff; j++)
                                lineObj[i] = lineObj[i] + " ";
                        }

                        if (lineObj[i].length() > fieldSizes[i]) // Making the field smaller if larger than the fieldsize
                            lineObj[i] = lineObj[i].substring(0, fieldSizes[i]);
                        newRecord = newRecord + lineObj[i] + " ";
                    }
                    dataFile.seek(0);
                    dataFile.skipBytes((nextRecordPos * recordSize));
                    dataFile.writeBytes(newRecord + "\n");
                    adding = false;
                }
                else
                {
                    System.out.println("Reorganizing...");
                    System.out.println("Let's try that again");
                    adding = false;
                    reorganize();
                    add();
                }
            }
            else if(record == null)
            {
                System.out.println("Reorganizing...");
                System.out.println("Let's try that again");
                adding = false;
                reorganize();
                add();
            }
            else
            {
                adding = false;
                System.out.println("The record already exist");
            }
        }
        else
            System.out.println("Whoop, please open a database first");
    }

    // Choice 8
    // Delete a record
    void delete() throws IOException 
    { 
        String temp = "missing";
        int lengthDiff = recordSize - temp.length();
        if (dataFile != null) 
        {
            System.out.println("Please enter the ID of the record that you want to delete");
            String recordNum = input.nextLine();
            int recordPos = search(dataFile, recordNum);

            if(success) // Checking if record exists
            {
                dataFile.seek(0);
                dataFile.skipBytes(((recordPos) * recordSize));

                
                for (int i = 0; i < lengthDiff; i++)
                    temp = temp + " ";
                System.out.println(temp);
                dataFile.writeBytes(temp + "\n");
                System.out.println("Record " + recordNum + " has successfully been deleted");
                reorganize();
            }
            else
                System.out.println("record doesn't exist");
        } else
            System.out.println("Whoops, please open a database first\n");
    }

    public static void main(String[] args) throws IOException 
    {
        FileData database = new FileData();

        // Menu Choices
        System.out.println("Welcome to my file-based database!" + "\n" + "Please select from the following: \n" + "1: Create a new database\n"
                + "2: Open Database\n" + "3: Close Database\n" + "4: Display record\n" + "5: Update record\n"
                + "6: Create Report\n" + "7: Add record\n" + "8: Delete record\n" + "9: Quit");

        String menuChoice = input.nextLine();
        System.out.println("You have selected: " + menuChoice + "\n");

        if (menuChoice == "9") 
        {
            System.out.println("Exiting Program...");
            System.exit(0);
        }

        // Recursive menu; must select the quit option to exit.
        // If the user chooses a character that is not among the below choices, the program will present a messege to the user
        // that their choice was invalid and prompt them again with the menu choices.
        while (menuChoice != "9") 
        {
            switch (menuChoice) 
            {
                case "1":
                    database.createDB(); // Create a database
                    break;
                case "2":
                    database.open(); // Open a database
                    break;
                case "3":
                    database.close(); // Close a database
                    break;
                case "4":
                    database.display(); // Display a record
                    break;
                case "5":
                    database.update(); // Update a record
                    break;
                case "6":
                    database.createR(); // Create a report
                    break;
                case "7":
                    database.add(); // Add a record
                    break;
                case "8":
                    database.delete(); // Delete a record
                    break;
                case "9":
                    System.out.println("Goodbye....Thanks for using my file-based database :) \n");
                    System.out.println("Exiting Program..."); // Exit the program
                    if(configFile != null)
                    {
                        configFile.close();
                        dataFile.close();
                    }
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invaild Input" + "\n");
            }

            System.out.println("Please select from the following: \n" + "1: Create a new database\n" + "2: Open Database\n" + "3: Close Database\n" + "4: Display record\n" + "5: Update record\n" + "6: Create Report\n" + "7: Add record\n" + "8: Delete record\n" + "9: Quit");

            menuChoice = input.nextLine();
            System.out.println("You have selected choice: " +  menuChoice + "\n");   
    
        }
    }
}

