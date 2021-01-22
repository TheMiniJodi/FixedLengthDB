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
    RandomAccessFile configFile;
    RandomAccessFile dataFile;
    boolean success;
    String[] fieldNames = {"ID", "Region", "State", "Code", "Name", "Type", "Visitors"};
    int[] fieldSizes = { 10, 2, 2, 4, 90, 40, 11 };
    Scanner input = new Scanner(System.in);


    FileData() 
    {
        configFile = null;
        dataFile = null;
        recordSize = 167;
        success = false;
    }

    // Choice 1
    // Creating a new DataBase
    void createDB() 
    {
        System.out.println("Creating a new DataBase");

        Scanner obj = new Scanner(System.in); // I tried closing this, but it throws an error. It will have to stay
                                              // open, for now.
        System.out.println("Please enter the name of the csv file you would like to create a database for...\n"
                + "(ex: if your file is Name.csv only enter Name)");

        String fileName = obj.nextLine();
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

                    if(lineObj[i].contains(" "))
                        lineObj[i] = lineObj[i].replace(' ', '_');

                    
                    if (lineObj[i].length() < fieldSizes[i]) // Making the fields their required sizes with $ as holders
                        diff = fieldSizes[i] - lineObj[i].length();

                    for (int j = 0; j < diff; j++)
                        lineObj[i] = lineObj[i] + " ";

                    dataFile.write(lineObj[i] + " ");
                }
                dataFile.write("\n");
                for(int i = 0 ; i < 83; i++)
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
            System.exit(0);
        }
    }

    // Choice 2
    // Opening a database
    void open() 
    {
        System.out.println("Opening a database");

        // Scanner databaseObj = new Scanner(System.in); // I tried closing this, but it throws an error. It will have to stay open, for now
        System.out.println("Please enter the prefix of the database that you would like to open...\n"
                + "(ex: if your file is Name.data only enter Name)");

        String databaseName = input.nextLine();
        System.out.println("You have enter in the file name: " + databaseName);

        try {
            if (configFile == null && dataFile == null) 
            {
                configFile = new RandomAccessFile(databaseName + ".config", "r");
                dataFile = new RandomAccessFile(databaseName + ".data", "rw");
                System.out.println("Database is open\n");
            } 
            else 
            {
                System.out.println("Currently, there is an opened database. You must close it before opening another.");
                System.out.println("\n");
            }

        } catch (Exception e) {
            System.out.println("Something went wrong, could not open the database: " + databaseName + ".");
            // e.printStackTrace();

            System.out.println("\n");
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
    void display() throws IOException
    {
        if(dataFile != null)
        {
            System.out.println("Please enter the ID of the record");
            try{
            // Scanner id = new Scanner(System.in);

            String numID = input.nextLine();

            System.out.println("Searching..." + "\n");

            if(getRecordDisplay(dataFile, search(dataFile, numID)))
            {
                System.out.println("Record found:" + "\n");

                String[] dataInfo = record.split(" ");
                for(int i = 0; i < dataInfo.length; i++)
                {
                    dataInfo[i] = dataInfo[i].replace('_', ' ');
                    System.out.println(fieldNames[i] + ": " + dataInfo[i] + " ");
                }                
            }
            else
            {
                System.out.println("Could not get Record "+numID);
                System.out.println("Record out of range\n\n");
            } 
            System.out.println("\n"); 
        }
        catch(Exception e)
        {
            System.out.println("Something went wrong");
        }
        }  
        else
        {
            System.out.println("Whoops, please open a database first\n");   
        }

    }

    // Binary Search Tree
    int search(RandomAccessFile dataFile, String ID) throws IOException 
    {
        
        int low = 0;
        configFile.seek(0);
        int high = Integer.parseInt(configFile.readLine());
        int middle = 0;
        boolean found = false;

        while (!found && (high >= low))
        {
            middle = (low + high) / 2;
            success = getRecordDisplay(dataFile, middle);
            String MiddleId[] = record.split(" ");
            System.out.println(MiddleId[0]);
            int midId = Integer.parseInt(MiddleId[0]);
            int lookId = Integer.parseInt(ID);
        
            if(midId == lookId)   
                found = true;
            else if(midId < lookId){
                low = middle + 1;
            }
            else
                high = middle - 1;
        }
        if(found)
        {
           return middle;		// the record number of the record
        }
        else
        	return -1;
    }

    // Grabbing the record
    //  I might make this a dual function rather than a getrecord for display and getrecord for insert or something. will see!
    boolean getRecordDisplay(RandomAccessFile dataFile, int recordNum) throws IOException 
    {
        configFile.seek(0);
        int numRows = Integer.parseInt(configFile.readLine());
        success = false;

        if ((recordNum >=0) && (recordNum < numRows))
        {
            dataFile.seek(0); 
            dataFile.skipBytes(recordNum * recordSize);
            record = dataFile.readLine().replaceAll("\\s+", " "); 

            if(record.contains("//"))               // Skipping blank records
            {       
                recordNum++;
                dataFile.seek(0);
                dataFile.skipBytes(recordNum * recordSize);
                record = dataFile.readLine().replaceAll("\\s+", " "); 

            }
            success = true;   
        }
        return success;
    }

    void update() throws IOException
    {
        int recordPos;
        // Scanner edit = new Scanner(System.in);

        if(dataFile != null)
        {
            System.out.println("Enter the Id of the record that you want to update");
            String recordNum = input.nextLine();

            System.out.println("Searching..." + "\n");
            recordPos = search(dataFile, recordNum);

            if(getRecordDisplay(dataFile, recordPos))
            {
                System.out.println("Record found:" + "\n");

                String[] dataInfo = record.split(" ");
                for(int i = 0; i < dataInfo.length; i++)
                {
                    dataInfo[i] = dataInfo[i].replace('_', ' ');
                    System.out.println(fieldNames[i] + ": " + dataInfo[i] + " ");
                }  
                
                System.out.println("Please type in the field name you would like to edit. For example: Visitors");

                String field = input.nextLine();
    
                System.out.println("You entered the field name: " + field + "\n");
    
    
                if(field.equals("id"))
                    System.out.println("Sorry, but we can't edit the ID");
                else
                {
                    int pos = 0;
                    int pos2 = 0;
                    int maxLength = 0;
                    switch(field) 
                    {
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
                            maxLength =11;
                            break;
                        default:
                            System.out.println("Invaild Input" + "\n");
                    }
                    dataFile.seek(0);
                    dataFile.skipBytes(((recordPos + 1) * recordSize) + pos);

                    System.out.println("Enter in the new value for " + field);
                    String temp = input.nextLine();
                    
                    int diff = 0;

                    if(temp.contains(" "))
                        temp = temp.replace(' ', '_');
                    if (temp.length() < fieldSizes[pos2]) // Making the fields their required sizes with spaces as holders
                        diff = fieldSizes[pos2] - temp.length();

                    for (int j = 0; j < diff; j++)
                        temp = temp + " ";

                    byte[] words = temp.getBytes();
                    dataFile.write(words);
                    // System.out.println(dataInfo[pos]); 
                    dataFile.close();
                }
            }
            else
                System.out.println("Could not get Record ");
                // System.out.println("Record out of range\n\n");
        }
        else
            System.out.println("Whoops, please open a database first\n");
    }


    // Choice 6
    // Displaying the first ten real records 
    void createR() throws IOException
    {   
        if(dataFile != null)
        {     
            System.out.println("This is the first ten records:" + "\n");

            for(int i = 0; i < 10; i++)
            {
                getRecordDisplay(dataFile, i);

                String[] dataInfo = record.split(" ");

                System.out.print((i + 1) + ". ");
        
                for(int j = 0; j < dataInfo.length; j++)
                {
                    dataInfo[j] = dataInfo[j].replace('_', ' ');         // Taking out the underscores
                    
                    System.out.print(fieldNames[j] + ": " + dataInfo[j] +
                    " ");
                }
                System.out.println("\n");
            }
        }
        else
        {
            System.out.println("Whoops, please open a database first\n");    

        }
    }

    void add(){}

    void delete(){}


    public static void main(String [] args) throws IOException
    {
        FileData database =  new FileData();

        // Menu Choices
        System.out.println("Welcome!" + "\n" + "Please select from the following: \n" + "1: create a new database\n" + "2: Open Database\n" + "3: Close Database\n" + "4: Display record\n" + "5: Update record\n" + "6: Create Report\n" + "7: Add record\n" + "8: Delete record\n" + "9: QUIT");

            Scanner menu = new Scanner(System.in);
            String menuChoice = menu.nextLine();
            System.out.println("You have selected: " +  menuChoice + "\n");   

            if(menuChoice == "8")
            {
                System.out.println("Exiting Program...");
                System.exit(0);
            }
    
            // Recursive menu until QUIT is selected unless they choose a char then program will quit
            while(menuChoice != "8")
            {
                    switch(menuChoice) 
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
                            // System.out.println("In case 5");
                            break;
                        case "6":
                            database.createR();
                            // System.out.println("In case 6");
                            break;
                        case "7":
                            System.out.println("In case 7");
                            break;
                        case "8":
                            System.out.println("In case 8");
                        case "9":
                            System.out.println("Exiting Program...");
                            System.exit(0);
                            break;
                        default:
                            System.out.println("Invaild Input" + "\n");
                    }

                System.out.println("Welcome!" + "\n" + "Please select from the following: \n" + "1: create a new database\n" + "2: Open Database\n" + "3: Close Database\n" + "4: Display record\n" + "5: Update record\n" + "6: Create Report\n" + "7: Add record\n" + "8: Delete record\n" + "9: QUIT");
    
                menuChoice = menu.nextLine();
                System.out.println("You have selected choice: " +  menuChoice + "\n");   
    
            }
            menu.close();
    }
    // input.close();
}

