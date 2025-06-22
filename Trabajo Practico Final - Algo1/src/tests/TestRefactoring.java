package tests;

import importador.CSVParser;

import tabla.*;
import java.util.List;


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

            df1.head(2);

            df1.tail(2);

            df1.info();
            
            System.out.println(df1.getRow(3));
            System.out.println(df1.getColumn("Apellido"));
            System.out.println(df1.getCell(3, "Apellido"));

        }catch(Exception e){
            System.err.println(("Error al importar el CSV: " + e.getMessage()));
        }
    }
}
