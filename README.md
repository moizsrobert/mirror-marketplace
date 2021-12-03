# Mirror Marketplace

Egyszerűen kezelhető online piactér minimális átirányítással.

Backend (Spring Boot) & DB (MySQL)

Adatbázis inicializálása:

A backend MySQL adatbázissal működik, innen letölthető:
https://dev.mysql.com/downloads/windows/installer/8.0.html

A telepítőben ezeket a product-okat kell kiválasztani:
https://pasteboard.co/qudMI4VMl0e6.png

A MySQL Server konfigurálásánál username-nek "root"-ot, jelszónak "asd123"-at, portnak "3306"-ot kell megadni,
de ha más lett, akkor csak át kell írni a "spring: datasource:" alatti értékeket a
src/main/resoures/application.yml fájlban.

Ha már fut a MySQL Server, akkor MySQL Workbench-en keresztül lehet hozzá csatlakozni.
(Hostname: localhost, Port: 3306, Username: root, csatlakozásnál a jelszó: asd123)
Amint sikerült belépni itt létrehozunk egy "marketplace" nevű scheme-t és készen vagyunk.

Ha a program problémázik indításnál, akkor nagy eséllyel át kell állítani a Project SDK-t Java 17-re,
vagy nincsen elindítva a MySQL Server.
(Ha telepítve van, akkor Windows-on kereső -> services.msc -> MySQL80 -> Indítás)
Célszerű IntelliJ-t használni hozzá.
