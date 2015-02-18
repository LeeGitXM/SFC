-- Convert values found in G2 blocks to proper Ignition equivalents.
insert into PropertyValueMap values ('scope','LOCAL','local');
insert into PropertyValueMap values ('scope','PREVIOUS','previous');
insert into PropertyValueMap values ('scope','SUPERIOR','superior');

-- Names of callbacks get read from here and inserted into ProcedureMap also
insert into PropertyValueMap values ('callback','PH-TEST-CALLBACK-1','test1onstart1.py');
insert into PropertyValueMap values ('callback','PH-CALLBACK-1'     ,'test2onstart1.py');
insert into PropertyValueMap values ('callback','PH-CALLBACK-2'     ,'test2onstart2.py');
insert into PropertyValueMap values ('callback','TEST-3-RECIPE_DATA','test3onstartrd.py');
insert into PropertyValueMap values ('callback','S88-CALLBACK-TEMPLATE','s88OnstartTemplate.py');
insert into PropertyValueMap values ('callback','TEST-5-CALLBACK-1','test5onstart1.py');
insert into PropertyValueMap values ('callback','TEST-5-CALLBACK-2','test5onstart2.py');
insert into PropertyValueMap values ('callback','TEST-5-SHARED-CALLBACK','test5onstart.py');
insert into PropertyValueMap values ('callback','TEST-6-CALLBACK',  'test6onstart.py');
