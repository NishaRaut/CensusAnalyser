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

    public Map<String, CensusDAO> loadCensusData(CensusAnalyser.Country country, String... csvFilePath) throws CensusAnalyserException {
        if(country.equals(CensusAnalyser.Country.INDIA))
          return this.loadCensusData(IndiaCensusCSV.class,csvFilePath);
        else if(country.equals(CensusAnalyser.Country.US))
           return this.loadCensusData(USCensusCSV.class,csvFilePath);
        else throw new CensusAnalyserException("incorrect country",CensusAnalyserException.ExceptionType.INVALID_COUNTRY);
    }
    private <E> Map<String, CensusDAO> loadCensusData(Class<E> censusCSVClass, String... csvFilePath) throws CensusAnalyserException {
        Map<String, CensusDAO> censusCSVMap = new HashMap<>();
        try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath[0]))) {
            ICSVBuilder csvBuilder = CSVBulderFactory.createCSVBuilder();
            Iterator<E> censusCSVIterator = csvBuilder.getCSVFileIterator(reader, censusCSVClass);
            Iterable<E> csvIterable = () -> censusCSVIterator;
            if (censusCSVClass.getName().equals("censusanalyser.IndiaCensusCSV")) {
                StreamSupport.stream(csvIterable.spliterator(), false)
                        .map(IndiaCensusCSV.class::cast)
                        .forEach(censusCSV -> {
                            censusCSVMap.put(censusCSV.state, new CensusDAO(censusCSV));
                        });
            } else if (censusCSVClass.getName().equals("censusanalyser.USCensusCSV")) {
                StreamSupport.stream(csvIterable.spliterator(), false)
                        .map(USCensusCSV.class::cast)
                        .forEach(censusCSV -> {
                            censusCSVMap.put(censusCSV.state, new CensusDAO(censusCSV));
                        });
            }
            if (csvFilePath.length == 1) return censusCSVMap;
            this.loadIndiaCensusCode(censusCSVMap, csvFilePath[1]);
            return censusCSVMap;
        } catch (IOException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.CENSUS_FILE_PROBLEM);
        } catch (CSVBuilderException e) {
            throw new CensusAnalyserException(e.getMessage(), e.type.name());
        }
    }

    public int loadIndiaCensusCode(Map<String, CensusDAO> censusCSVMap, String csvFilePath)throws CensusAnalyserException {

        try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath))) {

            ICSVBuilder csvBuilder = CSVBulderFactory.createCSVBuilder();
            Iterator<IndiaStateCodeCSV> censusCSVStateCodeIterator = csvBuilder.getCSVFileIterator(reader,IndiaStateCodeCSV.class);
            Iterable<IndiaStateCodeCSV> csvIterable = () -> censusCSVStateCodeIterator;
            StreamSupport.stream(csvIterable.spliterator(),false)
                    .filter(csvState -> censusCSVMap.get(csvState.state) != null)
                    .forEach(csvState-> censusCSVMap.get(csvState.state).state = csvState.state);
            return censusCSVMap.size();
        } catch (IOException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.CENSUS_FILE_PROBLEM);
        } catch (CSVBuilderException e) {
            throw new CensusAnalyserException(e.getMessage(),e.type.name());
        }
    }

}
