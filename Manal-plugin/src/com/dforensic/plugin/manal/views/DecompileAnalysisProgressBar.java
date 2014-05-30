/*
 *  <Manal project is an eclipse plugin for the automation of malware analysis.>
 *  Copyright (C) <2014>  <Nikolay Akatyev, Hojun Son>
 *  This file is part of Manal project.
 *
 *  Manal project is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, version 3 of the License.
 *
 *  Manal project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Manal project. If not, see <http://www.gnu.org/licenses/>.
 *  
 *  Contact information of contributors:
 *  - Nikolay Akatyev: nikolay.akatyev@gmail.com
 *  - Hojun Son: smuoon4680@gmail.com
 */
package com.dforensic.plugin.manal.views;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;

public class  DecompileAnalysisProgressBar  extends  Thread  {
        private  Display  display;
        private  ProgressBar  progressBar;
        private int percentage;
        public  DecompileAnalysisProgressBar(Display  display,  ProgressBar  progressBar)  {
                this.display  =  display;
                this.progressBar  =  progressBar;
                this.percentage = 0;
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