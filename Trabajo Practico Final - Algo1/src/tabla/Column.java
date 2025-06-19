package tabla;

import java.util.ArrayList;
import java.util.List;
import exceptions.*;

public class Column <T> {
    //Atributos
    private Label<?> label;
    private List<Cell<T>> cells;
    private final Class<T> type;

    public Column(Label<?> label ,List<?> values) {
        this.label = label;


        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("La lista no puede ser nula ni vacía.");
        }
        Object first = null;

        for(Object v: values){
            if(v==null){continue;}
            first = values.get(0);
        }

        if (!(first==null || first instanceof Number || first instanceof String || first instanceof Boolean)) {
            throw new IllegalArgumentException("El tipo debe ser Number, String o Boolean.");
        }
        
        // Establecer el tipo de la columna
        @SuppressWarnings("unchecked")
        Class<T> inferredType = (Class<T>) first.getClass();
        this.type = inferredType;

        // Verificar que todos los elementos sean del mismo tipo
        for (Object obj : values) {
            if(obj==null){
                cells.add(null);
                continue;
            }
            if (!type.isInstance(obj)) {
                throw new IllegalArgumentException("Todos los elementos deben ser del mismo tipo: " + type.getSimpleName());
            }

            @SuppressWarnings("unchecked")
            T value = (T) obj;
            cells.add(new Cell<>(value));
        }
    }

    public List<Cell<T>> getCells() {
        return cells;
    }

    public Class<T> getColumnType() {
        return type;
    }

    @Override
    public String toString() {
        return "Column<" + type.getSimpleName() + ">{" + cells + '}';
    }




    /* 
    //Constructores
    public Column(Label<?> label){
        this.label = label;
        this.cells = new ArrayList<>();
    }
    public Column(Label<?> label, List<Cell<T>> cells){
        this.label = label;
        this.cells = cells;
    }

    // Constructor copia con copia profunda
    public Column(Column<T> other) {
        this.label = new Label<>(other.label); 
        this.cells = new ArrayList<>();
        for (Cell<T> cell : other.cells) {
            this.cells.add(new Cell<>(cell)); 
        }
    }

    //Getters

    public Label<?> getLabel() {
        return this.label;
    }
    public Cell<?> getCell(int i) throws IndexOutOfBoundsException{
        validarIndice(i);
        return cells.get(i);
    }
    public List<Cell<T>> getCells(){
        return cells;
    }
    public Class<?> getType() {

        //Tener en cuenta el caso de qué pasaría si el primer dato es NA
        if (this.cells.isEmpty()) {
            return null; // o sería mejor lanzar una excepción?
        }

        Object firstValue = cells.get(0).getValue();

        // Si el primer valor es NA, no se puede usar como referencia para el tipo
        if (firstValue instanceof MissingValue) {
            // Buscar el primer valor no NA como referencia de tipo
            for (Cell<?> c : cells) {
                if (!(c.getValue() instanceof MissingValue)) {
                    return c.getValue().getClass();
                }
            }
            return null;
            }

        return firstValue.getClass();
    }

    //Setters

    public void setLabel(Label<?> label){
        this.label=label;
    }

    //Metodos


    protected void addCell(Cell<T> cell) throws InvalidTypeException{
         
        //tener en cuenta también los datos de tipo NA
        if(!validarTipo(cell)){
            throw new InvalidTypeException();
        }
        
        cells.add(cell);
    }
    protected void add(Object value){
        cells.add(new Cell<>(value));

    }
    public int size(){
        return cells.size();
    }

    public void setCell(int i, T value) throws IndexOutOfBoundsException, InvalidTypeException, IllegalStateException{

        //Comprobar que i esté dentro del tamaño de la lista
        validarIndice(i);

        //Permitir que sea de tipo NA
        if(value instanceof MissingValue){
            cells.get(i).setValue(value);
            return;
        }
        //Comprobar si T coincide con el tipo de la columna
        if (!value.getClass().equals(this.getType())){
            throw new InvalidTypeException();
        }
        if (this.getType() == null) {
            throw new IllegalStateException("El tipo de la columna no ha sido definido.");
        }
        cells.get(i).setValue(value);
    }

    public int countNA(){
        int i=0;
        for (Cell c:cells){
            if (c.getValue() instanceof MissingValue){
                i++;
            }
        }
        return i;
    }

    private boolean validarTipo(Cell<?> cell) {
        Object value = cell.getValue();

        // Aceptar valores NA sin importar el tipo
        if (value == null || value instanceof MissingValue) {
            return true;
        }

        // Si la columna está vacía, aceptar cualquier tipo no NA
        if (cells.isEmpty()) {
            return true;
        }

        Object firstValue = cells.get(0).getValue();

        // Si el primer valor es NA, no se puede usar como referencia para el tipo
        if (firstValue == null || firstValue instanceof MissingValue) {
            // Buscar el primer valor no NA como referencia de tipo
            for (Cell<?> c : cells) {
                if (!(c.getValue() instanceof MissingValue)) {
                    firstValue = c.getValue();
                    break;
                }
            }
            // Si no hay valores válidos aún, aceptar cualquier tipo
            if (firstValue instanceof MissingValue) {
                return true;
            }
        }

        return value != null && value.getClass().equals(firstValue.getClass());
    }
    private void validarIndice(int i){
        if (i < 0 || i >= cells.size()) {
            throw new IndexOutOfBoundsException("Índice fuera del rango: " + i);
    }
    }

    
    public boolean matches(Object obj) {
        if (this == obj) return true; // Son la misma referencia
        if (obj == null || getClass() != obj.getClass()) return false;

        Column other = (Column) obj;

        // Comparo las etiquetas
        if (this.label == null) {
            if (other.label != null) return false;
        } else if (!this.label.equals(other.label)) {
            return false;
        }

        // Comparo tipo de datos
        Class<?> thisType = this.getType();
        Class<?> otherType = other.getType();

        if (thisType == null) {
            return otherType == null;
        }

        return thisType.equals(otherType);
    }*/

}
