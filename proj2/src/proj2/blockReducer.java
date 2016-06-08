package proj2;

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;


public class blockReducer extends Reducer<Text, Text, Text, Text> {
	protected void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		Iterator<Text> itr = values.iterator();
//		
		Text inputValues = new Text();
//		will hold the tokens per map item sent
		String[] inputTokens = null;
		
		Float pRankPortions = 0.0f;
		Float dampingFactor = singleNodeMaster.dampingFactor;
		Float thatConstant = (1- dampingFactor) / singleNodeMaster.totalNodes;
		
		while (itr.hasNext()){
			
		}
		
		
	}
}
