package com.mindbox.pe.server.imexport;

import com.mindbox.pe.model.TypeEnumValue;
import com.mindbox.pe.server.cache.TypeEnumValueManager;
import com.mindbox.pe.xsd.data.TypeEnumDataElement;
import com.mindbox.pe.xsd.data.TypeEnumDataElement.TypeEnum;

public class TypeEnumDataImporter extends AbstractImporter<TypeEnumDataElement, String> {

	public TypeEnumDataImporter(ImportBusinessLogic importBusinessLogic) {
		super(importBusinessLogic);
	}

	@Override
	protected void processData(final TypeEnumDataElement dataToImport, final String optionalData) throws ImportException {
		if (!dataToImport.getTypeEnum().isEmpty()) {
			if (!merge) {
				TypeEnumValueManager.getInstance().startLoading();
			}
			try {
				for (final TypeEnum typeEnum : dataToImport.getTypeEnum()) {
					final TypeEnumValue typeEnumValue = new TypeEnumValue(typeEnum.getEnumId(), typeEnum.getEnumValue(), typeEnum.getDisplayLabel());
					if (merge && TypeEnumValueManager.getInstance().hasEnumValue(typeEnum.getType(), typeEnum.getEnumId())) {
						TypeEnumValueManager.getInstance().update(typeEnum.getType(), typeEnumValue);
					}
					else {
						TypeEnumValueManager.getInstance().insert(typeEnum.getType(), typeEnumValue);
					}
				}
			}
			finally {
				TypeEnumValueManager.getInstance().finishLoading();
			}
		}
	}
}
