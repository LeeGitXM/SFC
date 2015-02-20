-- Convert values found in G2 blocks to proper Ignition equivalents.
insert into PropertyValueMap values ('scope','LOCAL','local');
insert into PropertyValueMap values ('scope','PREVIOUS','previous');
insert into PropertyValueMap values ('scope','SUPERIOR','superior');

-- Names of callbacks get read from here and inserted into ProcedureMap also
insert into PropertyValueMap values ('callback','ph-test-callback-1','test1OnStart1.onStart');
insert into PropertyValueMap values ('callback','ph-callback-1'     ,'test2OnStart1.onStart');
insert into PropertyValueMap values ('callback','ph-callback-2'     ,'test2OnStart2.onStart');
insert into PropertyValueMap values ('callback','test-3-recipe-data','test3OnStartrd.onStart');
insert into PropertyValueMap values ('callback','S88-callback-template','s88OnstartTemplate.onStart');
insert into PropertyValueMap values ('callback','test-5-callback-1','test5OnStart1.onStart');
insert into PropertyValueMap values ('callback','test-5-callback-2','test5OnStart2.onStart');
insert into PropertyValueMap values ('callback','test-5-shared-callback','test5OnStart.onStart');
insert into PropertyValueMap values ('callback','test-6-callback',  'test6OnStart.onStart');
