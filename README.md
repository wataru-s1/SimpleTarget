# SimpleTarget

This program implements an algorithm to execute SimpleTarget algorithm.

# Requirements

This program calls IBM ILOG CPLEX Optimization Studio internally.
To download CPLEX, see [here](https://www.ibm.com/id-en/products/ilog-cplex-optimization-studio).

# Usage

If you do not have JDK installed, install it from
[here](https://www.oracle.com/java/technologies/downloads/#jdk19-windows) and insert the path
so that the &quot;java&quot; command can be used. The path is &quot;SimpleTarget/bin&quot;.

Download all class files (files with &quot;class&quot; extension) in the &quot;SimpleTarget/bin&quot; folder.

Place the file with the edge list under &quot;... /DATA&quot;.

Use one folder for analysis of one network.
The folder structure should be as follows.


../DATA ━┳━ /NETWORK_1  
　　　　 ┃　　　┣━━━ /net  
　　　　 ┃　　　┃　　　 ┗━━━ NETWORK_1.txt  
　　　　 ┃　　　┗━━━ /target  
　　　　 ┃　　　　　　　　┣━━━ TARGET_1.txt  
　　　　 ┃　　　 　　 　　 ：  
　　　　 ┃　　　　　　　　┗━━━ TARGET_LAST.txt  
　　　　 ┃  
　　　　 ┣━━━ /NETWORK_2  
　　　　 ┃　　　┣━━━ /net  
　　　　 ┃　　　┃　　　 ┗━━━ NETWORK_2.txt  
　　　　 ┃　　　┗━━━ /target  
　　　　 ┃　　　　　　　　┣━━━ TARGET_1.txt  
　　　　 ┃　　　 　　　 　 ：  
　　　　 ┃　　　　　　　　┗━━━ TARGET_LAST.txt  
　 　 　 ：  


The directed NETWORK data should be written using an edge list with tab-delimited as follows.

v1 v2

v2 v4

v2 v3

v2 v5

v3 v1

v6 v1

：


The TARGET data should be written as follows.

v1

v3

：


Go to the folder containing the class files and enter the following command:

java -clssspath ".;PATH_TO_cplex.jar" Main


If your cplex.jar exists in C:\test, the execution command will be following.

java -clssspath ".;C:\test\cplex.jar" Main


The above command works on windows OS.

If you want to run it on other os, please refer to the options of the java command.


If it works correctly, you will see &quot;Please enter DATA folder path. Then, enter the path of &quot;...
/DATA&quot;.

The program will then execute the folder path in &quot;... /DATA&quot; and output the results in a folder
named NEW_RESULT in the same hierarchy as the DATA.

# Author
Wataru Someya

Toho University

6522009s@st.toho-u.jp
