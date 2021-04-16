## Overview

Starter App: It is a sample application created to show the power of the transform framework 
in creating the ETL jobs. It has default input, transform and output processors. 
These can be overridden to have custom logic for the application. It also has a JSL(job specification 
language) file which has input, transform and output section and they represent the extraction, transformation
and loading steps of ETL job.  

## Usage
### System Requirements to build:

* Java 8
  
* maven
  
### Quick Start

* Clone this project.
 
* Import as Maven project into Eclipse or IntelliJ (Note: this repo supports JAVA version 1.8).
 
* Update Maven dependencies following Project properties->Maven→Update Project.
 
* Build the Java project following Project→Build Project.
 
* Run App.java (under com.eamericanexpress.omnitransform.app) as a Java application.
 
By now you should have a working application that demonstrates the use of the framework to parse fixed length files, transform them using simple SQL statements and generate output file in the Parquet format.

The Input, Transform and Output configuration is specified through a JSL (job spec language) file. (src/main/resources/sales-jsl-config.json)

The test Input files are available under (src/test/resources).

Once the job executes, it will generate Parquet Output files under (src/test/resources/output).

<h1>
  <div align="center"><img src='https://stash.aexp.com/stash/projects/AIM600000785/repos/transform-framework-opensource/raw/examples/transform-app-starter/OmniTransform-example.png' alt="OmniTransform Starter" width='100%'/>  OmniTransform Starter</div>
</h1>

