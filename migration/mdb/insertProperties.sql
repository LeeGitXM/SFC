
-- Map G2 block properties into Ignition block properties for use within procedures
-- Columns are: G2 class, G2 propertyName, property, mode
--     where mode = 1 => wholesale replacement
--                  2 => rename property
--                  3 => constant
-- chart - ignore debug-mode
insert into PropertyMap(G2Class,G2Property,Property,Mode) values ('chart','debug-mode','log.isTraceEnabled()',1);
insert into PropertyMap(G2Class,G2Property,Property,Mode) values ('structure','target-id','Target',2);
-- step
insert into PropertyMap(G2Class,G2Property,Property,Mode) values ('s88-recipe-entity','debug-mode','log.isTraceEnabled()',1);
insert into PropertyMap(G2Class,G2Property,Property,Mode) values ('s88-recipe-entity','class','step',3);
insert into PropertyMap(G2Class,G2Property,Property,Mode) values ('s88-recipe-entity','label','name',2);
insert into PropertyMap(G2Class,G2Property,Property,Mode) values ('s88-pv-monitoring-task','configuration','configuration',2);
insert into PropertyMap(G2Class,G2Property,Property,Mode) values ('value-array','array-length','len()',1);
