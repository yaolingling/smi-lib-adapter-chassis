/**
 * Copyright © 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.adapter.chassis;

import com.dell.isg.smi.common.protocol.command.cmc.entity.RacadmCredentials;

/**
 * @author rahman.muhammad
 *
 */
public interface IChassisOnboardingManager {

    public void configureTraps(RacadmCredentials racadmCredentials, String destinationAddress) throws Exception;


    public void resetFactoryConfig(RacadmCredentials racadmCredentials) throws Exception;

}
