package tabla;

import java.util.ArrayList;
import java.util.List;

import exceptions.*;
import interfaces.Labeled;

public class Column <T> implements Labeled{

    //Atributos
    private Label<?> label;
    private List<Cell<T>> cells;
    private Class<T> type = null;
  
    //Constructores
    public Column(Label<?> label){
        this.label = label;
        this.cells = new ArrayList<>();
    }
    
    /* 
    public Column(Label<?> label, List<Cell<T>> cells){
        this.label = label;
        this.cells = cells;
    }
    */

    // Constructor copia con copia profunda
    public Column(Column<T> other) {
        this.label = new Label<>(other.label); 
        this.cells = new ArrayList<>();
        if(other.getCells()!=null || !(other.getCells().isEmpty())){
            for (Cell<T> cell : other.getCells()) {
                if(cell==null){
                    this.add(null);
                    continue;
                }
                this.add(cell.getValue()); 
            }
        }
    }

    //Getters

    public Label<?> getLabel() {
        return this.label;
    }
    public Cell<?> getCell(int i) throws IndexOutOfBoundsException{
        if (i < 0 || i >= cells.size()) {
            throw new IndexOutOfBoundsException("Índice fuera del rango: " + i + ". Columna: "+ label.toString());
        }
        return cells.get(i);
    }
    protected List<Cell<T>> getCells(){
        return cells;
    }
    public Class<?> getType() {
        return type;
    }
    public int size(){
        return cells.size();
    }

    //Setters

    public void setLabel(Label<?> label){
        this.label=label;
    }


    //Metodos

    protected void addCell(Cell<T> cell) throws InvalidTypeException{
        if (cell == null) {
            cells.add(null);
            return;
        }

        add(cell.getValue());
    }
    protected void add(Object obj) throws ClassCastException, InvalidTypeException{
        
        //Si el dato es null, agregarlo igualmente
        if(obj == null){
            cells.add(null);
            return;
        }
        //Si el tipo de dato de la columna no está definido aún, definirlo
        if (type==null){
            @SuppressWarnings("unchecked")
            Class<T> inferredType = (Class<T>) obj.getClass();
            this.type = inferredType;
        }
        //Validar el tipo de cada elemento que se agrega
        if (!type.isInstance(obj)) {
                throw new IllegalArgumentException("Todos los elementos deben ser del mismo tipo: " + type.getSimpleName());
        }

        //Casteo de Object al tipo de la columna
        @SuppressWarnings("unchecked")
        T value = (T) obj;
        cells.add(new Cell<>(value));

    }

    protected void setCell(int i, Object value) {
        if (i < 0 || i >= cells.size()) {
            throw new IndexOutOfBoundsException("Index " + i + " out of bounds");
        }

        if (value == null) {
            cells.set(i, null);
            return;
        }

        if (!this.type.isInstance(value)) {
            throw new IllegalArgumentException("Tipo de dato no coincide con el de la columna: se esperaba "+ this.type.getSimpleName() + "pero se recibió " + value.getClass().getSimpleName());
        }
        @SuppressWarnings("unchecked")
        T content = (T) value;

        if(cells.get(i)!=null){
            Cell<T> cell = cells.get(i);
            cell.setValue(content);
        }else{
            Cell<T> cell = new Cell<>(content);
            cells.set(i, cell);
        }
        
    }

    public int countNA(){
        int n=0;
        for (int i = 0; i<cells.size();i++){
            if (cells.get(i)==null){
                n++;
            }
        }
        return n;
    }

    
    public boolean matches(Object obj) {
        if (this == obj) return true; // Son la misma referencia
        if (obj == null || getClass() != obj.getClass()) return false;

        @SuppressWarnings("unchecked")
        Column<T> other = (Column<T>) obj;

        // Comparo las etiquetas
        if (this.label == null) {
            if (other.label != null) return false;
        } 
        else if (!this.label.equals(other.label)) {
            return false;
        }

        // Comparo tipo de datos
        Class<?> thisType = this.getType();
        Class<?> otherType = other.getType();

        if (thisType == null) {
            return otherType == null;
        }

        return thisType.equals(otherType);
    }

    @Override
    public String toString() {
        return "Column<" + type.getSimpleName() + ">{" + cells + '}';
    }

}
