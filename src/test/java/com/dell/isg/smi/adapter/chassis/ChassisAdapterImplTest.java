/**
 * Copyright © 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.adapter.chassis;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dell.isg.smi.common.protocol.command.cmc.entity.Chassis;
import com.dell.isg.smi.common.protocol.command.cmc.entity.ChassisCMCViewEntity;
import com.dell.isg.smi.common.protocol.command.cmc.entity.RacadmCredentials;
import com.dell.isg.smi.commons.elm.utilities.CustomRecursiveToStringStyle;
import com.dell.isg.smi.adapter.chassis.IChassisAdapter;

/**
 * @author prashanth.gowda
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/test-chassis-adapter-context.xml" })
public class ChassisAdapterImplTest {

    @Autowired
    IChassisAdapter chassisAdapterImpl;

    @Autowired
    RacadmCredentials racadmCredentials;

    private final static Logger logger = LoggerFactory.getLogger(ChassisAdapterImplTest.class.getName());


    @Ignore
    @Test
    public void collectChassisSummaryTest() throws Exception {
        ChassisCMCViewEntity chassisCmcViewEntity = chassisAdapterImpl.collectChassisSummary(racadmCredentials);
        logger.trace("Chassis Summary :" + ReflectionToStringBuilder.toString(chassisCmcViewEntity, new CustomRecursiveToStringStyle(99)));
    }


    @Ignore
    @Test
    public void collectChassisInventoryTest() throws Exception {
        Chassis chassis = chassisAdapterImpl.collectChassisInventory(racadmCredentials);
        logger.trace("Chassis Inventory :" + ReflectionToStringBuilder.toString(chassis, new CustomRecursiveToStringStyle(99)));
    }


    @Ignore
    @Test
    public void reseatServerTest() throws Exception {
        chassisAdapterImpl.reseatServer(racadmCredentials, 1L);
    }
}
