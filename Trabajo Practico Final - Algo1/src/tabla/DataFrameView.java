package tabla;

import java.util.ArrayList;
import java.util.List;

public class DataFrameView {
    //Atributos

    private static int MAX_ROWS = 10;
    private static int MAX_COLS = 10;
    private static int MAX_CELL_CHARS = 15;
    
    //Constructor
    public DataFrameView(){

    }/* 
    public DataFrameView(int MAX_ROWS, int MAX_COLS, int MAX_CELL_CHARS){
        this.MAX_ROWS = MAX_ROWS;
        this.MAX_COLS = MAX_COLS;
        this.MAX_CELL_CHARS = MAX_CELL_CHARS;
    }*/

    public static String formatTable(List<? extends List<?>> data, List<?> rowLabels, List<?> columnLabels) {
        StringBuilder sb = new StringBuilder();

        int totalRows = Math.min(data.size(), MAX_ROWS);
        int totalCols = columnLabels != null ? Math.min(columnLabels.size(), MAX_COLS) : 0;

        // Formato de encabezado
        sb.append(padRight("", MAX_CELL_CHARS));  // espacio para la columna de etiquetas de fila
        for (int col = 0; col < totalCols; col++) {
            sb.append(padRight(truncate(columnLabels.get(col).toString()), MAX_CELL_CHARS));
        }
        if (columnLabels.size() > MAX_COLS) {
            sb.append("...");
        }
        sb.append("\n");

        // Formato de filas
        for (int row = 0; row < totalRows; row++) {
            // Etiqueta de fila
            String rowLabel = rowLabels != null && row < rowLabels.size()
                    ? truncate(rowLabels.get(row).toString())
                    : "";
            sb.append(padRight(rowLabel, MAX_CELL_CHARS));

            // Celdas
            List<?> rowCells = data.get(row);
            for (int col = 0; col < totalCols && col < rowCells.size(); col++) {
                Object cell = rowCells.get(col);
                if (cell==null){
                    sb.append(padRight(truncate("N/A"), MAX_CELL_CHARS));
                }
                else{
                sb.append(padRight(truncate(cell.toString()), MAX_CELL_CHARS));
                }
            }

            if (rowCells.size() > MAX_COLS) {
                sb.append("...");
            }
            sb.append("\n");
        }

        if (data.size() > MAX_ROWS) {
            sb.append("...\n");
        }

        return sb.toString();
    }

    private static String truncate(String input) {
        return input.length() > MAX_CELL_CHARS ? input.substring(0, MAX_CELL_CHARS - 3) + "..." : input;
    }

    private static String padRight(String text, int length) {
        if (text.length() >= length) return text;
        return String.format("%-" + length + "s", text);
    }


    public static void viewDataFrame(int start, int end, DataFrame df){
        List<Label<?>> colLabels = df.getColumnLabels();
        List<Label<?>> rowLabels =  new ArrayList<>();
        List<Column<?>> columns = df.getColumns();
        List<List<Cell<?>>> data = new ArrayList<>();

        for(int i=start; i<df.countRows();i++){
            if(i==end)break;
            List<Cell<?>> row = new ArrayList<>();

            for(Column<?> c: columns){
                row.add(c.getCell(i));
                
            }
            rowLabels.add(df.getRowLabels().get(i));
            data.add(row);
        }

        String tabla = formatTable(data, rowLabels, colLabels);
        System.out.println(tabla);
    }

    public static void sliceDataFrame(List<?> columnLabels, List<?> rowLabels, DataFrame df){
        List<Column<?>> columnList = new ArrayList<>();
        List<List<Object>> rowList = new ArrayList<>();
        List<Label<?>> cLabels = new ArrayList<>();
        List<Label<?>> rLabels = new ArrayList<>();

        //Si no se especifica una lista de columnas o filas, se interpreta como que se quieren mostrar todas
        if(columnLabels==null || columnLabels.isEmpty()){
            columnLabels = df.getColumnLabels();
        }
        if(rowLabels==null || rowLabels.isEmpty() ){
            rowLabels = df.getRowLabels();  
        }
        
        for (Object l:columnLabels){
            Column<?> c = df.getColumn(l);
            columnList.add(c);
            cLabels.add(c.getLabel());
        }
         
        for (Object l:rowLabels){
            Row r = df.getRow(l);
            rowList.add(df.buildRow(r.getIndex(),columnList));
            rLabels.add(r.getLabel());
        }

        System.out.println(formatTable(rowList, rLabels, cLabels));
    }

    //Getters 

    public static int getMaxRows(){
        return MAX_ROWS;
    }
    public static int getMaCols(){
        return MAX_COLS;
    }
    public static int getMaxCellChars(){
        return MAX_CELL_CHARS;
    }

    //Setters
    public static void setMaxRows(int maxRows){
        MAX_ROWS = maxRows;
    }
    public static void setMaxCols(int maxCols){
        MAX_COLS = maxCols;
    }
    public static void setMaxCellChars(int maxCellChars){
        MAX_CELL_CHARS = maxCellChars;
    }

}
