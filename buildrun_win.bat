REM ����������� patterns33.dat ����� ��� DraGo, ������� �� �������� ��������� classpath �.�. � ���� �� ����� ";"
REM ��� ������ ����� GTP ������ ����� -gtp � ����� �������
mkdir build
copy /y res\patterns33.dat build
javac -d build com\letitgo\Main.java && java -cp build;res com.letitgo.Main