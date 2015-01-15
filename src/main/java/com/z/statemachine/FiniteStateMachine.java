/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.z.statemachine;

import com.argos.controlx.Modbus.MbAction;
import com.argos.controlx.Modbus.MbEvent;
import com.argos.controlx.Modbus.MbEvent.MbStatus;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pavel
 */
public class FiniteStateMachine {
    
    private final Object sync;
    private final Map<Integer, ConditionUnit> map;
    private  MbEvent mEvent;
    private byte[] sendPack;
    private ActionFMS actionFMS;
    private boolean run = true;
    
    public FiniteStateMachine(){
        sync = new Object();
        map = new HashMap<>();
    }
    
    public enum ConditionsFMS{
        OK,
        TIME_OUT,
        DATA_CORRUPTION,
        ERROR_CRC,
        UNKNOWN,
        ANYTHUNG,
    }
    
    public MbAction mbAction = new MbAction() {

        @Override
        public void actionPerfomed(MbEvent e) {
            actionEvent(e);
        }
    };
    
    
    public void actionEvent(MbEvent ev){
        mEvent = ev;
        synchronized(sync){
            sync.notify();
        }
    }
    
    public void addActionFMS(ActionFMS a){
        actionFMS = a;
    }
    
    public void start(byte[] pack){
        sendPack = pack;
        int fc, sa = 0;
        while(run){
            //TODO send package to com port
            sa = ((int)pack[0]) & 0xff;
            fc = ((int)pack[1]) & 0xff;
            
            mEvent = null;
            if(sendPack == null){
                System.out.println("[Error] sendPack = null");
                return;
            } // TODO thow exeption
            /*
            System.out.print("transmit: ");
            for(int i = 0; i < sendPack.length; i++){
                System.out.print("0x" + Integer.toHexString(sendPack[i]) + " ");
            }
            System.out.println("");*/
            actionFMS.actionPerfomed(sendPack);
            
            synchronized(sync){
                try {
                    sync.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(FiniteStateMachine.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            //receive package to com port
            int id = getId(sa, 0);
            
            ConditionUnit cUnit = map.get(id); 
            if(cUnit == null){
                id = getId(sa, fc);
                cUnit = map.get(id);
            }
            /*
            System.out.print("receive: ");
            int[] d = mEvent.getData();
            if(d != null)
                for(int i = 0; i < d.length; i++){
                    System.out.print("0x" + Integer.toHexString(d[i]) + " ");
                }
            System.out.println("");*/
            //System.out.println("id: " + id);
             // add exeption
            //System.out.println(sa + " " + fc + " " + mEvent.getStatus());
            cUnit.doingDo(sa, fc, mEvent.getStatus());
            
        }
    }
    
    public void stop(){
        run = false;
    }
    
    
    public int addCondition(int slaveAddress, int functionCode){
        int id = getId(slaveAddress, functionCode);
        //System.out.println("add id: " + id);
        map.put(id, new ConditionUnit(slaveAddress, functionCode));
        return id;
    }
    
    
    public void addExecution(int id, MbStatus cnd, StateFMS stateFMS){
        ConditionUnit cUnit = map.get(id);  // TODO add exeption
        cUnit.addDoing(cnd, stateFMS);
    }
    
    // TODO add remove condition, and add function remove Execution
    
    private int getId(int slaveAddress, int functionCode){
        return (slaveAddress & 0xff) | ((functionCode << 8) & 0xff00);
    }
    
    private class ConditionUnit{
        
        private final int slaveAddress;
        private final int functionCode;
        private final Map <MbStatus, StateFMS> doMap;

        public ConditionUnit(int slaveAddress, int functionCode) {
            this.slaveAddress = slaveAddress;
            this.functionCode = functionCode;
            doMap = new HashMap<MbStatus, StateFMS>();
        }
        
        public void addDoing(MbStatus cnd, StateFMS stateFMS){
            doMap.put(cnd, stateFMS);
        }
        
        public void removeDoing(MbStatus cnd){
            doMap.remove(cnd);
        }
        
        public boolean doingDo(int slaveAddress, int functionCode, MbStatus cnd){
            //if(cnd != MbStatus.OK) return false;
            
            if(slaveAddress != this.slaveAddress) return false;
            if(functionCode != 0)
                if(functionCode != this.functionCode) return false;
            StateFMS sfms = doMap.get(cnd);
            if(sfms == null) return false;
            sendPack = null;
            sfms.doingDo();  // TODO add exeption
            sendPack = doMap.get(cnd).getSendPackage();
            if(sendPack == null) System.out.println("ERROR PACK");
            
           
            
            return true;
        }

    }

}
