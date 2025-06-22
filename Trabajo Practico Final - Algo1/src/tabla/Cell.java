package tabla;


public class Cell<T>{
    //Atributo
    private T content;

    //Constructor

    public Cell(){
        this.content=null;
    }

    public Cell(T content) throws IllegalArgumentException{
        if(content instanceof Number || content instanceof String || content instanceof Boolean || content == null){
            this.content = content;
        } else {
            throw new IllegalArgumentException("Tipo de dato no válido para una celda: " + content.getClass());
        }
    }

    // Constructor copia
    public Cell(Cell<T> other) {
        this.content = other.content;
    }
    
    //Devolver null, si el contenido es nulo.
    @Override
    public String toString() {
        return content != null ? content.toString() : "null";
    }

    //Metodos
    public T getValue() {
        return this.content;
    }
    public void setValue(T content){
        if(content instanceof Number || content instanceof String || content instanceof Boolean || content == null){
            this.content = content;
        } else {
            throw new IllegalArgumentException("Tipo de dato no válido para una celda: " + content.getClass());
        }
    }

}
