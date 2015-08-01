-- Copyright 2015. ILS Automaition. All rights reserved.
-- These tables hold conversion mappings between G2 and Ignition
-- sequential control blocks and Ignition Sequential Function Charts.
--

-- Map a G2 class to an Ignition python class
CREATE TABLE ClassMap(
    G2Class text PRIMARY KEY ,
    IgnitionClass text NOT NULL
);
-- These are values of symbolic constants
CREATE TABLE EnumerationMap(
   G2Name text      NOT NULL,
   EnumerationName  text NOT NULL,
   Value            text NOT NULL
);
-- Define project preferences
CREATE TABLE PreferenceMap(
  Name text NOT NULL,
  Value text NOT NULL
);

-- Map properties of G2 blocks to properties of
-- Ignition blocks. For use with procedure translation.
CREATE TABLE PropertyMap(
    G2Class text NOT NULL,
    G2Property text NOT NULL,
	Property  text NOT NULL,
	Mode integer DEFAULT 2
);
-- Property value equivalents.
-- The special property "callback"
-- also results in entries into the 
-- PropertyMap table.
CREATE TABLE PropertyValueMap(
    Property text NOT NULL,
	G2Value text NOT NULL,
	IgnitionValue text NOT NULL
);

-- Map G2 GSI names to Ignition tags
CREATE TABLE TagMap(
	GSIName  text NOT NULL,
	TagPath  text NOT NULL,
	DataType text NOT NULL
);


-- Convert G2 procedute names into Python module names.
CREATE TABLE ProcedureMap(
    G2Procedure text NOT NULL,
    IgnitionProcedure text NOT NULL,
	ReturnType text
);

-- ========================== BLT Tables ===================
CREATE TABLE BltAnchorMap(
	G2Class text NOT NULL,
	G2Port  text NOT NULL,
	Port    text NOT NULL,
	Annotation text NULL
);
CREATE TABLE BltPythonAnchorMap(
	IgnitionClass text NOT NULL,
	Port    text NOT NULL,
    ConnectionType text NOT NULL,
    Direction text NOT NULL,
	Annotation text NULL
);
-- Map properties of G2 blocks to properties of
-- Ignition blocks. For use with procedure translation.
CREATE TABLE BltPropertyMap(
    G2Class text NOT NULL,
    G2Property text NOT NULL,
	PropertyName  text NOT NULL,
	Datatype  text NOT NULL,
	Editable integer DEFAULT 1,
	BindingType text DEFAULT 'NONE'
);

-- This table is used to define UI attributes for
-- blocks that are defined in Python.
CREATE TABLE BltPythonPrototypes(
    BlockClass text,
	Key text,
	Value text
);

-- These are properties of blocks that are fixed
-- no matter what. Applicable to python blocks.
CREATE TABLE BltPythonBlockProperties(
    BlockClass text NOT NULL,
    PropertyName text NOT NULL,
	PropertyType text NOT NULL,
	Editable integer
);
-- ======================= G2-Python Tables ================
-- Change the name of string arguments to procedures when
-- converting from G2 to Python
CREATE TABLE G2PyArgumentMap(
    G2Argument text NOT NULL,
    IgnitionArgument text NOT NULL
);

-- ========================== SFC Tables ===================
-- Map a G2 s88 class to an Ignition step class
CREATE TABLE SfcClassMap(
    G2Class text PRIMARY KEY ,
    FactoryId text NOT NULL,
	Type text DEFAULT ''
);
-- Map values of properties found in G2 S88 blocks to
-- Ignition blocks
CREATE TABLE SfcPropertyMap(
    FactoryId text NOT NULL,
	Property  text NOT NULL,
    G2Property text NOT NULL
);
