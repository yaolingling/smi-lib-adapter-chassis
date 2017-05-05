/**
 * Copyright © 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.adapter.chassis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.dell.isg.smi.common.protocol.command.chassis.entity.CfgTraps;
import com.dell.isg.smi.common.protocol.command.cmc.entity.RacadmCredentials;
import com.dell.isg.smi.common.protocol.command.racadm.ResetFactoryConfigCmd;
import com.dell.isg.smi.common.protocol.command.racadm.SetChassisTrapCfgCmd;
import com.dell.isg.smi.commons.elm.exception.RuntimeCoreException;

/**
 * @author rahman.muhammad
 *
 */
@Component("chassisOnboardingManagerImpl")
public class ChassisOnboardingManagerImpl implements IChassisOnboardingManager {

    private static final Logger logger = LoggerFactory.getLogger(ChassisOnboardingManagerImpl.class);
    final String DISABLED = "0";
    final String ENABLED = "1";
    final String COMMUNITY = "public";


    @Override
    public void configureTraps(RacadmCredentials racadmCredentials, String destinationAddress) throws Exception {
        String address = racadmCredentials.getAddress();
        logger.info("Entering configureTraps {} ", address);
        String username = racadmCredentials.getUserName();
        String password = racadmCredentials.getPassword();
        boolean isCertificateCheck = racadmCredentials.isCertificateCheck();
        CfgTraps configTraps = new CfgTraps();
        configTraps.setCfgTrapsCommunityName(COMMUNITY);

        /**
         * 0 = disabled 1 = enabled.
         */
        configTraps.setCfgTrapsEnable(ENABLED);
        configTraps.setCfgTrapsAlertDestIPAddr(destinationAddress);
        SetChassisTrapCfgCmd trapConfigCmd = new SetChassisTrapCfgCmd(address, username, password, isCertificateCheck, configTraps);
        if (!trapConfigCmd.execute()) {
            RuntimeCoreException exception = new RuntimeCoreException("Configuring SNMP TRAPS failed");
            throw exception;
        }
        logger.trace("Entering configureTraps {} ", address);
    }


    @Override
    public void resetFactoryConfig(RacadmCredentials racadmCredentials) throws Exception {
        String address = racadmCredentials.getAddress();
        logger.info("Entering resetFactoryConfig {} ", address);
        String username = racadmCredentials.getUserName();
        String password = racadmCredentials.getPassword();
        boolean isCertificateCheck = racadmCredentials.isCertificateCheck();
        try {
            ResetFactoryConfigCmd resetFactoryConfigCmd = new ResetFactoryConfigCmd(address, username, password, isCertificateCheck);
            resetFactoryConfigCmd.execute();
        } catch (Exception e) {
            logger.info("Problem encountered while resetting chassis.", e);
        } finally {
            logger.trace("Entering resetFactoryConfig {} ", address);
        }
    }

}
