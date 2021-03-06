<h1 align="center">Softeng 306 Project 1: Using AI and parallel processing power to solve difficult problem</h1>

### Project Overview ###
This project is about using artificial intelligence and parallel processing power to solve a difficult scheduling problem. It is about Speed! In this project of the SoftEng306 design course your client is the leader of a Parallel Computing Centre, who needs you to solve a difficult scheduling problem for his parallel computer systems. This client happens to be Oliver Sinnen. The project is well defined and scoped, but the particular challenge of this project are the (possibly) contradicting objectives: developing a solution with a Software Engineering approach and design while having a very fast execution speed. At the end of the project your solutions will compete against each other in a speed comparison. Your team might choose to contribute the solution to the PARC lab for their work.
### Team Details ###
|Name|upi   |id number|email   |github username   |
|--|---|---|---|---|
|Corey Hill|chil144|940666365   |chil144@aucklanduni.ac.nz   |CMH133   |
|Grace Meng|ymen958|809626093   |ymen958@aucklanduni.ac.nz   |Shira-n   |
|Jenny Lee|jlee923|924698666   |jlee923@aucklanduni.ac.nz   |0608jennylee   |
|Joshua Rosairo|jtha772|631382102   |jtha772@aucklanduni.ac.nz   |josh-rosairo   |
|Suying Shen|sshe899|517494842   |sshe899@aucklanduni.ac.nz   |sueyin   |

## Installation introduction ##

Basic Milestone release: https://github.com/Shira-n/306ProjectOne/releases/tag/V1.0 Available from August 6 2018, 12pm.
Final Milestone release: https://github.com/Shira-n/306ProjectOne/releases/tag/v2.0 Available from August 20 2018, 12pm.
1. Download scheduler.jar
2. Open command line
3. Navigate to the folder containing scheduler.jar
4. Add the input dot file to the same level as the scheduler.jar
5. Run using `java -jar scheduler.jar INPUT.dot 2` where INPUT.dot is the name of the input file and 2 is the number of processors to be used for the schedule. Refer to the manual below for command line options

Requirements:
Please run on a computer with resolution 1680 x 1050.

~~~~
java -jar scheduler.jar INPUT.dot P [OPTION]
INPUT.dot  a task graph with integer weights in dot format
P          number of processors to schedule the INPUT graph on

Optional:
-p N       use N cores for executions in parallel (default is sequential)
-v         visualise the search
-o OUTPUT  output file is named OUTPUT (default is INPUT-output.dot)
~~~~

### Example images of Final Milestone
#### Example not started
![](https://github.com/Shira-n/306ProjectOne/blob/master/Wiki-Resources/1.PNG)
#### Example complete
![](https://github.com/Shira-n/306ProjectOne/blob/master/Wiki-Resources/2.PNG)
#### Example running
![](https://github.com/Shira-n/306ProjectOne/blob/master/Wiki-Resources/3.PNG)
#### Example 2 not started
![](https://github.com/Shira-n/306ProjectOne/blob/master/Wiki-Resources/4.PNG)
#### Example 2 running
![](https://github.com/Shira-n/306ProjectOne/blob/master/Wiki-Resources/5.PNG)
#### Example 2 complete
![](https://github.com/Shira-n/306ProjectOne/blob/master/Wiki-Resources/6.PNG)
#### Example 2 output chart
![](https://github.com/Shira-n/306ProjectOne/blob/master/Wiki-Resources/7.PNG)

Refer to wiki for more information
Link: https://github.com/Shira-n/306ProjectOne/wiki
