/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.gui;

/**
 *
 * @author piotr_grudzien
 */
public class Resolution {
    
    
    public enum Mode {
         R1600_900(0.7f),
         R1680_1050(1f),
         R1920_1080(1f);
     
         private float resolutionMultiplyer;
         
         private Mode(float multiplyer){
         this.resolutionMultiplyer = multiplyer;
         } 
         float multiplyer(){
            return resolutionMultiplyer;
         }
         };
    
}
