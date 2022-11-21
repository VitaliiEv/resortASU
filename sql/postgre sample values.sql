INSERT INTO resort.resort
    ("name", description, resorttype, resortclass)
VALUES ('Sample hotel', 'Just regular hotel', 2, 6);

INSERT INTO resort.building
    (resort, name, description)
VALUES (1, 'Main', 'Main building');

INSERT INTO resort.buildingfloor
    (building, floor)
VALUES (1, 1),
       (1, 2),
       (1, 3),
       (1, 4),
       (1, 5);

INSERT INTO resort.suittype
    (suitclass, beds, area, currentprice, minimumprice)
VALUES (1, 2, 32, 67.76, 55.10),
       (1, 3, 23, 92.03, 67.66),
       (1, 3, 26, 98.02, 65.34),
       (2, 2, 32, 88.32, 55.70),
       (1, 5, 33, 92.20, 78.78),
       (2, 2, 26, 66.16, 55.57),
       (1, 6, 23, 61.05, 70.95),
       (1, 3, 27, 78.53, 54.23),
       (2, 5, 23, 69.01, 63.41),
       (2, 5, 23, 70.40, 63.24),
       (2, 4, 26, 99.36, 56.80),
       (1, 6, 22, 76.39, 52.25),
       (2, 4, 22, 74.51, 69.87),
       (2, 1, 24, 87.72, 64.63),
       (1, 4, 23, 62.27, 67.34),
       (1, 5, 26, 93.59, 62.31),
       (1, 5, 21, 71.19, 68.51),
       (1, 2, 21, 94.11, 78.62),
       (2, 5, 32, 71.92, 50.20),
       (1, 4, 26, 91.82, 50.90);

INSERT INTO resort.suit (number, suitstatus, suittype, buildingfloor)
VALUES
    ('471O',1,11,3),
    ('614G',3,11,3),
    ('626I',2,6,5),
    ('136Z',2,3,2),
    ('445U',3,10,5),
    ('823M',3,7,1),
    ('670C',3,17,3),
    ('624U',3,14,3),
    ('053Q',2,2,1),
    ('995H',4,10,2),
    ('750H',3,10,4),
    ('638B',2,12,3),
    ('794B',3,10,5),
    ('671X',2,12,5),
    ('447D',4,19,3),
    ('708S',3,18,4),
    ('111Y',1,5,3),
    ('883K',3,6,3),
    ('134Q',2,19,3),
    ('824C',1,9,2),
    ('451V',3,19,2),
    ('622P',4,10,2),
    ('235H',2,19,5),
    ('016H',1,8,4),
    ('665J',3,8,3),
    ('322Z',3,18,2),
    ('542B',2,6,4),
    ('950D',3,9,5),
    ('437T',2,5,4),
    ('624L',3,7,4),
    ('371W',3,10,4),
    ('024O',1,16,3),
    ('987U',3,18,2),
    ('585X',2,5,5),
    ('146D',2,18,3),
    ('817N',2,1,2),
    ('748T',4,19,5),
    ('757D',4,1,4),
    ('495N',3,7,3),
    ('585L',4,8,2);

INSERT INTO resort.services
    (service, description, currentprice, minimumprice, onetimepayment)
values ('Air conditioner', 'Air conditioner', 15.0, 10.0, true),
       ('Mini-bar', 'Fresh drinks', 6.0, 5.0, false),
       ('Safe', 'Store your goods', 10.0, 10.0, true),
       ('Seaview', 'You can see water from your windows', 0, 0, true),
       ('Wi-fi', 'You can enjoy free websurfing', 0, 0, true);
