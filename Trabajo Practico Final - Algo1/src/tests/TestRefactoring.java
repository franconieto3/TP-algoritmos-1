package tests;

import importador.CSVParser;
import tabla.DataFrame;
import tabla.Column;
import java.util.List;
import java.util.ArrayList;

public class TestRefactoring {

    public static void main(String[] args) {
        CSVParser csvParser = new CSVParser();
        //Prueba 1.1

        System.out.println("\n DataFrame desde un archivo .csv que contiene encabezados \n");
        try{
            DataFrame df1 = csvParser.toDataFrame(System.getProperty("user.dir").toString()+"/Trabajo Practico Final - Algo1/prueba2.csv");
            List<Column<?>> columns = df1.getColumns();
            for (Column<?> c : columns){
                System.out.println(c);
            }
        }catch(Exception e){
            System.err.println(("Error al importar el CSV: " + e.getMessage()));
        }
    }
}
