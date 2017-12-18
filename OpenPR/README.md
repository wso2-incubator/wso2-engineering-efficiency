# Steps you have to follow inorder to get results

1.  Initially you have to download the OpenPRAnalysis folder from GitHub.

2.  Extract it and provide following details in config.properties

	GitHubtokenKey  =>  your github token key
	databaseUrl     =>  your local host url
	user            =>  Username of your localhost
	password        =>  Password of your local host


	jenkinsDbUrl    =>  Provide jenkins Database Url
	userNameJenkins =>  jenkins username
	PasswordJenkins =>  jenkins password




3.  Then you have to import the dump.sql file into your local repository (MySQL Workbench).

4.  After that open OpenPRAnalysis project folder via your IDE and run OpenPRAnalysis class. 
  
5.  Then required details will be in your local database Open-pr-requests in the table 'RetrieveOpenPR'.


# Dashboard Implementation

1.  First you have to add the OpenPRAnalysis folder into your your php server directory. (/opt/lampp/htdocs)
	*** Ensure your pc have xampp
	

2.  Then you have to add database url, username and password in db.php file.

3.  In your browser redirect to OpenPRAnalysis folder by localhost/OpenPRAnalysis , There your dashboard display.
