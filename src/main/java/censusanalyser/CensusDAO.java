package censusanalyser;

public class CensusDAO {
    public String state;
    public String stateCode;
    public int population;
    public double populationDensity;
    public double totalArea;


    public CensusDAO(IndiaCensusCSV indiaCensusCSV) {
        state = indiaCensusCSV.state;
        totalArea = indiaCensusCSV.totalArea;
        populationDensity = indiaCensusCSV.populationDensity;
        population = indiaCensusCSV.population;
    }

    public CensusDAO(USCensusCSV usCensusCSV) {
        stateCode = usCensusCSV.id;
        state = usCensusCSV.state;
        population = usCensusCSV.population;
        populationDensity = usCensusCSV.population_density;
        totalArea = usCensusCSV.total_area;
    }
//    public IndiaCensusCSV getIndiaCensusCSV(){
//        return new IndiaCensusCSV(state,totalArea,(int)populationDensity,(int)population);
//    }
}
