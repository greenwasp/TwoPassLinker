

import java.io.*;
import java.util.*;
import java.io.IOException;
import java.util.InputMismatchException;


/*
 * This class deals with all attributes of a module
 * */
class Module{
	//Variable Declaration
	private String defList[]; //holds the variables in Definition list of a module
	private int valDefList[];//holds only the relative addresses of symbol appearing in Definition list
    private String useList[];//holds use list of a module
    private String codeList[];//holds all the codes ("E","R","I" or "A") in a module
    private String codeWordList[];//holds all words following the codes in module
    private int isVariableUsed[];//If a symbol in the use list is used in the Module then the corresponding value in useFlag array is set to 1
    private int moduleNum; //Stores the module number of a module
    private int moduleBaseAddress;//Stores the base address of the module
    private int noOfVarDef;//Stores number of arguments of Definition list
    private int noOfVarUse;//Stores number of arguments of Use list
    private int noOfVarCode;//Stores number of arguments of Code list
    static int sizeOfArray=50;//array sizeOfArray
    
    //Constructor
    Module()
    {
    	//Initialize all variables used in class Module
    	defList=new String[sizeOfArray];
        useList=new String[sizeOfArray];
        codeList=new String[sizeOfArray];
        codeWordList=new String[sizeOfArray];
        valDefList = new int[sizeOfArray];
        isVariableUsed=new int[sizeOfArray];
        moduleNum=0;
        moduleBaseAddress=0;
    	
    }
	//Get/Set properties of Private variables
    public String[] getDefList()
    {
        return defList;
    }
    
    public void SetDefList(String[] defVar,int [] addRel){
    	for (int i = 0; i < defVar.length; i++) {
			defList[i] = defVar[i];
			valDefList[i] = addRel[i];
		}
    }
    
    public String[] getUseList(){
    	return useList;
    }
    
    public void SetUseList(String[] defUse){
    	for (int i = 0; i < defUse.length; i++) {
			useList[i] = defUse[i];
		}
    }
    
    public String[] getCodeList(){
    	return codeList;
    }
    
    public void SetCodeList(String[] codeVar, String[] codeWordVar){
    	for (int i = 0; i < codeVar.length; i++) {
			codeList[i] = codeVar[i];
			codeWordList[i] = codeWordVar[i];
		}
    }
    public String[] GetCodeWordList(){
    	return codeWordList;
    }
    public int[] GetIsVariableUsed(){
    	return isVariableUsed;
    }
    
    public void SetIsVariableUsed(int[] indicator){
    	isVariableUsed = indicator;
    }
    
    public int GetModuleNum(){
    	return moduleNum;
    }
    
    public void SetModuleNum(int modNo){
    	moduleNum = modNo;
    }
    
    public int GetModuleBaseAddress(){
    	return moduleBaseAddress;
    }
    
    public void SetModuleBaseAddress(int modBAdd){
    	moduleBaseAddress = modBAdd;
    }
    
    public int GetNoOfVarDefLst(){
    	return noOfVarDef;
    }
    
    public void SetNoOfVarDefLst(int noVarInDef){
    	noOfVarDef = noVarInDef;
    }
    
    public int GetNoOfVarUseLst(){
    	return noOfVarUse;
    }
    
    public void SetNoOfVarUseLst(int noVarInUse){
    	noOfVarUse = noVarInUse;
    }
    
    public int GetNoOfVarCodeLst(){
    	return noOfVarCode;
    }
    
    public void SetNoofVarCodeLst(int noVarInCode){
    	noOfVarCode = noVarInCode;
    }
}
public class TwoPassLinker {

	//Variable Declarations/Definitions
	private String fName; //holds file name
	private int moduleCounter;//keep a count of module number
    private String symTable[];//stores the external symbols in symbol table
    private int symValTable[];//stores the corresponding values of the symbols appearing in symTable
    private int symFlag[];//act as a flag for each value of symTable if symFlag[i]=1 then the symbol in symTable[i] is multiply defined
    private int symAddedCounter;//keep a count of symbols added in the symbol table
    private int Flag[];	//this array is maintained to keep track whether the symbols defined in symbol table are actually used,the value corresponding
    			//to the symbol in the symTable array will be set to 1 if the symbol is used
    private int moduleBaseAdd[];//this array stores the module address of the modules moduleBaseAdd[0] will contain the base address of module1
    private int modNumber[];//storing the module number corresponding to the symbols appearing in symbol table
	
	//Constructor
	TwoPassLinker(){
		//Initialize all the variables used in Class TwoPassLinker
		symTable=new String[Module.sizeOfArray];
		symValTable=new int[Module.sizeOfArray];
        symAddedCounter=0;
        Flag=new int[Module.sizeOfArray];
        modNumber=new int[Module.sizeOfArray];
        symFlag=new int[Module.sizeOfArray];
        moduleBaseAdd=new int[Module.sizeOfArray];
		
	}
	
	/*
	 * Set File Name to be used for Linker
	 * */
	private void SetFileName (String fileName){
		fName = fileName;
	}
	
	private int IfVariableExists(String chkVar){
		for (int j = 0; j < symAddedCounter; j++) {
			
			//Determine if variable/symbol is already added to the symbol table
			if(chkVar.equals(symTable[j])){
				return j;
			}
		}
		return -3;
		
	}
	private int GetVariable(String str){
        for (int i=0;i<symAddedCounter;i++)
        {
            if(symTable[i].equals(str))
            {   
	            Flag[i]=1;
	            return symValTable[i];
            }
        }
        return -1;
	}
	
	/*
	 * Method to execute First Pass of the Linker
	 * 1) Calculate Base address for each module
	 * 2) Symbol Table for all variables from all modules
	 * */
	private void PassOne(){
		String strVar;//stores the variable in the definition list  
		int valDefVariable = 0;// stores the absolute address of a variable 
        int moduleBaseAddr=0;// stores the base address of the current module
        int noOfVar=0;//stores the number of variables in def or use or code list
        int chkIfVarExists =0;//stores the value returned by the function IfVariableExists
        
        moduleCounter=0;
        Scanner fScanner = readFromFile();
       
        try{
            //Symbol Table for input
        	System.out.println("\n \nSymbol Table!");
        	
        	while(fScanner.hasNext()){
              	moduleBaseAdd[moduleCounter] = moduleBaseAddr; //Set/Calculate base address for each module
        		moduleCounter++; //Increment the counter of modules to continue calculating the base address for each
        		
        		//Definition list
        		noOfVar = fScanner.nextInt(); //Fetches the first symbol in def list which specifies the number of variables in def list
        									  // and sets it against noOfVar to scan through all the variables in def list
        		
        		//Scan through all variables in def list, calculate their absolute address
        		//Determine if these are already in symbol table (by using the flag); if not, add them
        		//Set module count added
        		if(noOfVar >= 0){
        			
	        		for (int i = 0; i < noOfVar; i++) {
						strVar = fScanner.next();
						valDefVariable = fScanner.nextInt() + moduleBaseAddr;
						
						chkIfVarExists = IfVariableExists(strVar);
						
						if(chkIfVarExists == -3){
							symTable[symAddedCounter] = strVar;
							modNumber[symAddedCounter] = moduleCounter;
							symValTable[symAddedCounter++] = valDefVariable;
						}
						else{
							Flag[chkIfVarExists] = 1; //denotes multiply defined
						}
					
					}
        		}
        		
        		//Use List
        		noOfVar = fScanner.nextInt();
        		if (noOfVar >= 0) {
        			for (int i = 0; i < noOfVar; i++) {
    					fScanner.next();
    				}
				}
        		
        		
        		        		
        		//Code List
        		noOfVar = fScanner.nextInt();
        		if (noOfVar >= 0) {
        			for (int i = 0; i < noOfVar; i++) {
    					fScanner.next();
    					fScanner.nextInt();
    				}
				}
        		
        		
        		//Symbol Table
        		for (int i = 0; symTable[i] != null; i++) {
					if (symValTable[i] >= (noOfVar + moduleBaseAddr)) {
						symValTable[i] = moduleBaseAddr ;
						symFlag[i] = 99 ; //Set an indicator to handle this situation
					}
				}
        		moduleBaseAddr = moduleBaseAddr + noOfVar; //Update module base Address for following modules
        	}
        	
        	HandlePassOneErrors();
        	fScanner.close();
        }
        
        catch (ArrayIndexOutOfBoundsException ex) {
			System.out.println("Array bound exceeded : "+ Module.sizeOfArray);
			System.out.println("Please reset the array size and execute the program again");
			System.exit(1);
		}
        catch (InputMismatchException ex)
        {
        	System.out.println("Input format mismatch.Program will exit.");
        	System.exit(1);
        }
	}
	private void HandlePassOneErrors(){
		for (int i=0;symTable[i]!=null;i++)
        {
        	if (symFlag[i]==99)
        	{
        		System.out.print(symTable[i]+" = "+symValTable[i]);
        		System.out.println(" Error!! Address exceeds the size Of Array for module; treated as 0 (relative)");
        	}
        	else if ( symFlag[i]!=1 )// multiply defined
        	{
        		System.out.println(symTable[i]+" = "+symValTable[i]);
        	}
        	else
        	{
        		System.out.print(symTable[i]+" = "+symValTable[i]);
        		System.out.println(" Error!! This variable is multiply defined; first value used.");
        	}
        }
	}
	/*
	 * Method to execute Second Pass of the Linker
	 * 1) Relocate Relative (R) address
	 * 2) Resolve external reference (E)
	 * 
	 */
	 
	private void PassTwo(){
		Module [] objModule=new Module[moduleCounter]; //objects of Module class - totaling the total number of modules in a file
        int noOfVar=0;//number of variables in definition or use or code list
        int counter =0;
        int moduleNumber=0; //count of module number
        Scanner fScanner = readFromFile();
        try{
          
        	System.out.println("\nMemory Map \n");
        	while (fScanner.hasNext()) {
        		moduleNumber=counter+1;
                String [] defList= new String[Module.sizeOfArray];//Container for Def list
                int [] relativeAddr=new int[Module.sizeOfArray];//Container for relative address for variables in def list
                String [] useList= new String[Module.sizeOfArray];//use list
                String [] codeList= new String[Module.sizeOfArray];//codes
                String [] codeWordList= new String[Module.sizeOfArray];
        		
                objModule[counter]=new Module();        
                
                /*definition List*/
                noOfVar=fScanner.nextInt();
                objModule[counter].SetNoOfVarDefLst(noOfVar);
                
                if (noOfVar >= 0) {
                	for(int i=0;i<noOfVar;i++){
                        defList[i]=fScanner.next();
                        relativeAddr[i] = fScanner.nextInt();
                    }
				}
                
                
                /* use list*/
                noOfVar=fScanner.nextInt();
                if (noOfVar >= 0) {
                	objModule[counter].SetNoOfVarUseLst(noOfVar);
                    for (int i = 0; i < noOfVar; i++) {
    					useList[i] = fScanner.next();
    				}
				}
                
                
                /*Code/word*/
                noOfVar=fScanner.nextInt();
                if (noOfVar >= 0) {
                	objModule[counter].SetNoofVarCodeLst(noOfVar);
                    for(int i=0;i<noOfVar;i++)
                    {
                    	codeList[i]=fScanner.next();
                        codeWordList[i]=fScanner.next();
                    }
				}
                
                objModule[counter].SetDefList(defList,relativeAddr);
                objModule[counter].SetUseList(useList);
                objModule[counter].SetCodeList(codeList,codeWordList);
                objModule[counter].SetModuleBaseAddress(moduleBaseAdd[counter]);
                objModule[counter].SetModuleNum(moduleNumber);
                counter++;
		
			}
        }
        
        catch (ArrayIndexOutOfBoundsException ex) {
			System.out.println("Arrays bound exceeded : " + Module.sizeOfArray + "Reset the variable array sizeOfArray contained in variable sizeOfArray and execute again");
			System.exit(1);
		}
        catch (InputMismatchException ex)
        {
        	System.out.println("Incorrect Input Format.Program will exit.");
        	System.exit(1);
        }
        
        PassTwoResolveAddress(objModule);
        
		System.out.println();
		
		PassTwoWarningsCheck();
				
		//Determine if any variable defined in use list ; not used in module - Print warnings for the same
		PassTwoUnusedVariableCheck(objModule);
	}
	
	private void PassTwoResolveAddress(Module[] objModule){
		int loopCounter=0;
		for(int i=0;i<moduleCounter;i++)
		{
	        String [] codeType=new String[Module.sizeOfArray];//Code list
	        String [] codeAdd=new String[Module.sizeOfArray];//code word list 
	        String [] useList=new String[Module.sizeOfArray];
	        int [] isVariableUsed =new int[Module.sizeOfArray];
	        int moduleBaseAddress=0;
	        int noOfVarInCode =0;
	        int noOfVarInUse=0;
	        String word;
	        String variable;
	        int symbolValue=0;//stores value returned from getSymbolValue function
	        int address=0;//for calculating address out of a word
	        int absAdd=0;//for calculating absolute address
	        codeType=objModule[i].getCodeList();
	        codeAdd=objModule[i].GetCodeWordList();
	        useList=objModule[i].getUseList();
	        noOfVarInUse=objModule[i].GetNoOfVarUseLst();
	        noOfVarInCode=objModule[i].GetNoOfVarCodeLst();
	        moduleBaseAddress=objModule[i].GetModuleBaseAddress();
	        isVariableUsed=objModule[i].GetIsVariableUsed();
	        
		    for (int j=0;j<noOfVarInCode;j++)
		    {
                word=codeAdd[j];//sets to 4 digit word
                absAdd=Integer.parseInt(word);
                address=Integer.parseInt(word.substring(1,4));//address is the last 3 digits of the 4 digit word
                if (codeType[j].equals("R"))
                {
                    if(address < noOfVarInCode)//no of Arguments of program text determines the module length or sizeOfArray
                    {
                        absAdd+=moduleBaseAddress;//this gives the absolute address
                        System.out.println(loopCounter+":"+absAdd);
                    }
                    else
                    {
                        absAdd= absAdd-address;
                        System.out.print(loopCounter+":"+absAdd);
                        System.out.println(" Error: Relative address exceeds module sizeOfArray; zero used.");
                    }
                }
                else if (codeType[j].equals("A"))
                {
                    if(address < 600)//Absolute address should be less than machine sizeOfArray 
                    {
                        System.out.println(loopCounter+":"+absAdd);
                    }
                    else
                    {
                        absAdd= absAdd-address;
                        System.out.print(loopCounter+":"+absAdd);
                        System.out.println(" Error: Absolute address exceeds machine sizeOfArray; zero used.");
                    }
                }
                else if (codeType[j].equals("E"))
                {
                    if (address < noOfVarInUse)
                    {
                        variable=useList[address];
                        
                        /*useFlag is set to 1 corresponding to the symbols in the useList array which shows that the 
                         * symbol defined in the use list has been used so no warning is required for this symbol
                         * */
                        isVariableUsed[address]=1;
                        symbolValue=GetVariable(variable);//symbolValue of -1 will be returned if the symbol does not exist in symbol table
                        if (symbolValue == -1)
                        {
                        	symbolValue=0;
                            absAdd= absAdd-address+symbolValue;
                            System.out.print(loopCounter + ":" + absAdd);
                            System.out.println(" Error: "+variable+" is not defined but is used.");
                        }
                        else
                        {
                        	absAdd= absAdd-address+symbolValue;
                            System.out.println(loopCounter + ":" + absAdd);
                        }
                       
                       }
	                    else
	                    {
	                        System.out.print(loopCounter+":"+absAdd);
	                        System.out.println(" Error: External address exceeds length of use list; treated as immediate.");
	                    }
                   }
                else
                {
                    System.out.println(loopCounter+":"+absAdd);
                }
                loopCounter++;
	        }
		}
	}

	private void PassTwoWarningsCheck(){
		for (int i=0;i<symAddedCounter;i++)
        {
            if (Flag[i]!= 1)
            {
                System.out.println("Warning: "+symTable[i]+" was defined in module "+modNumber[i]+", but never used.");
            }
        } 
		
	}
	
	private void PassTwoUnusedVariableCheck(Module[] objModule){
		for (int i=0;i<moduleCounter;i++)
		{
            int [] useFlag=new int[Module.sizeOfArray];
            String [] useList=new String[Module.sizeOfArray];
            int moduleNum;
            useFlag=objModule[i].GetIsVariableUsed();
            useList=objModule[i].getUseList();
            moduleNum=objModule[i].GetModuleNum();
            for(int j=0;useList[j]!=null;j++)
            {
                if(useFlag[j]!=1)//this means the symbol was never used
                {
                   System.out.println("Warning: In module "+moduleNum+", "+ useList[j] +" appeared in the use list but was not actually used.");
                }
           }
		}
	}
	
	/*
	 * Returns scanner for the file to be used as input
	 * */
	private Scanner readFromFile(){
		Scanner scanner=null;
		try
		{
		/*InputStream input = getClass().getResourceAsStream(fName);
		
		if(input != null){
			scanner= new Scanner (new InputStreamReader(input));
			}*/
			FileReader fReader = new FileReader(fName);//this.fileName contains the name of file passed as command line argument
	        scanner= new Scanner (fReader);
		
	    
		}
		catch(FileNotFoundException ex){
			System.out.println("File " + fName + " not Found. Program will exit.");
			System.out.println("Execute again and provide proper file name with entire path.");
			System.exit(0);
			return null;
		}
	    return scanner;
	}
	
	
	public static void main(String[] args) {
		//Call method to read from file
	    TwoPassLinker L1 = new TwoPassLinker();
	    
	    InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		
		String fileName = null;
		
		try{
			fileName = br.readLine();
		    L1.SetFileName(fileName); //file name received from command line
		    L1.PassOne(); //Execute Pass one of two pass linker
		    L1.PassTwo(); //Execute Pass Two of two pass linker
		 }
		
		catch (FileNotFoundException ex){
			System.out.println("File " + fileName +" not Found.Program will exit!");
		    System.exit(0);
		    	
		}
	    catch (IOException ex){
	    	System.out.println("IO Error. Program will exit!");
	    	System.exit(0);
	      }
	}

}
	


