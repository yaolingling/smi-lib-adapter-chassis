/**
 * Copyright © 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.adapter.chassis;

import java.util.List;

import com.dell.isg.smi.common.protocol.command.chassis.entity.ChassisLog;
import com.dell.isg.smi.common.protocol.command.cmc.entity.Chassis;
import com.dell.isg.smi.common.protocol.command.cmc.entity.ChassisCMCViewEntity;
import com.dell.isg.smi.common.protocol.command.cmc.entity.RacadmCredentials;

public interface IChassisAdapter {
    public ChassisCMCViewEntity collectChassisSummary(RacadmCredentials credentials) throws Exception;


    public Chassis collectChassisInventory(RacadmCredentials credentials) throws Exception;


    public String reseatServer(RacadmCredentials credentials, Long slotNumber) throws Exception;


    public List<ChassisLog> getChassisLogs(RacadmCredentials credentials) throws Exception;

}
