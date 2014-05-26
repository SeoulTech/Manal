package com.dforensic.plugin.manal.views;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;

public class  myProgressBar  extends  Thread  {
        private  Display  display;
        private  ProgressBar  progressBar;

        public  myProgressBar(Display  display,  ProgressBar  progressBar)  {
                this.display  =  display;
                this.progressBar  =  progressBar;
        }

        public  void  run()  {
                //  Perform  work  here--this  operation  just  sleeps
                for  (int  i  =  0;  i  <  30;  i++)  {
                        try  {
                                Thread.sleep(300);
                        }  catch  (InterruptedException  e)  {
                                //  Do  nothing
                        }
                        display.getDefault().asyncExec(new  Runnable()  {
                                public  void  run()  {
                                        if  (progressBar.isDisposed())               
                                                return;
                                        //  Increment  the  progress  bar
                                        progressBar.setSelection(progressBar.getSelection()  +  1);
                                }
                        });
                }
        }
}