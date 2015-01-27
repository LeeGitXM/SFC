-- Map a G2 block class to an Ignition step class
CREATE TABLE ClassMap(
    G2Class text PRIMARY KEY ,
    IgnitionClass text NOT NULL
);

-- Map properties of G2 blocks to properties of
-- Ignition blocks
CREATE TABLE PropertyMap(
    IgnitionClass  text NOT NULL,
	Name     text NOT NULL,
	DataType text NOT NULL,
    G2Property text NOT NULL
);

-- Convert G2 procedute names into Python module names.
CREATE TABLE ProcedureMap(
    G2Procedure text NOT NULL,
    IgnitonProcedure text NOT NULL
);
-- Map G2 GSI names to Ignition tags
CREATE TABLE TagMap(
	GSIName  text NOT NULL,
	TagPath  text NOT NULL,
	DataType text NOT NULL
);
