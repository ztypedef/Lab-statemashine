/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.z.statemachine;

/**
 *
 * @author pavel
 */
public abstract class StateFMS {

    private byte[] frame;
    
    
    public StateFMS() {
    }
    
    public StateFMS(byte[] pack) {
        frame = pack;
    }

    
    public void setSendPackage(byte[] pack){
        frame = pack;
    }
    
    public byte[] getSendPackage(){
        return frame;
    }
    
    public abstract void doingDo();
}
