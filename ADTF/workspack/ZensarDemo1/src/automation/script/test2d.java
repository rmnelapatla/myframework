package automation.script;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class test2d {
	
	
	public ArrayList<String> getexceldata(){
		List<String> list = new ArrayList<String>();
		 File excel = new File ("D://ADTF//TestCases.xlsx");
		    FileInputStream fis = new FileInputStream(excel);

		    XSSFWorkbook wb = new XSSFWorkbook(fis);
		    XSSFSheet sheet = wb.getSheetAt(0);
		    String Allcolvalues="";
		    int rowNum = sheet.getLastRowNum()+1;
		    int colNum = sheet.getRow(0).getLastCellNum();
		    String[][] data = new String[rowNum][colNum];
		    for (int i=0; i<rowNum; i++){
		        //get the row
		        XSSFRow row = sheet.getRow(i);
		            for (int j=0; j<colNum; j++){
		                //this gets the cell and sets it as blank if it's empty.
		                XSSFCell cell = row.getCell(j, Row.CREATE_NULL_AS_BLANK);
		                String value = String.valueOf(cell);                             
		                System.out.println("Value: " + value);
		                if (Allcolvalues.isEmpty()){
		                	Allcolvalues=value;
		                }else{
		                Allcolvalues=Allcolvalues+","+value;
		                }
		            }      
		            list.add(i, Allcolvalues);
		       }
		    System.out.println("End Value:  " + data[2][2]);
		    

		
		
		return (ArrayList<String>) list;
		
		
	}
	
	

		public static void main(String[] args) throws Exception {
		   	
			
		}
		
}
