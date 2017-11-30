# Steps you have to follow inorder to get results

1.  Initially you have to download the zip from GitHub.

2.  Then you have to import the dump.sql file into your local repository (MySQL Workbench).

3.  After that open AnalyzeOpenPR project folder via your IDE. 

4. open src/main/java/com/wso2/org directory and Run PullRequestAnalysis.java file.

    In particular java file you have to insert following String values,

           Token key= “your personal github access token”
           
           String user= “Mysql workbench userName”
           
           String psw= “Mysql workbench password”

 5.  Then required results will be in your local database in the table 'RetrieveOpenPR'.
