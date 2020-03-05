package censusanalyser;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.Reader;
import java.util.Iterator;

public class OpenCSVBuilder implements ICSVBuilder{

    public <E> Iterator<E> getCSVFileIterator(Reader reader, Class<E> csvClass) throws CSVBuilderException {
        try {
            CsvToBeanBuilder<E> csvToBeanBuilder = new CsvToBeanBuilder<>(reader);
            csvToBeanBuilder.withType(csvClass);
            csvToBeanBuilder.withIgnoreLeadingWhiteSpace(true);
            CsvToBean<E> csvToBean = csvToBeanBuilder.build();
            return csvToBean.iterator();
        } catch (IllegalStateException e) {
            throw new CSVBuilderException(e.getMessage(),
                                                CSVBuilderException.ExceptionType.CENSUS_FILE_PROBLEM);
        }

//    private Iterator<IndiaCensusCSV> getCSVFileIterator1(Reader reader,IndiaCensusCSV csvClass) throws CensusAnalyserException {}
//    private Iterator<IndiaCensusCSV> getCSVFileIterator2(Reader reader,IndiaCensusCSV csvClass) throws CensusAnalyserException {}
    }
}
