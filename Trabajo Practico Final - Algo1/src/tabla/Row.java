package tabla;

import interfaces.Labeled;

public class Row implements Labeled{
    //Atributos
    private Label<?> label;
    private int index;

    //Constructor

    public Row (Label<?> label, int index){
        this.label = label;
        this.index = index;
    }
    // Constructor copias
    public Row(Row other) {
        this.label = new Label<>(other.getLabel());
        this.index = other.index;
    }
    //Getters

    public Label<?> getLabel(){
        return label;
    }
    public int getIndex(){
        return index;
    }

    //Setter
    protected void setIndex(int i){
        this.index = i;
    }

    @Override
    public String toString(){
        return "Row: "+label+", index "+index;    
    }
}
