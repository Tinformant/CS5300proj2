package proj2;

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;



public class singleReducer extends Reducer<Text, Text, Text, Text> {

		protected void reduce(Text key, Iterable<Text> values, Context context)
						throws IOException, InterruptedException{
			
			
//			the reduce metric is going to receive one of two types of incoming values
//			a.
//				"pR " + holder[1] + " " + edgeList 
//			b. 
//				"pC "+ rankFactor
			Iterator<Text> itr = values.iterator();
//			
			Text inputValues = new Text();
//			will hold the tokens per map item sent
			String[] inputTokens = null;
			
			Float pRankPortions = 0.0f;
			Float dampingFactor = singleNodeMaster.dampingFactor;
			Float thatConstant = (1- dampingFactor) / singleNodeMaster.totalNodes;
		
			Float oldPR = 0.0f;
			String edgeList = "";
			String degree = "0";
			
			while (itr.hasNext()){
				inputValues = itr.next();
				inputTokens = inputValues.toString().split("\\s+");
				if(inputTokens[0].equals("pR")){
					oldPR = Float.parseFloat(inputTokens[1]);
					edgeList = inputTokens[3];
					degree = inputTokens[2];
					
				} else if (inputTokens[0].equals("pC")){
//					append the portion contribution to the nodes sum variable pRankPortions
					pRankPortions += new Float(Float.parseFloat(inputTokens[1]));
				}
				
			}
			
//			Once all the messages are done sending, we can calculate the newPR variable
			
			Float newPR = (dampingFactor * pRankPortions) + thatConstant;
//			in addition, we know the residual information as well
			Float residuals = Math.abs(oldPR - newPR)/ newPR;
//			residuals would be a small fraction
//			we don't want to lose any data due to rounding, so we will keep 5 decimals worth of data
			
			long residual = (long) Math.floor(residuals * singleNodeMaster.longator);
			
			context.getCounter(singleNodeMaster.residualCounters.residualCounter).increment(residual);
			
			if (degree.equals("0") | edgeList.equals("")){
				throw new IOException("Received bad data from map value :" + values );
			}
			
			String output = newPR + "," + degree + "," + "edgeList";
			Text out = new Text(output);
			context.write(key, out);
		}
}
