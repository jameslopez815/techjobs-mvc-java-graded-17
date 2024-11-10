package org.launchcode.techjobsmvc.models;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.launchcode.techjobsmvc.NameSorter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LaunchCode
 */
public class JobData {

    private static final String DATA_FILE = "job_data.csv";
    private static boolean isDataLoaded = false;

    private static ArrayList<Job> allJobs;
    private static ArrayList<Employer> allEmployers = new ArrayList<>();
    private static ArrayList<Location> allLocations = new ArrayList<>();
    private static ArrayList<PositionType> allPositionTypes = new ArrayList<>();
    private static ArrayList<CoreCompetency> allCoreCompetency = new ArrayList<>();

    /**
     * Fetch list of all job objects from loaded data,
     * without duplicates, then return a copy.
     */

    public static ArrayList<Job> findAll() {

        // load data, if not already loaded
        loadData();

        // Bonus mission; normal version returns allJobs
        return new ArrayList<>(allJobs);
    }

    /**
     * Returns the results of searching the Jobs data by field and search term.
     *
     * For example, searching for employer "Enterprise" will include results
     * with "Enterprise Holdings, Inc".
     *
     * @param column Job field that should be searched.
     * @param value Value of the field to search for.
     * @return List of all jobs matching the criteria.
     */
    public static ArrayList<Job> findByColumnAndValue(String column, String value) {

        // load data, if not already loaded
        loadData(); // * Ensures the job data is available for processing. If already loaded, nothing will occur.

        ArrayList<Job> jobs = new ArrayList<>(); // * Initialize an empty list, storing jobs that match the search criteria.


        if (value.toLowerCase().equals("all")){ // * Input "all" as the value, it will return all job entries without further filtering.
            return findAll();
        }

        if (column.equals("all")){ // * If the column specified is "all", it will retrieve jobs that match the given value across all columns.
            jobs = findByValue(value);
            return jobs;
        }
        for (Job job : allJobs) { // * Loops through each job in the list. If it does.

            String aValue = getFieldValue(job, column); // * Retrieves the value for the specified column,

            if (aValue != null && aValue.toLowerCase().contains(value.toLowerCase())) { // * Checks if column contains the search value (case-insensitive)
                jobs.add(job); //* The job is added to the results list.
            }
        }

        return jobs; // * Returns the list of jobs that matched the criteria.
    }

    public static String getFieldValue(Job job, String fieldName){ //* public static, meaning it can be accessed from anywhere in the program without needing an instance of the class.
        String theValue; //* Takes a Job object and a String representing the field name.
        if (fieldName.equals("name")){
            theValue = job.getName();
        } else if (fieldName.equals("employer")){
            theValue = job.getEmployer().toString();
        } else if (fieldName.equals("location")){
            theValue = job.getLocation().toString();
        } else if (fieldName.equals("positionType")){
            theValue = job.getPositionType().toString(); // * if-else statements to check the value of fieldName, retrieving respective values.
        } else {
            theValue = job.getCoreCompetency().toString(); // * If no match, it returns the core competency of the job.
        }

        return theValue; // * Returns the value of the specified field as a String.
    }
    /**
     * Search all Job fields for the given term.
     *
     * @param value The search term to look for.
     * @return      List of all jobs with at least one field containing the value.
     */
    public static ArrayList<Job> findByValue(String value) {

        // load data, if not already loaded
        loadData();

        ArrayList<Job> jobs = new ArrayList<>(); // * Create a list to hold matching jobs

        for (Job job : allJobs) {

            if (job.getName().toLowerCase().contains(value.toLowerCase())) {
                jobs.add(job);
            } else if (job.getEmployer().toString().toLowerCase().contains(value.toLowerCase())) {
                jobs.add(job);
            } else if (job.getLocation().toString().toLowerCase().contains(value.toLowerCase())) {
                jobs.add(job);
            } else if (job.getPositionType().toString().toLowerCase().contains(value.toLowerCase())) {
                jobs.add(job);
            } else if (job.getCoreCompetency().toString().toLowerCase().contains(value.toLowerCase())) {
                jobs.add(job);
            } // * Check each job's attributes to see if they contain the search term. If a match, the job is added to the jobs list. Case insensitivity.

        }

        return jobs; // * Return the list of matching jobs
    }

    private static Object findExistingObject(ArrayList list, String value){
        for (Object item : list){
            if (item.toString().toLowerCase().equals(value.toLowerCase())){
                return item;
            }
        }
        return null;
    }

    /**
     * Read in data from a CSV file and store it in an ArrayList of Job objects.
     */
    private static void loadData() {

        // Only load data once
        if (isDataLoaded) {
            return;
        }

        try {

            // Open the CSV file and set up pull out column header info and records
            Resource resource = new ClassPathResource(DATA_FILE);
            InputStream is = resource.getInputStream();
            Reader reader = new InputStreamReader(is);
            CSVParser parser = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);
            List<CSVRecord> records = parser.getRecords();
            Integer numberOfColumns = records.get(0).size();
            String[] headers = parser.getHeaderMap().keySet().toArray(new String[numberOfColumns]);

            allJobs = new ArrayList<>();

            // Put the records into a more friendly format
            for (CSVRecord record : records) {

                String aName = record.get(0);
                String anEmployer = record.get(1);
                String aLocation = record.get(2);
                String aPosition = record.get(3);
                String aSkill = record.get(4);

                Employer newEmployer = (Employer) findExistingObject(allEmployers, anEmployer);
                Location newLocation = (Location) findExistingObject(allLocations, aLocation);
                PositionType newPosition = (PositionType) findExistingObject(allPositionTypes, aPosition);
                CoreCompetency newSkill = (CoreCompetency) findExistingObject(allCoreCompetency, aSkill);

                if (newEmployer == null){
                    newEmployer = new Employer(anEmployer);
                    allEmployers.add(newEmployer);
                }

                if (newLocation == null){
                    newLocation = new Location(aLocation);
                    allLocations.add(newLocation);
                }

                if (newSkill == null){
                    newSkill = new CoreCompetency(aSkill);
                    allCoreCompetency.add(newSkill);
                }

                if (newPosition == null){
                    newPosition = new PositionType(aPosition);
                    allPositionTypes.add(newPosition);
                }

                Job newJob = new Job(aName, newEmployer, newLocation, newPosition, newSkill);

                allJobs.add(newJob);
            }
            // flag the data as loaded, so we don't do it twice
            isDataLoaded = true;

        } catch (IOException e) {
            System.out.println("Failed to load job data");
            e.printStackTrace();
        }
    }

    public static ArrayList<Employer> getAllEmployers() {
        loadData();
        allEmployers.sort(new NameSorter());
        return allEmployers;
    }

    public static ArrayList<Location> getAllLocations() {
        loadData();
        allLocations.sort(new NameSorter());
        return allLocations;
    }

    public static ArrayList<PositionType> getAllPositionTypes() {
        loadData();
        allPositionTypes.sort(new NameSorter());
        return allPositionTypes;
    }

    public static ArrayList<CoreCompetency> getAllCoreCompetency() {
        loadData();
        allCoreCompetency.sort(new NameSorter());
        return allCoreCompetency;
    }

}


