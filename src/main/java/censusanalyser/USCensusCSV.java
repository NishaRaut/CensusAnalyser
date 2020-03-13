package censusanalyser;

import com.opencsv.bean.CsvBindByName;

public class USCensusCSV {

    @CsvBindByName(column = "State Id", required = true)
    public String id;

    @CsvBindByName(column = "State", required = true)
    public String state;

    @CsvBindByName(column = "Population", required = true)
    public int population;

    @CsvBindByName(column = "Housing units", required = true)
    public String housing_units;

    @CsvBindByName(column = "Total area", required = true)
    public double total_area;

    @CsvBindByName(column = "Water area", required = true)
    public double water_area;

    @CsvBindByName(column = "Land area", required = true)
    public double land_area;

    @CsvBindByName(column = "Population Density", required = true)
    public double population_density;

    @CsvBindByName(column = "Housing Density", required = true)
    public double housing_density;
}
