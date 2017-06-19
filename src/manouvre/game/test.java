/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import java.io.IOException;
import static java.lang.Thread.sleep;

/**
 *
 * @author Bartosz
 */
 class TestA {

     Integer Liczba2;
    public TestA(Integer liczba) {
        this.Liczba2=liczba;
    } 
}

 class TestB{
        public Integer Liczba;
        public TestB(Integer liczba) {
        this.Liczba=liczba;
    } 
        public void add(){
            Liczba = Liczba + 1;
        }
}

class Test{
    public static void main(String[] args) throws InterruptedException  {
    Integer Liczba= new Integer(0);
     
    TestB b= new TestB(Liczba);
    TestA a= new TestA(Liczba);
    
    for(int i=0; i<5;i++){
        Liczba = Liczba + 1;
        System.out.println('A' + a.Liczba2.toString());
        System.out.println('B' + b.Liczba.toString());
    }
    
    }
}


//class TestA {
//
//    TestB kopiaB=new TestB();
//    public TestA(TestB nowaB) {
//        this.kopiaB=nowaB;
//    } 
//}
//
// class TestB{
//    public Integer Liczba=0;
//        public void add(){
//            Liczba++;
//        }
//}
//
//class Test{
//    public static void main(String[] args) throws InterruptedException  {
//    TestB b= new TestB();
//    TestA a= new TestA(b);
//    
//    for(int i=0; i<20;i++){
//        b.add();
//    }
//    }
//}