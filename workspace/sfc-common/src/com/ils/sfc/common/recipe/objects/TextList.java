package com.ils.sfc.common.recipe.objects;

import com.ils.sfc.common.IlsProperty;

/**
superiorClass: sequence (the symbol S88-RECIPE-LIST-DATA)
attributes:sequence (structure (ATTRIBUTE-NAME: the symbol VAL,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol S88-PERMANENT-TEXT-LIST,
    ATTRIBUTE-INITIAL-ITEM-CLASS: the symbol S88-PERMANENT-TEXT-LIST))
 */
public class TextList extends List {

		public TextList() {
			addProperty(IlsProperty.STRING_VALUE);
		}
}
