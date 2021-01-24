import java.util.Scanner;
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
    boolean success;
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
    }

    // Choice 1
    // Creating a new DataBase
    void createDB() {
        System.out.println("Creating a new DataBase");

        System.out.println("Please enter the name of the csv file you would like to create a database for...\n"
                + "(ex: if your file is Name.csv only enter Name)");
        String fileName = input.nextLine();
        System.out.println("You have enter in the file name: " + fileName);

        readInFile(fileName);
    }

    // Reading in the file name for the new database
    void readInFile(String fileName) {
        int numRows = 0;

        try {
            BufferedReader file = new BufferedReader(new FileReader(fileName + ".csv"));
            String firstLine = file.readLine();
            String[] data = firstLine.split(",");

            String line = null;
            FileWriter dataFile = new FileWriter(new File(fileName + ".data")); // Writing the data file

            while ((line = file.readLine()) != null) {
                String temp, begin, last;

                // Getting rid of entries that have commas within the field
                // This helps separate the data correctly
                if (line.contains("\"")) {
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
                // writeToFile(lineObj);
                for (int i = 0; i < lineObj.length; i++) {
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
            // e.printStackTrace();
            System.exit(0);
        }
    }

    void reorganize() throws IOException
    {
        dataFile.seek(0);
        String line = null;
        int pos = 0;
        configFile.seek(0);
        // int numRows = Integer.parseInt(configFile.readLine());

        while((line = dataFile.readLine()) != null)
        {
            System.out.println("The beginning: " + pos);

            if(line.contains("missing"))
            {   
                System.out.println("In the if: " + pos);
                System.out.println(line);

                // dataFile.seek(0);
                // dataFile.skipBytes(((recordPos) * recordSize));

                dataFile.seek(0);
                dataFile.skipBytes(((pos) * recordSize));
                String temp = "";
                for(int i = 0; i < 83; i++)
                {
                    temp = temp + "//";
                }
                byte[] blank = temp.getBytes();
                dataFile.write(blank);
                // System.out.println("In the if statement");
                // System.out.println(pos);
                dataFile.seek(0);
                dataFile.skipBytes(((pos + 1) * recordSize));
            }

            pos++;

            // I need to add a way to add a line after each non blank entry
            
        }

    }

    // Choice 2
    // Opening a database
    void open() {
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
            } else
                System.out.println(
                        "Currently, there is an opened database. You must close it before opening another." + "\n");

        } catch (Exception e) {
            System.out.println("Something went wrong, could not open the database");
        }
    }

    // Choice 3
    // Closing a database
    void close() {
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
            try {
                System.out.println("Please enter the ID of the record");
            
                String recordPos = input.nextLine();
                System.out.println("Searching..." + "\n");

                if (getRecordDisplay(dataFile, search(dataFile, recordPos))) {
                    System.out.println("Record found:" + "\n");

                    String[] dataInfo = record.split(" ");
                    for (int i = 0; i < dataInfo.length; i++) 
                    {
                        dataInfo[i] = dataInfo[i].replace('_', ' ');
                        System.out.println(fieldNames[i] + ": " + dataInfo[i] + " ");
                    }
                    System.out.println("\n");
                    return recordPos;
                } else {
                    System.out.println("Could not get Record " + recordPos);
                    System.out.println("Record out of range\n\n");
                    return null;
                }
            } catch (Exception e) {
                System.out.println("Something went wrong");
                e.printStackTrace();
            }
        } else
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

        while (!found && (high >= low)) {
            middle = (low + high) / 2;
            success = getRecordDisplay(dataFile, middle);

            System.out.println("middle: " + middle);
            System.out.println("high: " + high);
            System.out.println("low: " + low);

            while(((record.contains("//")) || record.contains("missing") && (middle < (high - 1))))
            {
                middle++;
                success = getRecordDisplay(dataFile, middle);
            }
            
            String MiddleId[] = record.split(" ");
    
            if(MiddleId[0].contains("missing"))
                    return -1;

            int midId = Integer.parseInt(MiddleId[0]);
            int lookId = Integer.parseInt(ID);

            if (midId == lookId)
                found = true;
            else if (midId < lookId)
                low = middle + 1;
            else
                high = middle - 1;
        }
        if (found)
            return middle; // the record number of the record
        else
            return -1;
    }

    // Grabbing the record
    // I might make this a dual function rather than a getrecord for display and
    // getrecord for insert or something. will see!
    boolean getRecordDisplay(RandomAccessFile dataFile, int recordNum) throws IOException 
    {
        configFile.seek(0);
        int numRows = Integer.parseInt(configFile.readLine());
        success = false;

        if ((recordNum >= 0) && (recordNum < numRows)) 
        {
            dataFile.seek(0);
            dataFile.skipBytes(recordNum * recordSize);
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
                    if (pos != 0) {
                        dataFile.seek(0);
                        dataFile.skipBytes(((recordPos + 1) * recordSize) + pos);

                        System.out.println("Enter in the new value for " + field);
                        String temp = input.nextLine();

                        int diff = 0;

                        if (temp.contains(" "))
                            temp = temp.replace(' ', '_');
                        if (temp.length() < fieldSizes[pos2])// Making the fields their required sizes with spaces as
                                                            // holders
                        {
                            diff = fieldSizes[pos2] - temp.length();

                            for (int j = 0; j < diff; j++)
                                temp = temp + " ";
                        }
                        if (temp.length() > fieldSizes[pos2]) // Making the field smaller if larger than the fieldsize
                            temp = temp.substring(0, maxLength);

                        byte[] words = temp.getBytes();
                        dataFile.write(words);
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

            for (int i = 0; pos < 10; i++) 
            {
                getRecordDisplay(dataFile, i);
                while(record.contains("//") || record.contains("missing")){
                    getRecordDisplay(dataFile, i++);
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
        } else {
            System.out.println("Whoops, please open a database first\n");

        }
    }

    void add() throws IOException 
    {
        System.out.println("Please enter in the Id number for the new addition");
        String recordNum = input.nextLine();
        int id = Integer.parseInt(recordNum);
        if(!getRecordDisplay(dataFile, id))
        {
            System.out.println("Please enter in the Region, State, Code, Name, Type, and Visitors in that order.\n" + "After each field press enter");
            String region = input.nextLine();
            String state = input.nextLine();
            String code = input.nextLine();
            String name = input.nextLine();
            String type = input.nextLine();
            String visitors = input.nextLine();

            String line = region + " " + state + " " + code + " " + name + " " + type + " " + visitors;
            System.out.println(line);
        }

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
                for (int i = 0; i < 159; i++)
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

