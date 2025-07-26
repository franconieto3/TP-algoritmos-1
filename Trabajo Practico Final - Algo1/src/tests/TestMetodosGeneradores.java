package tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exceptions.InvalidShape;
import importador.CSVExporter;
import importador.CSVParser;
import tabla.*;
import java.util.function.*;

public class TestMetodosGeneradores {
    public static void main(String[] args) {
        CSVParser csvParser = new CSVParser();
        try{
            DataFrame df1 = csvParser.toDataFrame(System.getProperty("user.dir").toString()+"/Trabajo Practico Final - Algo1/prueba1.csv");
            DataFrame df2 = csvParser.toDataFrame(System.getProperty("user.dir").toString()+"/Trabajo Practico Final - Algo1/prueba2.csv");
            df1.head(5);
            df2.head(5);

            //Slicing en DataFrame 1
            System.out.println("\n Slicing en df1 \n");
            List<Object> lista_labels = new ArrayList<>();
            lista_labels.add("Nombre");
            lista_labels.add("Apellido");

            df2.slice(lista_labels,null);
 
            //Concatenación de DataFrames
            System.out.println("\n Concatencación de df1 y df2 \n");
            DataFrame df3 = df1.concatenar(df2);
            df3.head(10);
            df3.info();

            System.out.println("\n Modifico la celda de la fila 2, columna 'Edad' para que valga 25: \n");
            df3.setValue(2, "Edad", 25);

            df3.head(10);
/*
            //Filtrado
            System.out.println("\n Filtrado: conservo solo las filas donde 'Edad'>24: \n");
            Map<Label<?>, Predicate<Object>> condiciones = new HashMap<>();
            condiciones.put(new Label<>("Edad"), v -> ((Integer)v) > 24);

            DataFrame dfFiltrado = df3.filter(condiciones);
            dfFiltrado.head(5);

            System.out.println("Agrego la condición de filtrado para que solo se llamen 'Ana' \n");
            condiciones.put(new Label<>("Nombre"), v -> ((String)v).equals("Ana"));

            DataFrame dfFiltrado2 = df3.filter(condiciones);
            dfFiltrado2.head(5);
            
            //Ordenamiento
            System.out.println("\n Ordenamiento de df3 por 'Nombre' \n");
            List<Object> lista_labels_2 = new ArrayList<>();
            lista_labels_2.add("Nombre");
            DataFrame dfOrdenado = df3.sortBy(lista_labels_2, false);

            //Copio el df ordenado
            DataFrame dfCopia = dfOrdenado.copy();
            System.out.println("\n Copia \n");
            System.out.println("Se imprime una copia del primer DataFrame ordenado: \n");
            dfCopia.head(10);

            System.out.println("\n Ordenamiento de df3 por 'Edad' en orden descendente \n");
            List<Object> lista_labels_3 = new ArrayList<>();
            lista_labels_3.add("Edad");
            DataFrame dfOrdenado2 = df3.sortBy(lista_labels_3, true);
            dfOrdenado2.head(10);

            //Sampleo el 60% de df3
            System.out.println("\n Sampleo el 60% de df3: \n");
            df3.sample(60).head(10);
*/
            //CSVExporter exporter = new CSVExporter();
            //exporter.exportDataFrame(System.getProperty("user.dir").toString()+"/Trabajo Practico Final - Algo1/prueba4.csv", df3);

        }catch(Exception e){
            System.err.println(("Error al importar el CSV: " + e.getMessage()));;
        }

    }
}
