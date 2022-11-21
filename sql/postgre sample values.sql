INSERT INTO resort.resort
("name", description, resorttype, resortclass)
VALUES('Sample hotel', 'Just regular hotel', 2, 6);

INSERT INTO resort.building
(resort, name, description)
VALUES(1, 'Main', 'Main building');

INSERT INTO resort.buildingfloor
(building, floor)
VALUES(1, 1),
VALUES(1, 2),
VALUES(1, 3),
VALUES(1, 4),
VALUES(1, 5);

INSERT INTO resort.suittype
(suitclass,beds,area,currentprice,minimumprice)
VALUES
  (1,2,32,67.76,55.10),
  (1,3,23,92.03,67.66),
  (1,3,26,98.02,65.34),
  (2,2,32,88.32,55.70),
  (1,5,33,92.20,78.78),
  (2,2,26,66.16,55.57),
  (1,6,23,61.05,70.95),
  (1,3,27,78.53,54.23),
  (2,5,23,69.01,63.41),
  (2,5,23,70.40,63.24),
  (2,4,26,99.36,56.80),
  (1,6,22,76.39,52.25),
  (2,4,22,74.51,69.87),
  (2,1,24,87.72,64.63),
  (1,4,23,62.27,67.34),
  (1,5,26,93.59,62.31),
  (1,5,21,71.19,68.51),
  (1,2,21,94.11,78.62),
  (2,5,32,71.92,50.20),
  (1,4,26,91.82,50.90);
  
  INSERT INTO resort.suit (number,suitstatus,suittype,buildingfloor)
VALUES
  ('442I',1,22,23),
  ('742F',2,12,24),
  ('912K',3,18,24),
  ('066I',3,15,25),
  ('217K',3,13,23),
  ('843J',3,6,22),
  ('365K',2,11,24),
  ('397U',3,16,25),
  ('271N',3,8,23),
  ('535U',3,8,23),
  ('659V',1,12,22),
  ('552C',1,20,24),
  ('821D',3,4,23),
  ('698U',3,7,24),
  ('672W',3,23,25),
  ('755J',2,10,25),
  ('265G',3,10,24),
  ('107E',2,22,22),
  ('120I',2,21,22),
  ('796R',2,9,22),
  ('222E',1,15,23),
  ('963M',3,5,21),
  ('409N',3,15,22),
  ('227I',4,9,22),
  ('716E',1,22,22),
  ('896N',2,19,22),
  ('835I',3,15,23),
  ('376K',2,17,21),
  ('825B',3,20,21),
  ('167S',3,10,24),
  ('245U',3,8,24),
  ('595M',1,16,23),
  ('422B',4,7,22),
  ('617K',3,7,25),
  ('740Z',4,6,24),
  ('385B',1,5,24),
  ('776S',1,17,22),
  ('565Y',4,21,23),
  ('600X',2,12,22),
  ('393B',2,7,25);
  
INSERT INTO resort.services
(service, description, currentprice, minimumprice, onetimepayment)
values ('Air conditioner', 'Air conditioner', 15.0, 10.0, true),
('Mini-bar', 'Fresh drinks', 6.0, 5.0, false),
('Safe', 'Store your goods', 10.0, 10.0, true),
('Seaview', 'You can see water from your windows', 0, 0, true),
('Wi-fi', 'You can enjoy free websurfing', 0, 0, true);
