package censusanalyser;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.StreamSupport;

public class CensusLoader {
    public <E> Map<String,CensusDAO> loadCensusData(String csvFilePath, Class<E> censusCSVClass) throws CensusAnalyserException {
        Map<String,CensusDAO> stateMap = new HashMap<>();
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
            return stateMap;
        } catch (IOException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.CENSUS_FILE_PROBLEM);
        }catch (CSVBuilderException e) {
            throw new CensusAnalyserException(e.getMessage(),e.type.name());
        }
    }
}
