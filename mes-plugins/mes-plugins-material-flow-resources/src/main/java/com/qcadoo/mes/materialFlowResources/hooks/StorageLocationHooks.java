package com.qcadoo.mes.materialFlowResources.hooks;

import com.qcadoo.mes.materialFlowResources.PalletValidatorService;
import com.qcadoo.mes.materialFlowResources.constants.StorageLocationFields;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StorageLocationHooks {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private PalletValidatorService palletValidatorService;

    public boolean validatesWith(final DataDefinition storageLocationDD, final Entity storageLocation) {
        boolean isValid = checkResourcePalletNumbers(storageLocationDD, storageLocation);

        isValid = isValid && checkMaxNumberOfPallets(storageLocationDD, storageLocation);

        return isValid;
    }

    private boolean checkResourcePalletNumbers(final DataDefinition storageLocationDD, final Entity storageLocation) {
        if (palletValidatorService.checkPalletNumbersInStorageLocation(storageLocation)) {
            storageLocation.addError(storageLocationDD.getField(StorageLocationFields.PLACE_STORAGE_LOCATION),
                    "materialFlowResources.storageLocation.maximumNumberOfPallets.palletsWithoutPalletNumbers");

            return false;
        }

        return true;
    }

    private boolean checkMaxNumberOfPallets(final DataDefinition storageLocationDD, final Entity storageLocation) {
        if (palletValidatorService.checkMaximumNumberOfPallets(storageLocation, 0)) {
            storageLocation.addError(storageLocationDD.getField(StorageLocationFields.MAXIMUM_NUMBER_OF_PALLETS),
                    "materialFlowResources.storageLocation.maximumNumberOfPallets.toManyPallets");

            return false;
        }

        return true;
    }

    public void onSave(final DataDefinition storageLocationDD, final Entity storageLocation) {
        clearMaxNumberOfPallets(storageLocationDD, storageLocation);
    }


    private void clearMaxNumberOfPallets(final DataDefinition storageLocationDD, final Entity storageLocation) {
        boolean placeStorageLocation = storageLocation.getBooleanField(StorageLocationFields.PLACE_STORAGE_LOCATION);

        if (!placeStorageLocation) {
            storageLocation.setField(StorageLocationFields.MAXIMUM_NUMBER_OF_PALLETS, null);
        }
    }

}
