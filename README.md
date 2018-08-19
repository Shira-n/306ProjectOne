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
### Basic Milestone

Basic Milestone release: https://github.com/Shira-n/306ProjectOne/releases

Available from August 6 2018, 12pm.
1. Download Scheduler.jar
2. Open command line
3. Navigate to the folder containing Scheduler.jar
4. Add the input dot file to the same level as the Scheduler.jar
5. Run using `java -jar greedyScheduler.jar INPUT.dot 2` where INPUT.dot is the name of the input file and 2 is the number of processors to be used for the schedule. Refer to the manual below for command line options

~~~~
java -jar greedyScheduler.jar INPUT.dot P [OPTION]
INPUT.dot  a task graph with integer weights in dot format
P          number of processors to schedule the INPUT graph on

Optional:
-p N       use N cores for executions in parallel (default is sequential)
-v         visualise the search
-o OUTPUT  output file is named OUTPUT (default is INPUT-output.dot)
~~~~

NOTE: parallisation and visualise is not currently available for basic milestone

Refer to wiki for more information
Link: https://github.com/Shira-n/306ProjectOne/wiki
