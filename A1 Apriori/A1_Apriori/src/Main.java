import java.util.*;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.openxml4j.exceptions.InvalidFormatException;

class Main {
            static String[][] Coffe_Transactions =new String[9959][3];
            static int Min_Support_Count = 0;
            static double Min_Confidence = 0;
            static ArrayList<Set> singleSet = new ArrayList<Set>();
            static ArrayList<Set> pairsSets = new ArrayList<Set>();
            static ArrayList<Set> triSets = new ArrayList<Set>();
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    static class Set{
        int dim;
        public String[] items;
        public float counter;
        public Set(int dimension){
            this.dim = dimension;
            items = new String[dimension];
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void main(String[] args) throws IOException, InvalidFormatException {
        Scanner scan = new Scanner(System.in); //taking inputs from user (min supp count  - min confd)
        System.out.print("Enter the minimum support count: ");
        Min_Support_Count = scan.nextInt();
        System.out.print("Enter the minimum Confidance percentage:  ");
        Min_Confidence = scan.nextInt();
        scan.close();
        System.out.println(" Minimum Support Count = " + Min_Support_Count + " Minimum Confidence= " +  Min_Confidence + "\n");
        ReadFromFileExcel();         //Read Transactions From Text File
        CreateSingleList();          //Count each item in the list
        CreatePairList();            //Count Pairs of each two items
        CreateTripleList();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void ReadFromFileExcel() throws IOException {
        FileInputStream file = new FileInputStream(new File("CoffeeShopTransactions.xls"));
        HSSFWorkbook workbook = new HSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        int x = 0;
        boolean skip = true;
        for(Row row : sheet){
            if(skip){
                skip = false;
            }
            else {
                Cell cell = row.getCell(3);
                Coffe_Transactions[x++][0] = cell.getStringCellValue();
            }
        }
        skip = true; x = 0;
        for(Row row : sheet){
            if(skip){
                skip = false;
            }
            else {
                Cell cell = row.getCell(4);
                Coffe_Transactions[x++][1] =cell.getStringCellValue();
            }
        }
        skip = true;
        x = 0;
        for(Row row : sheet){
            if(skip){
                skip = false;
            }
            else {
                Cell cell = row.getCell(5);
                Coffe_Transactions[x++][2] =cell.getStringCellValue();
            }
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    static int search(ArrayList<Set> array, String item) {
        int isHere = 0;
        for(int i=0;i<array.size();i++) {
            for(int j=0;j<array.get(i).dim;j++) {
                if(array.get(i).items[j] == item) {
                    array.get(i).counter ++;
                    isHere = 1;
                    break;
                }
            }
        }
        return isHere;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    static void PairsCounter() {
        // Each iteration consist of a pair of two items
        // Search for the pairs items in the Transactions list
        // increment the pairs counter if found in the same items set
        for(int k=0;k<pairsSets.size();k++) {
            int counter=0;
            for(int i=0;i<Coffe_Transactions.length;i++) {
                counter = 0;
                for(int j=0;j<Coffe_Transactions[i].length;j++) {
                    if(pairsSets.get(k).items[counter] == Coffe_Transactions[i][j])
                        counter++;
                    if(counter == 2) {
                        pairsSets.get(k).counter ++;
                        break;
                    }
                }
            }
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    static void AssociationRule() {
        for(int k=0;k<triSets.size();k++) {
            System.out.print( ( "  {" + triSets.get(k).items[0] + ", " + triSets.get(k).items[1] + ", "  + triSets.get(k).items[2] + "} Frequent Items\n" ));
            int c1 = 0, c2 = 1;
            for(int t = 0;t<3; t++) {
                int exist = 0;
                int i = 0;
                for(;i<pairsSets.size();i++) {
                    if(triSets.get(k).items[c1] == pairsSets.get(i).items[0]&& triSets.get(k).items[c2] == pairsSets.get(i).items[1]) {exist = 1; break;}
                }
                if(exist == 1) {
                    int support = (int) ((triSets.get(k).counter/pairsSets.get(i).counter) * 100);
                    System.out.print("=>{" + pairsSets.get(i).items[0] + ", " + pairsSets.get(i).items[1] + "} " + support + "% ");
                    if(support < Min_Confidence)
                        System.out.print("    >> Ignore \n");
                    else
                        System.out.print( "\n");
                }
                if(t==0) { c1 = 0; c2 = 2;}
                else if(t==1) {c1 = 1; c2 = 2;}
            }
            for(int j = 0;j<singleSet.size();j++) {
                int support = (int) ((triSets.get(k).counter/singleSet.get(j).counter) * 100);
                System.out.print("=>{" + singleSet.get(j).items[0] + "} " + support + "% ");
                if(support < Min_Confidence)
                    System.out.print("    >> Ignore \n");
                else
                    System.out.print("\n");
            }
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    static void CreateSingleList() {
        for(int i=0;i<Coffe_Transactions[0].length;i++) {
            Set set = new Set(1);
            set.items[0] = Coffe_Transactions[0][i];
            set.counter = 1;
            singleSet.add(set);
        }
        for (int i=1;i<Coffe_Transactions.length;i++) {
            for(int j=0;j<Coffe_Transactions[i].length;j++) {
                if(search(singleSet, Coffe_Transactions[i][j]) == 0) {
                    Set set = new Set(1);
                    set.items[0] = Coffe_Transactions[i][j];
                    set.counter = 1;
                    singleSet.add(set);
                }
            }
        }
        for(int i=0;i<singleSet.size();) {              //Remove infrequent items from single item list
            if(singleSet.get(i).counter < Min_Support_Count) {
                singleSet.remove(i);
            }
            else i++;
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    static void CreatePairList() {
        for(int i=0;i<singleSet.size();i++) {
            for(int j = i+1;j<singleSet.size();j++) {
                Set set = new Set(2);
                set.items[0] = singleSet.get(i).items[0];
                set.items[1] = singleSet.get(j).items[0];
                set.counter = 0;
                pairsSets.add(set);
            }
        }
        PairsCounter(); //Count Each items paired with other items
        for(int i=0;i<pairsSets.size();) {      //Remove Infrequent Items from Pairs items List
            if(pairsSets.get(i).counter < Min_Support_Count) {
                pairsSets.remove(i);
            }else i++;
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    static void CreateTripleList() {
        //Trio items sets
        for(int i=0;i<singleSet.size() - 1;i++) {
            for(int j = i+1;j<singleSet.size();j++) {
                for(int k= j+1;k<singleSet.size();k++) {
                    Set set = new Set(3);
                    set.items[0] = singleSet.get(i).items[0];
                    set.items[1] = singleSet.get(j).items[0];
                    set.items[2] = singleSet.get(k).items[0];
                    set.counter = 0;
                    triSets.add(set);
                }
            }
        }
        for(int k=0;k<triSets.size();k++) {     //Remove Infrequent sets compared to the pairs list
            int c1 = 0, c2 = 1;
            for(int t = 0;t<3; t++) {
                int exist = 0;
                int i = 0;
                for(;i<pairsSets.size();i++) {
                    if(triSets.get(k).items[c1] == pairsSets.get(i).items[0]&&
                            triSets.get(k).items[c2] == pairsSets.get(i).items[1]) {exist = 1; break;}
                }
                if(exist == 0) {
                    triSets.remove(k);
                    k--;
                    break;
                }
                if(t==0) { c1 = 0; c2 = 2;}
                else if(t==1) {c1 = 1; c2 = 2;}
            }
        }
        for(int k=0;k<triSets.size();k++) {
            for(int i=0;i<Coffe_Transactions.length;i++) {
                int t = 0;
                for(int j=0;j<Coffe_Transactions[i].length;j++) {
                    if(triSets.get(k).items[t] == Coffe_Transactions[i][j] )
                        t++;

                    if(t >= 3) {
                        triSets.get(k).counter++;
                        break;
                    }
                }
            }
        }
        AssociationRule();
    }
}
