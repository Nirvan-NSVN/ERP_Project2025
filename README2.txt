This file is solely made by Nirvan 2024390 and Virat Yadav 202462.
We have used our extreme most skills to identify and make the working ERP though there can be some flaws but in future. We will going to work on future too.
For now, Virat had done Login Screen and instructor dashboard
Nirvan had done student dashboard and admin dashboard.

We have segregated are files to models, Dao , Users ,auth , databases, and ui

where DAO stands for database access to organisation and models contain the structure where ui and database as per name.

Now, next is Through README PDF Version.

run command: ./erpbuild.bat -> press any key -> ./erprun.bat and then work

run command manual:javac -cp ".;mysql-connector-j-9.4.0.jar;jbcrypt-0.4.jar" "database/*.java" "auth/*.java" "dao/*.java" "models/*.java" "ui/login/*.java" "ui/studentdash/*.java" "ui/instructordash/*.java" Main.java  
and then 
java -cp ".;mysql-connector-j-9.4.0.jar;jbcrypt-0.4.jar" Main