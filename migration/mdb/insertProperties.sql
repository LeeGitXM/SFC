-- Map G2 block properties into Ignition block properties
-- Columns are: FactoryId, ignition propertyName, datatype, G2Property
-- NOTE: Are description, label, name, uuid universal attributes?
-- S88-UNIT-PROCEDURE
insert into PropertyMap values ('enclosing-step','??','STRING','description');
insert into PropertyMap values ('enclosing-step','??','STRING','label');
insert into PropertyMap values ('enclosing-step','??','STRING','name');
insert into PropertyMap values ('enclosing-step','??','STRING','uuid');
insert into PropertyMap values ('enclosing-step','??','DOUBLE','conditional-block-recheck-interval-seconds');
insert into PropertyMap values ('enclosing-step','??','SCRIPT','post-to-error-queue-procedure');
insert into PropertyMap values ('enclosing-step','??','SCRIPT','post-to-message-queue-procedure');
insert into PropertyMap values ('enclosing-step','??','SCRIPT','show-message-queue-procedure');
insert into PropertyMap values ('enclosing-step','??','STRING','message-queue-name');
insert into PropertyMap values ('enclosing-step','??','BOOLEAN','show-control-panel');
-- S88-TIME-DELAY
insert into PropertyMap values ('com.ils.TimedDelayStep','??','STRING','description');
insert into PropertyMap values ('com.ils.TimedDelayStep','??','STRING','label');
insert into PropertyMap values ('com.ils.TimedDelayStep','??','STRING','name');
insert into PropertyMap values ('com.ils.TimedDelayStep','??','STRING','uuid');
insert into PropertyMap values ('com.ils.TimedDelayStep','??','DOUBLE','delay-time');
insert into PropertyMap values ('com.ils.TimedDelayStep','??','STRING','delay-units');
insert into PropertyMap values ('com.ils.TimedDelayStep','??','STRING','recipe-location');
insert into PropertyMap values ('com.ils.TimedDelayStep','??','STRING','strategy');
insert into PropertyMap values ('com.ils.TimedDelayStep','??','BOOLEAN','post-notification');
