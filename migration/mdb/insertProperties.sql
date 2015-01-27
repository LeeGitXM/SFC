-- Map G2 block properties into Ignition block properties
-- Columns are: IgnitonClass, ignition propertyName, datatype, G2Property
-- NOTE: Are description, label, name, uuid universal attributes?
-- S88-UNIT-PROCEDURE
insert into PropertyMap values ('com.ils.??','??','STRING','description');
insert into PropertyMap values ('com.ils.??','??','STRING','label');
insert into PropertyMap values ('com.ils.??','??','STRING','name');
insert into PropertyMap values ('com.ils.??','??','STRING','uuid');
insert into PropertyMap values ('com.ils.??','??','DOUBLE','conditional-block-recheck-interval-seconds');
insert into PropertyMap values ('com.ils.??','??','SCRIPT','post-to-error-queue-procedure');
insert into PropertyMap values ('com.ils.??','??','SCRIPT','post-to-message-queue-procedure');
insert into PropertyMap values ('com.ils.??','??','SCRIPT','show-message-queue-procedure');
insert into PropertyMap values ('com.ils.??','??','STRING','message-queue-name');
insert into PropertyMap values ('com.ils.??','??','BOOLEAN','show-control-panel');
-- S88-TIME-DELAY
insert into PropertyMap values ('com.ils.??','??','STRING','description');
insert into PropertyMap values ('com.ils.??','??','STRING','label');
insert into PropertyMap values ('com.ils.??','??','STRING','name');
insert into PropertyMap values ('com.ils.??','??','STRING','uuid');
insert into PropertyMap values ('com.ils.??','??','DOUBLE','delay-time');
insert into PropertyMap values ('com.ils.??','??','STRING','delay-units');
insert into PropertyMap values ('com.ils.??','??','STRING','recipe-location');
insert into PropertyMap values ('com.ils.??','??','STRING','strategy');
insert into PropertyMap values ('com.ils.??','??','BOOLEAN','post-notification');
