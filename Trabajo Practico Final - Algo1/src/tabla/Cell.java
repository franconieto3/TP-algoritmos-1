package tabla;

import java.util.List;

public class Cell<T>{
    //Atributo
    private T content;

    //Constructor
    public Cell(T content){
        if(content instanceof Number || content instanceof String || content instanceof Boolean){
            this.content = content;
        }else{
            throw new IllegalArgumentException("Tipo no soportado en la columna: " + content.getClass());
        }
        
    }

    public Cell(){
        this.content=null;
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
    public void setValue(Object content){
        try{
            this.content = (T) content;
        }catch(ClassCastException e){
            throw e;
        }
    }

}
