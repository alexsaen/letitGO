REM ����������� patterns33.dat ����� ��� DraGo, ������� �� �������� ��������� classpath �.�. � ���� �� ����� ";"
REM ��� ������ ����� GTP ������ ����� -gtp � ����� �������

javac -d build com\letitgo\Main.java && copy /y res\patterns33.dat build && java -cp build;res com.letitgo.Main -gtp