DROP DATABASE IF EXISTS montealcohol;
CREATE DATABASE montealcohol;
USE montealcohol;


CREATE TABLE Cliente (
    Nif_Cli        VARCHAR(15) PRIMARY KEY,
    Nombre         VARCHAR(50) NOT NULL,
    Apellido       VARCHAR(50) NOT NULL,
    Calle          VARCHAR(100) NOT NULL,
    Numero         INT NOT NULL,
    Piso           VARCHAR(10),
    Localidad      VARCHAR(50) NOT NULL,
    Provincia      VARCHAR(50) NOT NULL,
    Telefono       VARCHAR(50),   -- Teléfono o email
    Email          VARCHAR(100)            -- Opcional
);

CREATE TABLE Proveedor (
    Nif_Prove      VARCHAR(15) PRIMARY KEY,
    Nombre         VARCHAR(50) NOT NULL,
    Localidad      VARCHAR(50) NOT NULL,
    Telefono       VARCHAR(20) ,
    Email          VARCHAR(100)
);


CREATE TABLE Producto (
    Cod_Pro        VARCHAR(5) PRIMARY KEY,
    Nom_Pro        VARCHAR(50) NOT NULL,
    Precio_Pro     DECIMAL(10,2) NOT NULL CHECK (Precio_Pro > 0),
    Stock          INT NOT NULL CHECK (Stock >= 0),
    Tipo           ENUM('Fermentadas','Destiladas','Encabezadas','Licores','Nuestra Selección') NOT NULL,
    Nif_Prove      VARCHAR(15) NOT NULL,
    FOREIGN KEY (Nif_Prove) REFERENCES Proveedor(Nif_Prove)
        ON DELETE CASCADE on update cascade,
    CHECK (Cod_Pro REGEXP '^[A-Z][0-9]{4}$')
);


CREATE TABLE Pedido (
    Num_Pedido     INT AUTO_INCREMENT PRIMARY KEY,
    Fecha_Ped      DATE NOT NULL,
    Fecha_Ent      DATE NOT NULL,
    Precio_Total_Ped DECIMAL(10,2) NOT NULL CHECK (Precio_Total_Ped > 0),
    Nif_Cli        VARCHAR(15) NOT NULL,
    FOREIGN KEY (Nif_Cli) REFERENCES Cliente(Nif_Cli)
        ON DELETE CASCADE on update cascade,
    CHECK (Fecha_Ent > Fecha_Ped)
);


CREATE TABLE Contiene (
    Num_Pedido     INT,
    Cod_Pro        VARCHAR(5),
    Cantidad_Pro   INT NOT NULL CHECK (Cantidad_Pro > 0),
    Precio_Total   DECIMAL(10,2) NOT NULL CHECK (Precio_Total > 0),
    PRIMARY KEY (Num_Pedido, Cod_Pro),
    FOREIGN KEY (Num_Pedido) REFERENCES Pedido(Num_Pedido)
        ON DELETE CASCADE on update cascade,
    FOREIGN KEY (Cod_Pro) REFERENCES Producto(Cod_Pro)
        ON DELETE CASCADE on update cascade
);


INSERT INTO Cliente (Nif_Cli, Nombre, Apellido, Calle, Numero, Piso, Localidad, Provincia, telefono, Email) VALUES
('12345678A', 'Ana', 'García', 'C/ Mayor', 12, '2B', 'Bilbao', 'Bizkaia', '600123456', NULL), -- Solo teléfono
('87654321B', 'Luis', 'Martínez', 'Av. Libertad', 45, '1A', 'Donostia', 'Gipuzkoa', 'lmartinez@gmail.com', 'lmartinez@gmail.com'), -- Solo email
('11223344C', 'Marta', 'López', 'C/ Arana', 8, NULL, 'Vitoria', 'Álava', '699112233', NULL), -- Solo teléfono
('54129726P', 'Álvaro', 'Jauregui', 'Calle San Juan', 11, '4Izq', 'Barakaldo', 'Bizkaia', 'alvaro.jauregui@correo.es', 'alvaro.jauregui@correo.es'), -- Solo email
('52332160F', 'Anartz', 'Mamani', 'C/ Bajounpu Ente', 2, '2Der', 'Sopelana', 'Bizkaia', 'bujarrita43@gmail.com', 'bujarrita43@gmail.com'), -- Solo email
('16974082J', 'Inés', 'Fernandez', 'Av. General', 23, '7A', 'Bermeo', 'Bizkaia', '747465821', 'ines.fernandez@correo.es'), -- Teléfono + email
('56096353C', 'Begoña', 'Moncalvillo', 'C/ Trompetas', 10, '3C', 'Mondragón','Gipuzkoa', '654876231', NULL), -- Solo teléfono
('19163153J', 'Irati', 'Santobeña', 'Artea', 2, '4B', 'Bilbao', 'Bizakaia', 'iratipersonal@gmail.com', 'iratipersonal@gmail.com'), -- Solo email
('11730743F', 'Oier', 'Crispin', 'C/ Porcelana', 13, 'BA', 'Irun', 'Gipuzkoa', 'oier.crispin@correo.es', 'oier.crispin@correo.es'), -- Solo email
('17465505H', 'Mario', 'Lira', 'Av. Eguzkilore', 21, '5B', 'Amurrio', 'Álava', '638831521', NULL); -- Solo teléfono


INSERT INTO Proveedor (Nif_Prove, Nombre, Localidad, Telefono, Email) VALUES
('76607064K', 'Bebidas Norte', 'Bilbao', '944123456', NULL), -- Solo teléfono
('19961263T', 'Destilería Eibarresa', 'Eibar', NULL, 'contactanos@destileriaeibarresa.net'), -- Solo email
('85597644Q', 'Licores del Sur', 'Sevilla', '955112233', NULL), -- Solo teléfono
('48496113S', 'Forja Liquida', 'Malaga', '605351349', 'info@forjaliquida.es'), -- Ambos
('43970291H', 'La Cámara del Alquimista', 'Granada', NULL, 'contacto@alquimista.es'), -- Solo email
('64694667Z', 'Gran Reserva Aurea', 'Madrid', NULL, 'GRAcontacto@gmail.com'), -- Solo email
('55915694A', 'La Quinta Esencia', 'Alicante', '717109417', NULL), -- Solo teléfono
('36129978Z', 'Sierra Alquimia', 'Cordoba', NULL, 'sierralquimia@gmail.com'), -- Solo email
('49315429W', 'La Destilería Montealcohol', 'Bilbao', '727189541', 'info@montealcohol.es'); -- Ambos


INSERT INTO Producto (Cod_Pro, Nom_Pro, Precio_Pro, Stock, Tipo, Nif_Prove) VALUES
('D0101', 'Whisky Roble', 25.50, 120, 'Destiladas', '76607064K'),
('D0102', 'Vodka Puro', 18.90, 200, 'Destiladas', '19961263T'),
('D0103', 'Ron Dorado', 22.00, 150, 'Destiladas', '85597644Q'),
('D0104', 'Ginebra Azul', 30.00, 80, 'Destiladas', '48496113S'),
('D0105', 'Alambique Arcano', 23.50, 90, 'Destiladas', '36129978Z'),
('L0106', 'Elixir de Fuego', 48.00, 100, 'Licores', '36129978Z'),
('D0107', 'Espíritu Rebelde', 21.90, 40, 'Destiladas', '55915694A'),
('D0108', 'Sombra & Vapor', 19.60, 50, 'Destiladas', '48496113S'),
('F0109', 'Lobo de Malta', 18.00, 120, 'Fermentadas', '64694667Z'),
('D0110', 'Cuervo Dorado', 27.00, 200, 'Destiladas', '85597644Q'),
('D0111', 'Serpiente Blanca', 30.00, 180, 'Destiladas', '85597644Q'),
('D0112', 'Tequila Rosado', 32.90, 140, 'Destiladas', '19961263T'),
('E0113', 'Jerez 1912', 28.00, 100, 'Encabezadas', '43970291H'),
('L0114', 'Grand Marnier', 28.00, 70, 'Licores', '64694667Z'),
('L0115', 'Limoncello', 16.50, 190, 'Licores', '76607064K'),
('F0116', 'Sidra del Norte', 17.00, 150, 'Fermentadas','76607064K'),
('F0117', 'Sake de Mono', 25.50, 30, 'Fermentadas','43970291H'),
('D0118', 'Ginebra Roja', 30.00, 90, 'Destiladas', '48496113S'),
('N0119', 'Reserva del Fundador', 189.00, 700, 'Nuestra Selección','49315429W'),
('N0120', 'Aguardiente de Montaña', 64.00, 670, 'Nuestra Selección','49315429W'),
('N0121', 'Gran Reserva Centenario', 620.00, 400, 'Nuestra Selección','49315429W'),
('N0122', 'Coñac Solera Especial', 112.00, 790, 'Nuestra Selección','49315429W'),
('N0123', 'Malt Single Barrel', 245.00, 520, 'Nuestra Selección','49315429W'),
('N0124', 'Ginebra Botánica Sierra', 48.00, 810, 'Nuestra Selección','49315429W'),
('N0125', 'Ron de Caña Tostada', 78.00, 630, 'Nuestra Selección','49315429W'),
('N0126', 'Vodka Alpino Cristal', 42.00, 600, 'Nuestra Selección', '49315429W');

INSERT INTO Pedido (Fecha_Ped, Fecha_Ent, Precio_Total_Ped, Nif_Cli) VALUES
('2025-03-05', '2025-03-10', 58.50, '12345678A'),
('2025-03-12', '2025-03-17', 41.80, '11223344C'),
('2025-03-20', '2025-03-26', 75.00, '56096353C'),
('2025-04-02', '2025-04-07', 96.00, '87654321B'),
('2025-04-10', '2025-04-15', 134.50, '54129726P'),
('2025-04-18', '2025-04-23', 52.00, '17465505H'),
('2025-04-25', '2025-04-30', 189.00, '16974082J'),
('2025-05-03', '2025-05-09', 64.00, '52332160F'),
('2025-05-11', '2025-05-16', 112.00, '19163153J'),
('2025-05-19', '2025-05-25', 78.00, '11730743F'),

('2025-06-01', '2025-06-06', 145.50, '12345678A'),
('2025-06-08', '2025-06-14', 189.00, '87654321B'),
('2025-06-15', '2025-06-20', 245.00, '54129726P'),
('2025-06-22', '2025-06-27', 96.00, '11223344C'),
('2025-06-29', '2025-07-04', 58.50, '56096353C'),
('2025-07-05', '2025-07-10', 134.50, '16974082J'),
('2025-07-12', '2025-07-18', 78.00, '52332160F'),
('2025-07-19', '2025-07-25', 112.00, '19163153J'),
('2025-07-26', '2025-08-01', 48.00, '17465505H'),
('2025-08-02', '2025-08-07', 620.00, '11730743F'),

('2025-08-10', '2025-08-15', 30.00, '12345678A'),
('2025-08-17', '2025-08-22', 112.00, '87654321B'),
('2025-08-24', '2025-08-29', 48.00, '11223344C'),
('2025-09-01', '2025-09-06', 96.00, '56096353C'),
('2025-09-08', '2025-09-13', 245.00, '54129726P'),
('2025-09-15', '2025-09-20', 78.00, '52332160F'),
('2025-09-22', '2025-09-27', 112.00, '19163153J'),
('2025-09-29', '2025-10-04', 48.00, '17465505H'),
('2025-10-06', '2025-10-11', 620.00, '11730743F'),
('2025-10-13', '2025-10-18', 189.00, '16974082J'),

('2025-10-20', '2025-10-25', 58.50, '12345678A'),
('2025-10-27', '2025-11-01', 41.80, '11223344C'),
('2025-11-03', '2025-11-08', 96.00, '87654321B'),
('2025-11-10', '2025-11-15', 134.50, '54129726P'),
('2025-11-17', '2025-11-22', 52.00, '17465505H'),
('2025-11-24', '2025-11-29', 189.00, '16974082J'),
('2025-12-01', '2025-12-06', 64.00, '52332160F'),
('2025-12-08', '2025-12-13', 112.00, '19163153J'),
('2025-12-15', '2025-12-20', 78.00, '11730743F'),
('2025-12-22', '2025-12-27', 245.00, '56096353C'),

('2026-01-03', '2026-01-08', 30.00, '12345678A'),
('2026-01-10', '2026-01-15', 112.00, '87654321B'),
('2026-01-17', '2026-01-22', 48.00, '11223344C'),
('2026-01-24', '2026-01-29', 96.00, '56096353C'),
('2026-02-01', '2026-02-06', 245.00, '54129726P'),
('2026-02-08', '2026-02-13', 78.00, '52332160F'),
('2026-02-15', '2026-02-20', 112.00, '19163153J'),
('2026-02-22', '2026-02-27', 48.00, '17465505H'),
('2026-03-01', '2026-03-06', 620.00, '11730743F'),

('2026-03-08', '2026-03-13', 189.00, '16974082J'),
('2026-03-14', '2026-03-19', 58.50, '12345678A'),
('2026-03-16', '2026-03-21', 112.00, '87654321B'),
('2026-03-18', '2026-03-23', 48.00, '11223344C'),
('2026-03-20', '2026-03-25', 96.00, '56096353C'),
('2026-03-22', '2026-03-27', 245.00, '54129726P'),
('2026-03-24', '2026-03-29', 78.00, '52332160F'),
('2026-03-26', '2026-03-31', 112.00, '19163153J'),
('2026-03-28', '2026-04-02', 48.00, '17465505H'),
('2026-03-30', '2026-04-04', 620.00, '11730743F'),
('2026-04-01', '2026-04-06', 189.00, '16974082J'),

('2026-04-03', '2026-04-08', 58.50, '12345678A'),
('2026-04-05', '2026-04-10', 41.80, '11223344C'),
('2026-04-07', '2026-04-12', 96.00, '87654321B'),
('2026-04-09', '2026-04-14', 134.50, '54129726P'),
('2026-04-11', '2026-04-16', 52.00, '17465505H'),
('2026-04-13', '2026-04-18', 189.00, '16974082J'),
('2026-04-15', '2026-04-20', 64.00, '52332160F'),
('2026-04-17', '2026-04-22', 112.00, '19163153J'),
('2026-04-19', '2026-04-24', 78.00, '11730743F'),
('2026-04-21', '2026-04-26', 245.00, '56096353C'),

('2026-04-23', '2026-04-28', 30.00, '12345678A'),
('2026-04-25', '2026-04-30', 112.00, '87654321B'),
('2026-04-27', '2026-05-02', 48.00, '11223344C'),
('2026-04-29', '2026-05-04', 96.00, '56096353C'),
('2026-05-01', '2026-05-06', 245.00, '54129726P'),
('2026-05-03', '2026-05-08', 78.00, '52332160F'),
('2026-05-05', '2026-05-10', 112.00, '19163153J'),
('2026-05-07', '2026-05-12', 48.00, '17465505H'),
('2026-05-09', '2026-05-14', 620.00, '11730743F'),
('2026-05-11', '2026-05-16', 189.00, '16974082J'),

('2026-05-13', '2026-05-18', 58.50, '12345678A'),
('2026-05-15', '2026-05-20', 41.80, '11223344C'),
('2026-05-17', '2026-05-22', 96.00, '87654321B'),
('2026-05-19', '2026-05-24', 134.50, '54129726P'),
('2026-05-21', '2026-05-26', 52.00, '17465505H'),
('2026-05-23', '2026-05-28', 189.00, '16974082J'),
('2026-05-25', '2026-05-30', 64.00, '52332160F'),
('2026-05-27', '2026-06-01', 112.00, '19163153J'),
('2026-05-29', '2026-06-03', 78.00, '11730743F'),
('2026-05-31', '2026-06-05', 245.00, '56096353C');

-- Pedido 1 (58.50) - Ana (prefiere destiladas económicas)
INSERT INTO Contiene VALUES
(1, 'D0101', 2, 51.00),
(1, 'L0115', 1, 7.50);

-- Pedido 2 (41.80) - Marta (fermentadas y productos suaves)
INSERT INTO Contiene VALUES
(2, 'F0116', 2, 34.00),
(2, 'L0115', 1, 7.50);

-- Pedido 3 (75.00) - Begoña (económica)
INSERT INTO Contiene VALUES
(3, 'D0102', 2, 37.80),
(3, 'F0109', 2, 36.00),
(3, 'L0115', 1, 1.20); -- ajuste pequeño

-- Pedido 4 (96.00) - Luis (gourmet)
INSERT INTO Contiene VALUES
(4, 'L0106', 2, 96.00);

-- Pedido 5 (134.50) - Álvaro (variedad)
INSERT INTO Contiene VALUES
(5, 'D0104', 2, 60.00),
(5, 'D0103', 2, 44.00),
(5, 'D0105', 1, 23.50),
(5, 'L0115', 1, 7.00); -- ajuste

-- Pedido 6 (52.00) - Mario (clásico)
INSERT INTO Contiene VALUES
(6, 'D0103', 2, 44.00),
(6, 'L0115', 1, 8.00);

-- Pedido 7 (189.00) - Inés (premium)
INSERT INTO Contiene VALUES
(7, 'N0119', 1, 189.00);

-- Pedido 8 (64.00) - Anartz (licores)
INSERT INTO Contiene VALUES
(8, 'L0106', 1, 48.00),
(8, 'L0115', 1, 16.00);

-- Pedido 9 (112.00) - Irati (ginebras y licores)
INSERT INTO Contiene VALUES
(9, 'D0104', 2, 60.00),
(9, 'L0114', 1, 28.00),
(9, 'L0115', 1, 24.00); -- ajuste

-- Pedido 10 (78.00) - Oier (fermentadas y encabezadas)
INSERT INTO Contiene VALUES
(10, 'F0117', 2, 51.00),
(10, 'E0113', 1, 28.00);

-- Pedido 11 (145.50) - Ana (destiladas económicas)
INSERT INTO Contiene VALUES
(11, 'D0101', 3, 76.50),
(11, 'D0102', 2, 37.80),
(11, 'L0115', 2, 31.20);

-- Pedido 12 (189.00) - Luis (gourmet / selección)
INSERT INTO Contiene VALUES
(12, 'N0119', 1, 189.00);


-- Pedido 13 (245.00) - Álvaro (variedad)
INSERT INTO Contiene VALUES
(13, 'N0123', 1, 245.00);

-- Pedido 14 (96.00) - Marta (fermentadas)
INSERT INTO Contiene VALUES
(14, 'F0109', 3, 54.00),
(14, 'F0116', 2, 34.00),
(14, 'L0115', 1, 8.00);

-- Pedido 15 (58.50) - Begoña (económica)
INSERT INTO Contiene VALUES
(15, 'D0102', 2, 37.80),
(15, 'L0115', 2, 20.70);

-- Pedido 16 (134.50) - Inés (premium)
INSERT INTO Contiene VALUES
(16, 'N0124', 1, 48.00),
(16, 'D0112', 1, 32.90),
(16, 'D0104', 1, 30.00),
(16, 'L0115', 1, 23.60);

-- Pedido 17 (78.00) - Anartz (licores)
INSERT INTO Contiene VALUES
(17, 'L0106', 1, 48.00),
(17, 'L0114', 1, 28.00),
(17, 'L0115', 1, 2.00);

-- Pedido 18 (112.00) - Irati (ginebras y licores)
INSERT INTO Contiene VALUES
(18, 'D0104', 2, 60.00),
(18, 'D0118', 1, 30.00),
(18, 'L0115', 1, 22.00);

-- Pedido 19 (48.00) - Mario (clásico)
INSERT INTO Contiene VALUES
(19, 'D0103', 2, 44.00),
(19, 'L0115', 1, 4.00);

-- Pedido 20 (620.00) - Oier (fermentadas + selección ocasional)
INSERT INTO Contiene VALUES
(20, 'N0121', 1, 620.00);

-- Pedido 21 (30.00) - Ana (destiladas económicas)
INSERT INTO Contiene VALUES
(21, 'F0109', 1, 18.00),
(21, 'L0115', 1, 12.00);

-- Pedido 22 (112.00) - Luis (gourmet)
INSERT INTO Contiene VALUES
(22, 'N0122', 1, 112.00);


-- Pedido 23 (48.00) - Marta (fermentadas)
INSERT INTO Contiene VALUES
(23, 'F0116', 2, 34.00),
(23, 'L0115', 1, 14.00);

-- Pedido 24 (96.00) - Begoña (económica)
INSERT INTO Contiene VALUES
(24, 'D0102', 3, 56.70),
(24, 'F0109', 2, 36.00),
(24, 'L0115', 1, 3.30);

-- Pedido 25 (245.00) - Álvaro (variedad)
INSERT INTO Contiene VALUES
(25, 'N0123', 1, 245.00);

-- Pedido 26 (78.00) - Anartz (licores)
INSERT INTO Contiene VALUES
(26, 'L0106', 1, 48.00),
(26, 'L0114', 1, 28.00),
(26, 'L0115', 1, 2.00);

-- Pedido 27 (112.00) - Irati (ginebras y licores)
INSERT INTO Contiene VALUES
(27, 'D0104', 2, 60.00),
(27, 'D0118', 1, 30.00),
(27, 'L0115', 1, 22.00);

-- Pedido 28 (48.00) - Mario (clásico)
INSERT INTO Contiene VALUES
(28, 'D0103', 2, 44.00),
(28, 'L0115', 1, 4.00);

-- Pedido 29 (620.00) - Oier (fermentadas + selección ocasional)
INSERT INTO Contiene VALUES
(29, 'N0121', 1, 620.00);

-- Pedido 30 (189.00) - Inés (premium)
INSERT INTO Contiene VALUES
(30, 'N0119', 1, 189.00);
-- Pedido 31 (58.50) - Ana (destiladas económicas)
INSERT INTO Contiene VALUES
(31, 'D0101', 2, 51.00),
(31, 'L0115', 1, 7.50);

-- Pedido 32 (41.80) - Marta (fermentadas)
INSERT INTO Contiene VALUES
(32, 'F0116', 2, 34.00),
(32, 'L0115', 1, 7.80);

-- Pedido 33 (96.00) - Luis (gourmet)
INSERT INTO Contiene VALUES
(33, 'L0106', 2, 96.00);

-- Pedido 34 (134.50) - Álvaro (variedad)
INSERT INTO Contiene VALUES
(34, 'N0125', 1, 78.00),
(34, 'D0112', 1, 32.90),
(34, 'L0115', 1, 23.60);


-- Pedido 35 (52.00) - Mario (clásico)
INSERT INTO Contiene VALUES
(35, 'D0103', 2, 44.00),
(35, 'L0115', 1, 8.00);

-- Pedido 36 (189.00) - Inés (premium)
INSERT INTO Contiene VALUES
(36, 'N0119', 1, 189.00);

-- Pedido 37 (64.00) - Anartz (licores)
INSERT INTO Contiene VALUES
(37, 'L0106', 1, 48.00),
(37, 'L0115', 1, 16.00);

-- Pedido 38 (112.00) - Irati (ginebras y licores)
INSERT INTO Contiene VALUES
(38, 'D0104', 2, 60.00),
(38, 'L0114', 1, 28.00),
(38, 'L0115', 1, 24.00);

-- Pedido 39 (78.00) - Oier (fermentadas y encabezadas)
INSERT INTO Contiene VALUES
(39, 'F0117', 2, 51.00),
(39, 'E0113', 1, 28.00);

-- Pedido 40 (245.00) - Begoña (económica pero pedido grande)
INSERT INTO Contiene VALUES
(40, 'N0123', 1, 245.00);
-- Pedido 41 (30.00) - Ana (destiladas económicas)
INSERT INTO Contiene VALUES
(41, 'F0109', 1, 18.00),
(41, 'L0115', 1, 12.00);

-- Pedido 42 (112.00) - Luis (gourmet)
INSERT INTO Contiene VALUES
(42, 'N0122', 1, 112.00);

-- Pedido 43 (48.00) - Marta (fermentadas)
INSERT INTO Contiene VALUES
(43, 'F0116', 2, 34.00),
(43, 'L0115', 1, 14.00);

-- Pedido 44 (96.00) - Begoña (económica)
INSERT INTO Contiene VALUES
(44, 'D0102', 3, 56.70),
(44, 'F0109', 2, 36.00),
(44, 'L0115', 1, 3.30);

-- Pedido 45 (245.00) - Álvaro (variedad)
INSERT INTO Contiene VALUES
(45, 'N0123', 1, 245.00);

-- Pedido 46 (78.00) - Anartz (licores)
INSERT INTO Contiene VALUES
(46, 'L0106', 1, 48.00),
(46, 'L0114', 1, 28.00),
(46, 'L0115', 1, 2.00);

-- Pedido 47 (112.00) - Irati (ginebras y licores)
INSERT INTO Contiene VALUES
(47, 'D0104', 2, 60.00),
(47, 'D0118', 1, 30.00),
(47, 'L0115', 1, 22.00);

-- Pedido 48 (48.00) - Mario (clásico)
INSERT INTO Contiene VALUES
(48, 'D0103', 2, 44.00),
(48, 'L0115', 1, 4.00);

-- Pedido 49 (620.00) - Oier (fermentadas + selección ocasional)
INSERT INTO Contiene VALUES
(49, 'N0121', 1, 620.00);

-- Pedido 50 (189.00) - Inés (premium)
INSERT INTO Contiene VALUES
(50, 'N0119', 1, 189.00);
-- Pedido 51 (58.50) - Ana (destiladas económicas)
INSERT INTO Contiene VALUES
(51, 'D0101', 2, 51.00),
(51, 'L0115', 1, 7.50);

INSERT INTO Contiene VALUES
(52, 'N0122', 1, 112.00);


-- Pedido 53 (48.00) - Marta (fermentadas)
INSERT INTO Contiene VALUES
(53, 'F0116', 2, 34.00),
(53, 'L0115', 1, 14.00);

-- Pedido 54 (96.00) - Begoña (económica)
INSERT INTO Contiene VALUES
(54, 'D0102', 3, 56.70),
(54, 'F0109', 2, 36.00),
(54, 'L0115', 1, 3.30);

-- Pedido 55 (245.00) - Álvaro (variedad)
INSERT INTO Contiene VALUES
(55, 'N0123', 1, 245.00);

-- Pedido 56 (78.00) - Anartz (licores)
INSERT INTO Contiene VALUES
(56, 'L0106', 1, 48.00),
(56, 'L0114', 1, 28.00),
(56, 'L0115', 1, 2.00);

-- Pedido 57 (112.00) - Irati (ginebras y licores)
INSERT INTO Contiene VALUES
(57, 'D0104', 2, 60.00),
(57, 'D0118', 1, 30.00),
(57, 'L0115', 1, 22.00);

-- Pedido 58 (48.00) - Mario (clásico)
INSERT INTO Contiene VALUES
(58, 'D0103', 2, 44.00),
(58, 'L0115', 1, 4.00);

-- Pedido 59 (620.00) - Oier (fermentadas + selección ocasional)
INSERT INTO Contiene VALUES
(59, 'N0121', 1, 620.00);

-- Pedido 60 (189.00) - Inés (premium)
INSERT INTO Contiene VALUES
(60, 'N0119', 1, 189.00);
-- Pedido 61 (58.50) - Ana (destiladas económicas)
INSERT INTO Contiene VALUES
(61, 'D0101', 2, 51.00),
(61, 'L0115', 1, 7.50);

-- Pedido 62 (41.80) - Marta (fermentadas)
INSERT INTO Contiene VALUES
(62, 'F0116', 2, 34.00),
(62, 'L0115', 1, 7.80);

-- Pedido 63 (96.00) - Luis (gourmet)
INSERT INTO Contiene VALUES
(63, 'L0106', 2, 96.00);

-- Pedido 64 (134.50) - Álvaro (variedad)
INSERT INTO Contiene VALUES
(64, 'N0125', 1, 78.00),
(64, 'D0112', 1, 32.90),
(64, 'L0115', 1, 23.60);

-- Pedido 65 (52.00) - Mario (clásico)
INSERT INTO Contiene VALUES
(65, 'D0103', 2, 44.00),
(65, 'L0115', 1, 8.00);

-- Pedido 66 (189.00) - Inés (premium)
INSERT INTO Contiene VALUES
(66, 'N0119', 1, 189.00);

-- Pedido 67 (64.00) - Anartz (licores)
INSERT INTO Contiene VALUES
(67, 'L0106', 1, 48.00),
(67, 'L0115', 1, 16.00);

-- Pedido 68 (112.00) - Irati (ginebras y licores)
INSERT INTO Contiene VALUES
(68, 'D0104', 2, 60.00),
(68, 'L0114', 1, 28.00),
(68, 'L0115', 1, 24.00);

-- Pedido 69 (78.00) - Oier (fermentadas y encabezadas)
INSERT INTO Contiene VALUES
(69, 'F0117', 2, 51.00),
(69, 'E0113', 1, 28.00);

-- Pedido 70 (245.00) - Begoña (económica pero pedido grande)
INSERT INTO Contiene VALUES
(70, 'N0123', 1, 245.00);
-- Pedido 71 (30.00) - Ana (destiladas económicas)
INSERT INTO Contiene VALUES
(71, 'F0109', 1, 18.00),
(71, 'L0115', 1, 12.00);

-- Pedido 72 (112.00) - Luis (gourmet)
INSERT INTO Contiene VALUES
(72, 'N0122', 1, 112.00);


-- Pedido 73 (48.00) - Marta (fermentadas)
INSERT INTO Contiene VALUES
(73, 'F0116', 2, 34.00),
(73, 'L0115', 1, 14.00);

-- Pedido 74 (96.00) - Begoña (económica)
INSERT INTO Contiene VALUES
(74, 'D0102', 3, 56.70),
(74, 'F0109', 2, 36.00),
(74, 'L0115', 1, 3.30);

-- Pedido 75 (245.00) - Álvaro (variedad)
INSERT INTO Contiene VALUES
(75, 'N0123', 1, 245.00);

-- Pedido 76 (78.00) - Anartz (licores)
INSERT INTO Contiene VALUES
(76, 'L0106', 1, 48.00),
(76, 'L0114', 1, 28.00),
(76, 'L0115', 1, 2.00);

-- Pedido 77 (112.00) - Irati (ginebras y licores)
INSERT INTO Contiene VALUES
(77, 'D0104', 2, 60.00),
(77, 'D0118', 1, 30.00),
(77, 'L0115', 1, 22.00);

-- Pedido 78 (48.00) - Mario (clásico)
INSERT INTO Contiene VALUES
(78, 'D0103', 2, 44.00),
(78, 'L0115', 1, 4.00);

-- Pedido 79 (620.00) - Oier (fermentadas + selección ocasional)
INSERT INTO Contiene VALUES
(79, 'N0121', 1, 620.00);

-- Pedido 80 (189.00) - Inés (premium)
INSERT INTO Contiene VALUES
(80, 'N0119', 1, 189.00);

-- Pedido 81 (58.50) - Ana (destiladas económicas)
INSERT INTO Contiene VALUES
(81, 'D0101', 2, 51.00),
(81, 'L0115', 1, 7.50);

-- Pedido 82 (41.80) - Marta (fermentadas)
INSERT INTO Contiene VALUES
(82, 'F0116', 2, 34.00),
(82, 'L0115', 1, 7.80);

-- Pedido 83 (96.00) - Luis (gourmet)
INSERT INTO Contiene VALUES
(83, 'L0106', 2, 96.00);

-- Pedido 84 (134.50) - Álvaro (variedad)
INSERT INTO Contiene VALUES
(84, 'N0125', 1, 78.00),
(84, 'D0112', 1, 32.90),
(84, 'L0115', 1, 23.60);

-- Pedido 85 (52.00) - Mario (clásico)
INSERT INTO Contiene VALUES
(85, 'D0103', 2, 44.00),
(85, 'L0115', 1, 8.00);

-- Pedido 86 (189.00) - Inés (premium)
INSERT INTO Contiene VALUES
(86, 'N0119', 1, 189.00);

-- Pedido 87 (64.00) - Anartz (licores)
INSERT INTO Contiene VALUES
(87, 'L0106', 1, 48.00),
(87, 'L0115', 1, 16.00);

-- Pedido 88 (112.00) - Irati (ginebras y licores)
INSERT INTO Contiene VALUES
(88, 'D0104', 2, 60.00),
(88, 'L0114', 1, 28.00),
(88, 'L0115', 1, 24.00);

-- Pedido 89 (78.00) - Oier (fermentadas y encabezadas)
INSERT INTO Contiene VALUES
(89, 'F0117', 2, 51.00),
(89, 'E0113', 1, 28.00);

-- Pedido 90 (245.00) - Begoña (económica pero pedido grande)
INSERT INTO Contiene VALUES
(90, 'N0123', 1, 245.00);


– —--------------- –
-- FUNCIONES –
– —--------------- –

– 1. STOCK SUFICIENTE –

DELIMITER //

CREATE FUNCTION STOCK_SUFICIENTE(P_COD VARCHAR(10), P_CANT INT)
RETURNS TINYINT
DETERMINISTIC
BEGIN
    DECLARE V_STOCK INT DEFAULT 0;

    SELECT Stock INTO V_STOCK
    FROM Producto
    WHERE Cod_Pro = P_COD;

    RETURN (V_STOCK >= P_CANT);
END//

DELIMITER ;



-- 2.  VALIDAR NIF –

DELIMITER //

CREATE FUNCTION VALIDAR_NIF(P_NIF VARCHAR(15))
RETURNS TINYINT
DETERMINISTIC
BEGIN
    DECLARE V_NUM INT;
    DECLARE V_LETRA CHAR(1);
    DECLARE V_TABLA_LETRAS CHAR(23) DEFAULT 'TRWAGMYFPDXBNJZSQVHLCKE';
    DECLARE V_LETRA_CORRECTA CHAR(1);

    IF P_NIF IS NULL OR P_NIF = '' THEN
        RETURN 0;
    END IF;

    IF LENGTH(P_NIF) <> 9 THEN
        RETURN 0;
    END IF;

    IF SUBSTRING(P_NIF, 1, 8) NOT REGEXP '^[0-9]{8}$' THEN
        RETURN 0;
    END IF;

    SET V_NUM = CAST(SUBSTRING(P_NIF, 1, 8) AS UNSIGNED);
    SET V_LETRA = UPPER(SUBSTRING(P_NIF, 9, 1));
    SET V_LETRA_CORRECTA = SUBSTRING(V_TABLA_LETRAS, (V_NUM % 23) + 1, 1);

    RETURN (V_LETRA = V_LETRA_CORRECTA);
END//

DELIMITER ;




-- 3. VALIDAR CODIGO DE PRODUCTO –

DELIMITER //

CREATE FUNCTION VALIDAR_COD_PRO(P_COD VARCHAR(5))
RETURNS TINYINT
DETERMINISTIC
BEGIN
    IF P_COD IS NULL OR LENGTH(P_COD) <> 5 THEN
        RETURN 0;
    END IF;

    IF P_COD REGEXP '^[A-Z][0-9]{4}$' THEN
        RETURN 1;
    ELSE
        RETURN 0;
    END IF;
END//

DELIMITER ;


-- 4.  TOTAL PEDIDOS CLIENTE –

DELIMITER //

CREATE FUNCTION TOTAL_PEDIDOS_CLIENTE(P_NIF VARCHAR(15))
RETURNS DECIMAL(10,2)
DETERMINISTIC
BEGIN
    DECLARE V_TOTAL DECIMAL(10,2) DEFAULT 0;

    SELECT IFNULL(SUM(C.Cantidad_Pro * PR.Precio_Pro), 0)
    INTO V_TOTAL
    FROM Pedido P
    JOIN Contiene C ON P.Num_Pedido = C.Num_Pedido
    JOIN Producto PR ON C.Cod_Pro = PR.Cod_Pro
    WHERE P.Nif_Cli = P_NIF;

    RETURN V_TOTAL;
END//

DELIMITER ;



-- 5.  PRODUCTO MAS COMPRADO –

DELIMITER //

CREATE FUNCTION PRODUCTO_MAS_COMPRADO(P_NIF VARCHAR(15))
RETURNS VARCHAR(50)
DETERMINISTIC
BEGIN
    DECLARE V_PRODUCTO VARCHAR(50) DEFAULT '';

    SELECT PR.Nom_Pro
    INTO V_PRODUCTO
    FROM Pedido P
    JOIN Contiene C ON P.Num_Pedido = C.Num_Pedido
    JOIN Producto PR ON C.Cod_Pro = PR.Cod_Pro
    WHERE P.Nif_Cli = P_NIF
    GROUP BY PR.Nom_Pro
    ORDER BY SUM(C.Cantidad_Pro) DESC
    LIMIT 1;

    RETURN V_PRODUCTO;
END//

DELIMITER ;



-- 6. TOTAL VENDIDO DE UN PRODUCTO –

DELIMITER //

CREATE FUNCTION TOTAL_VENDIDO_PRODUCTO(P_COD VARCHAR(5))
RETURNS INT
DETERMINISTIC
BEGIN
    DECLARE V_UNIDADES INT DEFAULT 0;

    SELECT IFNULL(SUM(Cantidad_Pro), 0)
    INTO V_UNIDADES
    FROM Contiene
    WHERE Cod_Pro = P_COD;

    RETURN V_UNIDADES;
END//

DELIMITER ;



-- 7.  PRODUCTO MAS VENDIDO DE UN PROVEEDOR –

DELIMITER //

CREATE FUNCTION PRODUCTO_MAS_VENDIDO_PROVEEDOR(P_NIF VARCHAR(15))
RETURNS VARCHAR(50)
DETERMINISTIC
BEGIN
    DECLARE V_PRODUCTO VARCHAR(50) DEFAULT '';

    SELECT PR.Nom_Pro
    INTO V_PRODUCTO
    FROM Producto PR
    LEFT JOIN Contiene C ON PR.Cod_Pro = C.Cod_Pro
    WHERE PR.Nif_Prove = P_NIF
    GROUP BY PR.Nom_Pro
    ORDER BY SUM(C.Cantidad_Pro) DESC
    LIMIT 1;

    RETURN V_PRODUCTO;
END//

DELIMITER ;

– 8. PRECIO DEL PRODUCTO –

DELIMITER //

CREATE FUNCTION PRECIO_PRODUCTO(P_COD VARCHAR(10))
RETURNS DECIMAL(10,2)
DETERMINISTIC
BEGIN
    DECLARE V_PRECIO DECIMAL(10,2) DEFAULT 0;

    SELECT Precio_Pro INTO V_PRECIO
    FROM Producto
    WHERE Cod_Pro = P_COD;

    RETURN V_PRECIO;
END//

DELIMITER ;

– 9. TOTAL DEL PEDIDO –

DELIMITER //

CREATE FUNCTION TOTAL_PEDIDO(P_NUMPED INT)
RETURNS DECIMAL(10,2)
DETERMINISTIC
BEGIN
    DECLARE V_TOTAL DECIMAL(10,2) DEFAULT 0;

    SELECT IFNULL(SUM(Precio_Total), 0)
    INTO V_TOTAL
    FROM Contiene
    WHERE Num_Pedido = P_NUMPED;

    RETURN V_TOTAL;
END//

DELIMITER ;



– —------------------------- –
-- PROCEDIMIENTOS –
– —------------------------- –

-- 1. CREAR CLIENTE – 

DELIMITER //

CREATE PROCEDURE CREAR_CLIENTE(
    IN P_NIF VARCHAR(15),
    IN P_NOMBRE VARCHAR(50),
    IN P_APELLIDO VARCHAR(50),
    IN P_CALLE VARCHAR(100),
    IN P_NUMERO INT,
    IN P_LOCALIDAD VARCHAR(50),
    IN P_PROVINCIA VARCHAR(50),
    IN P_TELEFONO VARCHAR(50)
)
BEGIN
    INSERT INTO Cliente (Nif_Cli, Nombre, Apellido, Calle, Numero, Localidad, Provincia, Telefono)
    VALUES (P_NIF, P_NOMBRE, P_APELLIDO, P_CALLE, P_NUMERO, P_LOCALIDAD, P_PROVINCIA, P_TELEFONO);
END//

DELIMITER ;



– 2.  MODIFICAR LOS DATOS DE USUARIO –

DELIMITER //

CREATE PROCEDURE MODIFICAR_DATOS_USUARIO(
    IN P_NIF VARCHAR(15),
    IN P_NOMBRE VARCHAR(50),
    IN P_APELLIDO VARCHAR(50),
    IN P_CALLE VARCHAR(100),
    IN P_NUMERO INT,
    IN P_PISO VARCHAR(10),
    IN P_LOCALIDAD VARCHAR(50),
    IN P_PROVINCIA VARCHAR(50),
    IN P_TELEFONO VARCHAR(50),
    IN P_EMAIL VARCHAR(100)
)
MODIFICAR_DATOS_USUARIO: BEGIN
    DECLARE V_EXISTE INT DEFAULT 0;
    DECLARE V_ERROR INT DEFAULT 0;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        SET V_ERROR = 1;
        ROLLBACK;
    END;

    SELECT COUNT(*) INTO V_EXISTE
    FROM Cliente
    WHERE Nif_Cli = P_NIF;

    IF V_EXISTE = 0 THEN
        LEAVE MODIFICAR_DATOS_USUARIO;
    END IF;

    START TRANSACTION;

    IF P_NOMBRE IS NOT NULL AND P_NOMBRE <> '' THEN
        IF VALIDAR_TEXTO(P_NOMBRE) = 0 THEN ROLLBACK; LEAVE MODIFICAR_DATOS_USUARIO; END IF;
    END IF;

    IF P_APELLIDO IS NOT NULL AND P_APELLIDO <> '' THEN
        IF VALIDAR_TEXTO(P_APELLIDO) = 0 THEN ROLLBACK; LEAVE MODIFICAR_DATOS_USUARIO; END IF;
    END IF;

    IF P_CALLE IS NOT NULL AND P_CALLE <> '' THEN
        IF CAMPO_NOT_NULL_VALIDO(P_CALLE) = 0 THEN ROLLBACK; LEAVE MODIFICAR_DATOS_USUARIO; END IF;
    END IF;

    IF P_NUMERO IS NOT NULL THEN
        IF VALIDAR_NUMERO(P_NUMERO) = 0 THEN ROLLBACK; LEAVE MODIFICAR_DATOS_USUARIO; END IF;
    END IF;

    IF P_LOCALIDAD IS NOT NULL AND P_LOCALIDAD <> '' THEN
        IF VALIDAR_TEXTO(P_LOCALIDAD) = 0 THEN ROLLBACK; LEAVE MODIFICAR_DATOS_USUARIO; END IF;
    END IF;

    IF P_PROVINCIA IS NOT NULL AND P_PROVINCIA <> '' THEN
        IF VALIDAR_TEXTO(P_PROVINCIA) = 0 THEN ROLLBACK; LEAVE MODIFICAR_DATOS_USUARIO; END IF;
    END IF;

    IF P_TELEFONO IS NOT NULL AND P_TELEFONO <> '' THEN
        IF VALIDAR_TELEFONO(P_TELEFONO) = 0 THEN ROLLBACK; LEAVE MODIFICAR_DATOS_USUARIO; END IF;
    END IF;

    IF P_EMAIL IS NOT NULL AND P_EMAIL <> '' THEN
        IF VALIDAR_EMAIL(P_EMAIL) = 0 THEN ROLLBACK; LEAVE MODIFICAR_DATOS_USUARIO; END IF;
    END IF;

    UPDATE Cliente
    SET
        Nombre    = COALESCE(NULLIF(P_NOMBRE,''), Nombre),
        Apellido  = COALESCE(NULLIF(P_APELLIDO,''), Apellido),
        Calle     = COALESCE(NULLIF(P_CALLE,''), Calle),
        Numero    = COALESCE(P_NUMERO, Numero),
        Piso      = COALESCE(NULLIF(P_PISO,''), Piso),
        Localidad = COALESCE(NULLIF(P_LOCALIDAD,''), Localidad),
        Provincia = COALESCE(NULLIF(P_PROVINCIA,''), Provincia),
        Telefono  = COALESCE(NULLIF(P_TELEFONO,''), Telefono),
        Email     = COALESCE(NULLIF(P_EMAIL,''), Email)
    WHERE Nif_Cli = P_NIF;

    IF V_ERROR = 0 THEN
        COMMIT;
    END IF;

END//

DELIMITER ;



-- 4. CREAR UN PRODUCTO –

DELIMITER //

CREATE PROCEDURE CREAR_PRODUCTO(
    IN P_COD VARCHAR(5),
    IN P_NOMBRE VARCHAR(50),
    IN P_PRECIO DECIMAL(10,2),
    IN P_STOCK INT,
    IN P_TIPO VARCHAR(30),
    IN P_NIF_PROVE VARCHAR(15)
)
CREAR_PRODUCTO: BEGIN
    DECLARE V_EXISTE INT DEFAULT 0;
    DECLARE V_PROVE INT DEFAULT 0;
    DECLARE V_ERROR INT DEFAULT 0;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        SET V_ERROR = 1;
        ROLLBACK;
    END;

    IF VALIDAR_COD_PRO(P_COD) = 0 THEN LEAVE CREAR_PRODUCTO; END IF;

    SELECT COUNT(*) INTO V_EXISTE FROM Producto WHERE Cod_Pro = P_COD;
    IF V_EXISTE > 0 THEN LEAVE CREAR_PRODUCTO; END IF;

    SELECT COUNT(*) INTO V_PROVE FROM Proveedor WHERE Nif_Prove = P_NIF_PROVE;
    IF V_PROVE = 0 THEN LEAVE CREAR_PRODUCTO; END IF;

    IF CAMPO_NOT_NULL_VALIDO(P_NOMBRE) = 0 OR VALIDAR_TEXTO(P_NOMBRE) = 0 THEN LEAVE CREAR_PRODUCTO; END IF;
    IF P_PRECIO IS NULL OR P_PRECIO <= 0 THEN LEAVE CREAR_PRODUCTO; END IF;
    IF P_STOCK IS NULL OR P_STOCK < 0 THEN LEAVE CREAR_PRODUCTO; END IF;

    IF P_TIPO NOT IN ('Fermentadas','Destiladas','Encabezadas','Licores','Nuestra Selección') THEN
        LEAVE CREAR_PRODUCTO;
    END IF;

    START TRANSACTION;

    INSERT INTO Producto (Cod_Pro, Nom_Pro, Precio_Pro, Stock, Tipo, Nif_Prove)
    VALUES (P_COD, P_NOMBRE, P_PRECIO, P_STOCK, P_TIPO, P_NIF_PROVE);

    IF V_ERROR = 0 THEN
        COMMIT;
    END IF;

END//

DELIMITER ;


– 5.  CREAR UN PROVEEDOR –

DELIMITER //

CREATE PROCEDURE CREAR_PROVEEDOR(
    IN P_NIF VARCHAR(15),
    IN P_NOMBRE VARCHAR(50),
    IN P_LOCALIDAD VARCHAR(50),
    IN P_TELEFONO VARCHAR(20),
    IN P_EMAIL VARCHAR(100)
)
CREAR_PROVEEDOR: BEGIN
    DECLARE V_EXISTE INT DEFAULT 0;
    DECLARE V_ERROR INT DEFAULT 0;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        SET V_ERROR = 1;
        ROLLBACK;
    END;

    IF VALIDAR_NIF(P_NIF) = 0 THEN LEAVE CREAR_PROVEEDOR; END IF;

    SELECT COUNT(*) INTO V_EXISTE FROM Proveedor WHERE Nif_Prove = P_NIF;
    IF V_EXISTE > 0 THEN LEAVE CREAR_PROVEEDOR; END IF;

    IF CAMPO_NOT_NULL_VALIDO(P_NOMBRE) = 0 OR VALIDAR_TEXTO(P_NOMBRE) = 0 THEN LEAVE CREAR_PROVEEDOR; END IF;
    IF CAMPO_NOT_NULL_VALIDO(P_LOCALIDAD) = 0 OR VALIDAR_TEXTO(P_LOCALIDAD) = 0 THEN LEAVE CREAR_PROVEEDOR; END IF;

    IF P_TELEFONO IS NOT NULL AND P_TELEFONO <> '' THEN
        IF VALIDAR_TELEFONO(P_TELEFONO) = 0 THEN LEAVE CREAR_PROVEEDOR; END IF;
    END IF;

    IF P_EMAIL IS NOT NULL AND P_EMAIL <> '' THEN
        IF VALIDAR_EMAIL(P_EMAIL) = 0 THEN LEAVE CREAR_PROVEEDOR; END IF;
    END IF;

    START TRANSACTION;

    INSERT INTO Proveedor (Nif_Prove, Nombre, Localidad, Telefono, Email)
    VALUES (P_NIF, P_NOMBRE, P_LOCALIDAD, P_TELEFONO, P_EMAIL);

    IF V_ERROR = 0 THEN
        COMMIT;
    END IF;

END//

DELIMITER ;



– 6.  MODIFICAR UN PROVEEDOR –

DELIMITER //

CREATE PROCEDURE MODIFICAR_PROVEEDOR(
    IN P_NIF VARCHAR(15),
    IN P_NOMBRE VARCHAR(50),
    IN P_LOCALIDAD VARCHAR(50),
    IN P_TELEFONO VARCHAR(20),
    IN P_EMAIL VARCHAR(100)
)
MODIFICAR_PROVEEDOR: BEGIN
    DECLARE V_EXISTE INT DEFAULT 0;
    DECLARE V_ERROR INT DEFAULT 0;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        SET V_ERROR = 1;
        ROLLBACK;
    END;

    IF VALIDAR_NIF(P_NIF) = 0 THEN LEAVE MODIFICAR_PROVEEDOR; END IF;

    SELECT COUNT(*) INTO V_EXISTE FROM Proveedor WHERE Nif_Prove = P_NIF;
    IF V_EXISTE = 0 THEN LEAVE MODIFICAR_PROVEEDOR; END IF;

    IF P_NOMBRE IS NOT NULL AND P_NOMBRE <> '' THEN
        IF VALIDAR_TEXTO(P_NOMBRE) = 0 THEN LEAVE MODIFICAR_PROVEEDOR; END IF;
    END IF;

    IF P_LOCALIDAD IS NOT NULL AND P_LOCALIDAD <> '' THEN
        IF VALIDAR_TEXTO(P_LOCALIDAD) = 0 THEN LEAVE MODIFICAR_PROVEEDOR; END IF;
    END IF;

    IF P_TELEFONO IS NOT NULL AND P_TELEFONO <> '' THEN
        IF VALIDAR_TELEFONO(P_TELEFONO) = 0 THEN LEAVE MODIFICAR_PROVEEDOR; END IF;
    END IF;

    IF P_EMAIL IS NOT NULL AND P_EMAIL <> '' THEN
        IF VALIDAR_EMAIL(P_EMAIL) = 0 THEN LEAVE MODIFICAR_PROVEEDOR; END IF;
    END IF;

    START TRANSACTION;

    UPDATE Proveedor
    SET
        Nombre    = COALESCE(NULLIF(P_NOMBRE,''), Nombre),
        Localidad = COALESCE(NULLIF(P_LOCALIDAD,''), Localidad),
        Telefono  = COALESCE(NULLIF(P_TELEFONO,''), Telefono),
        Email     = COALESCE(NULLIF(P_EMAIL,''), Email)
    WHERE Nif_Prove = P_NIF;

    IF V_ERROR = 0 THEN
        COMMIT;
    END IF;

END//

DELIMITER ;



– 7. INSERTAR LINEA EN EL PEDIDO (AUXILIAR) –

DELIMITER //

CREATE PROCEDURE INSERTAR_LINEA_PEDIDO(
    IN P_NUMPED INT,
    IN P_COD VARCHAR(10),
    IN P_CANT INT
)
BEGIN
    INSERT INTO Contiene (Num_Pedido, Cod_Pro, Cantidad_Pro, Precio_Total)
    VALUES (P_NUMPED, P_COD, P_CANT, P_CANT * PRECIO_PRODUCTO(P_COD));
END//

DELIMITER ;




– 8.  DESCUENTO POR VOLUMEN –

DELIMITER //

CREATE PROCEDURE DESCUENTOPORVOLUMEN(
    IN P_NUMPED INT
)
BEGIN
    DECLARE V_CODPRO VARCHAR(10);
    DECLARE V_CANT INT;
    DECLARE V_PRECIOUNIT DECIMAL(10,2);
    DECLARE V_DESCUENTO INT;
    DECLARE V_PRECIO_FINAL DECIMAL(10,2);
    DECLARE V_FIN INT DEFAULT 0;

    DECLARE C_LINEAS CURSOR FOR
        SELECT Cod_Pro, Cantidad_Pro, (Precio_Total / Cantidad_Pro)
        FROM Contiene
        WHERE Num_Pedido = P_NUMPED;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET V_FIN = 1;

    OPEN C_LINEAS;

    BUCLE_LINEAS: LOOP
        FETCH C_LINEAS INTO V_CODPRO, V_CANT, V_PRECIOUNIT;
        IF V_FIN = 1 THEN LEAVE BUCLE_LINEAS; END IF;

        SET V_DESCUENTO = FLOOR(V_CANT / 6) * 10;
        IF V_DESCUENTO > 50 THEN SET V_DESCUENTO = 50; END IF;

        SET V_PRECIO_FINAL = (V_PRECIOUNIT * V_CANT) * (1 - (V_DESCUENTO / 100));

        UPDATE Contiene
        SET Precio_Total = V_PRECIO_FINAL
        WHERE Num_Pedido = P_NUMPED AND Cod_Pro = V_CODPRO;
    END LOOP;

    CLOSE C_LINEAS;
END//

DELIMITER ;




– 9.  DEVOLVER STOCK (AUXILIAR)  –

DELIMITER //

CREATE PROCEDURE DEVOLVER_STOCK(
    IN P_COD VARCHAR(10),
    IN P_CANT INT
)
BEGIN
    UPDATE Producto
    SET Stock = Stock + P_CANT
    WHERE Cod_Pro = P_COD;
END//

DELIMITER ;


-- 10. HISTORICO DE PEDIDOS DE PRODUCTOS –

DELIMITER //

CREATE PROCEDURE HISTORICO_PEDIDOS_PRODUCTOS()
BEGIN
    DECLARE V_MAS VARCHAR(100);
    DECLARE V_MENOS VARCHAR(100);

    SELECT PR.Nom_Pro, IFNULL(SUM(C.Cantidad_Pro), 0) AS Unidades_Vendidas
    FROM Producto PR
    LEFT JOIN Contiene C ON PR.Cod_Pro = C.Cod_Pro
    GROUP BY PR.Nom_Pro
    ORDER BY Unidades_Vendidas DESC;

    SELECT Nom_Pro INTO V_MAS
    FROM (
        SELECT PR.Nom_Pro, SUM(C.Cantidad_Pro) AS Total
        FROM Producto PR
        LEFT JOIN Contiene C ON PR.Cod_Pro = C.Cod_Pro
        GROUP BY PR.Nom_Pro
        ORDER BY Total DESC
        LIMIT 1
    ) AS T;

    SELECT Nom_Pro INTO V_MENOS
    FROM (
        SELECT PR.Nom_Pro, IFNULL(SUM(C.Cantidad_Pro), 0) AS Total
        FROM Producto PR
        LEFT JOIN Contiene C ON PR.Cod_Pro = C.Cod_Pro
        GROUP BY PR.Nom_Pro
        ORDER BY Total ASC
        LIMIT 1
    ) AS T;

    SELECT
        CONCAT('MAS VENDIDO: ', V_MAS) AS Resumen,
        CONCAT('MENOS VENDIDO: ', V_MENOS) AS Resumen2;
END//

DELIMITER ;

-- 11. CREAR PORDUCTO CON STOCK INICIAL --

DELIMITER //

CREATE PROCEDURE CREAR_PRODUCTO_CON_STOCK_INICIAL(
    IN P_COD VARCHAR(5),
    IN P_NOMBRE VARCHAR(50),
    IN P_PRECIO DECIMAL(10,2),
    IN P_TIPO VARCHAR(30),
    IN P_NIF_PROVE VARCHAR(15)
)
CREAR_PRODUCTO_CON_STOCK_INICIAL: BEGIN
    DECLARE V_EXISTE INT DEFAULT 0;
    DECLARE V_PROVE INT DEFAULT 0;
    DECLARE V_STOCK_TOTAL INT DEFAULT 0;
    DECLARE V_CANTIDAD INT;
    DECLARE V_FIN INT DEFAULT 0;
    DECLARE V_ERROR INT DEFAULT 0;

    -- Cursor para recorrer los lotes de stock inicial
    DECLARE C_STOCK CURSOR FOR
        SELECT Cantidad
        FROM StockInicial
        WHERE Cod_Pro = P_COD;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET V_FIN = 1;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        SET V_ERROR = 1;
        ROLLBACK;
    END;

    -- Validar código
    IF VALIDAR_COD_PRO(P_COD) = 0 THEN
        LEAVE CREAR_PRODUCTO_CON_STOCK_INICIAL;
    END IF;

    -- Validar que no exista
    SELECT COUNT(*) INTO V_EXISTE FROM Producto WHERE Cod_Pro = P_COD;
    IF V_EXISTE > 0 THEN LEAVE CREAR_PRODUCTO_CON_STOCK_INICIAL; END IF;

    -- Validar proveedor
    SELECT COUNT(*) INTO V_PROVE FROM Proveedor WHERE Nif_Prove = P_NIF_PROVE;
    IF V_PROVE = 0 THEN LEAVE CREAR_PRODUCTO_CON_STOCK_INICIAL; END IF;

    START TRANSACTION;

    -- Insertar producto con stock 0
    INSERT INTO Producto (Cod_Pro, Nom_Pro, Precio_Pro, Stock, Tipo, Nif_Prove)
    VALUES (P_COD, P_NOMBRE, P_PRECIO, 0, P_TIPO, P_NIF_PROVE);

    -- Recorrer lotes de stock inicial
    OPEN C_STOCK;
    SET V_FIN = 0;

    BUCLE_STOCK: LOOP
        FETCH C_STOCK INTO V_CANTIDAD;
        IF V_FIN = 1 THEN
            LEAVE BUCLE_STOCK;
        END IF;

        SET V_STOCK_TOTAL = V_STOCK_TOTAL + V_CANTIDAD;
    END LOOP;

    CLOSE C_STOCK;

    -- Actualizar stock final
    UPDATE Producto
    SET Stock = V_STOCK_TOTAL
    WHERE Cod_Pro = P_COD;

    IF V_ERROR = 0 THEN
        COMMIT;
    END IF;

END//

DELIMITER ;

-- 12. MODIFICAR PEDIDO --

DELIMITER //

CREATE PROCEDURE MODIFICAR_PEDIDO(
    IN P_NUMPED INT,
    IN P_ACCION VARCHAR(15),
    IN P_LISTA_PRO VARCHAR(200),
    IN P_LISTA_CAN VARCHAR(200),
    IN P_NUEVA_CALLE VARCHAR(100),
    IN P_NUEVO_TEL VARCHAR(50),
    IN P_NUEVO_EMAIL VARCHAR(100)
)
MODIFICAR_PEDIDO: BEGIN

    -- 1) VARIABLES
    DECLARE V_CODPRO VARCHAR(10);
    DECLARE V_CANT INT;
    DECLARE V_FIN INT DEFAULT 0;
    DECLARE V_ERROR INT DEFAULT 0;
    DECLARE V_POS INT DEFAULT 1;

    -- 2) CURSORES
    DECLARE C_NUEVAS CURSOR FOR
        SELECT Cod_Pro, Cantidad
        FROM TempLineas;

    DECLARE C_ANTIGUAS CURSOR FOR
        SELECT Cod_Pro, Cantidad_Pro
        FROM Contiene
        WHERE Num_Pedido = P_NUMPED;

    -- 3) HANDLERS
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET V_FIN = 1;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        SET V_ERROR = 1;
        ROLLBACK;
    END;

    -- 4) AHORA YA PUEDES HACER DROP/CREATE, ETC.
    DROP TEMPORARY TABLE IF EXISTS TempLineas;

    CREATE TEMPORARY TABLE TempLineas (
        Cod_Pro VARCHAR(10),
        Cantidad INT
    );

    -- 5) Cargar tabla temporal desde listas
    WHILE V_POS <= (LENGTH(P_LISTA_PRO) - LENGTH(REPLACE(P_LISTA_PRO, ',', '')) + 1) DO
        SET V_CODPRO = SUBSTRING_INDEX(SUBSTRING_INDEX(P_LISTA_PRO, ',', V_POS), ',', -1);
        SET V_CANT   = CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(P_LISTA_CAN, ',', V_POS), ',', -1) AS UNSIGNED);

        INSERT INTO TempLineas VALUES (V_CODPRO, V_CANT);

        SET V_POS = V_POS + 1;
    END WHILE;

    START TRANSACTION;

    -- (… resto igual que antes …)

END //

DELIMITER ;


– 13. ELIMINAR PEDIDO –

DELIMITER //

CREATE PROCEDURE ELIMINAR_PRODUCTO(
    IN P_COD VARCHAR(10)
)
ELIMINAR_PRODUCTO: BEGIN

    DECLARE V_EXISTE INT DEFAULT 0;
    DECLARE V_NUMPED INT;
    DECLARE V_CANT INT;
    DECLARE V_FIN INT DEFAULT 0;
    DECLARE V_ERROR INT DEFAULT 0;

    -- Cursor para recorrer las líneas donde aparece el producto
    DECLARE C_LINEAS CURSOR FOR
        SELECT Num_Pedido, Cantidad_Pro
        FROM Contiene
        WHERE Cod_Pro = P_COD;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET V_FIN = 1;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        SET V_ERROR = 1;
        ROLLBACK;
    END;

    -- 1. Comprobar que el producto existe
    SELECT COUNT(*) INTO V_EXISTE
    FROM Producto
    WHERE Cod_Pro = P_COD;

    IF V_EXISTE = 0 THEN
        LEAVE ELIMINAR_PRODUCTO_CON_CURSOR;
    END IF;

    START TRANSACTION;

    -- 2. Recorrer líneas del producto
    OPEN C_LINEAS;
    SET V_FIN = 0;

    BUCLE_LINEAS: LOOP
        FETCH C_LINEAS INTO V_NUMPED, V_CANT;
        IF V_FIN = 1 THEN LEAVE BUCLE_LINEAS; END IF;

        -- Opcional: devolver stock
        UPDATE Producto
        SET Stock = Stock + V_CANT
        WHERE Cod_Pro = P_COD;

        -- Eliminar la línea del pedido
        DELETE FROM Contiene
        WHERE Num_Pedido = V_NUMPED
          AND Cod_Pro = P_COD;
    END LOOP;

    CLOSE C_LINEAS;

    -- 3. Eliminar el producto
    DELETE FROM Producto
    WHERE Cod_Pro = P_COD;

    IF V_ERROR = 0 THEN
        COMMIT;
    END IF;

END//

DELIMITER ;


