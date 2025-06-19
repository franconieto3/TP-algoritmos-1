package tabla;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.*;
import java.util.Collections;

import exceptions.*;

public class DataFrame {

    // --- 0. atributos ---

    private List<Column> columns;
    private List<Row> rows;
    private DataFrameHandler handler;
      
    // --- 1.0 Constructores ---

    // Constructor por defecto
    public DataFrame() {
        this.columns = new ArrayList<>();
        this.rows = new ArrayList<>();
        this.handler = new DataFrameHandler(this);
    }

    // Constructor desde matriz 2D
    public <T> DataFrame(T[][] array2D, List<T> columnLabels, List<T> rowLabels) throws InvalidShape, IllegalArgumentException,InvalidTypeException {
        this();
        List<List<T>> data = new ArrayList<>();
        for (T[] row : array2D) {
            data.add(Arrays.asList(row));
        }

        List<Label<T>> labelsC = adaptarLabels(columnLabels);
        List<Label<T>> labelsR = adaptarLabels(rowLabels);

        generarDataFrame(data, labelsC, labelsR);
    }

    // Constructor desde lista de listas 
    public <T> DataFrame(List<? extends List<?>> data, List<T> columnLabels, List<T> rowLabels) throws InvalidShape, IllegalArgumentException,InvalidTypeException {
        this();

        List<Label<T>> labelsC = adaptarLabels(columnLabels);
        List<Label<T>> labelsR = adaptarLabels(rowLabels);

        generarDataFrame(data, labelsC, labelsR);
    }

    // Constructor desde una sola columna
    public <T> DataFrame(List<Object> columnData, T columnLabel, List<T> rowLabels) throws InvalidShape, IllegalArgumentException,InvalidTypeException {
        this();
        List<List<Object>> data = new ArrayList<>();
        for (Object value : columnData) {
            List<Object> row = new ArrayList<>();
            row.add(value);
            data.add(row);
        }

        List<Label<T>> labelsC = adaptarLabels(List.of(columnLabel));
        List<Label<T>> labelsR = adaptarLabels(rowLabels);

        generarDataFrame(data, labelsC, labelsR);
    }
     /* 

    public <T> DataFrame(Map<Object, List<Object>> dataMap) throws InvalidShape, IllegalArgumentException, InvalidTypeException {
        //Llama al constructor por defecto
        this();
        //Verifica que el mapa no sea nulo o vacío
        if (dataMap == null || dataMap.isEmpty()) {
            throw new IllegalArgumentException("El mapa de datos no puede estar vacío.");
        }
        // Verifica que las columnas tengan la misma cantidad de elementos y calcula la cantidad de datos por columna.
        int expectedSize = -1;
        for (Map.Entry<Object, List<Object>> entry : dataMap.entrySet()) {
            if (entry.getValue() == null) {
                throw new IllegalArgumentException("Los datos de una columna no pueden ser nulos.");
            }
            if (expectedSize == -1) {
                expectedSize = entry.getValue().size();
            } else if (entry.getValue().size() != expectedSize) {
                throw new InvalidShape("Las columnas no tienen la misma cantidad de filas.");
            }
        }

        // Inicializa la firma del constructor DataFrame
        List<T> columnLabels = new ArrayList<>(dataMap.keySet()); //Crea las labels de columnas
        List<Object> rowLabels = new ArrayList<>();
        List<List<T>> data = new ArrayList<>();
        //Crea las labels de filas.
        for (int i = 0; i < expectedSize; i++) {
            data.add(new ArrayList<>());
            rowLabels.add(i);
        }
        //Crea la data del DataFrame a partir del mapa.
        for (T colLabel : columnLabels) {
            List<Object> colData = dataMap.get(colLabel);
            for (int i = 0; i < expectedSize; i++) {
                data.get(i).add(colData.get(i));
            }
        }
        //Construye el DataFrame con Labels y data.
        List<Label<T>> labelsC = adaptarLabels(columnLabels);
        List<Label<T>> labelsR = adaptarLabels(rowLabels);
        generarDataFrame(data, labelsC, labelsR);
    }*/

    // Constructor copia
    public DataFrame(DataFrame other) {

        // Copia profunda
        
        this.columns = new ArrayList<>();
        this.rows = new ArrayList<>();

        for (Column col : other.columns) {
            this.columns.add(new Column(col)); 
        }

        for (Row row : other.rows) {
            this.rows.add(new Row(row)); 
        }
    }

    // --- 0.1 Metodos auxiliares de constructores ---

    private <T> List<Label<T>> adaptarLabels(List<T> labels) throws IllegalArgumentException{
        
        List<Label<T>> aux = new ArrayList<>();

        if (labels == null || labels.isEmpty()) {
            return aux; // Devuelve lista vacía
        }

        for (T l:labels){
            Label<T> label = new Label<>(l);
            aux.add(label);
        }
        return aux;
    }
      
    
    // --- 1.1 Metodos auxiliares de generaDataFrame ---


    // Método interno para poblar el dataframe
    private <T> void generarDataFrame(List<? extends List<?>> rows, List<? extends Label<?>> columnLabels, List<? extends Label<?>> rowLabels) throws InvalidShape, InvalidTypeException, IllegalArgumentException {
        //Verifica que todas las listas dentro de rows tengan el mismo tamaño
        validateRowSize(rows);
        
        //Para la lista de filas, se verifica que el largo de cada fila coincida con la cantidad de headers de columnas
        if(!columnLabels.isEmpty()){
            validateRowShape(rows, columnLabels.size());
        }

        //Manejar labels de columnas: generar nuevos si no label=null, o validar que las labels sean consistentes con la data
        if(columnLabels == null || columnLabels.isEmpty()){
            columnLabels = generateLabels(rows.get(0).size());
        }
        //Manejar labels de filas: generar nuevos si no label=null, o validar que las labels sean consistentes con la data
        if(rowLabels == null || rowLabels.isEmpty()){
            rowLabels = generateLabels(rows.size());
        }
        //Evita que dos Labels sean iguales
        validarLabelsUnicos(columnLabels);
        validarLabelsUnicos(rowLabels);
        
        fillColumns(rows, columnLabels);
        fillRows(rowLabels);
    }

    private void validateRowSize(List<? extends List<?>> rows) throws InvalidShape{
        //Si las filas tienen distinto tamaño se arroja una excepción
        int ref =rows.get(0).size();
        for (int i=1;i<rows.size();i++ ){
            if (rows.get(i).size()!=ref){
                throw new InvalidShape();
            }
        }
    }

    private void validateRowShape(List<? extends List<?>> rows, int expectedSize)throws InvalidShape{
        for (List<?> row : rows){
            if(row.size()!=expectedSize){
                throw new InvalidShape();
            }
        }
    }

    private <T> List<Label<Integer>> generateLabels(int size){//argumento puede ser de tipo int: expectedSize
        List<Label<Integer>> labels = new ArrayList<>();
        for(int i=0; i<size; i++){
            labels.add(new Label<Integer>(i));
        }
        return labels;
    }

    private static void validarLabelsUnicos(List<? extends Label<?>> labels) throws IllegalArgumentException {

        Set<Object> valoresVistos = new HashSet<>();

        for (Label<?> label : labels) {
            Object valor = label.getLabel();
            if (!valoresVistos.add(valor)) {
                throw new IllegalArgumentException("No pueden haber dos labels con el mismo nombre: " + valor);
            }
        }
    }
    

    private <T> void fillColumns(List<? extends List<?>> rows, List<? extends Label<?>> columnLabels)throws InvalidTypeException{
        for (int i = 0; i < columnLabels.size(); i++) {
            Label<?> label = columnLabels.get(i);
            Column column = new Column(label);
            
            
            for (List<?> row : rows) {
                Object value = row.get(i);
                if (value == null || value.toString().equalsIgnoreCase("N/A")) {
                    column.addCell(null); // valores faltantes tratados como null
                    continue;
                }
                if(value instanceof Number){
                    Number v = (Number) value;
                    column.addCell(new Cell<>(v));
                }
                else if(value instanceof String){
                    String v = (String) value;
                    column.addCell(new Cell<>(v));
                }
                else if(value instanceof Boolean){
                    Boolean v = (Boolean) value;
                    column.addCell(new Cell<>(v));
                }else{
                    throw new IllegalArgumentException("Tipo no soportado en la columna '" + label + "': " + value.getClass());
                }
            }
            columns.add(column);
        }



    }


    private void fillRows(List<? extends Label<?>> rowLabels){
        for (int i = 0; i < rowLabels.size(); i++) {
            this.rows.add(new Row(rowLabels.get(i),i));
        }
    }



    public static final Object NA = new Object() {
    @Override
        public String toString() {
            return "N/A";
        }
    };

    //--- 2.0 Getters ---

    public List<Column> getColumns(){
        return new ArrayList<>(columns);
    }
    public List<Row> getRows(){
        return new ArrayList<>(rows);
    }
    public int contarColumnas(){
        if(columns==null){
            return 0;
        }
        return columns.size(); 
    }
    public int contarFilas(){
        if(rows==null){
            return 0;
        }
        return rows.size();
    }
    public List<Label> getColumnLabels(){
        List<Label> lista = new ArrayList<>();
        for(Column c: columns){
            lista.add(c.getLabel());
        }
        return lista;
    }
    public List<Label> getRowLabels(){
        List<Label> lista = new ArrayList<>();
        for(Row r: rows){
            lista.add(r.getLabel());
        }
        return lista;  
    }

    // --- 3.0 Metodos de Visualización ---

    public void head(int n){
        
        int k = 0;
        List<Label> colLabels = new ArrayList<>();
        List<Label> rowLabels = new ArrayList<>();
        List<List<Object>> ListOfRows = new ArrayList<>();

        for(Column c:columns){
            colLabels.add(c.getLabel());
        }
        for(Row row: rows){
           if(k==n){break;}
            int j = row.getIndex();
            rowLabels.add(row.getLabel());
            List<Object> rowList = buildRow(j,columns);
            ListOfRows.add(rowList);
            k++;
        }

        DataFrameView tabla = new DataFrameView();
        System.out.println(tabla.formatTable(ListOfRows, rowLabels, colLabels));
    }

    public void tail(int n) {
        
        int totalRows = rows.size();
        int start = Math.max(0, totalRows - n); // En caso de que n > totalRows
        
        List<Label> colLabels = new ArrayList<>();
        List<Label> rowLabels = new ArrayList<>();
        List<List<Object>> listOfRows = new ArrayList<>();

        for (Column c : columns) {
            colLabels.add(c.getLabel());
        }

        for (int i = start; i < totalRows; i++) {
            Row row = rows.get(i);
            int j = row.getIndex();
            rowLabels.add(row.getLabel());
            List<Object> rowList = buildRow(j,columns);
            listOfRows.add(rowList);
        }

        DataFrameView tabla = new DataFrameView();
        System.out.println(tabla.formatTable(listOfRows, rowLabels, colLabels));
    }

    public void info(){

        System.out.println(" \n" + "Data columns: total "+this.columns.size());

        for (Column c : columns){

            Label label = c.getLabel();
            int na = c.countNA();
            Class<?> tipo =c.getType();

            System.out.println(label + ": "+ (columns.size()-na) + " non-null, "+ tipo);
        }
    }

    //Acceso indexado



    // --- 3.1 Metodos auxiliares de Visualización ---
    
    protected List<Object> buildRow(int i, List<Column> list){
        List<Object> row = new ArrayList<>();
        for (Column c : list){
            row.add(c.getCell(i).getValue());
        }
        //System.out.println(i+": "+ row);
        return row;
    }



    // --- 4.0 Metodos de acceso indexado y selección ---

    public Row obtenerFila(Object input) throws IndexOutOfBoundsException {
        int index = -1;

        if (input instanceof Label) {
            index = buscarFila((Label) input);
        } else if (input instanceof String) {
            index = buscarFila(new Label((String) input));
        } else if (input instanceof Integer) {
            index = (Integer) input;
            if (index < 0 || index >= rows.size()) {
                throw new IndexOutOfBoundsException("Índice fuera de rango: " + index);
            }
        } else {
            throw new IllegalArgumentException("Tipo de argumento no soportado: " + input.getClass());
        }
        return new Row(rows.get(index));
    }

    protected int buscarFila(Label label) {
        for (int i = 0; i < rows.size(); i++) {
            if (label.equals(rows.get(i).getLabel())) {
                return i;
            }
        }
        throw new IndexOutOfBoundsException("Fila con etiqueta no encontrada: " + label);
    }

    public Column obtenerColumna(Object input) throws IndexOutOfBoundsException {
        int index = -1;

        if (input instanceof Label) {
            index = buscarColumna((Label) input);
        } else if (input instanceof String) {
            index = buscarColumna(new Label((String) input));
        } else if (input instanceof Integer) {
            index = (Integer) input;
            if (index < 0 || index >= columns.size()) {
                throw new IndexOutOfBoundsException("Índice fuera de rango: " + index);
            }
        } else {
            throw new IllegalArgumentException("Tipo de argumento no soportado: " + input.getClass());
        }

        // Obtener la columna y construir la lista de celdas
        return new Column(columns.get(index));
    }

    public Cell<?> obtenerCelda(Object rowLabel, Object columnLabel){
        int indice = this.obtenerFila(rowLabel).getIndex();
        Cell celda = this.obtenerColumna(columnLabel).getCell(indice);
        return new Cell<>(celda);
    }

    protected int buscarColumna(Label label) {
        for (int i = 0; i < columns.size(); i++) {
            if (label.equals(columns.get(i).getLabel())) {
                return i;
            }
        }
        throw new IndexOutOfBoundsException("Fila con etiqueta no encontrada: " + label);
    }



    //Modificación del DataFrame

    //Modificación de una celda
    public void setValue(Object rowLabel, Object columnLabel, Object newValue) {
        int rowIndex = buscarFila(new Label(rowLabel));
        int colIndex = buscarColumna(new Label(columnLabel));

        Column col = columns.get(colIndex);
        Class<?> expectedType = col.getType();

        if (newValue != null && !expectedType.isInstance(newValue)) {
            throw new IllegalArgumentException(
                "Tipo incompatible: se esperaba " + expectedType.getSimpleName() +
                " pero se recibió " + newValue.getClass().getSimpleName()
            );
        }
        
        columns.get(colIndex).getCell(rowIndex).setValue(newValue);
    }

    //Inserción de una columna
    public void addColumn(Column newColumn) {
        if (newColumn.getCells().size() != rows.size()) {
            throw new IllegalArgumentException("La nueva columna no tiene la misma cantidad de filas.");
        }
        columns.add(newColumn);
    }

    //Inserción de una columna a partir de una secuencia lineal de Java
  
    public void addColumnFromList(List<?> data, Object label) {
        if (data.size() != rows.size()) {
            throw new IllegalArgumentException("La lista no tiene la misma cantidad de filas.");
        }
        Column column = new Column(new Label<>(label));

        for (Object value : data) {

            if (value == null || value.toString().equalsIgnoreCase("N/A")) {
                column.addCell(new Cell(new MissingValue())); // valores faltantes tratados como null
                continue;
            }

            if (!(value instanceof Number || value instanceof Boolean || value instanceof String)) {
                throw new IllegalArgumentException("Tipo no soportado en la columna '" + label + "': " + value.getClass());
            }   
            column.addCell(new Cell(value));
        }
        columns.add(column);
    }

    //Eliminación de una columna
    public void removeColumn(Object columnLabel) {
        int index = buscarColumna(new Label(columnLabel));
        columns.remove(index);
    }
    //Eliminación de una fila
    public void removeRow(Object rowLabel) {
        int index = buscarFila(new Label(rowLabel));
        rows.remove(index);
        for (Column col : columns) {
            col.getCells().remove(index);
        }
        //Reacomodo los indices de las filas
        for (int i = 0; i<rows.size();i++){
            rows.get(i).setIndex(i);
        }
    }

    //Slicing
    public void slice(List<?> columnLabels, List<?> rowLabels){
        handler.slice(columnLabels, rowLabels);
    }

    //Copia
    public DataFrame copy(){
        return new DataFrame(this);
    }

    //Filtrado

   public DataFrame filter(Map<Object, Predicate<Object>> conditions){
        return handler.filter(conditions);
   }

   //Ordenamiento

   public DataFrame sortBy(List<? extends Object> labels, boolean descending){
        return handler.sortBy(labels, descending);
   }

   //Concatenación
   public DataFrame concatenar(DataFrame other){
        return handler.concatenar(other);
   }

   //Sampleo

   public DataFrame sample(int n){
    return handler.sample(n);
   }
   
   //Imputación de valores faltantes
   public void fillna(Object label, Object value){
        int colIndex = buscarColumna(new Label(label));
        Column column = columns.get(colIndex);

    Class<?> expectedType = column.getType();

    if (value != null && !expectedType.isInstance(value)) {
        throw new IllegalArgumentException(
            "Tipo incompatible: se esperaba " + expectedType.getSimpleName() +
            " pero se recibió " + value.getClass().getSimpleName()
        );
    }

    for (int i=0; i< column.size(); i++) {
            List<Cell<?>> cellList = column.getCells();
            Cell celda = cellList.get(i);
            if (celda.getValue() instanceof MissingValue) {
                column.getCell(i).setValue(value);
            }
    }
    
   }
    
}
