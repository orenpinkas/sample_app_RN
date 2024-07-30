package com.outbrain.tests.registration;

import com.outbrain.OBSDK.Entities.OBLocalSettings;
import com.outbrain.OBSDK.Registration.RegistrationService;

import junit.framework.TestCase;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.UUID;

/**
 * Created by rabraham on 20/06/2017.
 */

public class RegistrationServiceTest extends TestCase {

    private RegistrationService registrationService;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Field field = RegistrationService.class.getDeclaredField("WAS_INITIALIZED");
        field.setAccessible(true);
        field.set(null, false);

        this.registrationService = RegistrationService.getInstance();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        registrationService.setLocalSettings(null);
    }

    public final void testSetLocalSettings() throws Exception {
        Field field = RegistrationService.class.getDeclaredField("localSettings");
        field.setAccessible(true);
        OBLocalSettings registrationServiceLocalSettings = (OBLocalSettings) field.get(registrationService);

        OBLocalSettings localSettings = generateLocalSettings(UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                new Random().nextBoolean());
        assertNull(registrationServiceLocalSettings);

        registrationService.setLocalSettings(localSettings);
        registrationServiceLocalSettings = (OBLocalSettings) field.get(registrationService);
        assertNotNull(registrationServiceLocalSettings);

        assertEquals(registrationServiceLocalSettings.partnerKey, localSettings.partnerKey);
        assertEquals(registrationServiceLocalSettings.version, localSettings.version);
        assertEquals(registrationServiceLocalSettings.isTestMode(), localSettings.isTestMode());
    }

    public final void testSetTestMode() throws Exception {
        OBLocalSettings localSettings = generateLocalSettings(UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                new Random().nextBoolean());
        registrationService.setLocalSettings(localSettings);
        Field localSettingsField = RegistrationService.class.getDeclaredField("localSettings");
        localSettingsField.setAccessible(true);
        OBLocalSettings registrationServiceLocalSettings = (OBLocalSettings) localSettingsField.get(registrationService);

        boolean currentTestMode = registrationServiceLocalSettings.isTestMode();
        registrationService.setTestMode(!currentTestMode);

        registrationServiceLocalSettings = (OBLocalSettings) localSettingsField.get(registrationService);

        assertTrue(currentTestMode != registrationServiceLocalSettings.isTestMode());
    }

    public final void testRegisterIfInitialized() throws Exception {
        RegistrationService.WAS_INITIALIZED = true;
        OBLocalSettings localSettings = generateLocalSettings(UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                new Random().nextBoolean());
        registrationService.setLocalSettings(localSettings);

        String partnerKey = "new partner key";
        registrationService.register(partnerKey);

        Field localSettingsField = RegistrationService.class.getDeclaredField("localSettings");
        localSettingsField.setAccessible(true);
        OBLocalSettings registrationServiceLocalSettings = (OBLocalSettings) localSettingsField.get(registrationService);

        assertFalse(registrationServiceLocalSettings.partnerKey.equals(partnerKey));

    }

    public final void testRegisterIfNotInitializedAndPartnerKeyIsNull() throws Exception {
        String partnerKey = UUID.randomUUID().toString();
        OBLocalSettings localSettings = generateLocalSettings(partnerKey,
                UUID.randomUUID().toString(),
                new Random().nextBoolean());
        registrationService.setLocalSettings(localSettings);

        boolean exceptionWasThrown = false;
        try {
            registrationService.register(null);
        } catch (RuntimeException e) {
            exceptionWasThrown = true;
        }

        assertTrue(exceptionWasThrown);

        Field localSettingsField = RegistrationService.class.getDeclaredField("localSettings");
        localSettingsField.setAccessible(true);
        OBLocalSettings registrationServiceLocalSettings = (OBLocalSettings) localSettingsField.get(registrationService);

        assertTrue(registrationServiceLocalSettings.partnerKey.equals(partnerKey));

    }

    public final void testRegisterIfNotInitializedAndPartnerKeyIsEmpty() throws Exception {
        String partnerKey = UUID.randomUUID().toString();
        OBLocalSettings localSettings = generateLocalSettings(partnerKey,
                UUID.randomUUID().toString(),
                new Random().nextBoolean());
        registrationService.setLocalSettings(localSettings);

        boolean exceptionWasThrown = false;
        try {
            registrationService.register("");
        } catch (RuntimeException e) {
            exceptionWasThrown = true;
        }

        assertTrue(exceptionWasThrown);

        Field localSettingsField = RegistrationService.class.getDeclaredField("localSettings");
        localSettingsField.setAccessible(true);
        OBLocalSettings registrationServiceLocalSettings = (OBLocalSettings) localSettingsField.get(registrationService);

        assertTrue(registrationServiceLocalSettings.partnerKey.equals(partnerKey));

    }

    public final void testRegisterIfNotInitialized() throws Exception {
        String partnerKey = UUID.randomUUID().toString();
        OBLocalSettings localSettings = generateLocalSettings(partnerKey,
                UUID.randomUUID().toString(),
                new Random().nextBoolean());
        registrationService.setLocalSettings(localSettings);

        assertFalse(registrationService.wasInitialized());

        String newPartnerKey = "new partner key";
        boolean exceptionWasThrown = false;
        try {
            registrationService.register(newPartnerKey);
        } catch (RuntimeException e) {
            exceptionWasThrown = true;
        }

        assertFalse(exceptionWasThrown);

        Field localSettingsField = RegistrationService.class.getDeclaredField("localSettings");
        localSettingsField.setAccessible(true);
        OBLocalSettings registrationServiceLocalSettings = (OBLocalSettings) localSettingsField.get(registrationService);

        assertFalse(registrationServiceLocalSettings.partnerKey.equals(partnerKey));
        assertTrue(registrationServiceLocalSettings.partnerKey.equals(newPartnerKey));
        assertTrue(registrationService.wasInitialized());

    }

    public final void testGetPartnerKey() {
        String partnerKey = UUID.randomUUID().toString();
        OBLocalSettings localSettings = generateLocalSettings(partnerKey,
                UUID.randomUUID().toString(),
                new Random().nextBoolean());
        registrationService.setLocalSettings(localSettings);
        assertTrue(registrationService.getPartnerKey().equals(partnerKey));
    }

    private OBLocalSettings generateLocalSettings(String partnerKey, String version, boolean testMode) {
        OBLocalSettings localSettings = new OBLocalSettings();
        localSettings.setTestMode(testMode);
        localSettings.partnerKey = partnerKey;
        localSettings.version = version;
        return localSettings;
    }
}
