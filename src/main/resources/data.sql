insert into location values (1,'Toronto, ON - CANADA');
insert into location values (2,'Vancouver, BC - CANADA');


insert into jhi_user values ('Volunteer','1d95e100-35b5-4b0d-8830-824db3d2d99b','greywind','Akshay','Bay','blacksoul4202@gmail.com',null,true,'en','system','2021-08-19 23:50:00','system','2021-08-19 23:50:00','47','416-715-9003',1);
insert into jhi_user values ('Volunteer','c3cbff48-e75b-4a40-9555-0d70a7c0a364','tysidius','Tysidius','Tywin','rjboyce.data42@gmail.com',null,true,'en','system','2021-08-19 23:50:00','system','2021-08-19 23:50:00','134','416-588-3317',1);
insert into jhi_user values ('Volunteer','690be563-6fe5-4149-8ca2-5b0d006f619f','admin','Master','Administrator','response@vkind.site',null,true,'en','system','2021-09-13 16:46:54','system','2021-09-13 16:46:54',null,'416-032-2111',1);


insert into jhi_authority values ('ROLE_ADMIN');
insert into jhi_authority values ('ROLE_VOLUNTEER');
insert into jhi_authority values ('ROLE_ORGANIZER');
insert into jhi_authority values ('ROLE_ATTENDEE');


insert into jhi_user_authority values ('1d95e100-35b5-4b0d-8830-824db3d2d99b','ROLE_VOLUNTEER');
insert into jhi_user_authority values ('1d95e100-35b5-4b0d-8830-824db3d2d99b','ROLE_ORGANIZER');
insert into jhi_user_authority values ('c3cbff48-e75b-4a40-9555-0d70a7c0a364','ROLE_ORGANIZER');
insert into jhi_user_authority values ('690be563-6fe5-4149-8ca2-5b0d006f619f','ROLE_ADMIN');


insert into event values (1,'2022-10-30','Social Seekers',10,'6:30 PM','Kasters Luck',1);
insert into event values (2,'2022-10-29','Meet And Greet',8,'8:30 PM','Sangarios Italian',2);
insert into event values (3,'2022-10-30','Social Dinner',2,'7:00 PM','Roundhouse Recreation',1);
insert into event values (4,'2022-10-29','Easy Dining',2,'5:30 PM','Lockes Fish and Chips',2);
insert into event values (5,'2022-10-30','Newcomer',4,'4:45 PM','Snarky Al Steakhouse',2);
insert into event values (6,'2022-10-30','Social Meet',6,'5:00 PM','Lokis Best Roti',1);


insert into event_attendance values (1,'4:30 PM','8:30 PM','80b-654-014',6,'1d95e100-35b5-4b0d-8830-824db3d2d99b');
insert into event_attendance values (2,null,null,null,3,'c3cbff48-e75b-4a40-9555-0d70a7c0a364');
insert into event_attendance values (3,null,null,null,3,'1d95e100-35b5-4b0d-8830-824db3d2d99b');
insert into event_attendance values (4,'11:30 AM','3:30 PM','36b-03c-0fd',1,'1d95e100-35b5-4b0d-8830-824db3d2d99b');
insert into event_attendance values (5,'8:45 PM',null,'a2f-e97-1ba',6,'c3cbff48-e75b-4a40-9555-0d70a7c0a364');
insert into event_attendance values (6,null,null,'1a5-294-652',1,'690be563-6fe5-4149-8ca2-5b0d006f619f');

-- This is necessary when setting auto-commit to false or off in spring properties
commit;
