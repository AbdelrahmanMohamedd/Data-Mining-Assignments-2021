import java.io.*;
import java.util.Random;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;


public class Main {

    private static int DataTsize100 = 1728;
    private static int TrainingSize75 = 1296; //(1728*75)/100
    private static int TestingSize25 = 432; //(1728*25)/100
    private static int Columns = 7;
    static String [][] FileData = new String[DataTsize100][Columns];        // to store all of the data in the CarData file
    static String [][] TrainingSet = new String[TrainingSize75][Columns];     // to store 75% of this data
    static String [][] TestingSet = new String[TestingSize25][Columns];       // to store 25% (the rest of the data)
    static String [][] TestingSet_COPY = new String[TestingSize25][Columns];  // have a temp copy for the 25%

/////////////////////////////////////////////** MAIN **/////////////////////////////////////////////////////////////////
    public static void main( String[] args ) throws IOException {
        ReadFromFileExcel();   // read from the file
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void ReadFromFileExcel() throws IOException {
            FileInputStream file = new FileInputStream(new File("CarData.xls"));
            HSSFWorkbook workbook = new HSSFWorkbook(file); // Create an excel workbook from the file system.
            Sheet sheet = workbook.getSheetAt(0);   // Get the first sheet on the workbook.
            Iterator<Row> rowIterator = sheet.iterator();  // iterator on each row
            int j =0 ;
            while (rowIterator.hasNext()) {
                HSSFRow row = (HSSFRow) rowIterator.next();
                String [] arr =new String[Columns] ;
                Iterator<Cell> cellIterator = row.cellIterator();  // iterator on each cell(column)
                int counter = 0 ;
                while (cellIterator.hasNext()) {
                    HSSFCell cell = (HSSFCell) cellIterator.next();
                    arr[counter]=cell.toString();
                    counter ++ ;
                }
                for(int i=0;i<Columns;++i) {
                    FileData[j][i] = arr[i] ;
                }
                j++ ;
            }
            workbook.close();
            file.close();
    //#######################################################################################
        DivideDataRows();    // dividing the data into 75% (training) - 25% (testing) : RANDOMLY
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void DivideDataRows() {
        ArrayList<Integer> temp = new ArrayList<Integer>();
        for(int i=0 ; i<TrainingSize75 ; i++) { //1296
            Random generateRandomNum = new Random();
            int RandIndx = generateRandomNum.nextInt(FileData.length);
            if(!temp.contains(RandIndx)) temp.add(RandIndx);
            else {
                i=i-1 ;
                continue ;
            }
            for(int j=0 ; j<Columns ; ++j) {
                TrainingSet[i][j] = FileData[RandIndx][j] ;
            }
        }
        int k = 0 ;
        for(int i=0 ; i<DataTsize100 ; i++) {  //1728
            if(!temp.contains(i)) { // lw msh mwgpd
                for(int j=0 ; j<Columns ; ++j) {
                    TestingSet[k][j] = FileData[i][j] ;
                } k++;
            }
        }
        for ( int i=0 ; i<TestingSize25 ; i++ ) { //432
            for(int j=0 ; j<6 ; ++j) {   //6, to get it without the last col
                TestingSet_COPY[i][j] = TestingSet[i][j] ;
            }
        }
/*        System.out.println("\n"+"\n"+"*****************************************************");   //Printing
        System.out.println(">> Training Set (75% of the data)");
        System.out.println("***************************************************** " );
         for(int i=0 ; i<TrainingSize75 ; i++){ //1296
            for(int j=0 ; j<Columns ; ++j)
            {System.out.print(TrainingSet[i][j]+" ");
            }System.out.print("\n");
        }
        System.out.println("\n"+"\n"+"*****************************************************");    //Printing
        System.out.println(">> Testing Set (25% of the data)");
        System.out.println("***************************************************** " );
        for(int i=0 ; i<TestingSize25 ; ++i) { //432
            for(int j=0 ; j<Columns ; ++j) {
                System.out.print(TestingSet[i][j]+" ");
            }
            System.out.print("\n");
        }   */
    //#######################################################################################
        NaiveBayesClassifier();    // on >> TestingSet_COPY
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void NaiveBayesClassifier() {
        double acc_NUM = 0 ,unacc_NUM =0 , good_NUM =0 ,vgood_NUM=0;   //3ddhom
        double acc_Prob = 0 ,unacc_Prob =0 , good_Prob =0 ,vgood_Prob=0;      //probabilities
        for(int i=0;i<TrainingSize75;i++) { //1296
            if(TrainingSet[i][6].equals("acc"))        acc_NUM ++;
            else if(TrainingSet[i][6].equals("unacc")) unacc_NUM ++;
            else if(TrainingSet[i][6].equals("good"))  good_NUM ++;
            else if(TrainingSet[i][6].equals("vgood")) vgood_NUM ++;
        }
        acc_Prob = acc_NUM/TestingSize25;     //432
        unacc_Prob = unacc_NUM/TestingSize25;
        good_Prob = good_NUM/TestingSize25;
        vgood_Prob = vgood_NUM/TestingSize25;
        double Facc_NUM = 0 , Funacc_NUM = 0 , Fgood_NUM = 0 , Fvgood_NUM =0; // F:Found
        double TOTALacc = 0 , TOTALunacc = 0, TOTALgood = 0, TOTALvgood = 0, MAX = 0 ;
        for(int i=0 ; i<TestingSize25 ; ++i) {   //432
            TOTALacc = acc_Prob;
            TOTALunacc = unacc_Prob ;
            TOTALgood = good_Prob;
            TOTALvgood = vgood_Prob ;
            for(int j=0 ; j<6 ; ++j) {
                Facc_NUM = 0 ; Funacc_NUM = 0 ; Fgood_NUM = 0 ; Fvgood_NUM = 0 ;  //set them 0
                for(int k=0 ; k<TrainingSize75 ; ++k) { //1296
                    if(TestingSet_COPY[i][j].equals(TrainingSet[k][j]) && TrainingSet[k][6].equals("unacc"))  Funacc_NUM++;
                    else if(TestingSet_COPY[i][j].equals(TrainingSet[k][j]) && TrainingSet[k][6].equals("acc"))  Facc_NUM++;
                    else if(TestingSet_COPY[i][j].equals(TrainingSet[k][j]) && TrainingSet[k][6].equals("good"))  Fgood_NUM++;
                    else if(TestingSet_COPY[i][j].equals(TrainingSet[k][j]) && TrainingSet[k][6].equals("vgood")) Fvgood_NUM++;
                }
                TOTALacc *= (Facc_NUM/ acc_NUM);
                TOTALunacc *= (Funacc_NUM / unacc_NUM);
                TOTALgood *= (Fgood_NUM / good_NUM);
                TOTALvgood *= (Fvgood_NUM / vgood_NUM);
            }
            double x = Math.max( TOTALacc , TOTALunacc);
            double y = Math.max( x, TOTALgood);
            MAX = Math.max( y, TOTALvgood    );
            if( MAX == TOTALacc ) TestingSet_COPY[i][6] ="acc" ;
            else if(MAX == TOTALunacc) TestingSet_COPY[i][6] ="unacc" ;
            else if(MAX == TOTALgood)  TestingSet_COPY[i][6] ="good" ;
            else if(MAX == TOTALvgood)  TestingSet_COPY[i][6] ="vgood" ;
        }
        /*System.out.println("\n"+"\n"+"*****************************************************");    //Printing
        System.out.println(">> Testing Set *COPY* ");
        System.out.println("*****************************************************");
        for(int i=0;i<TestingSize25;++i) {   //432
            for(int j=0;j<Columns;++j) {
                System.out.print(TestingSet_COPY[i][j]+" ");
            }
            System.out.print("\n");
        }*/
        System.out.println("\n"+"\n"+"*****************************************************");
        System.out.println("Probability of (acc) >> "+ acc_Prob );
        System.out.println("Probability of (unacc) >> "+ unacc_Prob );
        System.out.println("Probability of (good) >> "+ good_Prob);
        System.out.println("Probability of (vgood) >> "+ vgood_Prob);
       /* System.out.println("*****************************************************");
        System.out.println("Total Probability of (acc) >> "+ total_acc );
        System.out.println("Total Probability of (unacc) >> "+ total_unacc );
        System.out.println("Total Probability of (good) >> "+ total_good);
        System.out.println("Total Probability of (vgood) >> "+ total_vgood);*/
    //#######################################################################################
        CalcAccuracy();  // get accur
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void CalcAccuracy() {
        double Accur_count =0 ;
        for(int i=0;i<TestingSize25;++i) {   //432
            if(TestingSet[i][6].equals(TestingSet_COPY[i][6]))   Accur_count ++ ;
        }
        double Accuracy = Accur_count/TestingSize25 ;   //432
        System.out.println("***************************************************** ");
        System.out.println(">> The Accuracy = " + Accuracy*100 +" %" );
        System.out.println("*****************************************************" +"\n"+"\n"+"\n"+"\n");
    }
}






//////////////////////////////////////////reading as CSV file///////////////////////////////////////////////////////////
/*
    // This method will read a CSV file and return a List of String[]
    public static List<String[]> ReadCSVfile(String filename) {
        List<String[]> data = new ArrayList<>();
        String testRow;
        try {// Open and read the file
            BufferedReader br = new BufferedReader(new FileReader(filename));// Read data as long as it's not empty
            while ((testRow = br.readLine()) != null) {
                String[] line = testRow.split(","); // Parse the data by comma using .split() method
                data.add(line); // Place into a temporary array, then add to List
            }
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: File not found " + filename);
        } catch (IOException e) {
            System.out.println("ERROR: Could not read " + filename);
        }
        return data;
 }*/
