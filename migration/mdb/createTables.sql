-- Copyright 2015. ILS Automaition. All rights reserved.
-- These tables hold conversion mappings between G2
-- sequential control blocks and Ignition Sequential Function Charts.
--
-- Map a G2 block class to an Ignition step class
CREATE TABLE ClassMap(
    G2Class text PRIMARY KEY ,
    FactoryId text NOT NULL,
	Type text DEFAULT ''
);

-- Map string arguments to procedures from
-- G2 to Ignition
CREATE TABLE ArgumentMap(
    G2Argument text NOT NULL,
    IgnitionArgument text NOT NULL
);
-- Map values of properties found in G2 blocks to
-- Map properties of G2 blocks to properties of
-- Ignition blocks
CREATE TABLE PropertyMap(
    FactoryId text NOT NULL,
	Property  text NOT NULL,
    G2Property text NOT NULL
);
-- Map values of properties found in G2 blocks to
-- Ignition equivalents. These include procedure
-- names.
CREATE TABLE PropertyValueMap(
    Property text NOT NULL,
	G2Value text NOT NULL,
	IgnitionValue text NOT NULL
);

-- Convert G2 procedute names into Python module names.
CREATE TABLE ProcedureMap(
    G2Procedure text NOT NULL,
    IgnitionProcedure text NOT NULL
);
-- Map G2 GSI names to Ignition tags
CREATE TABLE TagMap(
	GSIName  text NOT NULL,
	TagPath  text NOT NULL,
	DataType text NOT NULL
);

-- The tables below pertain to procedure translation
-- Map a G2 class to an Ignition python class
CREATE TABLE ProcClassMap(
    G2Class text PRIMARY KEY ,
    IgnitionClass text NOT NULL
);

-- Set properties of Ignition blocks based on a 
-- G2 class. These properties rely only on the G2 class.
CREATE TABLE ProcClassProperty(
    G2Class    text NOT NULL,
	Name     text NOT NULL,
	DataType text NOT NULL,
	Editable integer,
	BindingType text NOT NULL,
	Value text
);

-- Map properties of G2 blocks to properties of
-- Ignition blocks for use within procedures.
CREATE TABLE ProcPropertyMap(
    G2Class text NOT NULL,
    G2Property text NOT NULL,
	Property  text NOT NULL,
	Mode integer NOT NULL
);
-- These are values of symbolic constants
CREATE TABLE ProcEnumerationMap(
   G2Name text      NOT NULL,
   EnumerationName  text NOT NULL,
   Value            text NOT NULL
);
-- These are values of globals
-- by the procedures that need them.
CREATE TABLE ProcGlobalMap(
   PyProc      text NOT NULL,
   GlobalName  text NOT NULL
);
