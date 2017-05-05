/**
 * Copyright © 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.adapter.chassis;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dell.isg.smi.common.protocol.command.cmc.entity.RacadmCredentials;
import com.dell.isg.smi.adapter.chassis.IChassisOnboardingManager;

/**
 * @author prashanth.gowda
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/test-chassis-adapter-context.xml" })
public class ChassisOnboardingMangerImplTest {

    @Autowired
    IChassisOnboardingManager chassisOnboardingManagerImpl;

    @Autowired
    RacadmCredentials racadmCredentials;


    @Ignore
    @Test
    public void configureTrapsTest() throws Exception {
        String destinationAddress = "controller.icee.core";
        chassisOnboardingManagerImpl.configureTraps(racadmCredentials, destinationAddress);
    }


    @Ignore
    @Test
    public void resetFactoryConfigTest() throws Exception {
        chassisOnboardingManagerImpl.resetFactoryConfig(racadmCredentials);
    }

}
