INSERT INTO resort."role_resortASU"
(id, "name", enabled)
VALUES(1, 'ROLE_ADMIN', true),
(2, 'ROLE_USER', true),
(3, 'ROLE_CONTENT_MANAGER', true),
(4, 'ROLE_PROPERTY_MANAGER', true);

INSERT INTO resort."user_resortASU"
(username, "password")
VALUES('admin', '$2a$10$B0L9OAweQXvCLm6PzAaxj.yHV4I0P.FOdKc7QX7zsxy22XKkFNunu');

INSERT INTO resort.beds
(beds, beds_adult, beds_child)
VALUES('One bed for adult', 1, 0),
('One bed for adult, one bed for child', 1, 1),
('One double bed for two adults', 2, 0),
('Two separate beds for two adults', 2, 0),
('One double bed for two adults, one bed for child', 2 , 1),
('Two separate beds for two adults, one bed for child', 2 , 1);


INSERT INTO resort.floor
 (floor)
 VALUES('parking'),
('0'),
('1'),
('2'),
('3');

INSERT INTO resort.gender
 (gender)
 VALUES('not set'),
('male'),
('female');

INSERT INTO resort.paymentstatus
 (paymentstatus)
 VALUES('not payed'),
('partial prepayment'),
('full prepayment'),
('fully paid');

INSERT INTO resort.paymenttype
 (paymenttype)
 VALUES('Cash'),
('Checks'),
('Debit cards'),
('Credit cards'),
('Mobile payments'),
('Electronic bank transfers');

INSERT INTO resort.resortclass
(resortclass, description)
values ('no class', 'This resort was has no class yet'),
('*', 'Tourist'),
('*S', 'Superior Tourist'),
('**', 'Standard'),
('**S', 'Superior Standard'),
('***', 'Comfort'),
('***S', 'Superior Comfort'),
('****', 'First Class'),
('****S', 'First Class Superior'),
('*****', 'Luxury'),
('*****S', 'Superior Luxury');

INSERT INTO resort.resorttype
(name, description)
values ('default', 'This resort type was not set'),
('Hotel', 'A hotel provides basic services like room service and professional housekeeping.'),
('Motel', 'A motel is a roadside hotel mainly for motorists.'),
('Resort hotel', 'A resort hotel is usually spread out over more land than a hotel.'),
('Extended stay hotel (Aparthotel)', 'An extended stay hotel or aparthotel is a self-catered, fully-furnished apartmen that also offers lodging.'),
('Inn', 'An inn is primarily a restaurant, cafe, pub, or brewery that also offers lodging, and uses fewer than 20 rooms for lodging'),
('Bed and breakfast', 'A bed and breakfast (sometimes known as a guest house or pension) is a private or small commercial property');



INSERT INTO resort.suitclass
(suitclass, description)
values ('Basic', 'Basic suit'),
('Luxury', 'Luxury');

INSERT INTO resort.suitstatus
(status)
values ('Free'),
('Occupied'),
('Needs Maintenance'),
('Under Maintenance');

INSERT INTO resort.reservestatus
(status)
values ('Created'),
('Accepted'),
('Declined'),
('Cancelled'),
('Finished');
