import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.*;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

class Cluster{
    public ArrayList<Double> centroid = new ArrayList<>();
    public ArrayList<ArrayList<Double>> products = new ArrayList<>();
}

public class Main {
    public static void main(String[] args) throws IOException {
        ReadFromFileExcel();
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void ReadFromFileExcel() throws IOException {  ////ab2a 5leha btreturn 7aga 3shan l func ale b3dha tkml 3aleha
        ArrayList<ArrayList<Double>> salesData = new ArrayList<>();                   // Create an ArrayList to store the data read from excel sheet.
        FileInputStream file = new FileInputStream(new File("Sales.xls"));  // Create a FileInputStream that will be use to read the excel file.
        HSSFWorkbook workbook = new HSSFWorkbook(file); // Create an excel workbook from the file system.
        Sheet sheet = workbook.getSheetAt(0);;   // Get the first sheet on the workbook.
        Iterator rows = sheet.rowIterator();      // iterator on each row
            while (rows.hasNext()) {
            HSSFRow row = (HSSFRow) rows.next();
            if(row.getRowNum()==0) continue;      // skip the 1st row (ale feeh al weeks)
            // System.out.println ("col No.: " + cell.getColumnIndex());
        Iterator cells = row.cellIterator();      // iterator on each cell(column)
            ArrayList<Double> data = new ArrayList<>();
            while (cells.hasNext()) {
            HSSFCell cell = (HSSFCell) cells.next();
            if(cell.getColumnIndex()==0) continue; // skip the 1st col (ale feeh al products)
                data.add(cell.getNumericCellValue());
            }
            salesData.add(data);     // We store the datalist read on an ArrayList
        }
        ChooseInitialClusters(salesData);
       // showExcelData(salesData);
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static double ManhatanDistance (ArrayList<Double> point1, ArrayList<Double> point2) {
        double distance = 0.0;
        for(int i = 0; i < point1.size(); i++){
            distance += Math.abs(point1.get(i) - point2.get(i));
        }
        return distance;
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    static void ChooseInitialClusters(ArrayList<ArrayList<Double>> SalesData){
        System.out.println(" Enter K (Number of clusters): ");
        Scanner input = new Scanner(System.in);
        int K = input.nextInt();
        ArrayList<Cluster> Clusters= new ArrayList<>();  //b5zn feha al products ale h3ml grouping 3la asasha
         for(int i = 0; i < K; i++){
             int random = new Random().nextInt(SalesData.size());
             Cluster cluster = new Cluster();
             cluster.centroid = SalesData.get(random);
             Clusters.add(cluster);
         }
         for(int i = 0; i < SalesData.size(); i++){
             ArrayList<Double> distances = new ArrayList<>();
             ArrayList<Double> product = SalesData.get(i);
             for(int j = 0; j < Clusters.size(); j++){
                 double distance = ManhatanDistance(product, Clusters.get(j).centroid);
                 distances.add(distance);
             }
             int index_of_min = distances.indexOf(Collections.min(distances));
             Clusters.get(index_of_min).products.add(product);
         }
         k_means(Clusters, SalesData);
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void k_means(ArrayList<Cluster> Clusters, ArrayList<ArrayList<Double>> SalesData){
         ArrayList<Cluster> new_clusters = new ArrayList<>();
         for(int i = 0; i < Clusters.size(); i++){
             Cluster cluster = Clusters.get(i);
             ArrayList<Double> new_centroid = new ArrayList<>();
             for(int j = 0; j < cluster.centroid.size(); j++){ // j bet3abr 3an el week number
                 double mean = 0.0;
                 for(int k = 0; k < cluster.products.size(); k++){
                     mean += cluster.products.get(k).get(j);
                 }
                 mean /= cluster.products.size();
                 new_centroid.add(mean);
             }
             Cluster cluster1 = new Cluster();
             cluster1.centroid = new_centroid;
             new_clusters.add(cluster1);
         }
        for(int i = 0; i < SalesData.size(); i++){
            ArrayList<Double> distances = new ArrayList<>();
            ArrayList<Double> product = SalesData.get(i);
            for(int j = 0; j < new_clusters.size(); j++){
                double distance = ManhatanDistance(product, new_clusters.get(j).centroid);
                distances.add(distance);
            }
            int index_of_min = distances.indexOf(Collections.min(distances));
            new_clusters.get(index_of_min).products.add(product);
        }
        ArrayList<Double> Old = new ArrayList<>();
        ArrayList<Double> New = new ArrayList<>();
        for(int i = 0; i < Clusters.size(); i++){
            Cluster cluster = Clusters.get(i);
            for(int j = 0; j < cluster.products.size(); j++){
                ArrayList<Double> product = cluster.products.get(j);
                for(int k = 0; k < product.size(); k++){
                    Old.add(product.get(k));
                }
            }
        }
        for(int i = 0; i < new_clusters.size(); i++){
            Cluster cluster = new_clusters.get(i);
            for(int j = 0; j < cluster.products.size(); j++){
                ArrayList<Double> product = cluster.products.get(j);
                for(int k = 0; k < product.size(); k++){
                    New.add(product.get(k));
                }
            }
        }
        if(Old.equals(New)){
            for(int i = 0; i < Clusters.size(); i++){
                Cluster cluster = Clusters.get(i);
                System.out.println("****************************************************************************************************************************************************");
                System.out.println("Cluster " + (i+1));
                System.out.println(cluster.centroid);
                System.out.println("Products:- ");
                for(int j = 0; j < cluster.products.size(); j++){
                    System.out.println(cluster.products.get(j).toString());
                }
            }
            return;
        }
        k_means(new_clusters, SalesData);
    }
}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 /*   public static void Outliers(ArrayList<Cluster> Clusters){
        double Q1,Q3;
        double IQR=Q3-Q1;
        for(int i = 0; i < Clusters.size(); i++){
            ArrayList<Double> Clusters = Clusters.get(i);
            ArrayList<Double> Ouliers = new ArrayList<>();
            ArrayList<Double> Prod = Clusters.get(i);
            for(int j = 0; j < Clusters.size(); j++){
                double distance = ManhatanDistance(Ouliers, Clusters.get(j));
                Ouliers.add(distance);
            }
        }
    }
*/
/////////////////////////////////////////////////SHOWING ECXEL DATA (testing) /////////////////////////////////////////////////////////////////
/*  private static void showExcelData(List sheetData) { // Iterates the data and print it out to the console.
        for (int i = 0; i < sheetData.size(); i++) {
            List list = (List) sheetData.get(i);
            for (int j = 0; j < list.size(); j++) {
                Cell cell = (Cell) list.get(j);
                if (cell.getCellType() == CellType.NUMERIC) {
                    System.out.print(cell.getNumericCellValue());
                } else if (cell.getCellType() == CellType.STRING) {
                    System.out.print(cell.getRichStringCellValue());
                } else if (cell.getCellType() == CellType.BOOLEAN) {
                    System.out.print(cell.getBooleanCellValue());
                }
                if (j < list.size() - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println("");
        }
    }*/