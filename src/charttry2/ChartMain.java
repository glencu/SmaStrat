/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package charttry2;

import java.awt.Dimension;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import java.io.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.MovingAverage;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
/**
 *
 * @author Bobasek
 */
public class ChartMain {
    
    charttry2.ChartTryFrame frame;
    String filePath = "c:\\tools\\pierdy\\kghm.mst";
  
    int numberOfLines=0;
    
    ChartMain(charttry2.ChartTryFrame frame)
    {
         this.frame = frame;
         
        JFreeChart jfreechart = createCombinedChart();
        ChartPanel chartpanel = new ChartPanel(jfreechart,true,true,true,false,true);
        chartpanel.setPreferredSize(new Dimension(1000,540));

        
        frame.jTabbedPane1.add(chartpanel);

    }
    
    
    
    JFreeChart createCombinedChart()
    {
        File file = new File(filePath);
        
        this.numberOfLines = evaluateNumberOFLines(file);
        
        
        Random generator = new Random();
        //int count = (int)Math.round(generator.nextGaussian()*15);
        int count = generator.nextInt(numberOfLines);
        XYDataset xydataset = createPriceDataset(file,count);///CHANGE numerofline to count!!
        String s = "KGHM";
        int last = ((TimeSeriesCollection)xydataset).getSeries(0).getItemCount();
        RegularTimePeriod d = (((TimeSeriesCollection)xydataset).getSeries(0).getDataItem(last-1)).getPeriod();
        System.out.println(xydataset.getItemCount(0));
        System.out.println(xydataset.getItemCount(1));
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
        jfreechart.setNotify(true);
        return jfreechart;
    
    }
    
    
     XYDataset createPriceDataset(File file,int count)
    {
        int cntr=0;
        TimeSeries timeseries = new TimeSeries("Price");
        TimeSeries sma50 = new TimeSeries("SMA50");
        TimeSeries sma200 = new TimeSeries("SMA200");
        
        
        StringBuilder contents = new StringBuilder();
        ArrayList<Double> lastFifty=new ArrayList<Double>();
        ArrayList<Double> last200=new ArrayList<Double>();
        
        
        try {
            //use buffering, reading one line at a time
            //FileReader always assumes default encoding is OK!
            BufferedReader input = new BufferedReader(new FileReader(file));
            try {
                String line = null;
                while ((line = input.readLine()) != null) {
                    
                    
                    if (cntr >= count) break;
                    cntr++;
                    
                    if ( cntr == 1 ) continue;
                    
                    String delims = "[,]";
                    String[] tokens = line.split(delims);
                    DateFormat formatter;
                    formatter = new SimpleDateFormat("yyyyMMdd");
                    Date date = null;
                    try {
                        date = (Date) formatter.parse(tokens[1]);
                        double closingPrice = Double.parseDouble(tokens[5]);
                                                
                        timeseries.add(new Day(date),closingPrice);
                    
                        lastFifty.add( new Double(closingPrice));
                        last200.add( new Double(closingPrice));
                        
                                    
                        if ( cntr >= 51 )
                        {
                          if (cntr > 51)
                          {
                              lastFifty.remove(0);
                          }
                
                          if ( lastFifty.size() != 50 )
                              System.exit(0);
                          
                          
                          
                                    
                          double avg = 0;
                          for(double elem : lastFifty)
                              avg += elem;
                           
                          avg /= 50;
                          
                          sma50.add(new Day(date),avg);
                          
                        }
                        
                        
                         if ( cntr >= 201 )
                        {
                          if (cntr > 201)
                          {
                              last200.remove(0);
                          }
                
                          if ( last200.size() != 200 )
                              System.exit(0);
                          
                          
                          
                                    
                          double avg = 0;
                          for(double elem : last200)
                              avg += elem;
                           
                          avg /= 200;
                          
                          sma200.add(new Day(date),avg);
                          
                        }
                        
                        
                        
                        
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

        TimeSeriesCollection collection = new TimeSeriesCollection();
        collection.addSeries(timeseries);
        TimeSeries sma50_t = MovingAverage.createMovingAverage(timeseries, "SMA50_T", 3, 3);
        TimeSeries sma200_t = MovingAverage.createMovingAverage(timeseries, "SMA200_T", 200, 0);
        collection.addSeries(sma50_t);
        collection.addSeries(sma200_t);
        collection.addSeries(sma50);
        //collection.addSeries(timeseries.cre);
        collection.addSeries(sma200);
        
       /* for (int i = sma50.getItemCount() - 50 ; i < sma50.getItemCount() ; i++) {
            System.out.println((sma50_t.getDataItem(i)).getValue() + " " + (sma50.getDataItem(i)).getValue());
            System.out.println((sma50_t.getDataItem(i)).getPeriod() + " " + (sma50.getDataItem(i)).getPeriod());
        }*/
        
        return collection;
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

}
