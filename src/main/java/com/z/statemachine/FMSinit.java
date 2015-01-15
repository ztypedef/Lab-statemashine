/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.z.statemachine;

import com.argos.controlx.Modbus.MbEvent;
import com.argos.controlx.Modbus.Modbus;
import jssc.SerialPortException;

/**
 *
 * @author pavel
 */
public class FMSinit {
    
    byte[] pack;
    Modbus mb;
            
    public void toDo() throws SerialPortException{
        System.out.println("Start");
        pack = new byte[3];
        pack[0] = 0x10;
        pack[1] = 0x10;
        pack[2] = 0x4;
        
        mb = new Modbus();
        FiniteStateMachine f = new FiniteStateMachine();
        f.addActionFMS(aFMS);
        mb.openPort("/dev/ttyUSB0");
        
        mb.addActionMb(f.mbAction);
        int id = f.addCondition(10, 10);
        f.addExecution(id, MbEvent.MbStatus.OK, send0x1);
        f.start(pack);
        System.out.println("[Threads] exit");
        mb.closePort();
    }
    
    
    StateFMS send0x1 = new StateFMS(pack) {
        
        @Override
        public void doingDo() {

        }
    };
    
    ActionFMS aFMS = new ActionFMS(){

        @Override
        public void actionPerfomed(byte[] pack) {
            mb.send(pack);
        }  
    };
}
