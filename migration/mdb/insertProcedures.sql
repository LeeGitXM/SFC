-- Map G2 procedure names into a python module.
insert into ProcedureMap select G2Value,IgnitionValue FROM PropertyValueMap WHERE Property='callback';
-- These are names of procedures found as properties during the migration 
insert into ProcedureMap values ('IRTLQ-VIEW','ils.??');
insert into ProcedureMap values ('_S88-POST-MESSAGE-TO-QUEUE','ils.??');
insert into ProcedureMap values ('_s88-abort','com.ils.sfc.python.S88State.abort');
insert into ProcedureMap values ('em-s88-c-rx-calc-dil-flows','com.ils.sfc.python.DilFlows.calculate');
insert into ProcedureMap values ('em-write-file','com.ils.sfc.python.File.write');
insert into ProcedureMap values ('g2-array-subtract','com.ils.util.Array.subtract');
insert into ProcedureMap values ('g2-scalar-multiply','com.ils.util.Scalar.multiply');
insert into ProcedureMap values ('s88-feed-enthalpy-ex-c6','com.ils.sfc.python.EnthalphyExC6.feed');
insert into ProcedureMap values ('s88-get','com.ils.sfc.python.ScopeData.get');
insert into ProcedureMap values ('s88-get-data','ils.sfc.gateway.api.s88GetData');
insert into ProcedureMap values ('s88-get-unit-procedure','com.ils.sfc.python.UnitProcedure.get');
insert into ProcedureMap values ('s88-heat','com.ils.sfc.python.heat.calculate');
insert into ProcedureMap values ('s88-ht-of-rx-t','com.ils.sfc.python.heatOfRxT.calculate');
insert into ProcedureMap values ('s88-post-error','com.ils.sfc.python.Post.error');
insert into ProcedureMap values ('s88-post-message-to-queue','com.ils.sfc.python.Post.messageToQueue');
insert into ProcedureMap values ('s88-set','com.ils.sfc.python.ScopeData.set');
insert into ProcedureMap values ('s88-set-data','ils.sfc.gateway.api.s88SetData');
insert into ProcedureMap values ('s88-sum-array','com.ils.sfc.python.Array.sum');
