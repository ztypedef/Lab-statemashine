/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.z.statemachine;

import jssc.SerialPortException;

/**
 *
 * @author pavel
 */
public class App {
    
    public static void main(String[] args) throws SerialPortException {
        new FMSinit().toDo();
    }
       
}
