import java.util.Scanner;

// import jdk.jfr.consumer.RecordedEvent;

import java.io.RandomAccessFile;
import java.io.FileReader;  
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

public class FileData {
    String record;
    int recordSize;
    static RandomAccessFile configFile;
    static RandomAccessFile dataFile;
    boolean success, adding;
    String[] fieldNames = { "ID", "Region", "State", "Code", "Name", "Type", "Visitors" };
    int[] fieldSizes = { 10, 2, 2, 4, 90, 40, 11 };
    static Scanner input = new Scanner(System.in);
    // String recordPos;

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
        System.out.println("Please enter the name of the csv file you would like to create a database for...\n"
                + "(ex: if your file is Name.csv only enter Name)");
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

            while ((line = file.readLine()) != null) 
            {
                String temp, begin, last;

                // Getting rid of entries that have commas within the field
                // This helps separate the data correctly
                if (line.contains("\"")) 
                {
                    temp = line;
                    int holder1 = (temp.indexOf("\""));
                    int holder2 = (temp.lastIndexOf("\"") + 1);
                    temp = temp.substring(holder1, holder2);
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

                    if (lineObj[i].length() < fieldSizes[i]) // Making the fields their required sizes with spaces as holders
                        diff = fieldSizes[i] - lineObj[i].length();

                    for (int j = 0; j < diff; j++)
                        lineObj[i] = lineObj[i] + " ";

                    dataFile.write(lineObj[i] + " ");
                }
                dataFile.write("\n");

                for (int i = 0; i < 83; i++) // Adding my blank record
                    dataFile.write("//");
                dataFile.write("\n");
                numRows++;
                numRows++;

            }
            dataFile.close();

            FileWriter configFile = new FileWriter(new File(fileName + ".config")); // Writing the config file
            // System.out.println(numRows);
            configFile.write(String.format(Integer.toString(numRows)));
            configFile.write(System.lineSeparator());

            for (int i = 0; i < data.length; i++)
                configFile.write(data[i] + ":" + fieldSizes[i] + "\n");

            configFile.close();
            System.out.println("Database for " + fileName + " has been created");
            System.out.println("\n");
            file.close();

        } catch (Exception e) {
            System.out.println("Sorry could not open file\n" + "Terminating Program...");
            e.printStackTrace();
            System.exit(0);
        }
    }

    // write a blank record
    String writeBlank()
    {
        String temp = "";
        for(int i = 0; i < 83; i++)
        {
            temp = temp + "//";
        }
        return temp;
    }

    // Adding more blank recrds to the database
    void reorganize() throws IOException
    {
        dataFile.seek(0);
        String line = null;
        int pos = 0;
        int pos2 = 0;

        while((line = dataFile.readLine()) != null)
        {
            if(!line.contains("//") && !line.contains("missing"))
            {
                dataFile.seek(0);
                dataFile.skipBytes((pos * recordSize));
                dataFile.writeBytes(line + "\n");
                dataFile.writeBytes(writeBlank());
                pos+=2;
            }
            else if(line.contains("missing"))
            {
                dataFile.seek(0);
                dataFile.skipBytes((pos * recordSize));
                dataFile.writeBytes(writeBlank());
                pos++;
            }
            pos2++;
            dataFile.seek(0);
            dataFile.skipBytes((pos2 * recordSize));
        }
        // Add the new record number for the config file
        // System.out.println(pos2);
        // configFile.writeInt(pos2);
    }


    // Choice 2
    // Opening a database
    void open() 
    {
        System.out.println("Opening a database");
        System.out.println("Please enter the prefix of the database that you would like to open...\n"
                + "(ex: if your file is Name.data only enter Name)");
            try 
            {
                String databaseName = input.nextLine();
                System.out.println("You have enter in the file name: " + databaseName);

            
                if (configFile == null && dataFile == null) 
                {
                    configFile = new RandomAccessFile(databaseName + ".config", "r");
                    dataFile = new RandomAccessFile(databaseName + ".data", "rw");
                    System.out.println("Database is open\n");
                } 
                else
                    System.out.println("Currently, there is an opened database. You must close it before opening another." + "\n");
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
            System.out.println("Database is closed.");
            System.out.println("\n");
        } catch (Exception e) {
            System.out.println("Something went wrong!\n");
        }
    }

    // Choice number 4
    // Display record
    String display() throws IOException 
    {
        if (dataFile != null) 
        {
            try 
            {
                System.out.println("Please enter the ID of the record");
                String recordPos = input.nextLine();
                System.out.println("Searching..." + "\n");

                if (getRecord(dataFile, search(dataFile, recordPos))) 
                {
                    System.out.println("Record found:" + "\n");
                    String[] dataInfo = record.split(" ");

                    for (int i = 0; i < dataInfo.length; i++) 
                    {
                        dataInfo[i] = dataInfo[i].replace('_', ' ');
                        System.out.println(fieldNames[i] + ": " + dataInfo[i] + " ");
                    }
                    System.out.println("\n");
                    return recordPos;
                } 
                else 
                {
                    System.out.println("Could not get Record " + recordPos);
                    System.out.println("Record out of range\n\n");
                    return null;
                }
            } catch (Exception e) {
                System.out.println("Something went wrong");
                e.printStackTrace();
            }
        } 
        else
            System.out.println("Whoops, please open a database first\n");
        return null;
    }


    // Binary Search Tree
    int search(RandomAccessFile dataFile, String ID) throws IOException 
    {
        int low = 0;
        configFile.seek(0);
        int high = Integer.parseInt(configFile.readLine()); // Grabbing the number of rows from the config file
        int middle = 0;
        boolean found = false;
        int count = 0;
        int count2 = 0;

        while (!found && (high >= low)) 
        {
            middle = (low + high) / 2;
            success = getRecord(dataFile, middle);
            while(((record.contains("//")) || record.contains("missing") ))
            {
                middle++;
                success = getRecord(dataFile, middle);
            }
            String MiddleId[] = record.split(" ");
            System.out.println(MiddleId[0]);

            if(MiddleId[0].contains("missing") || MiddleId[0].contains("//"))
                    return -1;

            int midId = Integer.parseInt(MiddleId[0]);
            int lookId = Integer.parseInt(ID);

            if((high - middle <= 3) && (middle - low <= 3) && adding)
            {
                System.out.println("in the else if with high - middle and middle - low ");
                if(count2 == 4)
                    return middle;
                count2++;
            }
            else if((high - middle <= 3) && adding)
            {
                if(count == 4)
                    return middle;
                count++;
            }

            if (midId == lookId)
                found = true;
            else if (midId < lookId)
                low = middle + 1;
            else if((high - middle <= 3) && (middle - low <= 3) && !adding)
                return -1;
            else
                high = middle - 1;
        }
        if (found)
            return middle; // the record number of the record
        else
            return -1;
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
            dataFile.skipBytes(recordPos * recordSize);
            record = dataFile.readLine().replaceAll("\\s+", " ");
            success = true;
        }
        return success;
    }

    // Choice 5
    // Update a record
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

                if (field.equals("ID"))
                    System.out.println("Sorry, but we can't edit the ID");
                else 
                {
                    int pos = 0;
                    int pos2 = 0;
                    int maxLength = 0;
                    
                    // Switch statement for the field that the user wants to update
                    switch (field) {
                        case "Region":
                            pos = 11;
                            pos2 = 2;
                            maxLength = 2;
                            break;
                        case "State":
                            pos = 14;
                            pos2 = 3;
                            maxLength = 2;
                            break;
                        case "Code":
                            pos = 17;
                            pos2 = 4;
                            maxLength = 4;
                            break;
                        case "Name":
                            pos = 22;
                            pos2 = 5;
                            maxLength = 90;
                            break;
                        case "Type":
                            pos = 113;
                            pos2 = 6;
                            maxLength = 40;
                            break;
                        case "Visitors":
                            pos = 154;
                            pos2 = 7;
                            maxLength = 11;
                            break;
                        default:
                            System.out.println("Invaild Input" + "\n");
                    }
                    if (pos != 0) { // this needs to change
                        dataFile.seek(0);
                        dataFile.skipBytes(((recordPos) * recordSize) + pos);

                        System.out.println("Enter in the new value for " + field);
                        String temp = input.nextLine();

                        int diff = 0;

                        if (temp.contains(" "))
                            temp = temp.replace(' ', '_');
                        if (temp.length() < fieldSizes[pos2])// Making the fields their required sizes with spaces as holders
                        {
                            diff = fieldSizes[pos2] - temp.length();

                            for (int j = 0; j < diff; j++)
                                temp = temp + " ";
                        }
                        if (temp.length() > fieldSizes[pos2]) // Making the field smaller if larger than the fieldsize
                            temp = temp.substring(0, maxLength);

                        byte[] words = temp.getBytes();
                        dataFile.write(words); // I can just change this to writeBytes() instead of having two lines
                    }
                }
            }
        } else
            System.out.println("Whoops, please open a database first\n");
    }

    // Choice 6
    // Displaying the first ten real records
    void createR() throws IOException 
    {
        int pos = 0;
        if (dataFile != null) 
        {
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

                for (int j = 0; j < dataInfo.length; j++) 
                {
                    dataInfo[j] = dataInfo[j].replace('_', ' '); // Taking out the underscores
                    System.out.print(fieldNames[j] + ": " + dataInfo[j] + " ");
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
            System.out.println("Please enter in the Id number for the new addition");
            String recordNum = input.nextLine();
            int id = Integer.parseInt(recordNum);
            int diff = 0;
            int nextRecordPos = 0; 
            int recordLength = 167;

            if(!getRecord(dataFile, search(dataFile, recordNum)))
            {
                adding = true;
                nextRecordPos = search(dataFile, recordNum);

                String comparedNum[] = record.split(" ");
                int nextRecordId = Integer.parseInt(comparedNum[0]);

                if(id < nextRecordId)
                    nextRecordPos--;
                if(id > nextRecordId)
                    nextRecordPos++;

                dataFile.seek(0);
                dataFile.skipBytes(nextRecordPos * 167);
                String slot = dataFile.readLine();
                
                if(slot.contains("//"))
                {
                    // System.out.println(nextRecordPos);
                    System.out.println("Please enter in the Region, State, Code, Name, Type, and Visitors in that order.\n" + "After each field press enter");
                    String region = input.nextLine();
                    String state = input.nextLine();
                    String code = input.nextLine();
                    String name = input.nextLine(); //TODO: check to see if they add quotes
                    name = name.replace(" ", "_");
                    String type = input.nextLine();
                    type = type.replace(" ", "_");
                    String visitors = input.nextLine();

                    String line = id + "," + region + "," + state + "," + code + "," + name + "," + type + "," + visitors;
                    String newRecord = "";
                    String[] lineObj = line.split(",");

                    dataFile.seek(0);
                    dataFile.skipBytes(nextRecordPos * recordLength);

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
                    // System.out.println(newRecord);
                    }
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
        if (dataFile != null) 
        {
            System.out.println("Please enter the Id of the record that you want to delete");
            String recordNum = input.nextLine();
            int recordPos = search(dataFile, recordNum);
            System.out.println(success);
            if(success)
            {
                dataFile.seek(0);
                dataFile.skipBytes(((recordPos) * recordSize));

                String temp = "missing";
                for (int i = 0; i < 159; i++) // TODO: change 159 to a variable
                    temp = temp + " ";

                byte[] words = temp.getBytes();
                dataFile.write(words);
                System.out.println("Record " + recordNum + " has successfully been deleted");
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
        System.out.println("Welcome!" + "\n" + "Please select from the following: \n" + "1: create a new database\n"
                + "2: Open Database\n" + "3: Close Database\n" + "4: Display record\n" + "5: Update record\n"
                + "6: Create Report\n" + "7: Add record\n" + "8: Delete record\n" + "9: QUIT");

        // Scanner menu = new Scanner(System.in);
        String menuChoice = input.nextLine();
        System.out.println("You have selected: " + menuChoice + "\n");

        if (menuChoice == "9") 
        {
            System.out.println("Exiting Program...");
            System.exit(0);
        }

        // Recursive menu until QUIT is selected unless they choose a char then program
        // will quit
        while (menuChoice != "9") 
        {
            switch (menuChoice) 
            {
                case "1":
                    database.createDB();
                    break;
                case "2":
                    database.open();
                    break;
                case "3":
                    database.close();
                    break;
                case "4":
                    database.display();
                    break;
                case "5":
                    database.update();
                    break;
                case "6":
                    database.createR();
                    break;
                case "7":
                    database.add();
                    break;
                case "8":
                    database.delete();
                    break;
                case "9":
                    System.out.println("Exiting Program...");
                    if(configFile != null)
                    {
                        configFile.close();
                        dataFile.close();
                    }
                    System.exit(0);
                    break;
                case"10":
                    database.reorganize();
                    break;
                default:
                    System.out.println("Invaild Input" + "\n");
            }

            System.out.println("Welcome!" + "\n" + "Please select from the following: \n" + "1: create a new database\n" + "2: Open Database\n" + "3: Close Database\n" + "4: Display record\n" + "5: Update record\n" + "6: Create Report\n" + "7: Add record\n" + "8: Delete record\n" + "9: QUIT");

            menuChoice = input.nextLine();
            System.out.println("You have selected choice: " +  menuChoice + "\n");   
    
        }
    }
}

