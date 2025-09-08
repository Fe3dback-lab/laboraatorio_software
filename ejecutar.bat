@echo off
echo Compilando compilador...
g++ "compilador (1).cpp" -o compilador
if errorlevel 1 goto error

echo Ejecutando compilador...
compilador
if errorlevel 1 goto error

echo Compilando graf.cpp con SFML...
g++ -c graf.cpp -I"C:\SFML-3.0.0\include"
if errorlevel 1 goto error

g++ graf.o -o graf -L"C:\SFML-3.0.0\lib" -lsfml-graphics -lsfml-window -lsfml-system
if errorlevel 1 goto error

echo Ejecutando graf...
graf
if errorlevel 1 goto error

echo Todo se ejecuto correctamente.
goto end

:error
echo Ocurrió un error durante el proceso.
pause

:end
pause
