package censusanalyser;



import com.google.gson.Gson;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.StreamSupport;

public class CensusAnalyser {
    List <CensusDAO> censusList = null;
    Map<SortField,Comparator<CensusDAO>> censusStateSortMap = null;
    Map<String, CensusDAO> stateMap = null;

    public CensusAnalyser() {
        this.censusList = new ArrayList<CensusDAO>();
        this.censusStateSortMap = new HashMap<>();
        this.stateMap = new HashMap<>();
        this.censusStateSortMap.put(SortField.STATE,Comparator.comparing(census -> census.state)) ;
        this.censusStateSortMap.put(SortField.POPULATION,Comparator.comparing(census -> census.population));
        this.censusStateSortMap.put(SortField.AREAINSQKM,Comparator.comparing(census -> census.totalArea));
        this.censusStateSortMap.put(SortField.DENSITYPERSQKM,Comparator.comparing(census -> census.populationDensity));
       // this.stateMap.put(SortField.STATE);
    }

    public int loadIndiaCensusData(String csvFilePath) throws CensusAnalyserException {
        return this.loadCensusData(csvFilePath,IndiaCensusCSV.class);
    }

    private <E> int  loadCensusData(String csvFilePath, Class<E> censusCSVClass) throws CensusAnalyserException {
        try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath))) {
            ICSVBuilder csvBuilder = CSVBulderFactory.createCSVBuilder();
            Iterator<E> censusCSVIterator = csvBuilder.getCSVFileIterator(reader,censusCSVClass);
            Iterable<E> csvIterable = () -> censusCSVIterator;
//            while(censusCSVIterator.hasNext())
//            {
//                System.out.println(censusCSVIterator.next());
//            }
            if(censusCSVClass.getName().equals("censusanalyser.IndiaCensusCSV")) {
                StreamSupport.stream(csvIterable.spliterator(), false)
                        .map(IndiaCensusCSV.class::cast)
                        .forEach(censusCSV -> {
                            stateMap.put(censusCSV.state, new CensusDAO(censusCSV));
                        });
            }
            else if(censusCSVClass.getName().equals("censusanalyser.USCensusCSV")) {
                StreamSupport.stream(csvIterable.spliterator(), false)
                        .map(USCensusCSV.class::cast)
                        .forEach(censusCSV -> {
                            stateMap.put(censusCSV.state, new CensusDAO(censusCSV));
                        });
            }
            return stateMap.size();
        } catch (IOException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.CENSUS_FILE_PROBLEM);
        }catch (CSVBuilderException e) {
            throw new CensusAnalyserException(e.getMessage(),e.type.name());
        }
    }

    public int loadUSCensusData(String csvFilePath) throws CensusAnalyserException {
        return this.loadCensusData(csvFilePath,USCensusCSV.class);
    }

    public int loadIndiaCensusCode(String csvFilePath)throws CensusAnalyserException {

        try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath))) {

            ICSVBuilder csvBuilder = CSVBulderFactory.createCSVBuilder();
            Iterator<IndiaStateCodeCSV> censusCSVStateCodeIterator = csvBuilder.getCSVFileIterator(reader,IndiaStateCodeCSV.class);
            Iterable<IndiaStateCodeCSV> csvIterable = () -> censusCSVStateCodeIterator;
            StreamSupport.stream(csvIterable.spliterator(),false)
                    .filter(csvState -> stateMap.get(csvState.state) != null)
                    .forEach(csvState-> stateMap.get(csvState.state).state = csvState.state);
           return stateMap.size();
        } catch (IOException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.CENSUS_FILE_PROBLEM);
        } catch (CSVBuilderException e) {
            throw new CensusAnalyserException(e.getMessage(),e.type.name());
        }
    }

    private <E> int getCount(Iterator<E> iterator)
    {
        Iterable<E> csvIterable = () -> iterator;
        int numOfEntries = (int) StreamSupport.stream(csvIterable.spliterator(),false).count();
        return numOfEntries;
    }


    public String getStateWiseSortedCensurData(SortField state) throws CensusAnalyserException {
        if( censusList == null || censusList.size() == 0)
        {
            throw new CensusAnalyserException("No Census Data",CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
           // Comparator<IndiaCensusDAO> censusComparator = Comparator.comparing(census -> census.state);
            this.sort(this.censusStateSortMap.get(state));
            String sortedStateCensusJSON = new Gson().toJson(censusList);
            return sortedStateCensusJSON;
    }

    private void sort( Comparator<CensusDAO> censusCoparator) {
        for (int i=0; i<censusList.size()-1; i++) {
            for (int j = 0; j < censusList.size() - i - 1; j++) {

                CensusDAO census1 = censusList.get(j);
                CensusDAO census2 = censusList.get(j+1);
                if (censusCoparator.compare(census1,census2) > 0 )
                {
                    censusList.set(j,census2);
                    censusList.set(j+1,census1);
                }
            }
        }
    }


}