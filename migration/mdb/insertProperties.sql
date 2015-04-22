-- Map G2 block properties into Ignition block properties
-- Columns are: FactoryId, ignition propertyName, G2Property
-- NOTE: name, factoryId, uuid are universal attributes and are not included here.
insert into PropertyMap values ('enclosing-step','description','description');
-- S88-PAHASE
insert into PropertyMap values ('com.ils.phaseStep','description','description');
-- S88-OPERATION
insert into PropertyMap values ('com.ils.operationStep','description','description');
-- S88-UNIT-PROCEDURE
insert into PropertyMap values ('com.ils.procedureStep','description','description');
-- S88-TIME-DELAY
insert into PropertyMap values ('com.ils.timedDelayStep','auditLevel','');
insert into PropertyMap values ('com.ils.timedDelayStep','callback','callback');
insert into PropertyMap values ('com.ils.timedDelayStep','delay','delay-time');
insert into PropertyMap values ('com.ils.timedDelayStep','delayUnit','delay-units');
insert into PropertyMap values ('com.ils.timedDelayStep','description','description');
insert into PropertyMap values ('com.ils.timedDelayStep','key','identifier-or-name');
insert into PropertyMap values ('com.ils.timedDelayStep','postNotification','post-notification');
insert into PropertyMap values ('com.ils.timedDelayStep','recipeLocation','recipe-location');
insert into PropertyMap values ('com.ils.timedDelayStep','strategy','strategy');
