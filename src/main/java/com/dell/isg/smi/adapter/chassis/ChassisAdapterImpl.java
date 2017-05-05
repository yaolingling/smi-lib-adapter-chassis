/**
 * Copyright © 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.adapter.chassis;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.dell.isg.smi.common.protocol.command.chassis.entity.CfgKvmInfo;
import com.dell.isg.smi.common.protocol.command.chassis.entity.ChassisLog;
import com.dell.isg.smi.common.protocol.command.chassis.entity.KvmInfo;
import com.dell.isg.smi.common.protocol.command.chassis.entity.ModInfo;
import com.dell.isg.smi.common.protocol.command.cmc.entity.Chassis;
import com.dell.isg.smi.common.protocol.command.cmc.entity.ChassisCMCViewEntity;
import com.dell.isg.smi.common.protocol.command.cmc.entity.ChassisFanEntity;
import com.dell.isg.smi.common.protocol.command.cmc.entity.ChassisIkvm;
import com.dell.isg.smi.common.protocol.command.cmc.entity.ChassisPCIeEntity;
import com.dell.isg.smi.common.protocol.command.cmc.entity.ChassisPowerSupplyEntity;
import com.dell.isg.smi.common.protocol.command.cmc.entity.ChassisTemperatureSensorEntity;
import com.dell.isg.smi.common.protocol.command.cmc.entity.IOModuleEntity;
import com.dell.isg.smi.common.protocol.command.cmc.entity.RacadmCredentials;
import com.dell.isg.smi.common.protocol.command.racadm.EnumChassisCMCViewCmd;
import com.dell.isg.smi.common.protocol.command.racadm.EnumChassisFanCmd;
import com.dell.isg.smi.common.protocol.command.racadm.EnumChassisLogsCmd;
import com.dell.isg.smi.common.protocol.command.racadm.EnumChassisPCIeCmd;
import com.dell.isg.smi.common.protocol.command.racadm.EnumChassisPowerSupplyCmd;
import com.dell.isg.smi.common.protocol.command.racadm.EnumChassisTemperatureSensorCmd;
import com.dell.isg.smi.common.protocol.command.racadm.EnumIOModuleCmd;
import com.dell.isg.smi.common.protocol.command.racadm.EnumKvmInfoCmd;
import com.dell.isg.smi.common.protocol.command.racadm.EnumModInfoCmd;
import com.dell.isg.smi.common.protocol.command.racadm.EnumModInfoCmd.ModInfoTypeEnum;
import com.dell.isg.smi.common.protocol.command.racadm.EnumSensorInfoCmd;
import com.dell.isg.smi.common.protocol.command.racadm.EnumVersionSummaryCmd;
import com.dell.isg.smi.common.protocol.command.racadm.ReseatServerCmd;

@Component("chassisAdapterImpl")
public class ChassisAdapterImpl implements IChassisAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ChassisAdapterImpl.class.getName());

    private EnumModInfoCmd enumRacadmModInfoCmd = null;
    private EnumVersionSummaryCmd enumVersionSummaryCmd = null;
    private EnumSensorInfoCmd enumSensorInfoCmd = null;


    @Override
    public ChassisCMCViewEntity collectChassisSummary(RacadmCredentials credentials) throws Exception {

        logger.info("Collecting Summary for chassis {} ", credentials.getAddress());

        ChassisCMCViewEntity cmcViewEntity = null;
        EnumChassisCMCViewCmd cmcViewCommand = new EnumChassisCMCViewCmd(credentials.getAddress(), credentials.getUserName(), credentials.getPassword(), credentials.isCertificateCheck());
        cmcViewEntity = cmcViewCommand.execute();

        logger.info("Summary Collected for chassis {} ", credentials.getAddress());
        return cmcViewEntity;
    }


    @Override
    public Chassis collectChassisInventory(RacadmCredentials credentials) throws Exception {

        logger.info("Collecting Inventory for chassis {} ", credentials.getAddress());

        Chassis chassis = new Chassis();
        chassis.getChassisCmcList().add(collectChassisSummary(credentials));
        collectSlots(chassis, credentials);
        collectPowerSupplies(chassis, credentials);
        collectPcis(chassis, credentials);
        collectFans(chassis, credentials);
        collectTemperatureSensors(chassis, credentials);
        collectIoms(chassis, credentials);
        collectServers(chassis, credentials);
        collectStashStorage(chassis, credentials);

        /*
         * This feature is not supported on target current Chassis, but we will keep if 1 - supported is added for FX2 2- support is added for M1000e OR VRTX -
         * collectKvmInfo(chassis, credentials);
         */

        logger.info("Inventory Collected for chassis {} ", credentials.getAddress());

        return chassis;
    }


    @Override
    public String reseatServer(RacadmCredentials credentials, Long slotNumber) throws Exception {
        logger.info("entered reseat Server ", credentials.getAddress());
        ReseatServerCmd reseatServerCmd = new ReseatServerCmd(credentials.getAddress(), credentials.getUserName(), credentials.getPassword(), credentials.isCertificateCheck(), slotNumber);
        String result = reseatServerCmd.execute();
        logger.info("ended reseat Server", credentials.getAddress());
        return result;
    }


    private void collectSlots(Chassis chassis, RacadmCredentials credentials) {

        try {
            EnumModInfoCmd enumRacadmModInfoCmd = this.getModInfoCmd(credentials);
            chassis.setNumberOfSlots(enumRacadmModInfoCmd.getServerSlotsCount());
            int usedSlots = enumRacadmModInfoCmd.getUsedServerSlotsCount();
            if (chassis.getNumberOfSlots() > usedSlots) {
                chassis.setNumberOfFreeSlots(chassis.getNumberOfSlots() - usedSlots);
            }
        } catch (Exception e) {
            logger.error("Enumerate Slots info command failed for Chassis");
            logger.error(e.getMessage());
        }

    }


    private void collectServers(Chassis chassis, RacadmCredentials credentials) {

        List<ModInfo> modInfoList = null;
        try {
            EnumModInfoCmd enumRacadmModInfoCmd = getModInfoCmd(credentials);
            modInfoList = enumRacadmModInfoCmd.getModInfoEntitiesByType(ModInfoTypeEnum.SERVER);
            chassis.setServerList(modInfoList);
        } catch (Exception e) {
            logger.error("Enumerate Server info command failed for Chassis");
            logger.error(e.getMessage());
        }
    }


    private void collectStashStorage(Chassis chassis, RacadmCredentials credentials) {

        List<ModInfo> modInfoList = null;
        try {
            EnumModInfoCmd enumRacadmModInfoCmd = getModInfoCmd(credentials);
            modInfoList = enumRacadmModInfoCmd.getModInfoEntitiesByType(ModInfoTypeEnum.SLED_STORAGE);
            if (modInfoList == null) {
                modInfoList = new ArrayList<ModInfo>();
            }
            chassis.setStashList(modInfoList);
        } catch (Exception e) {
            logger.error("Enumerate Server info command failed for Chassis");
            logger.error(e.getMessage());
        }
    }


    private void collectPowerSupplies(Chassis chassis, RacadmCredentials credentials) {

        try {
            EnumChassisPowerSupplyCmd cmd = new EnumChassisPowerSupplyCmd(credentials.getAddress(), credentials.getUserName(), credentials.getPassword(), credentials.isCertificateCheck());
            List<ChassisPowerSupplyEntity> powerSupplies = cmd.execute(getModInfoCmd(credentials));
            chassis.setChassisPowerSupplyList(powerSupplies);

        } catch (Exception e) {
            logger.error("Enumerate Power supply command failed for Chassis");
            logger.error(e.getMessage());
        }
    }


    private void collectPcis(Chassis chassis, RacadmCredentials credentials) {

        try {
            EnumChassisPCIeCmd cmd = new EnumChassisPCIeCmd(credentials.getAddress(), credentials.getUserName(), credentials.getPassword(), credentials.isCertificateCheck());
            List<ChassisPCIeEntity> pcieSlots = cmd.execute();
            chassis.setChassisPciList(pcieSlots);
        } catch (Exception e) {
            logger.error("Enumerate PCI command failed for Chassis: {} ", credentials.getAddress());
            logger.error(e.getMessage());
        }
    }


    private EnumModInfoCmd getModInfoCmd(RacadmCredentials credentials) {
        if (null == enumRacadmModInfoCmd) {
            enumRacadmModInfoCmd = new EnumModInfoCmd(credentials.getAddress(), credentials.getUserName(), credentials.getPassword(), credentials.isCertificateCheck());
        }
        return enumRacadmModInfoCmd;
    }


    private EnumVersionSummaryCmd getVersionSummaryCmd(RacadmCredentials credentials) {
        if (null == enumVersionSummaryCmd) {
            enumVersionSummaryCmd = new EnumVersionSummaryCmd(credentials.getAddress(), credentials.getUserName(), credentials.getPassword(), credentials.isCertificateCheck());
        }
        return enumVersionSummaryCmd;
    }


    private EnumSensorInfoCmd getSensorInfoCmd(RacadmCredentials credentials) {
        if (null == enumSensorInfoCmd) {
            enumSensorInfoCmd = new EnumSensorInfoCmd(credentials.getAddress(), credentials.getUserName(), credentials.getPassword(), credentials.isCertificateCheck());
        }
        return enumSensorInfoCmd;
    }


    private void collectKvms(Chassis chassis, RacadmCredentials credentials) {

        ChassisIkvm kvm = new ChassisIkvm();
        try {

            EnumKvmInfoCmd enumKvmInfoCmd = new EnumKvmInfoCmd(credentials.getAddress(), credentials.getUserName(), credentials.getPassword(), credentials.isCertificateCheck());
            KvmInfo kvmInfo = enumKvmInfoCmd.getKvmInfo();

            if (null != kvmInfo) {
                kvm.setFirmwareVersion(kvmInfo.getFWVersion());
                kvm.setName(kvmInfo.getModel());
                if ((null != kvmInfo.getPresence()) && kvmInfo.getPresence().toLowerCase().equals("present")) {
                    kvm.setPresent(true);
                }
                kvm.setHealth(kvmInfo.getStatus());
            }

            // set the front panel enabled and access to cmc enabled values
            CfgKvmInfo cfgKvmInfo = enumKvmInfoCmd.getCfgKvmInfo();
            if (null != cfgKvmInfo) {
                // set access to cmc enabled
                if ((null != cfgKvmInfo.getCfgKVMAccessToCMCEnable()) && cfgKvmInfo.getCfgKVMAccessToCMCEnable().equals("1")) {
                    kvm.setAccessToCmcEnabled(true);
                } else {
                    kvm.setAccessToCmcEnabled(false);
                }

                // set front panel
                if ((null != cfgKvmInfo.getCfgKVMFrontPanelEnable()) && cfgKvmInfo.getCfgKVMFrontPanelEnable().equals("1")) {
                    kvm.setFrontPanelEnabled(true);
                } else {
                    kvm.setFrontPanelEnabled(false);
                }
            }
            chassis.getChassisIKvmList().add(kvm);

        } catch (Exception e) {
            logger.error("Enumerate KVM command failed for Chassis {} ", credentials.getAddress());
            logger.error(e.getMessage());
        }
    }


    private void collectIoms(Chassis chassis, RacadmCredentials credentials) {

        try {
            EnumIOModuleCmd cmd = new EnumIOModuleCmd(credentials.getAddress(), credentials.getUserName(), credentials.getPassword(), credentials.isCertificateCheck());
            List<IOModuleEntity> ioms = cmd.execute(getModInfoCmd(credentials), getVersionSummaryCmd(credentials));
            chassis.setIoModuleEntityList(ioms);
        } catch (Exception e) {
            logger.error("Failed to discover IO Modules for the chassis");
            logger.error(e.getMessage());
        }
    }


    private void collectFans(Chassis chassis, RacadmCredentials credentials) {

        try {
            EnumChassisFanCmd cmd = new EnumChassisFanCmd(credentials.getAddress(), credentials.getUserName(), credentials.getPassword(), credentials.isCertificateCheck());
            List<ChassisFanEntity> fanList = null;
            fanList = cmd.executeForStomp(getModInfoCmd(credentials), getSensorInfoCmd(credentials));
            chassis.setChassisFanList(fanList);
        } catch (Exception e) {
            logger.error("Enumerate Fan command failed for Chassis ");
            logger.error(e.getMessage());
        }
    }


    private void collectTemperatureSensors(Chassis chassis, RacadmCredentials credentials) {

        try {
            EnumChassisTemperatureSensorCmd cmd = new EnumChassisTemperatureSensorCmd(credentials.getAddress(), credentials.getUserName(), credentials.getPassword(), credentials.isCertificateCheck());
            List<ChassisTemperatureSensorEntity> tempSensorList = null;
            tempSensorList = cmd.executeForStomp(getSensorInfoCmd(credentials));
            chassis.setChassisTemperatureSensorList(tempSensorList);
        } catch (Exception e) {
            logger.error("Enumerate Temperature Sensors command failed for Chassis");
            logger.error(e.getMessage());
        }
    }


    @Override
    public List<ChassisLog> getChassisLogs(RacadmCredentials credentials) throws Exception {
        List<ChassisLog> logList = new ArrayList<ChassisLog>();
        EnumChassisLogsCmd cmd = new EnumChassisLogsCmd(credentials.getAddress(), credentials.getUserName(), credentials.getPassword(), credentials.isCertificateCheck());
        logList = cmd.getChassisLogs();
        return logList;
    }

}