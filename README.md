## Purpose
Purpose of this project is to identify the updating capability of  WSO2 dependencies in components. 

## Goals
Create a tool that can visualize the build status of WSO2 product areas after updating WSO2 dependencies in each component to the latest available version/ latest available minor version.  

## Approach
The solution consists of three components. 

1. Dependency Version Updater - 
maven project which can 

- Retrieve a set of WSO2 components
- Analyze each component for WSO2 dependencies
- Find the latest version for each valid  dependency
- Update all dependencies to the latest version
- Build component and Save the build status along with the Unix timestamp

2. dashboard.dataservice -
Microservice to expose build statistics to dashboard 

3. Dashbord
WSO2 Dashboard Server 2.0.0 plugin which can visualize the build status of each product area after dependency update.

## Documentation
To run the application, locate the Dependancy-Version-Updater-1.0-SNAPSHOT.jar in target folder and execute `java -jar Dependancy-Version-Updater-1.0-SNAPSHOT.jar`. 
You can manully change the configurations by changing the attributes in Resources/config.xml




 
