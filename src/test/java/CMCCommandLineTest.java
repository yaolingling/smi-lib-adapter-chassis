
/**
 * Copyright © 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 */
import java.util.List;
import java.util.Scanner;

import com.dell.isg.smi.common.protocol.command.chassis.entity.ChassisLog;
import com.dell.isg.smi.common.protocol.command.cmc.entity.RacadmCredentials;
import com.dell.isg.smi.adapter.chassis.ChassisAdapterImpl;
import com.dell.isg.smi.adapter.chassis.ChassisOnboardingManagerImpl;

public class CMCCommandLineTest {

    private static Scanner scanner;
    private static final String CONFIGURE_SNMP = "TRAPS";
    private static final String FACTORY_RESET = "RESET";
    private static final String SEVER_RESEAT = "RESEAT";
    private ChassisOnboardingManagerImpl impl = new ChassisOnboardingManagerImpl();
    private ChassisAdapterImpl adapter = new ChassisAdapterImpl();


    /**
     * @param args
     */
    public static void main(String[] args) {
        CMCCommandLineTest cmcTest = new CMCCommandLineTest();
        RacadmCredentials cred = new RacadmCredentials();
        cred.setAddress("172.31.62.159");
        cred.setUserName("root");
        cred.setPassword("calvin");
        cmcTest.getChassisLogs(cred);
        scanner = new Scanner(System.in);
        System.out.print("Enter the command: ");
        // Get the command
        String command = scanner.next();
        if (command.trim().equals(CONFIGURE_SNMP)) {
            // prompt server ip
            System.out.print("Enter the Chassis IP : ");
            cred.setAddress(scanner.next());
            // prompt target ip
            System.out.print("Enter the destination target Server IP : ");
            String targetIp = scanner.next();
            cmcTest.configureTraps(cred, targetIp);
        } else if (command.trim().equals(FACTORY_RESET)) {
            // prompt server ip
            System.out.print("Enter the Chassis IP : ");
            cred.setAddress(scanner.next());
            cmcTest.resetFactoryConfiguration(cred);
        } else if (command.trim().equals(SEVER_RESEAT)) {
            // System.out.print("Enter the Chassis IP : ");
            // cred.setAddress(scanner.next());
            cmcTest.reseatServerTest(cred);
        } else {
            System.out.println("Invalid command.");
        }

    }


    public void configureTraps(RacadmCredentials cred, String ip) {
        System.out.println("Configuring Chassis SNMP Alerts for IP :" + cred.getAddress() + " Target Destination : " + ip);
        try {
            impl.configureTraps(cred, ip.trim());
        } catch (Exception e) {
            System.out.println("SNMP Configuring failed ......");
            e.printStackTrace();
        }
        System.out.println("SNMP Configuring finished ......");
    }


    public void resetFactoryConfiguration(RacadmCredentials cred) {
        System.out.println("Reset Chassis to factory setting started for IP :" + cred.getAddress());
        try {
            impl.resetFactoryConfig(cred);
            System.out.println("Reset Chassis to factory setting finished ......");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void reseatServerTest(RacadmCredentials cred) {
        System.out.println("server Reseat Test" + cred.getAddress());
        try {
            adapter.reseatServer(cred, 10L);
            System.out.println("server Reseat successfully resetted");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getChassisLogs(RacadmCredentials cred) {
        System.out.println("Chassis hardwarelogs" + cred.getAddress());
        try {

            List<ChassisLog> logs = adapter.getChassisLogs(cred);
            System.out.println("Logs count" + logs.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
