@echo off
javac -cp ".;mysql-connector-j-9.4.0.jar;jbcrypt-0.4.jar" ^
    database/*.java ^
    auth/*.java ^
    dao/*.java ^
    models/*.java ^
    ui/login/*.java ^
    ui/studentdash/*.java ^
    ui/instructordash/*.java ^
    Main.java

echo Build completed SIR!
pause
