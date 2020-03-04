package censusanalyser;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.stream.StreamSupport;

public class CensusAnalyser {
   // Iterator<IndiaCensusCSV> censusCSVIterator = null;
   // Iterator<IndiaStateCodeCSV> censusCSVStateIterator = null;
    public int loadIndiaCensusData(String csvFilePath) throws CensusAnalyserException {
        try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));) {
            // Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));
            Iterator <IndiaCensusCSV> censusCSVIterator = new OpenCSVBuilder().getCSVFileIterator(reader,IndiaCensusCSV.class);
            Iterable csvIterable = () -> censusCSVIterator;
            int numOfEntries = (int) StreamSupport.stream(csvIterable.spliterator(),false).count();
            return numOfEntries;
            //censusCSVIterator = csvToBean.iterator();
//            while (censusCSVIterator.hasNext()) {
//                namOfEateries++;
//                IndiaCensusCSV censusData = censusCSVIterator.next();
//            }
//            return namOfEateries;
        } catch (IOException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.CENSUS_FILE_PROBLEM);
        }
    }

    public int loadIndiaCensusCode(String csvFilePath)throws CensusAnalyserException {

        try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));) {
            // Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));
            Iterator <IndiaStateCodeCSV> censusCSVStateCodeIterator = new OpenCSVBuilder().getCSVFileIterator(reader,IndiaStateCodeCSV.class);
//            Iterable csvIterable = () -> censusCSVStateCodeIterator;
//            int numOfEntries = (int) StreamSupport.stream(csvIterable.spliterator(),false).count();
            return this.getCount(censusCSVStateCodeIterator);
            //int namOfEateries = 0;
            //censusCSVIterator = csvToBean.iterator();
//            while (censusCSVStateCodeIterator.hasNext()) {
//                namOfEateries++;
//                IndiaStateCodeCSV censusData = censusCSVStateIterator.next();
//            }
//            return namOfEateries;
        } catch (IOException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.CENSUS_FILE_PROBLEM);
        }
    }

    private <E> int getCount(Iterator<E> iterator)
    {
        Iterable<E> csvIterable = () -> iterator;
        int numOfEntries = (int) StreamSupport.stream(csvIterable.spliterator(),false).count();
        return numOfEntries;
    }


}