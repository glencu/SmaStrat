/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package freecharttry1;

import java.awt.Button;
import java.util.Locale;
import javax.swing.JPanel;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.*;
import org.jfree.data.xy.*;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.data.DomainOrder;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.time.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.axis.NumberAxis;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import org.jfree.chart.renderer.xy.*;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import java.io.*;


import java.awt.Dimension;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import javax.swing.JPanel;
import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.time.*;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import java.text.*;
import java.util.*;
import java.awt.event.*; 




/**
 *
 * @author akg023
 */
public class FreeChartTry1 extends ApplicationFrame
 {
    String appname="moj";
    String filePath = "c:\\tools\\pierdy\\kghm.mst";
    
    int numberOfLines=0;
    
    
    int evaluateNumberOFLines(File file)
    {
         int result = 0;
            try {
            //use buffering, reading one line at a time
            //FileReader always assumes default encoding is OK!
            BufferedReader input = new BufferedReader(new FileReader(file));
            try {
                String line = null; //not declared within while loop
        /*
                 * readLine is a bit quirky :
                 * it returns the content of a line MINUS the newline.
                 * it returns null only for the END of the stream.
                 * it returns an empty String if two newlines appear in a row.
                 */
                while ((line = input.readLine()) != null) {
                 result++;
                }
            }
            finally {
             input.close();
            }
           }
          catch (IOException ex){
           ex.printStackTrace();
         }   

        return result;
    }
    
    JFreeChart createCombinedChart()
    {
        File file = new File(filePath);
        
        this.numberOfLines = evaluateNumberOFLines(file);
        
        
        Random generator = new Random();
        //int count = (int)Math.round(generator.nextGaussian()*15);
        int count = generator.nextInt(numberOfLines);
       /* if (count < 0 ) {
            count = -count;
        }
         count = count % numberOfLines;*/
        System.out.println("numberOfLines: " + numberOfLines);
        System.out.println("count: " + count);
        XYDataset xydataset = createPriceDataset(file,count);
        String s = "KGHM";
        JFreeChart jfreechart = ChartFactory.createTimeSeriesChart(s, "Date", "Price", xydataset, true, true, false);
        XYPlot xyplot = (XYPlot)jfreechart.getPlot();
        NumberAxis numberaxis = (NumberAxis)xyplot.getRangeAxis();
        numberaxis.setLowerMargin(0.40000000000000002D);
        DecimalFormat decimalformat = new DecimalFormat("00.00");
        numberaxis.setNumberFormatOverride(decimalformat);
        XYItemRenderer xyitemrenderer = xyplot.getRenderer();
	xyitemrenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator("{0}: ({1}, {2})", new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0.00")));
	NumberAxis numberaxis1 = new NumberAxis("Volume");
	numberaxis1.setUpperMargin(1.0D);
	xyplot.setRangeAxis(1, numberaxis1);
        
	xyplot.setDataset(1, createVolumeDataset(file,count));
	xyplot.setRangeAxis(1, numberaxis1);
	xyplot.mapDatasetToRangeAxis(1, 1);
	XYBarRenderer xybarrenderer = new XYBarRenderer(0.20000000000000001D);
	xybarrenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator("{0}: ({1}, {2})", new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0,000.00")));
	xyplot.setRenderer(1, xybarrenderer);
	ChartUtilities.applyCurrentTheme(jfreechart);
	xybarrenderer.setBarPainter(new StandardXYBarPainter());
	xybarrenderer.setShadowVisible(false);
        
        return jfreechart;
 
    }
    
    
    XYDataset createPriceDataset(File file,int count)
    {
        int cntr=0;
        TimeSeries timeseries = new TimeSeries("Price");
        StringBuilder contents = new StringBuilder();
        try {
            //use buffering, reading one line at a time
            //FileReader always assumes default encoding is OK!
            BufferedReader input = new BufferedReader(new FileReader(file));
            try {
                String line = null;
                while ((line = input.readLine()) != null) {
                    if (cntr >= count) break;
                    cntr++;
                    String delims = "[,]";
                    String[] tokens = line.split(delims);
                    DateFormat formatter;
                    formatter = new SimpleDateFormat("yyyyMMdd");
                    Date date = null;
                    try {
                        date = (Date) formatter.parse(tokens[1]);
                        double closingPrice = Double.parseDouble(tokens[5]);
                                                
                        timeseries.add(new Day(date),closingPrice);
                        
                    } catch (ParseException e) {
                        System.out.println("dupa1");
                    }

                }//while
            } finally {
                input.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return new TimeSeriesCollection(timeseries);
    }
    
    private static IntervalXYDataset createVolumeDataset(File file,int count)
    {
         TimeSeries timeseries = new TimeSeries("Volume");
          StringBuilder contents = new StringBuilder();
           int cntr=0;
        try {
            //use buffering, reading one line at a time
            //FileReader always assumes default encoding is OK!
            BufferedReader input = new BufferedReader(new FileReader(file));
            try {
                String line = null;
                while ((line = input.readLine()) != null) {
                    if (cntr >= count) break;
                    cntr++;
                    
                    String delims = "[,]";
                    String[] tokens = line.split(delims);
                    DateFormat formatter;
                    formatter = new SimpleDateFormat("yyyyMMdd");
                    Date date = null;
                    try {
                        date = (Date) formatter.parse(tokens[1]);
                        int volume = Integer.parseInt(tokens[6]);
                                                
                        timeseries.add(new Day(date),volume);
                        
                    } catch (ParseException e) {
                        System.out.println("dupa");
                    }

                }//while
            } finally {
                input.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }        
         
         
         
         
         return new TimeSeriesCollection(timeseries);
    }

    public FreeChartTry1(String appname) {
        super(appname);
        
        
        Button bt_nextday = new Button("Next Day");
        Button bt_rndday = new Button("Random Day");
        MyActionListener myactlistener = new MyActionListener(this);
        
        bt_nextday.addActionListener(myactlistener);
        bt_rndday.addActionListener(myactlistener);
        
        JFreeChart jfreechart = createCombinedChart();
        ChartPanel chartpanel = new ChartPanel(jfreechart,true,true,true,false,true);
        chartpanel.setPreferredSize(new Dimension(1000,540));
        
        chartpanel.add(bt_nextday);
        chartpanel.add(bt_rndday);
        
        setContentPane(chartpanel);   
    }
    

    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        FreeChartTry1 try1 = new FreeChartTry1("moj");
        try1.pack();
        RefineryUtilities.centerFrameOnScreen(try1);
        try1.setVisible(true);
        
    }

    
    
}

class MyActionListener implements ActionListener { 
  
    FreeChartTry1 freetry1;
    
    public MyActionListener(FreeChartTry1 freetry)
    {
      this.freetry1 = freetry;
    }
    
  public void actionPerformed(ActionEvent ae) {
  String s = ae.getActionCommand(); 
  if (s.equals("Next Day")) { 
  System.exit(0); 
  } 
  else if (s.equals("Random Day")) { 
  System.out.println("Good Morning"); 
  
  
  JFreeChart jfreechart = freetry1.createCombinedChart();
  ChartPanel chartpanel = new ChartPanel(jfreechart,true,true,true,false,true);
  chartpanel.setPreferredSize(new Dimension(1000,574));
  
  
  
   Button bt_nextday = new Button("Next Day");
   Button bt_rndday = new Button("Random Day");
   MyActionListener myactlistener = new MyActionListener(freetry1);
        
   bt_nextday.addActionListener(myactlistener);
   bt_rndday.addActionListener(myactlistener);
     chartpanel.add(bt_nextday);
        chartpanel.add(bt_rndday);
  freetry1.setContentPane(chartpanel);
  freetry1.invalidate();
        
         
  } 
  else { 
  System.out.println(s + " clicked"); 
  } 
  } 
} 