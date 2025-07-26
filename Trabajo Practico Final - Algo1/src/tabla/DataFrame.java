package tabla;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.Random;
import java.util.function.*;


import exceptions.*;
import interfaces.Labeled;

public class DataFrame {

    // --- 0. atributos ---

    private List<Column<?>> columns;
    private List<Row> rows;
    //private DataFrameHandler handler;
      
    // --- 1.0 Constructores ---

    // Constructor por defecto
    public DataFrame() {
        this.columns = new ArrayList<>();
        this.rows = new ArrayList<>();
        //this.handler = new DataFrameHandler(this);
    }

    // Constructor desde matriz 2D
    public <T> DataFrame(T[][] array2D, List<T> columnLabels, List<T> rowLabels) throws InvalidShape, IllegalArgumentException,InvalidTypeException {
        this();
        List<List<T>> data = new ArrayList<>();
        for (T[] row : array2D) {
            data.add(Arrays.asList(row));
        }

        List<Label<T>> labelsC = adaptLabels(columnLabels);
        List<Label<T>> labelsR = adaptLabels(rowLabels);

        generarDataFrame(data, labelsC, labelsR);
    }

    // Constructor desde lista de listas 
    public <T> DataFrame(List<? extends List<?>> data, List<T> columnLabels, List<T> rowLabels) throws InvalidShape, IllegalArgumentException,InvalidTypeException {
        this();

        List<Label<T>> labelsC = adaptLabels(columnLabels);
        List<Label<T>> labelsR = adaptLabels(rowLabels);

        generarDataFrame(data, labelsC, labelsR);
    }

    // Constructor desde una sola columna
    public <T> DataFrame(List<Object> columnData, T columnLabel, List<T> rowLabels) throws InvalidShape, IllegalArgumentException,InvalidTypeException {
        this();
        List<List<Object>> data = new ArrayList<>();
        for (Object value : columnData) {
            data.add(List.of(value));
        }

        List<Label<T>> labelsC = adaptLabels(List.of(columnLabel));
        List<Label<T>> labelsR = adaptLabels(rowLabels);

        generarDataFrame(data, labelsC, labelsR);
    }

    // Constructor copia
    public DataFrame(DataFrame other) {

        // Copia profunda
        
        this.columns = new ArrayList<>();
        this.rows = new ArrayList<>();

        for (Column<?> col : other.columns) {
            this.columns.add(new Column<>(col)); 
        }

        for (Row row : other.rows) {
            this.rows.add(new Row(row)); 
        }
    }

    // --- 0.1 Metodos auxiliares de constructores ---

    private <T> List<Label<T>> adaptLabels(List<T> labels) throws IllegalArgumentException{
        
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

        //Manejar labels de columnas: generar nuevos si no label=null, o validar que las labels sean consistentes con la data
        if(columnLabels == null || columnLabels.isEmpty()){
            columnLabels = generateLabels(rows.get(0).size());
        }

        //Para la lista de filas, se verifica que el largo de cada fila coincida con la cantidad de headers de columnas
        if(!columnLabels.isEmpty()){
            validateRowShape(rows, columnLabels.size());
        }


        //Manejar labels de filas: generar nuevos si no label=null, o validar que las labels sean consistentes con la data
        if(rowLabels == null || rowLabels.isEmpty()){
            rowLabels = generateLabels(rows.size());
        }
        //Evita que dos Labels sean iguales
        UniqueLabels(columnLabels);
        UniqueLabels(rowLabels);
        
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

    private List<Label<Integer>> generateLabels(int size){//argumento puede ser de tipo int: expectedSize
        List<Label<Integer>> labels = new ArrayList<>();
        for(int i=0; i<size; i++){
            labels.add(new Label<Integer>(i));
        }
        return labels;
    }

    private static void UniqueLabels(List<? extends Label<?>> labels) throws IllegalArgumentException {

        Set<Object> valoresVistos = new HashSet<>();

        for (Label<?> label : labels) {
            Object valor = label.getLabel();
            if (!valoresVistos.add(valor)) {
                throw new IllegalArgumentException("No pueden haber dos labels con el mismo nombre: " + valor);
            }
        }
    }
    

    private void fillColumns(List<? extends List<?>> rows, List<? extends Label<?>> columnLabels)throws InvalidTypeException{
        for (int i = 0; i < columnLabels.size(); i++) {
            Label<?> label = columnLabels.get(i);
            Column<?> column = new Column<>(label);

            for (List<?> row : rows) {
                column.add((Object) row.get(i));
            }

            columns.add(column);
        }
    }


    private void fillRows(List<? extends Label<?>> rowLabels){
        for (int i = 0; i < rowLabels.size(); i++) {
            this.rows.add(new Row(rowLabels.get(i),i));
        }
    }


    //--- 2.0 Getters ---

    public List<Column<?>> getColumns(){
        return new ArrayList<>(columns);
    }
    public List<Row> getRows(){
        return new ArrayList<>(rows);
    }
    public int countColumns(){
        if(columns==null){
            return 0;
        }
        return columns.size(); 
    }
    public int countRows(){
        if(rows==null){
            return 0;
        }
        return rows.size();
    }
    public List<Label<?>> getColumnLabels(){
        List<Label<?>> lista = new ArrayList<>();
        for(Column<?> c: columns){
            lista.add(c.getLabel());
        }
        return lista;
    }
    public List<Label<?>> getRowLabels(){
        List<Label<?>> lista = new ArrayList<>();
        for(Row r: rows){
            lista.add(r.getLabel());
        }
        return lista;  
    }

    // --- 3.0 Metodos de Visualización ---
    
    public void head(int n){
        DataFrameView.viewDataFrame(0,n, this);
    }

    public void tail(int n){
        int totalRows = rows.size();
        int start = Math.max(0, totalRows - n); // En caso de que n > totalRows

        DataFrameView.viewDataFrame(start,rows.size(), this);  
    }
    
    public void info(){

        System.out.println(" \n" + "Data columns: total "+this.columns.size());
        
        for (Column<?> c : columns){

            Label<?> label = c.getLabel();
            int na = c.countNA();
            String tipo =c.getType().getSimpleName();

            System.out.println(label + ": "+ (columns.size()-na) + " non-null, "+ tipo);
        }
    }

    //Slicing
    public void slice(List<?> columnLabels, List<?> rowLabels){
        DataFrameView.sliceDataFrame(columnLabels, rowLabels, this);
    }
 
    //Acceso indexado

    // --- 3.1 Metodos auxiliares de Visualización ---
    
    protected List<Object> buildRow(int i, List<Column<?>> list){
        List<Object> row = new ArrayList<>();
        for (Column<?> c : list){
            if(c.getCell(i)==null){
                row.add(null);
            }else{
                Object v = c.getCell(i).getValue();
                row.add(v);
            }
        }
        //System.out.println(i+": "+ row);
        return row;
    }



    // --- 4.0 Metodos de acceso indexado y selección ---

    public Row getRow(Object input) throws IndexOutOfBoundsException, IllegalArgumentException {

        Label<?> label = validateLabel(input);
        int index = indexOf(label,rows);

        return new Row(rows.get(index));
    }

    public Column<?> getColumn(Object input) throws IndexOutOfBoundsException, IllegalArgumentException {

        Label<?> label = validateLabel(input);
        int index = indexOf(label, columns);
        
        // Obtener la columna y construir la lista de celdas
        return new Column<>(columns.get(index));
    }

    public Cell<?> getCell(Object rowLabel, Object columnLabel) throws IndexOutOfBoundsException{
        int indice = this.getRow(rowLabel).getIndex();
        Cell<?> celda = this.getColumn(columnLabel).getCell(indice);
        return new Cell<>(celda);
    }

    private Label<?> validateLabel(Object input) throws IllegalArgumentException{
        if (input instanceof Label<?>) {
            return (Label<?>) input;
        } else if (input instanceof String) {
            return new Label<>((String) input);
        } else if (input instanceof Integer) {
            return new Label<>((Integer) input);
        } else {
            throw new IllegalArgumentException("Tipo de argumento no soportado: " + input.getClass());
        }
    }

    protected int indexOf(Label<?> label, List<? extends Labeled> axis) throws IndexOutOfBoundsException {
        for (int i = 0; i < axis.size(); i++) {
            if (label.equals(axis.get(i).getLabel())) {
                return i;
            }
        }
        throw new IndexOutOfBoundsException("Etiqueta no encontrada: " + label);
    }

    

    //Modificación del DataFrame

    //Modificación de una celda
    public void setValue(Object rowLabel, Object columnLabel, Object newValue) throws IllegalArgumentException, IndexOutOfBoundsException{
        Label<?> rLabel = validateLabel(rowLabel);
        Label<?> cLabel = validateLabel(columnLabel);

        int rowIndex = indexOf(rLabel, this.rows);
        int colIndex = indexOf(cLabel, this.columns);

        Row row = rows.get(rowIndex);
        Column<?> col = columns.get(colIndex);
        
        col.setCell(row.getIndex(), newValue);
    }
    
    //Inserción de una columna
    public void addColumn(Column<?> newColumn) {
        if (newColumn.size() != rows.size()) {
            throw new IllegalArgumentException("La nueva columna no tiene la misma cantidad de filas.");
        }
        columns.add(newColumn);
    }
    
    
    //Inserción de una columna a partir de una secuencia lineal de Java
  
    public void addColumnFromList(List<?> data, Object label) {
        if (data.size() != rows.size()) {
            throw new IllegalArgumentException("La lista no tiene la misma cantidad de filas.");
        }
        Column<?> column = new Column<>(validateLabel(label));
        for (Object value : data) {
            column.add(value);
        }
        columns.add(column);
    }
 
    //Eliminación de una columna
    public void removeColumn(Object columnLabel) {
        Label<?> cLabel = validateLabel(columnLabel);
        int index = indexOf(cLabel, this.columns);
        columns.remove(index);
    }
    
    
    //Eliminación de una fila
    public void removeRow(Object rowLabel) {
        Label<?> rLabel = validateLabel(rowLabel);
        int index = indexOf(rLabel, this.rows);
        rows.remove(index);
        for (Column<?> col : columns) {
            col.getCells().remove(index);
        }
        //Reacomodo los indices de las filas
        for (int i = 0; i<rows.size();i++){
            rows.get(i).setIndex(i);
        }
    }
    
    //Copia
    public DataFrame copy(){
        return new DataFrame(this);
    }

    //Concatenación
   public DataFrame concatenar(DataFrame other){
        return DataFrameHandler.concatenar(other, this);
   }
    
    //Filtrado

   public DataFrame filter(Map<Label<?>, Predicate<Object>> conditions){
        return DataFrameHandler.filter(conditions, this);
   }
    /*
   //Ordenamiento

   public DataFrame sortBy(List<? extends Object> labels, boolean descending){
        return handler.sortBy(labels, descending);
   }

   //Sampleo

   public DataFrame sample(int n){
    return handler.sample(n);
   }
   
   //Imputación de valores faltantes
   public void fillna(Object label, Object value){
        int colIndex = findColumn(new Label(label));
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
    */
}
