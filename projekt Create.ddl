CREATE TABLE Bilet (
  ID                      int(10) NOT NULL AUTO_INCREMENT, 
  Uzytkownik_zalogowanyID int(10) NOT NULL, 
  SeansID                 int(10) NOT NULL, 
  StatusID                int(10) NOT NULL, 
  PRIMARY KEY (ID));
CREATE TABLE Film (
  ID                 int(10) NOT NULL AUTO_INCREMENT, 
  Nazwa              varchar(50) NOT NULL, 
  Rezyser            varchar(50), 
  Plakat             varchar(255), 
  Czas_trwania       time(7) NOT NULL, 
  Gatunek            varchar(50), 
  Kraj_pochodzenia   varchar(50), 
  Jezyk_wyswietlania varchar(50) NOT NULL, 
  Ocena              decimal(2, 1), 
  PRIMARY KEY (ID));
CREATE TABLE Jezyk_napsiow (
  ID           int(6) NOT NULL AUTO_INCREMENT, 
  Nazwa_jezyka varchar(50) NOT NULL, 
  PRIMARY KEY (ID));
CREATE TABLE Sala (
  ID        int(2) NOT NULL AUTO_INCREMENT, 
  Pojemnosc int(3) NOT NULL, 
  PRIMARY KEY (ID));
CREATE TABLE Seans (
  ID               int(10) NOT NULL AUTO_INCREMENT, 
  Jezyk_napsiowID  int(6), 
  Czas_rozpoczecia timestamp NOT NULL, 
  Czas_zakonczenia timestamp NOT NULL, 
  Wolne_miejsca    int(3) NOT NULL, 
  FilmID           int(10) NOT NULL, 
  SalaID           int(2) NOT NULL, 
  Dubbing          tinyint(1) NOT NULL, 
  PRIMARY KEY (ID));
CREATE TABLE Status (
  ID     int(10) NOT NULL AUTO_INCREMENT, 
  Status varchar(50) NOT NULL, 
  PRIMARY KEY (ID));
CREATE TABLE Uzytkownik_zalogowany (
  ID             int(10) NOT NULL AUTO_INCREMENT, 
  `e-mail`       varchar(50) NOT NULL UNIQUE, 
  Imie           varchar(50) NOT NULL, 
  Nazwisko       varchar(50) NOT NULL, 
  Data_urodzenia date NOT NULL, 
  Klient         tinyint(1), 
  Kasjer         tinyint(1) NOT NULL, 
  Kierownik      tinyint(1) NOT NULL, 
  PRIMARY KEY (ID));
ALTER TABLE Seans ADD CONSTRAINT FKSeans684682 FOREIGN KEY (FilmID) REFERENCES Film (ID);
ALTER TABLE Seans ADD CONSTRAINT FKSeans525471 FOREIGN KEY (SalaID) REFERENCES Sala (ID);
ALTER TABLE Seans ADD CONSTRAINT FKSeans804771 FOREIGN KEY (Jezyk_napsiowID) REFERENCES Jezyk_napsiow (ID);
ALTER TABLE Bilet ADD CONSTRAINT FKBilet791687 FOREIGN KEY (Uzytkownik_zalogowanyID) REFERENCES Uzytkownik_zalogowany (ID);
ALTER TABLE Bilet ADD CONSTRAINT FKBilet142996 FOREIGN KEY (SeansID) REFERENCES Seans (ID);
ALTER TABLE Bilet ADD CONSTRAINT FKBilet88827 FOREIGN KEY (StatusID) REFERENCES Status (ID);

